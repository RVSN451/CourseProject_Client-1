package org.example;

import org.slf4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SendMessage extends Thread {
    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public final static Logger log = App.log;

    private final Client client;

    public SendMessage(Client client) {
        this.client = client;
    }

    public static String consoleReadString() {
        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public void send(String message) {
        client.getOut().println(client.getClientName() + ": " + message);
        log.info("Клиент {} отправил сообщение: {}.",
                client.getClientID(), message.replace(client.getClientName() + ": ", ""));
    }


    @Override
    public void run() {
        try {
            Thread.sleep(App.SEND_THREAD_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\nВведите имя пользователя (DisplayName). Указанное имя будет идентифицировать вас в чате.");
        String message;
        message = consoleReadString();
        client.setClientName(message);
        client.getOut().println(message);
        log.info("Клиенту {} присвоено clientName: {}", client.getClientID(), client.getClientName());

        while (true) {
            message = consoleReadString();
            if ("/exit".equalsIgnoreCase(message)) {
                send(message);
                client.setClientIsInterrupt(true);
                break;
            } else if (message != null) {
                send(message);
            }
        }
    }
}