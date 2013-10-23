package org.neo4j.driver.internal.file;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.driver.Result;
import org.neo4j.driver.Type;
import org.neo4j.graphdb.ResourceIterator;

class FileResult implements Result
{
    private final ResourceIterator<Map<String, Object>> innerResult;
    private final Iterable<String> columns;
    private Map<String, Object> row = null;

    public FileResult( ExecutionResult innerResult )
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

}
