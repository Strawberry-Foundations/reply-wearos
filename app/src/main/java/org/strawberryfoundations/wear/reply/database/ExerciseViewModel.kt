package org.strawberryfoundations.wear.reply.database

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.strawberryfoundations.wear.reply.core.model.Exercise
import org.strawberryfoundations.wear.reply.theme.contrastColor
import org.strawberryfoundations.wear.reply.theme.darkenColor
import org.strawberryfoundations.wear.reply.theme.hexToColor


data class ExerciseUi(
    val exercise: Exercise,
    val buttonColor: Color,
    val fgColor: Color,
    val secondaryBgColor: Color,
    val secondaryTextColor: Color,
)


class ExerciseViewModel(application: Application): AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).trainingDao()

    val trainings: StateFlow<List<Exercise>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI model with precomputed colors to avoid repeated color conversions in Compose
    val trainingsUi: StateFlow<List<ExerciseUi>> = dao.getAll()
        .map { list ->
            list.map { e ->
                val button = hexToColor(e.color)
                val fg = contrastColor(button)
                val secBg = darkenColor(button)
                val secText = contrastColor(secBg)
                ExerciseUi(
                    exercise = e,
                    buttonColor = button,
                    fgColor = fg,
                    secondaryBgColor = secBg,
                    secondaryTextColor = secText
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(training: Exercise) {
        viewModelScope.launch { dao.insert(training) }
    }

    fun update(training: Exercise) {
        viewModelScope.launch { dao.update(training) }
    }

    fun delete(training: Exercise) {
        viewModelScope.launch { dao.delete(training) }
    }
}