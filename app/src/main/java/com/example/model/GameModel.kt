package com.example.model

enum class Move(
    val id: String,
    val displayName: String,
    val emoji: String,
    val iconName: String
) {
    ROCK("rock", "Rock", "✊", "ic_rock"),
    PAPER("paper", "Paper", "✋", "ic_paper"),
    SCISSORS("scissors", "Scissors", "✌️", "ic_scissors");

    fun beats(other: Move): Boolean {
        return when (this) {
            ROCK -> other == SCISSORS
            PAPER -> other == ROCK
            SCISSORS -> other == PAPER
        }
    }

    companion object {
        fun fromId(id: String): Move? = entries.find { it.id.equals(id, ignoreCase = true) }
        fun random(): Move = entries.random()
    }
}

enum class GameResult(
    val displayText: String,
    val scoreDelta: Int,
    val shortCode: String
) {
    WIN("You win!", 1, "W"),
    LOSS("You lose!", -1, "L"),
    TIE("It's a tie!", 0, "T")
}

data class RoundRecord(
    val roundNumber: Int,
    val playerChoice: Move,
    val computerChoice: Move,
    val result: GameResult,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameStats(
    val totalRounds: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val ties: Int = 0,
    val currentStreak: Int = 0, // Positive for win streak, negative for loss streak
    val bestWinStreak: Int = 0,
    val playerRockCount: Int = 0,
    val playerPaperCount: Int = 0,
    val playerScissorsCount: Int = 0,
    val computerRockCount: Int = 0,
    val computerPaperCount: Int = 0,
    val computerScissorsCount: Int = 0
) {
    val winRatePercent: Float
        get() = if (totalRounds == 0) 0f else (wins.toFloat() / totalRounds) * 100f

    val lossRatePercent: Float
        get() = if (totalRounds == 0) 0f else (losses.toFloat() / totalRounds) * 100f

    val tieRatePercent: Float
        get() = if (totalRounds == 0) 0f else (ties.toFloat() / totalRounds) * 100f

    val netScore: Int
        get() = wins - losses

    val favoritePlayerMove: Move?
        get() {
            if (totalRounds == 0) return null
            val maxCount = maxOf(playerRockCount, playerPaperCount, playerScissorsCount)
            if (maxCount == 0) return null
            return when (maxCount) {
                playerRockCount -> Move.ROCK
                playerPaperCount -> Move.PAPER
                else -> Move.SCISSORS
            }
        }
}
