package org.neo4j.driver.exceptions;

/**
 * An exhaustive list of all possible exceptions the driver can throw.
 *
 * TODO: These should map to the Neo4j Error codes, not arbitrary codes here.
 */
public enum DriverExceptionType
{

    //
    // CLIENT SIDE EXCEPTIONS
    //

    CLIENT_UNKNOWN_CONNECTION_PROTOCOL,

    CLIENT_INVALID_CONNECTION_URL,

    CLIENT_TYPE_CONVERSION,

    //
    // DATABASE EXCEPTIONS
    //

    CONSTRAINT_VIOLATION,
    UNKNOWN,

}
