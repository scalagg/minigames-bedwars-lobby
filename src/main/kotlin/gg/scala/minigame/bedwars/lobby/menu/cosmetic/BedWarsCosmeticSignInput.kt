package gg.scala.minigame.bedwars.lobby.menu.cosmetic

import gg.scala.commons.sign.RaphaSignGUI
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Limo
 * @date 2/21/2026
 */
object BedWarsCosmeticSignInput
{
    private val signGUI by lazy {
        RaphaSignGUI(JavaPlugin.getProvidingPlugin(this::class.java))
    }

    fun open(player: Player, lines: Array<String>, action: (Array<String>) -> Unit)
    {
        signGUI.open(player, lines) { _, resultLines ->
            action(resultLines)
        }
    }

    fun openSearch(player: Player, initialQuery: String = "", action: (String) -> Unit)
    {
        open(
            player,
            arrayOf(
                initialQuery.take(15),
                "^^^^^^^^^^^^^^",
                "Enter your",
                "search query"
            )
        ) { lines ->
            action(parseQuery(lines))
        }
    }

    fun parseQuery(lines: Array<String>, maxLength: Int = 32): String
    {
        return (lines.firstOrNull() ?: "")
            .trim()
            .replace("""\s+""".toRegex(), " ")
            .take(maxLength)
    }
}
