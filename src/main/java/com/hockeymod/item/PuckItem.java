package com.hockeymod.item;

import com.hockeymod.entity.ModEntities;
import com.hockeymod.entity.PuckEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PuckItem extends Item {
    public PuckItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World world = ctx.getWorld();
        if (!world.isClient) {
            BlockPos pos = ctx.getBlockPos().offset(ctx.getSide());
            PuckEntity puck = new PuckEntity(ModEntities.PUCK, world);
            puck.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            world.spawnEntity(puck);
            if (ctx.getPlayer() == null || !ctx.getPlayer().isCreative()) {
                ctx.getStack().decrement(1);
            }
        }
        return ActionResult.success(world.isClient);
    }
}
