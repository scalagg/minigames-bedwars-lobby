package gg.scala.minigame.bedwars.lobby.menu.cosmetic

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import gg.scala.game.extensions.profile.CorePlayerProfileService
import gg.scala.minigame.bedwars.shared.cosmetic.BedWarsCosmeticGroupAdapter
import gg.scala.minigame.bedwars.shared.cosmetic.BedWarsCosmeticItemAdapter
import gg.scala.minigame.bedwars.shared.cosmetic.CosmeticCategory
import gg.scala.minigame.bedwars.shared.cosmetic.CosmeticEntry
import gg.scala.minigame.bedwars.shared.cosmetic.CosmeticRarity
import gg.scala.minigame.bedwars.shared.cosmetic.ShopKeeperSkinType
import net.evilblock.cubed.util.math.Numbers
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Effect
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity
import org.bukkit.entity.*
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import gg.tropic.practice.menu.cosmetic.TemplateCosmeticCategoryMenu
import gg.tropic.practice.menu.cosmetic.TemplateCosmeticGroup
import gg.tropic.practice.menu.cosmetic.TemplateCosmeticItem

/**
 * @author Limo
 * @date 2/20/2026
 */
class BedWarsCosmeticCategoryMenu(
    group: BedWarsCosmeticGroupAdapter,
    page: Int = 0,
    searchQuery: String = "",
    sortMode: SortMode = SortMode.LOWEST_RARITY_FIRST,
    ownedItemsFirst: Boolean = false
) : TemplateCosmeticCategoryMenu(group, page, searchQuery, sortMode, ownedItemsFirst)
{
    companion object
    {
        private fun setNoAI(entity: org.bukkit.entity.LivingEntity)
        {
            val nmsEntity = (entity as CraftLivingEntity).handle
            val tag = NBTTagCompound()
            nmsEntity.c(tag)
            tag.setInt("NoAI", 1)
            nmsEntity.f(tag)
        }

        private const val DERPERINO_SKIN_VALUE =
            "ewogICJ0aW1lc3RhbXAiIDogMTY4NDU1ODYwMzIyNywKICAicHJvZmlsZUlkIiA6ICIxZjEyNTNhYTVkYTA0Y2NlYTQ1ZjJlYmRiNDc1ODVkMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJEZXJwZXJpbm8iLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE0YWY3MTg0NTVkNGFhYjUyOGU3YTYxZjg2ZmEyNWU2YTM2OWQxNzY4ZGNiMTNmN2RmMzE5YTcxM2ViODEwYiIKICAgIH0KICB9Cn0="
        private const val DERPERINO_SKIN_SIGNATURE =
            "nZfFACpn+NZOK3rlJkJFkoJByMAhTEPOIGlBMGOBg2PQPB+LSCfCNwCPx7dB+b/MvMHY/Bfczlr+/HfYBhWIE5mKBkqVOz+FnQzMBYG+J7eMBPi0ad3iGJILg30GjGvGcQFJMli/c7H8DzpWHPwhER4PLFAChSR8k0LS1c7/C8N0bVSmGf6a0iKDy8yEpkZfnJMdVR0KnJJZgcYbv/RFPA/+ELOB4kl+M4hLITqbJpahicBiRiD0ikIZTENziBqZhm/MzKg1Ynpbqc8bFCpNOYsDE6XGBJFeb+PR7M1UHTH+GhRnX4TjMLPNvi0xnE/scp0Z/UGMFhHvmKe8gt/MKe/CBuvCqHh7AjCBfr0H5Sq6WHY1bey8SWZbfiBe5TpGVT+cOqCpbr/k4wZChfSw3FbAc7GNwO5TcB1NYNsbi0ba6MQkDfLYhPd45MNEkGJ+lno4Gy/Bto+O5VoNLJZFHBPREfXm5E1rfgjFwcaF0UoI/G2EjQHFf1E/XRbFkCIFVdQDbQ7f6sFVjqasfPGBvMELjHDJBHymiW98Bq5PlfeSqHFe/L2xJFGzLCODTlNs0RNMFMFg38OAAH/GE90EvCxNEGGFBkjFiJMVQ8j8OlPBPXlnXGyvAGz3JZLNrkZiDlS3sMdG+C7VJIo2GzRLaDjKB3A2IEFM0pWRwMlpFs="
    }

    private fun bedwarsGroup() = group() as BedWarsCosmeticGroupAdapter
    private fun category() = bedwarsGroup().category()

    override fun getBalance(player: Player): Long
    {
        return CorePlayerProfileService.find(player)?.getGameBalance("bedwars_coins") ?: 0L
    }

    override fun balanceLabel() = "Coins"

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

    override fun openSelf(
        player: Player,
        newPage: Int,
        newSearchQuery: String,
        newSortMode: SortMode,
        newOwnedItemsFirst: Boolean
    )
    {
        BedWarsCosmeticCategoryMenu(
            group = bedwarsGroup(),
            page = newPage,
            searchQuery = newSearchQuery,
            sortMode = newSortMode,
            ownedItemsFirst = newOwnedItemsFirst
        ).openMenu(player)
    }

    override fun attemptPurchase(
        player: Player,
        group: TemplateCosmeticGroup,
        item: TemplateCosmeticItem,
        cost: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    )
    {
        val coreProfile = CorePlayerProfileService.find(player)
        if (coreProfile == null)
        {
            onFailure("${CC.RED}Could not load your profile. Try again later.")
            return
        }

        val balance = coreProfile.getGameBalance("bedwars_coins")

        if (balance < cost)
        {
            onFailure(
                "${CC.RED}You need ${CC.GOLD}${Numbers.format(cost)} coins${CC.RED} to purchase this cosmetic! " +
                "(You have ${CC.GOLD}${Numbers.format(balance)}${CC.RED})"
            )
            return
        }

        if (!coreProfile.removeGameBalance("bedwars_coins", cost))
        {
            onFailure("${CC.RED}Insufficient coins!")
            return
        }

        coreProfile.save()
        onSuccess()
    }

    override fun previewItem(player: Player, group: TemplateCosmeticGroup, item: TemplateCosmeticItem)
    {
        val adapter = item as? BedWarsCosmeticItemAdapter ?: return
        val entry = adapter.entry()
        previewCosmetic(player, entry)
    }

    private fun previewCosmetic(player: Player, entry: CosmeticEntry)
    {
        player.closeInventory()
        player.sendMessage("${CC.GRAY}Playing preview: ${CC.WHITE}${entry.displayName}")

        when (category())
        {
            CosmeticCategory.DEATH_CRIES -> previewDeathCry(player, entry)
            CosmeticCategory.KILL_MESSAGES -> previewKillMessage(player, entry)
            CosmeticCategory.PROJECTILE_TRAILS -> previewProjectileTrail(player, entry)
            CosmeticCategory.FINAL_KILL_EFFECTS -> previewFinalKillEffect(player, entry)
            CosmeticCategory.BED_BREAK_EFFECTS -> previewBedBreakEffect(player, entry)
            CosmeticCategory.VICTORY_DANCES -> previewVictoryDance(player, entry)
            CosmeticCategory.GLYPHS -> previewGlyph(player, entry)
            CosmeticCategory.SHOPKEEPER_SKINS -> previewShopKeeperSkin(player, entry)
            else -> {}
        }
    }

    private fun startCameraPreview(
        player: Player,
        previewLocation: Location,
        endDelay: Long = 100L,
        onStart: (ArmorStand) -> Unit,
        onEnd: (() -> Unit)? = null
    )
    {
        val plugin = JavaPlugin.getProvidingPlugin(this::class.java)
        val originalLocation = player.location.clone()
        val savedInventory = player.inventory.contents.clone()
        val savedArmor = player.inventory.armorContents.clone()

        for (online in Bukkit.getOnlinePlayers())
        {
            if (online != player)
            {
                online.hidePlayer(player)
            }
        }

        player.teleport(previewLocation)

        player.inventory.clear()
        player.inventory.armorContents = arrayOf(null, null, null, null)

        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, endDelay.toInt() + 20, 2, false, false))

        val armorStand = player.world.spawn(previewLocation, ArmorStand::class.java)
        armorStand.isVisible = false
        armorStand.setGravity(false)
        armorStand.isSmall = true

        try
        {
            val protocolManager = ProtocolLibrary.getProtocolManager()
            val cameraPacket = protocolManager.createPacket(PacketType.Play.Server.CAMERA)
            cameraPacket.integers.write(0, armorStand.entityId)
            protocolManager.sendServerPacket(player, cameraPacket)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        onStart(armorStand)

        Bukkit.getScheduler().runTaskLater(plugin, {
            try
            {
                val protocolManager = ProtocolLibrary.getProtocolManager()
                val resetPacket = protocolManager.createPacket(PacketType.Play.Server.CAMERA)
                resetPacket.integers.write(0, player.entityId)
                protocolManager.sendServerPacket(player, resetPacket)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            player.removePotionEffect(PotionEffectType.INVISIBILITY)

            for (online in Bukkit.getOnlinePlayers())
            {
                if (online != player)
                {
                    online.showPlayer(player)
                }
            }

            player.inventory.contents = savedInventory
            player.inventory.armorContents = savedArmor

            player.teleport(originalLocation)

            armorStand.remove()

            onEnd?.invoke()

            Bukkit.getScheduler().runTaskLater(plugin, {
                if (player.isOnline)
                {
                    openSelf(player, page())
                }
            }, 5L)
        }, endDelay)
    }

    private fun previewDeathCry(player: Player, entry: CosmeticEntry)
    {
        val sound = when (entry.id)
        {
            "bazinga" -> Sound.BLAZE_DEATH
            "enderman" -> Sound.ENDERMAN_DEATH
            "pig" -> Sound.PIG_DEATH
            "bat" -> Sound.BAT_DEATH
            "skeleton" -> Sound.SKELETON_DEATH
            "wolf" -> Sound.WOLF_HOWL
            "burp" -> Sound.BURP
            "firework" -> Sound.FIREWORK_LAUNCH
            "splash" -> Sound.SPLASH
            "sad-puppy" -> Sound.WOLF_DEATH
            "sad-moo" -> Sound.COW_IDLE
            "plop" -> Sound.CHICKEN_EGG_POP
            "ding" -> Sound.SUCCESSFUL_HIT
            "fireball" -> Sound.GHAST_FIREBALL
            "explode" -> Sound.EXPLODE
            "zombie" -> Sound.ENDERMAN_SCREAM
            "breath" -> Sound.BLAZE_BREATH
            "click" -> Sound.CLICK
            "creepy" -> Sound.AMBIENCE_CAVE
            else -> null
        }
        if (sound != null)
        {
            player.playSound(player.location, sound, 1.0f, 1.0f)
        }
    }

    private fun previewKillMessage(player: Player, entry: CosmeticEntry)
    {
        val sampleMessage = gg.scala.minigame.bedwars.shared.cosmetic.KillMessageType.formatKillMessage(
            killMessageId = entry.id,
            killerName = player.name,
            victimName = "Steve",
            killerColor = "${CC.GREEN}",
            victimColor = "${CC.RED}"
        )
        if (sampleMessage != null)
        {
            player.sendMessage("")
            player.sendMessage("${CC.GRAY}${CC.STRIKE_THROUGH}-------------------")
            player.sendMessage("${CC.YELLOW}${CC.BOLD}Kill Message Preview")
            player.sendMessage("")
            player.sendMessage(sampleMessage)
            player.sendMessage("")
            player.sendMessage("${CC.GRAY}${CC.STRIKE_THROUGH}-------------------")
        }
    }

    private fun previewProjectileTrail(player: Player, entry: CosmeticEntry)
    {
        val loc = player.location.clone().add(
            player.location.direction.multiply(2.0)
        ).add(0.0, 1.5, 0.0)
        val world = player.world
        val effect = when (entry.id)
        {
            "slime" -> Effect.SLIME
            "ender" -> Effect.PORTAL
            "lava" -> Effect.LAVA_POP
            "potion" -> Effect.POTION_SWIRL
            "water" -> Effect.WATERDRIP
            "black-smoke" -> Effect.LARGE_SMOKE
            "white-smoke" -> Effect.SMOKE
            "note" -> Effect.NOTE
            "blue-dust" -> Effect.COLOURED_DUST
            "red-dust" -> Effect.COLOURED_DUST
            "purple-dust" -> Effect.COLOURED_DUST
            "fire" -> Effect.MOBSPAWNER_FLAMES
            "magic" -> Effect.MAGIC_CRIT
            "firework" -> Effect.FIREWORKS_SPARK
            "angry-villager" -> Effect.VILLAGER_THUNDERCLOUD
            "happy-villager" -> Effect.HAPPY_VILLAGER
            "portal" -> Effect.PORTAL
            else -> null
        }
        if (effect != null)
        {
            for (i in 0 until 10)
            {
                val offset = loc.clone().add(
                    (Math.random() - 0.5) * 1.5,
                    (Math.random() - 0.5) * 1.5,
                    (Math.random() - 0.5) * 1.5
                )
                world.playEffect(offset, effect, 0)
            }
        }
    }

    private fun previewFinalKillEffect(player: Player, entry: CosmeticEntry)
    {
        val plugin = JavaPlugin.getProvidingPlugin(this::class.java)
        val previewLoc = player.location.clone()
        val world = player.world

        val cameraLoc = previewLoc.clone().add(
            previewLoc.direction.multiply(-4.0)
        ).add(0.0, 2.0, 0.0)

        val entitiesToClean = mutableListOf<Entity>()
        val npcsToClean = mutableListOf<NpcEntity>()

        startCameraPreview(player, cameraLoc, endDelay = 100L,
            onStart = { _ ->
                val victimLoc = previewLoc.clone()
                val victimNpc = object : NpcEntity(listOf("${CC.RED}Victim"), victimLoc)
                {
                    override fun onRightClick(player: Player) {}
                }
                victimNpc.persistent = false
                victimNpc.initializeData()
                EntityHandler.trackEntity(victimNpc)
                npcsToClean.add(victimNpc)

                val killerLoc = previewLoc.clone().add(previewLoc.direction.multiply(3.0))
                killerLoc.y = previewLoc.y
                val killerNpc = object : NpcEntity(listOf("${CC.GREEN}Killer"), killerLoc)
                {
                    override fun onRightClick(player: Player) {}
                }
                killerNpc.persistent = false
                killerNpc.initializeData()
                killerNpc.updateTexture(DERPERINO_SKIN_VALUE, DERPERINO_SKIN_SIGNATURE)
                EntityHandler.trackEntity(killerNpc)
                npcsToClean.add(killerNpc)

                Bukkit.getScheduler().runTaskLater(plugin, {
                    killerNpc.destroyForCurrentWatchers()
                    EntityHandler.forgetEntity(killerNpc)

                    executeFinalKillEffect(entry.id, victimLoc, world, plugin, entitiesToClean)
                }, 22L)
            },
            onEnd = {
                for (e in entitiesToClean)
                {
                    if (e.isValid) e.remove()
                }
                for (npc in npcsToClean)
                {
                    npc.destroyForCurrentWatchers()
                    EntityHandler.forgetEntity(npc)
                }
            }
        )
    }

    private fun executeFinalKillEffect(
        id: String,
        loc: Location,
        world: org.bukkit.World,
        plugin: org.bukkit.plugin.Plugin,
        tracked: MutableList<Entity>
    )
    {
        when (id)
        {
            "lightning_strike" ->
            {
                world.strikeLightningEffect(loc)
            }

            "firework" ->
            {
                val fw = world.spawn(loc.clone().add(0.0, 1.0, 0.0), Firework::class.java)
                val meta = fw.fireworkMeta
                meta.addEffect(
                    FireworkEffect.builder()
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .withColor(Color.RED, Color.YELLOW)
                        .withFade(Color.ORANGE)
                        .flicker(true)
                        .build()
                )
                meta.power = 0
                fw.fireworkMeta = meta
                fw.detonate()
            }

            "heart_aura" ->
            {
                val center = loc.clone().add(0.0, 1.5, 0.0)
                for (i in 0 until 20)
                {
                    val offset = center.clone().add(
                        (Math.random() - 0.5) * 2.0,
                        Math.random() * 2.0,
                        (Math.random() - 0.5) * 2.0
                    )
                    world.playEffect(offset, Effect.HEART, 0)
                }
            }

            "tornado" ->
            {
                var tick = 0
                Bukkit.getScheduler().runTaskTimer(plugin, {
                    if (tick >= 20) return@runTaskTimer
                    val radius = 1.0 + (tick * 0.1)
                    val y = tick * 0.15
                    for (angle in 0 until 360 step 30)
                    {
                        val rad = Math.toRadians(angle.toDouble())
                        val x = Math.cos(rad) * radius
                        val z = Math.sin(rad) * radius
                        world.playEffect(loc.clone().add(x, y, z), Effect.CLOUD, 0)
                    }
                    tick++
                }, 0L, 2L).let { task ->
                    Bukkit.getScheduler().runTaskLater(plugin, { task.cancel() }, 42L)
                }
            }

            "bat_crux" ->
            {
                for (i in 0 until 5)
                {
                    val bat = world.spawn(
                        loc.clone().add(
                            (Math.random() - 0.5) * 3.0,
                            1.0 + Math.random() * 2.0,
                            (Math.random() - 0.5) * 3.0
                        ),
                        Bat::class.java
                    )
                    setNoAI(bat)
                    tracked.add(bat)
                }
            }

            "burning_shoes" ->
            {
                val stand = world.spawn(loc, ArmorStand::class.java)
                stand.isVisible = false
                stand.setGravity(false)
                stand.setArms(true)
                stand.boots = ItemBuilder.of(Material.LEATHER_BOOTS).build()
                stand.fireTicks = 100
                tracked.add(stand)
            }

            "rekt" ->
            {
                val center = loc.clone().add(0.0, 2.5, 0.0)
                val stand = world.spawn(center, ArmorStand::class.java)
                stand.isVisible = false
                stand.setGravity(false)
                stand.isCustomNameVisible = true
                stand.customName = "${CC.RED}${CC.BOLD}R E K T"
                stand.isSmall = true
                tracked.add(stand)
            }

            "squid_missile" ->
            {
                val squid = world.spawn(loc.clone().add(0.0, 1.0, 0.0), Squid::class.java)
                setNoAI(squid)
                squid.velocity = org.bukkit.util.Vector(0.0, 1.5, 0.0)
                tracked.add(squid)
            }
        }
    }

    private fun previewBedBreakEffect(player: Player, entry: CosmeticEntry)
    {
        if (entry.id == "random")
        {
            val randomEffects = listOf("fireworks", "lightning_strike", "bed_bugs", "egg_explosion", "tornado", "pig_missile", "squid_missile", "ghost", "hologram", "thief")
            val randomEntry = CosmeticEntry(
                id = randomEffects.random(),
                displayName = entry.displayName,
                description = entry.description,
                material = entry.material,
                rarity = entry.rarity
            )
            previewBedBreakEffect(player, randomEntry)
            return
        }

        val plugin = JavaPlugin.getProvidingPlugin(this::class.java)
        val previewLoc = player.location.clone()
        val world = player.world

        val cameraLoc = previewLoc.clone().add(
            previewLoc.direction.multiply(-3.0)
        ).add(0.0, 2.0, 0.0)

        val entitiesToClean = mutableListOf<Entity>()

        startCameraPreview(player, cameraLoc, endDelay = 100L,
            onStart = { _ ->
                @Suppress("DEPRECATION")
                player.sendBlockChange(previewLoc, Material.BED_BLOCK, 14.toByte())

                Bukkit.getScheduler().runTaskLater(plugin, {
                    val originalBlock = world.getBlockAt(previewLoc)
                    @Suppress("DEPRECATION")
                    player.sendBlockChange(previewLoc, originalBlock.type, originalBlock.data)

                    executeBedBreakEffect(entry.id, previewLoc, world, plugin, entitiesToClean)
                }, 20L)
            },
            onEnd = {
                for (e in entitiesToClean)
                {
                    if (e.isValid) e.remove()
                }
            }
        )
    }

    private fun executeBedBreakEffect(
        id: String,
        loc: Location,
        world: org.bukkit.World,
        plugin: org.bukkit.plugin.Plugin,
        tracked: MutableList<Entity>
    )
    {
        when (id)
        {
            "fireworks" ->
            {
                val fw = world.spawn(loc.clone().add(0.0, 1.0, 0.0), Firework::class.java)
                val meta = fw.fireworkMeta
                meta.addEffect(
                    FireworkEffect.builder()
                        .with(FireworkEffect.Type.BALL)
                        .withColor(Color.RED, Color.WHITE)
                        .flicker(true)
                        .build()
                )
                meta.power = 0
                fw.fireworkMeta = meta
                fw.detonate()
            }

            "lightning_strike" ->
            {
                world.strikeLightningEffect(loc)
            }

            "bed_bugs" ->
            {
                for (i in 0 until 5)
                {
                    val silverfish = world.spawn(
                        loc.clone().add(
                            (Math.random() - 0.5) * 2.0,
                            0.5,
                            (Math.random() - 0.5) * 2.0
                        ),
                        Silverfish::class.java
                    )
                    setNoAI(silverfish)
                    tracked.add(silverfish)
                }
            }

            "egg_explosion" ->
            {
                for (i in 0 until 8)
                {
                    val egg = world.spawn(
                        loc.clone().add(0.0, 1.5, 0.0),
                        Egg::class.java
                    )
                    egg.velocity = org.bukkit.util.Vector(
                        (Math.random() - 0.5) * 0.8,
                        0.5 + Math.random() * 0.5,
                        (Math.random() - 0.5) * 0.8
                    )
                }
            }

            "tornado" ->
            {
                var tick = 0
                Bukkit.getScheduler().runTaskTimer(plugin, {
                    if (tick >= 20) return@runTaskTimer
                    val radius = 1.0 + (tick * 0.1)
                    val y = tick * 0.15
                    for (angle in 0 until 360 step 30)
                    {
                        val rad = Math.toRadians(angle.toDouble())
                        val x = Math.cos(rad) * radius
                        val z = Math.sin(rad) * radius
                        world.playEffect(loc.clone().add(x, y, z), Effect.CLOUD, 0)
                    }
                    tick++
                }, 0L, 2L).let { task ->
                    Bukkit.getScheduler().runTaskLater(plugin, { task.cancel() }, 42L)
                }
            }

            "pig_missile" ->
            {
                val pig = world.spawn(loc.clone().add(0.0, 1.0, 0.0), Pig::class.java)
                setNoAI(pig)
                pig.velocity = org.bukkit.util.Vector(0.0, 1.5, 0.0)
                tracked.add(pig)
            }

            "squid_missile" ->
            {
                val squid = world.spawn(loc.clone().add(0.0, 1.0, 0.0), Squid::class.java)
                setNoAI(squid)
                squid.velocity = org.bukkit.util.Vector(0.0, 1.5, 0.0)
                tracked.add(squid)
            }

            "ghost" ->
            {
                for (i in 0 until 3)
                {
                    val bat = world.spawn(
                        loc.clone().add(
                            (Math.random() - 0.5) * 4.0,
                            1.0 + Math.random() * 2.0,
                            (Math.random() - 0.5) * 4.0
                        ),
                        Bat::class.java
                    )
                    setNoAI(bat)
                    tracked.add(bat)
                }
            }

            "hologram" ->
            {
                val center = loc.clone().add(0.0, 2.5, 0.0)
                val stand = world.spawn(center, ArmorStand::class.java)
                stand.isVisible = false
                stand.setGravity(false)
                stand.isCustomNameVisible = true
                stand.customName = "${CC.RED}${CC.BOLD}BED DESTROYED!"
                stand.isSmall = true
                tracked.add(stand)
            }

            "thief" ->
            {
                val enderman = world.spawn(loc.clone().add(2.0, 0.0, 0.0), Enderman::class.java)
                setNoAI(enderman)
                tracked.add(enderman)
            }
        }
    }

    private fun previewVictoryDance(player: Player, entry: CosmeticEntry)
    {
        player.sendMessage("")
        player.sendMessage("${CC.GRAY}${CC.STRIKE_THROUGH}-------------------")
        player.sendMessage("${CC.YELLOW}${CC.BOLD}Victory Dance Preview")
        player.sendMessage("")
        player.sendMessage("${CC.GREEN}${entry.displayName}")
        for (line in entry.description)
        {
            player.sendMessage(line)
        }
        player.sendMessage("")

        val flavorText = when (entry.id)
        {
            "fireworks" -> "${CC.AQUA}Fireworks will launch around you after a victory!"
            "anvil_rain" -> "${CC.AQUA}Anvils rain from the sky on all losers!"
            "cold_snap" -> "${CC.AQUA}Ice spreads from your location, freezing the ground!"
            "dragon_rider" -> "${CC.AQUA}You mount an Ender Dragon and fly around the map!"
            "guardians" -> "${CC.AQUA}3 Guardian beams lock onto you protectively!"
            "haunted" -> "${CC.AQUA}Ghosts on bats swarm the battlefield around you!"
            "raining_pigs" -> "${CC.AQUA}Pigs with saddles rain from the sky endlessly!"
            "wither_rider" -> "${CC.AQUA}You ride a Wither and shoot skulls around the map!"
            "floating_lantern" -> "${CC.AQUA}Glowing lanterns float up into the sky on bats!"
            "night_shift" -> "${CC.AQUA}The world cycles through day and night rapidly!"
            "rainbow_dolly" -> "${CC.AQUA}Colorful sheep named Dolly spawn, right-click to clone them!"
            "toy_stick" -> "${CC.AQUA}You get a stick that launches you sky high on right-click!"
            "twerk_apocalypse" -> "${CC.AQUA}Sneak to spawn flowers and bonemeal effects everywhere!"
            "yee_haw" -> "${CC.AQUA}You ride a speedy horse with max jump boost!"
            else -> null
        }
        if (flavorText != null)
        {
            player.sendMessage(flavorText)
        }

        player.sendMessage("")
        player.sendMessage("${CC.GRAY}${CC.STRIKE_THROUGH}-------------------")

        val sound = when (entry.id)
        {
            "fireworks" -> Sound.FIREWORK_LAUNCH
            "anvil_rain" -> Sound.ANVIL_LAND
            "cold_snap" -> Sound.ORB_PICKUP
            "dragon_rider" -> Sound.ENDERDRAGON_GROWL
            "guardians" -> Sound.AMBIENCE_RAIN
            "haunted" -> Sound.BAT_IDLE
            "raining_pigs" -> Sound.PIG_IDLE
            "wither_rider" -> Sound.WITHER_SPAWN
            "floating_lantern" -> Sound.LEVEL_UP
            "night_shift" -> Sound.NOTE_PLING
            "rainbow_dolly" -> Sound.SHEEP_IDLE
            "toy_stick" -> Sound.FIREWORK_BLAST
            "twerk_apocalypse" -> Sound.DIG_GRASS
            "yee_haw" -> Sound.HORSE_GALLOP
            else -> null
        }
        if (sound != null)
        {
            player.playSound(player.location, sound, 1.0f, 1.0f)
        }
    }

    private fun previewGlyph(player: Player, entry: CosmeticEntry)
    {
        val world = player.world
        val center = player.location.clone().add(0.0, 3.0, 0.0)
        val plugin = JavaPlugin.getProvidingPlugin(this::class.java)

        when (entry.id)
        {
            "iron" ->
            {
                val points = mutableListOf<Pair<Double, Double>>()
                var t = 0.0
                while (t < Math.PI * 2)
                {
                    points += Pair(Math.cos(t) * 0.8, Math.sin(t) * 0.8)
                    t += 0.3
                }
                animateGlyphPoints(player, plugin, center, points, Effect.INSTANT_SPELL, 3, 2L)
            }

            "gold" ->
            {
                val points = mutableListOf<Pair<Double, Double>>()
                val r = 0.8
                for (i in 0..5) points += Pair(i * r / 5, (5 - i) * r / 5)
                for (i in 0..5) points += Pair((5 - i) * r / 5, -i * r / 5)
                for (i in 0..5) points += Pair(-i * r / 5, -(5 - i) * r / 5)
                for (i in 0..5) points += Pair(-(5 - i) * r / 5, i * r / 5)
                animateGlyphPoints(player, plugin, center, points, Effect.MAGIC_CRIT, 3, 1L)
            }

            "thumbsup" ->
            {
                val points = mutableListOf<Pair<Double, Double>>()
                for (i in 0..6) points += Pair(0.0, i * 0.12)
                points += Pair(-0.3, 0.0); points += Pair(-0.15, 0.0)
                points += Pair(0.15, 0.0); points += Pair(0.3, 0.0)
                points += Pair(-0.3, -0.12); points += Pair(-0.15, -0.12)
                points += Pair(0.15, -0.12); points += Pair(0.3, -0.12)
                animateGlyphPoints(player, plugin, center, points, Effect.HAPPY_VILLAGER, 4, 2L)
            }

            "thumbsdown" ->
            {
                val points = mutableListOf<Pair<Double, Double>>()
                for (i in 0..6) points += Pair(0.0, -i * 0.12)
                points += Pair(-0.3, 0.0); points += Pair(-0.15, 0.0)
                points += Pair(0.15, 0.0); points += Pair(0.3, 0.0)
                points += Pair(-0.3, 0.12); points += Pair(-0.15, 0.12)
                points += Pair(0.15, 0.12); points += Pair(0.3, 0.12)
                animateGlyphPoints(player, plugin, center, points, Effect.VILLAGER_THUNDERCLOUD, 4, 2L)
            }

            "sword" ->
            {
                val points = mutableListOf<Pair<Double, Double>>()
                for (i in 0..8) points += Pair(0.0, i * 0.15)
                points += Pair(-0.3, 0.6); points += Pair(-0.15, 0.6)
                points += Pair(0.15, 0.6); points += Pair(0.3, 0.6)
                points += Pair(0.0, -0.15); points += Pair(0.0, -0.3)
                animateGlyphPoints(player, plugin, center, points, Effect.INSTANT_SPELL, 3, 2L)
            }

            "storm" ->
            {
                val points = mutableListOf<Pair<Double, Double>>()
                points += Pair(0.0, 0.9); points += Pair(0.1, 0.75)
                points += Pair(-0.1, 0.6); points += Pair(0.1, 0.45)
                points += Pair(-0.1, 0.3); points += Pair(0.1, 0.15)
                points += Pair(0.0, 0.0); points += Pair(-0.1, -0.15)
                points += Pair(0.0, -0.3)
                points += Pair(-0.3, 0.9); points += Pair(0.3, 0.9)
                points += Pair(-0.2, 1.0); points += Pair(0.2, 1.0)
                points += Pair(0.0, 1.05)
                animateGlyphPoints(player, plugin, center, points, Effect.FIREWORKS_SPARK, 3, 2L)
            }

            "burn" ->
            {
                var angle = 0.0
                Bukkit.getScheduler().runTaskTimer(plugin, {
                    if (angle >= Math.PI * 4 || !player.isOnline) return@runTaskTimer
                    val r = 0.5 + Math.sin(angle) * 0.3
                    val x = Math.cos(angle) * r
                    val y = Math.sin(angle * 0.5) * 0.8
                    world.playEffect(center.clone().add(x, y, 0.0), Effect.MOBSPAWNER_FLAMES, 0)
                    world.playEffect(center.clone().add(-x * 0.5, y + 0.3, 0.0), Effect.LAVA_POP, 0)
                    angle += 0.4
                }, 0L, 1L).let { task ->
                    Bukkit.getScheduler().runTaskLater(plugin, { task.cancel() }, 40L)
                }
            }

            "bronze-shield" ->
            {
                val points = mutableListOf<Pair<Double, Double>>()
                for (i in -3..3) points += Pair(i * 0.15, 0.6)
                for (i in 0..4) points += Pair(-0.45, 0.6 - i * 0.15)
                for (i in 0..4) points += Pair(0.45, 0.6 - i * 0.15)
                points += Pair(-0.3, -0.15); points += Pair(0.3, -0.15)
                points += Pair(-0.15, -0.3); points += Pair(0.15, -0.3)
                points += Pair(0.0, -0.45)
                animateGlyphPoints(player, plugin, center, points, Effect.COLOURED_DUST, 4, 2L)
            }
        }
    }

    private fun animateGlyphPoints(
        player: Player,
        plugin: org.bukkit.plugin.Plugin,
        center: Location,
        points: List<Pair<Double, Double>>,
        effect: Effect,
        perTick: Int,
        interval: Long
    )
    {
        val world = player.world
        var index = 0
        Bukkit.getScheduler().runTaskTimer(plugin, {
            if (index >= points.size || !player.isOnline) return@runTaskTimer
            for (i in 0 until minOf(perTick, points.size - index))
            {
                val (px, py) = points[index + i]
                world.playEffect(center.clone().add(px, py, 0.0), effect, 0)
            }
            index += perTick
        }, 0L, interval).let { task ->
            val maxTicks = ((points.size / perTick) + 2) * interval + 10
            Bukkit.getScheduler().runTaskLater(plugin, { task.cancel() }, maxTicks)
        }
    }

    @Suppress("DEPRECATION")
    private fun previewShopKeeperSkin(player: Player, entry: CosmeticEntry)
    {
        if (entry.id == "random")
        {
            val selectable = ShopKeeperSkinType.selectableEntries()
            if (selectable.isNotEmpty())
            {
                val randomSkin = selectable.random()
                previewShopKeeperSkin(player, randomSkin.cosmetic)
            }
            return
        }

        val skinEntry = ShopKeeperSkinType.findSkinEntry(entry.id)
        val plugin = JavaPlugin.getProvidingPlugin(this::class.java)
        val previewLoc = player.location.clone()
        val world = player.world

        val cameraLoc = previewLoc.clone().add(
            previewLoc.direction.multiply(-3.0)
        ).add(0.0, 1.5, 0.0)

        val spawnLoc = previewLoc.clone().add(previewLoc.direction.multiply(2.0))
        spawnLoc.y = previewLoc.y

        val direction = cameraLoc.toVector().subtract(spawnLoc.toVector()).normalize()
        spawnLoc.direction = direction

        val entitiesToClean = mutableListOf<Entity>()
        val npcsToClean = mutableListOf<NpcEntity>()

        startCameraPreview(player, cameraLoc, endDelay = 100L,
            onStart = { _ ->
                if (skinEntry != null && skinEntry.skinValue != null)
                {
                    val npc = object : NpcEntity(listOf("${CC.GREEN}${entry.displayName}"), spawnLoc)
                    {
                        override fun onRightClick(player: Player) {}
                    }
                    npc.persistent = false
                    npc.initializeData()
                    npc.updateTexture(skinEntry.skinValue, skinEntry.skinSignature)
                    EntityHandler.trackEntity(npc)
                    npcsToClean.add(npc)
                }
                else if (skinEntry != null && skinEntry.entityType != null)
                {
                    val entityClass = when (skinEntry.entityType)
                    {
                        EntityType.SKELETON -> Skeleton::class.java
                        EntityType.PIG_ZOMBIE -> PigZombie::class.java
                        EntityType.ZOMBIE -> Zombie::class.java
                        EntityType.SNOWMAN -> Snowman::class.java
                        EntityType.BLAZE -> Blaze::class.java
                        EntityType.CREEPER -> Creeper::class.java
                        EntityType.WITCH -> Witch::class.java
                        EntityType.VILLAGER -> Villager::class.java
                        else -> null
                    }
                    if (entityClass != null)
                    {
                        val mob = world.spawn(spawnLoc, entityClass)
                        setNoAI(mob)
                        mob.isCustomNameVisible = true
                        mob.customName = "${CC.GREEN}${entry.displayName}"

                        val mobLoc = mob.location
                        mobLoc.direction = direction
                        mob.teleport(mobLoc)

                        entitiesToClean.add(mob)
                    }
                }
            },
            onEnd = {
                for (e in entitiesToClean)
                {
                    if (e.isValid) e.remove()
                }
                for (npc in npcsToClean)
                {
                    npc.destroyForCurrentWatchers()
                    EntityHandler.forgetEntity(npc)
                }
            }
        )
    }
}
