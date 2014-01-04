package org.neo4j.driver.internal.embedded;

import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedDatabaseReference
{
    private GraphDatabaseService db;
    private ExecutionEngine executionEngine;

    private AtomicLong referenceCounter = new AtomicLong( 1 );

    public EmbeddedDatabaseReference( GraphDatabaseService db, ExecutionEngine executionEngine )
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
