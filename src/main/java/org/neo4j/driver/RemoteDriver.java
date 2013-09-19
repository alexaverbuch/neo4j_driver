package org.neo4j.driver;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.test.server.HTTP;

class RemoteDriver implements Driver
{
    private final HTTP.Builder http;

    public RemoteDriver( String uri )
    {
        this.http = HTTP.withBaseUri( uri.substring( 0, uri.length() - 1 ) );
    }

    @Override
    public Transaction newTransaction()
    {
        return new RemoteTransaction( http );
    }

    static class RemoteTransaction implements Transaction
    {
        private final HTTP.Builder http;
        private int txId = -1;
        private boolean success = false;

        public RemoteTransaction( HTTP.Builder http )
        {
            this.http = http;
        }

        @Override
        public Result execute( String query )
        {
            if ( txId == -1 )
            {
                HTTP.Response response = http.POST( "/db/data/transaction",
                        MapUtil.map( "statements", Arrays.asList( MapUtil.map( "statement", query ) ) ) );

                String[] parts = response.location().split( "\\/" );
                txId = Integer.parseInt( parts[parts.length - 1] );

                return new RemoteResult( response.<Map<String, Object>>content() );
            }
            else
            {
                HTTP.Response response = http.POST( "/db/data/transaction/" + txId,
                        MapUtil.map( "statements", Arrays.asList( MapUtil.map( "statement", query ) ) ) );
                return new RemoteResult( response.<Map<String, Object>>content() );
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

    static class RemoteResult implements Result
    {
        private final Iterator<Map<String, Object>> rows;

        public RemoteResult( Map<String, Object> data )
        {
            this.rows = ( (Iterable<Map<String, Object>>) ( (List<Map<String, Object>>) data.get( "results" ) ).get( 0 ).get(
                    "data" ) ).iterator();
        }

        @Override
        public void close()
        {

        }

        @Override
        public boolean hasNext()
        {
            return rows.hasNext();
        }

        @Override
        public Map<String, Object> next()
        {
            return rows.next();
        }

        @Override
        public void remove()
        {
        }
    }
}
