package org.example;
import java.io.IOException;


public class UIObserver implements IObserver{
    static int turnLength = 0;

    public UIObserver() {
        EventBus.attach((IObserver)this, EventType.AllTurnEvents);
    }

    public void update(String message) throws IOException, InterruptedException {
        if(message != null && !message.isEmpty()) {
            Runtime.getRuntime().exec("nircmd.exe speak text \"" + message.replace("\"", ",") + "\"");
            Arcane.getInstance().render(message);
            Thread.sleep(turnLength);
        }
    }
}
