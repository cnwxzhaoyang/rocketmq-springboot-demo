package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class Consumer
{
    public static void main( String[] args )
    {
        System.setProperty("rocketmq.client.logUseSlf4j", "true");
        SpringApplication.run(Consumer.class);
    }
}
