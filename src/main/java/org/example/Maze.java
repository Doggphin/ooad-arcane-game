package org.example;
import java.io.IOException;
import java.util.*;

import csci.ooad.layout.IMaze;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Maze implements IMaze {

    public Maze(Room[][] rooms){
        this.rooms = rooms;
    }

    private static final Logger logger = LoggerFactory.getLogger("csci.ooad.arcane.Arcane");
    private Room[][] rooms;       // [x][y]

    private ArrayList<Room> getRoomsAsArray() {
        ArrayList<Room> ret = new ArrayList<Room>();
        for(int y = 0; y < rooms.length; y++) {
            for(int x = 0; x < rooms[0].length; x++) {
                ret.add(rooms[x][y]);
            }
        }
        return ret;
    }

    private Room getRandomRoom() {
        ArrayList<Room> rooms = getRoomsAsArray();
        Random rand = new Random();
        return rooms.get(rand.nextInt(rooms.size()));
    }
    public void placeEntity(Entity entity) {
        getRandomRoom().addEntity(entity);
    }
    public void placeFood(Food food) {
        getRandomRoom().addFood(food);
    }

    public int getGlobalAmountOfEntity(Alignment alignment) {
        int ret = 0;
        for(Room room : getRoomsAsArray()) {
            ret += room.countEntitiesOfAlignment(alignment);
        }
        return ret;
    }

    public void runTurns() throws IOException, InterruptedException {
        for(Room room : getRoomsAsArray()) {
            room.resetEntityTurns();
        }
        for(Room room : getRoomsAsArray()) {
            room.runTurn();
        }
    }

    public int[] getMapSize() {
        if(rooms != null && rooms[0] != null){
            return new int[]{rooms.length, rooms[0].length};
        }
        throw new RuntimeException("WTF are you doing? Row or Column is Null!");
    }


    public void logState() {
        ArrayList<Room> roomsArray=  getRoomsAsArray();
        for(int i=0; i<roomsArray.size(); i++) {
            //logger.info("Room " + i + ":\n");
            roomsArray.get(i).logState();
        }
    }

    public Room getRoomByName(String roomName) {
        Room roomCache = null;
        for(Room room : getRoomsAsArray()) {
            if(Objects.equals(room.getName(), roomName)) {
                roomCache = room;
                break;
            }
        }
        //logger.info(roomName + " gave " + (roomCache == null ? "null" : roomCache.getName()) + "\n");
        return roomCache;
    }
    @Override
    public List<String> getRooms() {
        List<String> names = new ArrayList<>();
        for(Room room : getRoomsAsArray()) {
            names.add(room.getName());
        }
        return names;
    }

    @Override
    public List<String> getNeighborsOf(String roomName) {
        Room room = getRoomByName(roomName);
        if(room == null) {
            return null;
        } else {
            Room[] neighbors = room.getAdjacentRooms();
            List<String> neighborNames = new ArrayList<>();
            for(Room neighbor : neighbors) {
                if(neighbor != null) {
                    neighborNames.add(neighbor.getName());
                }
            }
            return neighborNames;
        }
    }

    @Override
    public List<String> getContents(String roomName) {
        Room room = getRoomByName(roomName);
        if(room == null) {
            return null;
        } else {
            List<String> contents = new ArrayList<>();
            for(Entity entity : room.getEntitiesOfAlignment(Alignment.GOOD)) {
                contents.add(entity.getEntityStringInfo());
            }
            for(Entity entity : room.getEntitiesOfAlignment(Alignment.EVIL)) {
                contents.add(entity.getEntityStringInfo());
            }
            for(Food food : room.getFoods()) {
                contents.add(food.getName());
            }
            return contents;
        }
    }
}
