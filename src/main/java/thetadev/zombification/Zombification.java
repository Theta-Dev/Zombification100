package thetadev.zombification;

import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Zombification.MODID)
public class Zombification
{
    public static final String MODID = "zombification";
    public static final Logger LOGGER = LogManager.getLogger();

    public Zombification() {
        MinecraftForge.EVENT_BUS.register(this);

        ConfigHandler.register();
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        // Is a villager being killed by a zombie?
        LivingEntity entity = event.getEntityLiving();
        if(!(entity instanceof VillagerEntity)) return;
        if(!(event.getSource().getTrueSource() instanceof ZombieEntity)) return;

        // Spawn a zombified villager
        if(chanceOccurred(entity, ConfigHandler.ZOMBIFICATION_CHANCE.get())) {
            VillagerEntity villager = (VillagerEntity) entity;
            ServerWorld world = (ServerWorld) villager.world;

            // Copied from net.minecraft.entity.monster.ZombieEntity#onKillEntity
            ZombieVillagerEntity zombievillager = EntityType.ZOMBIE_VILLAGER.create(world);
            zombievillager.copyLocationAndAnglesFrom(villager);

            zombievillager.onInitialSpawn(world, world.getDifficultyForLocation(zombievillager.getPosition()), SpawnReason.CONVERSION, null, (CompoundNBT)null);

            zombievillager.setVillagerData(villager.getVillagerData());
            zombievillager.setGossips(villager.getGossip().func_234058_a_(NBTDynamicOps.INSTANCE).getValue());
            zombievillager.setOffers(villager.getOffers().write());
            zombievillager.setEXP(villager.getXp());
            zombievillager.setChild(villager.isChild());
            zombievillager.setNoAI(villager.isAIDisabled());
            if (villager.hasCustomName()) {
                zombievillager.setCustomName(villager.getCustomName());
                zombievillager.setCustomNameVisible(villager.isCustomNameVisible());
            }
            zombievillager.setInvulnerable(villager.isInvulnerable());

            if (villager.isNoDespawnRequired() || chanceOccurred(zombievillager, ConfigHandler.PERSISTENCE_CHANCE.get())) {
                zombievillager.enablePersistence();
            }
            zombievillager.setCanPickUpLoot(chanceOccurred(zombievillager, ConfigHandler.PICKUP_CHANCE.get()));

            world.addEntity(zombievillager);
            world.playEvent((PlayerEntity)null, 1026, zombievillager.getPosition(), 0);
        }

        // Remove villager and prevent the vanilla logic from being called
        entity.remove();
        event.setCanceled(true);
    }

    private static boolean chanceOccurred(LivingEntity entity, double chance) {
        return chance > entity.getRNG().nextDouble();
    }
}
