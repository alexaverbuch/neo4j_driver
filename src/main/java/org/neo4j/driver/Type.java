package org.neo4j.driver;

import java.util.Map;

import org.neo4j.driver.exceptions.ClientException;
import org.neo4j.driver.exceptions.DriverExceptionType;

import static java.lang.String.format;

public abstract class Type<JAVA_TYPE>
{
    public static final Type<Boolean> BOOLEAN = new Type<Boolean>()
    {
        @Override
        public Boolean cast( Object raw )
        {
            if ( raw instanceof Boolean )
            {
                return ( (Boolean) raw );
            }
            else
            {
                throw new ClientException( DriverExceptionType.CLIENT_TYPE_CONVERSION,
                        format( "Cannot convert %s to %s.", describe( raw ), "Boolean" ));
            }
        }
    };
    public static final Type<Integer> INTEGER = new Type<Integer>()
    {
        @Override
        public Integer cast( Object raw )
        {
            if ( raw instanceof Number )
            {
                return ( (Number) raw ).intValue();
            }
            else
            {
                throw new ClientException( DriverExceptionType.CLIENT_TYPE_CONVERSION,
                        format( "Cannot convert %s to %s.", describe( raw ), "Integer" ));
            }
        }
    };
    public static final Type<Long> LONG = new Type<Long>()
    {
        @Override
        public Long cast( Object raw )
        {
            if ( raw instanceof Number )
            {
                return ( (Number) raw ).longValue();
            }
            else
            {
                throw new ClientException( DriverExceptionType.CLIENT_TYPE_CONVERSION,
                        format( "Cannot convert %s to %s.", describe( raw ), "Long" ));
            }
        }
    };
    public static final Type<Double> DOUBLE = new Type<Double>()
    {
        @Override
        public Double cast( Object raw )
        {
            if ( raw instanceof Number )
            {
                return ( (Number) raw ).doubleValue();
            }
            else
            {
                throw new ClientException( DriverExceptionType.CLIENT_TYPE_CONVERSION,
                        format( "Cannot convert %s to %s.", describe( raw ), "Double" ));
            }
        }
    };
    public static final Type<String> STRING = new Type<String>()
    {
        @Override
        public String cast( Object raw )
        {
            return (String) raw;
        }
    };
    public static final Type<Map<String, Object>> MAP = new Type<Map<String, Object>>()
    {
        @Override
        public Map<String, Object> cast( Object raw )
        {
            if ( raw instanceof Map )
            {
                return (Map<String, Object>) raw;
            }
            else
            {
                throw new ClientException( DriverExceptionType.CLIENT_TYPE_CONVERSION,
                        format( "Cannot convert %s to %s.", describe( raw ), "Map" ));
            }
        }
    };

    public abstract JAVA_TYPE cast( Object raw );

    private static String describe( Object object )
    {
        return object.getClass().getSimpleName();
    }
}
