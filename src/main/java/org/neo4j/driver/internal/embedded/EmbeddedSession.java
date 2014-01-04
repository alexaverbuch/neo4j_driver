package org.neo4j.driver.internal.embedded;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedSession implements Session
{
    private final EmbeddedDatabaseReference dbReference;

    public EmbeddedSession( GraphDatabaseService db )
    {
        this(new EmbeddedDatabaseReference( db, new ExecutionEngine( db ) ));

        // If the database was injected from the outside, it is up to the caller to ensure it is turned off. Therefore,
        // we increment our reference count to the database by one, representing the outside caller, ensuring we wont
        // turn it off.
        dbReference.acquire();
    }

    public EmbeddedSession( EmbeddedDatabaseReference dbRef )
    {
        this.dbReference = dbRef;
    }

    @Override
    public Transaction newTransaction()
    {
        return new EmbeddedTransaction( dbReference.db().beginTx(), dbReference.executionEngine() );
    }

    @Override
    public void close()
    {
        dbReference.release();
    }

}
