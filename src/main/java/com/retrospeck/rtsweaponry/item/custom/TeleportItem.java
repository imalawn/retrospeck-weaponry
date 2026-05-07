package com.retrospeck.rtsweaponry.item.custom;

import com.retrospeck.rtsweaponry.Config;
import com.retrospeck.rtsweaponry.ModComponents;
import com.retrospeck.rtsweaponry.RTSWeaponry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TeleportItem extends SwordItem {
    private static double teleportLength;
    private static int manaCost;

    public TeleportItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Settings settings){
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }
    public static boolean initialize() {
        boolean success = true;
        try {
            teleportLength = Config.getDouble("abilities.teleport.teleportLength");
        }
        catch (ArithmeticException e) {
            teleportLength = 0.0;
            RTSWeaponry.LOGGER.error("Configuration value teleportLength in [abilities.teleport] is invalid! Expected a double (floating point value).");
            success = false;
        }
        try {
            manaCost = (int) (Config.getLong("abilities.teleport.manaCost"));
        }
        catch (ArithmeticException e) {
            manaCost = 0;
            RTSWeaponry.LOGGER.error("Configuration value manaCost in [abilities.teleport] is invalid! Expected an integer.");
            success = false;
        }
        return success;
    }
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        if (world.isClient){
            return ActionResult.PASS;
        }
        if (ModComponents.MANA.get(player).consume(manaCost)) {
            teleport(player);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    private void teleport(PlayerEntity playerEntity){
        HitResult rayResult = playerEntity.raycast(teleportLength, 0.0f, false);
        Vec3d hitPos = rayResult.getPos();
        Vec3d playerCamPos = playerEntity.getEyePos();
        Vec3d direction = hitPos.subtract(playerCamPos).normalize();
        double offsetDistance = 0.3;
        Vec3d safePos = hitPos.subtract(direction.multiply(offsetDistance));

        playerEntity.requestTeleport(safePos.x, rayResult.getPos().y, safePos.z);
    }

}
