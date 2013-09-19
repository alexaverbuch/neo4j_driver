package org.neo4j.driver;

import java.util.Iterator;
import java.util.Map;

public interface Result extends AutoCloseable, Iterator<Map<String, Object>>
{
    @Override
    void close();
}
