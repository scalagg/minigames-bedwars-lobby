package gg.scala.minigame.bedwars.lobby.menu.shop

import gg.scala.minigame.bedwars.shared.shop.BedWarsShopCatalog
import gg.scala.minigame.bedwars.shared.shop.BedWarsShopCategory
import gg.scala.minigame.bedwars.shared.shop.bedWarsQuickBuyLayoutResolved
import gg.scala.minigame.bedwars.shared.shop.setBedWarsQuickBuyLayout
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
class BedWarsQuickBuySelectMenu(
    private val quickBuySlot: Int,
    private val category: BedWarsShopCategory = BedWarsShopCategory.Blocks
) : Menu("Select Quick Buy Item")
{
    companion object
    {
        private val ITEM_SLOTS = intArrayOf(
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        )
    }

    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 54

    override fun getButtons(player: Player): MutableMap<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val profile = BedWarsProfileService.find(player)
        if (profile == null)
        {
            buttons[22] = ItemBuilder
                .of(Material.REDSTONE)
                .name("${CC.RED}Your profile is still loading")
                .toButton()
            return buttons
        }

        BedWarsShopCategory.entries
            .filterNot { entry -> entry == BedWarsShopCategory.QuickBuy }
            .forEachIndexed { index, entry ->
                buttons[10 + index] = ItemBuilder
                    .of(entry.icon)
                    .name(
                        if (entry == category)
                            "${CC.GREEN}${entry.displayName} ${CC.D_GRAY}(Selected)"
                        else
                            "${CC.GREEN}${entry.displayName}"
                    )
                    .addToLore(
                        "${CC.YELLOW}Click to view"
                    )
                    .toButton { _, _ ->
                        BedWarsQuickBuySelectMenu(quickBuySlot, entry).openMenu(player)
                    }
            }

        BedWarsShopCatalog.categoryEntries(category)
            .filter { entry -> entry.quickBuyEligible }
            .take(ITEM_SLOTS.size)
            .forEachIndexed { index, entry ->
                val slot = ITEM_SLOTS[index]
                buttons[slot] = ItemBuilder
                    .of(entry.icon)
                    .amount(entry.amount)
                    .data(entry.data)
                    .name("${CC.GREEN}${entry.displayName}")
                    .addToLore(
                        "${CC.GRAY}Cost: ${entry.currency.color}${entry.cost} ${if (entry.cost == 1) entry.currency.displaySingular else entry.currency.displayPlural}",
                        "",
                        "${CC.YELLOW}Click to place in quick buy slot ${quickBuySlot + 1}"
                    )
                    .toButton { _, _ ->
                        val layout = profile.bedWarsQuickBuyLayoutResolved()
                        layout[quickBuySlot] = entry.id
                        profile.setBedWarsQuickBuyLayout(layout)
                        profile.save()
                        BedWarsQuickBuyEditorMenu().openMenu(player)
                    }
            }

        buttons[49] = ItemBuilder
            .of(Material.BARRIER)
            .name("${CC.RED}Clear Slot")
            .addToLore(
                "${CC.GRAY}Set this quick buy slot to empty.",
                "",
                "${CC.YELLOW}Click to clear"
            )
            .toButton { _, _ ->
                val layout = profile.bedWarsQuickBuyLayoutResolved()
                layout[quickBuySlot] = BedWarsShopCatalog.EMPTY_SLOT_ID
                profile.setBedWarsQuickBuyLayout(layout)
                profile.save()
                BedWarsQuickBuyEditorMenu().openMenu(player)
            }

        buttons[50] = ItemBuilder
            .of(Material.ARROW)
            .name("${CC.YELLOW}Back")
            .toButton { _, _ ->
                BedWarsQuickBuyEditorMenu().openMenu(player)
            }

        return buttons
    }
}
