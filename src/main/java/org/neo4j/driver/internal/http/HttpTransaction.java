package org.neo4j.driver.internal.http;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.test.server.HTTP;

class HttpTransaction implements Transaction
{
    private final HTTP.Builder http;
    private final HttpExceptionMapper exceptionMapper;
    private int txId = -1;
    private boolean success = false;

    public HttpTransaction( HTTP.Builder http, HttpExceptionMapper exceptionMapper )
    {
        this.http = http;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public Result execute( String query )
    {
        return execute( query, MapUtil.map() );
    }

    @Override
    public Result execute( String query, Map<String, Object> params )
    {
        HTTP.Response response;
        if ( txId == -1 )
        {
            response = http.POST(
                    "/db/data/transaction",
                    MapUtil.map( "statements",
                            Arrays.asList( MapUtil.map( "statement", query, "parameters", params ) ) ) );

            String[] parts = response.location().split( "\\/" );
            txId = Integer.parseInt( parts[parts.length - 1] );
        }
        else
        {
            response = http.POST(
                    "/db/data/transaction/" + txId,
                    MapUtil.map( "statements",
                            Arrays.asList( MapUtil.map( "statement", query, "parameters", params ) ) ) );
        }

        Map<String, Object> content = response.content();
        assertNoErrors((Collection<Map<String, Object>>)content.get("errors"));

        return new HttpResult( content );
    }

    private void assertNoErrors( Collection<Map<String, Object>> content )
    {
        // TODO: Don't just throw the first error, turn this into a multiple cause exception
        // TODO: Take into account that status codes may just be warnings or info, not necessarily an actual error.
        for ( Map<String, Object> errorContent : content )
        {
            String code = (String) errorContent.get( "code" );
            String message = (String) errorContent.get( "message" );
            throw exceptionMapper.map(code, message);
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
