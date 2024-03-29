package org.example;

import java.io.IOException;

public interface IObserver {
    void update(String message) throws IOException, InterruptedException;
}
