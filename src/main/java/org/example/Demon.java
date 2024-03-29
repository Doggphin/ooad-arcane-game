package org.example;
import java.util.Arrays;
import java.util.List;

public class Demon extends Entity {

    public Demon(String name, Alignment alignment) {
        super(name, alignment, 15.0);
    }

    @Override public List<EntityRequest> getRequests() {
        return Arrays.asList(
                EntityRequest.stopEverythingFromLeaving,
                EntityRequest.stopEverythingFromEating,
                EntityRequest.wantsToFightEverything
        );
    }
    @Override public String getEntityStringInfo() {
        return getEntityStringInfo("Demon");
    }
}
