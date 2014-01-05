package org.neo4j.driver;

import java.io.File;
import java.io.IOException;

import org.neo4j.driver.compatibility.DriverCompatibilitySuite;
import org.neo4j.driver.internal.embedded.EmbeddedSession;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

public class FileDriverCompatibilityIT extends DriverCompatibilitySuite
{
    private final String dir = "filedriver-test";
    private GraphDatabaseService db;

    @Override
    public Session newSession()
    {
        return new EmbeddedSession( db );
    }

    @Override
    public void beforeTest()
    {
        try
        {
            FileUtils.deleteRecursively( new File( dir ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e.getCause() );
        }
        db = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder( dir ).newGraphDatabase();
    }

    @Override
    public void afterTest()
    {
        db.shutdown();
        try
        {
            FileUtils.deleteRecursively( new File( dir ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e.getCause() );
        }
    }
}
