package org.neo4j.driver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.neo4j.driver.testutils.ServerStarter;
import org.neo4j.kernel.impl.util.FileUtils;

public class RemoteSessionTest extends SessionTest
{
    private String dir;
    private ServerStarter serverStarter;

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
        serverStarter = new ServerStarter( dir, new HashMap<String, String>() );
        serverStarter.start();
        while ( serverStarter.isRunning() == false )
        {
            try
            {
                Thread.sleep( 500 );
            }
            catch ( InterruptedException e )
            {
                // don't care
            }
        }
        return new RemoteSession( "http://localhost:7474/" );
    }

    @Override
    public void cleanDb()
    {
        serverStarter.stopServer();
        try
        {
            serverStarter.join();
        }
        catch ( InterruptedException e1 )
        {
            // don't care
        }
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
