/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import com.iqtb.validacion.excepcion.ExcepcionFTP;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class ConectarFTP {

    private Logger logger = Logger.getLogger(ConectarFTP.class);
    private FTPClient client = new FTPClient();

//    public static void main(String[] args) {
//        String sFTP = "xdanserver.t";
//        String sUser = "u980906958";
//        String sPassword = "seTeda";
//
//        ConectarFTP conectarFTP = new ConectarFTP();
//        conectarFTP.connectFTP(sFTP, sUser, sPassword);
//
//    }

    public boolean connectFTP(String host, String user, String pass) {
        boolean conn = false;
        boolean reintentar = false;
        int intentos = 0;

        do {
            intentos++;
            logger.info("Intento: " + intentos);
            try {
                logger.info("Conectandose a " + host);
                client.connect(host);
                verificarRespuesta(client.getReplyCode());
                boolean login = client.login(user, pass);

                if (login) {
                    client.enterLocalPassiveMode();
                    verificarRespuesta(client.getReplyCode());
                    conn = true;
                    logger.info("Login correcto");
                    boolean logout = client.logout();
                    if (logout) {
                        logger.info("Logout del servidor FTP");
                    }
                } else {
                    logger.error("Error en el login.");
                }

                logger.info("Desconectando.");
                client.disconnect();
            } catch (ExcepcionFTP e) {
                logger.error(e.getMessage() + " Se intentara de nuevo");
                reintentar = true;
            }catch (SocketException ex) {
                logger.error("ERROR: SocketException" + ex.getMessage());
                reintentar = true;//No estoy muy seguro de que valla true
            } catch (IOException ex) {
                logger.error("IOException: " + ex);
                reintentar = true;
            }
        } while (reintentar && (intentos < 5));
        if (reintentar) {
            logger.error("Se exedio el numero de intentos sin una conexion exitosa, verifique los datos");
        }
        return conn;
    }

    private void verificarCarpeta(String dirLocal) {
        File dir = new File(dirLocal);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void verificarRespuesta(int respuesta) throws ExcepcionFTP {
        if (!FTPReply.isPositiveCompletion(respuesta)) {
            throw new ExcepcionFTP("Error FTP, Codigo: " + respuesta);
        }
    }

}
