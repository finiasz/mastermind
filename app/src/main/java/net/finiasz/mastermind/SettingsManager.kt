package net.finiasz.mastermind

import android.content.Context
import androidx.compose.runtime.mutableStateOf

class SettingsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val pegCount = mutableStateOf(getInt(PEG_COUNT_KEY, 5))
    val colorCount = mutableStateOf(getInt(COLOR_COUNT_KEY, 6))
    val allowDuplicates = mutableStateOf(getBoolean(ALLOW_DUPLICATES_KEY, false))
    val guessCount = mutableStateOf(getInt(GUESS_COUNT_KEY, 12))
    val colorBlind = mutableStateOf(getBoolean(COLOR_BLIND, false))

    fun setPegCount(pegCount : Int) {
        saveInt(PEG_COUNT_KEY, pegCount)
        this.pegCount.value = pegCount
    }

    fun setColorCount(colorCount : Int) {
        saveInt(COLOR_COUNT_KEY, colorCount)
        this.colorCount.value = colorCount
    }

    fun setAllowDuplicates(allowDuplicates : Boolean) {
        saveBoolean(ALLOW_DUPLICATES_KEY, allowDuplicates)
        this.allowDuplicates.value = allowDuplicates
    }

    fun setGuessCount(guessCount : Int) {
        saveInt(GUESS_COUNT_KEY, guessCount)
        this.guessCount.value = guessCount
    }

    fun setColorBlind(colorBlind : Boolean) {
        saveBoolean(COLOR_BLIND, colorBlind)
        this.colorBlind.value = colorBlind
    }

    private fun getBoolean(key: String, default : Boolean) : Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    private fun saveBoolean(key : String, value : Boolean?) {
        val editor = sharedPreferences.edit()
        if (value == null) {
            editor.remove(key)
        } else {
            editor.putBoolean(key, value)
        }
        editor.apply()
    }

    private fun getInt(key: String, default : Int) : Int {
        return sharedPreferences.getInt(key, default)
    }

    private fun saveInt(key : String, value : Int?) {
        val editor = sharedPreferences.edit()
        if (value == null) {
            editor.remove(key)
        } else {
            editor.putInt(key, value)
        }
        editor.apply()
    }

    companion object {
        val PEG_COUNT_KEY = "peg_count"
        val COLOR_COUNT_KEY = "color_count"
        val ALLOW_DUPLICATES_KEY = "allow_duplicates"
        val GUESS_COUNT_KEY = "guess_count"
        val COLOR_BLIND = "color_blind"
    }
}