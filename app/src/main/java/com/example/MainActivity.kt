package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppTopBar
import com.example.ui.components.DataVisualizationSection
import com.example.ui.components.KpiStatsRow
import com.example.ui.components.RoundHistorySection
import com.example.ui.components.RpsArenaCard
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(themeMode = uiState.themeMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        AppTopBar(
                            themeMode = uiState.themeMode,
                            onToggleTheme = { viewModel.toggleThemeMode() }
                        )
                    }
                ) { innerPadding ->
                    RpsAppScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun RpsAppScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // High-Level KPI Summary Cards
            KpiStatsRow(stats = uiState.stats)

            // Game Play Arena
            RpsArenaCard(
                playerChoice = uiState.playerChoice,
                computerChoice = uiState.computerChoice,
                currentResult = uiState.currentResult,
                isAnimating = uiState.isAnimating,
                animationStep = uiState.animationStep,
                onMoveSelected = { viewModel.play(it) },
                onSimulateQuickMatches = { viewModel.simulateRounds(5) },
                onResetStats = { viewModel.resetStats() }
            )

            // Rich Data Visualization Section (Donut, Trajectory, Moves, Matrix)
            DataVisualizationSection(
                stats = uiState.stats,
                history = uiState.history,
                selectedTab = uiState.selectedVizTab,
                onTabSelected = { viewModel.setSelectedVizTab(it) }
            )

            // Match History Section with Filters
            RoundHistorySection(
                history = uiState.history,
                currentFilter = uiState.historyFilter,
                onFilterChange = { viewModel.setHistoryFilter(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Retained for tests and preview compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
