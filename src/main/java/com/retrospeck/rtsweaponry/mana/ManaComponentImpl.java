package com.retrospeck.rtsweaponry.mana;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;

public class ManaComponentImpl implements ManaComponent {
    private int playerMana;
    private int playerMax;
    private int playerRegen;
    private final DoubleArrayList playerMaxModifiers;
    private final DoubleArrayList playerRegenModifiers;

    public ManaComponentImpl() {
        playerMana = ManaSystem.getDefaultMax(); // mana is full after initialization
        playerMaxModifiers = new DoubleArrayList();
        playerRegenModifiers = new DoubleArrayList();
        refreshPlayerMax();
        refreshPlayerRegen();
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        playerMana = nbtCompound.getInt("mana");
        playerMax = nbtCompound.getInt("manaLimit");
        playerRegen = nbtCompound.getInt("regenRate");
        // load playerMaxModifiers
        playerMaxModifiers.clear();
        if (nbtCompound.contains("maxModifiers", 9)) {  // 9 = NbtList type
            NbtList maxModifiersList = nbtCompound.getList("maxModifiers", 6); // 6 = double type
            for (int i = 0; i < maxModifiersList.size(); i++) {
                playerMaxModifiers.add(maxModifiersList.getDouble(i));
            }
        }
        refreshPlayerMax();
        // load playerRegenModifiers
        playerRegenModifiers.clear();
        if (nbtCompound.contains("regenModifiers", 9)) {
            NbtList regenModifiersList = nbtCompound.getList("regenModifiers", 6);
            for (int i = 0; i < regenModifiersList.size(); i++) {
                playerRegenModifiers.add(regenModifiersList.getDouble(i));
            }
        }
        refreshPlayerRegen();
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putInt("mana", playerMana);
        nbtCompound.putInt("manaLimit", playerMax);
        nbtCompound.putInt("regenRate", playerRegen);
        // save playerMaxModifiers
        NbtList maxModifiersList = new NbtList();
        for (double d : playerMaxModifiers) {
            maxModifiersList.add(NbtDouble.of(d));
        }
        nbtCompound.put("maxModifiers", maxModifiersList);
        // save playerRegenModifiers
        NbtList regenModifiersList = new NbtList();
        for (double d : playerRegenModifiers) {
            regenModifiersList.add(NbtDouble.of(d));
        }
        nbtCompound.put("regenModifiers", regenModifiersList);
    }

    public boolean consume(int cost) {
        int newMana = playerMana - cost;
        if (newMana < 0)
            return false;
        playerMana = newMana;
        return true;
    }

    public boolean regenerate() {
        if (playerMana >= playerMax)
            return false;
        int newMana = playerMana + playerRegen;
        if (newMana > playerMax) {
            playerMana = playerMax;
            return false;
        }
        playerMana = newMana;
        return true;
    }

    public boolean addMana(int amount) {
        if (playerMana >= playerMax && amount > 0)
            return false;
        else if (playerMana <= 0 && amount < 0)
            return false;
        int newAmount = playerMana + amount;
        if (newAmount > playerMax) {
            playerMana = playerMax; // fills player's mana to full but doesn't go over
            return true;
        }
        else if (newAmount < 0) {
            playerMana = 0; // does not let mana go negative
            return true;
        }
        playerMana = newAmount;
        return true;
    }

    // for mod elements that can modify mana cap/regen rate
    public void addMaxModifier(double modifier) {
        playerMaxModifiers.add(modifier);
        refreshPlayerMax();
    }
    public void addRegenModifier(double modifier) {
        playerRegenModifiers.add(modifier);
        refreshPlayerRegen();
    }

    public void removeMaxModifier(double modifier) {
        playerMaxModifiers.rem(modifier);
        refreshPlayerMax();
    }

    public void removeRegenModifier(double modifier) {
        playerRegenModifiers.rem(modifier);
        refreshPlayerRegen();
    }

    public void refreshPlayerMax() {
        double newPlayerMax = ManaSystem.getDefaultMax();
        for (double scalar: playerMaxModifiers) {
            newPlayerMax *= scalar;
        }
        playerMax = (int)newPlayerMax;
        // remove overflow mana
        if (playerMana > playerMax) {
            playerMana = playerMax;
        }
    }

    public void refreshPlayerRegen() {
        double newPlayerRegen = ManaSystem.getDefaultRegen();
        for (double scalar: playerRegenModifiers) {
            newPlayerRegen *= scalar;
        }
        playerRegen = (int)newPlayerRegen;
    }

    public void resetPlayerMax() {
        playerMaxModifiers.clear();
        refreshPlayerMax();
    }

    public void resetPlayerRegen() {
        playerRegenModifiers.clear();
        refreshPlayerRegen();
    }

    public boolean setMana(int newAmount) {
        playerMana = newAmount;
        return newAmount >= 0 && newAmount < playerMax;
    }

    public boolean setPlayerMax(int newMax) {
        if (newMax < 0)
            return false;
        playerMax = newMax;
        return true;
    }

    public boolean setPlayerRegen (int newRate) {
        if (newRate < 0)
            return false;
        playerRegen = newRate;
        return true;
    }

    public int getPlayerMana() { return playerMana; }
    public int getPlayerMax() { return playerMax; }
    public int getPlayerRegen() { return playerRegen; }
}