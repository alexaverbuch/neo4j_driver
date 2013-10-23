package org.neo4j.driver;

/**
 * A session represents a connection to the database. The session is not guaranteed to be thread-safe, but it is
 * guaranteed to be shareable between threads, as long as only one thread at a time uses it.
 *
 * Because session objects may be, depending on connection method, expensive to create, it is advised that you pool
 * these objects.
 */
public interface Session extends AutoCloseable
{
    /**
     * Begin a new transaction.
     */
    Transaction newTransaction();

    /**
     * Finish this session, closing any resources exclusively associated with it.
     */
    @Override
    void close();
}
