package de.fortunify;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fortunify.api.FortuneDispatcherFactory;
import de.fortunify.api.FortuneGeneratorFactory;
import de.fortunify.api.FortuneSink;
import de.fortunify.api.FortuneSource;
import de.fortunify.spi.FortuneDispatcher;
import de.fortunify.spi.FortuneGenerator;

public class Main {

    private static final String ERROR_MESSAGE_ARGS = String.format("Two arguments expected: fortune source %s and fortune sink %s", Main.enumToLogMessage(FortuneSource.class), Main.enumToLogMessage(FortuneSink.class));
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getSimpleName());

    private static final String DELIMITER = ",";

    private static final int MAX_CHARS = 140;

    private Main() {}
    
    private static <E extends Enum<E>> String enumToLogMessage(Class<E> enumClass) {
        return Arrays.toString(enumClass.getEnumConstants()).toLowerCase().replaceAll(", ", "|");
    }

    /**
     * @param args
     *            fist argument depicts the fortune source, second argument
     *            depicts the fortune sink (comma-separated list)
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            args = new String[] { FortuneSource.SPOTIFY.name(), FortuneSink.CONSOLE.name() };
        } else if (args.length != 2) {
            if (args.length < 2) {
                logger.error(ERROR_MESSAGE_ARGS);
                System.exit(1);
            } else {
                logger.warn(ERROR_MESSAGE_ARGS);
            }
        }
        FortuneGenerator fortuneGenerator;
        try {
            fortuneGenerator = Main.createFortuneGenerator(args[0]);
        } catch (IOException e) {
            logger.error("Fortunify failed - " + e.getMessage());
            System.exit(1);
            return;
        }

        String fortune;
        try {
            fortune = fortuneGenerator.generateFortune(MAX_CHARS);
        } catch (IOException e) {
            logger.error("Fortunify failed - " + e.getMessage());
            System.exit(1);
            return;
        }

        for (FortuneDispatcher fortuneDispatcher : Main.createFortuneDispatchers(args[1])) {
            try {
                fortuneDispatcher.dispatchFortune(fortune);
            } catch (IOException e) {
                // provide details about the dispatcher that failed
                logger.error("Failed to dispatch fortune - " + e.getMessage());
            }
        }

    }

    private static FortuneGenerator createFortuneGenerator(String sourceArg) throws IOException {

        FortuneSource source;
        try {
            source = FortuneSource.valueOf(sourceArg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IOException("unknown fortune source: " + sourceArg);
        }
        return FortuneGeneratorFactory.createFortuneGenerator(source);
    }

    private static Set<FortuneDispatcher> createFortuneDispatchers(String sinkArg) {

        Set<FortuneDispatcher> fortuneDispatchers = new HashSet<>();
        for (String arg : sinkArg.split(DELIMITER)) {
            FortuneSink sink;
            try {
                sink = FortuneSink.valueOf(arg.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Failed to dispatch fortune - unknown fortune sink: " + arg);
                continue;
            }
            fortuneDispatchers.add(FortuneDispatcherFactory.createFortuneDispatcher(sink));
        }

        return fortuneDispatchers;
    }

}
