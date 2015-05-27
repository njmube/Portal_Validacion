/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 *
 * @author danielromero
 */
public class RevisarXML {
    
    private Document document;
    private Logger logger = Logger.getLogger(RevisarXML.class);
    
    public RevisarXML(InputStream archivoXML) {
        SAXBuilder builder = new SAXBuilder();
        try {
            logger.debug("Creando el builder: ");
            //Se crea el documento a traves del archivo
            document = (Document) builder.build(archivoXML);
            logger.debug("El documentos: " + document.toString());
        } catch (JDOMException ex) {
            logger.error("Error en JDOM: " + ex.getMessage());
            logger.error("La causa: " + ex.getCause());
            logger.error("La localizacion: " + ex.getLocalizedMessage());
//            throw new JDOMException(ex.getMessage());
        } catch (IOException ex) {
            logger.error("Error en IOEception: " + ex.getMessage());
        } finally {
            archivoXML = null;
        }
    }

    /**
     * Revisa cualquier nodo pasandole como parametros: atributo, XPath y el
     * nodo a evaluar
     *
     * @param atributo Es el nombre del atributo que se desea obtener para sacar
     * su valor
     * @param rutaXpath Es la ruta del nodo al que se desea accesar
     * @param nodoEvaluar Es el nodo donde se entcuentra el atributo pasado
     * anteriormente para poder saber su ubicacion
     * @return El valor del atributo deseado
     */
    public String revisaNodos(String atributo, String rutaXpath, String nodoEvaluar) {//File ArchivoXML,
        String valorAtributo = "";
        try {
            try {
                Element nodo = (Element) XPath.selectSingleNode(document, rutaXpath);
                List listTfd = nodo.getChildren();
                for (int i = 0; i < listTfd.size(); i++) {
                    Element tfd = (Element) listTfd.get(i);
                    if (tfd.getName().equals(nodoEvaluar)) {
                        valorAtributo = tfd.getAttributeValue(atributo);
                        logger.debug("El atributo " + atributo + " contiene: " + valorAtributo);
                    }
                }
            } catch (NullPointerException e) {
                logger.error("El atributo " + atributo + " no se encuentra ERROR: " + e.getMessage());
            }
            
        } catch (JDOMException ex) {
            logger.error("Error en JDOM: " + ex.getMessage());
//            valorAtributo="Error en JDOM:"+ex.getMessage();
        }
        return valorAtributo;
    }
    
}
