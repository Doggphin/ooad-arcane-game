package org.example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Food {
    private static final Logger logger = LoggerFactory.getLogger("csci.ooad.arcane.Arcane");

    private final String name;

    public String getName() {
        return name;
    }

    public Food(String name){
        this.name = name;
    }

    public void logState() {
        logger.info(name + " ");
    }
}
