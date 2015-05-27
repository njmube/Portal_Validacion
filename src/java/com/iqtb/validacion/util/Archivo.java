/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class Archivo {

    private Logger logger = Logger.getLogger(Archivo.class);

    public String leer(String archivo) {
        File f = null;
        FileReader lectorArchivo = null;
        BufferedReader br = null;
        byte[] strbytes = null;
        String str;

        try {
            logger.debug("Creando objeto del archivo a leer");
            f = new File(archivo); //Creamos el objeto del archivo que vamos a leer
            
            lectorArchivo = new FileReader(f); //Creamos el objeto FileReader que abrira el flujo(Stream) de datos para realizar la lectura

            br = new BufferedReader(lectorArchivo); //Creamos un lector en buffer para recopilar datos a travez del flujo "lectorArchivo" que hemos creado

            String l = "";
            String aux = "";
            logger.debug("Inicia proceso de lectura");
            while (true) { //este ciclo while se usa para repetir el proceso de lectura, ya que se lee solo 1 linea de texto a la vez
                aux = br.readLine();
                if (aux != null) { //leemos una linea de texto y la guardamos en la variable auxiliar
                    l = l + aux + "\n";
                } else {
                    break;
                }
            }
//            br.close();
//            lectorArchivo.close();
            logger.debug("Termino proceso de lectura");
            strbytes = l.getBytes("UTF-8");
            str = new String(strbytes, "UTF-8");
            return str;

        } catch (UnsupportedEncodingException ex) {
            logger.error("leer - La codificacion no es soportada: " + ex);
        } catch (IOException e) {
            logger.error("leer - ERROR:" + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    logger.debug("Cerrando BufferedReader");
                    br.close();
                }
                if (lectorArchivo != null) {
                    logger.debug("Cerrando FileReader");
                    lectorArchivo.close();
                }
            } catch (IOException e) {
                logger.error("leer - finally ERROR: " + e);
            }

        }
        return null;
    }

}
