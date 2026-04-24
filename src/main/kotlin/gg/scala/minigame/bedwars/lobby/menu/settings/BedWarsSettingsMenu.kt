package gg.scala.minigame.bedwars.lobby.menu.settings

import gg.scala.minigame.bedwars.lobby.menu.BedWarsProfileMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/16/2026
 */
class BedWarsSettingsMenu : Menu("Bed Wars Settings")
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 54

    private fun staticSettingIcon(material: Material, name: String) = ItemBuilder
        .of(material)
        .name(name)
        .toButton()

    override fun getButtons(player: Player): MutableMap<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        buttons[10] = staticSettingIcon(Material.BOOK_AND_QUILL, "${CC.WHITE}Profile")
        buttons[11] = staticSettingIcon(Material.PAPER, "${CC.WHITE}Statistics")
        buttons[12] = staticSettingIcon(Material.IRON_FENCE, "${CC.WHITE}Gameplay")
        buttons[13] = staticSettingIcon(Material.NETHER_STAR, "${CC.WHITE}Quick Buy")
        buttons[14] = staticSettingIcon(Material.WEB, "${CC.WHITE}Social")
        buttons[15] = staticSettingIcon(Material.BLAZE_POWDER, "${CC.WHITE}Hotbar")
        buttons[16] = staticSettingIcon(Material.BED, "${CC.WHITE}Bed Wars")

        buttons[28] = ItemBuilder
            .of(Material.BLAZE_POWDER)
            .name("${CC.GREEN}Hotbar Manager")
            .addToLore(
                "${CC.GRAY}Edit preferred slots for your items",
                "${CC.GRAY}per category.",
                "",
                "${CC.YELLOW}Click to edit!"
            )
            .toButton { _, _ ->
                BedWarsHotbarManagerMenu().openMenu(player)
            }

        buttons[30] = ItemBuilder
            .of(Material.ARROW)
            .name("${CC.GREEN}Go Back")
            .addToLore("${CC.GRAY}To BedWars Profile")
            .toButton { _, _ ->
                BedWarsProfileMenu().openMenu(player)
            }

        return buttons
    }
}
