package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameResult
import com.example.model.GameStats
import com.example.model.Move
import com.example.model.RoundRecord
import com.example.ui.theme.ColorLoss
import com.example.ui.theme.ColorPaper
import com.example.ui.theme.ColorRock
import com.example.ui.theme.ColorScissors
import com.example.ui.theme.ColorTie
import com.example.ui.theme.ColorWin
import kotlin.math.max

@Composable
fun KpiStatsRow(
    stats: GameStats,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KpiCard(
            title = "Win Rate",
            value = "${stats.winRatePercent.toInt()}%",
            subtitle = "${stats.wins}W - ${stats.losses}L",
            accentColor = ColorWin,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Streak",
            value = if (stats.currentStreak > 0) "+${stats.currentStreak}" else "${stats.currentStreak}",
            subtitle = if (stats.currentStreak > 0) "🔥 On Fire" else if (stats.currentStreak < 0) "❄️ Slump" else "Neutral",
            accentColor = if (stats.currentStreak > 0) ColorWin else if (stats.currentStreak < 0) ColorLoss else ColorTie,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Best Run",
            value = "${stats.bestWinStreak}",
            subtitle = "Consecutive",
            accentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Net Score",
            value = if (stats.netScore > 0) "+${stats.netScore}" else "${stats.netScore}",
            subtitle = "${stats.totalRounds} Rounds",
            accentColor = if (stats.netScore >= 0) ColorWin else ColorLoss,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = accentColor,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DataVisualizationSection(
    stats: GameStats,
    history: List<RoundRecord>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("data_visualization_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Game Analytics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time metrics & data visualization",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation tabs for visualizations
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .height(42.dp)
            ) {
                val tabs = listOf(
                    "Win Ratio" to Icons.Default.PieChart,
                    "Trajectory" to Icons.Default.AutoGraph,
                    "Move Frequency" to Icons.Default.Equalizer,
                    "Matchup Grid" to Icons.Default.TableChart
                )
                tabs.forEachIndexed { index, (label, icon) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        modifier = Modifier.testTag("viz_tab_$index")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab content
            when (selectedTab) {
                0 -> DonutWinRatioComponent(stats)
                1 -> TrajectoryLineChartComponent(history)
                2 -> MoveFrequencyComparisonComponent(stats)
                3 -> MatchupMatrixComponent(history)
            }
        }
    }
}

/**
 * 1. Donut Win Ratio Component
 */
@Composable
fun DonutWinRatioComponent(stats: GameStats) {
    if (stats.totalRounds == 0) {
        EmptyChartPlaceholder(message = "Play a round to view Win / Loss distribution")
        return
    }

    val winFraction = stats.wins.toFloat() / stats.totalRounds
    val lossFraction = stats.losses.toFloat() / stats.totalRounds
    val tieFraction = stats.ties.toFloat() / stats.totalRounds

    val animSweep = remember { Animatable(0f) }
    LaunchedEffect(stats.totalRounds) {
        animSweep.snapTo(0f)
        animSweep.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Canvas Donut
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(170.dp),
                contentAlignment = Alignment.Center
            ) {
                val winAngle = 360f * winFraction * animSweep.value
                val lossAngle = 360f * lossFraction * animSweep.value
                val tieAngle = 360f * tieFraction * animSweep.value

                Canvas(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(10.dp)
                ) {
                    val strokeWidth = 24.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Background track
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )

                    var currentAngle = -90f

                    if (winAngle > 0f) {
                        drawArc(
                            color = ColorWin,
                            startAngle = currentAngle,
                            sweepAngle = winAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        currentAngle += winAngle
                    }

                    if (lossAngle > 0f) {
                        drawArc(
                            color = ColorLoss,
                            startAngle = currentAngle,
                            sweepAngle = lossAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        currentAngle += lossAngle
                    }

                    if (tieAngle > 0f) {
                        drawArc(
                            color = ColorTie,
                            startAngle = currentAngle,
                            sweepAngle = tieAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                    }
                }

                // Center Win % Label
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${stats.winRatePercent.toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = ColorWin
                    )
                    Text(
                        text = "Win Rate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            // Legend breakdown
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DonutLegendItem(
                    label = "Wins",
                    count = stats.wins,
                    percent = stats.winRatePercent,
                    color = ColorWin
                )
                DonutLegendItem(
                    label = "Losses",
                    count = stats.losses,
                    percent = stats.lossRatePercent,
                    color = ColorLoss
                )
                DonutLegendItem(
                    label = "Ties",
                    count = stats.ties,
                    percent = stats.tieRatePercent,
                    color = ColorTie
                )
            }
        }
    }
}

@Composable
private fun DonutLegendItem(
    label: String,
    count: Int,
    percent: Float,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$count (${percent.toInt()}%)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * 2. Cumulative Score Trajectory Line Chart
 */
@Composable
fun TrajectoryLineChartComponent(history: List<RoundRecord>) {
    if (history.isEmpty()) {
        EmptyChartPlaceholder(message = "Play rounds to view the score trajectory over time")
        return
    }

    // Compute cumulative score points
    val cumulativeScores = remember(history) {
        var runningScore = 0
        val points = mutableListOf(0) // initial point at 0
        history.forEach {
            runningScore += it.result.scoreDelta
            points.add(runningScore)
        }
        points
    }

    val maxScore = cumulativeScores.maxOrNull() ?: 1
    val minScore = cumulativeScores.minOrNull() ?: 0
    val range = max(maxScore - minScore, 1)

    val currentNetScore = cumulativeScores.last()
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(history.size) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cumulative Score Trajectory (+1 Win, -1 Loss)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (currentNetScore >= 0) "+$currentNetScore" else "$currentNetScore",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (currentNetScore >= 0) ColorWin else ColorLoss
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .padding(12.dp)
        ) {
            val lineColor = if (currentNetScore >= 0) ColorWin else ColorLoss
            val fillBrush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.35f), lineColor.copy(alpha = 0.02f))
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val paddingY = 16f

                val usableHeight = height - (paddingY * 2)
                val totalPoints = cumulativeScores.size
                val stepX = width / max(totalPoints - 1, 1).toFloat()

                // Draw baseline 0
                val zeroRatio = if (range == 0) 0.5f else (maxScore - 0f) / range
                val zeroY = paddingY + (zeroRatio * usableHeight)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.35f),
                    start = Offset(0f, zeroY),
                    end = Offset(width, zeroY),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Build Line Path
                val linePath = Path()
                val fillPath = Path()

                val animatedPointCount = (totalPoints * animatedProgress.value).toInt().coerceAtLeast(1)

                cumulativeScores.take(animatedPointCount).forEachIndexed { index, score ->
                    val x = index * stepX
                    val ratio = (maxScore - score).toFloat() / range
                    val y = paddingY + (ratio * usableHeight)

                    if (index == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, zeroY)
                        fillPath.lineTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    // Last point marker
                    if (index == animatedPointCount - 1) {
                        drawCircle(
                            color = lineColor,
                            radius = 6f,
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3f,
                            center = Offset(x, y)
                        )
                    }
                }

                if (animatedPointCount > 0) {
                    val lastX = (animatedPointCount - 1) * stepX
                    fillPath.lineTo(lastX, zeroY)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = fillBrush
                    )
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Round 0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
            Text(
                text = "Round ${history.size}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * 3. Move Frequency Comparison Bar Chart (User vs Computer)
 */
@Composable
fun MoveFrequencyComparisonComponent(stats: GameStats) {
    if (stats.totalRounds == 0) {
        EmptyChartPlaceholder(message = "Play rounds to see your move preferences vs the computer")
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Player",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Computer",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                )
            }
        }

        MoveComparisonBar(
            moveName = "Rock ✊",
            userCount = stats.playerRockCount,
            compCount = stats.computerRockCount,
            total = stats.totalRounds,
            accentColor = ColorRock
        )

        MoveComparisonBar(
            moveName = "Paper ✋",
            userCount = stats.playerPaperCount,
            compCount = stats.computerPaperCount,
            total = stats.totalRounds,
            accentColor = ColorPaper
        )

        MoveComparisonBar(
            moveName = "Scissors ✌️",
            userCount = stats.playerScissorsCount,
            compCount = stats.computerScissorsCount,
            total = stats.totalRounds,
            accentColor = ColorScissors
        )
    }
}

