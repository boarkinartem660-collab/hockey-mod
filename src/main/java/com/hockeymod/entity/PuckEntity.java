package com.hockeymod.entity;

import com.hockeymod.ModBlocks;
import com.hockeymod.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PuckEntity extends Entity implements FlyingItemEntity {
    private String lastKicker = null;

    public PuckEntity(EntityType<? extends PuckEntity> type, World world) {
        super(type, world);
    }

    @Override protected void initDataTracker() {}
    @Override protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override public ItemStack getStack() { return new ItemStack(ModItems.PUCK); }
    @Override public boolean canHit() { return !this.isRemoved(); }
    @Override public boolean damage(DamageSource source, float amount) { return false; }

    /** Удар по шайбе в направлении взгляда игрока. */
    public void kick(PlayerEntity player, double power) {
        Vec3d look = player.getRotationVec(1.0f);
        Vec3d flat = new Vec3d(look.x, 0, look.z);
        if (flat.lengthSquared() < 1.0E-6) return;
        flat = flat.normalize();
        double lift = Math.max(0.0, look.y) * power * 0.6 + 0.02;
        this.setVelocity(flat.x * power, lift, flat.z * power);
        this.velocityDirty = true;
        this.lastKicker = player.getName().getString();
        this.getWorld().playSound(null, getX(), getY(), getZ(),
                SoundEvents.BLOCK_WOOD_HIT, SoundCategory.PLAYERS, 1.0f, 1.5f);
    }

    // Shift + ПКМ — подобрать шайбу
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            if (!getWorld().isClient) {
                player.giveItemStack(new ItemStack(ModItems.PUCK));
                this.discard();
            }
            return ActionResult.success(getWorld().isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3d vel = this.getVelocity();
        if (!this.hasNoGravity()) {
            vel = vel.add(0, -0.04, 0);
        }

        Vec3d before = this.getPos();
        this.move(MovementType.SELF, vel);
        Vec3d moved = this.getPos().subtract(before);

        double vx = vel.x, vy = vel.y, vz = vel.z;

        // Отскок от бортов (стен) и пола
        if (Math.abs(moved.x - vel.x) > 1.0E-4) vx = -vel.x * 0.7;
        if (Math.abs(moved.z - vel.z) > 1.0E-4) vz = -vel.z * 0.7;
        if (Math.abs(moved.y - vel.y) > 1.0E-4) {
            vy = -vel.y * 0.3;
            if (Math.abs(vy) < 0.05) vy = 0;
        }

        // Трение: на льду скользит далеко, на обычных блоках быстро тормозит
        if (this.isOnGround()) {
            float slip = getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness();
            double f = 0.5 + slip * 0.5;
            vx *= f;
            vz *= f;
        } else {
            vx *= 0.99;
            vz *= 0.99;
        }
        if (Math.abs(vx) < 0.003) vx = 0;
        if (Math.abs(vz) < 0.003) vz = 0;

        this.setVelocity(vx, vy * 0.98, vz);

        // Проверка гола (только на сервере)
        if (!getWorld().isClient) {
            BlockPos p = this.getBlockPos();
            if (getWorld().getBlockState(p).isOf(ModBlocks.GOAL)) {
                score(p);
            }
        }
    }

    private void score(BlockPos p) {
        ServerWorld sw = (ServerWorld) getWorld();
        String who = lastKicker != null ? " Забил: " + lastKicker : "";
        Text msg = Text.literal("🏒 ГОЛ!" + who).formatted(Formatting.GOLD, Formatting.BOLD);

        for (ServerPlayerEntity pl : sw.getPlayers(pp -> pp.squaredDistanceTo(this) < 64 * 64)) {
            pl.sendMessage(msg, true);
        }
        sw.playSound(null, p, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
        sw.spawnParticles(ParticleTypes.FIREWORK, getX(), getY() + 0.5, getZ(), 40, 0.5, 0.5, 0.5, 0.1);

        // Шайба возвращается предметом, чтобы не пропадать
        sw.spawnEntity(new ItemEntity(sw, p.getX() + 0.5, p.getY() + 1.2, p.getZ() + 0.5,
                new ItemStack(ModItems.PUCK)));
        this.discard();
    }
}
