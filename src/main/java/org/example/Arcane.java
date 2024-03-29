package org.example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import csci.ooad.layout.IMaze;
import csci.ooad.layout.MazeObserver;

import java.io.IOException;

public class Arcane {
    private static final Logger logger = LoggerFactory.getLogger("csci.ooad.arcane.Arcane");
    Maze maze;
    int turnCounter;
    boolean gameIsOver = false;

    private static Arcane instance;
    public static Arcane getInstance() {
        return instance;
    }

    public Arcane(Maze maze) {
        this.maze = maze;
        if(instance == null) {
            instance = this;
        }
    }

    private UIObserver tts = new UIObserver();
    private MazeObserver mazeDrawer = MazeObserver.getNewBuilder("Arcane Game")
            .useRadialLayoutStrategy()
            .setDelayInSecondsAfterUpdate(1)
            .build();

    public void render(String statusMessage) {
        mazeDrawer.update((IMaze)maze, statusMessage);
        mazeDrawer.paintToFile("test");
    }

    public void play() throws IOException, InterruptedException {
        turnCounter = 0;
        logger.info("The game begins.");
        EventBus.postMessage(EventType.GameStart, "The game begins.");
        while(!gameIsOver && turnCounter < 100) {
            logger.info("\nARCANE MAZE: turn " + turnCounter + "\n");
            EventBus.postMessage(EventType.TurnStart, "Turn " + String.valueOf(turnCounter + 1) + " begins!");
            maze.logState();
            turnCounter++;
            maze.runTurns();
            gameIsOver = maze.getGlobalAmountOfEntity(Alignment.EVIL) == 0 || maze.getGlobalAmountOfEntity(Alignment.GOOD) == 0;
            //EventBus.postMessage(EventType.TurnEnd, "");
        }
        if(maze.getGlobalAmountOfEntity(Alignment.EVIL) == 0) {
            //drawGameState("Normal");
            logger.info("Yippee, the adventurers won!");
            EventBus.postMessage(EventType.GameOver, "Yippee, the adventurers won!");
        } else if(maze.getGlobalAmountOfEntity(Alignment.GOOD) == 0) {
            //drawToScreen("Normal");
            logger.info("Boo, the creatures won!");
            EventBus.postMessage(EventType.GameOver, "Boo, the creatures won!");
        } else {
            logger.info("100 turns have passed and the maze collapses- nobody wins!");
            EventBus.postMessage(EventType.GameOver, "100 turns have passed and the maze collapses- nobody wins!");
        }
    }
}