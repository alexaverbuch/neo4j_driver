package org.neo4j.driver.internal.http;

import java.net.URI;
import java.util.Map;

import org.neo4j.driver.Session;
import org.neo4j.driver.spi.SessionProvider;

public class HttpSessionProvider implements SessionProvider
{
    @Override
    public boolean supportsProtocol( String protocol )
    {
        return protocol.equals( "http" ) || protocol.equals("https");
    }

    @Override
    public Session newSession( URI uri, Map<String, String> configuration )
    {
        return new HttpSession( uri.toASCIIString() );
    }
}
