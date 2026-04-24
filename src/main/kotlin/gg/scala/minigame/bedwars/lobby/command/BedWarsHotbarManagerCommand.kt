package gg.scala.minigame.bedwars.lobby.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.minigame.bedwars.lobby.menu.settings.BedWarsHotbarManagerMenu
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/16/2026
 */
@AutoRegister
object BedWarsHotbarManagerCommand : ScalaCommand()
{
    @CommandAlias("bedwarshotbar|bwhotbar")
    fun onHotbarManager(player: Player)
    {
        BedWarsHotbarManagerMenu().openMenu(player)
    }
}
