package com.egon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.Set;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        int i = 0;
        Random random = new Random();
        Jedis jedis = new Jedis("127.0.0.1", 6379);
//        while (i < 1000) {
//            jedis.sadd("car", "苏D" + String.format("%05d", random.nextInt(100000)));
//            i++;
//        }
        System.out.println("===遍历key*===");
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }
    }

    public void insertCarToRedis() {
        int i = 0;
        Random random = new Random();
        Jedis jedis = new Jedis("127.0.0.1", 6379);
//        while (i < 1000) {
//            jedis.sadd("car", "苏D" + String.format("%05d", random.nextInt(100000)));
//            i++;
//        }
        System.out.println("===遍历key*===");
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }
    }
}
