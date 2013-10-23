package org.neo4j.driver.internal.http;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Result;
import org.neo4j.driver.Type;

class HttpResult implements Result
{
    private final Iterator<Map<String, Object>> rows;
    private Map<String, Object> row = null;
    private final Collection<String> columns;

    public HttpResult( Map<String, Object> response )
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

}
