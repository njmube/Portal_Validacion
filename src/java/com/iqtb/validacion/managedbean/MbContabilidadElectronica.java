/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionEmpresa;
import com.iqtb.validacion.dao.DaoContabilidadElectronica;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.ConfigEmpresa;
import com.iqtb.validacion.emun.ContabilidadEstado;
import com.iqtb.validacion.emun.ContabilidadTipo;
import com.iqtb.validacion.emun.Servicio;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.ContabilidadElectronica;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.mchange.v2.c3p0.impl.C3P0Defaults;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
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
public class MbContabilidadElectronica implements Serializable {

    private ContabilidadElectronica contabilidadElectronica;
    private List<ContabilidadElectronica> listaContabilidad;
    private List<ContabilidadElectronica> contabilidadSeleccionados;
    private Usuarios usuario;
    private Empresas empresa;
    private Servicios servGenerador;
    private String tipoArchivo;
    private String codigoEditar;
//    private TabView tabView;
    private final String sessionUsuario;
    private FacesMessage msg;
    private String descBitacora;
    private LazyDataModel<ContabilidadElectronica> modelDataList;
    private SelectItem[] tipoSelectItems = null;
    private SelectItem[] mesSelectItems = null;
    private SelectItem[] estadoSelectItems = null;
    private Session session;
    private Transaction tx;
    private String queryCount;
    private String query;
    private String filterProperty;
    private String filterValue;
    private Iterator it;
    private DaoUsuario daoUsuario;
    private DaoEmpresa daoEmpresa;
    private DaoServicio daoServicio;
    private DaoContabilidadElectronica daoContabilidadElectronica;
    private static final Logger logger = Logger.getLogger(MbContabilidadElectronica.class);

