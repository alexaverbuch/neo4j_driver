package org.neo4j.driver.exceptions;

/**
 * All exceptions thrown by the driver subclass this exception.
 */
public class DriverException extends RuntimeException
{
    private final DriverExceptionType type;

    // Note: Package private, don't throw these directly, create and throw appropriate sub-types.
    DriverException( DriverExceptionType type, String message )
    {
        this(type, message, null);
    }

    // Note: Package private, don't throw these directly, create and throw appropriate sub-types.
    DriverException( DriverExceptionType type, String message, Throwable cause )
    {
        super( message, cause );
        this.type = type;
    }

    public DriverExceptionType type()
    {
        return type;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        DriverException that = (DriverException) o;

        if ( type != that.type )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return type.hashCode();
    }
}
