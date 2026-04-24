package gg.scala.minigame.bedwars.lobby.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.minigame.bedwars.lobby.menu.BedWarsProfileMenu
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/13/2026
 */
@AutoRegister
object BedWarsProfileCommand : ScalaCommand()
{
    @CommandAlias("bedwarsprofile|bwprofile")
    fun onProfile(player: Player)
    {
        BedWarsProfileMenu().openMenu(player)
    }
}
