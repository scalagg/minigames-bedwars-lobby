package gg.scala.minigame.bedwars.lobby.menu.shop

import gg.scala.minigame.bedwars.shared.shop.BedWarsQuickBuySlots
import gg.scala.minigame.bedwars.shared.shop.BedWarsShopCatalog
import gg.scala.minigame.bedwars.shared.shop.bedWarsQuickBuyLayoutResolved
import gg.scala.minigame.bedwars.shared.shop.setBedWarsQuickBuyLayout
import gg.scala.minigame.bedwars.shared.profile.BedWarsProfile
import gg.scala.minigame.bedwars.shared.profile.BedWarsProfileService
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
class BedWarsQuickBuyEditorMenu : Menu("Quick Buy Editor")
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 54

    private fun saveLayout(
        profile: BedWarsProfile,
        layout: MutableList<String>
    )
    {
        profile.setBedWarsQuickBuyLayout(layout)
        profile.save()
    }

    override fun getButtons(player: Player): MutableMap<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val profile = BedWarsProfileService.find(player)
        if (profile == null)
        {
            buttons[22] = ItemBuilder
                .of(Material.REDSTONE)
                .name("${CC.RED}Your profile is still loading")
                .addToLore(
                    "${CC.GRAY}Please wait a moment and try again."
                )
                .toButton()

            return buttons
        }

        val layout = profile.bedWarsQuickBuyLayoutResolved()
        BedWarsQuickBuySlots.menuSlots.forEachIndexed { index, slot ->
            val itemID = layout[index]
            val entry = BedWarsShopCatalog.byId[itemID]

            if (entry == null)
            {
                buttons[slot] = ItemBuilder
                    .of(Material.BARRIER)
                    .name("${CC.RED}Empty Slot")
                    .addToLore(
                        "${CC.GRAY}Left-Click to set item.",
                        "${CC.GRAY}Right-Click keeps it empty."
                    )
                    .toButton { _, click ->
                        if (click?.isRightClick == true)
                        {
                            openMenu(player)
                            return@toButton
                        }

                        BedWarsQuickBuySelectMenu(index).openMenu(player)
                    }

                return@forEachIndexed
            }

            buttons[slot] = ItemBuilder
                .of(entry.icon)
                .amount(entry.amount)
                .data(entry.data)
                .name("${CC.GREEN}${entry.displayName}")
                .addToLore(
                    "${CC.GRAY}Cost: ${entry.currency.color}${entry.cost} ${if (entry.cost == 1) entry.currency.displaySingular else entry.currency.displayPlural}",
                    "",
                    "${CC.YELLOW}Left-Click to replace",
                    "${CC.RED}Right-Click to clear"
                )
                .toButton { _, click ->
                    if (click!!.isRightClick)
                    {
                        layout[index] = BedWarsShopCatalog.EMPTY_SLOT_ID
                        saveLayout(profile, layout)
                        openMenu(player)
                        return@toButton
                    }

                    BedWarsQuickBuySelectMenu(index).openMenu(player)
                }
        }

        buttons[49] = ItemBuilder
            .of(Material.NETHER_STAR)
            .name("${CC.AQUA}Reset To Default")
            .addToLore(
                "${CC.GRAY}Restore the recommended",
                "${CC.GRAY}quick buy layout.",
                "",
                "${CC.YELLOW}Click to reset"
            )
            .toButton { _, _ ->
                saveLayout(profile, BedWarsShopCatalog.defaultQuickBuyLayout.toMutableList())
                openMenu(player)
            }

        buttons[50] = ItemBuilder
            .of(Material.CHEST)
            .name("${CC.AQUA}Open Shop Browser")
            .addToLore(
                "${CC.GRAY}Preview all purchasable items.",
                "",
                "${CC.YELLOW}Click to view"
            )
            .toButton { _, _ ->
                BedWarsShopBrowserMenu().openMenu(player)
            }

        return buttons
    }
}
