package org.neo4j.driver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.neo4j.driver.compatibility.DriverCompatibilitySuite;
import org.neo4j.driver.internal.http.HttpExceptionMapper;
import org.neo4j.driver.internal.http.HttpSession;
import org.neo4j.driver.testutils.ServerStarter;
import org.neo4j.kernel.impl.util.FileUtils;

public class HttpDriverCompatibilityIT extends DriverCompatibilitySuite
{
    private String dir = "filedriver-test";
    private ServerStarter serverStarter;

    @Override
    public Session newSession()
    {
        return new HttpSession( "http://localhost:7474/", new HttpExceptionMapper() );
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
    }

    @Override
    public void afterTest()
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
