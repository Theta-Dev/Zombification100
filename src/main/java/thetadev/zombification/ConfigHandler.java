package thetadev.zombification;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler
{
	private static final ForgeConfigSpec.Builder builder;

	public static final ForgeConfigSpec.DoubleValue ZOMBIFICATION_CHANCE;
	public static final ForgeConfigSpec.DoubleValue PICKUP_CHANCE;
	public static final ForgeConfigSpec.DoubleValue PERSISTENCE_CHANCE;

	static {
		builder = new ForgeConfigSpec.Builder();
		builder.comment("Chance for a villager killed by a zombie to turn into a zombie villager");
		ZOMBIFICATION_CHANCE = builder.defineInRange("ZombificationChance", 1D, 0, 1);
		builder.comment("Chance for a converted zombie to be able to pick up loot");
		PICKUP_CHANCE = builder.defineInRange("PickupChance", 1D, 0, 1);
		builder.comment("Chance for a converted zombie to not despawn");
		PERSISTENCE_CHANCE = builder.defineInRange("PersistenceChance", 1D, 0, 1);
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build(), Zombification.MODID + ".toml");
	}
}
