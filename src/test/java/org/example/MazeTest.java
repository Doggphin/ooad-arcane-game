package org.example;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MazeTest {
    @Test
    public void TestMazeClass() throws IOException, InterruptedException {
        FoodFactory foodFactoryTest = new FoodFactory();
        EntityFactory entityFactoryTest = new EntityFactory();
        Maze mazeTest = new MazeBuilder(entityFactoryTest,foodFactoryTest)
                .addRooms(3,3)
                .placeFoodInRoom(new Food("test"), 0, 0)
                .addGluttons(   new String[]{"Joe \"Eater of Worlds\" Stuffer"})
                .addKnights(    new String[]{"\"The Chosen One\""})
                .addCowards(    new String[]{"Bartholamew \"The Destroyer\" Soulhunter"})
                .addAdventurers(new String[]{"Abraham the Vanquisher", "Sam the Lightbringer", "Merlin the Dunce"})
                .addFoods(      new String[]{"Pizza", "Glass", "Used Tissue", "\"Food\"", "Health Potion"})
                .addCreatures(  new String[]{"Jose", "Snowball", "Gary"})
                .addDemons(     new String[]{"Horace"})
                .setSequential(false)
                .build();


        int[] size = mazeTest.getMapSize();
        assertEquals(size[0], 3);
        assertEquals(size[1], 3);

        mazeTest.placeEntity(new Demon("Bluey", Alignment.EVIL));
        assert(mazeTest.getGlobalAmountOfEntity(Alignment.EVIL) == 5);
        assert(mazeTest.getNeighborsOf("Nonexistent room") == null);
        assert(mazeTest.getContents("Nonexistent room") == null);
        assert(mazeTest.getRooms().size() == 9);
        assert(mazeTest.getRoomByName("Room 1") != null);
        Arcane arcaneTest = new Arcane(mazeTest);
        arcaneTest.play();
    }
}
