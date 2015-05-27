/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import com.iqtb.validacion.dao.DaoCfdisRecibidos;
import com.iqtb.validacion.emun.EstadoCfdiR;
import com.iqtb.validacion.jasper.JasperUtils;
import com.iqtb.validacion.pojo.CfdisRecibidos;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

/**
 *
 * @author joaquin
 */
public class GenerarArchivo {

    private Logger logger = Logger.getLogger(GenerarArchivo.class);

    public File getArchivoXML(List<Integer> idsCfdisR, String ruta) {
        File dir = new File(ruta);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            DaoCfdisRecibidos daoCfdiRecibido = new DaoCfdisRecibidos();
            CfdisRecibidos cfdiRecibido;
            File archivoXML;
            if (idsCfdisR.size() > 1) {
                List lista = new ArrayList();
                int cont = 0;
                boolean ban = true;
                for (Integer idCfdi : idsCfdisR) {
                    logger.debug("Obteniendo el cfdiRecibido con id " + idCfdi);
                    cfdiRecibido = daoCfdiRecibido.getCfdiByID(idCfdi);
                    if (cfdiRecibido != null) {
                        if (!cfdiRecibido.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                            archivoXML = new File(ruta + cfdiRecibido.getNombreArchivo().replace(" ", "") + ".xml");
                            do {
                                if (lista.contains(archivoXML)) {
                                    cont++;
                                    archivoXML = new File(ruta + cfdiRecibido.getNombreArchivo().replace(" ", "") + "(" + cont + ").xml");
                                } else {
                                    crearXML(cfdiRecibido.getXmlSat(), archivoXML);
                                    lista.add(archivoXML);
                                    ban = false;
                                }
                            } while (ban);
                        } else {
                            logger.info("El archivo xml no se generara. cfdiRecibido id " + cfdiRecibido.getIdCfdiRecibido() + " estado " + cfdiRecibido.getEstado());
                        }
                    } else {
                        logger.error("No se pudo obtener el cfdiRecibido con id " + idCfdi);
                    }
                    cfdiRecibido = null;
                }
                File destino = new File(ruta + idsCfdisR.size() + "_XMLs.zip");
                new CrearZip().zip(lista, destino.getAbsolutePath());
                return destino;
            } else {
                cfdiRecibido = null;
                logger.info("Obteniendo el cfdiRecibido con id " + idsCfdisR.get(0));
                cfdiRecibido = daoCfdiRecibido.getCfdiByID(idsCfdisR.get(0));
                String nArchivo = cfdiRecibido.getNombreArchivo().replace(" ", "") + ".xml";
                archivoXML = new File(ruta + nArchivo);
                crearXML(cfdiRecibido.getXmlSat(), archivoXML);
                return archivoXML;
            }
        } catch (Exception e) {
            logger.error("getArchivoXML ERROR " + e.getMessage());
        } finally {
            idsCfdisR = null;
        }
        return null;
    }

    public File getArchivoPDF(List<Integer> idsCfdisR, String ruta, String rutaPlantilla) {
        File dir = new File(ruta);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File plantilla = new File(rutaPlantilla);
            JasperUtils jasperUtils = new JasperUtils();
            JasperPrint jasperPrint;
            DaoCfdisRecibidos daoCfdiRecibido = new DaoCfdisRecibidos();
            CfdisRecibidos cfdiRecibido;
            File archivoPDF;
            if (idsCfdisR.size() > 1) {
                List lista = new ArrayList();
                int cont = 0;
                boolean ban = true;
                for (Integer idCfdi : idsCfdisR) {
                    logger.debug("Obteniendo el cfdiRecibido con id " + idCfdi);
                    cfdiRecibido = daoCfdiRecibido.getCfdiByID(idCfdi);
                    if (cfdiRecibido != null) {
                        if (!cfdiRecibido.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                            archivoPDF = new File(ruta + cfdiRecibido.getNombreArchivo().replace(" ", "") + ".pdf");
                            do {
                                if (lista.contains(archivoPDF)) {
                                    cont++;
                                    archivoPDF = new File(ruta + cfdiRecibido.getNombreArchivo().replace(" ", "") + "(" + cont + ").xml");
                                } else {
                                    jasperPrint = jasperUtils.fill(cfdiRecibido.getXmlSat(), plantilla.getAbsolutePath());
                                    try {
                                        jasperUtils.generatePdfFile(jasperPrint, archivoPDF.getAbsolutePath());
                                        lista.add(archivoPDF);
                                    } catch (JRException ex) {
                                        logger.error("Algo salio mal al copilar la plantilla con el XML: " + ex.getMessage());
                                    }
                                    ban = false;
                                }
                            } while (ban);
                        } else {
                            logger.info("El archivo pdf no se generara. cfdiRecibido id " + cfdiRecibido.getIdCfdiRecibido() + " estado " + cfdiRecibido.getEstado());
                        }
                    } else {
                        logger.error("No se pudo obtener el cfdiRecibido con id " + idCfdi);
                    }
                    cfdiRecibido = null;
                    jasperPrint = null;
                }
                File destino = new File(ruta + lista.size() + "_PDFs.zip");
                new CrearZip().zip(lista, destino.getAbsolutePath());
                return destino;
            } else {
                cfdiRecibido = daoCfdiRecibido.getCfdiByID(idsCfdisR.get(0));
                String nArchivo = cfdiRecibido.getNombreArchivo().replace(" ", "") + ".pdf";
                jasperPrint = jasperUtils.fill(cfdiRecibido.getXmlSat(), plantilla.getAbsolutePath());
                archivoPDF = new File(ruta + nArchivo);
                try {
                    jasperUtils.generatePdfFile(jasperPrint, archivoPDF.getAbsolutePath());
                } catch (JRException ex) {
                    logger.error("Algo salio mal al copilar la plantilla con el XML: " + ex.getMessage());
                }
                jasperPrint = null;
                return archivoPDF;
            }
        } catch (Exception e) {
            logger.error("getArchivoPDF ERROR " + e.getMessage());
        }
        return null;
    }

    public File getArchivoAMBOS(List<Integer> idsCfdisR, String ruta, String rutaPlantilla) {
        File dir = new File(ruta);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File plantilla = new File(rutaPlantilla);
        List lista = new ArrayList();
        JasperUtils jasperUtils;
        JasperPrint jasperPrint;
        jasperUtils = new JasperUtils();
        DaoCfdisRecibidos daoCfdiRecibido = new DaoCfdisRecibidos();
        CfdisRecibidos cfdiRecibido;
        File archivoXML;
        File archivoPDF;
        String nArchivo = "";
        int cont = 0;
        boolean ban = true;
        try {
            for (Integer idCfdi : idsCfdisR) {
                logger.debug("Obteniendo el cfdiRecibido con id " + idCfdi);
                cfdiRecibido = daoCfdiRecibido.getCfdiByID(idCfdi);
                if (cfdiRecibido != null) {
                    if (!cfdiRecibido.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                        nArchivo = cfdiRecibido.getNombreArchivo().replace(" ", "");
                        archivoXML = new File(ruta + nArchivo + ".xml");
                        archivoPDF = new File(ruta + nArchivo + ".pdf");
                        do {
                            if (lista.contains(archivoXML)) {
                                cont++;
                                archivoXML = new File(ruta + nArchivo + "(" + cont + ").xml");
                                archivoPDF = new File(ruta + nArchivo + "(" + cont + ").xml");
                            } else {
                                try {
                                    crearXML(cfdiRecibido.getXmlSat(), archivoXML);

                                    lista.add(archivoXML);
                                    if (plantilla.exists()) {
                                        //Creando PDF
                                        jasperPrint = jasperUtils.fill(cfdiRecibido.getXmlSat(), plantilla.getAbsolutePath());

                                        jasperUtils.generatePdfFile(jasperPrint, archivoPDF.getAbsolutePath());
                                        lista.add(archivoPDF);
                                    }
                                } catch (JRException ex) {
                                    logger.error("Algo salio mal al copilar la plantilla con el XML: " + ex.getMessage());
                                }
                                ban = false;
                            }
                        } while (ban);
                    } else {
                        logger.info("El archivo xml no se generara. cfdiRecibido id " + cfdiRecibido.getIdCfdiRecibido() + " estado " + cfdiRecibido.getEstado());
                    }
                } else {
                    logger.error("No se pudo obtener el cfdiRecibido con id " + idCfdi);
                }
                cfdiRecibido = null;
                jasperPrint = null;
            }
            //Creado ZIP
            File destino;
            if (idsCfdisR.size() == 1) {
                destino = new File(ruta + nArchivo + ".zip");
            } else {
                destino = new File(ruta + idsCfdisR.size() + "_CFDIs.zip");
            }

            if (new CrearZip().zip(lista, destino.getAbsolutePath())) {
                return destino;
            }
        } catch (Exception e) {
            logger.error("getArchivoAMBOS ERROR " + e.getMessage());
        }
        return null;
    }

    private boolean crearXML(String xml, File archivo) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        boolean correcto = false;
        try {

            fichero = new FileWriter(archivo.getAbsolutePath());
            logger.info("Creando Archvio " + archivo.getName());
            PrintWriter writer = new PrintWriter(archivo.getAbsolutePath(), "UTF-8");
            logger.info("Inicia escritura");
            writer.println(xml);
            writer.close();
            logger.info("termina escritura");
            correcto = true;
        } catch (IOException e) {
            logger.error("generarXML escribiendo ERROR " + e);
            correcto = false;
        } finally {
            try {
                if (null != fichero) {
                    fichero.close();
                }
            } catch (IOException e2) {
                logger.error("generarXML cerrando ERROR " + e2);
            }
        }
        return correcto;
    }

}

