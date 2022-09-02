package com.dev.notespace.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.dev.core.model.presenter.Note

@Composable
fun HomePopularList(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    getPreview: (String, String, (ImageBitmap?) -> Unit) -> Unit
) {
    LazyRow(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        items(
            items = notes,
            key = { note -> note.note_id }
        ) { note ->
            var preview by remember {
                mutableStateOf<ImageBitmap?>(null)
            }

            LaunchedEffect(true) {
                getPreview(
                    note.user_id,
                    note.note_id
                ) { imgBitmap -> preview = imgBitmap }
            }

            NoteItem(
                modifier = Modifier
                    .clickable {  },
                preview = preview,
                star = note.star,
                name = note.name,
                subject = note.subject
            )
        }
    }
}