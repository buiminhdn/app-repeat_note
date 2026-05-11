package com.marcus.repeatnote.feature_widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.marcus.repeatnote.ui.theme.RepeatNoteTheme
import com.marcus.repeatnote.worker.WidgetWorkScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetConfigActivity : ComponentActivity() {

    private val viewModel: WidgetConfigViewModel by viewModels()

    @Inject
    lateinit var workScheduler: WidgetWorkScheduler

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to CANCELED first — if user backs out, widget won't be placed
        setResult(Activity.RESULT_CANCELED)

        // Get the widget ID from the intent
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        viewModel.setWidgetId(appWidgetId)

        // Listen for config saved effect
        lifecycleScope.launch {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is WidgetConfigContract.Effect.ConfigSaved -> {
                        // Schedule the worker
                        workScheduler.schedulePeriodicUpdate(this@WidgetConfigActivity)

                        // Trigger immediate update
                        workScheduler.triggerImmediateUpdate(this@WidgetConfigActivity)

                        // Return RESULT_OK
                        val resultIntent = Intent().apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }

                    is WidgetConfigContract.Effect.ShowError -> {
                        // Could show a toast or snackbar
                    }
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            RepeatNoteTheme {
                WidgetConfigScreen(viewModel = viewModel)
            }
        }
    }
}
