package org.example;

import org.slf4j.Logger;
import java.io.IOException;
import java.net.SocketException;

public class GetMessage extends Thread {
    public final static Logger log = App.log;

    private final Client client;

    public GetMessage(Client client) {
        this.client = client;
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        String message = "";
        while (true) {
            try {
                message = client.getIn().readLine();
                log.info("Клиент {} получил сообщение: {}.", client.getClientID(), message);
            } catch (SocketException socketException) {
                System.out.println("УПС!!! Что-то пошло не так.. Потеряно соединение с сервером. " +
                        "\nРабота приложения остановлена. Попробуйте перезапустить приложение.");
                log.error("Клиент {} потерял связь с сервером", client.getClientID());
                client.setClientIsInterrupt(true);
                try {
                    Thread.sleep(App.GET_MASSAGE_THREAD_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(message);
        }
    }
}