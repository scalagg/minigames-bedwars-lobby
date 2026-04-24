package gg.scala.minigame.bedwars.lobby.menu.cosmetic

import gg.scala.game.extensions.profile.CorePlayerProfileService
import gg.scala.minigame.bedwars.shared.cosmetic.BedWarsCosmeticGroupAdapter
import gg.tropic.practice.menu.cosmetic.TemplateCosmeticGroup
import gg.tropic.practice.menu.cosmetic.TemplateCosmeticsMenu
import org.bukkit.entity.Player

/**
 * @author Limo
 * @date 2/20/2026
 */
class BedWarsCosmeticsMenu : TemplateCosmeticsMenu()
{
    override fun groups(): List<TemplateCosmeticGroup> = BedWarsCosmeticGroupAdapter.allGroups()

    override fun getBalance(player: Player): Long
    {
        return CorePlayerProfileService.find(player)?.getGameBalance("bedwars_coins") ?: 0L
    }

    override fun balanceLabel() = "Coins"
    override fun backLabel() = "To BedWars Menu"

    override fun openSearchSign(player: Player, callback: (String) -> Unit)
    {
        BedWarsCosmeticSignInput.openSearch(player) { searchText ->
            callback(searchText)
        }
    }

    override fun openSearchResults(player: Player, query: String)
    {
        BedWarsCosmeticSearchResultsMenu(query).openMenu(player)
    }

    override fun openSelf(player: Player)
    {
        BedWarsCosmeticsMenu().openMenu(player)
    }

    override fun onGroupClick(player: Player, group: TemplateCosmeticGroup)
    {
        BedWarsCosmeticCategoryMenu(group as BedWarsCosmeticGroupAdapter).openMenu(player)
    }
}
