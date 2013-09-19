package org.neo4j.driver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public abstract class DriverTest
{
    public static String DB_DIR = "tempDb";
    public static Driver driver = null;

    public abstract Driver initDb();

    public abstract void cleanDb();

    @Before
    public void before() throws IOException, InterruptedException
    {
        driver = initDb();
    }

    @After
    public void after() throws IOException
    {
        cleanDb();
    }

    @Test
    public void driverShouldReadWhatItWritesWithoutParams()
    {
        String createQuery = "CREATE (node1:NodeType1)-[:RelType1]->(node2:NodeType2)";
        String readQuery = "MATCH (:NodeType1)-[r]->(:NodeType2)\nRETURN type(r) AS relType";

        boolean exceptionThrown = false;

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

        try (Transaction tx = driver.newTransaction())
        {
            try (Result r = tx.execute( readQuery ))
            {
                assertThat( r.hasNext(), is( true ) );
                Map<String, Object> row1 = r.next();
                assertThat( row1.containsKey( "relType" ), is( true ) );
                assertThat( (String) row1.get( "relType" ), is( "RelType1" ) );
                assertThat( r.hasNext(), is( false ) );
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
        String readQuery = "MATCH (node1:NodeType1)-[r]->(node2:NodeType2)\nRETURN type(r) AS relType, node1.name AS name1, node2.number AS number2";

        boolean exceptionThrown = false;

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

        try (Transaction tx = driver.newTransaction())
        {
            try (Result r = tx.execute( readQuery ))
            {
                assertThat( r.hasNext(), is( true ) );
                Map<String, Object> row1 = r.next();
                assertThat( (String) row1.get( "relType" ), is( "RelType1" ) );
                assertThat( (String) row1.get( "name1" ), is( "node one" ) );
                assertThat( (int) row1.get( "number2" ), is( 2 ) );
                assertThat( r.hasNext(), is( false ) );
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
