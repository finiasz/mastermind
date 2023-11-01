package net.finiasz.mastermind

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
            return guesses.get(validatedCount).contains(null).not()
        }

    val firstRowColorCount: Int
        get() {
            return when(colorCount) {
                4 -> 2
                5 -> 3
                6 -> 4
                7 -> 4
                8 -> 5
                9 -> 5
                else -> 6
            }
        }
}


enum class Won {
    NOT_WON,
    LOST,
    WON
}