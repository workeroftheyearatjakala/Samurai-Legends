import com.ionspin.kotlin.bignum.decimal.times
import kotlinx.serialization.Serializable
import util.Gelds
import util.GeldsSerializer
import util.gelds
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class GameState(
    @Serializable(with = GeldsSerializer::class)
    internal val stashedMoney: Gelds,
    val workers: List<GameWorker>,
    val availableJobs: List<GameJob> = listOf(
        GameJob(1, Level(1, 10.gelds, 1.gelds, 1.seconds), title = "Katana"),
        GameJob(2, Level(1, 50.gelds, 10.gelds, 10.seconds), title = "Shuriken" ),
        GameJob(3, Level(1, 250.gelds, 50.gelds, 30.seconds), title= "Tonfa"),
        GameJob(4, Level(1, 500.gelds, 250.gelds, 60.seconds), title = "Dragon sword"),
        GameJob(5, Level(1, 1000.gelds, 500.gelds, 120.seconds), title = "Eclipse Scythe")
    ),
)

@Serializable
data class GameWorker(
    val jobId: Int,
    val createdAt: Long,
) {

    fun earnedWorker(job: GameJob, now: Long): Pair<Long, Gelds> {
        val collected = abs((now - createdAt) / job.level.duration.inWholeMilliseconds)
        return collected to collected * job.level.earn
    }
}

@Serializable
data class GameJob(
    val id: Int,
    val level: Level,
    var title: String
)

@Serializable
data class Level(
    val level: Int,
    @Serializable(with = GeldsSerializer::class)
    val cost: Gelds,
    @Serializable(with = GeldsSerializer::class)
    val earn: Gelds,
    val duration: Duration,
) {
    fun upgradeEfficiency() = copy(
        level = level + 1,
        earn = earn * 2,
        cost = cost *4

    )
}
