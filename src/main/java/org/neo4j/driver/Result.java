package org.neo4j.driver;

public interface Result extends AutoCloseable
{
    /**
     * Move to the next row in the result.
     * @return true if there was another row
     */
    boolean next();

    /**
     * Retrieve a list of the columns in this result.
     */
    public Iterable<String> columns();

    /**
     * Retrieve a value from the current row.
     *
     * @param type Specifies which type you wish to get back, to allow type safe operations.
     */
    public <T> T getValue( Type<T> type, String column );

    /**
     * Close this result, releasing any resources held by it.
     */
    @Override
    void close();
}
