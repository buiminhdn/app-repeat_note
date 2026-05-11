package com.marcus.repeatnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.marcus.repeatnote.feature_notes.NotesScreen
import com.marcus.repeatnote.ui.theme.RepeatNoteTheme
import com.marcus.repeatnote.worker.WidgetWorkScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var workScheduler: WidgetWorkScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule widget updates on app start
        workScheduler.schedulePeriodicUpdate(this)

        setContent {
            RepeatNoteTheme {
                NotesScreen()
            }
        }
    }
}