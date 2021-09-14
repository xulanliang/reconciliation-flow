package com.yiban.framework.core.util;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;

public abstract class Applications {

    public static void supports(SpringApplication springApplication) {
        ApplicationPids.supports(springApplication);
    }

    static abstract class ApplicationPids {

        public final static String PID_FILE = "app.pid";

        public static void supports(SpringApplication springApplication) {
            Runtime.getRuntime().addShutdownHook(new RemovePidFileHook());
            springApplication.addListeners(new ApplicationPidFileWriter(PID_FILE));
        }

        private static class RemovePidFileHook extends Thread {

            @Override
            public void run() {
                boolean ok = new File(PID_FILE).delete();
                if (!ok) {
                    System.err.println("error to delete " + PID_FILE);
                }
            }
        }
    }
}
