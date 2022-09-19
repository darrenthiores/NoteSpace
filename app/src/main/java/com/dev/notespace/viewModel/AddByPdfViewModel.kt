package com.dev.notespace.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.core.data.Resource
import com.dev.core.domain.useCase.NoteSpaceUseCase
import com.dev.notespace.holder.TextFieldHolder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddByPdfViewModel @Inject constructor(
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
                            width * 2,
                            height * 2,
                            Bitmap.Config.ARGB_8888
                        )
                    page?.render(
                        bitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )
                    _previews.add(bitmap.asImageBitmap())
                    getTextByPdf(bitmap)
                    page?.close()
                }
                renderer?.close()
            }
    }

    private val _textByImages = mutableStateListOf<String>()
    val textByImages: SnapshotStateList<String>
        get() = _textByImages

    private fun getTextByPdf(
        pdfBitmap: Bitmap
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _textByImages.clear()

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(pdfBitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    _textByImages.add(visionText.text)
                }
                .addOnFailureListener { e ->
                    Timber.e(e.message.toString())
                    _textByImages.add("")
                }

        }
    }

    private val _insertResult = mutableStateOf<Resource<Any?>>(Resource.Loading())
    val insertResult: State<Resource<Any?>>
        get() = _insertResult

    fun insertNote(file: Uri, previewUri: Uri) = viewModelScope.launch {
        _insertResult.value =
            useCase.insertNoteByPdf(
                nameHolder.value,
                descriptionHolder.value,
                subjectHolder.value,
                file,
                textByImages.subList(0, textByImages.size).toList(),
                previewUri
            )
    }

}