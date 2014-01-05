package org.neo4j.driver.internal.http;

import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.test.server.HTTP;

public class HttpSession implements Session
{
    private final HTTP.Builder http;
    private final HttpExceptionMapper exceptionMapper;

    public HttpSession( String uri, HttpExceptionMapper exceptionMapper )
    {
        this.http = HTTP.withBaseUri( uri.substring( 0, uri.length() - 1 ) );
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public Transaction newTransaction()
    {
        return new HttpTransaction( http, exceptionMapper );
    }

    @Override
    public void close()
    {
    }
}
