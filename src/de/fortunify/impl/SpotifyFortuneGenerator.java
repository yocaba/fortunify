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

import de.fortunify.spi.FortuneGenerator;

public class SpotifyFortuneGenerator implements FortuneGenerator {

    private static final String URL_SCHEME = "https://";

    // TODO make path configurable
    private static final String FORTUNE_CMD = "/usr/games/fortune";
    
    private static final int MAX_RETRIES = 10;

    private static final Random INT_GENERATOR = new Random();

    @Override
    public String generateFortune(int maxLength) throws IOException {
        String fortune;
        int retryCount = 0;
        do {
            int resultCount;
            String keyword;
            do {
                keyword = getRandomKeyword();
                resultCount = getTrackCountForKeyword(keyword);
            } while (resultCount == 0 && retryCount++ <= MAX_RETRIES);

            String trackId = getRandomTrackId(keyword, resultCount);
            fortune = getTrackFortune(trackId);
        } while (isLongerAsMaxLength(fortune, maxLength) && retryCount++ <= MAX_RETRIES);
        
        if (retryCount > MAX_RETRIES) {
            throw new IOException("Failed to generate fortune; maximum retries exceeded: " + MAX_RETRIES);
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

    private int getTrackCountForKeyword(String keyword) throws IOException {
        String url = "https://api.spotify.com/v1/search?limit=1&type=track&q=" + keyword;
        String json = requestSpotifyApi(url);
        return SpotifyJsonResponseParser.getTrackCount(json);
    }

    private String getRandomTrackId(String keyword, int resultCount) throws IOException {
        int offset = INT_GENERATOR.nextInt(Math.min(100000, resultCount));
        String url = "https://api.spotify.com/v1/search?limit=1&type=track&q=" + keyword + "&offset=" + offset;
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

    private String getRandomKeyword() throws ExecuteException, IOException {
        String randomWord = "";
        boolean done = false;
        while (!done) {
            String line = getAdageFromFortuneApp();
            String[] lines = line.split("\\s"); // split by whitespace
            int maxCount = lines.length;
            int count = 0;
            while ((randomWord.isEmpty() || !randomWord.matches("[a-zA-Z]*")) && count <= maxCount) {
                randomWord = lines[INT_GENERATOR.nextInt(lines.length)];
                count++;
            }
            if (count <= maxCount) {
                done = true;
            }
        }
        return randomWord;
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
                return IOUtils.toString(outputStream.toInputStream(), "UTF-8");
            } else {
                throw new ExecuteException(
                        "Failed to execute program 'fortune' that is required by Fortunify; unexpected exit code",
                        exitCode);
            }
        }
    }

}
