package org.neo4j.driver.internal.embedded;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.helpers.collection.MapUtil;

class EmbeddedTransaction implements Transaction
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
