### Fortunify

Libaries required (build file follows):
 * Apache Commons Exec (org.apache.commons:commons-exec:1.3)
 * Apache Commons IO 2.5 (org.apache.commons:commons-io:2.5)
 * Gson 2.8.0 (com.google.code.gson:gson:2.8.0)
 * Twitter4J 4.0.6 (org.twitter4j:twitter4j:4.0.6)
 
Token required to access the Twitter API are looked up under [user.home]/twitter.properties. See [TwitterFortuneDispatcher] (https://github.com/yocaba/fortunify/blob/master/src/de/fortunify/impl/TwitterFortuneDispatcher.java) for keys expected (file format: key=value).
