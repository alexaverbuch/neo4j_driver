package org.neo4j.driver.internal.file;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.driver.Session;
import org.neo4j.driver.spi.SessionProvider;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class FileSessionProvider implements SessionProvider
{
    public static class DatabaseReference
    {
        private GraphDatabaseService db;
        private ExecutionEngine executionEngine;

        private AtomicLong referenceCounter = new AtomicLong( 1 );

        public DatabaseReference( GraphDatabaseService db, ExecutionEngine executionEngine )
        {
            this.db = db;
            this.executionEngine = executionEngine;
        }

        /**
         * Attempt to increment the reference count on this instance, giving us permission to use it.
         */
        public boolean acquire()
        {
            long references = referenceCounter.get();

            // If the reference count to this is == 0, then the database will have been closed
            while( references > 0)
            {
                // Try and aquire a reference
                if(referenceCounter.compareAndSet( references, references + 1 ))
                {
                    return true;
                }

                references = referenceCounter.get();
            }
            return false;
        }

        /**
         * Release a reference to this database, potentially turning it off.
         */
        public void release()
        {
            referenceCounter.decrementAndGet();
        }

        public GraphDatabaseService db()
        {
            return db;
        }

        public ExecutionEngine executionEngine()
        {
            return executionEngine;
        }
    }

    private final ConcurrentMap<URI, DatabaseReference> runningDatabases = new ConcurrentHashMap<>();

    @Override
    public boolean supportsProtocol( String protocol )
    {
        return protocol.equals( "file" );
    }

    @Override
    public Session newSession( URI uri, Map<String, String> configuration )
    {
        DatabaseReference dbRef = runningDatabases.get( uri );
        if(dbRef != null && dbRef.acquire())
        {
            return new FileSession( dbRef );
        }

        // No such database, or we lost a race pinning a reference to it
        return new FileSession( startNewDatabase( uri, configuration ) );
    }

    private synchronized DatabaseReference startNewDatabase( URI uri, Map<String, String> configuration )
    {
        GraphDatabaseService db = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder( uri.getPath() )
                .setConfig( configuration )
                .newGraphDatabase();
        DatabaseReference dbRef = new DatabaseReference( db, new ExecutionEngine( db ) );

        runningDatabases.put(uri, dbRef);

        return dbRef;
    }
}
