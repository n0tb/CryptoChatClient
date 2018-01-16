package CryptoChatClient;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Cryptographer {

    static PublicKey publicKey;
    static PrivateKey privateKey;

    public static void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024, new SecureRandom());
            final KeyPair key = keyGen.generateKeyPair();
            publicKey = key.getPublic();
            privateKey = key.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(String msg, PublicKey pubKeyRecip) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKeyRecip);
            cipherText = cipher.doFinal(msg.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public static String decrypt(byte[] msgByteBase64) {
        byte[] decryptMsg = null;
        byte[] msgByte = Base64.getMimeDecoder().decode(msgByteBase64);
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptMsg = cipher.doFinal(msgByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decryptMsg);
    }

    public static PublicKey getPubKey(String fileNama) {
        PublicKey pubKey = null;
        String pubKeyBase64;
        File file = new File(fileNama);
        try (BufferedReader reader = new BufferedReader
                (new FileReader(file))) {

            pubKeyBase64 = reader.readLine();
            pubKey = formatToPubKey(pubKeyBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubKey;
    }

    public static PrivateKey getPrivKey(String fileName) {
        PrivateKey privKey = null;
        String privKeyBase64;
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader
                (new FileReader(file))) {

            privKeyBase64 = reader.readLine();
            byte[] privKeyByte = Base64.getMimeDecoder().decode(privKeyBase64);
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKeyByte);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privKey = kf.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privKey;
    }

    public static PublicKey formatToPubKey(String pubKeyBase64) {
        PublicKey pubKey = null;
        try {
            byte[] pubKeyByte = Base64.getMimeDecoder().decode(pubKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyByte);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            pubKey = kf.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubKey;
    }
}
