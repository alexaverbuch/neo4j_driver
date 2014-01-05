package org.neo4j.driver.compatibility;

import org.junit.Test;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.exceptions.ConstraintViolationException;

import static junit.framework.Assert.fail;

/**
 * TODO: This should expand somehow with a switch statement or something ensuring we have tests covering all
 * possible error codes the server can return.
 */
public class ErrorCompatibility extends DriverCompatibilitySuite.Compatibility
{
    public ErrorCompatibility( DriverCompatibilitySuite suite )
    {
        super( suite );
    }

    @Test
    public void shouldHandleConstraintViolation() throws Exception
    {
        // Given
        Session session = newSession();
        try(Transaction tx = session.newTransaction())
        {
            tx.execute( "CREATE CONSTRAINT ON (user:User) ASSERT user.name IS UNIQUE" );
            tx.success();
        }

        try(Transaction tx = session.newTransaction())
        {
            tx.execute( "CREATE (user:User {name:'bob'})" );
            tx.success();
        }

        // When
        try
        {
            try(Transaction tx = session.newTransaction())
            {
                tx.execute( "CREATE (user:User {name:'bob'})" );
                tx.success();
            }

            fail("Expected exception.");
        }
        catch(ConstraintViolationException e)
        {
            // ok
        }
    }
}
