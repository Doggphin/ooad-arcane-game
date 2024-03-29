package org.example;

public class EntityFactory {

    Adventurer createAdventurer(String name){
        return new Adventurer(name, Alignment.GOOD);
    }

    Glutton createGlutton(String name){
        return new Glutton(name, Alignment.GOOD);
    }

    Knight createKnight(String name){
        return new Knight(name, Alignment.GOOD);
    }

    Demon createDemon(String name){
        return new Demon(name, Alignment.EVIL);
    }

    Creature createCreature(String name){
        return new Creature(name, Alignment.EVIL);
    }

    Coward createCoward(String name){
        return new Coward(name, Alignment.GOOD);
    }

}
