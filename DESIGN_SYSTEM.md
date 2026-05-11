# Repeat Note — Design System

Design reference: minimal monochrome note app with dark splash + light content screens.
Font: **Manrope** (imported via `font/` folder). No fallback to Inter or Roboto.

---

## 1. Design Language

**Aesthetic:** Clean editorial minimalism. Dark/light dual-mode. Typography-driven hierarchy. Generous whitespace. No decorative elements — structure comes from weight and spacing alone.

**Personality:** Calm, focused, trustworthy. Like a well-designed physical notebook.

**Avoid:**
- Gradients on backgrounds
- Colored shadows
- Rounded corners beyond `12dp`
- Icon-heavy UI
- Saturated category colors
- Any dashboard/productivity-app visual language

---

## 2. Color Tokens

### Light Theme (Default — used in main app screens)

| Token | Hex | Usage |
|-------|-----|-------|
| `color_background` | `#F7F7F7` | Screen background |
| `color_surface` | `#FFFFFF` | Cards, sheets, dialogs |
| `color_surface_variant` | `#F0F0F0` | Chip backgrounds, secondary surfaces |
| `color_on_surface` | `#111111` | Primary text |
| `color_on_surface_secondary` | `#888888` | Metadata, timestamps, muted labels |
| `color_on_surface_tertiary` | `#BBBBBB` | Placeholder text, dividers |
| `color_primary` | `#111111` | Active chip, FAB, primary button fill |
| `color_on_primary` | `#FFFFFF` | Text/icon on primary fill |
| `color_accent` | `#E8845A` | Edit icon, destructive action highlight, single accent touch |
| `color_divider` | `#EBEBEB` | List separators |
| `color_outline` | `#E0E0E0` | Card borders (hairline, optional) |

### Dark Theme (Splash screen, potential dark mode)

| Token | Hex | Usage |
|-------|-----|-------|
| `color_background` | `#111111` | Screen background |
| `color_surface` | `#1C1C1C` | Cards, sheets |
| `color_surface_variant` | `#252525` | Chip backgrounds |
| `color_on_surface` | `#F5F5F5` | Primary text |
| `color_on_surface_secondary` | `#888888` | Muted text |
| `color_on_surface_tertiary` | `#444444` | Dividers, placeholders |
| `color_primary` | `#F5F5F5` | Active chip, FAB |
| `color_on_primary` | `#111111` | Text on primary fill |
| `color_accent` | `#E8845A` | Same accent, consistent across modes |
| `color_divider` | `#2A2A2A` | |

> **Accent usage rule:** `color_accent` appears on at most one element per screen. It is a signal, not a theme color. Use it only for: edit icon on note cards, active/focused field border, or a single CTA.

---

## 3. Typography

All type uses **Manrope**. Import path: `font/manrope_*.ttf` (variable or individual weights).

### Scale

| Style name | Weight | Size | Line height | Letter spacing | Usage |
|---|---|---|---|---|---|
| `Display` | ExtraBold (800) | 28sp | 36sp | -0.5px | Splash headline, onboarding |
| `Heading1` | Bold (700) | 20sp | 28sp | -0.3px | Screen titles, app bar title |
| `Heading2` | SemiBold (600) | 16sp | 24sp | -0.2px | Note title in card |
| `Body` | Regular (400) | 14sp | 22sp | 0px | Note content preview, body text |
| `BodyMedium` | Medium (500) | 14sp | 22sp | 0px | Sheet field labels, button text |
| `Caption` | Regular (400) | 12sp | 18sp | 0.2px | Timestamps, category tags, metadata |
| `CaptionMedium` | Medium (500) | 12sp | 18sp | 0.2px | Active chip label, badge text |
| `Overline` | SemiBold (600) | 11sp | 16sp | 1.0px | Section headers ("List Notes"), all-caps optional |

### Usage Rules

