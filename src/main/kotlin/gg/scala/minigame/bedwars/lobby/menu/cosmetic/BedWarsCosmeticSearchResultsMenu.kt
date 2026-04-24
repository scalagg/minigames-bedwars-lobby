package gg.scala.minigame.bedwars.lobby.menu.cosmetic

import gg.scala.minigame.bedwars.shared.cosmetic.BedWarsCosmeticGroupAdapter
import gg.tropic.practice.menu.cosmetic.TemplateCosmeticGroup
import gg.tropic.practice.menu.cosmetic.TemplateCosmeticSearchResultsMenu
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/21/2026
 */
class BedWarsCosmeticSearchResultsMenu(
    query: String,
    page: Int = 0
) : TemplateCosmeticSearchResultsMenu(query, page)
{
    override fun groups(): List<TemplateCosmeticGroup> = BedWarsCosmeticGroupAdapter.allGroups()

    override fun openSearchSign(player: Player, initialQuery: String, callback: (String) -> Unit)
    {
        BedWarsCosmeticSignInput.openSearch(player, initialQuery) { searchText ->
            callback(searchText)
        }
    }

    override fun openMainMenu(player: Player)
    {
        BedWarsCosmeticsMenu().openMenu(player)
    }

    override fun openSearchResults(player: Player, query: String, page: Int)
    {
        BedWarsCosmeticSearchResultsMenu(query, page).openMenu(player)
    }

    override fun openCategoryMenu(player: Player, group: TemplateCosmeticGroup, searchQuery: String)
    {
        BedWarsCosmeticCategoryMenu(
            group = group as BedWarsCosmeticGroupAdapter,
            searchQuery = searchQuery
        ).openMenu(player)
    }
}
