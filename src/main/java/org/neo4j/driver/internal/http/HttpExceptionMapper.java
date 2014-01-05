package org.neo4j.driver.internal.http;

import org.neo4j.driver.exceptions.ConstraintViolationException;
import org.neo4j.driver.exceptions.DriverException;
import org.neo4j.driver.exceptions.UnknownDriverException;

public class HttpExceptionMapper
{
    public DriverException map( String code, String message )
    {
        // TODO: This should obviously be expanded to be a clean mechanism of mapping all known status codes.

        if( code.equals( "Neo.ClientError.Schema.ConstraintViolation" ) ||

            // Temp workaround for incorrect error code
            (code.equals( "Neo.DatabaseError.Statement.ExecutionFailure")
                    && message.contains( "already exists with label" )))
        {
            throw new ConstraintViolationException( message, null );
        }
        throw new UnknownDriverException( code + ": " + message, null );
    }
}
