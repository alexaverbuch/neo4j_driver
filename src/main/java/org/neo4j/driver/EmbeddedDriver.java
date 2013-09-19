package org.neo4j.driver;

import java.util.Iterator;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;

public class EmbeddedDriver implements Driver
{
    private final GraphDatabaseService db;
    private final ExecutionEngine engine;

    public static void main( String[] args )
    {
        // TODO
        GraphDatabaseService db = null;
        Driver driver = new EmbeddedDriver( db );
        try (Transaction tx = driver.newTransaction())
        {
            try (Result r = tx.execute( "" ))
            {
                while ( r.hasNext() )
                {
                    r.next();
                }
            }
            tx.success();
        }

    }

    public EmbeddedDriver( GraphDatabaseService db )
    {
        this.db = db;
        this.engine = new ExecutionEngine( db );
    }

    @Override
    public Transaction newTransaction()
    {
        return new EmbeddedTransaction( db.beginTx(), engine );
    }

    static class EmbeddedTransaction implements Transaction
    {
        private final org.neo4j.graphdb.Transaction innerTx;
        private final ExecutionEngine engine;

        public EmbeddedTransaction( org.neo4j.graphdb.Transaction innerTx, ExecutionEngine engine )
        {
            this.innerTx = innerTx;
            this.engine = engine;
        }

        @Override
        public Result execute( String query )
        {
            return new EmbeddedResult( engine.execute( query ) );
        }

        @Override
        public void success()
        {
            innerTx.success();
        }

        @Override
        public void close()
        {
            // TODO like that
            innerTx.close();
        }
    }

    static class EmbeddedResult implements Result
    {
        private final Iterator<Map<String, Object>> innerResult;

        public EmbeddedResult( ExecutionResult innerResult )
        {
            this.innerResult = innerResult.iterator();
        }

        @Override
        public void close()
        {
            // Exhaust result
            while ( innerResult.hasNext() )
            {
                innerResult.next();
            }
        }

        @Override
        public boolean hasNext()
        {
            return innerResult.hasNext();
        }

        @Override
        public Map<String, Object> next()
        {
            return innerResult.next();
        }

        @Override
        public void remove()
        {
        }
    }
}
