### Fortunify

Similar to the 'fortune' console app (that "print[s] a random, hopefully interesting, adage"), Fortunify queries a random track from Spotify and tweets name, artist, and url.

Latest version of Fortunify posts 1 track/day to [@Fortunify] (https://twitter.com/fortunify).

#### Build

```gradle fatJar```

#### Requirements

##### Fortune Console App

Requires the fortune console app (for "random" keyword selection) and expect it to be installed at /etc/games/fortune.

##### Twitter Access

Token required to post to a Twitter account are expected to be put into [user.home]/twitter.properties. See [TwitterFortuneDispatcher] (https://github.com/yocaba/fortunify/blob/master/src/de/fortunify/impl/TwitterFortuneDispatcher.java) for keys expected (file format: key=value).

#### Constraints

Not runnable under Windows as fortune console app is used under the hood (see requirements above). 
