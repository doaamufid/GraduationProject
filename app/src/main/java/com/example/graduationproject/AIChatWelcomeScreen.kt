package com.example.graduationproject

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush as GraphicsBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

fun ComposeView.setWelcomeScreen(onBackClick: () -> Unit, onActionClick: (String) -> Unit) {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    setContent {
        AIChatWelcomeScreen(onBackClick = onBackClick, onActionClick = onActionClick)
    }
}

fun ComposeView.setBubblesOnly() {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    setContent {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BubbleCloud()
            FluidOrb()
            Text(
                text = "Hi, can I help you?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF4A3B69),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}

@Composable
fun AIChatWelcomeScreen(
    onBackClick: () -> Unit = {},
    onActionClick: (String) -> Unit = {}
) {
    val backgroundColor = GraphicsBrush.verticalGradient(
        colors = listOf(Color(0xFFF7F5FC), Color(0xFFEFEBF9))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF4A3B69),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI 1.3.2",
                            color = Color(0xFF4A3B69),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF4A3B69),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Central AI Greeting Orb & Bubble Cloud
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                BubbleCloud()
                FluidOrb()
                Text(
                    text = "Hi, can I help you?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFF4A3B69),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }

            // Quick Action Suggestion Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                QuickActionGrid(onActionClick)
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Input area
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(30.dp),
                color = Color.White.copy(alpha = 0.8f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* Add functionality */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF4A3B69).copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color(0xFF4A3B69)
                        )
                    }
                    
                    Text(
                        text = "Write here...",
                        color = Color(0xFF4A3B69).copy(alpha = 0.6f),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        fontSize = 14.sp
                    )
                    
                    IconButton(
                        onClick = { /* Send functionality */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF4A3B69), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FluidOrb() {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Box(
        modifier = Modifier
            .size(280.dp)
            .blur(50.dp)
            .drawBehind {
                val radius = size.minDimension / 2
                
                // Layer 1: Indigo
                drawCircle(
                    color = Color(0xFF9FA8DA).copy(alpha = 0.6f),
                    radius = radius * (1f + 0.1f * kotlin.math.sin(time.toDouble()).toFloat()),
                    center = center + androidx.compose.ui.geometry.Offset(
                        10f * kotlin.math.cos(time.toDouble()).toFloat(),
                        10f * kotlin.math.sin(time.toDouble()).toFloat()
                    )
                )
                
                // Layer 2: Magenta
                drawCircle(
                    color = Color(0xFFF48FB1).copy(alpha = 0.5f),
                    radius = radius * (0.9f + 0.1f * kotlin.math.cos(time.toDouble() * 0.7).toFloat()),
                    center = center + androidx.compose.ui.geometry.Offset(
                        -15f * kotlin.math.sin(time.toDouble() * 0.8).toFloat(),
                        15f * kotlin.math.cos(time.toDouble() * 0.8).toFloat()
                    )
                )
                
                // Layer 3: Sky Blue
                drawCircle(
                    color = Color(0xFF81D4FA).copy(alpha = 0.6f),
                    radius = radius * (0.8f + 0.15f * kotlin.math.sin(time.toDouble() * 1.2).toFloat()),
                    center = center + androidx.compose.ui.geometry.Offset(
                        20f * kotlin.math.cos(time.toDouble() * 0.5).toFloat(),
                        -20f * kotlin.math.sin(time.toDouble() * 0.5).toFloat()
                    )
                )
            }
    )
}

@Composable
fun BubbleCloud() {
    val bubbles = remember { List(25) { BubbleState() } }
    
    Box(modifier = Modifier.size(320.dp)) {
        bubbles.forEach { bubble ->
            FloatingBubble(bubble)
        }
    }
}

class BubbleState {
    val initialX = Random.nextFloat()
    val initialY = Random.nextFloat()
    val size = Random.nextInt(4, 12).dp
    val delay = Random.nextInt(0, 2000)
    val duration = Random.nextInt(3000, 6000)
}

@Composable
fun FloatingBubble(state: BubbleState) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubble")
    
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(state.duration, delayMillis = state.delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x"
    )

    val yOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(state.duration + 500, delayMillis = state.delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "y"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(state.duration, delayMillis = state.delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .offset(
                x = (state.initialX * 300).dp + xOffset.dp,
                y = (state.initialY * 300).dp + yOffset.dp
            )
            .size(state.size)
            .scale(scale)
            .clip(CircleShape)
            .background(Color(0xFFCE93D8).copy(alpha = 0.3f))
    )
}

@Composable
fun QuickActionGrid(onActionClick: (String) -> Unit) {
    val actions = listOf(
        ActionItem("Rewrite", Icons.Default.Star),
        ActionItem("Create an Image", Icons.Default.Create),
        ActionItem("Make a Plan", Icons.Default.DateRange),
        ActionItem("Analyse the Data", Icons.Default.List)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionChip(actions[0], Modifier.weight(1f), onActionClick)
            ActionChip(actions[1], Modifier.weight(1f), onActionClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionChip(actions[2], Modifier.weight(1f), onActionClick)
            ActionChip(actions[3], Modifier.weight(1f), onActionClick)
        }
    }
}

data class ActionItem(val title: String, val icon: ImageVector)

@Composable
fun ActionChip(item: ActionItem, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable {
                onClick(item.title)
            },
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF0EAF8),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Color(0xFF4A3B69),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.title,
                color = Color(0xFF4A3B69),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
