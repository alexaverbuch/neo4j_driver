package org.neo4j.driver;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

public class EmbeddedDriverTest extends DriverTest
{
    private GraphDatabaseService db = null;

    @Override
    public Driver initDb()
    {
        try
        {
            FileUtils.deleteRecursively( new File( DriverTest.DB_DIR ) );
            db = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder( DriverTest.DB_DIR ).newGraphDatabase();
            return new EmbeddedDriver( db );
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    @Override
    public void cleanDb()
    {
        if ( null != db ) db.shutdown();
    }
}
