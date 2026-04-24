package gg.scala.minigame.bedwars.lobby.menu

import gg.scala.minigame.bedwars.lobby.BedWarsLobbyCustomizer
import gg.tropic.practice.profile.PracticeProfileService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/13/2026
 */
class BedWarsProfileMenu : Menu("Bed Wars Statistics")
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 45

    override fun getButtons(player: Player): MutableMap<Int, Button>
    {
        val profile = PracticeProfileService.find(player)
            ?: return mutableMapOf(
                22 to ItemBuilder
                    .of(Material.REDSTONE)
                    .name("${CC.RED}Your profile is still loading")
                    .addToLore("${CC.GRAY}Please try again in a moment.")
                    .toButton()
            )

        return BedWarsLobbyCustomizer
            .statisticsMenuProvider(profile)
            .toMutableMap()
    }
}
