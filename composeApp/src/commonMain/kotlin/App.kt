import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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





            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Crossaint",
                    style = MaterialTheme.typography.h1,
                )
                 Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)


                ) {
                    Text("Reset Game")
                }

                gameState?.let { state ->
                    Text(
                        "Bank: ${currentMoney?.toHumanReadableString()} Gelds",
                        style = MaterialTheme.typography.h4,
                    )
                    Button(
                        onClick = { viewModel.clickMoney(state) }
                    ) {
                        Text("Click money")
                    }

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
            Text("Generator ${gameJob.id}" )
            Text("Level: ${gameJob.level.level}")
            Text(
                "Costs: ${gameJob.level.cost.toHumanReadableString()} Gelds", color = Color.White)
            Text("Earns: ${gameJob.level.earn.toHumanReadableString()} Gelds")
            Text("Duration: ${gameJob.level.duration.inWholeSeconds} Seconds")
        }
        if (!alreadyBought) {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black,
                    contentColor = Color.White)
            ) {
                Text("Buy")
            }
        } else {
            Text("Bought")
        }
        Button(
            onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black,
                contentColor = Color.White)
            ) {
            Text ("Upgrade")
        }
    }
}

// Function( argumente ) { Inhalt }