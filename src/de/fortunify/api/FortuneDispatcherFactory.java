package de.fortunify.api;

import de.fortunify.impl.TwitterFortuneDispatcher;
import de.fortunify.spi.FortuneDispatcher;

public final class FortuneDispatcherFactory {

    private FortuneDispatcherFactory() {}
    
    public static final FortuneDispatcher createFortuneGenerator(FortuneSink fortuneSink) {
        if (fortuneSink == FortuneSink.TWITTER) {
            return new TwitterFortuneDispatcher();
        } else {
            throw new IllegalArgumentException("Unknow fortune sink: " + fortuneSink);
        }
    }
}
