package org.neo4j.driver;

import java.util.Map;

/**
 * A transaction encapsulates an atomic set of statements against the database. They will either all succeed, or all
 * fail.
 *
 * When you are done with a transaction, you {@link #success() close}. it. By default, the transaction will roll back
 * when you do this. If you wish to commit the transaction, making your changes visible in the database, you must mark
 * the transaction as successful by calling {@link #success()}.
 *
 * The reason that you have to mark a transaction as successful (rather than marking it to be rolled back on failure)
 * is that it makes it much less likely to accidentally commit a transaction after an exception. The general pattern
 * you use should look like:
 *
 * <code>
 * try(Transaction tx = session.newTransaction())
 * {
 *     tx.execute("CREATE (n:User)");
 *     tx.success();
 * }
 * </code>
 *
 * This ensures that if an exception is thrown when executing your statement, the transaction is properly rolled back.
 */
public interface Transaction extends AutoCloseable
{
    /**
     * Execute a single statement.
     */
    Result execute( String query );

    /**
     * Execute a statement with parameters.
     */
    Result execute( String query, Map<String, Object> params );

    /**
     * Mark this transaction as successful.
     */
    void success();

    /**
     * Close this transaction. This will, if you have called {@link #success()} commit your changes. If not, it will
     * roll them back.
     *
     * In either case, it will release all locks and resources held exclusively by this transaction.
     */
    @Override
    void close();
}
