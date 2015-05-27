/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;

/**
 *
 * @author Valentin
 */
public class TratarConexiones {
    
    private URLConnection connection;
    private HttpURLConnection httpConn;
    Logger logger = Logger.getLogger("TratarConexiones");
    
    public Boolean revisarURL(URL url){
        try {    
            connection = url.openConnection();
            connection.setReadTimeout(60000);
            logger.debug("Realizando la conexion...");
            httpConn = (HttpURLConnection)connection;
            if(httpConn.getResponseMessage().trim().startsWith("OK")||Integer.toString(httpConn.getResponseCode()).trim().charAt(0)=='2')
                return true;
        } catch (IOException ex) {
            logger.error("RUTA ERRONEA");
            logger.error("ERROR imposible realizar conexion a este WS: "+ex.getMessage());
        } finally{
            connection=null;
            httpConn=null;
        }
       return false;
    }
    
}
