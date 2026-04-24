package gg.scala.minigame.bedwars.lobby

import com.cryptomorin.xseries.XMaterial
import gg.scala.game.extensions.profile.CorePlayerProfileService
import gg.scala.minigame.bedwars.lobby.menu.BedWarsMainMenu
import gg.scala.minigame.bedwars.shared.BedWarsMode
import gg.scala.minigame.bedwars.shared.BedWarsTypeProvider
import gg.scala.minigame.bedwars.shared.profile.BedWarsProfileStatisticsService
import gg.scala.minigame.bedwars.shared.profile.BedWarsTrackedStatisticType
import gg.tropic.practice.extensions.toShortString
import gg.tropic.practice.menu.LeaderboardsMenu
import gg.tropic.practice.minigame.MinigameLobbyCustomizer
import gg.tropic.practice.minigame.MinigameLobbyScoreboardProvider
import gg.tropic.practice.profile.PracticeProfile
import gg.tropic.practice.profile.PracticeProfileService
import gg.tropic.practice.statistics.StatisticID
import gg.tropic.practice.statistics.StatisticLifetime
import gg.tropic.practice.statistics.TrackedKitStatistic
import gg.tropic.practice.statistics.statisticIdFrom
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/13/2026
 */
object BedWarsLobbyCustomizer : MinigameLobbyCustomizer
{
    override val id = "bedwars"

    private data class RankedStatistic(
        val value: Long,
        val rank: Long?
    )

    private val coreModes = listOf(
        BedWarsMode.SOLO,
        BedWarsMode.DOUBLES,
        BedWarsMode.TRIOS,
        BedWarsMode.QUADS
    )

    private val menuModes = listOf(
        BedWarsMode.DOUBLES to 21,
        BedWarsMode.TRIOS to 22,
        BedWarsMode.QUADS to 23,
        BedWarsMode.SOLO to 31
    )

    private val modeKitIds by lazy {
        BedWarsTypeProvider
            .provide()
            .gameModes
            .values
            .mapNotNull { metadata ->
                val mode = metadata.mode as? BedWarsMode
                    ?: return@mapNotNull null

                mode to metadata.kitID
            }
            .toMap()
    }

    private fun modeDisplay(mode: BedWarsMode) = if (mode == BedWarsMode.FOURS) "4v4"
        else mode.name.lowercase().replaceFirstChar { it.uppercaseChar() }

    private fun modeItemAmount(mode: BedWarsMode) = when (mode)
    {
        BedWarsMode.SOLO -> 1
        BedWarsMode.DOUBLES -> 2
        BedWarsMode.TRIOS -> 3
        BedWarsMode.QUADS,
        BedWarsMode.FOURS -> 4
    }

    private fun rankSuffix(rank: Long?): String
    {
        if (rank == null)
        {
            return " ${CC.GRAY}(#${CC.RED}-${CC.GRAY})"
        }

        return " ${CC.GRAY}(#${CC.GREEN}${Numbers.format(rank)}${CC.GRAY})"
    }

    private fun simpleLine(label: String, value: Long) = "${CC.GRAY}$label: ${CC.GREEN}${Numbers.format(value)}"

    private fun ratioLine(label: String, value: Double) = "${CC.GRAY}$label: ${CC.GREEN}${"%.2f".format(value)}"

    private fun leaderboardLine(label: String, statistic: RankedStatistic) =
        "${CC.GRAY}$label: ${CC.GREEN}${Numbers.format(statistic.value)}${rankSuffix(statistic.rank)}"

    private fun modeKit(mode: BedWarsMode) = modeKitIds[mode]
        ?: "bedwars"

    private fun PracticeProfile.readStatistic(id: StatisticID): RankedStatistic
    {
        val score = getStatisticValue(id)
        return RankedStatistic(
            value = score?.score?.toLong() ?: statistics()[id.toId()] ?: 0L,
            rank = score?.value
                ?.takeIf { it >= 0L }
                ?.plus(1L)
        )
    }

