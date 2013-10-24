/*
 * Copyright (C) 2012 Neo Technology
 * All rights reserved
 */
package org.neo4j.driver.spi;

import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.neo4j.driver.Session;

/**
 * Used by the {@link org.neo4j.driver.Driver} to create sessions for various connection schemes. If you implement
 * a new connection scheme, the session factory is how you plug it in to make it available in the
 * {@link org.neo4j.driver.Driver}.
 *
 * Session providers are expected to be thread safe, and to allow multiple connections per database, or to fail
 * gracefully if this is not allowed.
 */
public interface SessionProvider
{
    boolean supportsProtocol( String protocol );

    Session newSession( URI uri, Map<String, String> configuration );
}
