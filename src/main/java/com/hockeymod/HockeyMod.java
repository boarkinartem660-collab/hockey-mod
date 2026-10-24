package com.hockeymod;

import com.hockeymod.entity.ModEntities;
import com.hockeymod.entity.PuckEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class HockeyMod implements ModInitializer {
    public static final String MOD_ID = "hockey";

    public static final ItemGroup GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(MOD_ID, "main"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.HOCKEY_STICK))
                    .displayName(Text.translatable("itemGroup.hockey.main"))
                    .entries((ctx, entries) -> {
                        entries.add(ModItems.HOCKEY_STICK);
                        entries.add(ModItems.PUCK);
                        entries.add(ModBlocks.GOAL_ITEM);
                    })
                    .build());

    @Override
    public void onInitialize() {
        ModItems.register();
        ModBlocks.register();
        ModEntities.register();

        // Удар по шайбе (ЛКМ): клюшкой сильно, рукой слабо
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (entity instanceof PuckEntity puck && !player.isSpectator()) {
                boolean stick = player.getMainHandStack().isOf(ModItems.HOCKEY_STICK);
                if (!world.isClient) {
                    puck.kick(player, stick ? 0.9 : 0.25);
                    if (stick) {
                        player.getMainHandStack().damage(1, player,
                                p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
                    }
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}
