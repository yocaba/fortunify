package de.fortunify;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fortunify.api.FortuneDispatcherFactory;
import de.fortunify.api.FortuneGeneratorFactory;
import de.fortunify.api.FortuneSink;
import de.fortunify.api.FortuneSource;
import de.fortunify.spi.FortuneDispatcher;
import de.fortunify.spi.FortuneGenerator;

public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getSimpleName());

    private static final int MAX_CHARS_TWITTER = 140;

    private Main() {}
    
    public static void main(String[] args) {

        // TODO pass source and sink instead of hard code them here

        FortuneGenerator fortuneGenerator = FortuneGeneratorFactory.createFortuneGenerator(FortuneSource.SPOTIFY);
        FortuneDispatcher fortuneDispatcher = FortuneDispatcherFactory.createFortuneGenerator(FortuneSink.TWITTER);

        try {
            fortuneDispatcher.dispatchFortune(fortuneGenerator.generateFortune(MAX_CHARS_TWITTER));
        } catch (IOException e) {
            logger.error("Fortunify failed", e);
            System.exit(1);
        }

    }

}
