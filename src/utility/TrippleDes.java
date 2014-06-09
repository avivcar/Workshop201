package utility;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class TrippleDes {

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private static KeySpec ks;
    private static SecretKeyFactory skf;
    private static Cipher cipher;
    static byte[] arrayBytes;
    private static String myEncryptionKey;
    private static String myEncryptionScheme;
    static SecretKey key;

    public TrippleDes() throws Exception {
   
    }


    public static String encrypt(String unencryptedString) {
      
    	
        String encryptedString = null;
        try {
        	  myEncryptionKey = "MarinaTrosYakiyorekSlihi";
              myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
              arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
              ks = new DESedeKeySpec(arrayBytes);
              skf = SecretKeyFactory.getInstance(myEncryptionScheme);
              cipher = Cipher.getInstance(myEncryptionScheme);
              key = skf.generateSecret(ks);
          	
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encode(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }


    public static String decrypt(String encryptedString) {
        String decryptedText=null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decode(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText= new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }


 /*   public static void main(String args []) throws Exception
    {
        TrippleDes td= new TrippleDes();

        String target="hello";
        String encrypted=td.encrypt(target);
        String decrypted=td.decrypt(encrypted);

        System.out.println("String To Encrypt:"+ target);
        System.out.println("Encrypted String:" + encrypted);
        System.out.println("Decrypted String:" + decrypted);

    }*/

}