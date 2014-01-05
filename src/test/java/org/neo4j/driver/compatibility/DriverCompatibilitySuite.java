package org.neo4j.driver.compatibility;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.neo4j.driver.Session;
import org.neo4j.kernel.api.index.ParameterizedSuiteRunner;

/**
 * This is somewhat complicated, but the essence of it is that it is a parameterized junit test suite that verifies
 * compatibility of a driver implementation.
 *
 * Each driver implementation is expected to implement a test class called something like MyDriverCompatibility, which
 * should extend this class. It must then implement the {@link #newSession()} method to allow the compatibility tests
 * to accesss a session implementation.
 *
 * It may optionally implement the {@link #beforeTest()} and {@link #afterTest()} to do any necessary setup and
 * tear-down logic.
 *
 * The actual compatibility tests are in the various tests listed as suite classes in the annotation below.
 */
@RunWith(ParameterizedSuiteRunner.class)
@Suite.SuiteClasses({
        ResultCompatibility.class,
        ErrorCompatibility.class,
})
public abstract class DriverCompatibilitySuite
{
    public abstract Session newSession();

    public void beforeTest() { }

    public void afterTest() { }

    public static abstract class Compatibility
    {
        private final DriverCompatibilitySuite suite;

        public Compatibility(DriverCompatibilitySuite suite)
        {
            this.suite = suite;
        }

        @Before
        public void beforeTest()
        {
            suite.beforeTest();
        }

        protected Session newSession()
        {
            return suite.newSession();
        }

        @After
        public void afterTest()
        {
            suite.afterTest();
        }
    }

}
