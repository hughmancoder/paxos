package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Host;
import models.Message;
import models.MessageTargetHost;

public class NetworkHandler {

    private ServerSocket serverSocket;
    private Thread listeningThread;
    private Thread sendingThread;
    private boolean listening = false;
    private boolean sending = false;
    private BlockingQueue<MessageTargetHost> sendingQueue = new LinkedBlockingQueue<>();
    private Host localHost;
    private static final Logger LOGGER = Logger.getLogger(NetworkHandler.class.getName());

    public NetworkHandler(Host host) {
        localHost = host;
        try {
            serverSocket = new ServerSocket();
            /*
             * Optionally we can bind to any available address on the machine
             * serverSocket.bind(new InetSocketAddress("0.0.0.0", host.getPort()));
             */
            serverSocket.bind(new InetSocketAddress(host.getAddress(), host.getPort()));
            LOGGER.log(Level.INFO, "Started server at " + host.getAddress() + ":" +
                    host.getPort());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not start server on port: " + host.getPort(),
                    e);
            serverSocket = null;
        }
    }

    public void startMessageProcessing() {
        if (!sending) {
            sending = true;
            sendingThread = new Thread(this::processMessageQueue);
            sendingThread.start();
        }
    }

    public void stopMessageProcessing() {
        sending = false;
        if (sendingThread != null) {
            sendingThread.interrupt();
        }
    }

    public void startListening(BlockingQueue<Message> messageQueue) {
        listening = true;
        listeningThread = new Thread(() -> {
            while (listening) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClientSocket(clientSocket, messageQueue);
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        LOGGER.log(Level.SEVERE, "Exception caught while listening for connections",
                                e);
                    }
                    break; // If the server socket is closed, we exit the loop
                }
            }
        });
        listeningThread.start();
    }

    private void handleClientSocket(Socket clientSocket, BlockingQueue<Message> messageQueue) {
        new Thread(() -> {
            try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {
                Message message = (Message) ois.readObject();
                messageQueue.put(message);
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Exception caught while handling client socket", e);
            } finally {
                try {
                    clientSocket.close(); // Close the client socket here, after handling it.
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Exception caught while closing client socket", e);
                }
            }
        }).start();
    }

    public void stopListening() {
        listening = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception caught while closing server socket", e);
        }
        if (listeningThread != null) {
            listeningThread.interrupt();
        }
    }

    public void sendMessage(Message message, Host targetHost) {
        queueMessage(new MessageTargetHost(message, targetHost));
    }

    public void queueMessage(MessageTargetHost messageTargetPair) {
        try {
            sendingQueue.put(messageTargetPair);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processMessageQueue() {
        while (sending) {
            try {
                MessageTargetHost pair = sendingQueue.take();
                send(pair.getMessage(), pair.getTargetHost());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /* Sends a message to a target host */
    private void send(Message message, Host targetHost) {
        try (Socket socket = new Socket(targetHost.getAddress(),
                targetHost.getPort());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject(message);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Send Message Exception caught", e);
        }
    }

    public Host getLocalHost() {
        return localHost;
    }
}
