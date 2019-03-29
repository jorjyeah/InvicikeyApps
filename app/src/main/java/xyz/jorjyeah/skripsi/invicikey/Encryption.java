package xyz.jorjyeah.skripsi.invicikey;

import android.content.Context;
import android.nfc.Tag;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.jar.JarFile;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

import static android.content.ContentValues.TAG;

public class Encryption {
    // function for generate key handle
    public static String generateKeyHandle(){
        SecureRandom random = new SecureRandom();
        byte[] array = new byte[32];
        random.nextBytes(array);
        return bytesToHex(array);
    }
    private static String bytesToHex(byte[] bytes){
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for(int i = 0; i<bytes.length; i++){
            int x = bytes[i] & 0xFF;
            hexChars[i*2] = hexArray[x >>> 4];
            hexChars[i*2+1] = hexArray[x & 0x0F];
        }
        return new String(hexChars);
    }

    // function for generate new key pair and return public key
    public static String createKey(String alias, Context context) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, UnrecoverableEntryException, KeyStoreException {
        // if there is no key handle, create first
        try{
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if(!keyStore.containsAlias(alias)){
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setKeyType(KeyProperties.KEY_ALGORITHM_RSA)
                        .setKeySize(2048)
                        .setSubject(new X500Principal("CN=test"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
                Log.v("RSA", "new pair created");
            }
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);

            PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

            PrivateKey privateKey = privateKeyEntry.getPrivateKey();

            Log.v("RSA Pub Key", Base64.encodeToString(publicKey.getEncoded(),Base64.DEFAULT));

            // let's prove encrypt and decrypt here

            return Base64.encodeToString(publicKey.getEncoded(),Base64.DEFAULT);
        } catch(Exception e){
            Log.v("RSA", "S*Thing went wrong");
            e.printStackTrace();

        }
        return null;
    }

    public static String encrypt(String alias, String plain){
        try {
            PublicKey publicKey = getPrivateEntry(alias).getCertificate().getPublicKey();
            PrivateKey privateKey = getPrivateEntry(alias).getPrivateKey();
            Log.v(TAG,"uncrypted: "+plain);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            Log.v(TAG, "encrypted in base64: "+Base64.encodeToString(cipher.doFinal(plain.getBytes()), Base64.DEFAULT));
            return Base64.encodeToString(cipher.doFinal(plain.getBytes()), Base64.DEFAULT);
        } catch (Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
            return "error";
        }
    }

    public static String decrypt(String alias, String chipertext){
        try {
            PublicKey publicKey = getPrivateEntry(alias).getCertificate().getPublicKey();
            PrivateKey privateKey = getPrivateEntry(alias).getPrivateKey();
            Log.v(TAG,"cipher in base64: "+chipertext);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            String decrypted = new String(cipher.doFinal(Base64.decode(chipertext, Base64.DEFAULT)));
            Log.v(TAG, "decrypted: "+decrypted);
            return decrypted;
        } catch (Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
            return "error";
        }
    }

    private static KeyStore.PrivateKeyEntry getPrivateEntry(String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(alias,null);
            if(entry == null){
                return null;
            }
            if(!(entry instanceof KeyStore.PrivateKeyEntry)){
                return null;
            }
            return (KeyStore.PrivateKeyEntry) entry;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    public static ArrayList<String> getAllAliasesInTheKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return Collections.list(keyStore.aliases());
    }

    public static String getKeyCount() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return String.valueOf(keyStore.size());
    }

    public static void deleteEntry(String alias) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        keyStore.deleteEntry(alias);
    }
}