    private fun PracticeProfile.readStatistic(id: String): RankedStatistic
    {
        val score = getStatisticValue(id)
        return RankedStatistic(
            value = score?.score?.toLong() ?: statistics()[id] ?: 0L,
            rank = score?.value
                ?.takeIf { it >= 0L }
                ?.plus(1L)
        )
    }

    private fun PracticeProfile.tracked(
        statistic: TrackedKitStatistic,
        kitId: String,
        lifetime: StatisticLifetime? = null
    ): RankedStatistic
    {
        val id = statisticIdFrom(statistic) {
            kit(kitId)
            lifetime(lifetime)
        }

        return readStatistic(id)
    }

    private fun PracticeProfile.custom(
        mode: BedWarsMode?,
        type: BedWarsTrackedStatisticType
    ): RankedStatistic
    {
        val key = if (mode == null)
        {
            "bedwars:core:${type.key}"
        }
        else
        {
            "bedwars:mode:${mode.name.lowercase()}:${type.key}"
        }

        return readStatistic(StatisticID.fromCustom(key))
    }

    private fun PracticeProfile.customWindow(
        mode: BedWarsMode?,
        type: BedWarsTrackedStatisticType,
        lifetime: StatisticLifetime
    ): RankedStatistic
    {
        val lifetimeKey = lifetime.name.lowercase()
        val key = if (mode == null)
        {
            "custom:bedwars:core:${type.key}:$lifetimeKey"
        }
        else
        {
            "custom:bedwars:mode:${mode.name.lowercase()}:${type.key}:$lifetimeKey"
        }

        return readStatistic(key)
    }

    override fun mainMenuProvider(player: Player)
    {
        BedWarsMainMenu().openMenu(player)
    }

    override fun playProvider(player: Player)
    {
        BedWarsMainMenu.quickJoin(player)
    }

    override fun leaderboardsProvider(player: Player): Map<Int, Button> = mapOf(
        22 to ItemBuilder
            .of(XMaterial.NETHER_STAR)
            .name("${CC.GREEN}Global Leaderboards")
            .addToLore(
                "${CC.GRAY}View global leaderboards",
                "${CC.GRAY}across all game modes.",
                "",
                "${CC.YELLOW}Click to open!"
            )
            .toButton { _, _ ->
                LeaderboardsMenu(player).openMenu(player)
            }
    )

    override fun profileStatsNPCHeader(): List<String> = listOf(
        "${CC.GOLD}Your Bed Wars Profile"
    )

