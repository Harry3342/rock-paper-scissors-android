package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameResult
import com.example.model.Move
import com.example.ui.theme.ColorLoss
import com.example.ui.theme.ColorPaper
import com.example.ui.theme.ColorRock
import com.example.ui.theme.ColorScissors
import com.example.ui.theme.ColorTie
import com.example.ui.theme.ColorWin

@Composable
fun RpsArenaCard(
    playerChoice: Move?,
    computerChoice: Move?,
    currentResult: GameResult?,
    isAnimating: Boolean,
    animationStep: String,
    onMoveSelected: (Move) -> Unit,
    onSimulateQuickMatches: () -> Unit,
    onResetStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rps_arena_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Battle Arena Display (Player vs Computer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Side
                FighterSlot(
                    label = "YOU",
                    move = playerChoice,
                    isThinking = isAnimating,
                    accentColor = MaterialTheme.colorScheme.primary,
                    testTag = "user-choice"
                )

                // VS / Status Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    if (isAnimating) {
                        Text(
                            text = animationStep,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("animating_status")
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Computer Side
                FighterSlot(
                    label = "COMPUTER",
                    move = computerChoice,
                    isThinking = isAnimating,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    testTag = "computer-choice"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Result Banner
            AnimatedContent(
                targetState = currentResult to isAnimating,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.92f))
                        .togetherWith(fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.95f))
                },
                label = "ResultBannerAnimation"
            ) { (result, animating) ->
                if (animating) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Choosing move...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                } else if (result != null) {
                    val (bannerBg, textColor, subtitle) = when (result) {
                        GameResult.WIN -> Triple(
                            ColorWin.copy(alpha = 0.15f),
                            ColorWin,
                            getWinningExplanation(playerChoice, computerChoice)
                        )
                        GameResult.LOSS -> Triple(
                            ColorLoss.copy(alpha = 0.15f),
                            ColorLoss,
                            getWinningExplanation(computerChoice, playerChoice)
                        )
                        GameResult.TIE -> Triple(
                            ColorTie.copy(alpha = 0.15f),
                            ColorTie,
                            "Both chose ${playerChoice?.displayName ?: "the same move"}"
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bannerBg)
                            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .testTag("result"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = result.displayText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = textColor
                            )
                            if (subtitle.isNotEmpty()) {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select Rock, Paper, or Scissors below to play!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Choice Buttons (Rock, Paper, Scissors)
            Text(
                text = "CHOOSE YOUR MOVE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MoveActionButton(
                    move = Move.ROCK,
                    color = ColorRock,
                    enabled = !isAnimating,
                    testTag = "rock",
                    onClick = { onMoveSelected(Move.ROCK) },
                    modifier = Modifier.weight(1f)
                )

                MoveActionButton(
                    move = Move.PAPER,
                    color = ColorPaper,
                    enabled = !isAnimating,
                    testTag = "paper",
                    onClick = { onMoveSelected(Move.PAPER) },
                    modifier = Modifier.weight(1f)
                )

                MoveActionButton(
                    move = Move.SCISSORS,
                    color = ColorScissors,
                    enabled = !isAnimating,
                    testTag = "scissors",
                    onClick = { onMoveSelected(Move.SCISSORS) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Simulation & Reset Toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSimulateQuickMatches,
                    enabled = !isAnimating,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_5_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Simulate +5",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                OutlinedButton(
                    onClick = onResetStats,
                    enabled = !isAnimating,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reset_stats_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reset Stats",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun FighterSlot(
    label: String,
    move: Move?,
    isThinking: Boolean,
    accentColor: Color,
    testTag: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ThinkingPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleTransition"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(76.dp)
                .scale(if (isThinking) pulseScale else 1f)
                .clip(CircleShape)
                .background(
                    if (move != null) accentColor.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
                .border(
                    width = 2.dp,
                    color = if (move != null) accentColor else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isThinking) {
                Text(
                    text = "❓",
                    fontSize = 32.sp
                )
            } else if (move != null) {
                Text(
                    text = move.emoji,
                    fontSize = 36.sp
                )
            } else {
                Text(
                    text = "—",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (isThinking) "Deciding..." else move?.displayName ?: "Ready",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MoveActionButton(
    move: Move,
    color: Color,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(76.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.18f),
            contentColor = color,
            disabledContainerColor = color.copy(alpha = 0.08f),
            disabledContentColor = color.copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = move.emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = move.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

private fun getWinningExplanation(winner: Move?, loser: Move?): String {
    if (winner == null || loser == null) return ""
    return when {
        winner == Move.ROCK && loser == Move.SCISSORS -> "Rock crushes Scissors"
        winner == Move.PAPER && loser == Move.ROCK -> "Paper covers Rock"
        winner == Move.SCISSORS && loser == Move.PAPER -> "Scissors cuts Paper"
        else -> ""
    }
}
