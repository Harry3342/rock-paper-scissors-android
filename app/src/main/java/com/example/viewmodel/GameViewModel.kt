package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.GameResult
import com.example.model.GameStats
import com.example.model.Move
import com.example.model.RoundRecord
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HistoryFilter(val label: String) {
    ALL("All"),
    WINS("Wins"),
    LOSSES("Losses"),
    TIES("Ties")
}

data class GameUiState(
    val playerChoice: Move? = null,
    val computerChoice: Move? = null,
    val currentResult: GameResult? = null,
    val isAnimating: Boolean = false,
    val animationStep: String = "",
    val history: List<RoundRecord> = emptyList(),
    val stats: GameStats = GameStats(),
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val historyFilter: HistoryFilter = HistoryFilter.ALL,
    val selectedVizTab: Int = 0 // 0: Overview/Donut, 1: Trends, 2: Choices, 3: Matchup Matrix
)

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        // Pre-populate a few realistic rounds so visualization components
        // are immediately demonstrative and visually engaging on launch.
        populateInitialRounds()
    }

    private fun populateInitialRounds() {
        val demoRounds = listOf(
            RoundRecord(1, Move.ROCK, Move.SCISSORS, GameResult.WIN),
            RoundRecord(2, Move.PAPER, Move.ROCK, GameResult.WIN),
            RoundRecord(3, Move.SCISSORS, Move.ROCK, GameResult.LOSS),
            RoundRecord(4, Move.ROCK, Move.ROCK, GameResult.TIE),
            RoundRecord(5, Move.PAPER, Move.SCISSORS, GameResult.LOSS),
            RoundRecord(6, Move.ROCK, Move.SCISSORS, GameResult.WIN),
            RoundRecord(7, Move.PAPER, Move.ROCK, GameResult.WIN)
        )
        val initialStats = computeStats(demoRounds)
        val lastRound = demoRounds.last()

        _uiState.update {
            it.copy(
                playerChoice = lastRound.playerChoice,
                computerChoice = lastRound.computerChoice,
                currentResult = lastRound.result,
                history = demoRounds,
                stats = initialStats
            )
        }
    }

    fun play(playerMove: Move) {
        if (_uiState.value.isAnimating) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnimating = true,
                    playerChoice = playerMove,
                    computerChoice = null,
                    currentResult = null,
                    animationStep = "Rock..."
                )
            }

            // Quick suspense animation
            delay(180)
            _uiState.update { it.copy(animationStep = "Paper...") }
            delay(180)
            _uiState.update { it.copy(animationStep = "Scissors...") }
            delay(180)
            _uiState.update { it.copy(animationStep = "Shoot!") }
            delay(120)

            val computerMove = Move.random()
            val result = if (computerMove == playerMove) {
                GameResult.TIE
            } else if (playerMove.beats(computerMove)) {
                GameResult.WIN
            } else {
                GameResult.LOSS
            }

            val newRoundNumber = (_uiState.value.history.maxOfOrNull { it.roundNumber } ?: 0) + 1
            val newRound = RoundRecord(
                roundNumber = newRoundNumber,
                playerChoice = playerMove,
                computerChoice = computerMove,
                result = result
            )

            val updatedHistory = _uiState.value.history + newRound
            val updatedStats = computeStats(updatedHistory)

            _uiState.update {
                it.copy(
                    isAnimating = false,
                    computerChoice = computerMove,
                    currentResult = result,
                    history = updatedHistory,
                    stats = updatedStats
                )
            }
        }
    }

    fun simulateRounds(count: Int = 5) {
        if (_uiState.value.isAnimating) return

        val currentHistory = _uiState.value.history.toMutableList()
        var lastP = Move.ROCK
        var lastC = Move.ROCK
        var lastR = GameResult.TIE

        repeat(count) {
            val p = Move.random()
            val c = Move.random()
            val r = if (p == c) GameResult.TIE else if (p.beats(c)) GameResult.WIN else GameResult.LOSS
            val roundNum = (currentHistory.maxOfOrNull { it.roundNumber } ?: 0) + 1
            currentHistory.add(RoundRecord(roundNum, p, c, r))
            lastP = p
            lastC = c
            lastR = r
        }

        val updatedStats = computeStats(currentHistory)
        _uiState.update {
            it.copy(
                playerChoice = lastP,
                computerChoice = lastC,
                currentResult = lastR,
                history = currentHistory,
                stats = updatedStats
            )
        }
    }

    fun resetStats() {
        _uiState.update {
            it.copy(
                playerChoice = null,
                computerChoice = null,
                currentResult = null,
                history = emptyList(),
                stats = GameStats()
            )
        }
    }

    fun toggleThemeMode() {
        _uiState.update {
            val nextMode = when (it.themeMode) {
                AppThemeMode.SYSTEM -> AppThemeMode.DARK
                AppThemeMode.DARK -> AppThemeMode.LIGHT
                AppThemeMode.LIGHT -> AppThemeMode.SYSTEM
            }
            it.copy(themeMode = nextMode)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setHistoryFilter(filter: HistoryFilter) {
        _uiState.update { it.copy(historyFilter = filter) }
    }

    fun setSelectedVizTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedVizTab = tabIndex) }
    }

    private fun computeStats(rounds: List<RoundRecord>): GameStats {
        if (rounds.isEmpty()) return GameStats()

        var wins = 0
        var losses = 0
        var ties = 0
        var pRock = 0
        var pPaper = 0
        var pScissors = 0
        var cRock = 0
        var cPaper = 0
        var cScissors = 0

        var currentStreak = 0
        var bestStreak = 0
        var runningStreak = 0

        for (record in rounds) {
            when (record.result) {
                GameResult.WIN -> {
                    wins++
                    if (runningStreak >= 0) runningStreak++ else runningStreak = 1
                    if (runningStreak > bestStreak) bestStreak = runningStreak
                }
                GameResult.LOSS -> {
                    losses++
                    if (runningStreak <= 0) runningStreak-- else runningStreak = -1
                }
                GameResult.TIE -> {
                    ties++
                    // Ties can maintain streak or reset
                }
            }

            when (record.playerChoice) {
                Move.ROCK -> pRock++
                Move.PAPER -> pPaper++
                Move.SCISSORS -> pScissors++
            }

            when (record.computerChoice) {
                Move.ROCK -> cRock++
                Move.PAPER -> cPaper++
                Move.SCISSORS -> cScissors++
            }
        }

        currentStreak = runningStreak

        return GameStats(
            totalRounds = rounds.size,
            wins = wins,
            losses = losses,
            ties = ties,
            currentStreak = currentStreak,
            bestWinStreak = bestStreak,
            playerRockCount = pRock,
            playerPaperCount = pPaper,
            playerScissorsCount = pScissors,
            computerRockCount = cRock,
            computerPaperCount = cPaper,
            computerScissorsCount = cScissors
        )
    }
}
