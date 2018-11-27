package com.example.mike.dnd_mobile_app;

import java.util.Random;

public class DnDCharacter {

    public String CharImage = "";
    public String Name = "";
    public int Strength = 0;
    public int Dexterity = 0;
    public int Constitution = 0;
    public int Intelligence = 0;
    public int Wisdom = 0;
    public int Charisma = 0;

    public DnDCharacter()
    {

    }

    public DnDCharacter(String Name)
    {
        this.Name = Name;
        RandomStats();
    }

    public DnDCharacter(String CharImage, String Name, int Strength, int Dexterity, int Constitution, int Intelligence, int Wisdom, int Charisma)
    {
        this.CharImage = CharImage;
        this.Name = Name;
        this.Strength = Strength;
        this.Dexterity = Dexterity;
        this.Constitution = Constitution;
        this.Intelligence = Intelligence;
        this.Wisdom = Wisdom;
        this.Charisma = Charisma;
    }

    public void RandomStats()
    {
        this.Strength = new Random().nextInt(21);
        this.Dexterity = new Random().nextInt(21);
        this.Constitution = new Random().nextInt(21);
        this.Intelligence = new Random().nextInt(21);
        this.Wisdom = new Random().nextInt(21);
        this.Charisma = new Random().nextInt(21);

    }

    public String getCharImage() {
        return CharImage;
    }

    public String getName() {
        return Name;
    }

    public int getStrength() {
        return Strength;
    }

    public int getDexterity() {
        return Dexterity;
    }

    public int getConstitution() {
        return Constitution;
    }

    public int getIntelligence() {
        return Intelligence;
    }

    public int getWisdom() {
        return Wisdom;
    }

    public int getCharisma() {
        return Charisma;
    }

    public void setCharImage(String charImage) {
        CharImage = charImage;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setStrength(int strength) {
        Strength = strength;
    }

    public void setDexterity(int dexterity) {
        Dexterity = dexterity;
    }

    public void setConstitution(int constitution) {
        Constitution = constitution;
    }

    public void setIntelligence(int intelligence) {
        Intelligence = intelligence;
    }

    public void setWisdom(int wisdom) {
        Wisdom = wisdom;
    }

    public void setCharisma(int charisma) {
        Charisma = charisma;
    }
}
