package gg.scala.minigame.bedwars.lobby.menu.shop

import gg.scala.minigame.bedwars.shared.shop.BedWarsQuickBuySlots
import gg.scala.minigame.bedwars.shared.shop.BedWarsShopCatalog
import gg.scala.minigame.bedwars.shared.shop.BedWarsShopCategory
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/13/2026
 */
class BedWarsShopBrowserMenu(
    private val category: BedWarsShopCategory = BedWarsShopCategory.Blocks
) : Menu("BedWars Shop")
{
    init
    {
        placeholder = true
    }

    override fun size(buttons: Map<Int, Button>) = 54

    override fun getButtons(player: Player): MutableMap<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val categories = BedWarsShopCategory.entries
            .filterNot { entry -> entry == BedWarsShopCategory.QuickBuy }

        categories.forEachIndexed { index, entry ->
            val slot = 10 + index
            buttons[slot] = ItemBuilder
                .of(entry.icon)
                .name(
                    if (entry == category)
                        "${CC.GREEN}${entry.displayName} ${CC.D_GRAY}(Selected)"
                    else
                        "${CC.GREEN}${entry.displayName}"
                )
                .addToLore(
                    "${CC.GRAY}Browse ${entry.displayName.lowercase()} items.",
                    "",
                    "${CC.YELLOW}Click to view"
                )
                .toButton { _, _ ->
                    BedWarsShopBrowserMenu(entry).openMenu(player)
                }
        }

        BedWarsShopCatalog.categoryEntries(category)
            .take(BedWarsQuickBuySlots.menuSlots.size)
            .forEachIndexed { index, entry ->
                buttons[BedWarsQuickBuySlots.menuSlots[index]] = ItemBuilder
                    .of(entry.icon)
                    .amount(entry.amount)
                    .data(entry.data)
                    .name("${CC.GREEN}${entry.displayName}")
                    .addToLore(
                        "${CC.GRAY}Cost: ${entry.currency.color}${entry.cost} ${if (entry.cost == 1) entry.currency.displaySingular else entry.currency.displayPlural}",
                        "",
                        "${CC.D_GRAY}Preview only in lobby"
                    )
                    .toButton()
            }

        buttons[49] = ItemBuilder
            .of(org.bukkit.Material.NETHER_STAR)
            .name("${CC.AQUA}Edit Quick Buy")
            .addToLore(
                "${CC.GRAY}Customize your quick buy layout.",
                "",
                "${CC.YELLOW}Click to open"
            )
            .toButton { _, _ ->
                BedWarsQuickBuyEditorMenu().openMenu(player)
            }

        return buttons
    }
}
