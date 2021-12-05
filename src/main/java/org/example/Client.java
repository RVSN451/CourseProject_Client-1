package org.example;

import javax.inject.Singleton;

import lombok.*;
import org.slf4j.Logger;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.UUID;


@Data
@NoArgsConstructor
@Singleton
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Client extends Thread {
    public final static Logger log = App.log;

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private UUID clientID;

    private String clientName;

    private boolean clientIsInterrupt;

    @Setter(AccessLevel.NONE)
    private Socket clientSocket;

    @Setter(AccessLevel.NONE)
    private PrintWriter out;

    @Setter(AccessLevel.NONE)
    private BufferedReader in;

    public Client(String host, Integer port) {
        clientID = UUID.randomUUID();
        try {
            clientSocket = new Socket(host, port);
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (ConnectException connectException) {
            log.error("Пользователю {} не удалось установить связь с сервером. Север не доступен." +
                    "Попробуйте перезапустить приложение позже.", clientID);
            App.stopApplication();

        } catch (IOException e) {
            log.error(e.toString());
        }
        if (clientSocket != null) {
            clientIsInterrupt = false;
            log.info("Клиент {} зарегистрирован. Socket {}", clientID, clientSocket);
        } else clientIsInterrupt = true;
    }

    @Override
    public void run() {

        if (!clientIsInterrupt) {
            Thread sendMessage = new Thread(new SendMessage(this));
            sendMessage.setName("SendMessageThread");
            sendMessage.setDaemon(true);
            sendMessage.start();

            Thread getMessage = new Thread(new GetMessage(this));
            getMessage.setName("GetMessageThread");
            getMessage.setDaemon(true);
            getMessage.start();
        }

        while (!clientIsInterrupt) {
            try {
                Thread.sleep(App.CLIENT_THREAD_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.currentThread().interrupt();
    }
}