/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author danielromero
 */
public class LeerPDF {

    PDFParser parser;
    String parsedText;
    PDFTextStripper pdfStripper;
    PDDocument pdDoc;
    COSDocument cosDoc;
    PDDocumentInformation pdDocInfo;
    private static Logger logger = Logger.getLogger(LeerPDF.class);

    public LeerPDF() {
    }

    public String pdftoText(byte[] bytesPdf) {

        InputStream in = new ByteArrayInputStream(bytesPdf);

        // Se verifica si se puede abrir el InputStream
        try {
            parser = new PDFParser(in);
        } catch (IOException e) {
            logger.error("No se puede abrir. ERROR "+e);
            return null;
        }

        // En este proceso se abre, convierte y se cierra
        // el archivo PDF
        try {
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
            cosDoc.close();
            pdDoc.close();

        } catch (IOException e) {
            logger.error("Ocurrió un error. ERROR "+e);
            try {
                if (cosDoc != null) {
                    cosDoc.close();
                }
                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (IOException e1) {
                logger.error("Ocurrió un error. ERROR "+e1);
            }

            return null;
        }

        return parsedText;
    }

}
