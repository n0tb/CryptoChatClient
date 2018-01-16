package CryptoChatClient;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;

import static CryptoChatClient.Cryptographer.*;

public class CryptoChatApp {

    static String login;
    static String pass;
    static String pubKeyBase64;

    public static void main(String[] args) {

        String msg;
        String clients;
        String recipient;
        String pubKeyRecipStr;
        int serverPort = 9999;
        String serverAddr = "127.0.0.1";
        String fileName = "*\\Java\\Sockets\\" +
                "CryptoChatClient\\src\\main\\resources\\pubKey";

        try (Socket socket = new Socket(serverAddr, serverPort);
             PrintWriter output = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader input = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {


            System.out.println("Connect to server [ "
                    + serverAddr + ":" + serverPort + "]");
            login(output, input, scanner, fileName);

            ReaderWorker readerWorker = new ReaderWorker(input);
            while (true) {
                clients = input.readLine();
                System.out.println("List of users: " + clients);
                while (true) {
                    System.out.println("Select user:");
                    recipient = scanner.next();
                    output.println(recipient);
                    if (recipient.equals("UPDT")) {
                        clients = input.readLine();
                        System.out.println("List of users: " + clients);
                        continue;
                    } else break;
                }

                pubKeyRecipStr = input.readLine();
                PublicKey pubKeyRecip = formatToPubKey(pubKeyRecipStr);

                readerWorker.setRecipient(recipient);
                readerWorker.start();

                System.out.println("Enter message:");
                while (true) {
                    msg = scanner.next();
                    if (msg.equals("CLOSE") || msg.equals("QUIT")) {
                        output.println(msg);
                        break;
                    }

                    byte[] encryptMsg = encrypt(msg, pubKeyRecip);
                    byte[] encryptMsgBase64 = Base64.getEncoder().encode(encryptMsg);
                    output.println(new String(encryptMsgBase64));
                    System.out.println("ChiphroText:" +
                            new String(encryptMsgBase64).substring(0, 10) + "...");
                }
                if (msg.equals("QUIT")) {
                    readerWorker.requestStop();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void login(PrintWriter output, BufferedReader input,
                              Scanner scanner, String fileName){
        String status;
        String succLogin;

        try {
            System.out.println("1-LOGIN | 2-REGISTRATION");
            status = scanner.next();
            output.println(status);
            if (status.equals("2")) {
                System.out.println("[REGISTRATION]\nEnter login:");
                login = scanner.next();
                System.out.println("Enter password:");
                pass = scanner.next();
                output.println(login);
                output.println(pass);
                System.out.println("Registration  successfully completed!");

                generateKey();
                saveKeysToFile(fileName);
                output.println(pubKeyBase64);
            }

            while (true) {
                System.out.println("[LOGIN]\nEnter login:");
                login = scanner.next();
                System.out.println("Enter password:");
                pass = scanner.next();
                output.println(login);
                output.println(pass);
                succLogin = input.readLine();
                if (succLogin.equals("false")) {
                    System.out.println("Authorisation Error(");
                    continue;
                } else {
                    System.out.println("Login successfully complete!");

                    String fileNamePubKey = fileName +
                            login.toUpperCase() + ".txt";
                    String fileNamePrivKey = fileName.replace("pubKey", "privKey")
                            + login.toUpperCase() + ".txt";

                    publicKey = getPubKey(fileNamePubKey);
                    pubKeyBase64 = new String(Base64
                            .getEncoder().encode(publicKey.getEncoded()));
                    privateKey = getPrivKey(fileNamePrivKey);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveKeysToFile(String fileName) {
        File pubKeyFile = new File(fileName +
                login.toUpperCase() + ".txt");
        File privKeyFile = new File(fileName.replace("pubKey", "privKey")
                + login.toUpperCase() + ".txt");

        try (FileWriter fileWriter = new FileWriter(pubKeyFile);
             FileWriter fileWriterPrivK = new FileWriter(privKeyFile)){

            pubKeyBase64 = new String(Base64.getEncoder()
                    .encode(publicKey.getEncoded()));
            fileWriter.write(pubKeyBase64);

            String privKeyBase64 = new String(Base64.getEncoder()
                    .encode(privateKey.getEncoded()));
            fileWriterPrivK.write(privKeyBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
