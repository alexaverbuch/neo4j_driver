package org.neo4j.driver.exceptions;

public class UnknownDriverException extends DriverException
{
    public UnknownDriverException( String message, Throwable cause )
    {
        super( DriverExceptionType.UNKNOWN, message, cause );
    }
}
