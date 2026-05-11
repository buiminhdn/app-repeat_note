package com.marcus.repeatnote.feature_notes.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marcus.repeatnote.domain.model.Note
import com.marcus.repeatnote.ui.theme.Accent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue

private val noteColors = listOf(
    Color(0xFFFFFDF5),
    Color(0xFFF2F7F2),
    Color(0xFFFFFFFF),
    Color(0xFFFFF5F0),
    Color(0xFFF5F5FF),
    Color(0xFFF5FAFA)
)

private val noteBorders = listOf(
    Color(0xFFD1CFC8),
    Color(0xFFC6CAC6),
    Color(0xFFD1D1D1),
    Color(0xFFD1C8C4),
    Color(0xFFC8C8D1),
    Color(0xFFC8CDCD)
)

private val noteTags = listOf(
    Color(0xFFF5EDDA),
    Color(0xFFE4F0E4),
    Color(0xFFF5F5F5),
    Color(0xFFFCE3D7),
    Color(0xFFE6E6FA),
    Color(0xFFE0F0F0)
)

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorIndex = (note.id.absoluteValue % 6).toInt()
    val bgColor = noteColors[colorIndex]
    val borderColor = noteBorders[colorIndex]
    val tagColor = noteTags[colorIndex]

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = bgColor,
        ),
        border = BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Title row with edit icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit note",
                    tint = Accent,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content preview
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tags and date row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Category tag
                Surface(
                    shape = CircleShape,
                    color = tagColor,
                ) {
                    Text(
                        text = note.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF888888),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                // Date
                Text(
                    text = formatDate(note.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}
