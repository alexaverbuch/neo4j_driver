package org.neo4j.driver.internal.embedded;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.driver.Session;
import org.neo4j.driver.spi.SessionProvider;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class FileSessionProvider implements SessionProvider
{
    private final ConcurrentMap<URI, EmbeddedDatabaseReference> runningDatabases = new ConcurrentHashMap<>();

    @Override
    public boolean supportsProtocol( String protocol )
    {
        return protocol.equals( "file" );
    }

    @Override
    public Session newSession( URI uri, Map<String, String> configuration )
    {
        EmbeddedDatabaseReference dbRef = runningDatabases.get( uri );
        if(dbRef != null && dbRef.acquire())
        {
            return new EmbeddedSession( dbRef );
        }

        // No such database, or we lost a race pinning a reference to it
        return new EmbeddedSession( startNewDatabase( uri, configuration ) );
    }

    private synchronized EmbeddedDatabaseReference startNewDatabase( URI uri, Map<String, String> configuration )
    {
        GraphDatabaseService db = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder( uri.getPath() )
                .setConfig( configuration )
                .newGraphDatabase();
        EmbeddedDatabaseReference dbRef = new EmbeddedDatabaseReference( db, new ExecutionEngine( db ) );

        runningDatabases.put(uri, dbRef);

        return dbRef;
    }
}
