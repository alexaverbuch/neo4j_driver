package org.neo4j.driver;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.helpers.collection.MapUtil;

public class EmbeddedSession implements Session
{
    private final GraphDatabaseService db;
    private final ExecutionEngine engine;

    public EmbeddedSession( GraphDatabaseService db )
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
            return execute( query, MapUtil.map() );
        }

        @Override
        public Result execute( String query, Map<String, Object> params )
        {
            return new EmbeddedResult( engine.execute( query, params ) );
        }

        @Override
        public void success()
        {
            innerTx.success();
        }

        @Override
        public void close()
        {
            innerTx.close();
        }
    }

    static class EmbeddedResult implements Result
    {
        private final ResourceIterator<Map<String, Object>> innerResult;
        private final Iterable<String> columns;
        private Map<String, Object> row = null;

        public EmbeddedResult( ExecutionResult innerResult )
        {
            this.innerResult = innerResult.iterator();
            this.columns = innerResult.columns();
        }

        @Override
        public void close()
        {
            innerResult.close();
        }

        @Override
        public boolean next()
        {
            if ( innerResult.hasNext() )
            {
                row = innerResult.next();
                return true;
            }
            else
            {
                return false;
            }
        }

        @Override
        public Iterable<String> columns()
        {
            return columns;
        }

        @Override
        public <T> T getValue( Type<T> type, String column )
        {
            return type.cast( row.get( column ) );
        }

        @Override
        public Map<String, Object> getRow()
        {
            return row;
        }
    }
}
