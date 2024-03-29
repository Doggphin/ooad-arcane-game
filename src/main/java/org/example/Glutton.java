package org.example;
import java.util.Arrays;
import java.util.List;

public class Glutton extends Entity {

    public Glutton(String name, Alignment alignment) {
        super(name, alignment, 5.0);
    }

    @Override public List<EntityRequest> getRequests() {
        return Arrays.asList(
                EntityRequest.willingToEatSomething,
                EntityRequest.wantsToExplore
        );
    }
    @Override public String getEntityStringInfo() {
        return getEntityStringInfo("Glutton");
    }
}

