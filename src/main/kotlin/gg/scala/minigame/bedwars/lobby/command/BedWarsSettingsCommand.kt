package gg.scala.minigame.bedwars.lobby.command

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.minigame.bedwars.lobby.menu.settings.BedWarsSettingsMenu
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/16/2026
 */
@AutoRegister
object BedWarsSettingsCommand : ScalaCommand()
{
    @CommandAlias("bedwarssettings|bwsettings")
    fun onSettings(player: Player)
    {
        BedWarsSettingsMenu().openMenu(player)
    }
}
