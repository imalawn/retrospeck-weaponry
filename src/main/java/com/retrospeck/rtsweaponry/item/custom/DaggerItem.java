package com.retrospeck.rtsweaponry.item.custom;

import com.retrospeck.rtsweaponry.Config;
import com.retrospeck.rtsweaponry.RTSWeaponry;
import com.retrospeck.rtsweaponry.item.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.SwordItem;

public class DaggerItem extends SwordItem {
    private final float attackDamage;
    private static float extraDamageMulti;
    private static double areaCosAngle;
    private static int cooldownTicks;

    public static boolean initialize() {
        boolean success = true;
        try {
            extraDamageMulti = (float)(Config.getDouble("abilities.backstab.damageMultiplier"));
        }
        catch (ArithmeticException e) {
            extraDamageMulti = 0;
            RTSWeaponry.LOGGER.error("Configuration value damageMultiplier in [abilities.backstab] is invalid! Expected a float (floating point value).");
            success = false;
        }

        try {
            areaCosAngle = Config.getDouble("abilities.backstab.areaCosAngle");
        }
        catch (ArithmeticException e) {
            areaCosAngle = 0;
            RTSWeaponry.LOGGER.error("Configuration value areaCosAngle in [abilities.backstab] is invalid! Expected a double (floating point value).");
            success = false;
        }

        try {
            cooldownTicks = (int)(Config.getLong("abilities.backstab.cooldown"));
            if (cooldownTicks < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            cooldownTicks = 0;
            RTSWeaponry.LOGGER.error("Configuration value cooldown in [abilities.backstab] is invalid! Expected a positive integer.");
            success = false;
        }

        return success;
    }

    public DaggerItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
        this.attackDamage = attackDamage;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof PlayerEntity player) || !isBackstab(attacker, target) || player.getItemCooldownManager().isCoolingDown(stack))
            return super.postHit(stack, target, attacker);
        if (attacker.getWorld().isClient() || !(attacker.getWorld() instanceof ServerWorld serverWorld))
            return super.postHit(stack, target, attacker);

        float extraDamage = attackDamage*extraDamageMulti;
        target.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), extraDamage);
        serverWorld.spawnParticles(
                ModParticles.BACKSTAB_PARTICLE,
                target.getX(), target.getY() + 1.0, target.getZ(),
                12,
                0.3, 0.3, 0.3,
                0.2
        );
        // start cooldown
        player.getItemCooldownManager().set(stack, cooldownTicks);

        return super.postHit(stack, target, attacker);
    }

    public boolean isBackstab(LivingEntity player, LivingEntity target) {
        Vec3d targetDir = target.getRotationVec(0.0f);
        Vec3d flatTargetLooking = new Vec3d(targetDir.x, 0, targetDir.z);

        Vec3d delta = player.getPos().subtract(target.getPos());
        Vec3d flatToAttacker = new Vec3d(delta.x, 0, delta.z).normalize();

        double dot = flatTargetLooking.dotProduct(flatToAttacker);
        return dot < areaCosAngle;

        /* atree implementation
        double fullConeAngleDegrees = 60;
        Vec3d playerFacingVec = player.getRotationVec(1.0f).normalize();
        Vec3d dirToTarget = target.getPos().subtract(player.getPos()).normalize();

        double dot = playerFacingVec.dotProduct(dirToTarget);
        double angleCos = Math.cos(Math.toRadians(fullConeAngleDegrees/2.0));
        //for 60 degrees, this is -0.866: concave up between both points (150 and 210 aka +-30 of 180)

        return dot < angleCos; */
    }
}
