package com.marcus.repeatnote.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RepeatNoteShapes = Shapes(
    small = RoundedCornerShape(8.dp),   // chips, tags
    medium = RoundedCornerShape(12.dp), // cards, inputs
    large = RoundedCornerShape(16.dp),  // bottom sheet, large surfaces
)
