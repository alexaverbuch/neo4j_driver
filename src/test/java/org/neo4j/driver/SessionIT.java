package org.neo4j.driver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public abstract class SessionIT
{
    public static String DB_DIR = "tempDb";
    public static int dirNumber = 0;
    public static Session driver = null;

    public abstract Session initDb( String dir );

    public abstract void cleanDb();

    @Before
    public void before() throws IOException, InterruptedException
    {
        String dir = DB_DIR + dirNumber++;
        driver = initDb( dir );
    }

    @After
    public void after() throws IOException
    {
        cleanDb();
    }

    @Test
    public void driverShouldReadWhatItWritesWithoutParams()
    {
        // Given
        String createQuery = "CREATE (node1:NodeType1)-[:RelType1]->(node2:NodeType2)";
        String readQuery = "MATCH (:NodeType1)-[r]->(:NodeType2) RETURN type(r) AS relType, id(r) AS id";

        boolean exceptionThrown = false;

        // When
        try (Transaction tx = driver.newTransaction())
        {
            tx.execute( createQuery );
            tx.success();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            exceptionThrown = true;
        }
        assertThat( exceptionThrown, is( false ) );

        // Then
        try (Transaction tx = driver.newTransaction())
        {
            try (Result r = tx.execute( readQuery ))
            {
                assertThat( r.next(), is( true ) );
                assertThat( r.getValue( Type.LONG, "id" ), is( 0l ) );
                assertThat( r.getValue( Type.STRING, "relType" ), is( "RelType1" ) );
                assertThat( r.next(), is( false ) );
            }
            tx.success();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            exceptionThrown = true;
        }
        assertThat( exceptionThrown, is( false ) );
    }

    @Test
    public void driverShouldReadWhatItWritesWithParams()
    {
        // Given
        Map<String, Object> node1Params = new HashMap<String, Object>();
        node1Params.put( "name", "node one" );
        node1Params.put( "number", 1 );
        Map<String, Object> node2Params = new HashMap<String, Object>();
        node2Params.put( "name", "node two" );
        node2Params.put( "number", 2 );
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( "node1Params", node1Params );
        params.put( "node2Params", node2Params );
        String createQuery = "CREATE (node1:NodeType1 {node1Params})-[:RelType1]->(node2:NodeType2 {node2Params})";
        String readQuery = "MATCH (node1:NodeType1)-[r]->(node2:NodeType2)\n"
                           + "RETURN type(r) AS relType, count(r) AS count, node1.name AS name, node2.number AS number";

        boolean exceptionThrown = false;

        // When
        try (Transaction tx = driver.newTransaction())
        {
            tx.execute( createQuery, params );
            tx.success();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            exceptionThrown = true;
        }
        assertThat( exceptionThrown, is( false ) );

        // Then
        try (Transaction tx = driver.newTransaction())
        {
            try (Result r = tx.execute( readQuery ))
            {
                assertThat( r.next(), is( true ) );
                assertThat( r.getValue( Type.STRING, "relType" ), is( "RelType1" ) );
                assertThat( r.getValue( Type.INTEGER, "count" ), is( 1 ) );
                assertThat( r.getValue( Type.STRING, "name" ), is( "node one" ) );
                assertThat( r.getValue( Type.LONG, "number" ), is( 2l ) );
                assertThat( r.next(), is( false ) );
            }
            tx.success();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            exceptionThrown = true;
        }
        assertThat( exceptionThrown, is( false ) );
    }
}