    override fun statisticsMenuProvider(profile: PracticeProfile): Map<Int, Button>
    {
        val snapshot = BedWarsProfileStatisticsService.snapshot(profile)

        val coreModeStats = coreModes.mapNotNull { mode ->
            snapshot.modeStatistics[mode]
        }

        val coreWins = coreModeStats.sumOf { mode -> mode.wins }
        val coreBedsBroken = coreModeStats.sumOf { mode -> mode.bedsBroken }
        val coreFinalKills = coreModeStats.sumOf { mode -> mode.finalKills }

        val overallWinsLifetime = profile.tracked(TrackedKitStatistic.Wins, "bedwars")
        val overallWinsMonthly = profile.tracked(TrackedKitStatistic.Wins, "bedwars", StatisticLifetime.Monthly)
        val overallWinsWeekly = profile.tracked(TrackedKitStatistic.Wins, "bedwars", StatisticLifetime.Weekly)
        val overallWinsDaily = profile.tracked(TrackedKitStatistic.Wins, "bedwars", StatisticLifetime.Daily)

        val overallBedsBrokenLifetime = profile.custom(null, BedWarsTrackedStatisticType.BedsBroken)
        val overallBedsBrokenMonthly = profile.customWindow(null, BedWarsTrackedStatisticType.BedsBroken, StatisticLifetime.Monthly)
        val overallBedsBrokenWeekly = profile.customWindow(null, BedWarsTrackedStatisticType.BedsBroken, StatisticLifetime.Weekly)
        val overallBedsBrokenDaily = profile.customWindow(null, BedWarsTrackedStatisticType.BedsBroken, StatisticLifetime.Daily)

        val overallFinalKillsLifetime = profile.custom(null, BedWarsTrackedStatisticType.FinalKills)
        val overallFinalKillsMonthly = profile.customWindow(null, BedWarsTrackedStatisticType.FinalKills, StatisticLifetime.Monthly)
        val overallFinalKillsWeekly = profile.customWindow(null, BedWarsTrackedStatisticType.FinalKills, StatisticLifetime.Weekly)
        val overallFinalKillsDaily = profile.customWindow(null, BedWarsTrackedStatisticType.FinalKills, StatisticLifetime.Daily)

        val buttons = mutableMapOf<Int, Button>()
        buttons[13] = ItemBuilder
            .of(XMaterial.PAPER)
            .name("${CC.GREEN}Overall Statistics")
            .addToLore(
                simpleLine("Games Played", snapshot.plays),
                "",
                simpleLine("Wins", snapshot.wins),
                simpleLine("Losses", snapshot.losses),
                "",
                simpleLine("Beds Broken", snapshot.bedBreaks),
                simpleLine("Beds Lost", snapshot.losses),
                "",
                simpleLine("Kills", snapshot.kills),
                simpleLine("Deaths", snapshot.deaths),
                simpleLine("Final Kills", snapshot.finalKills),
                simpleLine("Final Deaths", snapshot.finalDeaths),
                ratioLine("Final K/D Ratio", snapshot.fkdr),
                "",
                "${CC.GREEN}Core Mode Leaderboards",
                leaderboardLine("Lifetime Wins", RankedStatistic(coreWins, overallWinsLifetime.rank)),
                leaderboardLine("Monthly Wins", overallWinsMonthly),
                leaderboardLine("Weekly Wins", overallWinsWeekly),
                leaderboardLine("Daily Wins", overallWinsDaily),
                leaderboardLine("Lifetime Beds Broken", RankedStatistic(coreBedsBroken, overallBedsBrokenLifetime.rank)),
                leaderboardLine("Monthly Beds Broken", overallBedsBrokenMonthly),
                leaderboardLine("Weekly Beds Broken", overallBedsBrokenWeekly),
                leaderboardLine("Daily Beds Broken", overallBedsBrokenDaily),
                leaderboardLine("Lifetime Final Kills", RankedStatistic(coreFinalKills, overallFinalKillsLifetime.rank)),
                leaderboardLine("Monthly Final Kills", overallFinalKillsMonthly),
                leaderboardLine("Weekly Final Kills", overallFinalKillsWeekly),
                leaderboardLine("Daily Final Kills", overallFinalKillsDaily),
                "",
                simpleLine("Winstreak", snapshot.winStreak)
            )
            .toButton()

        menuModes.forEach { (mode, slot) ->
            val modeStats = snapshot.modeStatistics[mode]
                ?: return@forEach

            val kitId = modeKit(mode)

            val modeWinsLifetime = profile.tracked(TrackedKitStatistic.Wins, kitId)
            val modeWinsMonthly = profile.tracked(TrackedKitStatistic.Wins, kitId, StatisticLifetime.Monthly)
            val modeWinsWeekly = profile.tracked(TrackedKitStatistic.Wins, kitId, StatisticLifetime.Weekly)
            val modeWinsDaily = profile.tracked(TrackedKitStatistic.Wins, kitId, StatisticLifetime.Daily)

            val modeBedsBrokenLifetime = profile.custom(mode, BedWarsTrackedStatisticType.BedsBroken)
            val modeBedsBrokenMonthly = profile.customWindow(mode, BedWarsTrackedStatisticType.BedsBroken, StatisticLifetime.Monthly)
            val modeBedsBrokenWeekly = profile.customWindow(mode, BedWarsTrackedStatisticType.BedsBroken, StatisticLifetime.Weekly)
            val modeBedsBrokenDaily = profile.customWindow(mode, BedWarsTrackedStatisticType.BedsBroken, StatisticLifetime.Daily)

            val modeFinalKillsLifetime = profile.custom(mode, BedWarsTrackedStatisticType.FinalKills)
            val modeFinalKillsMonthly = profile.customWindow(mode, BedWarsTrackedStatisticType.FinalKills, StatisticLifetime.Monthly)
            val modeFinalKillsWeekly = profile.customWindow(mode, BedWarsTrackedStatisticType.FinalKills, StatisticLifetime.Weekly)
            val modeFinalKillsDaily = profile.customWindow(mode, BedWarsTrackedStatisticType.FinalKills, StatisticLifetime.Daily)

            val modeWinStreak = profile.tracked(TrackedKitStatistic.WinStreak, kitId)

            buttons[slot] = ItemBuilder
                .of(XMaterial.PAPER)
                .amount(modeItemAmount(mode))
                .name("${CC.GREEN}${modeDisplay(mode)} Statistics")
                .addToLore(
                    simpleLine("Games Played", modeStats.plays),
                    "",
                    leaderboardLine("Lifetime Wins", modeWinsLifetime),
                    leaderboardLine("Monthly Wins", modeWinsMonthly),
                    leaderboardLine("Weekly Wins", modeWinsWeekly),
                    leaderboardLine("Daily Wins", modeWinsDaily),
                    simpleLine("Losses", modeStats.losses),
                    "",
                    leaderboardLine("Lifetime Beds Broken", modeBedsBrokenLifetime),
                    leaderboardLine("Monthly Beds Broken", modeBedsBrokenMonthly),
                    leaderboardLine("Weekly Beds Broken", modeBedsBrokenWeekly),
                    leaderboardLine("Daily Beds Broken", modeBedsBrokenDaily),
                    simpleLine("Beds Lost", modeStats.losses),
                    "",
                    simpleLine("Kills", modeStats.kills),
                    simpleLine("Deaths", modeStats.deaths),
                    leaderboardLine("Lifetime Final Kills", modeFinalKillsLifetime),
                    leaderboardLine("Monthly Final Kills", modeFinalKillsMonthly),
                    leaderboardLine("Weekly Final Kills", modeFinalKillsWeekly),
                    leaderboardLine("Daily Final Kills", modeFinalKillsDaily),
                    simpleLine("Final Deaths", modeStats.finalDeaths),
                    ratioLine("Final K/D Ratio", modeStats.fkdr),
                    "",
                    simpleLine("Winstreak", modeWinStreak.value)
                )
                .toButton()
        }

        buttons[40] = ItemBuilder
            .of(XMaterial.BARRIER)
            .name("${CC.RED}Close")
            .toButton { clicked, _ ->
                clicked?.closeInventory()
            }

        return buttons
    }

