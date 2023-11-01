package net.finiasz.mastermind

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Random

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state : StateFlow<GameState> = _state.asStateFlow()

    fun reset(pegCount: Int, colorCount: Int, allowDuplicates: Boolean, guessCount: Int) {
        if (pegCount < 1 || colorCount < 1 || guessCount < 1) {
            return
        }


        val target : MutableList<Int>
        if (allowDuplicates) {
            val random = Random()
            target = MutableList(pegCount) { random.nextInt(colorCount) }
        } else {
            val shuffled = MutableList(colorCount) {index -> index}
            shuffled.shuffle()
            target = shuffled.subList(0, pegCount)
        }

        val value = GameState(
            target = target,
            revealedTargets = MutableList(pegCount) { false },
            colorCount = colorCount,
            guesses = List(guessCount) { MutableList(pegCount) { null } },
            exactPlacements = MutableList(guessCount) { null },
            badPlacements = MutableList(guessCount) { null },
            validatedCount = 0,
            selectedIndex = 0,
            won = Won.NOT_WON
        )

        _state.update { value }
    }


    fun pegClicked(row: Int, col: Int) {
        if (state.value.target != null && state.value.won == Won.NOT_WON && state.value.validatedCount == row) {
            _state.update { it.copy(selectedIndex = col) }
        }
    }

    fun colorClicked(color: Int) {
        if (state.value.target != null && state.value.won == Won.NOT_WON) {
            _state.update {
                val guesses = it.guesses.toList()
                guesses[it.validatedCount][it.selectedIndex] = color

                it.copy(
                    guesses = guesses,
                    selectedIndex = (it.selectedIndex + 1) % (it.target?.size ?: 1)
                )
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun validate() {
        if (state.value.target != null && state.value.won == Won.NOT_WON && state.value.guesses[state.value.validatedCount].contains(null).not()) {
            val targetCopy : MutableList<Int?> = state.value.target?.toMutableList() ?: return
            val guessCopy : MutableList<Int?> = state.value.guesses[state.value.validatedCount].toMutableList()

            var exact = 0
            var bad = 0

            for (i in 0..<guessCopy.size) {
                if (guessCopy[i] == targetCopy[i]) {
                    exact++
                    targetCopy[i] = null
                    guessCopy[i] = null
                }
            }

            for (i in 0..<guessCopy.size) {
                if (guessCopy[i] != null) {
                    val pos = targetCopy.indexOf(guessCopy[i])
                    if (pos != -1) {
                        bad++
                        targetCopy[pos] = null
                    }
                }
            }

            _state.update {
                val exacts = it.exactPlacements.apply { this[it.validatedCount] = exact }
                val bads = it.badPlacements.apply { this[it.validatedCount] = bad }
                var won = it.won
                var validatedCount = it.validatedCount + 1

                if (exact == guessCopy.size) {
                    won = Won.WON
                    validatedCount = it.guesses.size
                } else if (validatedCount >= it.guesses.size) {
                    won = Won.LOST
                }

                it.copy(
                    exactPlacements = exacts,
                    badPlacements = bads,
                    validatedCount = validatedCount,
                    won = won,
                    selectedIndex = 0
                )
            }
        }
    }

    fun reveal(pos: Int) {
        if (state.value.won == Won.NOT_WON && state.value.revealedTargets[pos].not()) {
            _state.update {
                it.copy(
                    revealedTargets = it.revealedTargets.apply { this[pos] = true }
                )
            }
        }
    }
}