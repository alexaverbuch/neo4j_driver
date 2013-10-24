package org.neo4j.driver;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.neo4j.driver.exceptions.ClientException;
import org.neo4j.driver.exceptions.DriverExceptionType;
import org.neo4j.driver.spi.SessionProvider;
import org.neo4j.helpers.Service;

import static java.lang.String.format;

/**
 * This is your entry point for starting sessions with the database. Use the {@link #newSession(String)} methods in
 * this class to begin.
 *
 * Example:
 *
 * <code>
 * Session myRemoteSession   = Driver.newSession("http://localhost:7474");
 * Session myEmbeddedSession = Driver.newSession("file:///tmp/mydb");
 * </code>
 */
public class Driver
{
    /**
     * Create a new session.
     */
    public static Session newSession( String url )
    {
        return newSession( url, new HashMap<String, String>() );
    }

    /**
     * Create a new session, with configuration.
     */
    public static Session newSession( String url, Map<String, String> configuration )
    {
        URI uri = URI.create( url );
        return INSTANCE.provider( uri ).newSession( uri, configuration );
    }

    /**
     * Register a new session provider.
     */
    public static void registerProvider( SessionProvider provider )
    {
        INSTANCE.addProvider( provider );
    }

    private static Driver INSTANCE = new Driver();
    private List<SessionProvider> providers = new CopyOnWriteArrayList<>();

    private Driver()
    {
        for ( SessionProvider provider : Service.load( SessionProvider.class ) )
        {
            addProvider( provider );
        }
    }

    private SessionProvider provider( URI url)
    {
        for ( SessionProvider provider : providers )
        {
            if(provider.supportsProtocol( url.getScheme() ))
            {
                return provider;
            }
        }

        throw new ClientException( DriverExceptionType.CLIENT_UNKNOWN_CONNECTION_PROTOCOL,
                format( "There is no session provider available to connect via '%s' to '%s'. Make sure you've spelled" +
                        " the protocol correctly, and that the appropriate driver is on your classpath.",
                        url.getScheme(), url.toASCIIString()));
    }

    private void addProvider(SessionProvider provider)
    {
        providers.add( provider );
    }
}
