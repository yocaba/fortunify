package de.fortunify.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fortunify.spi.FortuneDispatcher;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterFortuneDispatcher implements FortuneDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(TwitterFortuneDispatcher.class.getSimpleName());
    
    private static final String TOKEN_FILE = System.getProperty("user.home") + "/token.properties";

    private static final String KEY_CONSUMER_TOKEN = "consumerToken";

    private static final String KEY_CONSUMER_SECRET = "consumerSecret";

    private static final String KEY_ACCESS_TOKEN = "accessToken";

    private static final String KEY_ACCESS_SECRET = "accessSecret";

    private static final TwitterFactory TWITTER_FACTORY = new TwitterFactory();

    @Override
    public void dispatchFortune(String fortune) throws IOException {
        Token token = loadToken();

        Twitter twitter = TWITTER_FACTORY.getInstance();
        twitter.setOAuthConsumer(token.consumerToken.consumerKey, token.consumerToken.consumerSecret);
        twitter.setOAuthAccessToken(token.accessToken);

        try {
            Status status = twitter.updateStatus(fortune);
            logger.info("Posted to account '{}': '{}'", twitter.getScreenName(), status.getText());
        } catch (TwitterException e) {
            throw new IOException("Failed to post tweet for 'Fortunify'", e);
        }
    }

    private Token loadToken() throws IOException {
        File propsFile = new File(TOKEN_FILE);
        if (!propsFile.exists()) {
            throw new IOException("Twitter properties file missing: " + TOKEN_FILE);
        }
        Properties props = new Properties();
        try (InputStream fileInputStream = FileUtils.openInputStream(new File(TOKEN_FILE))) {
            props.load(fileInputStream);
        }

        if (props.getProperty(KEY_CONSUMER_TOKEN) == null || props.getProperty(KEY_CONSUMER_SECRET) == null
                || props.getProperty(KEY_ACCESS_TOKEN) == null || props.getProperty(KEY_ACCESS_SECRET) == null) {
            // TODO specify props actually missing
            throw new IOException("Missing properties in Twitter properties file: " + TOKEN_FILE);
        }
        ConsumerToken consumerToken = new ConsumerToken(props.getProperty(KEY_CONSUMER_TOKEN),
                props.getProperty(KEY_CONSUMER_SECRET));
        AccessToken accessToken = new AccessToken(props.getProperty(KEY_ACCESS_TOKEN),
                props.getProperty(KEY_ACCESS_SECRET));
        return new Token(consumerToken, accessToken);
    }

    class ConsumerToken {

        private final String consumerKey;

        private final String consumerSecret;

        public ConsumerToken(String consumerKey, String consumerSecret) {
            this.consumerKey = consumerKey;
            this.consumerSecret = consumerSecret;
        }
    }

    class Token {
        private final ConsumerToken consumerToken;

        private final AccessToken accessToken;

        public Token(ConsumerToken consumerToken, AccessToken accessToken) {
            this.consumerToken = consumerToken;
            this.accessToken = accessToken;
        }
    }
}
