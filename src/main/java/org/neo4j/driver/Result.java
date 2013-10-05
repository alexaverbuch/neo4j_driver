package org.neo4j.driver;

import java.util.Map;

public interface Result extends AutoCloseable
{
    boolean next();

    public Iterable<String> columns();

    public <T> T getValue( Type<T> type, String column );

    public Map<String, Object> getRow();

    @Override
    void close();
}
