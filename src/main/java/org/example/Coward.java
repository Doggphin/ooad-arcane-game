package org.example;
import java.util.Arrays;
import java.util.List;

public class Coward extends Entity {

    public Coward(String name, Alignment alignment) {
        super(name, alignment, 5.0);
    }

    @Override public List<EntityRequest> getRequests() {
        return Arrays.asList(
                EntityRequest.wantsToRun,
                EntityRequest.willingToEatSomething,
                EntityRequest.wantsToExplore
        );
    }
    @Override public String getEntityStringInfo() {
        return getEntityStringInfo("Coward");
    }
}
