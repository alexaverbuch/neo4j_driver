package org.neo4j.driver;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.test.server.HTTP;

class RemoteSession implements Session
{
    private final HTTP.Builder http;

    public RemoteSession( String uri )
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
                return new RemoteResult( response.<Map<String, Object>>content() );
            }
            else
            {
                HTTP.Response response = http.POST(
                        "/db/data/transaction/" + txId,
                        MapUtil.map( "statements",
                                Arrays.asList( MapUtil.map( "statement", query, "parameters", params ) ) ) );
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
        private Map<String, Object> row = null;
        private final Collection<String> columns;

        public RemoteResult( Map<String, Object> response )
        {
            Map<String, Object> result = ( (List<Map<String, Object>>) response.get( "results" ) ).get( 0 );
            this.columns = (Collection<String>) result.get( "columns" );
            this.rows = new RowIterator( ( (Iterable<Map<String, Object>>) result.get( "data" ) ).iterator(), columns );
        }

        @Override
        public void close()
        {

        }

        @Override
        public boolean next()
        {
            if ( rows.hasNext() )
            {
                row = rows.next();
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

    static class RowIterator implements Iterator<Map<String, Object>>
    {
        private final Iterator<Map<String, Object>> rows;
        private final Collection<String> columns;

        private RowIterator( Iterator<Map<String, Object>> rows, Collection<String> columns )
        {
            this.rows = rows;
            this.columns = columns;
        }

        @Override
        public boolean hasNext()
        {
            return rows.hasNext();
        }

        @Override
        public Map<String, Object> next()
        {
            List<Object> rowData = (List<Object>) rows.next().get( "row" );
            Map<String, Object> row = new HashMap<String, Object>();
            int i = 0;
            for ( String column : this.columns )
            {
                row.put( column, rowData.get( i ) );
                i++;
            }
            return row;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

    }
}
