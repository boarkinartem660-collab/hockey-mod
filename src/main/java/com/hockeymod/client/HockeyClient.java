package com.hockeymod.client;

import com.hockeymod.ModBlocks;
import com.hockeymod.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class HockeyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.PUCK,
                ctx -> new FlyingItemEntityRenderer<>(ctx, 0.6f, true));
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GOAL, RenderLayer.getCutout());
    }
}
