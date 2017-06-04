package de.fortunify.impl;

import java.io.IOException;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SpotifyJsonResponseParser {

    private static final String JSON_ELEMENT_MISSING = "Invalid JSON; element missing: ";

    private static final String DEFAULT_ARTIST = "[Unknown]";

    private static final String FORTUNE_TRACK_FORMAT_URL = "\"%s\" -- %s %s";

    private static final String FORTUNE_TRACK_FORMAT = "\"%s\" -- %s";

    private static final String KEY_ARTISTS = "artists";

    private static final String KEY_EXTERNAL_URL = "external_urls";

    private static final String KEY_ITEMS = "items";

    private static final String KEY_NAME = "name";

    private static final String KEY_SPOTIFY = "spotify";

    private static final String KEY_TOTAL = "total";

    private static final String KEY_TRACKS = "tracks";

    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final Gson GSON = new Gson();

    private SpotifyJsonResponseParser() {}
    
    protected static int getTrackCount(String json) throws IOException {
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        if (!root.has(KEY_TRACKS)) {
            throw new IOException(JSON_ELEMENT_MISSING + KEY_TRACKS);
        }
        JsonObject tracks = root.getAsJsonObject(KEY_TRACKS);
        if (!tracks.has(KEY_TOTAL)) {
            throw new IOException(JSON_ELEMENT_MISSING + KEY_TOTAL);
        }
        return tracks.get(KEY_TOTAL).getAsInt();
    }

    protected static String getTrackId(String json) throws IOException {
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        if (!root.has(KEY_TRACKS)) {
            throw new IOException(JSON_ELEMENT_MISSING + KEY_TRACKS);
        }
        JsonObject tracks = root.getAsJsonObject(KEY_TRACKS);
        if (!tracks.has(KEY_ITEMS)) {
            throw new IOException(JSON_ELEMENT_MISSING + KEY_ITEMS);
        }
        JsonArray items = tracks.getAsJsonArray(KEY_ITEMS);
        if (items.size() > 0) {
            return ((JsonObject) items.get(0)).get("id").getAsString();
        } else {
            return null;
        }
    }

    protected static String getTrackFortune(String json) throws IOException {
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        if (!root.has(KEY_NAME)) {
            throw new IOException(JSON_ELEMENT_MISSING + KEY_NAME);
        }
        String name = root.get(KEY_NAME).getAsString();

        String artists = DEFAULT_ARTIST;
        if (root.has(KEY_ARTISTS)) {
            StringBuilder artistBuilder = new StringBuilder();
            Iterator<JsonElement> iterator = root.getAsJsonArray(KEY_ARTISTS).iterator();
            while (iterator.hasNext()) {
                artistBuilder.append(((JsonObject) iterator.next()).get(KEY_NAME).getAsString());
                artistBuilder.append(", ");
            }
            artistBuilder.delete(artistBuilder.length() - 2, artistBuilder.length());
            artists = artistBuilder.toString();
        }

        if (root.has(KEY_EXTERNAL_URL)) {
            JsonObject externalUrls = root.getAsJsonObject(KEY_EXTERNAL_URL);
            if (externalUrls.has(KEY_SPOTIFY)) {
                String url = externalUrls.get(KEY_SPOTIFY).getAsString();
                return String.format(FORTUNE_TRACK_FORMAT_URL, name, artists, url);
            }
        }
        return String.format(FORTUNE_TRACK_FORMAT, name, artists);
    }
    
    protected static String getAccessToken(String json) throws IOException {
        JsonObject root = GSON.fromJson(json, JsonObject.class);
        if (!root.has(KEY_ACCESS_TOKEN)) {
            throw new IOException(JSON_ELEMENT_MISSING + KEY_ACCESS_TOKEN);
        }
        return root.get(KEY_ACCESS_TOKEN).getAsString();
    }
}
