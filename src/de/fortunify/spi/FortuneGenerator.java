package de.fortunify.spi;

import java.io.IOException;

public interface FortuneGenerator {

    String generateFortune(int maxLength) throws IOException;
}
