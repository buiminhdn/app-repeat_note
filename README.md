# Repeat Note

Passive memory reinforcement app using Android home screen widgets. Users create notes (birthdays, vocabulary, phone numbers, quotes, etc.) and widgets automatically rotate through them every 15 minutes — silently, without notifications or gamification.

**The widget is the primary experience.** The app itself exists only for note management and widget configuration.

---

## UX Philosophy

- **Passive by design.** The app does not prompt the user. It simply surfaces notes on the homescreen at a steady cadence.
- **Widget-first.** Everything is designed around Glance widget reliability, readability, and configurability.
- **Calm utility.** No streaks, no cloud sync, no auth. Minimal UI with strong typography.
- **Independent widget instances.** Each widget has its own configuration. No shared global state.

---

## Feature List

- Create / edit / delete notes (title, content, category)
- Category filter on home screen note list
- Search notes
- Add/Edit via modal bottom sheet (not full screen)
- Add widget to homescreen — configure category filter and rotation mode per instance
- Widget auto-rotates notes every 15 minutes via a single shared WorkManager worker
- Two rotation modes: Sequential and Random (with recent-note exclusion)
- Responsive widget layout adapts to small / medium / large sizes
- Tap widget → opens app
- Full offline, no auth, no cloud

---

## Architecture

Clean modular architecture with strict MVI per feature. Unidirectional state flow only.

```
app/
├── core/               # DI setup, base classes, extensions, constants
├── data/               # Room DB, DAOs, DataStore, repositories (impl)
├── domain/             # Entities, use cases, repository interfaces
├── feature_notes/      # Home screen, note list, add/edit bottom sheet
├── feature_widget/     # Widget configuration screen
├── worker/             # WidgetUpdateWorker (WorkManager)
├── widget/             # Glance widget UI, state, receivers
└── designsystem/       # Theme, typography, color tokens, reusable composables
```

### Dependency Rules

- `domain` has no Android dependencies
- `data` depends on `domain`
- Features depend on `domain` only (not `data` directly)
- `core` provides DI bindings that wire `data` → `domain`
- `worker` depends on `domain` and `data`
- `widget` depends on `domain` and `worker`

---

## Data Models

### Room Entity: `MemoryNote`

```kotlin
@Entity(tableName = "memory_notes")
data class MemoryNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String,       // Matches Category enum name or custom string
    val createdAt: Long,        // epoch millis
)
```

### Room Entity: `WidgetConfig`

```kotlin
@Entity(tableName = "widget_configs")
data class WidgetConfig(
    @PrimaryKey val widgetId: Int,
    val categoryFilter: String?,    // null = all categories
    val rotationMode: RotationMode, // SEQUENTIAL | RANDOM
    val currentIndex: Int = 0,      // used for SEQUENTIAL mode
)
```

### Domain Models

```kotlin
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val category: Category,
    val createdAt: Long,
)

enum class Category {
    FAMILY, NUMBERS, STUDY, WORK, QUOTES, LANGUAGE, CUSTOM
}

enum class RotationMode { SEQUENTIAL, RANDOM }
```

### DataStore: `RecentlyShownStore`

Persists a per-widget list of recently shown note IDs to prevent immediate repetition in RANDOM mode.

```kotlin
// Key pattern: "recent_ids_{widgetId}" → serialized List<Long> (JSON or proto)
// Trim to last N=5 entries after each update
```

---

## Widget System

### Technology

Glance App Widget with `SizeMode.Responsive` to deliver different layouts by size class.

### Size Breakpoints

| Size | Behavior |
|------|----------|
| Small (≤ 2×2 approx) | Category label, title, content |
| Medium / Large | Adds: rotation mode badge, total note count, last updated time |

Use `LocalSize.current` in the Glance composable to read current dimensions and branch layout accordingly.

### Widget State

Widget UI state is managed via `GlanceStateDefinition` backed by DataStore. State object per widget:

```kotlin
data class WidgetUiState(
    val widgetId: Int,
    val noteTitle: String,
    val noteContent: String,
    val category: String,
    val rotationMode: RotationMode,
    val totalNotes: Int,
    val lastUpdatedAt: Long,
)
```

### Widget Receiver

`MemoryWidgetReceiver : GlanceAppWidgetReceiver`

Handles:
- `onEnabled` / `onDeleted` → insert / remove `WidgetConfig` from Room
- `onUpdate` → trigger `WidgetUpdateWorker` immediately on first add

### Widget Tap Action

```kotlin
actionStartActivity<MainActivity>()
```

No per-note or next/previous actions in MVP.

---

## Rotation Logic

### Sequential

1. Load notes matching the widget's `categoryFilter` (or all if null), ordered by `createdAt ASC`
2. Display note at `currentIndex`
3. Increment `currentIndex`, wrap on overflow: `(currentIndex + 1) % notes.size`
4. Persist updated `currentIndex` to `WidgetConfig`

### Random

1. Load candidate notes (filtered by category)
2. Load `recentlyShown` list for this widget from DataStore
3. Exclude `recentlyShown` IDs from candidates — if all notes are excluded, reset and pick from full set
4. Pick randomly from remaining candidates
5. Prepend selected ID to `recentlyShown`, trim to last 5 entries
6. Persist updated list

Edge case: if filtered category has only 1 note, always show that note regardless of recently-shown logic.

---

## WorkManager Flow

### Worker: `WidgetUpdateWorker`

One shared periodic worker for all widgets. Do NOT create one worker per widget instance.

```kotlin
@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val widgetConfigRepo: WidgetConfigRepository,
    private val noteRepo: NoteRepository,
    private val recentlyShownStore: RecentlyShownStore,
    private val glanceManager: GlanceAppWidgetManager,
) : CoroutineWorker(context, params)
```

