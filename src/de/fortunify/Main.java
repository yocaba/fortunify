package de.fortunify;

import java.io.IOException;

import de.fortunify.api.FortuneDispatcherFactory;
import de.fortunify.api.FortuneGeneratorFactory;
import de.fortunify.api.FortuneSink;
import de.fortunify.api.FortuneSource;
import de.fortunify.spi.FortuneDispatcher;
import de.fortunify.spi.FortuneGenerator;

public class Main {

    private static final int MAX_CHARS_TWITTER = 140;

    public static void main(String[] args) {

        // TODO pass source and sink instead of hard code them here

        FortuneGenerator fortuneGenerator = FortuneGeneratorFactory.createFortuneGenerator(FortuneSource.Spotify);
        FortuneDispatcher fortuneDispatcher = FortuneDispatcherFactory.createFortuneGenerator(FortuneSink.Twitter);

        try {
            String fortune = fortuneGenerator.generateFortune(MAX_CHARS_TWITTER);
            System.out.println("Fortune: " + fortune);
            fortuneDispatcher.dispatchFortune(fortune);
        } catch (IOException e) {
            System.err.println("Fortunify failed");
            e.printStackTrace();
            System.exit(1);
        }

    }

}