- **Note card title:** `Heading2`, clamp to 2 lines, ellipsize end
- **Note card content preview:** `Body`, `color_on_surface_secondary`, clamp to 3 lines
- **Timestamps / tags:** `Caption`, `color_on_surface_secondary`
- **App bar title:** `Heading1`
- **Active filter chip:** `CaptionMedium`, `color_on_primary` on `color_primary` background
- **Inactive filter chip:** `CaptionMedium`, `color_on_surface_secondary` on `color_surface_variant`
- **Bottom sheet field label:** `BodyMedium`, `color_on_surface_secondary`
- **Widget note content:** `Body` or `Heading2` depending on available size (see Widget section)

---

## 4. Spacing & Layout

Base unit: **4dp**

| Token | Value | Usage |
|-------|-------|-------|
| `space_xs` | 4dp | Icon-to-label gap, tag internal padding vertical |
| `space_sm` | 8dp | Tag internal padding horizontal, list item internal gaps |
| `space_md` | 16dp | Screen horizontal margin, card internal padding |
| `space_lg` | 24dp | Section spacing, bottom sheet top padding |
| `space_xl` | 32dp | Large vertical gaps |
| `space_xxl` | 48dp | Splash screen vertical rhythm |

**Screen margins:** `space_md` (16dp) on all sides.

**List item spacing:** No explicit divider lines by default. Use `space_sm` (8dp) vertical gap between cards.

---

## 5. Shape & Elevation

| Token | Value | Usage |
|-------|-------|-------|
| `radius_sm` | 8dp | Chips, tags, small badges |
| `radius_md` | 12dp | Note cards, input fields |
| `radius_lg` | 16dp | Bottom sheet top corners, FAB shape if squircle |
| `radius_full` | 100dp | Fully rounded pills (only if needed) |

**Elevation philosophy:** Use tonal surface color shifts only (`color_surface` vs `color_surface_variant`). No drop shadows on cards. A hairline border (`color_outline`, 0.5dp) is preferred over shadow for card separation.

**Bottom sheet corner radius:** Top corners `radius_lg`, bottom corners 0.

---

## 6. Components

### Note Card

```
┌─────────────────────────────────────┐
│ Title (Heading2, 2 lines max)   [◆] │  ← accent edit icon, appears on long-press or swipe
│                                     │
│ Content preview (Body, 3 lines)     │
│                                     │
│ [Tag1]  [Tag2]          2024-05-09  │  ← Caption, color_on_surface_secondary
└─────────────────────────────────────┘
```

- Background: `color_surface`
- Corner radius: `radius_md`
- Padding: `space_md` all sides
- No elevation shadow — use border `color_outline` 0.5dp or rely on background contrast
- Edit indicator (pencil/accent icon): visible only on swipe-to-reveal or long-press; `color_accent`
- Highlighted card variant (active/selected): left border `2dp color_accent`

### Filter Chip

- Inactive: `color_surface_variant` bg, `color_on_surface_secondary` label, `radius_sm`
- Active: `color_primary` bg, `color_on_primary` label
- Height: 32dp, horizontal padding `space_md`
- No icon in chip in MVP

### Category Tag (inside card)

- Background: `color_surface_variant`
- Label: `Caption`, `color_on_surface_secondary`
- Padding: `4dp` vertical, `8dp` horizontal
- Radius: `radius_sm`
- No colored category fills — all tags use the same neutral token

### FAB (Add Note)

- Shape: circle, 56dp × 56dp
- Background: `color_primary`
- Icon: `+`, `color_on_primary`
- No label in MVP
- Position: bottom-end, `space_md` margin from edges

### Bottom Sheet (Add/Edit Note)

- Top handle bar: 4dp × 32dp, `color_outline`, centered, `space_sm` below top edge
- Top radius: `radius_lg`
- Background: `color_surface`
- Internal padding: `space_md` horizontal, `space_lg` top after handle
- Field style: underline-only (no outlined box) — bottom border `1dp color_outline`, focused bottom border `1.5dp color_primary`
- Field label: `BodyMedium`, `color_on_surface_secondary`, floats up on focus
- Category selector: horizontal chip row inside sheet (same chip style as filter chips)
- Save button: full-width, height 52dp, `color_primary` bg, `BodyMedium` label, `radius_md`

