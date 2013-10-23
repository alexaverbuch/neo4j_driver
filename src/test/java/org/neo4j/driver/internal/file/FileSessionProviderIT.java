package org.neo4j.driver.internal.file;

import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.test.TargetDirectory;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.neo4j.driver.Type.INTEGER;
import static org.neo4j.helpers.collection.MapUtil.stringMap;
import static org.neo4j.test.TargetDirectory.cleanTestDirForTest;

public class FileSessionProviderIT
{

    @Rule
    public TargetDirectory.TestDirectory testDir = cleanTestDirForTest( getClass() );

    @Test
    public void shouldSupportMultipleSessionsAgainstTheSameFile() throws Exception
    {
        // Given
        FileSessionProvider provider = new FileSessionProvider();
        Session sessionOne = provider.newSession( URI.create( "file://" + testDir.absolutePath() ), stringMap() );

        // When I start a second session
        Session sessionTwo = provider.newSession( URI.create( "file://" + testDir.absolutePath() ), stringMap() );

        // Then I should be able to use both
        try(Transaction tx = sessionOne.newTransaction())
        {
            tx.execute( "CREATE (n:User)" );
            tx.success();
        }

        try(Transaction tx = sessionTwo.newTransaction())
        {
            Result result = tx.execute( "MATCH (n:User) RETURN count(*)" );
            assertTrue( result.next() );
            assertEquals( (Integer) 1, result.getValue( INTEGER, "count(*)" ) );
        }


        // And When
        sessionOne.close();

        // Then session one should remain available
        try(Transaction tx = sessionTwo.newTransaction())
        {
            Result result = tx.execute( "MATCH (n:User) RETURN count(*)" );
            assertTrue(result.next());
            assertEquals((Integer)1, result.getValue( INTEGER, "count(*)" ));
        }

        // Finally
        sessionTwo.close();
    }

}
