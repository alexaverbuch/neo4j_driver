package org.neo4j.driver;

import java.util.Map;

public interface Transaction extends AutoCloseable
{
    Result execute( String query );

    Result execute( String query, Map<String, Object> params );

    void success();

    @Override
    void close();
}
