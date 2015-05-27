/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class ArchivoBaseDatos {

    private static Logger logger = Logger.getLogger(ArchivoBaseDatos.class);

//    public void guardarArchivo(String rutaArchivo, CfdisRecibidos cfdi) {
//        {
//            FileInputStream fis = null;
//            try {
//                File file = new File(rutaArchivo);
//                logger.info("Archivo cargado " + file.getAbsolutePath());
//                byte[] b = getBytesFromFile(file);
//
//                cfdi.setPdf(b);
//                if (new DaoArchivos().insertArchivo(archivo)) {
//                    System.out.println("Archivo insertado en base de datos");
//                } else {
//                    System.out.println("Algo salio mal no se inserto el archoivo en base de datos");
//                }
//
//            } catch (FileNotFoundException ex) {
//                System.out.println("ERROR FileNotFoundException" + ex.getMessage());
//            } catch (IOException ex) {
//                System.out.println("ERROR IOException" + ex);
//            } catch (Exception ex) {
//                System.out.println("ERROR Exception " + ex);
//            }
//        }
//    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

}
