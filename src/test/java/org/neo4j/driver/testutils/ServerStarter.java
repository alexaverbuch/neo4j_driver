package org.neo4j.driver.testutils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.server.WrappingNeoServerBootstrapper;

public class ServerStarter extends Thread
{
    public static void main( String[] args ) throws IOException
    {
        ServerStarter serverStarter = new ServerStarter( "./test-data", new HashMap<String, String>() );
        serverStarter.start();
    }

    private final String path;
    private final Map<String, String> config;
    private AtomicBoolean stop = new AtomicBoolean( false );
    private AtomicBoolean running = new AtomicBoolean( false );

    public ServerStarter( String path, Map<String, String> config )
    {
        super();
        this.path = path;
        this.config = config;
    };

    public void stopServer()
    {
        stop.set( true );
    }

    public boolean isRunning()
    {
        return running.get();
    }

    @Override
    public void run()
    {
        try
        {
            FileUtils.deleteRecursively( new File( path ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            return;
        }
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder( path ).setConfig( config ).newGraphDatabase();
        WrappingNeoServerBootstrapper server = new WrappingNeoServerBootstrapper( (GraphDatabaseAPI) db );
        server.start();
        running.set( true );
        try
        {
            while ( stop.get() == false )
            {
                Thread.sleep( 50 );
            }
        }
        catch ( InterruptedException e )
        {
        }
        finally
        {
            server.stop();
            db.shutdown();
        }
    }

}
