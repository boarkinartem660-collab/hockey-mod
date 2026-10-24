package com.hockeymod;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public class ModBlocks {
    // Ворота: блок без коллизии, шайба залетает внутрь и засчитывается гол
    public static final Block GOAL = Registry.register(Registries.BLOCK,
            new Identifier(HockeyMod.MOD_ID, "goal"),
            new Block(FabricBlockSettings.create()
                    .mapColor(MapColor.RED)
                    .strength(1.5f)
                    .noCollision()
                    .nonOpaque()));

    public static final Item GOAL_ITEM = Registry.register(Registries.ITEM,
            new Identifier(HockeyMod.MOD_ID, "goal"),
            new BlockItem(GOAL, new FabricItemSettings()));

    public static void register() {}
}
