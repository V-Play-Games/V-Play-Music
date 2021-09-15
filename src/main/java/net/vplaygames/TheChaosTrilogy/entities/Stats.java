package net.vplaygames.TheChaosTrilogy.entities;

import net.dv8tion.jda.api.utils.data.DataObject;

public class Stats {
    int hp;
    int attack;
    int defense;
    int spAtk;
    int spDef;
    int speed;

    public Stats(DataObject data) {
        this(data.getInt("hp"),
            data.getInt("attack"),
            data.getInt("defense"),
            data.getInt("special-attack"),
            data.getInt("special-defense"),
            data.getInt("speed"));
    }

    public Stats(int hp, int attack, int defense, int spAtk, int spDef, int speed) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.spAtk = spAtk;
        this.spDef = spDef;
        this.speed = speed;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpAtk() {
        return spAtk;
    }

    public int getSpDef() {
        return spDef;
    }

    public int getSpeed() {
        return speed;
    }

    public String toString() {
        return "HP: " + hp + " | Attack: " + attack + " | Defense: " + defense + " | Sp. Atk: " + spAtk + " | Sp. Def: " + spDef + " | Speed: " + speed;
    }
}
