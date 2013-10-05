package org.neo4j.driver;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Map;

import org.junit.Test;
import org.neo4j.helpers.collection.MapUtil;

public class TypeTest
{
    @Test
    public void shouldCastBoolean()
    {
        // Given
        boolean expectedVal = true;
        Object rawVal = expectedVal;
        Type type = Type.BOOLEAN;

        // When
        boolean castWorked = castValEqualsExpectedVal( type, rawVal, expectedVal );

        // Then
        assertThat( castWorked, is( true ) );
    }

    @Test
    public void shouldCastInteger()
    {
        // Given
        int expectedVal = 6;
        Object rawVal = expectedVal;
        Type type = Type.INTEGER;

        // When
        boolean castWorked = castValEqualsExpectedVal( type, rawVal, expectedVal );

        // Then
        assertThat( castWorked, is( true ) );
    }

    @Test
    public void shouldCastLong()
    {
        // Given
        long expectedVal = 6;
        Object rawVal = expectedVal;
        Type type = Type.LONG;

        // When
        boolean castWorked = castValEqualsExpectedVal( type, rawVal, expectedVal );

        // Then
        assertThat( castWorked, is( true ) );
    }

    @Test
    public void shouldCastDouble()
    {
        // Given
        double expectedVal = 6.0;
        Object rawVal = expectedVal;
        Type type = Type.DOUBLE;

        // When
        boolean castWorked = castValEqualsExpectedVal( type, rawVal, expectedVal );

        // Then
        assertThat( castWorked, is( true ) );
    }

    @Test
    public void shouldCastString()
    {
        // Given
        String expectedVal = "6";
        Object rawVal = expectedVal;
        Type type = Type.STRING;

        // When
        boolean castWorked = castValEqualsExpectedVal( type, rawVal, expectedVal );

        // Then
        assertThat( castWorked, is( true ) );
    }

    @Test
    public void shouldCastMap()
    {
        // Given
        Map<String, Object> expectedVal = MapUtil.map( "six", 6 );
        Object rawVal = expectedVal;
        Type type = Type.MAP;

        // When
        boolean castWorked = castValEqualsExpectedVal( type, rawVal, expectedVal );

        // Then
        assertThat( castWorked, is( true ) );
    }

    <T> boolean castValEqualsExpectedVal( Type<T> type, Object rawVal, T expectedVal )
    {
        try
        {
            T castVal = type.cast( rawVal );
            return castVal.equals( expectedVal );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }
}
