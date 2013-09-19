package org.neo4j.driver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class Neo4jDriverTest
{
    public static String dbDir = "tempDb";
    public static GraphDatabaseService db = null;
    public static Driver driver = null;

    @BeforeClass
    public static void initDb() throws IOException
    {
        FileUtils.deleteRecursively( new File( dbDir ) );

        db = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder( dbDir ).newGraphDatabase();
        driver = new EmbeddedDriver( db );
    }

    @AfterClass
    public static void closeDb() throws IOException
    {
        db.shutdown();
    }

    @Test
    public void driverShouldReadWhatItWrites()
    {
        Map<String, Object> node1Params = new HashMap<String, Object>();
        node1Params.put( "name", "node one" );
        node1Params.put( "number", 1 );
        Map<String, Object> node2Params = new HashMap<String, Object>();
        node1Params.put( "name", "node two" );
        node1Params.put( "number", 2 );
        List<Map<String, Object>> params = Arrays.asList( node1Params, node2Params );
        // String createQuery =
        // "CREATE (node1:NodeType1 {node1Params})-[:RelType1]->(node2:NodeType2 {node2Params})";
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
    }
}