@Composable
private fun MoveComparisonBar(
    moveName: String,
    userCount: Int,
    compCount: Int,
    total: Int,
    accentColor: Color
) {
    val userFraction = if (total > 0) userCount.toFloat() / total else 0f
    val compFraction = if (total > 0) compCount.toFloat() / total else 0f

    val animUser by animateFloatAsState(targetValue = userFraction, animationSpec = tween(500))
    val animComp by animateFloatAsState(targetValue = compFraction, animationSpec = tween(500))

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = moveName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "You: $userCount (${(userFraction * 100).toInt()}%) | CPU: $compCount (${(compFraction * 100).toInt()}%)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Dual stacked bars
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // User bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = animUser.coerceIn(0.02f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor)
                )
            }

            // Computer bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = animComp.coerceIn(0.02f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.4f))
                )
            }
        }
    }
}

/**
 * 4. Head-to-Head 3x3 Matchup Matrix Grid
 */
@Composable
fun MatchupMatrixComponent(history: List<RoundRecord>) {
    if (history.isEmpty()) {
        EmptyChartPlaceholder(message = "Play rounds to populate the Matchup Matrix")
        return
    }

    val moves = listOf(Move.ROCK, Move.PAPER, Move.SCISSORS)

    // Compute 3x3 counts
    val matrix = remember(history) {
        val map = mutableMapOf<Pair<Move, Move>, Int>()
        moves.forEach { p ->
            moves.forEach { c ->
                map[p to c] = 0
            }
        }
        history.forEach {
            val key = it.playerChoice to it.computerChoice
            map[key] = (map[key] ?: 0) + 1
        }
        map
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Your Move (Row) vs Computer Move (Column)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Matrix Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "YOU \\ CPU",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            moves.forEach { cMove ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cMove.emoji} ${cMove.displayName.take(3)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Matrix Rows
        moves.forEach { pMove ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Row Header
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "${pMove.emoji} ${pMove.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Columns
                moves.forEach { cMove ->
                    val count = matrix[pMove to cMove] ?: 0
                    val result = if (pMove == cMove) GameResult.TIE else if (pMove.beats(cMove)) GameResult.WIN else GameResult.LOSS

                    val cellColor = when (result) {
                        GameResult.WIN -> ColorWin.copy(alpha = if (count > 0) 0.25f else 0.08f)
                        GameResult.LOSS -> ColorLoss.copy(alpha = if (count > 0) 0.25f else 0.08f)
                        GameResult.TIE -> ColorTie.copy(alpha = if (count > 0) 0.25f else 0.08f)
                    }

                    val textColor = when (result) {
                        GameResult.WIN -> ColorWin
                        GameResult.LOSS -> ColorLoss
                        GameResult.TIE -> ColorTie
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(cellColor)
                            .border(
                                width = if (count > 0) 1.dp else 0.dp,
                                color = textColor.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = if (count > 0) textColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = result.shortCode,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = textColor.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyChartPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Equalizer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