    public MbContabilidadElectronica() {
        contabilidadElectronica = new ContabilidadElectronica();
        contabilidadSeleccionados = new ArrayList<ContabilidadElectronica>();
        usuario = new Usuarios();
        empresa = new Empresas();
        servGenerador = new Servicios();
        daoUsuario = new DaoUsuario();
        daoEmpresa = new DaoEmpresa();
        daoServicio = new DaoServicio();
        daoContabilidadElectronica = new DaoContabilidadElectronica();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        try {
            usuario = daoUsuario.getByUserid(sessionUsuario);
            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
            servGenerador = daoServicio.getServicoByNombre(Servicio.GENERADOR.name());
        } catch (Exception e) {
            descBitacora = "[CONT_ELECTRONICA - MbContabilidadElectronica] Error al obtener USUARIO EMPRESA SERVICIO. ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        tipoSelectItems = new SelectItem[6];
        tipoSelectItems[0] = new SelectItem("", "TODOS");
        tipoSelectItems[1] = new SelectItem(ContabilidadTipo.BALANZA.name(), ContabilidadTipo.BALANZA.name());
        tipoSelectItems[2] = new SelectItem(ContabilidadTipo.CATALOGO.name(), ContabilidadTipo.CATALOGO.name());
        tipoSelectItems[3] = new SelectItem(ContabilidadTipo.POLIZA.name(), ContabilidadTipo.POLIZA.name());
        tipoSelectItems[4] = new SelectItem(ContabilidadTipo.AUXCUENTAS.name(), ContabilidadTipo.AUXCUENTAS.name());
        tipoSelectItems[5] = new SelectItem(ContabilidadTipo.AUXFOLIOS.name(), ContabilidadTipo.AUXFOLIOS.name());

        estadoSelectItems = new SelectItem[5];
        estadoSelectItems[0] = new SelectItem("", "TODOS");
        estadoSelectItems[1] = new SelectItem(ContabilidadEstado.NUEVO.name(), ContabilidadEstado.NUEVO.name());
        estadoSelectItems[2] = new SelectItem(ContabilidadEstado.GENERANDO.name(), ContabilidadEstado.GENERANDO.name());
        estadoSelectItems[3] = new SelectItem(ContabilidadEstado.GENERADO.name(), ContabilidadEstado.GENERADO.name());
        estadoSelectItems[4] = new SelectItem(ContabilidadEstado.ERROR.name(), ContabilidadEstado.ERROR.name());

        mesSelectItems = new SelectItem[13];
        mesSelectItems[0] = new SelectItem("", "TODOS");
        mesSelectItems[1] = new SelectItem("01", "ENERO");
        mesSelectItems[2] = new SelectItem("02", "FEBRERO");
        mesSelectItems[3] = new SelectItem("03", "MARZO");
        mesSelectItems[4] = new SelectItem("04", "ABRIL");
        mesSelectItems[5] = new SelectItem("05", "MAYO");
        mesSelectItems[6] = new SelectItem("06", "JUNIO");
        mesSelectItems[7] = new SelectItem("07", "JULIO");
        mesSelectItems[8] = new SelectItem("08", "AGOSTO");
        mesSelectItems[9] = new SelectItem("09", "SEPTIEMBRE");
        mesSelectItems[10] = new SelectItem("10", "OCTUBRE");
        mesSelectItems[11] = new SelectItem("11", "NOVIEMBRE");
        mesSelectItems[12] = new SelectItem("12", "DICIEMBRE");
    }

    @PostConstruct
    public void init() {
        modelDataList = new LazyDataModel<ContabilidadElectronica>() {

            @Override
            public List<ContabilidadElectronica> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {

                listaContabilidad = new ArrayList<ContabilidadElectronica>();
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                try {
                    queryCount = "select count(idContabilidad) ";
                    query = "from ContabilidadElectronica where idEmpresa = " + empresa.getIdEmpresa();
                    if (!filters.isEmpty()) {
                        it = filters.keySet().iterator();
                        while (it.hasNext()) {
                            filterProperty = (String) it.next();
                            filterValue = filters.get(filterProperty);
                            if (filterProperty.equals("anio")) {
                                query = query + " and " + filterProperty + " = " + filterValue;
                            }
                            if (filterProperty.equals("tipoArchivo") || filterProperty.equals("estado") || filterProperty.equals("mes")) {
                                query = query + " and " + filterProperty + " = '" + filterValue + "'";
                            }
                            if (filterProperty.equals("fechaRecepcion") || filterProperty.equals("nombreRecibido") || filterProperty.equals("nombreGenerado")) {
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
                    Query result = session.createQuery("select new ContabilidadElectronica(idContabilidad, estado, tipoArchivo, nombreRecibido, nombreGenerado, fechaRecepcion, mes, anio) " + query);
                    result.setFirstResult(first);
                    result.setMaxResults(pageSize + first);
                    this.setRowCount(((Long) session.createQuery(queryCount).uniqueResult()).intValue());
                    listaContabilidad = result.list();
                    tx.commit();

                } catch (HibernateException he) {
                    descBitacora = "[CONT_ELECTRONICA - init] " + usuario.getUserid() + " HibernateException ERROR " + he.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                    tx.rollback();
                } finally {
                    if (session.isOpen()) {
                        session.clear();
                        session.close();
                    }
                }
                return listaContabilidad;
            }
        };
    }

    public void verContElectronica() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idContabilidad"));
        boolean estadoCFDI = false;
        try {
            String hql = "select new ContabilidadElectronica(idContabilidad, estado, tipoArchivo, nombreRecibido, nombreGenerado, fechaRecepcion, error, mes, anio) from ContabilidadElectronica where idContabilidad = " + id;
            contabilidadElectronica = daoContabilidadElectronica.getContElectronicaByHql(hql);
            estadoCFDI = true;
        } catch (Exception e) {
            descBitacora = "[CONT_ELECTRONICA - verContElectronica] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoCFDI", estadoCFDI);

    }

    public void downloadXMLGenerado() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idContabilidad"));
        String strNombreXML;
        try {
            logger.info(usuario.getUserid() + ". downloadXMLGenerado- inicia descarga de XML");
            contabilidadElectronica = daoContabilidadElectronica.getContabilidadElectronica(id);

            strNombreXML = contabilidadElectronica.getNombreGenerado();

            logger.info(usuario.getUserid() + ". downloadXMLGenerado- Nombre del XML: " + strNombreXML);
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setContentType("application/force-download");
            String disposition = "attachment; fileName=" + strNombreXML;
            response.setHeader("Content-Disposition", disposition);
            ServletOutputStream out = response.getOutputStream();
            InputStream ascii;
//            out.print(cr.getXmlSat());

            String texto = contabilidadElectronica.getXmlGenerado();
            logger.debug("xml " + texto);

            ascii = new ByteArrayInputStream(texto.getBytes("UTF-8"));

            byte[] buf = new byte[5 * 1000 * 1024]; // 5000K buffer
            //byte[] buf = new byte[]; // 5000K buffer
            int bytesRead;
            while ((bytesRead = ascii.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.flush();
            out.close();
            ctx.responseComplete();
            descBitacora = "[CONT_ELECTRONICA - downloadXMLGenerado] " + usuario.getUserid() + " descargó XML con id " + contabilidadElectronica.getIdContabilidad() + " Tipo " + contabilidadElectronica.getTipoArchivo();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(descBitacora);
        } catch (Exception e) {
            descBitacora = "[CONT_ELECTRONICA - downloadXMLGenerado] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void downloadArchivoRecibido() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idContabilidad"));
        String strNombreXML;
        try {
            logger.info(usuario.getUserid() + ". downloadArchivoRecibido- inicia descarga de Archivo fuente");
            contabilidadElectronica = daoContabilidadElectronica.getContabilidadElectronica(id);

            strNombreXML = contabilidadElectronica.getNombreRecibido();

            logger.info(usuario.getUserid() + ". downloadArchivoRecibido- Nombre de Archivo fuente: " + strNombreXML);
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setContentType("application/force-download");
            String disposition = "attachment; fileName=" + strNombreXML;
            response.setHeader("Content-Disposition", disposition);
            ServletOutputStream out = response.getOutputStream();
            InputStream ascii;
//            out.print(cr.getXmlSat());

            String texto = contabilidadElectronica.getArchivoRecibido();
            logger.debug("Archivo fuente " + texto);

            ascii = new ByteArrayInputStream(texto.getBytes("UTF-8"));

            byte[] buf = new byte[5 * 1000 * 1024]; // 5000K buffer
            //byte[] buf = new byte[]; // 5000K buffer
            int bytesRead;
            while ((bytesRead = ascii.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.flush();
            out.close();
            ctx.responseComplete();
            descBitacora = "[CONT_ELECTRONICA - downloadArchivoRecibido] " + usuario.getUserid() + " descargo Archivo recibido con id " + contabilidadElectronica.getIdContabilidad() + " Tipo " + contabilidadElectronica.getTipoArchivo();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(descBitacora);
        } catch (Exception e) {
            descBitacora = "[CONT_ELECTRONICA - downloadArchivoRecibido] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void tipoArchivo() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();
        tipoArchivo = (String) params.get("tipoArchivo");
        logger.info("tipoArchivo " + tipoArchivo);
    }

    public void fileUpload(FileUploadEvent event) {
        logger.debug(usuario.getUserid() + ". Inicia fileUpload");

        InputStream inputStream = null;
        OutputStream outputStream = null;
        logger.info("tipoArchivo " + tipoArchivo);
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        logger.debug("nombreArchivo " + nombreArchivo);

        if (!nombreArchivo.endsWith(".txt")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "El archivo debe ser con extensión \".txt\""));
            return;
        }
        if (tipoArchivo.equals("CATALOGO")) {
            logger.info(usuario.getUserid() + ". Tipo de Archivo Catalogo");
        }
        if (tipoArchivo.equals("BALANZA")) {
            logger.info(usuario.getUserid() + ". Tipo de Archivo Balanza");
        }
        if (tipoArchivo.equals("POLIZA")) {
            logger.info(usuario.getUserid() + ". Tipo de Archivo Poliza");
        }
        if (tipoArchivo.equals("AUXFOLIOS")) {
            logger.info(usuario.getUserid() + ". Tipo de Archivo auxiliar folios");
        }
        if (tipoArchivo.equals("AUXCUENTAS")) {
            logger.info(usuario.getUserid() + ". Tipo de Archivo auxiliar folios");
        }
        logger.info("tipoArchivo " + tipoArchivo);
        try {
            ConfiguracionesEmpresas congifEmpresa = new DaoConfiguracionEmpresa().getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.VAL_USER_FTP.name());
            if (!congifEmpresa.getValor().trim().isEmpty()) {
                File folder = new File("/work/ftp/" + empresa.getRfc() + "/" + tipoArchivo);
                if (!folder.exists()) {
                    logger.info("no se encontro el directorio " + "/work/ftp/" + empresa.getRfc() + "/" + tipoArchivo);
                    folder.mkdirs();
                    logger.info("/work/ftp/" + empresa.getRfc() + "/" + tipoArchivo + " creado");
                }
                outputStream = new FileOutputStream(new File("/work/ftp/" + empresa.getRfc() + "/" + tipoArchivo + "/" + event.getFile().getFileName()));
                inputStream = event.getFile().getInputstream();
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Archivo " + event.getFile().getFileName() + " cargado."));
                descBitacora = "[CONT_ELECTRONICA - fileUpload] " + usuario.getUserid() + " Archivo " + event.getFile().getFileName() + " cargado para " + tipoArchivo;
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(descBitacora);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Configure un Usuario FTP para archvios de tipo " + tipoArchivo));
                descBitacora = "[CONT_ELECTRONICA - fileUpload] " + usuario.getUserid() + " No existe usuario FTP";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                logger.warn(descBitacora);
            }
        } catch (IOException e) {
            descBitacora = "[CONT_ELECTRONICA - fileUpload] " + usuario.getUserid() + " IOException ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception ex) {
            descBitacora = "[CONT_ELECTRONICA - fileUpload] " + usuario.getUserid() + " ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = "[CONT_ELECTRONICA - fileUpload] " + usuario.getUserid() + " Error cerrando inputStream ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    descBitacora = "[CONT_ELECTRONICA - fileUpload] " + usuario.getUserid() + " Error cerrando outputStream ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
        }
    }

    public void deleteContabilidad() {

        for (ContabilidadElectronica item : contabilidadSeleccionados) {
            try {
                contabilidadElectronica = null;
                contabilidadElectronica = daoContabilidadElectronica.getContabilidadElectronica(item.getIdContabilidad());
                if (contabilidadElectronica != null) {
                    if (!(contabilidadElectronica.getEstado().equals("NUEVO") || contabilidadElectronica.getEstado().equals("GENERANDO"))) {
                        if (daoContabilidadElectronica.deleteContabilidadElectronica(contabilidadElectronica)) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Archivo de Contabilidad Electrónico eliminado");
                            descBitacora = "[CONT_ELECTRONICA - deleteContabilidad] " + usuario.getUserid() + " elimino el archivo de Contabilidad Electrónica con id " + item.getIdContabilidad() + " tipo de archivo " + item.getTipoArchivo();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                            logger.info(descBitacora);
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al eliminar el Archivo de Contabilidad Electrónica");
                            descBitacora = "[CONT_ELECTRONICA - deleteContabilidad] " + usuario.getUserid() + " Error al intentar eliminar el archivo de contabilidad Electrónica con id" + item.getIdContabilidad() + " tipo de archivo " + item.getIdContabilidad();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                            logger.error(descBitacora);
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible eliminar el documento, su estado es " + contabilidadElectronica.getEstado());
                        logger.error("No es posible eliminar el documento con nombre " + contabilidadElectronica.getNombreRecibido() + " su estado es " + contabilidadElectronica.getEstado());
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al eliminar el Archivo de Contabilidad Electrónica");
                    descBitacora = "[CONT_ELECTRONICA - deleteContabilidad] " + usuario.getUserid() + " Error al obtener el archivo de contabilidad electronica con id " + item.getIdContabilidad();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } catch (Exception e) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al eliminar el Archivo de Contabilidad Electrónica");
                descBitacora = "[CONT_ELECTRONICA - deleteContabilidad] " + usuario.getUserid() + " ERROR " + e.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void procesarContabilidad() {
        logger.info(usuario.getUserid() + ". Inicia reprocesar archivos de contabilidad Electrónica");
        int numArchivos = 0;
        if (contabilidadSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un archivo de Contabilidad Electrónica.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        for (ContabilidadElectronica item : contabilidadSeleccionados) {
            try {
                contabilidadElectronica = null;
                contabilidadElectronica = daoContabilidadElectronica.getContabilidadElectronica(item.getIdContabilidad());
                if (contabilidadElectronica != null) {
                    if (!(contabilidadElectronica.getEstado().equals("NUEVO") || contabilidadElectronica.getEstado().equals("GENERANDO"))) {
                        contabilidadElectronica.setEstado("NUEVO");
                        contabilidadElectronica.setNombreGenerado("");
                        contabilidadElectronica.setXmlGenerado("");
                        contabilidadElectronica.setError("");
                        contabilidadElectronica.setMes(null);
                        contabilidadElectronica.setAnio(null);
                        if (daoContabilidadElectronica.updateContabilidadElectonica(contabilidadElectronica)) {
                            numArchivos++;
                            descBitacora = "[CONT_ELECTRONICA - procesarContabilidad] " + usuario.getUserid() + " Archivo con id " + contabilidadElectronica.getIdContabilidad() + " se ha colocado estado " + contabilidadElectronica.getEstado() + " para ser procesado.";
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                            logger.info(descBitacora);
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ha ocurrido un error al Reprocesar el archivo " + contabilidadElectronica.getNombreRecibido());
                            descBitacora = "[CONT_ELECTRONICA - procesarContabilidad] " + usuario.getUserid() + " Archivo con id " + contabilidadElectronica.getIdContabilidad() + " no se pudo colocar en estado nuevo para ser procesado.";
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                            logger.error(descBitacora);
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible procesar el documento, su estado es " + contabilidadElectronica.getEstado());
                        FacesContext.getCurrentInstance().addMessage(null, this.msg);
                        logger.error("No es posible procesar el documento con nombre " + contabilidadElectronica.getNombreRecibido() + " su estado es " + contabilidadElectronica.getEstado());
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al Reprocesar el archivo " + item.getNombreRecibido());
                    descBitacora = "[CONT_ELECTRONICA - procesarContabilidad] " + usuario.getUserid() + " Error al obtener el archivo de contabilidad electronica con id " + item.getIdContabilidad();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } catch (Exception ex) {
                descBitacora = "[CONT_ELECTRONICA - procesarContabilidad] " + usuario.getUserid() + " ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al intentar Reprocesar. Por favor, pongase en contancto con el administrador.");
            }
        }
        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Contabilidad Elactrónica", numArchivos + " Archivos seran procesados nuevamente en unos minutos.");
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String crearVistaEditar() {
        System.out.println("inicia crearVistaEditar");
//        codigoEditar = "<h:form id=\"frmEditarPoliza\">\n" +
//"                    <h:panelGrid columns=\"2\" cellspacing=\"5\">\n" +
//"                        <h:outputLabel value=\"Nombre\" />\n" +
//"                        <p:inputText value=\"\"/>\n" +
//"                        <h:outputLabel value=\"Apellido\" />\n" +
//"                        <p:inputText value=\"\"/>\n" +
//"                        <p></p>\n" +
//"                        <h:outputLabel />\n" +
//"                        <p:commandButton value=\"Guardar\" icon=\"ui-icon-disk\" styleClass=\"boton\"/>\n" +
//"                    </h:panelGrid>\n" +
//"                </h:form>";

        return "/ContabilidadElectronica/editarPoliza?faces-redirect=true";
    }

//    public TabView getTabView() throws Exception {
//        FacesContext fc = FacesContext.getCurrentInstance();
//        tabView = (TabView) fc.getApplication().createComponent("org.primefaces.component.TabView");
//
//        // cargar la lista de objetos para tabview
//        List<Usuarios> liscaldimensional = new ArrayList();
//        liscaldimensional = new DaoUsuario().getAllUsuarios();
//
//        //Se crean dinamicamente las tabs y en su contenido, unas cajas de texto
//        for (Usuarios sub : liscaldimensional) {
//            Tab tab = new Tab();
//            tab.setTitle(sub.getNombre());
//            int total = 4;
////            for (int i = 0; i < total; i++) {
//            PanelGrid pg = new PanelGrid();
//            pg.setColumns(2);
//            OutputLabel o = new OutputLabel();
//            o.setValue("Nombre");
//            InputText inputtext = new InputText();
//            inputtext.setLabel("Label");
//            inputtext.setValue(sub.getNombre());
//            inputtext.setOnfocus("");
//            OutputLabel o1 = new OutputLabel();
//            o1.setValue("APellido Paterno");
//            InputText inputtext1 = new InputText();
//            inputtext1.setLabel("Label");
//            inputtext1.setValue(sub.getApaterno());
//            inputtext1.setOnfocus("");
//            tab.getChildren().add(pg);
//            pg.getChildren().add(o);
//            pg.getChildren().add(inputtext);
//            pg.getChildren().add(o1);
//            pg.getChildren().add(inputtext1);
////                tab.getChildren().add(o);
////                tab.getChildren().add(inputtext);
////                tab.getChildren().add(o1);
////                tab.getChildren().add(inputtext1);
////            }
//            tabView.getChildren().add(tab);
//        }
//        return tabView;
//    }
    public void existeSeleccionContabilidad() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");

        boolean estadoContabilidad = false;
        if (contabilidadSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un documento de Contabilidad Electrónica");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null && parametro.equals("eliminar")) {
                estadoContabilidad = true;
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("contabilidad", estadoContabilidad);
    }

    public ContabilidadElectronica getContabilidadElectronica() {
        return contabilidadElectronica;
    }

    public void setContabilidadElectronica(ContabilidadElectronica contabilidadElectronica) {
        this.contabilidadElectronica = contabilidadElectronica;
    }

    public List<ContabilidadElectronica> getContabilidadSeleccionados() {
        return contabilidadSeleccionados;
    }

    public void setContabilidadSeleccionados(List<ContabilidadElectronica> contabilidadSeleccionados) {
        this.contabilidadSeleccionados = contabilidadSeleccionados;
    }

    public SelectItem[] getTipoSelectItems() {
        return tipoSelectItems;
    }

    public void setTipoSelectItems(SelectItem[] tipoSelectItems) {
        this.tipoSelectItems = tipoSelectItems;
    }

    public SelectItem[] getMesSelectItems() {
        return mesSelectItems;
    }

    public void setMesSelectItems(SelectItem[] mesSelectItems) {
        this.mesSelectItems = mesSelectItems;
    }

    public SelectItem[] getEstadoSelectItems() {
        return estadoSelectItems;
    }

    public void setEstadoSelectItems(SelectItem[] estadoSelectItems) {
        this.estadoSelectItems = estadoSelectItems;
    }

    public LazyDataModel<ContabilidadElectronica> getModelDataList() {
        return modelDataList;
    }

    public void setModelDataList(LazyDataModel<ContabilidadElectronica> modelDataList) {
        this.modelDataList = modelDataList;
    }

}