///PDF
//if (listaCfdis.size() > 1) {
//            List lista = new ArrayList();
//            for (int i = 0; i < listaCfdis.size(); i++) {
//                File pdf = new File(ruta + listaCfdis.get(i).getNombreArchivo().replace(" ", "") + ".pdf");
//                int cont = 0;
//                boolean ban = true;
//                do {
//                    if (lista.contains(pdf)) {
//                        cont++;
//                        pdf = new File(ruta + listaCfdis.get(i).getNombreArchivo().replace(" ", "") + "(" + cont + ").pdf");
//                    } else {
//                        jasperPrint = jasperUtils.fill(listaCfdis.get(i).getXmlSat(), plantilla.getAbsolutePath());
//                        try {
//                            jasperUtils.generatePdfFile(jasperPrint, pdf.getAbsolutePath());
//                            lista.add(pdf);
//                        } catch (JRException ex) {
//                            logger.error("Algo salio mal al copilar la plantilla con el XML: " + ex.getMessage());
//                        }
//                        ban = false;
//                    }
//                } while (ban);
//            }
//            File destino = new File(ruta + listaCfdis.size() + "_PDFs.zip");
//            new CrearZip().zip(lista, destino.getAbsolutePath());
//
//            jasperPrint = null;
//            return destino;
//        } else {
//            String nArchivo = listaCfdis.get(0).getNombreArchivo().replace(" ", "") + ".pdf";
//            jasperPrint = jasperUtils.fill(listaCfdis.get(0).getXmlSat(), plantilla.getAbsolutePath());
//            File pdf = new File(ruta + nArchivo);
//            try {
//                jasperUtils.generatePdfFile(jasperPrint, pdf.getAbsolutePath());
//            } catch (JRException ex) {
//                logger.error("Algo salio mal al copilar la plantilla con el XML: " + ex.getMessage());
//            }
//            jasperPrint = null;
//            return pdf;
//        }
// AMBOS
//for (int i = 0; i < listaCfdis.size(); i++) {
//            nArchivo = listaCfdis.get(i).getNombreArchivo().replace(" ", "");
//            //Creando XML
//            File archivoXML = new File(ruta + nArchivo + ".xml");
//            File pdf = new File(ruta + nArchivo + ".pdf");
//            int cont = 0;
//            boolean ban = true;
//            do {
//                if (lista.contains(archivoXML)) {
//                    cont++;
//                    archivoXML = new File(ruta + nArchivo + "(" + cont + ").xml");
//                    pdf = new File(ruta + nArchivo + "(" + cont + ").pdf");
//                } else {
//                    try {
//                        crearXML(listaCfdis.get(i).getXmlSat(), archivoXML);
//
//                        lista.add(archivoXML);
//                        if (plantilla.exists()) {
//                            //Creando PDF
//                            jasperPrint = jasperUtils.fill(listaCfdis.get(i).getXmlSat(), plantilla.getAbsolutePath());
//
//                            jasperUtils.generatePdfFile(jasperPrint, pdf.getAbsolutePath());
//                            lista.add(pdf);
//                        }
//                    } catch (JRException ex) {
//                        logger.error("Algo salio mal al copilar la plantilla con el XML: " + ex.getMessage());
//                    }
//                    ban = false;
//                }
//            } while (ban);
//
//        }
//        jasperPrint = null;
//
//        //Creado ZIP
//        File destino;
//        if (listaCfdis.size() == 1) {
//            destino = new File(ruta + nArchivo + ".zip");
//        } else {
//            destino = new File(ruta + listaCfdis.size() + "_CFDIs.zip");
//        }
//
//        if (new CrearZip().zip(lista, destino.getAbsolutePath())) {
//            return destino;
//        } else {
//            return null;
//        }
