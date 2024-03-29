package org.example;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Room {

    private String name;
    public String getName() {
        return name;
    }

    private static final Logger logger = LoggerFactory.getLogger("csci.ooad.arcane.Arcane");
    Room[] adjacentRooms = new Room[4];
    private List<Entity> presentEntities = new ArrayList<Entity>();
    private List<Food> presentFoods = new ArrayList<Food>();
    private int worldPositionIndex;     // This variable is only being used to conform to project guidelines, describes position in map index
    // This should be in map but because we need to log things in a certain order and can't use dependency injection for a coordinate "struct" this is in room

    public Room(String name) {
        this.name = name;
    }
    public void initialize(Room north, Room east, Room south, Room west, int worldPositionIndex) {      // WorldPositionIndex is only being used as a workaround for conform with guidelines
        adjacentRooms = new Room[]{north, east, south, west};
        this.worldPositionIndex = worldPositionIndex;
    }

    private Alignment flipAlignment(Alignment type) {
        return type == Alignment.GOOD ? Alignment.EVIL : Alignment.GOOD;
    }

    public int countEntitiesOfAlignment(Alignment type) {
        int count = 0;
        for(Entity entity : presentEntities) {
            if(entity.alignment == type) {
                count++;
            }
        }
        return count;
    }

    public List<Entity> getEntitiesOfAlignment(Alignment type) {
        List<Entity> entities = new ArrayList<>();
        for(Entity entity : presentEntities) {
            if(entity.alignment == type) {
                entities.add(entity);
            }
        }
        return entities;
    }
    public List<Food> getFoods() {
        return new ArrayList<>(presentFoods);
    }

    public void addEntity(Entity entity) {
        presentEntities.add(entity);
    }
    public void addFood(Food food) {
        presentFoods.add(food);
    }

    // Moves an entity present in this room to a random adjacent room.
    private void moveEntityToRandAdjacentRoom(Entity entity) {
        Random rand = new Random();
        int indexCache = 0;
        while(adjacentRooms[indexCache] == null) {  // Find an adjacent room that isn't null
            indexCache = rand.nextInt(4);
        }

        int indexOfDirectionToPrint = worldPositionIndex;   // Finds the string of the world position direction relative to the current world position direction

        // While this isn't necessarily a bad thing, this function does not stop any entities from inside the room from realizing that this entity just moved in.
        // Ideally, in the future, add some kind of functionality that disables them from fighting when moved into a new room since this behavior is kinda odd.
        adjacentRooms[indexCache].addEntity(entity);
        presentEntities.remove(entity);
    }

    private Entity getHealthiestEntityOfType(Alignment alignment) {
        Entity healthiestEntity = null;
        for(Entity entity : presentEntities) {   // Check through every present adventurer. Store the highest health one in cache
            if((entity.alignment == alignment) && (healthiestEntity == null || entity.getHealth() > healthiestEntity.getHealth())) {
                healthiestEntity = entity;
            }
        }
        return healthiestEntity;
    }

    // Returns if a battle occurred
    private boolean runBattle(Entity aggressor, Entity defender, boolean allowMultipleFightsPerTurn) throws IOException, InterruptedException {
        logger.info("============== " + aggressor.getName() + " is fighting " + defender.getName() + "!\n");
        EventBus.postMessage(EventType.Attacks, aggressor.getName() + " picks a fight with " + defender.getName() + "!");
        if(allowMultipleFightsPerTurn || (!aggressor.hasFoughtCache.contains(defender) && !defender.hasFoughtCache.contains(aggressor))) {
            Random rand = new Random();
            int aRoll = rand.nextInt(6) + 1;
            //logger.info("================== " + aggressor.getName() + " rolled a " + aRoll + "...\n");
            int bRoll = rand.nextInt(6) + 1;
            //logger.info("================== " + defender.getName() + " rolled a " + aRoll + "...\n");
            int difference = aRoll - bRoll;
            if (difference > 0) {               // Adventurer got the higher roll
                defender.modifyHealth(-difference);
                logger.info("================== " + aggressor.getName() + " strikes " + defender.getName() + "!\n");
                EventBus.postMessage(EventType.TookDamage, aggressor.getName() + " strikes " + defender.getName() + "!");
            } else if (difference < 0) {        // Creature got the higher roll
                aggressor.modifyHealth(difference);
                logger.info("================== " + defender.getName() + " strikes " + aggressor.getName() + "!\n");
                EventBus.postMessage(EventType.TookDamage, defender.getName() + " strikes " + aggressor.getName() + "!");
            } else {
                logger.info("================== " + defender.getName() + " and " + aggressor.getName() + " can't seem to hit one another!\n");
                EventBus.postMessage(EventType.TookDamage, defender.getName() + " and " + aggressor.getName() + " can't seem to hit one another!");
            }
            aggressor.hasFoughtCache.add(defender);
            defender.hasFoughtCache.add(aggressor);
            aggressor.turns += 1;
            defender.turns += 1;
            return true;
        }
        return false;
    }

    public Room[] getAdjacentRooms() {
        return adjacentRooms.clone();
    }

    private List<Entity> getEntitiesContainingRequest(Map<Entity, List<EntityRequest>> allRequests, List<EntityRequest> desiredRequests, Alignment alignment) {
        List<Entity> entities = new ArrayList<>();
        for (Map.Entry<Entity, List<EntityRequest>> entry : allRequests.entrySet()) {
            Entity entity = entry.getKey();
            if (entity.alignment != alignment) {
                continue;
            } else if (!Collections.disjoint(entry.getValue(), desiredRequests)) {
                entities.add(entity);
            }
        }
        return entities;
    }


    // Chooses the entity from a list most fitting the health criteria (lowest or highest health)
    private enum HealthCriteria { LOWEST, HIGHEST }
    private Entity selectEntityMeetingHealthCriteria(List<Entity> entities, HealthCriteria criteria) {
        Entity bestFittingEntity = null;
        for(Entity entity : entities) {
            if (bestFittingEntity == null) {
                bestFittingEntity = entity;
            }
            else if(criteria == HealthCriteria.LOWEST && bestFittingEntity.getHealth() > entity.getHealth()) {
                bestFittingEntity = entity;
            }
            else if(criteria == HealthCriteria.HIGHEST && bestFittingEntity.getHealth() < entity.getHealth()) {
                bestFittingEntity = entity;
            }
        }
        return bestFittingEntity;
    }

    public void resetEntityTurns() {
        for(Entity entity : presentEntities) {
            entity.resetTurnState();
        }
    }
    // Runs a turn in a room.
    public void runTurn() throws IOException, InterruptedException {
        boolean stopAttemptsToLeave = false;
        boolean stopAttemptsToEat = false;
        Map<Entity, List<EntityRequest>> allRequests = new HashMap<>();

        // For all entities in this room,
        for(Entity entity : presentEntities) {
            // Clear previous turn state
            if(entity.getHealth() <= 0) {
                continue;
            }
            // Get requests from this entity
            List<EntityRequest> requests = entity.getRequests();

            // Map this entity and its requests into allRequests
            allRequests.put(entity, requests);

            // Process any high priority requests
            for(EntityRequest request : requests) {
                stopAttemptsToLeave |= (request == EntityRequest.stopEverythingFromLeaving);
                stopAttemptsToEat |= (request == EntityRequest.stopEverythingFromEating);
            }
        }

        // For all entries in allRequests,
        for(Map.Entry<Entity, List<EntityRequest>> entry : allRequests.entrySet()) {
            // Process its requests. Only process one successful request per entity.
            Entity entity = entry.getKey();

            // Break to this label when an action has been successfully completed; otherwise, break normally to progress to the next request.
            outer_jump:
            for(EntityRequest request : entry.getValue()) {
                // This is only a temporary fix. Other entities will still see dead entities and, for example, try to beat their corpse to death.
                if(entity.isDead()) { continue; }

                // Handle request
                switch(request) {

                    case stopEverythingFromEating:
                    case stopEverythingFromLeaving:
                        break;

                    case willingToEatSomething:
                        // Good-aligned adventurers who aren't pigs will spare the food in the room for the one who needs it most
                        if(entity.alignment == Alignment.GOOD && selectEntityMeetingHealthCriteria(getEntitiesOfAlignment(Alignment.GOOD), HealthCriteria.LOWEST) != entity) {
                            break;
                        }
                    case wantsToEatSomething:
                        if(entity.turns > 0) { break; }
                        // Only eat food if food exists, there isn't something stopping the entity from eating, and if the entity isn't full health
                        if(!presentFoods.isEmpty() && !stopAttemptsToEat && (entity.getHealth() < entity.getMaxHealth())) {
                            entity.modifyHealth(1);
                            logger.info("============== " + entity.getName() + " ate a/some " + presentFoods.get(0).getName() + ".\n");
                            EventBus.postMessage(EventType.AteSomething, entity.getName() + " ate a/some " + presentFoods.get(0).getName() + ".");
                            presentFoods.remove(0);
                            break outer_jump;
                        }
                        break;

                    case willingToFightSomething:
                        // Good-aligned adventurers who need not necessarily fight only willingly attempt to do so using the single strongest among their alignment
                        if(     entity.alignment == Alignment.GOOD &&
                                entity != selectEntityMeetingHealthCriteria(
                                    getEntitiesContainingRequest(
                                            allRequests,
                                            Arrays.asList(
                                                    EntityRequest.wantsToFightSomething,
                                                    EntityRequest.willingToEatSomething,
                                                    EntityRequest.wantsToExplore
                                            ),
                                            Alignment.GOOD
                                            ),
                                    HealthCriteria.HIGHEST)
                        ) {
                            break;
                        }
                    case wantsToFightSomething:
                    case wantsToFightEverything:
                        if(entity.turns > 0 && request != EntityRequest.wantsToFightEverything) { break; }
                        List<Entity> entitiesToFight = getEntitiesOfAlignment(flipAlignment(entity.alignment));
                        boolean foughtSomething = false;
                        for(Entity entityToFight : entitiesToFight) {
                            foughtSomething |= runBattle(entity, entityToFight, false);

                            // If the entity isn't trying to fight everything, break out after a fight happens
                            if(!(request == EntityRequest.wantsToFightEverything) && foughtSomething) {
                                break outer_jump;
                            }
                        }
                        if(request == EntityRequest.wantsToFightEverything && foughtSomething) {
                            break outer_jump;
                        }
                        break;

                    case wantsToRun:
                        if(countEntitiesOfAlignment(flipAlignment(entity.alignment)) > 0 && !stopAttemptsToLeave) {
                            entity.modifyHealth(-0.5);  // All cowardly entities take damage >:(
                            logger.info("============== " + entity.getName() + " ran in cowardice!\n");
                            EventBus.postMessage(EventType.Ran, entity.getName() + " ran in cowardice!");
                            entity.turns++;
                            moveEntityToRandAdjacentRoom(entity);
                            break outer_jump;
                        }
                        break;

                    case wantsToExplore:
                        if(entity.turns > 0) { break outer_jump; }
                        if(!stopAttemptsToLeave) {
                            logger.info("============== " + entity.getName() + " goes to explore another room.\n");
                            EventBus.postMessage(EventType.Exploring, entity.getName() + " goes to explore another room.\n");
                            entity.turns++;
                            moveEntityToRandAdjacentRoom(entity);
                            break outer_jump;
                        }
                        break;

                    default:
                        logger.info("Error: Unimplemented runTurn EntityRequest handle encountered!");
                        EventBus.postMessage(EventType.GameError, "Unimplemented runTurn EntityRequest handle encountered!");
                        break;
                }
            }
        }

        List<Entity> deadEntities = new ArrayList<>();
        for(Entity entity : presentEntities){
            if(entity.isDead()){
                deadEntities.add(entity);
                Random rand = new Random();
                int choice = rand.nextInt(6);
                switch(choice) {
                    case 0:
                        logger.info("================== " + entity.getName() + " died...\n");
                        break;
                    case 1:
                        logger.info("================== " + entity.getName() + ", covered in blood, could keep going no longer...\n");
                        break;
                    case 2:
                        logger.info("================== " + entity.getName() + " has died, but will live in legend for centuries to come...\n");
                        break;
                    case 3:
                        logger.info("================== " + entity.getName() + " passed away...\n");
                        break;
                    case 4:
                        logger.info("================== " + entity.getName() + " has died...\n");
                        break;
                    case 5:
                        logger.info("================== " + entity.getName() + " perished...\n");
                        break;
                }
                EventBus.postMessage(EventType.Death, entity.getName() + " dies.");
            }
        }
        presentEntities.removeAll(deadEntities);
    }


    public void logState() {
        logger.info("==== " + name +":\n");
        logger.info("========== Adventurers: ");
        boolean hasPrinted = false;
        for (Entity entity: getEntitiesOfAlignment(Alignment.GOOD)){
            entity.logState();
        }
        logger.info("\n========== Creatures: ");
        for (Entity entity: getEntitiesOfAlignment(Alignment.EVIL)){
            entity.logState();
        }
        logger.info("\n========== Food: ");
        for(Food food : presentFoods) {
            food.logState();
        }
        logger.info("\n");
    }
}
