package gg.scala.minigame.bedwars.lobby

import gg.scala.basics.plugin.profile.BasicsProfileService
import gg.scala.game.extensions.profile.CorePlayerProfileService
import gg.scala.minigame.bedwars.lobby.scoreboard.WaveAnimationTitle
import gg.scala.minigame.bedwars.shared.profile.BedWarsProfileStatisticsService
import gg.tropic.practice.extensions.toShortString
import gg.tropic.practice.minigame.MinigameLobbyScoreboardProvider
import gg.tropic.practice.player.LobbyPlayer
import gg.tropic.practice.profile.PracticeProfileService
import gg.tropic.practice.settings.DuelsSettingCategory
import gg.tropic.practice.settings.scoreboard.LobbyScoreboardView
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/13/2026
 */
object BedWarsLobbyScoreboardProvider : MinigameLobbyScoreboardProvider
{
    override fun provideTitle() = WaveAnimationTitle.getCurrentTitle()

    private fun styledProgressBar(progress: Long, total: Long): String
    {
        val segments = 10
        val safeTotal = total.coerceAtLeast(1L)
        val ratio = (progress.toDouble() / safeTotal.toDouble())
            .coerceIn(0.0, 1.0)
        val complete = (segments * ratio).toInt()
            .coerceIn(0, segments)
        val remaining = (segments - complete)
            .coerceAtLeast(0)

        return "${CC.D_GRAY}[${CC.AQUA}${"■".repeat(complete)}${CC.GRAY}${"■".repeat(remaining)}${CC.D_GRAY}]"
    }

    override fun provideIdleLines(player: Player, lobbyProfile: LobbyPlayer): List<String>
    {
        val coreProfile = CorePlayerProfileService.find(player)
        val levelInfo = runCatching {
            coreProfile?.getLevelInfo("bedwars")
        }.getOrNull()

        val requiredXP = (levelInfo?.xpRequiredForNext?.toLong() ?: 1L)
            .coerceAtLeast(1L)
        val currentXP = (levelInfo?.currentXP?.toLong() ?: 0L)
            .coerceIn(0L, requiredXP)

        val levelDisplay = levelInfo
            ?.formattedDisplay
            ?: "${CC.L_PURPLE}${Numbers.format(coreProfile?.level ?: 1)}✫"
        val progressDisplay = "${CC.AQUA}${currentXP.toShortString().lowercase()}${CC.GRAY}/${CC.GREEN}${requiredXP.toShortString().lowercase()}"

        val practiceProfile = PracticeProfileService.find(player)
        val snapshot = practiceProfile?.let(BedWarsProfileStatisticsService::snapshot)

        val bedwarsCoins = coreProfile?.getGameBalance("bedwars_coins") ?: 0L
        val totalKills = snapshot?.kills ?: 0L
        val totalWins = snapshot?.wins ?: 0L

        BasicsProfileService.find(player)
            ?.apply {
                val scoreboardView = setting(
                    "${DuelsSettingCategory.DUEL_SETTING_PREFIX}:lobby-scoreboard-view",
                    LobbyScoreboardView.None
                )

                if (scoreboardView != LobbyScoreboardView.None)
                {
                    return listOf(
                        "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Level: $levelDisplay",
                        "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Progress: $progressDisplay",
                        styledProgressBar(currentXP, requiredXP),
                        "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Coins: ${CC.GOLD}${
                            Numbers.format(
                                bedwarsCoins
                            )
                        }",
                        "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Total Wins: ${CC.GREEN}${
                            Numbers.format(
                                totalWins
                            )
                        }"
                    )
                }
            }

        return listOf(
            "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Level: $levelDisplay",
            "",
            "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Progress: $progressDisplay",
            styledProgressBar(currentXP, requiredXP),
            "",
            "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Coins: ${CC.GOLD}${Numbers.format(bedwarsCoins)}",
            "",
            "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Total Kills: ${CC.GREEN}${Numbers.format(totalKills)}",
            "${CC.GRAY}${Constants.THIN_VERTICAL_LINE} ${CC.WHITE}Total Wins: ${CC.GREEN}${Numbers.format(totalWins)}"
        )
    }
}
