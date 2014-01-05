package org.neo4j.driver.exceptions;

public class ConstraintViolationException extends DriverException
{
    public ConstraintViolationException( String message, Throwable cause )
    {
        super( DriverExceptionType.CONSTRAINT_VIOLATION, message, cause );
    }
}
