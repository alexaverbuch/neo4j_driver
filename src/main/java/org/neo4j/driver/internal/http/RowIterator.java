package org.neo4j.driver.internal.http;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class RowIterator implements Iterator<Map<String, Object>>
{
    private final Iterator<Map<String, Object>> rows;
    private final Collection<String> columns;

    RowIterator( Iterator<Map<String, Object>> rows, Collection<String> columns )
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
