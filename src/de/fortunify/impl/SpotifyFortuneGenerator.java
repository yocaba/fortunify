package de.fortunify.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fortunify.spi.FortuneGenerator;

public class SpotifyFortuneGenerator implements FortuneGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyFortuneGenerator.class.getSimpleName());
    
    private static final String URL_SCHEME = "https://";

    // TODO make path configurable
    private static final String FORTUNE_CMD = "/usr/games/fortune";
    
    private static final int MAX_ATTEMPTS = 10;

    private static final Random INT_GENERATOR = new Random();

    @Override
    public String generateFortune(int maxLength) throws IOException {
        String fortune;
        int attemptCount = 0;
        do {
            int resultCount;
            String keyword;
            do {
                keyword = getRandomKeyword();
                resultCount = getResultCountForKeyword(keyword);
            } while (resultCount == 0 && attemptCount++ <= MAX_ATTEMPTS);

            String trackId = getRandomTrackId(keyword, resultCount);
            fortune = getTrackFortune(trackId);
            logger.info("Fortune: {} (attempt: {})", fortune, attemptCount);
        } while (isLongerAsMaxLength(fortune, maxLength) && attemptCount++ <= MAX_ATTEMPTS);
        
        if (attemptCount > MAX_ATTEMPTS) {
            throw new IOException("Failed to generate fortune; maximum retries exceeded: " + MAX_ATTEMPTS);
        }
        return fortune;
    }

    private boolean isLongerAsMaxLength(String fortune, int maxLength) {
        if (fortune.contains(URL_SCHEME)) {
            return fortune.lastIndexOf(URL_SCHEME) > maxLength;
        } else {
            return fortune.length() > maxLength;
        }
    }

    private int getResultCountForKeyword(String keyword) throws IOException {
        String url = "https://api.spotify.com/v1/search?limit=1&type=track&q=" + keyword;
        String json = requestSpotifyApi(url);
        int resultCount = SpotifyJsonResponseParser.getTrackCount(json);
        logger.debug("Result count for keyword '{}': {}", keyword, resultCount);
        return resultCount;
    }

    private String getRandomTrackId(String keyword, int resultCount) throws IOException {
        int randomResultNumber = INT_GENERATOR.nextInt(Math.min(100000, resultCount));
        logger.debug("Result number picked (result count: {}; max: 100,000): {}", resultCount, randomResultNumber);
        String url = "https://api.spotify.com/v1/search?limit=1&type=track&q=" + keyword + "&offset=" + randomResultNumber;
        String json = requestSpotifyApi(url);
        return SpotifyJsonResponseParser.getTrackId(json);
    }

    private String getTrackFortune(String trackId) throws IOException {
        String url = "https://api.spotify.com/v1/tracks/" + trackId;
        String json = requestSpotifyApi(url);
        return SpotifyJsonResponseParser.getTrackFortune(json);
    }

    private String requestSpotifyApi(String url) throws IOException {
        HttpsURLConnection httpsConnection = (HttpsURLConnection) new URL(url).openConnection();
        httpsConnection.setRequestMethod("GET");
        httpsConnection.setRequestProperty("Accept", "application/json");

        int responseCode = httpsConnection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException(String.format(
                    "Unexpected response from Spotify; response code: %d; response message: %s; request url: %s",
                    responseCode, httpsConnection.getResponseMessage(), url));
        }
        // TODO extract charset from "application/json; charset=utf-8" retrieved
        // by httpsConnection.getContentType()
        return IOUtils.toString(httpsConnection.getInputStream(), "utf-8");
    }

    private String getRandomKeyword() throws IOException {
        String randomWord = "";
        int attemptCount1 = 0;
        boolean done = false;
        while (!done && attemptCount1 <= MAX_ATTEMPTS) {
            String adage = getAdageFromFortuneApp();
            String[] words = adage.split("\\s"); // split by whitespace
            int maxAttempt2 = words.length;
            int attemptCount2 = 0;
            while ((randomWord.isEmpty() || !randomWord.matches("[a-zA-Z]*")) && attemptCount2 <= maxAttempt2) {
                randomWord = words[INT_GENERATOR.nextInt(words.length)].replaceAll("[.!?,\\\"]", "");
                logger.debug("Word picked from adage '{}': {} (attempt: {}-{})", abbreviate(adage, 20), randomWord, attemptCount1, attemptCount2);
                attemptCount2++;
            }
            if (attemptCount2 <= maxAttempt2) {
                logger.info("Keyword picked from adage '{}...': {} (attempt: {})", abbreviate(adage, 20), randomWord, attemptCount1);
                done = true;
            }
            attemptCount1++;
        }
        return randomWord;
    }
    
    private String abbreviate(String str, int max) {
        if (str.length() > max) {
            str = str.substring(0, max) + "[..]";
        }
        return str;
    }

    private String getAdageFromFortuneApp() throws IOException {
        CommandLine cmdLine = CommandLine.parse(FORTUNE_CMD);
        DefaultExecutor executor = new DefaultExecutor();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            executor.setStreamHandler(streamHandler);
            int exitCode = executor.execute(cmdLine);
            if (exitCode == 0) {
                // TODO figure out encoding instead of assuming the most-likely
                String adage = IOUtils.toString(outputStream.toInputStream(), "UTF-8").trim();
                logger.info("Adage from fortune console app: '{}'", adage);
                return adage;
            } else {
                throw new ExecuteException("Failed to execute program 'fortune' that is required by Fortunify; unexpected exit code", exitCode);
            }
        }
    }

}
