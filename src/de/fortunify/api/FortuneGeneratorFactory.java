package de.fortunify.api;

import de.fortunify.impl.SpotifyFortuneGenerator;
import de.fortunify.spi.FortuneGenerator;

public final class FortuneGeneratorFactory {

    private FortuneGeneratorFactory() {}
    
    public static final FortuneGenerator createFortuneGenerator(FortuneSource fortuneSource) {
        if (fortuneSource == FortuneSource.SPOTIFY) {
            return new SpotifyFortuneGenerator();
        } else {
            throw new IllegalArgumentException("Unknow fortune source: " + fortuneSource);
        }
    }
}
