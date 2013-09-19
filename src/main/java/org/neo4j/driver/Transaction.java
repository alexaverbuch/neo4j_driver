package org.neo4j.driver;

public interface Transaction extends AutoCloseable
{
    Result execute( String query );

    void success();

    @Override
    void close();
}
