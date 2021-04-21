package com.example.sensacion;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Los helpers son clases de Java que contienen información genérica que se utilizará
 * dentro del proyecto en distintas ocasiones
 *
 * Los Helpers deben ser clases finales utilizando el patrón de diseño
 * Singleton
 *
 */

//final indica que esta clase NO PUEDE HEREDARSE
public final class AppHelper {

    /*
    Evitamos la creación de instacias creando un constructor
    privado
     */
    private AppHelper (){

    }

    /**
     * Metodo que encripta una cadena de texto en md5
     * @param s
     * @return
     */
    public  static String MD5_Hash (String s){
        MessageDigest m = null;

        try
        {
            m = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }
}
