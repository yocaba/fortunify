package de.fortunify.spi;

import java.io.IOException;

public interface FortuneDispatcher {

    void dispatchFortune(String fortune) throws IOException;
}
