package com.dev.notespace.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
class AddByImageViewModel @Inject constructor(
    private val useCase: NoteSpaceUseCase
): ViewModel() {
    val nameHolder = TextFieldHolder()
    val descriptionHolder = TextFieldHolder()
    val subjectHolder = TextFieldHolder()

    private val _textByImages = mutableStateListOf<String>()
    val textByImages: SnapshotStateList<String>
        get() = _textByImages

    fun getTextByImages(
        imagesUri: List<Uri>,
        context: Context
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _textByImages.clear()

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            imagesUri.forEach {
                val image: InputImage
                try {
                    image = InputImage.fromFilePath(context, it)

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            _textByImages.add(visionText.text)
                        }
                        .addOnFailureListener { e ->
                            Timber.e(e.message.toString())
                        }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        }
    }

    private val _insertResult = mutableStateOf<Resource<Any?>>(Resource.Loading())
    val insertResult: State<Resource<Any?>>
        get() = _insertResult

    fun insertNote(file: List<Uri>, previewUri: Uri) = viewModelScope.launch {
        _insertResult.value =
            useCase.insertNoteByImg(
                nameHolder.value,
                descriptionHolder.value,
                subjectHolder.value,
                file,
                _textByImages.subList(0, _textByImages.size).toList(),
                previewUri
            )
    }

}