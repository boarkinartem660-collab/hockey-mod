package com.hockeymod;

import com.hockeymod.item.HockeyStickItem;
import com.hockeymod.item.PuckItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item HOCKEY_STICK = Registry.register(Registries.ITEM,
            new Identifier(HockeyMod.MOD_ID, "hockey_stick"),
            new HockeyStickItem(new FabricItemSettings().maxDamage(500)));

    public static final Item PUCK = Registry.register(Registries.ITEM,
            new Identifier(HockeyMod.MOD_ID, "puck"),
            new PuckItem(new FabricItemSettings()));

    public static void register() {}
}
