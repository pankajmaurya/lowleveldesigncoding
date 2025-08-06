package dev.lld.practice.messagebroker;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class MessageTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testCreate()
    {
        Message foo = new Message("foo");
        Message bar = new Message("bar");

        assertTrue( bar.getId() == foo.getId() + 1);
    }
}
