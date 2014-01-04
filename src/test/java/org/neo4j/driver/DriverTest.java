package org.neo4j.driver;

import java.net.URI;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.exceptions.DriverException;
import org.neo4j.driver.internal.embedded.EmbeddedSession;
import org.neo4j.driver.spi.SessionProvider;
import org.neo4j.test.TargetDirectory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.neo4j.test.TargetDirectory.cleanTestDirForTest;

public class DriverTest
{
    @Rule
    public TargetDirectory.TestDirectory testDir = cleanTestDirForTest( getClass() );

    @Test
    public void shouldThrowReasonableErrorIfNoKnownSessionProviderIsFound() throws Exception
    {
        // When
        try
        {
            Driver.newSession( "nosuchscheme://asd" );
            fail("Expected exception");
        }
        catch(DriverException e)
        {
            // Then
            assertThat(e.getMessage(), equalTo("There is no session provider available to connect via 'nosuchscheme' " +
                    "to 'nosuchscheme://asd'. Make sure you've spelled the protocol correctly, and that the " +
                    "appropriate driver is on your classpath."));
        }
    }

    @Test
    public void shouldDelegateToSessionProviderIfProviderCanHandleScheme() throws Exception
    {
        // Given
        Session session = mock(Session.class);

        SessionProvider provider = mock( SessionProvider.class );
        when(provider.supportsProtocol( "somescheme" )).thenReturn( true );
        when( provider.newSession( any( URI.class ), any(Map.class) )).thenReturn( session );

        Driver.registerProvider(provider);

        // When
        Session result = Driver.newSession( "somescheme://asd" );

        // Then
        assertThat( result, equalTo( session ) );
    }

    @Test
    public void shouldLoadDriversViaJavaServiceLoader() throws Exception
    {
        // Given
        // That I'm in a project with a dependency on a driver that lists itself as a java service

        // When
        try(Session session = Driver.newSession( "file://" + testDir.absolutePath() ))
        {
            assertThat(session, instanceOf( EmbeddedSession.class ));
        }

        // Then
        // No exception should have been thrown.
    }

}
