package com.iqtb.validacion.jasper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public class JasperUtils {

    public static final String COMPILE_TEMPLATE_EXTENSION = ".jasper";
    public static final String SOURCE_TEMPLATE_EXTENSION = ".xprint";
    public static final String COMPILE_REPORT_EXTENSION = ".jrprint";
    public static final String PDF_EXTENSION = ".pdf";
    private static Logger logger = Logger.getLogger(JasperUtils.class);

    public JasperUtils() {
        logger.info("JASPER REPORTS ENGINE");
    }

    public JasperPrint fill(String XML, String dirJasper) {
        logger.info("Generando vista a partir de: " + dirJasper);
        Map<String, Object> params = new HashMap<String, Object>();
        Document document = null;
        try {
            InputStream is = new ByteArrayInputStream(XML.getBytes("UTF-8"));
            document = JRXmlUtils.parse(is);
            logger.info("XML Cargado");
            logger.info("Documento: " + document.toString());
        } catch (JRException e1) {
            logger.error("Error al cargar XML: " + XML + e1.getMessage());
            logger.error("Error en parse: " + e1.getMessage());
            return null;
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error encoding: " + ex.getMessage());
            return null;
        }
        logger.info("Colocando Parametros");
        params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
        params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "dd-MM-yyyy");
        params.put(JRXPathQueryExecuterFactory.XML_LOCALE, Locale.ENGLISH);
        logger.info("Parametros Colocandos");
        try {
            logger.info("Generando Vista " + dirJasper);
            JasperPrint jasper = JasperFillManager.fillReport(dirJasper, params);
            logger.info("Vista: " + dirJasper + " Generada");
            return jasper;
        } catch (JRException e) {
            logger.error("Error al generar vista: " + dirJasper);
            logger.error("Error en el fillREport: " + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error generando la vista: " + dirJasper);
            logger.error("ERROR: " + e.getMessage());
            return null;
        }
    }

    public void generatePdfFile(JasperPrint paramJasperPrint, String paramString) throws JRException {
        JasperExportManager.exportReportToPdfFile(paramJasperPrint, paramString);
        logger.info("Generacion exitosa, archivo PDF: " + paramString);
    }

    public byte[] getPdfAsBytesArray(JasperPrint paramJasperPrint) throws JRException {
        byte[] arrayOfByte = JasperExportManager.exportReportToPdf(paramJasperPrint);
        logger.info("Generacion exitosa, retornando archivo PDF en un arreglo de bytes.");
        return arrayOfByte;
    }
}