    override fun holographicStatsProvider(player: Player): List<String>
    {
        val profile = PracticeProfileService.find(player)
            ?: return listOf(
                "${CC.GOLD}Your Bed Wars Profile",
                "${CC.GRAY}Loading profile...",
                "${CC.YELLOW}CLICK FOR STATS"
            )

        val snapshot = BedWarsProfileStatisticsService.snapshot(profile)

        val coreProfile = CorePlayerProfileService.find(player)
        val levelInfo = runCatching {
            coreProfile?.getLevelInfo("bedwars")
        }.getOrNull()

        val requiredXP = (levelInfo?.xpRequiredForNext?.toLong() ?: 1L)
            .coerceAtLeast(1L)
        val currentXP = (levelInfo?.currentXP?.toLong() ?: 0L)
            .coerceIn(0L, requiredXP)

        val levelDisplay = levelInfo?.formattedDisplay
            ?: "${CC.RED}${Numbers.format(coreProfile?.level ?: 1)}✫"
        val progressDisplay = "${CC.AQUA}${currentXP.toShortString().lowercase()}${CC.GRAY}/${CC.GREEN}${requiredXP.toShortString().lowercase()}"

        return listOf(
            "${CC.GOLD}Your Bed Wars Profile",
            "${CC.WHITE}Your Level: $levelDisplay",
            "${CC.WHITE}Progress: $progressDisplay",
            "${CC.WHITE}Total Wins: ${CC.GREEN}${Numbers.format(snapshot.wins)}",
            "${CC.WHITE}Current Winstreak: ${CC.GREEN}${Numbers.format(snapshot.winStreak)}",
            "${CC.YELLOW}CLICK FOR STATS"
        )
    }

    override fun scoreboard(): MinigameLobbyScoreboardProvider = BedWarsLobbyScoreboardProvider
}