### Search Bar

- Inline at top of home screen (below app bar), not a separate toolbar
- Height: 44dp
- Background: `color_surface_variant`
- Radius: `radius_md`
- Placeholder: `Body`, `color_on_surface_tertiary`
- Leading icon: search, `color_on_surface_secondary`
- No floating search FAB

### App Bar

- Background: `color_background` (blends with screen — no elevated surface)
- Title: `Heading1`, `color_on_surface`
- Trailing icons: max 2 (search toggle, settings), `color_on_surface_secondary`
- No bottom border/divider

### Bottom Navigation (if used)

- Background: `color_surface`
- Top border: `1dp color_divider`
- 4 items max
- Active item: `color_primary` icon + label
- Inactive: `color_on_surface_tertiary`
- Label style: `Caption`

---

## 7. Widget Design

The widget is the primary surface. It must be readable at a glance with no interaction required.

### Small Widget (≈ 2×2)

```
┌──────────────────────┐
│ LANGUAGE        ·    │  ← Overline (category), color_on_surface_secondary
│                      │
│ Note title here      │  ← Heading2
│                      │
│ Note content preview │  ← Body, color_on_surface_secondary, 3 lines
│ that can wrap here   │
└──────────────────────┘
```

- Background: `color_surface` (light) / `color_background` (dark)
- Widget corner radius: follows system widget shape (Glance default)
- Padding: `space_md`
- No buttons, no metadata row

### Medium/Large Widget

Adds below the note content:

```
│ ── ── ── ── ── ── ── │  ← color_divider line
│ Sequential  12 notes │  ← Caption, color_on_surface_tertiary, space-between layout
```

- Rotation mode label: `Caption`, `color_on_surface_tertiary`
- Note count: `Caption`, `color_on_surface_tertiary`
- Last updated: omit unless space is abundant (Large only)

### Widget Color Modes

Support two presets:
- **Light:** `color_surface` background, dark text
- **Dark:** `#111111` background, light text (mirrors splash screen palette)

User selects per widget instance in config screen.

---

## 8. Iconography

- Use **Material Symbols** (Outlined weight, Grade 0, Optical size 24)
- Stroke weight: consistent with Manrope's geometric feel — use `wght=300` or `wght=400` symbol variant
- Icon size: 24dp standard, 20dp for in-card/dense contexts
- Color: always inherit from context token (`color_on_surface`, `color_on_surface_secondary`, or `color_accent`)
- No filled icons in MVP except FAB `+`

Key icons:
| Context | Icon name |
|---------|-----------|
| Add note FAB | `add` |
| Search | `search` |
| Settings | `tune` or `settings` |
| Edit note | `edit` (accent colored) |
| Delete | `delete` |
| Category | `label` |
| Widget config | `widgets` |
| Rotation mode | `shuffle` / `arrow_forward` |

---

## 9. Splash Screen

Mirrors the dark reference screen exactly:

- Background: `#111111`
- Logo: white icon + wordmark **"Repeat Note"** in `Display` / Manrope ExtraBold
- Tagline: 1 line, `Body`, `color_on_surface_secondary` (`#888888`)
- Loading indicator: `CircularProgressIndicator`, small, `color_on_surface_tertiary`
- Centered layout, vertically distributed with generous negative space

---

## 10. Motion

**Philosophy:** Purposeful and minimal. No decorative animation.

| Interaction | Animation |
|---|---|
| Screen transition | Fade + slight vertical slide (120ms, FastOutSlowIn) |
| Bottom sheet open | Slide up (300ms, FastOutSlowIn) |
| Bottom sheet close | Slide down (220ms, LinearOutSlowIn) |
| FAB press | Scale down to 0.95 (80ms) + release (120ms) |
| Chip select | Background color crossfade (150ms) |
| Note card appear (list load) | Staggered fade-in, 30ms delay per item, 200ms duration |
| Widget content update | No animation — content replaces silently |

No bounce, spring, or overshoot. No parallax.

---

## 11. Android-Specific Implementation Notes

### Compose Theme Setup

