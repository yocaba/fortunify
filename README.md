### Fortunify

Similar to the 'fortune' console app (that "print[s] a random, hopefully interesting, adage"), Fortunify queries a random track from Spotify and tweets name, artist, and url.

Latest version of Fortunify posts 1 track/day to [@Fortunify] (https://twitter.com/fortunify).

#### Build

```gradle fatJar```

#### Twitter Access

Token required to post to a Twitter account are expected to be put into [user.home]/twitter.properties. See [TwitterFortuneDispatcher] (https://github.com/yocaba/fortunify/blob/master/src/de/fortunify/impl/TwitterFortuneDispatcher.java) for keys expected (file format: key=value).
