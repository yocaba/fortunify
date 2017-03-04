package de.fortunify.api;

import de.fortunify.impl.SpotifyFortuneGenerator;
import de.fortunify.spi.FortuneGenerator;

public final class FortuneGeneratorFactory {

    public static final FortuneGenerator createFortuneGenerator(FortuneSource fortuneSource) {
        switch (fortuneSource) {
        case Spotify:
            return new SpotifyFortuneGenerator();
        default:
            throw new IllegalArgumentException("Unknow fortune source: " + fortuneSource);
        }
    }
}
