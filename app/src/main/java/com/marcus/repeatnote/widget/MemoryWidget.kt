package com.marcus.repeatnote.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.marcus.repeatnote.MainActivity
import com.marcus.repeatnote.ui.theme.Neutral400
import com.marcus.repeatnote.ui.theme.Neutral50
import com.marcus.repeatnote.ui.theme.Neutral900
import com.marcus.repeatnote.ui.theme.White

class MemoryWidget : GlanceAppWidget() {

    companion object {
        private val SMALL_SIZE = DpSize(120.dp, 120.dp)
        private val MEDIUM_SIZE = DpSize(200.dp, 200.dp)
        private val LARGE_SIZE = DpSize(300.dp, 300.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(SMALL_SIZE, MEDIUM_SIZE, LARGE_SIZE)
    )

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val widgetIdKey = WidgetStateKeys.stateJson(0)

            // Try to get state from each possible widget ID key
            var uiState = WidgetStateSerializer.defaultState()
            for (key in prefs.asMap().keys) {
                if (key.name.startsWith("state_")) {
                    val json = prefs[key as Preferences.Key<String>] ?: continue
                    val candidate = WidgetStateSerializer.deserialize(json)
                    if (candidate.widgetId != 0 || !candidate.isEmpty) {
                        uiState = candidate
                        break
                    }
                }
            }

            // Also check the generic key
            val genericJson = prefs[WidgetStateKeys.stateJson(0)]
            if (genericJson != null) {
                val candidate = WidgetStateSerializer.deserialize(genericJson)
                if (!candidate.isEmpty || candidate.noteTitle.isNotEmpty()) {
                    uiState = candidate
                }
            }

            WidgetContent(uiState = uiState)
        }
    }

    @Composable
    private fun WidgetContent(uiState: WidgetUiState) {
        val size = LocalSize.current
        val isSmall = size.width < 200.dp || size.height < 200.dp

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(White)
                .clickable(actionStartActivity<MainActivity>()),
        ) {
            if (uiState.isEmpty) {
                EmptyState(uiState.emptyMessage)
            } else if (isSmall) {
                SmallLayout(uiState)
            } else {
                MediumLargeLayout(uiState)
            }
        }
    }

    @Composable
    private fun EmptyState(message: String) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = message,
                style = TextStyle(
                    color = ColorProvider(Neutral400),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                ),
            )
        }
    }

    @Composable
    private fun SmallLayout(uiState: WidgetUiState) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            // Category overline
            Text(
                text = uiState.category.uppercase(),
                style = TextStyle(
                    color = ColorProvider(Neutral400),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Title
            Text(
                text = uiState.noteTitle,
                style = TextStyle(
                    color = ColorProvider(Neutral900),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 2,
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Content
            Text(
                text = uiState.noteContent,
                style = TextStyle(
                    color = ColorProvider(Neutral400),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                maxLines = 3,
            )
        }
    }

    @Composable
    private fun MediumLargeLayout(uiState: WidgetUiState) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            // Category overline
            Text(
                text = uiState.category.uppercase(),
                style = TextStyle(
                    color = ColorProvider(Neutral400),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Title
            Text(
                text = uiState.noteTitle,
                style = TextStyle(
                    color = ColorProvider(Neutral900),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 2,
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Content
            Text(
                text = uiState.noteContent,
                style = TextStyle(
                    color = ColorProvider(Neutral400),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                maxLines = 4,
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            // Divider-like spacing
            Spacer(modifier = GlanceModifier.height(8.dp))

            // Footer: rotation mode + note count
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = uiState.rotationMode.name.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    style = TextStyle(
                        color = ColorProvider(Neutral400),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = "${uiState.totalNotes} notes",
                    style = TextStyle(
                        color = ColorProvider(Neutral400),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
        }
    }
}
