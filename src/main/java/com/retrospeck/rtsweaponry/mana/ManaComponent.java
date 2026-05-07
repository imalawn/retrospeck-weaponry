package com.retrospeck.rtsweaponry.mana;

import org.ladysnake.cca.api.v3.component.Component;

public interface ManaComponent extends Component {
    // player consumes mana when they use an ability, returns true if successful
    boolean consume(int cost);

    // regenerate player's mana by set amount (stored in variable), return false once full
    boolean regenerate();

    // setter methods for player, either return true if successful or return nothing
    boolean addMana(int amount); // will add by set amount, could also be used for potions
    void resetPlayerMax(); // resets to default value
    void resetPlayerRegen(); // resets to default value
    boolean setMana(int newAmount); // allows for overflow
    boolean setPlayerMax(int newMax);
    boolean setPlayerRegen (int newRate);

    // for any mod elements that modify player's values
    void addMaxModifier(double scalar);
    void addRegenModifier(double scalar);
    void removeMaxModifier(double modifier);
    void removeRegenModifier(double modifier);

    // refresh modifiers
    void refreshPlayerMax();
    void refreshPlayerRegen();

    // getter methods
    int getPlayerMana();
    int getPlayerMax();
    int getPlayerRegen();
}