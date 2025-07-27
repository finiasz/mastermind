package net.finiasz.mastermind

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper

@JsonIgnoreProperties(ignoreUnknown = true)
data class GameState(
    val target : List<Int>? = null,
    val revealedTargets : MutableList<Boolean> = MutableList(0) { false },
    val colorCount : Int = 0,
    val guesses : List<MutableList<Int?>> = List(0) {MutableList(0) { null }},
    val exactPlacements : MutableList<Int?> = MutableList(0) {null},
    val badPlacements : MutableList<Int?> = MutableList(0) {null},
    var validatedCount : Int = 0,
    var selectedIndex: Int = 0,
    var won : Won = Won.NOT_WON,
) {

    val validateEnabled: Boolean
        get () {
            if (target == null || won != Won.NOT_WON) {
                return false
            }
            return guesses[validatedCount].contains(null).not()
        }

    fun serialize(): String? {
        return runCatching {
            ObjectMapper().writeValueAsString(this)
        }.getOrNull()
    }

    companion object {
        fun of(serialized: String): GameState {
            return runCatching {
                ObjectMapper().readValue(serialized, GameState::class.java)
            }.getOrElse {
                GameState()
            }
        }
    }
}

fun String?.deserializeGameState(): GameState? {
    return this?.let {
        GameState.of(it)
    }
}

enum class Won {
    NOT_WON,
    LOST,
    WON
}