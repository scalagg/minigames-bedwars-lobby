package gg.scala.minigame.bedwars.lobby.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.minigame.bedwars.lobby.menu.cosmetic.BedWarsCosmeticsMenu
import gg.scala.minigame.bedwars.lobby.menu.settings.BedWarsSettingsMenu
import gg.scala.minigame.bedwars.lobby.menu.shop.BedWarsShopBrowserMenu
import gg.scala.minigame.bedwars.shared.BedWarsTypeProvider
import gg.tropic.practice.minigame.joinGame
import gg.tropic.practice.minigame.toConciseJoinButton
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
class BedWarsMainMenu : Menu("BedWars")
{
    companion object
    {
        fun quickJoin(player: Player)
        {
            player.sendMessage("${CC.GRAY}Finding a BedWars game for you to join...")
            BedWarsTypeProvider
                .provide()
                .computeGameTypeRequiringPlayers()
                .thenAccept { mode ->
                    mode.joinGame(player)
                }
        }
    }

    override fun size(buttons: Map<Int, Button>) = 27

    override fun getButtons(player: Player): MutableMap<Int, Button>
    {
        val modeSlots = linkedMapOf(
            "solo" to 10,
            "doubles" to 11,
            "trios" to 12,
            "4v4" to 14,
            "quads" to 15
        )

        val buttons = mutableMapOf<Int, Button>()
        modeSlots.forEach { (modeID, slot) ->
            val mode = BedWarsTypeProvider
                .provide()
                .modeNullable(modeID)
                ?: return@forEach

            buttons[slot] = mode
                .toConciseJoinItem()
                .toConciseJoinButton(modeID)
        }

        buttons[22] = ItemBuilder
            .of(XMaterial.COMPASS)
            .name("${CC.GREEN}Quick Join")
            .addToLore(
                "${CC.GRAY}Join the mode that currently",
                "${CC.GRAY}needs players the most.",
                "",
                "${CC.YELLOW}Click to join!"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                player.closeInventory()
                quickJoin(player)
            }

        buttons[13] = ItemBuilder
            .of(XMaterial.PLAYER_HEAD)
            .name("${CC.AQUA}My BedWars Profile")
            .owner(player.name)
            .addToLore(
                "${CC.GRAY}View your BedWars",
                "${CC.GRAY}performance and mode stats.",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                BedWarsProfileMenu().openMenu(player)
            }

        buttons[20] = ItemBuilder
            .of(XMaterial.CHEST)
            .name("${CC.AQUA}Shop Browser")
            .addToLore(
                "${CC.GRAY}Preview the BedWars",
                "${CC.GRAY}item shop categories.",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                BedWarsShopBrowserMenu().openMenu(player)
            }

        buttons[24] = ItemBuilder
            .of(XMaterial.BLAZE_POWDER)
            .name("${CC.AQUA}Bed Wars Settings")
            .addToLore(
                "${CC.GRAY}Configure BedWars preferences",
                "${CC.GRAY}like quick buy and hotbar layout.",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                BedWarsSettingsMenu().openMenu(player)
            }

        buttons[16] = ItemBuilder
            .of(Material.EMERALD)
            .name("${CC.AQUA}Cosmetics")
            .addToLore(
                "${CC.GRAY}Customize your BedWars",
                "${CC.GRAY}experience with cosmetics!",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                Button.playNeutral(player)
                BedWarsCosmeticsMenu().openMenu(player)
            }

        return buttons
    }
}
