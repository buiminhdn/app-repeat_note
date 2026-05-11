package com.marcus.repeatnote.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.marcus.repeatnote.worker.WidgetWorkScheduler

class MemoryWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = MemoryWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Schedule periodic worker when first widget is enabled
        val scheduler = WidgetWorkScheduler()
        scheduler.schedulePeriodicUpdate(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // Trigger immediate update for newly added widgets
        val scheduler = WidgetWorkScheduler()
        scheduler.triggerImmediateUpdate(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        // Widget cleanup would happen in the worker on next run
    }
}
