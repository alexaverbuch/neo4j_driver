package org.neo4j.driver.internal.embedded;

import org.neo4j.cypher.CypherException;
import org.neo4j.driver.exceptions.ConstraintViolationException;
import org.neo4j.driver.exceptions.DriverException;
import org.neo4j.driver.exceptions.UnknownDriverException;
import org.neo4j.kernel.api.exceptions.schema.UniqueConstraintViolationKernelException;

public class EmbeddedExceptionMapper
{
    public DriverException map(Exception e)
    {
        if(e instanceof CypherException && e.getCause() != null && e.getCause() instanceof UniqueConstraintViolationKernelException )
        {
            return new ConstraintViolationException(e.getMessage(), e);
        }
        return new UnknownDriverException( e.getMessage(), e );
    }
}
