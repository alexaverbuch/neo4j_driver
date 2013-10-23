package org.neo4j.driver;

import java.io.File;
import java.io.IOException;

import org.neo4j.driver.internal.file.FileSession;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

public class FileSessionIT extends SessionIT
{
    private String dir;
    private GraphDatabaseService db;

    @Override
    public Session initDb( String dir )
    {
        this.dir = dir;
        try
        {
            FileUtils.deleteRecursively( new File( dir ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e.getCause() );
        }
        db = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder( dir ).newGraphDatabase();
        return new FileSession( db );
    }

    @Override
    public void cleanDb()
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
