package de.fortunify.api;

import de.fortunify.impl.TwitterFortuneDispatcher;
import de.fortunify.spi.FortuneDispatcher;

public final class FortuneDispatcherFactory {

    public static final FortuneDispatcher createFortuneGenerator(FortuneSink fortuneSink) {
        switch (fortuneSink) {
        case Twitter:
            return new TwitterFortuneDispatcher();
        default:
            throw new IllegalArgumentException("Unknow fortune sink: " + fortuneSink);
        }
    }
}
