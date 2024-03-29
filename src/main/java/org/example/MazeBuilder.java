package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeBuilder {
    private static final Logger logger = LoggerFactory.getLogger("csci.ooad.arcane.MazeBuilder");
    private Maze maze;
    private EntityFactory entityFactory;
    private FoodFactory foodFactory;

    private List<Entity> entities = new ArrayList<>();
    private List<Food> foods = new ArrayList<>();
    private Room[][] rooms;
    private boolean distributeSequentially = false;
    private int width;
    private int height;

    public MazeBuilder(EntityFactory entityFactory, FoodFactory foodFactory){
        this.foodFactory = foodFactory;
        this.entityFactory = entityFactory;
    }

    public MazeBuilder addRooms(int width, int height){

        rooms = new Room[width][height];

        int roomNumber = 0;
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                roomNumber++;
                rooms[x][y] = new Room("Room " + roomNumber);
            }
        }

        // Next, populate the adjacent rooms of each map with its neighbors
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                rooms[x][y].initialize(
                        x == 0 ? null : rooms[x-1][y],            // Get north
                        y == 0 ? null : rooms[x][y-1],            // Get east
                        x == width - 1 ? null : rooms[x+1][y],  // Get south
                        y == height - 1 ? null : rooms[x][y+1],  // Get west
                        y * width + x
                );
            }
        }
        this.width = width;
        this.height = height;

        return this;
    }

    public MazeBuilder placeEntityInRoom(Entity entity, int whereX, int whereY) {
        rooms[whereX][whereY].addEntity(entity);
        return this;
    }

    public MazeBuilder placeFoodInRoom(Food food, int whereX, int whereY) {
        rooms[whereX][whereY].addFood(food);
        return this;
    }

    public MazeBuilder addDemons(String[] names){
        for (String name: names){
            entities.add(entityFactory.createDemon(name));
        }
        return this;
    }

    public MazeBuilder addGluttons(String[] names){
        for (String name: names){
            entities.add(entityFactory.createGlutton(name));
        }
        return this;
    }

    public MazeBuilder addKnights(String[] names){
        for (String name: names){
            entities.add(entityFactory.createKnight(name));
        }
        return this;
    }

    public MazeBuilder addCowards(String[] names){
        for (String name: names){
            entities.add(entityFactory.createCoward(name));
        }
        return this;
    }

    public MazeBuilder addAdventurers(String[] names){
        for (String name: names){
            entities.add(entityFactory.createAdventurer(name));
        }
        return this;
    }

    public MazeBuilder addCreatures(String[] names){
        for (String name: names){
            entities.add(entityFactory.createCreature(name));
        }
        return this;
    }

    public MazeBuilder addFoods(String[] names){
        for (String name: names){
            foods.add(foodFactory.createFood(name));
        }
        return this;
    }

    public MazeBuilder setSequential(boolean set){
        distributeSequentially = set;
        return this;
    }

    public Maze build(){
        if (distributeSequentially) {
            List<Entity> goodEntities = new ArrayList<>();
            List<Entity> evilEntities = new ArrayList<>();
            for (Entity entity: entities){
                if (entity.alignment == Alignment.EVIL){
                    evilEntities.add(entity);
                }
                else{
                    goodEntities.add(entity);
                }
            }
            for (Room[] row: rooms){
                for (Room room: row){
                    if(goodEntities.size() > 0) {
                        room.addEntity(goodEntities.get(0));
                        goodEntities.remove(0);
                    }
                    else if(evilEntities.size() > 0) {
                        room.addEntity(evilEntities.get(0));
                        evilEntities.remove(0);
                    }
                    else if(foods.size() > 0){
                        room.addFood(foods.get(0));
                        foods.remove(0);
                    }
                }
            }
        }
        else{
            for (Entity entity: entities){
                Random rand = new Random();
                int randomColumn = rand.nextInt(width);
                int randomRow = rand.nextInt(height);
                rooms[randomRow][randomColumn].addEntity(entity);
            }

            for (Food food: foods){
                Random rand = new Random();
                int randomColumn = rand.nextInt(width);
                int randomRow = rand.nextInt(height);
                rooms[randomRow][randomColumn].addFood(food);
            }
        }
        return new Maze(rooms);
    }
}
