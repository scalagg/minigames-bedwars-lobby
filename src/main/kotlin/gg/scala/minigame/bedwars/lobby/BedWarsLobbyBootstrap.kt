package gg.scala.minigame.bedwars.lobby

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.minigame.bedwars.shared.profile.BedWarsStatistics
import gg.scala.minigame.bedwars.shared.BedWarsTypeProvider
import gg.scala.minigame.bedwars.shared.rotation.RotationalScheduleService
import gg.tropic.practice.minigame.MinigameLobby

/**
 * @author Limo
 * @date 2/13/2026
 */
@Service
object BedWarsLobbyBootstrap
{
    @Configure
    fun configure()
    {
        BedWarsStatistics.ensureCustomStatisticsRegistered()
        MinigameLobby.customize(BedWarsTypeProvider, BedWarsLobbyCustomizer)
        RotationalScheduleService.subscribe()
    }
}
