/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoCfdisRecibidos;
import com.iqtb.validacion.dao.DaoConfiguracionEmpresa;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoOrdenCompra;
import com.iqtb.validacion.dao.DaoPlantilla;
import com.iqtb.validacion.dao.DaoSociosComerciales;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.ConfigEmpresa;
import com.iqtb.validacion.emun.EstadoCfdiR;
import com.iqtb.validacion.jasper.JasperUtils;
import com.iqtb.validacion.pojo.CfdisRecibidos;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.OrdenesCompra;
import com.iqtb.validacion.pojo.Plantillas;
import com.iqtb.validacion.pojo.SociosComerciales;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.ArchivoBaseDatos.getBytesFromFile;
import com.iqtb.validacion.util.AuxOrdenCompra;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.LeerPDF;
import com.iqtb.validacion.util.ManejoArchivos;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbOrdenCompra implements Serializable {

    private OrdenesCompra ordenCompra;
    private DaoOrdenCompra daoOrdenCompra;
    private List<OrdenesCompra> listaOrden;
    private List<AuxOrdenCompra> listaAuxOrden;
    private AuxOrdenCompra auxOrdenCompra;
    private LazyDataModel<AuxOrdenCompra> lazyDataOrden;
    private List<AuxOrdenCompra> auxOrdenSeleccionados;
    private LazyDataModel<CfdisRecibidos> lazyDataCfdi;
    private List<CfdisRecibidos> listaCFDIs;
    private CfdisRecibidos cfdiRecibido;
    private DaoCfdisRecibidos daoCfdiRecibido;
    private DaoPlantilla daoPlantilla;
    private Session session;
    private Transaction tx;
    private String query;
    private String queryCount;
    private List<Integer> listaIdSocio;
    private String filterProperty;
    private String filterValue;
    private DaoSociosComerciales daoSociosComerciales;
    private DaoUsuario daoUsuario;
    private DaoEmpresa daoEmpresa;
    private Empresas empresa;
    private Usuarios usuario;
    private String empresaSeleccionada;
    private String sessionUsuario;
    private String descBitacora;
    private ConfiguracionesEmpresas congifEmpresa;
    private DaoConfiguracionEmpresa daoConfigEmpresa;
//    private SelectItem[] itemsTipoMoneda;
    private List<SelectItem> itemsSocioComercial;
    private List<SociosComerciales> listaSociosComerciales;
    private SelectItem selectItem;
    private static final Logger logger = Logger.getLogger(MbOrdenCompra.class);

    public MbOrdenCompra() {

        ordenCompra = new OrdenesCompra();
        daoUsuario = new DaoUsuario();
        daoEmpresa = new DaoEmpresa();
        daoSociosComerciales = new DaoSociosComerciales();
        daoCfdiRecibido = new DaoCfdisRecibidos();
        daoPlantilla = new DaoPlantilla();
        congifEmpresa = new ConfiguracionesEmpresas();
        daoConfigEmpresa = new DaoConfiguracionEmpresa();
        listaSociosComerciales = new ArrayList<SociosComerciales>();
        daoOrdenCompra = new DaoOrdenCompra();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = daoUsuario.getByUserid(sessionUsuario);
            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
        } catch (Exception e) {
            logger.error("Error obteniendo empresa " + e);
        }
//        itemsTipoMoneda = new SelectItem[4];
//        itemsTipoMoneda[0] = new SelectItem("", "TODOS");
//        itemsTipoMoneda[1] = new SelectItem("MXN", "MXN");
//        itemsTipoMoneda[2] = new SelectItem("USD", "USD");
//        itemsTipoMoneda[3] = new SelectItem("EUR", "EUR");
    }

    @PostConstruct
    public void init() {
        lazyDataOrden = new LazyDataModel<AuxOrdenCompra>() {
            @Override
            public List<AuxOrdenCompra> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                listaAuxOrden = new ArrayList<AuxOrdenCompra>();
                listaOrden = new ArrayList<OrdenesCompra>();
                try {
                    queryCount = "select count(idOrdenCompra) ";
                    query = "from OrdenesCompra where idEmpresa = " + empresa.getIdEmpresa();
                    if (!filters.isEmpty()) {
                        Iterator it = filters.keySet().iterator();
                        while (it.hasNext()) {
                            filterProperty = (String) it.next();
                            filterValue = filters.get(filterProperty);
                            if (filterProperty.equals("numeroOc")) {
                                query = query + " and " + filterProperty + " = '" + filterValue + "'";
                            }
                            if (filterProperty.equals("rfcVendedor") || filterProperty.equals("total") || filterProperty.equals("tipoCambio") || filterProperty.equals("tipoMoneda")) {
                                query = query + " and " + filterProperty + " like '" + filterValue + "%'";
                            }
                            if (filterProperty.equals("rfcSocioComercial")) {
                                listaIdSocio = daoSociosComerciales.filtroSocioComercialByRFC(empresa.getIdEmpresa(), filterValue);
                                for (Integer idSocio : listaIdSocio) {
                                    query = query + " and idSocioComercial = " + idSocio;
                                }
                            }
                        }
                    }
                    if (sortField != null && !sortField.isEmpty()) {
                        if (sortOrder.equals(SortOrder.ASCENDING)) {
                            query = query + " ORDER BY " + sortField + " ASC";
                        } else {
                            query = query + " ORDER BY " + sortField + " DESC";
                        }
                    } else {
                        query = query + " ORDER BY idOrdenCompra DESC";
                    }
                    queryCount = queryCount + query;
                    Query result = session.createQuery(query);
                    result.setMaxResults(pageSize + first);
                    result.setFirstResult(first);
                    this.setRowCount(((Long) session.createQuery(queryCount).uniqueResult()).intValue());
                    listaOrden = result.list();
                    tx.commit();
                    if (listaOrden.isEmpty()) {
                        logger.info("No existen Ordenes de Compra para mostar");
                    } else {
                        for (OrdenesCompra ordenCompra : listaOrden) {
                            auxOrdenCompra = new AuxOrdenCompra();
                            auxOrdenCompra.setIdOrdenCompra(ordenCompra.getIdOrdenCompra());
                            auxOrdenCompra.setIdEmpresa(ordenCompra.getIdEmpresa());
                            auxOrdenCompra.setIdSocioComercial(ordenCompra.getIdSocioComercial());
                            auxOrdenCompra.setNumeroOc(ordenCompra.getNumeroOc());
                            auxOrdenCompra.setTotal(ordenCompra.getTotal());
                            auxOrdenCompra.setTipoMoneda(ordenCompra.getTipoMoneda());
                            auxOrdenCompra.setTipoCambio(ordenCompra.getTipoCambio());
                            auxOrdenCompra.setRfcVendedor(ordenCompra.getRfcVendedor());
                            auxOrdenCompra.setRfcSocioComercial(daoSociosComerciales.getRFCSociobyIdSocio(ordenCompra.getIdSocioComercial()));
                            listaAuxOrden.add(auxOrdenCompra);
                        }
                    }
                } catch (HibernateException he) {
                    descBitacora = "[ORDEN_COMPRA - init] HibernateException ERROR: " + he.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                    tx.rollback();
                } catch (Exception ex) {
                    descBitacora = "[ORDEN_COMPRA - init] ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                } finally {
                    if (session.isOpen()) {
                        session.clear();
                        session.close();
                    }
                }
                return listaAuxOrden;
            }
        };
    }

    public void dataLazyCfdi() {
        lazyDataCfdi = new LazyDataModel<CfdisRecibidos>() {
            @Override
            public List<CfdisRecibidos> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                listaCFDIs = new ArrayList<CfdisRecibidos>();
                try {
                    queryCount = "select count(idCfdiRecibido) ";
                    query = "from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa() + " and idOrdenCompra = " + auxOrdenCompra.getIdOrdenCompra();
                    if (!filters.isEmpty()) {
                        Iterator it = filters.keySet().iterator();
                        while (it.hasNext()) {
                            filterProperty = (String) it.next();
                            filterValue = filters.get(filterProperty);
                            if (filterProperty.equals("serie") || filterProperty.equals("estado")) {
                                query = query + " and " + filterProperty + " = '" + filterValue + "'";
                            }
                            if (filterProperty.equals("folio")) {   //|| filterProperty.equals("total")
                                query = query + " and " + filterProperty + " = " + filterValue;
                            }
                            if (filterProperty.equals("fecha") || filterProperty.equals("fechaRecepcion") || filterProperty.equals("total")) {
                                query = query + " and " + filterProperty + " like '" + filterValue + "%'";
                            }
                        }
                    }
                    if (sortField != null && !sortField.isEmpty()) {
                        if (sortOrder.equals(SortOrder.ASCENDING)) {
                            query = query + " ORDER BY " + sortField + " ASC";
                        } else {
                            query = query + " ORDER BY " + sortField + " DESC";
                        }
                    } else {
                        query = query + " ORDER BY fechaRecepcion DESC";
                    }
                    queryCount = queryCount + query;
                    Query result = session.createQuery("select new CfdisRecibidos(idCfdiRecibido, idSocioComercial, serie, folio, fecha, total, fechaRecepcion, estado, tiposWarn)" + query);
                    this.setRowCount(((Long) session.createQuery(queryCount).uniqueResult()).intValue());
                    result.setFirstResult(first);
                    result.setMaxResults(pageSize + first);
                    listaCFDIs = result.list();
                } catch (HibernateException e) {
                    logger.error(usuario.getUserid() + " dataLazyCfdi ERROR " + e);
                } finally {
                    if (session.isOpen()) {
                        session.clear();
                        session.close();
                    }
                }
                return listaCFDIs;
            }
        };
    }

    public void downloadXML() {
        logger.info(usuario.getUserid() + " inicia descarga de XML");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        logger.info("id " + id);
        try {
            cfdiRecibido = daoCfdiRecibido.getCfdiByID(id);
            if (cfdiRecibido.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible descargar el XML del CFDI su estado es: " + cfdiRecibido.getEstado()));
                logger.warn(usuario.getUserid() + ". downloadXML - No es posible descargar el XML del CFDI uuid " + cfdiRecibido.getUuid() + " su estado es: " + cfdiRecibido.getEstado());
                return;
            }
            String strNombre = cfdiRecibido.getNombreArchivo();
            if (strNombre != null && !strNombre.trim().isEmpty()) {
                strNombre = strNombre.replace(" ", "") + ".xml";
            } else {
                strNombre = cfdiRecibido.getIdCfdiRecibido() + ".xml";
            }

            logger.info(usuario.getUserid() + " Nombre del XML: " + strNombre);
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setContentType("application/force-download");
            String disposition = "attachment; fileName=" + strNombre;
            response.setHeader("Content-Disposition", disposition);
            ServletOutputStream out = response.getOutputStream();
            InputStream ascii;

            String texto = cfdiRecibido.getXmlSat();
            logger.debug("xml " + texto);

            ascii = new ByteArrayInputStream(texto.getBytes("UTF-8"));

            byte[] buf = new byte[5 * 1000 * 1024];
            int bytesRead;
            while ((bytesRead = ascii.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.flush();
            out.close();
            ctx.responseComplete();
            descBitacora = "[CFDIs] " + usuario.getUserid() + ". Descargo XML de CFDI uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(usuario.getUserid() + ". Descargo XML de CFDI uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio());
            logger.info(usuario.getUserid() + ". downloadXML- XML: " + strNombre + " Generado.");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al descargar el archivo XML."));
            descBitacora = "[CFDIs] downloadXML ERROR: " + e;
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error("Usuario " + usuario.getUserid() + " downloadXML - ERROR: " + e);
        }
    }

    public void downloadPDF() {
        logger.info(usuario.getUserid() + " downloadPDF - inicia descarga de PDF");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        List<Plantillas> plantillas;
        try {
            cfdiRecibido = daoCfdiRecibido.getCfdiByID(id);
            if (cfdiRecibido.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible descargar el PFD del CFDI su estado es: " + cfdiRecibido.getEstado()));
                logger.info(usuario.getUserid() + ". downloadPDF - No es posible descargar el PFD del CFDI uuid " + cfdiRecibido.getUuid() + " su estado es: " + cfdiRecibido.getEstado());
                return;
            }

            String strNombre = cfdiRecibido.getNombreArchivo();
            if (strNombre != null && !strNombre.trim().isEmpty()) {
                strNombre = strNombre.replace(" ", "") + ".pdf";
            } else {
                strNombre = cfdiRecibido.getIdCfdiRecibido() + ".pdf";
            }
            plantillas = daoPlantilla.listaPlantillasByIdEmpresa(empresa.getIdEmpresa());
            if (plantillas.size() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No existen Plantillas almcenadas."));
                logger.warn(usuario.getUserid() + " downloadPDF - No existen Plantillas almcenadas");
                return;
            }
            JasperUtils jasperUtils = new JasperUtils();
            JasperPrint jasperPrint = jasperUtils.fill(cfdiRecibido.getXmlSat(), plantillas.get(0).getRuta());
            byte[] jas = jasperUtils.getPdfAsBytesArray(jasperPrint);
            logger.debug("colocando nombre al achivo: " + strNombre);
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setContentType("application/force-download");
            String disposition = "attachment; fileName=" + strNombre;
            response.setHeader("Content-Disposition", disposition);
            ServletOutputStream out = response.getOutputStream();
            InputStream ascii;

            ascii = new ByteArrayInputStream(jas);

            byte[] buf = new byte[5 * 1000 * 1024];
            int bytesRead;
            while ((bytesRead = ascii.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.flush();
            out.close();
            ctx.responseComplete();
            descBitacora = "[CFDIs] " + sessionUsuario + " descargo PDF de CFDI uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(usuario.getUserid() + ". descargo PDF de CFDI uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio());
            logger.info(usuario.getUserid() + ". downloadPDF- PDF: " + strNombre + " Generado.");
        } catch (JRException ex) {
            descBitacora = "[CFDIs] " + usuario.getUserid() + ". downloadPDF- ERROR JRE: " + ex;
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al descargar el archivo PDF."));
            logger.error(usuario.getUserid() + ". downloadPDF- JRException ERROR " + ex);
        } catch (Exception e) {
            descBitacora = "[CFDIs] " + usuario.getUserid() + ". downloadPDF- ERROR: " + e;
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al descargar el archivo PDF."));
            logger.error(usuario.getUserid() + ". downloadPDF- ERROR: " + e);
        }
    }

    public void descargarPDFSocio() {
        logger.info(usuario.getUserid() + " descargarPDFSocio - inicia descarga de PDF");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        String strNombrePDF;
        String dir = "/work/iqtb/validacionfiles/pdfRecibidos/" + empresa.getRfc() + "/";
        try {
            cfdiRecibido = daoCfdiRecibido.getCfdiByID(id);
            strNombrePDF = cfdiRecibido.getNombreArchivo().replace(" ", "") + ".pdf";
            if (cfdiRecibido.getPdf() == null) {
                logger.info(usuario.getUserid() + ". cfdi con id " + cfdiRecibido.getIdCfdiRecibido() + " no tiene PDF socio comercial asignado. Inicia busqueda en " + dir);
                if (buscarArchivo(dir, cfdiRecibido)) {
                    validarPDFSocio(cfdiRecibido);
                    servletDescarga(cfdiRecibido.getPdf(), strNombrePDF);
                } else {
                    descBitacora = usuario.getUserid() + ". No se encontro PDF con nombre " + cfdiRecibido.getNombreArchivo() + " en la ruta " + dir;
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PDF no encontrado", "Si desea puede cargar el archivo PDF con nombre: " + cfdiRecibido.getNombreArchivo() + " desde el botón Nuevo."));
                    logger.info(usuario.getUserid() + ". No se encontro PDF con nombre " + cfdiRecibido.getNombreArchivo() + " en la ruta " + dir);
                }
            } else {
                if (cfdiRecibido.getStatusPdf() == null) {
                    logger.info(usuario.getUserid() + ". CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " tiene PDF status " + cfdiRecibido.getStatusPdf() + " Iniciara validacion.");
                    validarPDFSocio(cfdiRecibido);
                    servletDescarga(cfdiRecibido.getPdf(), strNombrePDF);
                } else {
                    if (!cfdiRecibido.getStatusPdf()) {
                        logger.warn(usuario.getUserid() + ". CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " tiene PDF status " + cfdiRecibido.getStatusPdf() + " Iniciara descarga servlet.");
                        servletDescarga(cfdiRecibido.getPdf(), strNombrePDF);
                        logger.warn(usuario.getUserid() + ". CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " El PDF entregado podria no coincidir con el XML");
                        descBitacora = usuario.getUserid() + ". CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " El PDF entregado podria no coincidir con el XML";
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "El PDF entregado podria no coinicir con el XML."));
                    } else {
                        logger.info(usuario.getUserid() + ". CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " tiene PDF status " + cfdiRecibido.getStatusPdf() + " Iniciara descarga servlet.");
                        servletDescarga(cfdiRecibido.getPdf(), strNombrePDF);
                    }
                }
            }
        } catch (Exception e) {
            descBitacora = usuario.getUserid() + ". descargarPDFSocio- ERROR: " + e;
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al descargar PDF"));
            logger.error(usuario.getUserid() + ". descargarPDFSocio- ERROR: " + e);
        }
    }

    private boolean buscarArchivo(String strDir, CfdisRecibidos cfdi) {
        logger.info(usuario.getUserid() + " Inicia buscarArchivo con nombre " + cfdi.getNombreArchivo());
        boolean respuesta = false;
        String strNombreArchivo = null;
        File dir = new File(strDir);
        File fPDF;
        if (dir.exists()) {
            strNombreArchivo = cfdi.getNombreArchivo() + ".pdf";
            fPDF = new File(strDir + strNombreArchivo);
            if (fPDF.exists()) {
                logger.info("Archivo coincide " + strDir + strNombreArchivo);
                respuesta = true;
            }
        } else {
            descBitacora = usuario.getUserid() + ". Directorio no existe " + strDir;
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
            logger.warn(usuario.getUserid() + ". Directorio no existe " + strDir);
        }
        if (respuesta) {
            FileInputStream fis = null;
            try {
                File f = new File(strDir + strNombreArchivo);
                logger.info(usuario.getUserid() + ". Archivo cargado " + f.getAbsolutePath());
                byte[] b = getBytesFromFile(f);

                cfdi.setPdf(b);
                if (daoCfdiRecibido.actualizarCfdi(cfdi)) {
                    new ManejoArchivos().eliminar(strDir + strNombreArchivo);
                    descBitacora = usuario.getUserid() + ". modifico el cfdi con id " + cfdi.getIdCfdiRecibido() + " inserto pdf " + strNombreArchivo;
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(usuario.getUserid() + ". modifico el cfdi con id " + cfdi.getIdCfdiRecibido() + " inserto pdf " + strNombreArchivo);
                } else {
                    descBitacora = usuario.getUserid() + ". Error al modificar el cfdi con id " + cfdi.getIdCfdiRecibido() + " pdf " + strNombreArchivo;
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". Error al modificar el cfdi con id " + cfdi.getIdCfdiRecibido() + " pdf " + strNombreArchivo);
                }
            } catch (FileNotFoundException ex) {
                descBitacora = usuario.getUserid() + ". buscarArchivo - FileNotFoundException ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". buscarArchivo - FileNotFoundException ERROR " + ex);
            } catch (IOException ex) {
                descBitacora = usuario.getUserid() + ". buscarArchivo - IOException ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.info(usuario.getUserid() + ". buscarArchivo - IOException ERROR " + ex);
            } catch (Exception ex) {
                descBitacora = usuario.getUserid() + ". buscarArchivo - ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.info(usuario.getUserid() + ". buscarArchivo - ERROR " + ex);
            }
        }
        return respuesta;
    }

    private boolean validarPDFSocio(CfdisRecibidos cfdi) {
        logger.info(usuario.getUserid() + ". Inicia validarPDFSocio del cfdi id " + cfdi.getIdCfdiRecibido());
        boolean pdfValido = false;
        String strPdf;
        String[] arrayPdf;
        LeerPDF leerPdf = new LeerPDF();
        strPdf = leerPdf.pdftoText(cfdi.getPdf());
        arrayPdf = strPdf.split("\n");
        for (String item : arrayPdf) {
            if (cfdi.getUuid().equals(item)) {
                logger.info(usuario.getUserid() + ". PDF corresponde a este UUID " + cfdi.getUuid() + " uuidPDF " + item);
                pdfValido = true;
            }
        }
        if (pdfValido) {
            logger.info(usuario.getUserid() + ". CFDI con id " + cfdi.getIdCfdiRecibido() + " PDF corresponde al uuid");
        } else {
            logger.info(usuario.getUserid() + ". CFDI con id " + cfdi.getIdCfdiRecibido() + " PDF no corresponde al uuid");
        }
        try {
            cfdi.setStatusPdf(pdfValido);
            if (daoCfdiRecibido.actualizarCfdi(cfdi)) {
                descBitacora = usuario.getUserid() + ". modifico correctamente CFDI con id " + cfdi.getIdCfdiRecibido() + " statusPDF " + pdfValido;
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". modifico correctamente CFDI con id " + cfdi.getIdCfdiRecibido() + " statusPDF " + pdfValido);
            } else {
                descBitacora = usuario.getUserid() + ". Error al modificar CFDI con id " + cfdi.getIdCfdiRecibido() + " statusPDF " + pdfValido;
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al modificar CFDI con id " + cfdi.getIdCfdiRecibido() + " statusPDF " + pdfValido);
            }
        } catch (Exception e) {
            descBitacora = usuario.getUserid() + ". validarPDFSocio ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". validarPDFSocio ERROR " + e);
        }
        return pdfValido;
    }

    private void servletDescarga(byte[] bytesFile, String strNombreArchivo) {
        try {
            logger.info(usuario.getUserid() + ". Inicia servletDescarga");
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setContentType("application/force-download");
            String disposition = "attachment; fileName=" + strNombreArchivo;
            response.setHeader("Content-Disposition", disposition);
            ServletOutputStream out = response.getOutputStream();
            InputStream ascii;

            ascii = new ByteArrayInputStream(bytesFile);

            byte[] buf = new byte[5 * 1000 * 1024];
            int bytesRead;
            while ((bytesRead = ascii.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.flush();
            out.close();
            ctx.responseComplete();
            descBitacora = usuario.getUserid() + ". genero/descargo " + strNombreArchivo;
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(usuario.getUserid() + ". genero/descargo " + strNombreArchivo);
            logger.info(usuario.getUserid() + ". Termina servletDescarga");
        } catch (IOException e) {
            descBitacora = usuario.getUserid() + ". servletDescarga ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". servletDescarga ERROR " + e);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String ruta = "/work/ftp/" + empresa.getRfc() + "/VALIDACION";

        if (!nombreArchivo.endsWith(".txt")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "El archivo debe ser con extensión \".txt\""));
            return;
        }
        try {
            congifEmpresa = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.VAL_USER_FTP.name());
            if (!congifEmpresa.getValor().trim().isEmpty()) {
                File folder = new File(ruta);
                if (!folder.exists()) {
                    logger.info("no se encontro el directorio " + ruta);
                    folder.mkdirs();
                    logger.info(ruta + " creado");
                }
                outputStream = new FileOutputStream(new File(ruta + System.getProperty("file.separator") + event.getFile().getFileName()));
                inputStream = event.getFile().getInputstream();
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Archivo " + event.getFile().getFileName() + " cargado"));
                descBitacora = "[ORDEN_COMPRA - handleFileUpload] " + usuario.getUserid() + " Archivo " + event.getFile().getFileName() + " cargado para orden de compra";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(descBitacora);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Configure un Usuario FTP para archvios de Validación."));
                descBitacora = "[ORDEN_COMPRA - handleFileUpload] " + usuario.getUserid() + " No existe Configuracion Usuario FTP para archvios de Validación.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                logger.warn(descBitacora);
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cargar", "Ocurrio un error cargando los archivos."));
            descBitacora = "[ORDEN_COMPRA - handleFileUpload] " + usuario.getUserid() + " IOException ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cargar", "Ocurrio un error cargando los archivos."));
            descBitacora = "[ORDEN_COMPRA - handleFileUpload] " + usuario.getUserid() + " Exception ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = "[ORDEN_COMPRA - handleFileUpload] " + usuario.getUserid() + " Error cerrando inputStream ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    descBitacora = "[ORDEN_COMPRA - handleFileUpload] " + usuario.getUserid() + " Error cerrando outputStream ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
        }
    }

    public void obtenerRfcSociosComerciales() {
        ordenCompra = new OrdenesCompra();
        logger.info("Inicia obtenerRfcSociosComerciales");
        try {
            itemsSocioComercial = new ArrayList<SelectItem>();
            selectItem = null;
            listaSociosComerciales = daoSociosComerciales.listaSociosHQL("select new SociosComerciales(idSocioComercial, rfc) from SociosComerciales where idEmpresa = " + empresa.getIdEmpresa());
            if (listaSociosComerciales.isEmpty()) {
                logger.info("[ORDEN_COMPRA - obtenerRfcSociosComerciales] - No se han encontrado resultados para Socios Comerciales");
            } else {
                for (SociosComerciales var : listaSociosComerciales) {
                    selectItem = new SelectItem(var.getIdSocioComercial(), var.getRfc());
                    itemsSocioComercial.add(selectItem);
                }
            }
        } catch (Exception ex) {
            descBitacora = "[ORDEN_COMPRA - obtenerRfcSociosComerciales] " + usuario.getUserid() + " ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void saveOrdenCompra() {
        boolean insertOc = false;
        logger.info(usuario.getUserid() + " Inicia saveOrdenCompra");
        try {
            if (ordenCompra.getIdSocioComercial() != 0) {
                if (ordenCompra.getNumeroOc() != null && !ordenCompra.getNumeroOc().trim().isEmpty()) {
                    if (ordenCompra.getTotal() != null && ordenCompra.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                        if (ordenCompra.getRfcVendedor() != null && !ordenCompra.getRfcVendedor().trim().isEmpty()) {
                            if (ordenCompra.getTipoMoneda() != null && !ordenCompra.getTipoMoneda().trim().isEmpty()) {
                                ordenCompra.setTipoMoneda(ordenCompra.getTipoMoneda().toUpperCase());
                            }
                            ordenCompra.setIdEmpresa(empresa.getIdEmpresa());
                            if (daoOrdenCompra.insertOrdenCompra(ordenCompra)) {
                                insertOc = true;
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Orden de Compra", "Orden de Compra " + ordenCompra.getNumeroOc() + " se agrego correctamente."));
                                descBitacora = "[ORDEN_COMPRA - saveOrdenCompra] " + usuario.getUserid() + " inserto la orden de compra " + ordenCompra.getNumeroOc() + " idEmpresa " + ordenCompra.getIdEmpresa() + " idSocioComercial " + ordenCompra.getIdSocioComercial();
                                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                                logger.info(descBitacora);
                            } else {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al guardar la Orden de Compra " + ordenCompra.getNumeroOc() + "."));
                                descBitacora = "[ORDEN_COMPRA - saveOrdenCompra] " + usuario.getUserid() + " error al insertar la orden de compra " + ordenCompra.getNumeroOc() + " idEmpresa " + ordenCompra.getIdEmpresa() + " idSocioComercial " + ordenCompra.getIdSocioComercial();
                                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                                logger.error(descBitacora);
                            }
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para RFC Vendedor."));
                            logger.info(usuario.getUserid() + " rfc vendedor es un campo obligatorio");
                        }
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Total."));
                        logger.info(usuario.getUserid() + " total es un campo obligatorio");
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Número."));
                    logger.info(usuario.getUserid() + " numero es un campo obligatorio");
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Socio Comercial."));
                logger.info(usuario.getUserid() + " idsocioComercial es un campo obligatorio");
            }

        } catch (Exception e) {
            descBitacora = "[ORDEN_COMPRA - saveOrdenCompra] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("insertOc", insertOc);
    }

    public LazyDataModel<AuxOrdenCompra> getLazyDataOrden() {
        return lazyDataOrden;
    }

    public void setLazyDataOrden(LazyDataModel<AuxOrdenCompra> lazyDataOrden) {
        this.lazyDataOrden = lazyDataOrden;
    }

    public List<AuxOrdenCompra> getAuxOrdenSeleccionados() {
        return auxOrdenSeleccionados;
    }

    public void setAuxOrdenSeleccionados(List<AuxOrdenCompra> auxOrdenSeleccionados) {
        this.auxOrdenSeleccionados = auxOrdenSeleccionados;
    }

//    public SelectItem[] getItemsTipoMoneda() {
//        return itemsTipoMoneda;
//    }
//
//    public void setItemsTipoMoneda(SelectItem[] itemsTipoMoneda) {
//        this.itemsTipoMoneda = itemsTipoMoneda;
//    }
    public AuxOrdenCompra getAuxOrdenCompra() {
        return auxOrdenCompra;
    }

    public void setAuxOrdenCompra(AuxOrdenCompra auxOrdenCompra) {
        this.auxOrdenCompra = auxOrdenCompra;
    }

    public LazyDataModel<CfdisRecibidos> getLazyDataCfdi() {
        return lazyDataCfdi;
    }

    public void setLazyDataCfdi(LazyDataModel<CfdisRecibidos> lazyDataCfdi) {
        this.lazyDataCfdi = lazyDataCfdi;
    }

    public OrdenesCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenesCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public List<SelectItem> getItemsSocioComercial() {
        return itemsSocioComercial;
    }

    public void setItemsSocioComercial(List<SelectItem> itemsSocioComercial) {
        this.itemsSocioComercial = itemsSocioComercial;
    }

}
