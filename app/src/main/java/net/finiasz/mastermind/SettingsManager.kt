package net.finiasz.mastermind

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val pegCount = mutableIntStateOf(getInt(PEG_COUNT_KEY, 5))
    val colorCount = mutableIntStateOf(getInt(COLOR_COUNT_KEY, 6))
    val allowDuplicates = mutableStateOf(getBoolean(ALLOW_DUPLICATES_KEY, false))
    val guessCount = mutableIntStateOf(getInt(GUESS_COUNT_KEY, 12))
    val colorBlind = mutableStateOf(getBoolean(COLOR_BLIND, false))

    fun setPegCount(pegCount : Int) {
        saveInt(PEG_COUNT_KEY, pegCount)
        this.pegCount.intValue = pegCount
    }

    fun setColorCount(colorCount : Int) {
        saveInt(COLOR_COUNT_KEY, colorCount)
        this.colorCount.intValue = colorCount
    }

    fun setAllowDuplicates(allowDuplicates : Boolean) {
        saveBoolean(ALLOW_DUPLICATES_KEY, allowDuplicates)
        this.allowDuplicates.value = allowDuplicates
    }

    fun setGuessCount(guessCount : Int) {
        saveInt(GUESS_COUNT_KEY, guessCount)
        this.guessCount.intValue = guessCount
    }

    fun setColorBlind(colorBlind : Boolean) {
        saveBoolean(COLOR_BLIND, colorBlind)
        this.colorBlind.value = colorBlind
    }

    @Suppress("SameParameterValue")
    private fun getBoolean(key: String, default : Boolean) : Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    private fun saveBoolean(key : String, value : Boolean?) {
        sharedPreferences.edit {
            if (value == null) {
                remove(key)
            } else {
                putBoolean(key, value)
            }
        }
    }

    private fun getInt(key: String, default : Int) : Int {
        return sharedPreferences.getInt(key, default)
    }

    private fun saveInt(key : String, value : Int?) {
        sharedPreferences.edit {
            if (value == null) {
                remove(key)
            } else {
                putInt(key, value)
            }
        }
    }

    companion object {
        const val PEG_COUNT_KEY = "peg_count"
        const val COLOR_COUNT_KEY = "color_count"
        const val ALLOW_DUPLICATES_KEY = "allow_duplicates"
        const val GUESS_COUNT_KEY = "guess_count"
        const val COLOR_BLIND = "color_blind"
    }
}