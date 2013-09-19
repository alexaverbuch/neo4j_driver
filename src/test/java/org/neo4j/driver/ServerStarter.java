package org.neo4j.driver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.server.WrappingNeoServerBootstrapper;

public class ServerStarter
{
    public static void main( String[] args ) throws IOException
    {
        ServerStarter serverStarter = new ServerStarter();
        serverStarter.start( DriverTest.DB_DIR, new HashMap<String, String>() );
    }

    void start( String path, Map<String, String> config ) throws IOException
    {
        FileUtils.deleteRecursively( new File( path ) );
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder( path ).setConfig( config ).newGraphDatabase();
        registerShutdownHook( db );
        WrappingNeoServerBootstrapper server = new WrappingNeoServerBootstrapper( (GraphDatabaseAPI) db );
        server.start();
        try
        {
            while ( true )
            {
                Thread.sleep( 50 );
            }
        }
        catch ( InterruptedException e )
        {
            server.stop();
        }
    }

    private void registerShutdownHook( final GraphDatabaseService db )
    {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                db.shutdown();
            }
        } );
    }

}
