package CryptoChatClient;

import java.io.BufferedReader;
import java.io.IOException;

import static CryptoChatClient.Cryptographer.decrypt;

public class ReaderWorker extends Thread {

    private String recipient;
    private BufferedReader bufferedReader;
    private volatile boolean stop = false;

    public ReaderWorker(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run() {
        String msg;
        try {
            while (!stop) {
                String encryptedMsg;
                if (!(encryptedMsg = bufferedReader.readLine()).isEmpty()) {
                    msg = decrypt(encryptedMsg.getBytes());
                    System.out.println("> [" + recipient + "]:"
                            + encryptedMsg.substring(0, 10) + "..." + " -->> " + msg);
                }
            }
        } catch (IOException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void requestStop() {
        stop = true;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}

