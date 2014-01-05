package org.neo4j.driver.compatibility;

import java.util.Map;

import org.junit.Test;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.neo4j.driver.Type.INTEGER;
import static org.neo4j.driver.Type.LONG;
import static org.neo4j.driver.Type.STRING;
import static org.neo4j.helpers.collection.MapUtil.map;

public class ResultCompatibility extends DriverCompatibilitySuite.Compatibility
{
    public ResultCompatibility( DriverCompatibilitySuite suite )
    {
        super( suite );
    }

    @Test
    public void shouldReadWhatItWritesWithoutParams()
    {
        // Given
        Session session = newSession();

        // When
        try (Transaction tx = session.newTransaction())
        {
            tx.execute( "CREATE (node1:NodeType1)-[:RelType1]->(node2:NodeType2)" );
            tx.success();
        }

        // Then
        try (Transaction tx = session.newTransaction())
        {
            try (Result r = tx.execute( "MATCH (:NodeType1)-[r]->(:NodeType2) RETURN type(r) AS relType, id(r) AS id" ))
            {
                assertThat( r.next(), is( true ) );
                assertThat( r.getValue( LONG, "id" ), is( 0l ) );
                assertThat( r.getValue( STRING, "relType" ), is( "RelType1" ) );
                assertThat( r.next(), is( false ) );
            }
            tx.success();
        }
    }

    @Test
    public void shouldReadWhatItWritesWithParams()
    {
        // Given
        Session session = newSession();

        // Given
        Map<String, Object> params = map(
                "node1Params", map( "name", "node one", "number", 1 ),
                "node2Params", map( "name", "node two", "number", 2 ) );

        // When
        try (Transaction tx = session.newTransaction())
        {
            tx.execute( "CREATE (node1:NodeType1 {node1Params})-[:RelType1]->(node2:NodeType2 {node2Params})", params );
            tx.success();
        }

        // Then
        try (Transaction tx = session.newTransaction())
        {
            try (Result r = tx.execute(
                    "MATCH (node1:NodeType1)-[r]->(node2:NodeType2)\n" +
                    "RETURN type(r) AS relType, count(r) AS count, node1.name AS name, node2.number AS number" ))
            {
                assertThat( r.next(), is( true ) );
                assertThat( r.getValue( STRING, "relType" ), is( "RelType1" ) );
                assertThat( r.getValue( INTEGER, "count" ), is( 1 ) );
                assertThat( r.getValue( STRING, "name" ), is( "node one" ) );
                assertThat( r.getValue( LONG, "number" ), is( 2l ) );
                assertThat( r.next(), is( false ) );
            }
            tx.success();
        }
    }
}
