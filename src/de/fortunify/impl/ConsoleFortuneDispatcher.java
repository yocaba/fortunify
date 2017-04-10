package de.fortunify.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fortunify.spi.FortuneDispatcher;

public class ConsoleFortuneDispatcher implements FortuneDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleFortuneDispatcher.class.getSimpleName());

    @Override
    public void dispatchFortune(String fortune) throws IOException {
        logger.info(fortune);
    }

}