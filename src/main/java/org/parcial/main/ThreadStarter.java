package org.parcial.main;

import org.parcial.thread.CovidThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ThreadStarter implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ThreadStarter.class);

    @Autowired
    private CovidThread covidThread;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Waiting 15 seconds before starting the thread...");
        Thread.sleep(15000);
        new Thread(covidThread).start();
        logger.info("CovidThread has been started.");
    }
}
