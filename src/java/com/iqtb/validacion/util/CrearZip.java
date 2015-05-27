/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author joaquin
 */
public class CrearZip {
    private Logger logger = Logger.getLogger(CrearZip.class);
    /**
     * 
     * @param files lista de Objetos "File", contiene los archivos a agregar an el Zip
     * @param destino ruta donde se creara el zip
     * @return 
     */
    public boolean zip(List files,String destino) {

        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(destino);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            int BUFFER_SIZE = 128;
            byte[] data = new byte[BUFFER_SIZE];
            
            for (Iterator i = files.iterator(); i.hasNext();) {
                
                File filename = (File) i.next();
                logger.info("Agregando: " + filename.getAbsolutePath());
                FileInputStream fi = new FileInputStream(filename.getAbsoluteFile());
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                
                ZipEntry entry = new ZipEntry(filename.getName());
                out.putNextEntry(entry);
                
                int count;
                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                    out.write(data, 0, count);
                }
                
                filename.delete();
                origin.close();
            }
            
            out.close();
            return true;
        } catch (IOException e) {
            logger.error("IOException "+e.getMessage());
            return false;
        }
    }
}
