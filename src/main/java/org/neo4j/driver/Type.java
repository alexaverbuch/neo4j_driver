package org.neo4j.driver;

import java.util.Map;

public abstract class Type<JAVA_TYPE>
{
    public static final Type<Boolean> BOOLEAN = new Type<Boolean>()
    {
        @Override
        Boolean cast( Object raw )
        {
            return (boolean) raw;
        }
    };
    public static final Type<Integer> INTEGER = new Type<Integer>()
    {
        @Override
        Integer cast( Object raw )
        {
            if ( raw instanceof Number )
            {
                return ( (Number) raw ).intValue();
            }
            else
            {
                throw new RuntimeException( "Invalid type" );
            }
        }
    };
    public static final Type<Long> LONG = new Type<Long>()
    {
        @Override
        Long cast( Object raw )
        {
            if ( raw instanceof Number )
            {
                return ( (Number) raw ).longValue();
            }
            else
            {
                throw new RuntimeException( "Invalid type" );
            }
        }
    };
    public static final Type<Double> DOUBLE = new Type<Double>()
    {
        @Override
        Double cast( Object raw )
        {
            if ( raw instanceof Number )
            {
                return ( (Number) raw ).doubleValue();
            }
            else
            {
                throw new RuntimeException( "Invalid type" );
            }
        }
    };
    public static final Type<String> STRING = new Type<String>()
    {
        @Override
        String cast( Object raw )
        {
            return (String) raw;
        }
    };
    public static final Type<Map<String, Object>> MAP = new Type<Map<String, Object>>()
    {
        @Override
        Map<String, Object> cast( Object raw )
        {
            return (Map<String, Object>) raw;
        }
    };

    abstract JAVA_TYPE cast( Object raw );
}
