package org.example;

import java.util.Arrays;
import java.util.List;

public class Adventurer extends Entity {

    public Adventurer(String name, Alignment alignment) {
        super(name, alignment, 5.0);
    }


    @Override public List<EntityRequest> getRequests() {
        return Arrays.asList(
                EntityRequest.willingToFightSomething,
                EntityRequest.willingToEatSomething,
                EntityRequest.wantsToExplore
        );
    }
    @Override public String getEntityStringInfo() {
        return getEntityStringInfo("Adventurer");
    }
}
