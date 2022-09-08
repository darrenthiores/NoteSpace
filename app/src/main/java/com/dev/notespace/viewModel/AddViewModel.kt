package com.dev.notespace.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.data.Resource
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.notespace.holder.TextFieldHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    val nameHolder = TextFieldHolder()
    val descriptionHolder = TextFieldHolder()
    val subjectHolder = TextFieldHolder()

    private val _previews = mutableStateListOf<ImageBitmap?>()
    val previews: SnapshotStateList<ImageBitmap?>
        get() = _previews

    fun getPreviews(
        mediaUri: Uri?,
        width: Int,
        height: Int,
        context: Context
    ) = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _previews.clear()

                val input = kotlin.runCatching {
                    if(mediaUri!=null) context.contentResolver.openFileDescriptor(mediaUri, "r") else null
                }.getOrNull()

                val renderer = if (input != null) kotlin.runCatching { PdfRenderer(input) }
                    .getOrNull() else null

                for (i in 0 until (renderer?.pageCount ?: 0)) {
                    val page = renderer?.openPage(i)
                    val bitmap =
                        Bitmap.createBitmap(
                            width,
                            height,
                            Bitmap.Config.ARGB_8888
                        )
                    page?.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )
                    _previews.add(bitmap.asImageBitmap())
                    page?.close()
                }
                renderer?.close()
            }
    }

    private val _insertResult = mutableStateOf<Resource<Any?>>(Resource.Loading())
    val insertResult: State<Resource<Any?>>
        get() = _insertResult

    fun insertNote(file: Uri, previewUri: Uri) = viewModelScope.launch {
        _insertResult.value =
            useCase.insertNote(
                nameHolder.value,
                descriptionHolder.value,
                subjectHolder.value,
                file,
                previewUri
            )
    }

}