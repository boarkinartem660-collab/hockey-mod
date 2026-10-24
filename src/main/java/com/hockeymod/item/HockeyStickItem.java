package com.hockeymod.item;

import com.hockeymod.entity.PuckEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class HockeyStickItem extends Item {
    private static final double REACH = 3.5;

    public HockeyStickItem(Settings settings) {
        super(settings);
    }

    // ПКМ — бросок (сильный удар) по ближайшей шайбе
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Vec3d look = player.getRotationVec(1.0f);
        Box box = player.getBoundingBox().stretch(look.multiply(REACH)).expand(1.0);

        List<PuckEntity> pucks = world.getEntitiesByClass(PuckEntity.class, box,
                p -> p.squaredDistanceTo(player) <= REACH * REACH);
        PuckEntity target = pucks.stream()
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(player)))
                .orElse(null);

        if (target == null) {
            return TypedActionResult.pass(stack);
        }

        if (!world.isClient) {
            target.kick(player, 1.7);
            stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
        }
        player.getItemCooldownManager().set(this, 12);
        return TypedActionResult.success(stack, world.isClient);
    }
}
