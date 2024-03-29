package org.example;
import java.util.Arrays;
import java.util.List;

public class Knight extends Entity {

    public Knight(String name, Alignment alignment) {
        super(name, alignment, 8.0);
    }

    @Override public List<EntityRequest> getRequests() {
        return Arrays.asList(
                EntityRequest.wantsToFightSomething,
                EntityRequest.willingToEatSomething,
                EntityRequest.wantsToExplore
        );
    }
    @Override public String getEntityStringInfo() {
        return getEntityStringInfo("Knight");
    }
}
