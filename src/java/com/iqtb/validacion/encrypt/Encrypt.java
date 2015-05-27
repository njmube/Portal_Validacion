package com.iqtb.validacion.encrypt;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import net.sourceforge.lightcrypto.CryptoException;
import net.sourceforge.lightcrypto.PBECrypt;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author danielromero
 */
public class Encrypt {

    public static String getSALT(int size) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] seed = secureRandom.generateSeed(size);

        secureRandom = null;
        return Base64.encodeBase64String(seed);
    }

    public static String getSHA512(String cadena) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            md.update(cadena.getBytes());

            byte[] mb = md.digest();

            for (int i = 0; i < mb.length; i++) {
                sb.append(Integer.toString((mb[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {
            throw ex;
        }

        return sb.toString();
    }

    public static boolean verifySHASaltPassword(String testPass, String salt, String passKey) throws NoSuchAlgorithmException {
        String hash = getSHA512(testPass + salt);

        return hash.equals(passKey);
    }
    public static String getContraseniaAleatoria(int numCaracteres){
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ$*+#";
        String contrasenia = "";
        int longitud = base.length();

        for (int i = 0; i < numCaracteres; i++) { 
            int numero = (int) (Math.random() * (longitud)); 
            String caracter = base.substring(numero, numero + 1); 
            contrasenia = contrasenia + caracter; 
        }
        return contrasenia;
    }
    
    public static String encodeBase64(String str){
        
        return Base64.encodeBase64String(str.getBytes());
        
    }
    
    public static String decodeBase64(String str){
        byte[] decode = Base64.decodeBase64(str);
        return new String(decode);
    }
    
    public static String encrypt(String str) throws CryptoException, IOException{
        StringBuffer contrades = new StringBuffer(str); 
            StringBuffer encryptado = PBECrypt.encrypt(contrades, new StringBuffer("password"));
        
        return encryptado.toString();
    }
    
    public static String decrypt(String pass) throws CryptoException, IOException{          
            StringBuffer contrades;  
            
            contrades = PBECrypt.decrypt(new StringBuffer(pass.replace("%2B", "+")),new StringBuffer("password"));
            return contrades.toString();
    }

}