**Worker execution flow:**

```
1. Load all WidgetConfig rows from Room
2. For each config:
   a. Resolve notes for categoryFilter
   b. Apply rotation logic (Sequential or Random)
   c. Build WidgetUiState
   d. updateAppWidgetState(context, widgetId, state)
3. Trigger Glance recomposition: GlanceAppWidget.updateAll(context)
```

**Scheduling:**

```kotlin
// Enqueue on app start and on first widget add
WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "widget_rotation",
    ExistingPeriodicWorkPolicy.KEEP,
    PeriodicWorkRequestBuilder<WidgetUpdateWorker>(15, TimeUnit.MINUTES)
        .setConstraints(Constraints.Builder().build()) // no constraints needed
        .build()
)
```

Use `ExistingPeriodicWorkPolicy.KEEP` to avoid resetting the timer on app relaunch.

**OEM battery optimization note:** WorkManager minimum interval is 15 min, but OEM battery savers (Xiaomi, Huawei, Samsung) may defer work significantly. There is no workaround without foreground services or exact alarms. Accept this gracefully — widget content is best-effort, not real-time. Do not show stale timestamps as errors.

---

## MVI Structure

Each feature module follows this contract:

```kotlin
// Contract.kt
interface NotesContract {
    data class State(
        val notes: List<Note> = emptyList(),
        val categories: List<Category> = Category.entries,
        val selectedCategory: Category? = null,
        val searchQuery: String = "",
        val isBottomSheetOpen: Boolean = false,
        val editingNote: Note? = null,   // null = Add mode
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        data class SearchQueryChanged(val query: String) : Event
        data class CategorySelected(val category: Category?) : Event
        data class NoteClicked(val note: Note) : Event
        data object AddNoteClicked : Event
        data object BottomSheetDismissed : Event
        data class NoteSaved(val title: String, val content: String, val category: Category) : Event
        data class NoteDeleted(val note: Note) : Event
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
    }
}
```

```kotlin
// ViewModel.kt
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotes: GetNotesUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(NotesContract.State())
    val state: StateFlow<NotesContract.State> = _state.asStateFlow()
    val effects = Channel<NotesContract.Effect>(Channel.BUFFERED)

    fun onEvent(event: NotesContract.Event) { /* ... */ }
}
```

Bottom sheet open/close state is part of MVI `State`. Do not use standalone `rememberModalBottomSheetState` as source of truth.

---

## App Screens

### Home Screen (`feature_notes`)

- `LazyColumn` of notes, filtered by `selectedCategory` and `searchQuery`
- Category filter row (horizontal `LazyRow` of chips)
- `FloatingActionButton` → `AddNoteClicked` event
- Click on note → `NoteClicked` event (opens edit sheet)
- `ModalBottomSheet` overlaid on same screen, state-driven

### Add/Edit Bottom Sheet

- Controlled by `isBottomSheetOpen` + `editingNote` in MVI state
- Same composable for add (editingNote = null) and edit
- Fields: title (`TextField`), content (`TextField`, multiline), category (`DropdownMenu` or chip row)
- Save button → dispatches `NoteSaved` event
- Dismiss (swipe / outside tap) → `BottomSheetDismissed` event

### Widget Configuration Screen (`feature_widget`)

- Launched when user adds widget to homescreen via `AppWidgetManager.ACTION_APPWIDGET_CONFIGURE`
- Receives `widgetId` from Intent extras
- Fields: category filter, rotation mode
- On confirm: write `WidgetConfig` to Room, schedule worker, finish Activity with `RESULT_OK`

---

## Edge Cases

| Scenario | Behavior |
|----------|----------|
| No notes exist | Widget shows empty state: "No notes yet. Open the app to add some." |
| Category filter is set, but category has 0 notes | Widget shows: "No notes in this category." |
| Note referenced by widget is deleted | Worker falls back to next available note; if none, show empty state |
| Widget config missing (widgetId not found) | Worker skips that widget; Glance shows default empty state |
| All notes shown recently (RANDOM mode) | Reset `recentlyShown` for that widget, pick randomly from full set |
| Single note in filtered set | Always show that note; skip rotation |
| WorkManager deferred by OEM | Widget shows stale note; no error displayed — this is acceptable behavior |
| User adds widget without completing config | `RESULT_CANCELED` returned; no config written; worker skips widgetId |

---

## Technical Constraints

- **Minimum SDK:** 26 (Glance stable requires 26+; `WorkManager` periodic minimum is API 23)
- **Target SDK:** 35
- **WorkManager minimum interval:** 15 minutes — cannot be reduced without `AlarmManager` or foreground service
- **Glance recomposition:** Must call `GlanceAppWidget.updateAll()` or per-instance `update()` after writing state; Glance does not auto-observe Room/DataStore
- **Widget config Activity:** Must declare `android:exported="true"` and handle `ACTION_APPWIDGET_CONFIGURE` in `AndroidManifest.xml`; must finish with `RESULT_OK` + widgetId or Glance will not place the widget
- **DataStore vs SharedPreferences:** Use DataStore for all persistent non-Room state; do not use SharedPreferences
- **No coroutine leaks in Worker:** Use `withContext(Dispatchers.IO)` inside `doWork()` — `CoroutineWorker` is already on a background dispatcher but DB calls should be explicit
- **Glance state size limit:** Keep `WidgetUiState` small — DataStore values backing Glance state have a size cap; do not store full note lists in widget state
- **OEM kill:** No workaround for aggressive OEM process killing. Do not promise exact 15-min updates in marketing copy.