```kotlin
// Typography.kt — map tokens to Compose TextStyle
val ManropeFamily = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
)

val RepeatNoteTypography = Typography(
    displayLarge   = TextStyle(fontFamily = ManropeFamily, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = ManropeFamily, fontWeight = FontWeight.Bold,      fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.3).sp),
    titleMedium    = TextStyle(fontFamily = ManropeFamily, fontWeight = FontWeight.SemiBold,  fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = (-0.2).sp),
    bodyLarge      = TextStyle(fontFamily = ManropeFamily, fontWeight = FontWeight.Normal,    fontSize = 14.sp, lineHeight = 22.sp),
    bodyMedium     = TextStyle(fontFamily = ManropeFamily, fontWeight = FontWeight.Medium,    fontSize = 14.sp, lineHeight = 22.sp),
    labelSmall     = TextStyle(fontFamily = ManropeFamily, fontWeight = FontWeight.Normal,    fontSize = 12.sp, lineHeight = 18.sp, letterSpacing = 0.2.sp),
    labelMedium    = TextStyle(fontFamily = ManropeFamily, fontWeight = FontWeight.Medium,    fontSize = 12.sp, lineHeight = 18.sp, letterSpacing = 0.2.sp),
)
```

### Color Scheme Setup

```kotlin
// Color.kt
val Neutral900 = Color(0xFF111111)
val Neutral800 = Color(0xFF1C1C1C)
val Neutral100 = Color(0xFFF7F7F7)
val White      = Color(0xFFFFFFFF)
val Muted      = Color(0xFF888888)
val Outline    = Color(0xFFE0E0E0)
val Accent     = Color(0xFFE8845A)

val LightColorScheme = lightColorScheme(
    background        = Neutral100,
    surface           = White,
    surfaceVariant    = Color(0xFFF0F0F0),
    onBackground      = Neutral900,
    onSurface         = Neutral900,
    onSurfaceVariant  = Muted,
    primary           = Neutral900,
    onPrimary         = White,
    secondary         = Accent,
    outline           = Outline,
)

val DarkColorScheme = darkColorScheme(
    background        = Neutral900,
    surface           = Neutral800,
    surfaceVariant    = Color(0xFF252525),
    onBackground      = Color(0xFFF5F5F5),
    onSurface         = Color(0xFFF5F5F5),
    onSurfaceVariant  = Muted,
    primary           = Color(0xFFF5F5F5),
    onPrimary         = Neutral900,
    secondary         = Accent,
    outline           = Color(0xFF2A2A2A),
)
```

### Glance Widget Theme

Glance does not inherit Compose MaterialTheme. Define a `GlanceTheme` block:

```kotlin
GlanceTheme(
    colors = if (isSystemInDarkTheme) GlanceDarkColors else GlanceLightColors
) { /* widget content */ }
```

Map the same hex values to `ColorProviders` for Glance. Do not hardcode colors inline in widget composables.

### Shape Tokens

```kotlin
val RepeatNoteShapes = Shapes(
    small  = RoundedCornerShape(8.dp),   // chips, tags
    medium = RoundedCornerShape(12.dp),  // cards, inputs
    large  = RoundedCornerShape(16.dp),  // bottom sheet, large surfaces
)
```

---

## 12. Do / Don't Reference

| ✅ Do | ❌ Don't |
|-------|---------|
| Use Manrope weight contrast for hierarchy | Add color to category tags |
| Keep backgrounds neutral (`#F7F7F7`, `#FFFFFF`) | Use purple, blue, or any hue on backgrounds |
| Single `color_accent` touch per screen | Use accent as a theme color throughout |
| Underline-style inputs in bottom sheet | Use outlined `TextField` boxes |
| Staggered fade for list entry | Slide-in from left/right for list items |
| Monochrome icons, consistent optical size | Mix icon styles or weights |
| Hairline borders over drop shadows | Add elevation shadows to cards |
| `ExtraBold` only for splash/display context | Set body text to ExtraBold |
| One FAB, bottom-end | Multiple FABs or extended FAB with label |