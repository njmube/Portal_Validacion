/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author danielromero
 */
public class CompararXML {

    public static class XmlDefaultHandler extends DefaultHandler {

        /**
         * @see org.xml.sax.ErrorHandler#error(SAXParseException)
         */
        public void error(SAXParseException spe) throws SAXException {
            throw spe;
        }

        /**
         * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
         */
        public void fatalError(SAXParseException spe) throws SAXException {
            throw spe;
        }
    }
}
