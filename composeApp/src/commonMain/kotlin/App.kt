import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import idle_game.composeapp.generated.resources.Bild
import idle_game.composeapp.generated.resources.Res
import idle_game.composeapp.generated.resources.Samuraibackground
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.toHumanReadableString
import vw.GameViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        Screen()
    }
}

@Composable
@Preview
fun Screen() {
    Scaffold(
        content = {
            val coroutineScope = rememberCoroutineScope()
            val viewModel by remember {
                mutableStateOf(
                    GameViewModel(
                        scope = coroutineScope,
                    )
                )
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState? by viewModel.gameState.collectAsState()
            val currentMoney: Gelds? by remember(gameState) {
                derivedStateOf { gameState?.stashedMoney }
            }


            Image(
                painterResource(Res.drawable.Samuraibackground),
                contentDescription = "A square",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )


            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Samurai Legends",
                        color = Color.White,
                        style = MaterialTheme.typography.h1,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.reset() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),


                    ) {
                        Text(
                            "Reset Game",
                            color = Color.White
                        )
                    }

                    gameState?.let { state ->
                        Text(
                            " ${currentMoney?.toHumanReadableString()} Damage count",
                            style = MaterialTheme.typography.h4,
                            color = Color.White
                        )

                        state.availableJobs.forEach { availableJob ->
                            Generator(
                                gameJob = availableJob,
                                alreadyBought = state.workers.any { it.jobId == availableJob.id },
                                onBuy = { viewModel.addWorker(state, availableJob) },
                                onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("")
                    gameState?.let { state ->
                        Button(
                            onClick = { viewModel.clickMoney(state) },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                            modifier = Modifier.offset(x = 400.dp, y = 200.dp)
                        ) {
                            Text(
                                "Click Enemies",
                                color = Color.White
                            )

                            // Hier Image
                            Image(
                                painterResource(Res.drawable.Bild),
                                contentDescription = "Samurai enemy",
                                modifier = Modifier.width(900.dp).height(700.dp),
                            )


                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(8.dp)
            .background(Color.Red, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text("${gameJob.title}")
            Text("Level: ${gameJob.level.level}")
            Text(
                "Costs: ${gameJob.level.cost.toHumanReadableString()} Damage", color = Color.Black
            )
            Text("Earns: ${gameJob.level.earn.toHumanReadableString()} Damage")
            Text("Duration: ${gameJob.level.duration.inWholeSeconds} Seconds")
        }
        if (!alreadyBought) {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Buy")
            }
        } else {
            Text("Bought")
        }
        Button(
            onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Upgrade")
            @Composable
            fun MinimalDialog(onDismissRequest: () -> Unit) {
                Dialog(onDismissRequest = { onDismissRequest() }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = "Click the opponents to gain damage, with damage you can buy and upgradenew weapons",
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

// Function( argumente ) { Inhalt }