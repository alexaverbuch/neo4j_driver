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

        // Then
        assertThatcastValEqualsExpectedVal( type, rawVal, expectedVal );
    }

    @Test
    public void shouldCastInteger()
    {
        // Given
        int expectedVal = 6;
        Object rawVal = expectedVal;
        Type type = Type.INTEGER;

        // When

        // Then
        assertThatcastValEqualsExpectedVal( type, rawVal, expectedVal );
    }

    @Test
    public void shouldCastLong()
    {
        // Given
        long expectedVal = 6l;
        Object rawVal = expectedVal;
        Type type = Type.LONG;

        // When

        // Then
        assertThatcastValEqualsExpectedVal( type, rawVal, expectedVal );
    }

    @Test
    public void shouldCastDouble()
    {
        // Given
        double expectedVal = 6.0;
        Object rawVal = expectedVal;
        Type type = Type.DOUBLE;

        // When

        // Then
        assertThatcastValEqualsExpectedVal( type, rawVal, expectedVal );
    }

    @Test
    public void shouldCastString()
    {
        // Given
        String expectedVal = "6";
        Object rawVal = expectedVal;
        Type type = Type.STRING;

        // When

        // Then
        assertThatcastValEqualsExpectedVal( type, rawVal, expectedVal );
    }

    @Test
    public void shouldCastMap()
    {
        // Given
        Map<String, Object> expectedVal = MapUtil.map( "six", 6 );
        Object rawVal = expectedVal;
        Type type = Type.MAP;

        // When

        // Then
        assertThatcastValEqualsExpectedVal( type, rawVal, expectedVal );
    }

    <T> void assertThatcastValEqualsExpectedVal( Type<T> type, Object rawVal, T expectedVal )
    {
        boolean exceptionThrown = false;
        try
        {
            T castVal = type.cast( rawVal );
            assertThat( castVal.equals( expectedVal ), is( true ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            exceptionThrown = true;
        }
        assertThat( exceptionThrown, is( false ) );
    }

}
