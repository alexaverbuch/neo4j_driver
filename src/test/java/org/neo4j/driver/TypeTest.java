package org.neo4j.driver;

import org.junit.Test;
import org.neo4j.driver.exceptions.ClientException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.neo4j.driver.exceptions.DriverExceptionType.CLIENT_TYPE_CONVERSION;
import static org.neo4j.helpers.collection.MapUtil.map;

public class TypeTest
{
    @Test
    public void shouldCorrectlyCastHappyPath()
    {
        assertCastsCorrectly( Type.INTEGER, 6,   6 );
        assertCastsCorrectly( Type.LONG,    6l,  6l );
        assertCastsCorrectly( Type.DOUBLE,  6.0, 6.0 );
        assertCastsCorrectly( Type.STRING,  "a", "a" );
        assertCastsCorrectly( Type.MAP,     map("a", 1), map("a", 1) );
    }

    @Test
    public void shouldGiveHelpfulErrorMessageOnSadPath() throws Exception
    {
        assertExceptionForCastingIs( Type.INTEGER, true,
                new ClientException( CLIENT_TYPE_CONVERSION, "Cannot convert Boolean to integer."));
        assertExceptionForCastingIs( Type.LONG, "asd",
                new ClientException( CLIENT_TYPE_CONVERSION, "Cannot convert String to long."));
        assertExceptionForCastingIs( Type.DOUBLE, "asd",
                new ClientException( CLIENT_TYPE_CONVERSION, "Cannot convert String to double."));
        assertExceptionForCastingIs( Type.MAP, "asd",
                new ClientException( CLIENT_TYPE_CONVERSION, "Cannot convert String to map."));
    }

    private void assertExceptionForCastingIs( Type<?> type, Object rawVal, Exception expected )
    {
        try
        {
            type.cast( rawVal );
            fail("Expected conversion to fail");
        }
        catch(Exception seen)
        {
            assertThat(seen, equalTo(expected));
            assertThat(seen.getMessage(), equalTo(expected.getMessage()));
        }
    }

    <T> void assertCastsCorrectly( Type<T> type, Object rawVal, T expectedVal )
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
