package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class App {
    public static final Logger log = LoggerFactory.getLogger(App.class);
    private static final File setting = new File("src/main/resources/setting.txt");
    public static final long MAIN_THREAD_SLEEP = 500L;
    public static final long CLIENT_THREAD_SLEEP = 200L;
    public static final long GET_MASSAGE_THREAD_SLEEP = 3000L;
    public static final long SEND_THREAD_SLEEP = 1000L;
    private static BufferedInputStream bis;

    static {
        try {
            bis = new BufferedInputStream(new FileInputStream(setting));
        } catch (FileNotFoundException e) {
            log.error(e.toString());
        }
    }


    public static void main(String[] args) {
        Map<String, String> property = new HashMap<>();
        try {
            property = settingProperty(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread.currentThread().setName("MAIN_THREAD");
        Client newClient = new Client(property.get("host"), Integer.parseInt(property.get("port")));
        if (!newClient.isClientIsInterrupt()) {
            stopApplication();
        }
        Thread threadClient = new Thread(newClient);
        threadClient.setName("ClientThread");
        threadClient.setDaemon(true);
        threadClient.start();
        log.info("Клиент {} запущен.", newClient.getClientID());

        while (true) {
            if (threadClient.isInterrupted()) {
                log.info("Клиент {} с именем {} завершил работу приложения.",
                        newClient.getClientID(), newClient.getClientName());

                try {
                    Thread.sleep(MAIN_THREAD_SLEEP);
                } catch (InterruptedException e) {
                    log.error(e.toString());
                }
                break;
            }
        }
    }

    public static Map<String, String> settingProperty(BufferedInputStream bis) throws IOException {
        StringBuilder sb = new StringBuilder();
        Map<String, String> settingProperty = new HashMap();
        int i;
        while ((i = bis.read()) != -1) {
            sb.append((char) i);
        }
        String[] stringProperty = sb.toString().split("\n", -1);
        for (String s : stringProperty) {
            String[] st = s.split("=");
            settingProperty.put(st[0], st[1].replace("\r", ""));
        }
        return settingProperty;
    }

    public static void stopApplication() {
        log.error("Сервер недоступен.");
        Thread.currentThread().interrupt();
    }
}