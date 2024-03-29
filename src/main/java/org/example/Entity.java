package org.example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    protected static final Logger logger = LoggerFactory.getLogger("csci.ooad.arcane.Arcane");
    protected final String name;
    protected int turns;
    protected List<Entity> hasFoughtCache;
    protected final Alignment alignment;

    public String getName() {
        return name;
    }

    protected double health;
    protected double maxHealth;
    public double getHealth() {
        return health;
    }
    public double getMaxHealth() {
        return maxHealth;
    }



    public Entity(String name, Alignment alignment, Double health){
        this.alignment = alignment;
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.hasFoughtCache = new ArrayList<>();
    }

    public boolean isDead(){
        return health <= 0;
    }

    public double modifyHealth(double delta){
        health += delta;
        return health;
    }

    public void resetTurnState() {
        hasFoughtCache.clear();
        turns = 0;
    }

    // Returns, in order from first to last index of priority, the intent of the entity on a given turn.
    public List<EntityRequest> getRequests() { return null; }

    public String getEntityStringInfo() {
        return "Unimplemented Entity String Info";
    }
    protected String getEntityStringInfo(String entityTypeDisplayName){
        return(entityTypeDisplayName + " " + name + "(health: " + health + ") ");
    }
    public void logState() {
        logger.info(getEntityStringInfo());
    }
}
