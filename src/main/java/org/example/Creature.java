package org.example;
import java.util.Arrays;
import java.util.List;

public class Creature extends Entity {

    public Creature(String name, Alignment alignment) {
        super(name, alignment, 5.0);
    }

    @Override public List<EntityRequest> getRequests() {
        return Arrays.asList(
                EntityRequest.willingToFightSomething
        );
    }
    @Override public String getEntityStringInfo() {
        return getEntityStringInfo("Creature");
    }
}
