package org.neo4j.driver;

public interface Session
{
    Transaction newTransaction();
}
