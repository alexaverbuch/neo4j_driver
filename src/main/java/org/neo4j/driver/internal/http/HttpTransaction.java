package org.neo4j.driver.internal.http;

import java.util.Arrays;
import java.util.Map;

import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.test.server.HTTP;

class HttpTransaction implements Transaction
{
    private final HTTP.Builder http;
    private int txId = -1;
    private boolean success = false;

    public HttpTransaction( HTTP.Builder http )
    {
        this.http = http;
    }

    @Override
    public Result execute( String query )
    {
        return execute( query, MapUtil.map() );
    }

    @Override
    public Result execute( String query, Map<String, Object> params )
    {
        if ( txId == -1 )
        {
            // TODO
            System.out.println( MapUtil.map( "statements",
                    Arrays.asList( MapUtil.map( "statement", query, "parameters", params ) ).toString() ) );

            HTTP.Response response = http.POST(
                    "/db/data/transaction",
                    MapUtil.map( "statements",
                            Arrays.asList( MapUtil.map( "statement", query, "parameters", params ) ) ) );

            // TODO
            System.out.println( response.rawContent() );

            String[] parts = response.location().split( "\\/" );
            txId = Integer.parseInt( parts[parts.length - 1] );
            return new HttpResult( response.<Map<String, Object>>content() );
        }
        else
        {
            HTTP.Response response = http.POST(
                    "/db/data/transaction/" + txId,
                    MapUtil.map( "statements",
                            Arrays.asList( MapUtil.map( "statement", query, "parameters", params ) ) ) );
            return new HttpResult( response.<Map<String, Object>>content() );
        }
    }

    @Override
    public void success()
    {
        success = true;
    }

    @Override
    public void close()
    {
        if ( txId != -1 )
        {
            if ( success )
            {
                if ( http.POST( "/db/data/transaction/" + txId + "/commit" ).status() != 200 )
                {
                    throw new RuntimeException( "Unable to commit." );
                }
            }
            else
            {
                if ( http.DELETE( "/db/data/transaction/" + txId ).status() != 200 )
                {
                    throw new RuntimeException( "Unable to roll back." );
                }
            }
        }
    }
}
