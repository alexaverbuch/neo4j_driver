package org.neo4j.driver.exceptions;

/**
 * A problem occurred on our side of the connection to the database.
 */
public class ClientException extends DriverException
{

    public ClientException( DriverExceptionType type, String message )
    {
        super( type, message );
    }

    public ClientException( DriverExceptionType type, String message, Throwable cause )
    {
        super( type, message, cause );
    }
}
