package com.hockeymod.entity;

import com.hockeymod.HockeyMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<PuckEntity> PUCK = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(HockeyMod.MOD_ID, "puck"),
            FabricEntityTypeBuilder.<PuckEntity>create(SpawnGroup.MISC, PuckEntity::new)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.2f))
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(1)
                    .forceTrackedVelocityUpdates(true)
                    .build());

    public static void register() {}
}
