package us.dragonma.minecraft.mods.notwits

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.npc.VillagerDataHolder
import net.minecraft.world.entity.npc.VillagerProfession
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import org.apache.logging.log4j.LogManager

@Mod(Notwits.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
object Notwits {
    private const val ID = "{{ modId }}"
    private val LOGGER = LogManager.getLogger()

    init {
        LOGGER.info("Loading version {{ modVersion }}.")
    }

    @SubscribeEvent
    private fun onEntityJoinLevel(event: EntityJoinLevelEvent) {
        event.entity.apply {
            if (this is VillagerDataHolder && villagerData.profession == VillagerProfession.NITWIT) {
                LOGGER.debug("Forcing ${type.description.string} at $x, $y, $z to become a productive member of society.")
                villagerData = villagerData.setProfession(VillagerProfession.NONE)
                if (this is Villager) {
                    refreshBrain(level() as ServerLevel)
                }
            }
        }
    }
}
