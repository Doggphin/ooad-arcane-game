package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    protected static EventBus instance;
    public static EventBus getInstance() {
        if(instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    private Map<EventType, List<IObserver>> observerMap = new HashMap<>();
    private static final EventType[] allEvents = new EventType[]{
            EventType.AteSomething,
            EventType.Attacks,
            EventType.Death,
            EventType.Defends,
            EventType.Exploring,
            EventType.GameError,
            EventType.GameOver,
            EventType.GameStart,
            EventType.Ran,
            EventType.TookDamage,
            EventType.TurnEnd,
            EventType.TurnStart,
            EventType.NoDamage,
    };

    public static void attach(IObserver observer, EventType eventType) {
        if(eventType == EventType.AllTurnEvents) {
            for(EventType eventTypeIterator : allEvents) {
                attach(observer, eventTypeIterator);
            }
        }
        else {
            (getInstance().observerMap.computeIfAbsent(eventType, k -> new ArrayList<>())).add(observer);
        }
    }

    public static void postMessage(EventType eventType, String message) throws IOException, InterruptedException {
        List<IObserver> observers = getInstance().observerMap.get(eventType);
        if(observers != null) {
            for(IObserver observer : observers) {
                observer.update(message);
            }
        }
    }
}
