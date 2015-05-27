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
import com.iqtb.validacion.dao.DaoRoles;
import com.iqtb.validacion.dao.DaoSociosComerciales;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.ConfigEmpresa;
import com.iqtb.validacion.emun.EstadoCfdiR;
import com.iqtb.validacion.emun.NotificacionCfdiR;
import com.iqtb.validacion.emun.RolTipo;
import static com.iqtb.validacion.encrypt.Encrypt.decrypt;
import com.iqtb.validacion.jasper.JasperUtils;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.pojo.CfdisRecibidos;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.OrdenesCompra;
import com.iqtb.validacion.pojo.Plantillas;
import com.iqtb.validacion.pojo.Roles;
import com.iqtb.validacion.pojo.SociosComerciales;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.ArchivoBaseDatos.getBytesFromFile;
import com.iqtb.validacion.util.AuxCfdiRecibido;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.CompararXML;
import com.iqtb.validacion.util.GenerarArchivo;
import com.iqtb.validacion.util.LeerPDF;
import com.iqtb.validacion.util.ManejoArchivos;
import com.iqtb.validacion.util.RevisarXML;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbCfdisRecibidos implements Serializable {

    private static final long serialVersionUID = 1L;
    private CfdisRecibidos cfdiRecibido;
    private List<AuxCfdiRecibido> cfdisSeleccionados;
    private List<CfdisRecibidos> listaCFDIs;
    private Empresas empresa;
    private SociosComerciales socioComercial;
    private List<SociosComerciales> listaSocios;
    private Usuarios usuario;
    private String descBitacora;
    private List<String> estadosCFDI;
    private SelectItem[] estadoSelectItems;
    private SelectItem[] cfdiSelectItems;
    private final String sessionUsuario;
    private String empresaSeleccionada;
    private boolean reportarTodos;
    private Integer intIdSocio;
    private List<SelectItem> selectOneItemsSocio;
    private Date fechaMin;
    private Date fechaMax;
    private FacesMessage msg;
    private LazyDataModel<AuxCfdiRecibido> lazyCfdisEliminados;
    private LazyDataModel<AuxCfdiRecibido> cfdiDataList;
    private Roles rolUsuario;
    private List<AuxCfdiRecibido> lista;
    private List<Integer> rfcByIdSocio;
    private List<AuxCfdiRecibido> listaE;
    private List<CfdisRecibidos> listaCFDIsE;
    private Session session;
    private Transaction tx;
    private String queryCount;
    private String query;
    private String filterProperty;
    private String filterValue;
    private Iterator it;
    private DaoSociosComerciales daoSociosComerciales;
    private DaoRoles daoRol;
    private DaoCfdisRecibidos daoCfdiRecibido;
    private AuxCfdiRecibido auxCfdiR;
    private SelectItem selectItem;
    private String strNombre;
    private DaoConfiguracionEmpresa daoConfigEmpresa;
    private ConfiguracionesEmpresas configEmail;
    private ConfiguracionesEmpresas configUser;
    private ConfiguracionesEmpresas configPass;
    private ConfiguracionesEmpresas configHost;
    private ConfiguracionesEmpresas configPort;
    private ConfiguracionesEmpresas configSSL;
    private ConfiguracionesEmpresas emailRecepcion;
    private ConfiguracionesEmpresas listaNotificacion;
    private List<SelectItem> itemsOrdenCompra;
    private OrdenesCompra ordenCompra;
    private List<OrdenesCompra> listaOrdenCompra;
    private Integer idOrdenCompra;
    private List<Integer> listaIds;
    private DaoOrdenCompra daoOrdenCompra;
    private boolean tieneAddenda;
    private boolean crearOrden;
    private DaoUsuario daoUsuario;
    private DaoEmpresa daoEmpresa;
    private ManejoArchivos manejorArchivos;
    private ConfiguracionesEmpresas congifEmpresa;
    private RevisarXML revisarXml;
    private List<Integer> idsCfdisR;
    private GenerarArchivo generarArchivo;
    private List<Plantillas> plantillas;
    private DaoPlantilla daoPlantilla;
    private List<String> listError;
    private static final Logger logger = Logger.getLogger("MbCfdisRecibidos");

    public MbCfdisRecibidos() {
        cfdiRecibido = new CfdisRecibidos();
        cfdisSeleccionados = new ArrayList<AuxCfdiRecibido>();
        empresa = new Empresas();
        socioComercial = new SociosComerciales();
        usuario = new Usuarios();
        reportarTodos = false;
        daoSociosComerciales = new DaoSociosComerciales();
        daoRol = new DaoRoles();
        daoCfdiRecibido = new DaoCfdisRecibidos();
        daoConfigEmpresa = new DaoConfiguracionEmpresa();
        listaOrdenCompra = new ArrayList<OrdenesCompra>();
        daoOrdenCompra = new DaoOrdenCompra();
        ordenCompra = new OrdenesCompra();
        tieneAddenda = false;
        crearOrden = false;
        idOrdenCompra = null;
        listaIds = null;
        configEmail = null;
        configUser = null;
        configPass = null;
        configHost = null;
        configPort = null;
        configSSL = null;
        daoUsuario = new DaoUsuario();
        daoEmpresa = new DaoEmpresa();
        manejorArchivos = new ManejoArchivos();
        congifEmpresa = new ConfiguracionesEmpresas();
        idsCfdisR = null;
        generarArchivo = new GenerarArchivo();
        plantillas = null;
        daoPlantilla = new DaoPlantilla();
        listError = new ArrayList<String>();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = daoUsuario.getByUserid(this.sessionUsuario);
            if (httpServletRequest.getSession().getAttribute("empresaSeleccionada") != null) {
                empresa = daoEmpresa.getEmpresaByRFC(this.empresaSeleccionada);
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - MbCfdisRecibidos] Error al obtener USUARIO EMPRESA SERVICIO. ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }

        try {
            rolUsuario = daoRol.getRolById(usuario.getIdRol());
            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                estadosCFDI = daoCfdiRecibido.listaEstadosCFDIsByIdEmpresaIdSocio(empresa.getIdEmpresa(), usuario.getIdSocioComercial());
            } else {
                estadosCFDI = daoCfdiRecibido.listaEstadosCFDIsByIdEmpresa(empresa.getIdEmpresa());
            }
            estadosCFDI.remove(EstadoCfdiR.ELIMINADO.name());
            estadosCFDI.remove(EstadoCfdiR.CANCELADO.name());
            int numE = estadosCFDI.size() + 1;
            estadoSelectItems = new SelectItem[numE];
            estadoSelectItems[0] = new SelectItem("", "TODOS");
            for (int i = 1; i < numE; i++) {
                estadoSelectItems[i] = new SelectItem(estadosCFDI.get(i - 1), estadosCFDI.get(i - 1));
            }
        } catch (Exception ex) {
            descBitacora = "[CFDI_RECIBIDO - MbCfdisRecibidos] " + usuario.getUserid() + " Error al obtener la lista de Estados de CFDIs. ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        cfdiSelectItems = new SelectItem[3];
        cfdiSelectItems[0] = new SelectItem("", "TODOS");
        cfdiSelectItems[1] = new SelectItem(EstadoCfdiR.ELIMINADO.name(), EstadoCfdiR.ELIMINADO.name());
        cfdiSelectItems[2] = new SelectItem(EstadoCfdiR.CANCELADO.name(), EstadoCfdiR.CANCELADO.name());

    }

    @PostConstruct
    public void init() {
        cfdiDataList = new LazyDataModel<AuxCfdiRecibido>() {

            @Override
            public List<AuxCfdiRecibido> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                lista = new ArrayList<AuxCfdiRecibido>();
                rfcByIdSocio = null;
                try {
                    queryCount = "select count(idCfdiRecibido) ";
                    query = "from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa() + " and (estado != 'ELIMINADO' and estado != 'CANCELADO')";
                    if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                        query = query + " and idSocioComercial = " + usuario.getIdSocioComercial();
                    }

                    if (!filters.isEmpty()) {
                        it = filters.keySet().iterator();
                        while (it.hasNext()) {
                            filterProperty = (String) it.next();
                            filterValue = filters.get(filterProperty);
                            if (filterProperty.equals("folio")) {
                                query = query + " and " + filterProperty + " = " + filterValue;
                            }
                            if (filterProperty.equals("serie") || filterProperty.equals("estado")) {
                                query = query + " and " + filterProperty + " = '" + filterValue + "'";
                            }
                            if (filterProperty.equals("fecha") || filterProperty.equals("fechaRecepcion")) {
                                query = query + " and " + filterProperty + " like '" + filterValue + "%'";
                            }
                            if (filterProperty.equals("rfcSocioComercial")) {
                                rfcByIdSocio = daoSociosComerciales.filtroSocioComercialByRFC(empresa.getIdEmpresa(), filterValue);
                                if (rfcByIdSocio != null) {
                                    boolean isFirst = true;
                                    for (Integer idSocio : rfcByIdSocio) {
                                        if (isFirst) {
                                            query = query + " and (idSocioComercial = " + idSocio;
                                            isFirst = false;
                                        } else {
                                            query = query + " or idSocioComercial = " + idSocio;
                                        }
                                    }
                                    query = query + ")";
                                }
                                rfcByIdSocio = null;
                            }
                            if (filterProperty.equals("nombreSocioComercial")) {
                                rfcByIdSocio = daoSociosComerciales.filtroSocioComercialByNombre(empresa.getIdEmpresa(), filterValue);
                                if (rfcByIdSocio != null) {
                                    boolean isFirst = true;
                                    for (Integer idSocio : rfcByIdSocio) {
                                        if (isFirst) {
                                            query = query + " and (idSocioComercial = " + idSocio;
                                            isFirst = false;
                                        } else {
                                            query = query + " or idSocioComercial = " + idSocio;
                                        }
                                    }
                                    query = query + ")";
                                }
                                rfcByIdSocio = null;
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
                    Query result = session.createQuery("select new CfdisRecibidos(idCfdiRecibido, idSocioComercial, serie, folio, fecha, fechaRecepcion, estado, tiposWarn) " + query);
                    result.setFirstResult(first);
                    result.setMaxResults(pageSize + first);
                    this.setRowCount(((Long) session.createQuery(queryCount).uniqueResult()).intValue());
                    listaCFDIs = result.list();
                    tx.commit();
                    if (listaCFDIs == null || listaCFDIs.size() <= 0) {
                        logger.info("listaCfdisCriteria - No existen CfdisRecibidos para mostrar");
                        return lista;
                    }
                    for (CfdisRecibidos item : listaCFDIs) {
                        auxCfdiR = new AuxCfdiRecibido();
                        auxCfdiR.setIdCfdiRecibido(item.getIdCfdiRecibido());
                        auxCfdiR.setIdSocioComercial(item.getIdSocioComercial());
                        auxCfdiR.setSerie(item.getSerie());
                        auxCfdiR.setFolio(item.getFolio());
                        auxCfdiR.setFecha(item.getFecha());
                        auxCfdiR.setFechaRecepcion(item.getFechaRecepcion());
                        auxCfdiR.setEstado(item.getEstado());
                        auxCfdiR.setTiposWarn(item.getTiposWarn());
                        auxCfdiR.setRfcSocioComercial(daoSociosComerciales.getRFCSociobyIdSocio(item.getIdSocioComercial()));
                        auxCfdiR.setNombreSocioComercial(daoSociosComerciales.getNombreSociobyIdSocio(item.getIdSocioComercial()));
                        lista.add(auxCfdiR);
                    }
                } catch (HibernateException he) {
                    descBitacora = "[CFDI_RECIBIDO - init] " + usuario.getUserid() + " cfdiDataList HibernateException ERROR " + he.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                    tx.rollback();
                } catch (Exception ex) {
                    descBitacora = "[CFDI_RECIBIDO - init] " + usuario.getUserid() + " cfdiDataList ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                } finally {
                    listaCFDIs = null;
                    if (session.isOpen()) {
                        session.clear();
                        session.close();
                    }
                }
                return lista;
            }
        };

        lazyCfdisEliminados = new LazyDataModel<AuxCfdiRecibido>() {

            @Override
            public List<AuxCfdiRecibido> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                listaE = new ArrayList<AuxCfdiRecibido>();
                rfcByIdSocio = null;
                try {
                    queryCount = "select count(idCfdiRecibido) ";
                    query = "from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa() + " and (estado ='ELIMINADO' or estado ='CANCELADO')";
                    if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                        query = query + " and idSocioComercial = " + usuario.getIdSocioComercial();
                    }

                    if (!filters.isEmpty()) {
                        it = filters.keySet().iterator();
                        while (it.hasNext()) {
                            filterProperty = (String) it.next();
                            filterValue = filters.get(filterProperty);
                            if (filterProperty.equals("folio")) {
                                query = query + " and " + filterProperty + " = " + filterValue;
                            }
                            if (filterProperty.equals("serie") || filterProperty.equals("estado")) {
                                query = query + " and " + filterProperty + " = '" + filterValue + "'";
                            }
                            if (filterProperty.equals("fecha") || filterProperty.equals("fechaRecepcion")) {
                                query = query + " and " + filterProperty + " like '" + filterValue + "%'";
                            }
                            if (filterProperty.equals("rfcSocioComercial")) {
                                rfcByIdSocio = daoSociosComerciales.filtroSocioComercialByRFC(empresa.getIdEmpresa(), filterValue);
                                if (rfcByIdSocio != null) {
                                    boolean isFirst = true;
                                    for (Integer idSocio : rfcByIdSocio) {
                                        if (isFirst) {
                                            query = query + " and (idSocioComercial = " + idSocio;
                                            isFirst = false;
                                        } else {
                                            query = query + " or idSocioComercial = " + idSocio;
                                        }
                                    }
                                    query = query + ")";
                                }
                                rfcByIdSocio = null;
                            }
                            if (filterProperty.equals("nombreSocioComercial")) {
                                rfcByIdSocio = daoSociosComerciales.filtroSocioComercialByNombre(empresa.getIdEmpresa(), filterValue);
                                if (rfcByIdSocio != null) {
                                    boolean isFirst = true;
                                    for (Integer idSocio : rfcByIdSocio) {
                                        if (isFirst) {
                                            query = query + " and (idSocioComercial = " + idSocio;
                                            isFirst = false;
                                        } else {
                                            query = query + " or idSocioComercial = " + idSocio;
                                        }
                                    }
                                    query = query + ")";
                                }
                                rfcByIdSocio = null;
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
                    Query result = session.createQuery("select new CfdisRecibidos(idCfdiRecibido, idSocioComercial, serie, folio, fecha, fechaRecepcion, estado, tiposWarn) " + query);
                    result.setFirstResult(first);
                    result.setMaxResults(pageSize + first);
                    this.setRowCount(((Long) session.createQuery(queryCount).uniqueResult()).intValue());
                    listaCFDIsE = result.list();
                    tx.commit();
                    if (listaCFDIsE == null || listaCFDIsE.size() <= 0) {
                        logger.info("listaCfdisCriteriaE - No existen CfdisRecibidos para mostrar");
                        return listaE;
                    }
                    for (CfdisRecibidos item : listaCFDIsE) {
                        auxCfdiR = new AuxCfdiRecibido();
                        auxCfdiR.setIdCfdiRecibido(item.getIdCfdiRecibido());
                        auxCfdiR.setIdSocioComercial(item.getIdSocioComercial());
                        auxCfdiR.setSerie(item.getSerie());
                        auxCfdiR.setFolio(item.getFolio());
                        auxCfdiR.setFecha(item.getFecha());
                        auxCfdiR.setFechaRecepcion(item.getFechaRecepcion());
                        auxCfdiR.setEstado(item.getEstado());
                        auxCfdiR.setError(item.getError());
                        auxCfdiR.setRfcSocioComercial(daoSociosComerciales.getRFCSociobyIdSocio(item.getIdSocioComercial()));
                        auxCfdiR.setNombreSocioComercial(daoSociosComerciales.getNombreSociobyIdSocio(item.getIdSocioComercial()));
                        listaE.add(auxCfdiR);
                    }
                } catch (HibernateException he) {
                    descBitacora = "[CFDI_RECIBIDO - init] " + usuario.getUserid() + " lazyCfdisEliminados HibernateException ERROR " + he.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                    tx.rollback();
                } catch (Exception ex) {
                    descBitacora = "[CFDI_RECIBIDO - init] " + usuario.getUserid() + " lazyCfdisEliminados ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                } finally {
                    listaCFDIsE = null;
                    if (session.isOpen()) {
                        session.clear();
                        session.close();
                    }
                }
                return listaE;
            }
        };
    }

    public LazyDataModel<AuxCfdiRecibido> getCfdiDataList() {
        return cfdiDataList;
    }

    public LazyDataModel<AuxCfdiRecibido> getLazyCfdisEliminados() {
        return lazyCfdisEliminados;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public List<AuxCfdiRecibido> getCfdisSeleccionados() {
        return cfdisSeleccionados;
    }

    public void setCfdisSeleccionados(List<AuxCfdiRecibido> cfdisSeleccionados) {
        this.cfdisSeleccionados = cfdisSeleccionados;
    }

    public CfdisRecibidos getCfdiRecibido() {
        return cfdiRecibido;
    }

    public void setCfdiRecibido(CfdisRecibidos cfdiRecibido) {
        this.cfdiRecibido = cfdiRecibido;
    }

    public SelectItem[] getEstadoSelectItems() {
        return estadoSelectItems;
    }

    public Date getFechaMin() {
        return fechaMin;
    }

    public void setFechaMin(Date fechaMin) {
        this.fechaMin = fechaMin;
    }

    public Date getFechaMax() {
        return fechaMax;
    }

    public void setFechaMax(Date fechaMax) {
        this.fechaMax = fechaMax;
    }

    public Integer getIntIdSocio() {
        return intIdSocio;
    }

    public void setIntIdSocio(Integer intIdSocio) {
        this.intIdSocio = intIdSocio;
    }

    public List<SelectItem> getSelectOneItemsSocio() {
        return selectOneItemsSocio;
    }

    public void setSelectOneItemsSocio(List<SelectItem> selectOneItemsSocio) {
        this.selectOneItemsSocio = selectOneItemsSocio;
    }

    public boolean isReportarTodos() {
        return reportarTodos;
    }

    public void setReportarTodos(boolean reportarTodos) {
        this.reportarTodos = reportarTodos;
    }

    public SelectItem[] getCfdiSelectItems() {
        return cfdiSelectItems;
    }

    public void setCfdiSelectItems(SelectItem[] cfdiSelectItems) {
        this.cfdiSelectItems = cfdiSelectItems;
    }

    public List<SelectItem> getItemsOrdenCompra() {
        return itemsOrdenCompra;
    }

    public void setItemsOrdenCompra(List<SelectItem> itemsOrdenCompra) {
        this.itemsOrdenCompra = itemsOrdenCompra;
    }

    public OrdenesCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenesCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public boolean isCrearOrden() {
        return crearOrden;
    }

    public void setCrearOrden(boolean crearOrden) {
        this.crearOrden = crearOrden;
    }

    public Integer getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public AuxCfdiRecibido getAuxCfdiR() {
        return auxCfdiR;
    }

    public void setAuxCfdiR(AuxCfdiRecibido auxCfdiR) {
        this.auxCfdiR = auxCfdiR;
    }

    public List<String> getListError() {
        return listError;
    }
    
    

    public void obtenerRfcSocios() {
        intIdSocio = 0;
        logger.info("Inicia obtenerRfcSocios");
        try {
            selectOneItemsSocio = new ArrayList<SelectItem>();
            listaSocios = null;
            selectItem = null;
            listaSocios = daoSociosComerciales.listaSociosHQL("select new SociosComerciales(idSocioComercial, rfc) from SociosComerciales where idEmpresa = " + empresa.getIdEmpresa());
            if (listaSocios.isEmpty()) {
                logger.info("[CFDI_RECIBIDO - obtenerRfcSocios] - No se han encontrado resultados para Socios Comerciales");
            } else {
                for (SociosComerciales var : listaSocios) {
                    selectItem = new SelectItem(var.getIdSocioComercial(), var.getRfc());
                    selectOneItemsSocio.add(selectItem);
                }
            }
        } catch (Exception ex) {
            descBitacora = "[CFDI_RECIBIDO - obtenerRfcSocios] " + usuario.getUserid() + " ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void resetOneItemSocio() {
        selectOneItemsSocio = null;
    }

    public String deleteCFDIs() {
        int num = 0;

        if (cfdisSeleccionados == null || cfdisSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Seleccione al menos un CFDI");
            logger.warn("deleteCFDIs - no existen CFDIs seleccionados");
        } else {

            for (AuxCfdiRecibido imprimirCFDI : cfdisSeleccionados) {
                cfdiRecibido = new CfdisRecibidos();
                try {
                    cfdiRecibido = daoCfdiRecibido.getCfdiByID(imprimirCFDI.getIdCfdiRecibido());
                    if (daoCfdiRecibido.delete(cfdiRecibido)) {
                        num++;
                        descBitacora = "[CFDI_RECIBIDO - deleteCFDIs] " + usuario.getUserid() + " elimino permanentemente el CFDI con id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                        logger.info(descBitacora);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminar", num + " CFDI(s) eliminado(s)");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar el CFDI");
                        descBitacora = "[CFDI_RECIBIDO - deleteCFDIs] " + usuario.getUserid() + " Error al elimnar el CFDI con id " + cfdiRecibido.getIdCfdiRecibido() + "  uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    }
                } catch (Exception e) {
                    descBitacora = "[CFDI_RECIBIDO - deleteCFDIs] " + usuario.getUserid() + " deleteCFDIs ERROR " + e.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al eliminar los CFDIs.");
                    logger.error(descBitacora);
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return "/CFDI/eliminados";
    }

    //ESTE METODO COLOCA LOS CFDIS EN ESTADO ELIMINADO
    public String eliminarCFDIs() {
        int num = 0;

        if (cfdisSeleccionados == null || cfdisSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Seleccione al menos un CFDI");
            logger.info("eliminarCFDIs - no existen CFDIs seleccionados");
        } else {

            for (AuxCfdiRecibido imprimirCFDI : cfdisSeleccionados) {
                cfdiRecibido = new CfdisRecibidos();
                try {
                    cfdiRecibido = daoCfdiRecibido.getCfdiByID(imprimirCFDI.getIdCfdiRecibido());
                    if (!(cfdiRecibido.getEstado().equals(EstadoCfdiR.SIN_VALIDAR.name()) || cfdiRecibido.getEstado().equals(EstadoCfdiR.NUEVO.name()))) {
                        cfdiRecibido.setEstado(EstadoCfdiR.ELIMINADO.name());
                        if (daoCfdiRecibido.actualizarCfdi(cfdiRecibido)) {
                            num++;
                            descBitacora = "[CFDI_RECIBIDO - eliminarCFDIs] " + usuario.getUserid() + " coloco en estado eliminado el CFDI con id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                            logger.info(descBitacora);
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminar", num + " CFDI(s) eliminado(s).");
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar el CFDI");
                            descBitacora = "[CFDI_RECIBIDO - eliminarCFDIs] " + usuario.getUserid() + " Error al colocar en estado eliminado el CFDI con id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                            logger.error(descBitacora);
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible eliminar CFDI(s) en estado " + cfdiRecibido.getEstado());
                        descBitacora = "[CFDI_RECIBIDO - eliminarCFDIs] " + usuario.getUserid() + " Intento colocar en estado eliminado el CFDI con id " + cfdiRecibido.getIdCfdiRecibido() + " no es posible su estado es " + cfdiRecibido.getEstado();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    }
                } catch (Exception e) {
                    descBitacora = "[CFDI_RECIBIDO - eliminarCFDIs] " + usuario.getUserid() + " ERROR " + e.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar los CFDIs");
                    logger.error(descBitacora);
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return "/CFDI/recibidos";
    }

    public void existeSeleccionCFDI() {
        boolean estadoCFDI = false;
        if (cfdisSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione al menos un CFDI");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            estadoCFDI = true;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoCFDI", estadoCFDI);
    }

    public void verCFDI() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        boolean estadoCFDI = false;
        try {
            cfdiRecibido = null;
            cfdiRecibido = daoCfdiRecibido.getCfdiByID(id);
            if (cfdiRecibido != null) {
                auxCfdiR = new AuxCfdiRecibido();
                auxCfdiR.setSerie(cfdiRecibido.getSerie());
                auxCfdiR.setFolio(cfdiRecibido.getFolio());
                auxCfdiR.setUuid(cfdiRecibido.getUuid());
                auxCfdiR.setFecha(cfdiRecibido.getFecha());
                auxCfdiR.setTotal(cfdiRecibido.getTotal());
                auxCfdiR.setFechaRecepcion(cfdiRecibido.getFechaRecepcion());
                auxCfdiR.setEstado(cfdiRecibido.getEstado());
                auxCfdiR.setError(cfdiRecibido.getError());
                listaErrores(cfdiRecibido.getError());
                auxCfdiR.setTotalOc(cfdiRecibido.getTotalOc());
                auxCfdiR.setTipoMoneda(cfdiRecibido.getTipoMoneda());
                auxCfdiR.setTipoCambio(cfdiRecibido.getTipoCambio());
                auxCfdiR.setEntradaAlmacen(cfdiRecibido.getEntradaAlmacen());
                auxCfdiR.setControlCalidad(cfdiRecibido.getControlCalidad());
                auxCfdiR.setAutorizacion(cfdiRecibido.getAutorizacion());
                auxCfdiR.setTiposWarn(cfdiRecibido.getTiposWarn());
                auxCfdiR.setRfcSocioComercial(daoSociosComerciales.getRFCSociobyIdSocio(cfdiRecibido.getIdSocioComercial()));
                auxCfdiR.setNombreSocioComercial(daoSociosComerciales.getNombreSociobyIdSocio(cfdiRecibido.getIdSocioComercial()));
                if (cfdiRecibido.getIdOrdenCompra() != null) {
                    auxCfdiR.setNumeroOrdenCompra(daoOrdenCompra.numOrdenCompraById(cfdiRecibido.getIdOrdenCompra()));
                }
                cfdiRecibido = null;
                estadoCFDI = true;
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - verCFDI] ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoCFDI", estadoCFDI);
    }

    public void listaErrores(String error) {
        String[] numerosComoArray = error.split("\n");
        int size = numerosComoArray.length;
        for (int i = 0; i < size; i++) {
            listError.add(numerosComoArray[i]);
        }
        numerosComoArray = null;
    }

    public void downloadXML() {
        logger.info(usuario.getUserid() + " inicia descarga de XML");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        try {
            cfdiRecibido = new CfdisRecibidos();
            cfdiRecibido = daoCfdiRecibido.getCfdiByID(id);
            if (cfdiRecibido.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible descargar el archivo XML. El CFDI se encuentra Cancelado.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                descBitacora = "[CFDI_RECIBIDO - downloadXML] " + usuario.getUserid() + " No es posible descargar el XML del CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + " su estado es: " + cfdiRecibido.getEstado();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(descBitacora);
                return;
            }
            strNombre = cfdiRecibido.getNombreArchivo();
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
            descBitacora = "[CFDI_RECIBIDO - downloadXML] " + usuario.getUserid() + " Descargo XML de CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(descBitacora);
            logger.info(usuario.getUserid() + ". downloadXML- XML: " + strNombre + " Generado.");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al descargar el archivo XML."));
            descBitacora = "[CFDI_RECIBIDO - downloadXML] " + usuario.getUserid() + " ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void downloadPDF() {
        logger.info(usuario.getUserid() + " downloadPDF - inicia descarga de PDF");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));

        try {
            cfdiRecibido = new CfdisRecibidos();
            cfdiRecibido = daoCfdiRecibido.getCfdiByID(id);
            if (cfdiRecibido.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible descargar el archivo PDF. El CFDI se encuentra Cancelado.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                descBitacora = "[CFDI_RECIBIDO - downloadPDF] " + usuario.getUserid() + " No es posible descargar el PFD del CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + " su estado es: " + cfdiRecibido.getEstado();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(descBitacora);
                return;
            }

            strNombre = cfdiRecibido.getNombreArchivo();
            if (strNombre != null && !strNombre.trim().isEmpty()) {
                strNombre = strNombre.replace(" ", "") + ".pdf";
            } else {
                strNombre = cfdiRecibido.getIdCfdiRecibido() + ".pdf";
            }
            plantillas = daoPlantilla.listaPlantillasByIdEmpresa(empresa.getIdEmpresa());
            if (plantillas.size() <= 0) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No existen Plantillas almcenadas.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                descBitacora = "[CFDI_RECIBIDO - downloadPDF] " + usuario.getUserid() + " No existen Plantillas almcenadas";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(descBitacora);
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
            descBitacora = "[CFDI_RECIBIDO - downloadPDF] " + usuario.getUserid() + " descargo PDF de CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(descBitacora);
            logger.info(usuario.getUserid() + ". downloadPDF- PDF: " + strNombre + " Generado.");
        } catch (JRException ex) {
            descBitacora = "[CFDI_RECIBIDO - downloadPDF] " + usuario.getUserid() + " JRException ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al descargar el archivo PDF."));
            logger.error(descBitacora);
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - downloadPDF] " + usuario.getUserid() + " Exception ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al descargar el archivo PDF."));
            logger.error(descBitacora);
        }
    }

    public String validar() {
        logger.info(usuario.getUserid() + " Inicia Validar CFDIs");
        int numCfdis = 0;
        if (cfdisSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione al menos un CFDI");
        } else {
            for (AuxCfdiRecibido imprimirCFDI : cfdisSeleccionados) {
                cfdiRecibido = new CfdisRecibidos();
                try {
                    cfdiRecibido = daoCfdiRecibido.getCfdiByID(imprimirCFDI.getIdCfdiRecibido());
                    if (!imprimirCFDI.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                        cfdiRecibido.setEstado("NUEVO");
                        cfdiRecibido.setReportado(null);
                        cfdiRecibido.setReportadoXsa(null);
                        if (daoCfdiRecibido.actualizarCfdi(cfdiRecibido)) {
                            numCfdis++;
                            descBitacora = "[CFDI_RECIBIDO - validar] " + usuario.getUserid() + " CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid: " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + " se le ha colocado estado: " + cfdiRecibido.getEstado() + " para ser Validado";
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                            logger.info(descBitacora);

                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", " Ocurrio un error al validar el CFDI");
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            descBitacora = "[CFDI_RECIBIDO - validar] " + usuario.getUserid() + " Error al modificar el CFDI id " + cfdiRecibido.getIdCfdiRecibido() + "  uuid: " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                            logger.error(descBitacora);
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible validar CFDIs Cancelados");
                        descBitacora = "[CFDI_RECIBIDO - validar] " + usuario.getUserid() + " CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid: " + cfdiRecibido.getUuid() + " Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + " No se puede colocar en estado NUEVO, ya que se encuentra " + imprimirCFDI.getEstado();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                        logger.info(descBitacora);
                    }
                } catch (Exception ex) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", " Ocurrio un error al validar el CFDI");
                    descBitacora = "[CFDI_RECIBIDO - validar] " + usuario.getUserid() + " ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
            logger.info(usuario.getUserid() + " Validacion " + numCfdis + " CFDIs seran validados nuevamente.");
            if (numCfdis > 0) {
                if (numCfdis == 1) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Validación", numCfdis + " CFDI será validado nuevamente en unos minutos.");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Validación", numCfdis + " CFDIs serán validados nuevamente en unos minutos.");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", " Ningun CFDI será validado.");
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "/CFDI/recibidos";
    }

    public String restaurar() {
        logger.info(usuario.getUserid() + ". Inicia restaurar CFDIs");
        int numCfdis = 0;
        if (cfdisSeleccionados.isEmpty()) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione al menos un CFDI");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            return "/CFDI/eliminados";
        }
        for (AuxCfdiRecibido imprimirCFDI : cfdisSeleccionados) {
            cfdiRecibido = new CfdisRecibidos();
            try {
                cfdiRecibido = daoCfdiRecibido.getCfdiByID(imprimirCFDI.getIdCfdiRecibido());
                if (!imprimirCFDI.getEstado().equals(EstadoCfdiR.CANCELADO.name())) {
                    cfdiRecibido.setEstado(EstadoCfdiR.NUEVO.name());
                    cfdiRecibido.setEstadoNotificacion(NotificacionCfdiR.SIN_NOTIFICAR.name());
                    if (daoCfdiRecibido.actualizarCfdi(cfdiRecibido)) {
                        numCfdis++;
                        descBitacora = "[CFDI_RECIBIDO - restaurar] " + usuario.getUserid() + " CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid: " + cfdiRecibido.getUuid() + " se le ha colocado estado: " + cfdiRecibido.getEstado() + ". Ha sido restablecido.";
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                        logger.info(descBitacora);
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", " Ocurrio un error al restaurar el CFDI");
                        descBitacora = "[CFDI_RECIBIDO - restaurar] " + usuario.getUserid() + " Error al modificar el CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid: " + cfdiRecibido.getUuid();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", " No es posible restaurar el CFDI se encuentra Cancelado");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    descBitacora = "[CFDI_RECIBIDO - restaurar] " + usuario.getUserid() + " CFDI id " + cfdiRecibido.getIdCfdiRecibido() + " uuid: " + cfdiRecibido.getUuid() + " No se puede colocar en Estado NUEVO ya que se encuenta " + imprimirCFDI.getEstado();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.info(descBitacora);
                }

            } catch (Exception ex) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", " Ocurrio un error al restaurar el CFDI");
                descBitacora = "[CFDI_RECIBIDO - restaurar] " + usuario.getUserid() + " ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
            }
        }
        logger.info("restaurar " + numCfdis + " CFDIs se ha restaurado.");
        if (numCfdis > 0) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", numCfdis + " CFDI(s) han sido restaurado(s).");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Ningún documento ha sido Restaurado.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "/CFDI/eliminados";
    }

    public void handleFileUpload(FileUploadEvent event) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        congifEmpresa = new ConfiguracionesEmpresas();
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String ruta = "/work/ftp/" + empresa.getRfc() + "/VALIDACION";

        if (!(nombreArchivo.endsWith(".xml") || nombreArchivo.endsWith(".zip") || nombreArchivo.endsWith(".pdf"))) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "El archivo debe ser con extensión \".xml\" , \".zip\" ó \".pdf\""));
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
                descBitacora = "[CFDI_RECIBIDO - handleFileUpload] " + usuario.getUserid() + " Archivo " + event.getFile().getFileName() + " cargado para validacion";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(descBitacora);
                if (nombreArchivo.endsWith(".zip")) {
                    if (manejorArchivos.extractZipTo(ruta + System.getProperty("file.separator") + event.getFile().getFileName(), ruta)) {
                        manejorArchivos.eliminar(ruta + System.getProperty("file.separator") + event.getFile().getFileName());
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Configure un Usuario FTP para archvios de Validación."));
                descBitacora = "[CFDI_RECIBIDO - handleFileUpload] " + usuario.getUserid() + " No existe Configuracion Usuario FTP para archvios de Validación.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                logger.warn(descBitacora);
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nuevo", "Ocurrio un error cargando los archivos."));
            descBitacora = "[CFDI_RECIBIDO - handleFileUpload] " + usuario.getUserid() + " IOException ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nuevo", "Ocurrio un error cargando los archivos."));
            descBitacora = "[CFDI_RECIBIDO - handleFileUpload] " + usuario.getUserid() + " Exception ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = "[CFDI_RECIBIDO - handleFileUpload] " + usuario.getUserid() + " Error cerrando inputStream ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    descBitacora = "[CFDI_RECIBIDO - handleFileUpload] " + usuario.getUserid() + " Error cerrando outputStream ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
        }
    }

    public void fileUploadSocio(FileUploadEvent event) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        socioComercial = null;
        boolean writeFile = true;
        String rfc = null;
        congifEmpresa = new ConfiguracionesEmpresas();
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String ruta = "/work/ftp/" + empresa.getRfc() + "/VALIDACION";

        if (!(nombreArchivo.endsWith(".xml") || nombreArchivo.endsWith(".zip") || nombreArchivo.endsWith(".pdf"))) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "El archivo debe ser con extensión \".xml\" , \".zip\" ó \".pdf\""));
            return;
        }
        try {
            socioComercial = daoSociosComerciales.getSocioComercialByID(usuario.getIdSocioComercial());
            congifEmpresa = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.VAL_USER_FTP.name());
            if (!congifEmpresa.getValor().trim().isEmpty()) {
                File folder = new File(ruta);
                if (!folder.exists()) {
                    logger.info("no se encontro el directorio " + ruta);
                    folder.mkdirs();
                    logger.info(ruta + " creado");
                }
                if (nombreArchivo.endsWith(".xml")) {
                    writeFile = false;
                    revisarXml = new RevisarXML(event.getFile().getInputstream());
                    rfc = revisarXml.revisaNodos("rfc", "cfdi:Comprobante", "Emisor");
                    if (socioComercial.getRfc().equals(rfc)) {
                        writeFile = true;
                        logger.info("[CFDI_RECIBIDO - fileUploadSocio] " + usuario.getUserid() + " El rfc del socio comercial " + socioComercial.getRfc() + " coincide con el rfc del nodo emisor del xml " + rfc);
                    } else {
                        logger.info("[CFDI_RECIBIDO - fileUploadSocio] " + usuario.getUserid() + " El rfc del socio comercial " + socioComercial.getRfc() + " no coincide con el rfc del nodo emisor del xml " + rfc);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", "Archivo " + event.getFile().getFileName() + " no corresponde al RFC " + socioComercial.getRfc()));
                    }
                }
                if (writeFile) {
                    outputStream = new FileOutputStream(new File(ruta + System.getProperty("file.separator") + event.getFile().getFileName()));
                    inputStream = event.getFile().getInputstream();
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Archivo " + event.getFile().getFileName() + " cargado"));
                    descBitacora = "[CFDI_RECIBIDO - fileUploadSocio] " + usuario.getUserid() + " Archivo " + event.getFile().getFileName() + " cargado para validacion";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                    if (nombreArchivo.endsWith(".zip")) {
                        if (manejorArchivos.extractZipSocio(ruta + System.getProperty("file.separator") + event.getFile().getFileName(), ruta, socioComercial.getRfc())) {
                            manejorArchivos.eliminar(ruta + System.getProperty("file.separator") + event.getFile().getFileName());
                        }
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Configure un Usuario FTP para archvios de Validación."));
                descBitacora = "[CFDI_RECIBIDO - fileUploadSocio] " + usuario.getUserid() + " No existe Configuracion Usuario FTP para archvios de Validación.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                logger.warn(descBitacora);
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nuevo", "Ocurrio un error cargando los archivos."));
            descBitacora = "[CFDI_RECIBIDO - fileUploadSocio] " + usuario.getUserid() + " IOException ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nuevo", "Ocurrio un error cargando los archivos."));
            descBitacora = "[CFDI_RECIBIDO - fileUploadSocio] " + usuario.getUserid() + " Exception ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = "[CFDI_RECIBIDO - fileUploadSocio] " + usuario.getUserid() + " Error cerrando inputStream ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    descBitacora = "[CFDI_RECIBIDO - fileUploadSocio] " + usuario.getUserid() + " Error cerrando outputStream ERROR " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
        }
    }

    public void descargarReporte() {
        logger.info(usuario.getUserid() + ". Inicia descargar reporte");
        boolean donwload;
        String hql = "select new CfdisRecibidos(idCfdiRecibido, idSocioComercial, serie, folio, uuid, fecha, total, fechaRecepcion, estado, error, totalOc, tipoMoneda, tipoCambio, entradaAlmacen, controlCalidad, autorizacion, tiposWarn) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa() + " and (estado != 'ELIMINADO' and estado != 'CANCELADO')";
        try {
            List<CfdisRecibidos> listCfdis;
            if (!reportarTodos) {
                if (intIdSocio != 0) {
                    hql = hql + " and idSocioComercial = " + intIdSocio;
                }
                if (fechaMin != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String minFecha = df.format(fechaMin);
                    hql = hql + " and fecha > '" + minFecha + "'";
                }
                if (fechaMax != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String maxFecha = df.format(fechaMax);
                    maxFecha = maxFecha + " 23:59:59";
                    hql = hql + " and fecha < '" + maxFecha + "'";
                }
            }
            hql = hql + " ORDER BY fechaRecepcion DESC";
            logger.debug("hql " + hql);
            listCfdis = daoCfdiRecibido.listaCfdisByHql(hql);
            if (!listCfdis.isEmpty()) {
                donwload = generarReporte(listCfdis);
                if (donwload) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Generando Reporte."));
                    descBitacora = "[CFDI_RECIBIDO - descargarReporte] " + usuario.getUserid() + " Reporte Generado Corectamente";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error generando Reporte."));
                    descBitacora = "[CFDI_RECIBIDO - descargarReporte] " + usuario.getUserid() + " Error generando Reporte";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "No se han encontrado resultados."));
                logger.warn(usuario.getUserid() + " .La cosulta no devuleve ningun registro");
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - descargarReporte] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void descargarReporteSocio() {
        logger.info(usuario.getUserid() + ". Inicia descargar reporte Socio Comercial");
        boolean donwload;
        String hql = "select new CfdisRecibidos(idCfdiRecibido, idSocioComercial, serie, folio, uuid, fecha, total, fechaRecepcion, estado, error, totalOc, tipoMoneda, tipoCambio, entradaAlmacen, controlCalidad, autorizacion, tiposWarn) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa() + "and idSocioComercial = " + usuario.getIdSocioComercial() + " and (estado != 'ELIMINADO' and estado != 'CANCELADO')";
        try {
            List<CfdisRecibidos> listCfdis;
            if (!reportarTodos) {
                if (fechaMin != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String minFecha = df.format(fechaMin);
                    hql = hql + " and fecha > '" + minFecha + "'";
                }
                if (fechaMax != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String maxFecha = df.format(fechaMax);
                    maxFecha = maxFecha + " 23:59:59";
                    hql = hql + " and fecha < '" + maxFecha + "'";
                }
            }
            hql = hql + " ORDER BY fechaRecepcion DESC";
            logger.debug("hql " + hql);
            listCfdis = daoCfdiRecibido.listaCfdisByHql(hql);
            if (!listCfdis.isEmpty()) {
                donwload = generarReporte(listCfdis);
                if (donwload) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Generando Reporte."));
                    descBitacora = "[CFDI_RECIBIDO - descargarReporteSocio] " + usuario.getUserid() + " Reporte Socio Comercial Generado Corectamente";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error generando Reporte."));
                    descBitacora = "[CFDI_RECIBIDO - descargarReporteSocio] " + usuario.getUserid() + " Error generando Reporte Socio Comercial";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "No se han encontrado resultados."));
                logger.warn(usuario.getUserid() + " .La cosulta no devuleve ningun registro");
            }

        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - descargarReporteSocio] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void descargarReporteEliminados() {
        logger.info(usuario.getUserid() + ". Inicia descargar reporte Eliminados");
        boolean donwload;
        String hql = "select new CfdisRecibidos(idCfdiRecibido, idSocioComercial, serie, folio, uuid, fecha, total, fechaRecepcion, estado, error, totalOc, tipoMoneda, tipoCambio, entradaAlmacen, controlCalidad, autorizacion, tiposWarn) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa() + " and (estado ='ELIMINADO' or estado ='CANCELADO')";
        try {
            List<CfdisRecibidos> listCfdis;
            if (!reportarTodos) {
                if (intIdSocio != 0) {
                    hql = hql + " and idSocioComercial = " + intIdSocio;
                }
                if (fechaMin != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String minFecha = df.format(fechaMin);
                    hql = hql + " and fecha > '" + minFecha + "'";
                }
                if (fechaMax != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String maxFecha = df.format(fechaMax);
                    maxFecha = maxFecha + " 23:59:59";
                    hql = hql + " and fecha < '" + maxFecha + "'";
                }
            }
            hql = hql + " ORDER BY fechaRecepcion DESC";
            logger.debug("hql " + hql);
            listCfdis = daoCfdiRecibido.listaCfdisByHql(hql);
            if (!listCfdis.isEmpty()) {
                donwload = generarReporte(listCfdis);
                if (donwload) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Generando Reporte."));
                    descBitacora = "[CFDI_RECIBIDO - descargarReporteEliminados] " + usuario.getUserid() + " Reporte Eliminados Generado Corectamente";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error generando Reporte."));
                    descBitacora = "[CFDI_RECIBIDO - descargarReporteEliminados] " + usuario.getUserid() + " Error generando Reporte Eliminados";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "No se han encontrado resultados."));
                logger.warn(usuario.getUserid() + " .La cosulta no devuleve ningun registro");
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - descargarReporteEliminados] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void descargarReporteEliminadosSocio() {
        logger.info(usuario.getUserid() + ". Inicia descargar reporte Eliminados Socio Comercial");
        boolean donwload;
        String hql = "select new CfdisRecibidos(idCfdiRecibido, idSocioComercial, serie, folio, uuid, fecha, total, fechaRecepcion, estado, error, totalOc, tipoMoneda, tipoCambio, entradaAlmacen, controlCalidad, autorizacion, tiposWarn) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa() + "and idSocioComercial = " + usuario.getIdSocioComercial() + " and (estado ='ELIMINADO' or estado ='CANCELADO')";
        try {
            List<CfdisRecibidos> listCfdis;
            if (!reportarTodos) {
                if (fechaMin != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String minFecha = df.format(fechaMin);
                    hql = hql + " and fecha > '" + minFecha + "'";
                }
                if (fechaMax != null) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String maxFecha = df.format(fechaMax);
                    maxFecha = maxFecha + " 23:59:59";
                    hql = hql + " and fecha < '" + maxFecha + "'";
                }
            }
            hql = hql + " ORDER BY fechaRecepcion DESC";
            logger.debug("hql " + hql);
            listCfdis = daoCfdiRecibido.listaCfdisByHql(hql);
            if (!listCfdis.isEmpty()) {
                donwload = generarReporte(listCfdis);
                if (donwload) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Generando Reporte."));
                    descBitacora = "[CFDI_RECIBIDO - descargarReporteEliminadosSocio] " + usuario.getUserid() + " Reporte Eliminados Socio Comercial Generado Corectamente";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error generando Reporte."));
                    descBitacora = "[CFDI_RECIBIDO - descargarReporteEliminadosSocio] " + usuario.getUserid() + " Error generando Reporte Elimindados Socio Comercial";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "No se han encontrado resultados."));
                logger.warn(usuario.getUserid() + " .La cosulta no devuleve ningun registro");
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - descargarReporteEliminadosSocio] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void descargarZip() {
        logger.info(usuario.getUserid() + ". Inicia descargarZip");
        String ruta = "/work/iqtb/validacionfiles/zip/" + empresa.getRfc() + "/" + usuario.getUserid() + "/";

        try {
            idsCfdisR = daoCfdiRecibido.listaIdsCfdisRByHQL("select idCfdiRecibido " + query);
            logger.debug("QUERY: select idCfdiRecibido " + query);
            if (idsCfdisR != null) {
                plantillas = null;
                File archivoZip = null;
                plantillas = daoPlantilla.listaPlantillasByIdEmpresa(empresa.getIdEmpresa());
                if (plantillas.size() <= 0) {
                    descBitacora = "[CFDI_RECIBIDO - descargarZip] " + usuario.getUserid() + " No existen Plantillas almcenadas para crear PDF. Se generara el ZIP solo con XML.";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                    logger.warn(descBitacora);
                    archivoZip = new GenerarArchivo().getArchivoAMBOS(idsCfdisR, ruta, "");
                } else {
                    logger.info(usuario.getUserid() + ". Inicia generar ZIP con PDF y XML");
                    archivoZip = new GenerarArchivo().getArchivoAMBOS(idsCfdisR, ruta, plantillas.get(0).getRuta());
                }
                logger.info("[CFDI_RECIBIDO - descargarZip] " + usuario.getUserid() + " Inicia generacion de PDF.");
                if (archivoZip != null) {
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
                    response.setContentType("application/force-download");
                    String disposition = "attachment; fileName=" + archivoZip.getName();
                    response.setHeader("Content-Disposition", disposition);
                    ServletOutputStream out = response.getOutputStream();
                    InputStream ascii;
                    ascii = new FileInputStream(archivoZip);
                    byte[] buf = new byte[5 * 1000 * 1024];
                    int bytesRead;
                    while ((bytesRead = ascii.read(buf)) != -1) {
                        out.write(buf, 0, bytesRead);
                    }
                    out.flush();
                    out.close();
                    ctx.responseComplete();
                    descBitacora = "[CFDI_RECIBIDO - descargarZip] " + usuario.getUserid() + " genero/descargo PDF/XML en formato zip.";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                    manejorArchivos.eliminar(archivoZip.getAbsolutePath());
                    logger.info(usuario.getUserid() + ". Termina descargarZip");
                } else {
                    descBitacora = "[CFDI_RECIBIDO - descargarZip] " + usuario.getUserid() + " Archivo ZIP es nulo, no se genero correctamente.";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Descargar PDF/XML", "No se han encontrado resultados"));
                logger.info("[CFDI_RECIBIDO - descargarZip] " + usuario.getUserid() + " no se han encontrado resultados.");
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - descargarZip] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void descargarPDF() {
        logger.info(usuario.getUserid() + ". Inicia descargarPDF");
        String ruta = "/work/iqtb/validacionfiles/zip/" + empresa.getRfc() + "/" + usuario.getUserid() + "/";

        try {
            idsCfdisR = daoCfdiRecibido.listaIdsCfdisRByHQL("select idCfdiRecibido " + query);
            logger.info("QUERY: select idCfdiRecibido " + query);
            if (idsCfdisR != null) {
                plantillas = null;
                File archivoXml = null;
                plantillas = daoPlantilla.listaPlantillasByIdEmpresa(empresa.getIdEmpresa());
                if (plantillas.size() <= 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descargar PDF", "No existe Plantilla almacenada para generar PDF."));
                    logger.info("[CFDI_RECIBIDO - descargarPDF] " + usuario.getUserid() + " no existe plantilla para generar PDF.");
                } else {
                    logger.info("[CFDI_RECIBIDO - descargarPDF] " + usuario.getUserid() + " Inicia generacion de PDF.");
                    archivoXml = generarArchivo.getArchivoPDF(idsCfdisR, ruta, plantillas.get(0).getRuta());
                    if (archivoXml != null) {
                        FacesContext ctx = FacesContext.getCurrentInstance();
                        HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
                        response.setContentType("application/force-download");
                        String disposition = "attachment; fileName=" + archivoXml.getName();
                        response.setHeader("Content-Disposition", disposition);
                        ServletOutputStream out = response.getOutputStream();
                        InputStream ascii;
                        ascii = new FileInputStream(archivoXml);
                        byte[] buf = new byte[5 * 1000 * 1024];
                        int bytesRead;
                        while ((bytesRead = ascii.read(buf)) != -1) {
                            out.write(buf, 0, bytesRead);
                        }
                        out.flush();
                        out.close();
                        ctx.responseComplete();
                        descBitacora = "[CFDI_RECIBIDO - descargarPDF] " + usuario.getUserid() + " genero/descargo archivos PDF(s)";
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                        logger.info(descBitacora);
                        manejorArchivos.eliminar(archivoXml.getAbsolutePath());
                        logger.info(usuario.getUserid() + ". Termina descargarPDF");
                    } else {
                        descBitacora = "[CFDI_RECIBIDO - descargarPDF] " + usuario.getUserid() + " Archivo PDF es nulo, no se genero correctamente.";
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Descargar PDF", "No se han encontrado resultados"));
                logger.info("[CFDI_RECIBIDO - descargarPDF] " + usuario.getUserid() + " no se han encontrado resultados.");
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - descargarPDF] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void descargarXML() {
        logger.info(usuario.getUserid() + ". Inicia descargarXML");
        String ruta = "/work/iqtb/validacionfiles/zip/" + empresa.getRfc() + "/" + usuario.getUserid() + "/";
        try {
            idsCfdisR = daoCfdiRecibido.listaIdsCfdisRByHQL("select idCfdiRecibido " + query);
            logger.info("QUERY: select idCfdiRecibido " + query);
            if (idsCfdisR != null) {
                File archivoXml = null;
                logger.info("[CFDI_RECIBIDO - descargarXML] " + usuario.getUserid() + " Inicia generacion de XML.");
                archivoXml = generarArchivo.getArchivoXML(idsCfdisR, ruta);
                if (archivoXml != null) {
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
                    response.setContentType("application/force-download");
                    String disposition = "attachment; fileName=" + archivoXml.getName();
                    response.setHeader("Content-Disposition", disposition);
                    ServletOutputStream out = response.getOutputStream();
                    InputStream ascii;
                    ascii = new FileInputStream(archivoXml);
                    byte[] buf = new byte[5 * 1000 * 1024];
                    int bytesRead;
                    while ((bytesRead = ascii.read(buf)) != -1) {
                        out.write(buf, 0, bytesRead);
                    }
                    out.flush();
                    out.close();
                    ctx.responseComplete();
                    descBitacora = "[CFDI_RECIBIDO - descargarXML] " + usuario.getUserid() + " genero/descargo archivos XML(s)";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                    manejorArchivos.eliminar(archivoXml.getAbsolutePath());
                    logger.info(usuario.getUserid() + ". Termina descargarXML");

                } else {
                    descBitacora = "[CFDI_RECIBIDO - descargarXML] " + usuario.getUserid() + " Archivo XML es nulo, no se genero correctamente.";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Descargar XML", "No se han encontrado resultados"));
                logger.info("[CFDI_RECIBIDO - descargarXML] " + usuario.getUserid() + " no se han encontrado resultados.");
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - descargarXML] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public String cancelarCfdi() {
        logger.info(usuario.getUserid() + ". Inicia Notificacion de Cancelacion CFDIs");
        int cancelados = 0;
        int notificados = 0;
        for (AuxCfdiRecibido imprimirCFDI : cfdisSeleccionados) {
            cfdiRecibido = null;
            try {
                cfdiRecibido = daoCfdiRecibido.getCfdiByID(imprimirCFDI.getIdCfdiRecibido());
                cfdiRecibido.setEstado(EstadoCfdiR.CANCELADO.name());
                cfdiRecibido.setReportado(null);
                if (daoCfdiRecibido.actualizarCfdi(cfdiRecibido)) {
                    descBitacora = "[CFDI_RECIBIDO - cancelarCfdi] " + usuario.getUserid() + " CFDI con uuid: " + cfdiRecibido.getUuid() + " serie " + cfdiRecibido.getSerie() + " folio " + cfdiRecibido.getFolio() + " se le ha colocado estado: " + cfdiRecibido.getEstado();
                    logger.info(descBitacora);
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    cancelados++;
                    if (notificarCancelacion(imprimirCFDI)) {
                        descBitacora = "[CFDI_RECIBIDO - cancelarCfdi] " + usuario.getUserid() + " realizo correctamente la notificacion de cancelacion del cfdi con uuid: " + cfdiRecibido.getUuid() + " serie " + cfdiRecibido.getSerie() + " folio " + cfdiRecibido.getFolio();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                        logger.info(descBitacora);
                        notificados++;
                    } else {
                        descBitacora = "[CFDI_RECIBIDO - cancelarCfdi] " + usuario.getUserid() + " Error al intentar notificacion de cancelacion del cfdi con uuid: " + cfdiRecibido.getUuid() + " serie " + cfdiRecibido.getSerie() + " folio " + cfdiRecibido.getFolio();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    }
                } else {
                    descBitacora = "[CFDI_RECIBIDO - cancelarCfdi] " + usuario.getUserid() + " Error al actualizar CFDI con id: " + cfdiRecibido.getIdCfdiRecibido();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } catch (Exception ex) {
                descBitacora = "[CFDI_RECIBIDO - cancelarCfdi] " + usuario.getUserid() + " ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
            }
        }
        logger.info(usuario.getUserid() + ". Cancelacion " + cancelados + " CFDIs se han colocado en estado Cancelado. Se ha realizado " + notificados + " notificacion(es) con exito");
        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Notificacion de Cancelación", cancelados + " documentos se han colocado en estado CANCELADO. Se ha realizado " + notificados + " notificacion(es) con exito");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "/CFDI/recibidos";
    }

    public void descargarPDFSocio() {
        logger.info(usuario.getUserid() + " descargarPDFSocio - inicia descarga de PDF");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        int id = Integer.valueOf((String) params.get("idCfdi"));
        CfdisRecibidos cr;
        String strNombrePDF;
        String dir = "/work/iqtb/validacionfiles/pdfRecibidos/" + empresa.getRfc() + "/";
        try {
            cr = daoCfdiRecibido.getCfdiByID(id);
            strNombrePDF = cr.getNombreArchivo().replace(" ", "") + ".pdf";
            if (cr.getPdf() == null) {
                logger.info(usuario.getUserid() + ". cfdi con id " + cr.getIdCfdiRecibido() + " no tiene PDF socio comercial asignado. Inicia busqueda en " + dir);
                if (buscarArchivo(dir, cr)) {
                    validarPDFSocio(cr);
                    servletDescarga(cr.getPdf(), strNombrePDF);
                } else {
                    descBitacora = "[CFDI_RECIBIDO - descargarPDFSocio] " + usuario.getUserid() + " No se encontro PDF con nombre " + cr.getNombreArchivo() + " en la ruta " + dir;
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PDF no encontrado", "Si desea puede cargar el archivo PDF con nombre: " + cr.getNombreArchivo() + " desde el botón Nuevo."));
                    logger.info(descBitacora);
                }
            } else {
                if (cr.getStatusPdf() == null) {
                    logger.info(usuario.getUserid() + ". CFDI id " + cr.getIdCfdiRecibido() + " tiene PDF status " + cr.getStatusPdf() + " Iniciara validacion.");
                    validarPDFSocio(cr);
                    servletDescarga(cr.getPdf(), strNombrePDF);
                } else {
                    if (!cr.getStatusPdf()) {
                        logger.warn(usuario.getUserid() + ". CFDI id " + cr.getIdCfdiRecibido() + " tiene PDF status " + cr.getStatusPdf() + " Iniciara descarga servlet.");
                        servletDescarga(cr.getPdf(), strNombrePDF);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "El PDF entregado podria no coinicir con el XML."));
                        descBitacora = "[CFDI_RECIBIDO - descargarPDFSocio] " + usuario.getUserid() + " CFDI id " + cr.getIdCfdiRecibido() + " El PDF entregado podria no coincidir con el XML";
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                        logger.warn(descBitacora);
                    } else {
                        logger.info(usuario.getUserid() + ". CFDI id " + cr.getIdCfdiRecibido() + " tiene PDF status " + cr.getStatusPdf() + " Iniciara descarga servlet.");
                        servletDescarga(cr.getPdf(), strNombrePDF);
                    }
                }
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - descargarPDFSocio] " + usuario.getUserid() + " ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al descargar PDF.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            logger.error(descBitacora);
        }
    }

    public void updateCfdiOrdenCompra() {
        logger.info("Lista de id de cfdis para actualizar y relacionar con el id Orden de compra " + idOrdenCompra);
        int num = 0;
        try {
            for (Integer idCfdi : listaIds) {
                logger.info("idCfdi " + idCfdi);
                cfdiRecibido = null;
                cfdiRecibido = daoCfdiRecibido.getCfdiByID(idCfdi);
                if (cfdiRecibido != null) {
                    cfdiRecibido.setIdOrdenCompra(idOrdenCompra);
                    if (daoCfdiRecibido.actualizarCfdi(cfdiRecibido)) {
                        descBitacora = "[CFDI_RECIBIDO - updateCfdiOrdenCompra] " + usuario.getUserid() + " cfdi id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + ". se ha asignala la orden de compra id " + idOrdenCompra;
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                        logger.info(descBitacora);
                        num++;
                    } else {
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Asignar Orden de Compra", "Ocurrio un error asignando la orden de compra " + ordenCompra.getNumeroOc() + " con el CFDI con Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + "."));
                        descBitacora = "[CFDI_RECIBIDO - updateCfdiOrdenCompra] " + usuario.getUserid() + " ocurrio un error al actualizar el idOrdenCompra " + idOrdenCompra + " del cfdi id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.info(descBitacora);
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Asignar Orden de Compra", "Ocurrio un error en el proceso de asignar una Orden de Compra al CFDI."));
                    descBitacora = "[CFDI_RECIBIDO - updateCfdiOrdenCompra] " + usuario.getUserid() + " Error obteniendo cfdi con id " + idCfdi;
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Asignar Orden de Compra", "Ocurrio un error en el proceso de asignar una Orden de Compra al CFDI."));
            descBitacora = "[CFDI_RECIBIDO - updateCfdiOrdenCompra] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Asignar Orden de Compra", num + " CFDI(s) asignado(s) con su Orden de Compra."));

        listaIds = null;
    }

    public void asignarOrdenCompra() {
        boolean asignarOrden = false;
        if (cfdisSeleccionados.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione al menos un CFDI."));
        } else {
            logger.info(usuario.getUserid() + " Inicia asignarOrdenCompra");
            listaIds = new ArrayList<Integer>();
            for (AuxCfdiRecibido auxCfdi : cfdisSeleccionados) {
                cfdiRecibido = null;
                byte[] XmlBytes;
                InputStream entrada = null;
                String xml = null;
                try {
                    logger.info(usuario.getUserid() + " Obteniendo cfdi con id " + auxCfdi.getIdCfdiRecibido());
                    cfdiRecibido = daoCfdiRecibido.getCfdiByID(auxCfdi.getIdCfdiRecibido());
                    if (cfdiRecibido != null) {
                        if (!cfdiRecibido.getEstado().equals(EstadoCfdiR.NUEVO.name()) && !cfdiRecibido.getEstado().equals(EstadoCfdiR.SIN_VALIDAR.name())) {
                            if (cfdiRecibido.getIdOrdenCompra() == null) {
                                xml = cfdiRecibido.getXmlSat();
                                XmlBytes = xml.getBytes("UTF-8");
                                entrada = new ByteArrayInputStream(XmlBytes);
                                if (procesarAddenda(entrada, cfdiRecibido.getIdSocioComercial())) {
                                    cfdiRecibido.setEstado(EstadoCfdiR.NUEVO.name());
                                    if (daoCfdiRecibido.actualizarCfdi(cfdiRecibido)) {
                                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Asignar Orden de Compra", "CFDI con Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + ", se asignará con la orden de compra " + ordenCompra.getNumeroOc() + "."));
                                        descBitacora = "[CFDI_RECIBIDO - asignarOrdenCompra] " + usuario.getUserid() + " cfdi id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + ". tiene asignada la orden de compra id " + ordenCompra.getIdOrdenCompra() + " numero " + ordenCompra.getNumeroOc() + ". CFDI en estado " + cfdiRecibido.getEstado() + " para la asignacion automatica.";
                                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                                        logger.info(descBitacora);
                                    } else {
                                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Asignar Orden de Compra", "Ocurrio un error asignando la orden de compra " + ordenCompra.getNumeroOc() + " con el CFDI con Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + "."));
                                        descBitacora = "[CFDI_RECIBIDO - asignarOrdenCompra] " + usuario.getUserid() + " ocurrio un error al actualizar cfdi id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + ". para asignalo manualmente a la orden de compra id " + ordenCompra.getIdOrdenCompra() + " numero " + ordenCompra.getNumeroOc() + ".";
                                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                                        logger.info(descBitacora);
                                    }
                                } else {
                                    if (tieneAddenda) {
                                        if (ordenCompra == null) {
                                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Asignar Orden de Compra", "CFDI con Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + ". No se encontró la Orden de Compra. Por favor, asigne manualmente la Orden de Compra correspondiente."));
                                            descBitacora = "[CFDI_RECIBIDO - asignarOrdenCompra] " + usuario.getUserid() + " cfdi id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + ". No se encontro Orden de Compra en base de datos. El usuario podra asignarla manuelmente.";
                                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                                            logger.info(descBitacora);
                                            asignarOrden = true;
                                            logger.info("obtenerOrdenesCompra idSocio " + cfdiRecibido.getIdSocioComercial());
                                            obtenerOrdenesCompra(cfdiRecibido.getIdSocioComercial());
                                            listaIds.add(cfdiRecibido.getIdCfdiRecibido());
                                        }
                                    } else {
                                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Asignar Orden de Compra", "CFDI con Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + ". No cuenta con addenda. Por favor, asigne manualmente la orden de compra correspondiente."));
                                        descBitacora = "[CFDI_RECIBIDO - asignarOrdenCompra] " + usuario.getUserid() + " cfdi id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + ". No cuenta con addenda para asignarlo automaticamente a su orden de compra. El usuario podra asignarla manualmente";
                                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                                        logger.info(descBitacora);
                                        asignarOrden = true;
                                        logger.info("obtenerOrdenesCompra idSocio " + cfdiRecibido.getIdSocioComercial());
                                        obtenerOrdenesCompra(cfdiRecibido.getIdSocioComercial());
                                        listaIds.add(cfdiRecibido.getIdCfdiRecibido());
                                    }
                                }
                            } else {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Asignar Orden de Compra", "CFDI con Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + ", se encuntra asignado a una Orden de Compra."));
                                descBitacora = "[CFDI_RECIBIDO - asignarOrdenCompra] " + usuario.getUserid() + " cfdi id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + ". Ya tiene asignada una orden de compra idOrdenCompra " + cfdiRecibido.getIdOrdenCompra();
                                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                                logger.info(descBitacora);
                            }
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No es posible Asignar Orden de Compra", "CFDI con Serie " + cfdiRecibido.getSerie() + " Folio " + cfdiRecibido.getFolio() + ", su estado es " + cfdiRecibido.getEstado()));
                            logger.info("[CFDI_RECIBIDO - asignarOrdenCompra] " + usuario.getUserid() + " cfdi id " + cfdiRecibido.getIdCfdiRecibido() + " uuid " + cfdiRecibido.getUuid() + ". no es posible asignar orden de compra se encuntra en estado " + cfdiRecibido.getEstado());
                        }
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Asignar Orden de Compra", "Ocurrio un error en el proceso de asignar una Orden de Compra al CFDI."));
                        descBitacora = "[CFDI_RECIBIDO - asignarOrdenCompra] " + usuario.getUserid() + " Error obteniendo cfdi con id " + auxCfdi.getIdCfdiRecibido();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    }
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Asignar Orden de Compra", "Ocurrio un error en el proceso de asignar una Orden de Compra al CFDI."));
                    descBitacora = "[CFDI_RECIBIDO - asignarOrdenCompra] " + usuario.getUserid() + " ERROR " + e.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            }
            RequestContext context = RequestContext.getCurrentInstance();
            context.addCallbackParam("asignarOrden", asignarOrden);
        }
    }

    private boolean procesarAddenda(InputStream xml, int idSocio) {
        boolean respuesta = false;
        ordenCompra = null;
        tieneAddenda = false;
        if (xml != null) {
            logger.debug("No es nulo");
        } else {
            logger.info("XML Es nulo");
        }
        logger.info("Procesando CFDI con id: " + cfdiRecibido.getIdCfdiRecibido());
        SAXBuilder buider = new SAXBuilder();
        try {
            Document document = buider.build(xml);
            //logger.info("Documento: " + document.toString());
            XMLOutputter xMLOutputter = new XMLOutputter(Format.getPrettyFormat());
            //Buscando mediante NS
            Namespace nsiqtb = Namespace.getNamespace("iqtb", "iqtb.com/ContabilidadE/1_1/iqtbAddenda");
            Namespace nscfdi = Namespace.getNamespace("cfdi", "http://www.sat.gob.mx/cfd/3");
            Element nodoAddenda = document.getRootElement().getChild("Addenda", nscfdi);
            if (nodoAddenda != null) {
                logger.info("nodoAddenda: " + xMLOutputter.outputString(nodoAddenda));
                Element infoComercial = nodoAddenda.getChild("InfoComercial", nsiqtb);
                if (infoComercial != null) {
                    tieneAddenda = true;
                    logger.info("InfoComercial: " + xMLOutputter.outputString(infoComercial));
                    logger.info("Validando conta XSD");
                    if (validarXmlvsXsd(xMLOutputter.outputString(infoComercial))) {
                        logger.info("Correcto");
                        logger.info("******Atributos*****");

                        String ordenDeCompra = infoComercial.getAttributeValue("ordenCompra");
                        logger.info("ordenCompra: " + ordenDeCompra);
                        ordenCompra = daoOrdenCompra.obtenerOrden(ordenDeCompra, empresa.getIdEmpresa(), idSocio);
                        logger.info("Obteniendo orden");
                        if (ordenCompra != null) {
                            descBitacora = "[CFDI_RECIBIDO - procesarAddenda] " + usuario.getUserid() + " Se encontro la orden de compra: " + ordenDeCompra + " con el id: " + ordenCompra.getIdOrdenCompra();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                            logger.info(descBitacora);
                            respuesta = true;
                        } else {
                            descBitacora = "[CFDI_RECIBIDO - procesarAddenda] " + usuario.getUserid() + " No se encontro almacenada la orden de compra: " + ordenDeCompra + " idEmpresa " + empresa.getIdEmpresa() + " idSocioComercial " + idSocio;
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                            logger.warn(descBitacora);
                        }
                    } else {
                        descBitacora = "[CFDI_RECIBIDO - procesarAddenda] " + usuario.getUserid() + " Invalido contra el XSD";
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    }
                } else {
                    descBitacora = "[CFDI_RECIBIDO - procesarAddenda] " + usuario.getUserid() + " No se encontro el nodo Addenda/InfoComercial";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } else {
                descBitacora = "[CFDI_RECIBIDO - procesarAddenda] " + usuario.getUserid() + " No se encontro el nodo Addenda";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
            }
        } catch (IOException e) {
            descBitacora = "[CFDI_RECIBIDO - procesarAddenda] " + usuario.getUserid() + " IOException ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (JDOMException e) {
            descBitacora = "[CFDI_RECIBIDO - procesarAddenda] " + usuario.getUserid() + " JDOMException ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception ex) {
            descBitacora = "[CFDI_RECIBIDO - procesarAddenda] " + usuario.getUserid() + " Exception ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        return respuesta;
    }

    private Boolean validarXmlvsXsd(String stringXml) {
//    String YES = "yes";
//    String NO = "no";
//    String CHAR_ENCODING = "UTF8";
        String separadorArchivo = System.getProperty("file.separator");
        SAXParserFactory factory;
        boolean NAME_SPACE_AWARE = true;
        boolean VALIDATING = true;
        String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
        String SCHEMA_LANGUAGE_VAL = "http://www.w3.org/2001/XMLSchema";
        String SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
        Boolean esCorrecto = false;
        try {
            Reader xmlReader;
            Reader xsdReader;
            xmlReader = new StringReader(stringXml);  // FileReader(stringXml);
            xsdReader = new FileReader(new File("/work/iqtb/validacionfiles/recursos/xsd/addendaIqtb.xsd"));
            factory = SAXParserFactory.newInstance();
            // Configure SAXParserFactory to provide parsers that are namespace aware.
            factory.setNamespaceAware(NAME_SPACE_AWARE);
            // Configure SAXParserFactory to provide parsers that are validating. This property
            // must have the value true for any of the property strings defined below to take
            // effect.
            factory.setValidating(VALIDATING);
            SAXParser parser = factory.newSAXParser();
            // Setting the schema language for xml schema validation
            parser.setProperty(SCHEMA_LANGUAGE, SCHEMA_LANGUAGE_VAL);
            // Setting the schema source for xml schema validation
            parser.setProperty(SCHEMA_SOURCE, new InputSource(xsdReader));
            DefaultHandler handler = new CompararXML.XmlDefaultHandler();
            parser.parse(new InputSource(xmlReader), handler);
            esCorrecto = true;
            logger.info("Es correcto");
        } catch (FactoryConfigurationError e) {
            descBitacora = "[CFDI_RECIBIDO - validarXmlvsXsd] " + usuario.getUserid() + " Error al crear la configuracion: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (ParserConfigurationException e) {
            descBitacora = "[CFDI_RECIBIDO - validarXmlvsXsd] " + usuario.getUserid() + " Error al convertir la configuracion: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (FileNotFoundException ex) {
            descBitacora = "[CFDI_RECIBIDO - validarXmlvsXsd] " + usuario.getUserid() + " Error, no se encontro el archivo /work/iqtb/validacionfiles/recursos/xsd/addendaIqtb.xsd, FileNotFoundException: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (SAXException ex) {
            descBitacora = "[CFDI_RECIBIDO - validarXmlvsXsd] " + usuario.getUserid() + " Error, SAXException: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (IOException ex) {
            descBitacora = "[CFDI_RECIBIDO - validarXmlvsXsd] " + usuario.getUserid() + " Error, IOException: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception ex) {
            descBitacora = "[CFDI_RECIBIDO - validarXmlvsXsd] " + usuario.getUserid() + " Error, Exception: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        return esCorrecto;
    }

    private void obtenerOrdenesCompra(int idSocio) {
        logger.info(usuario.getUserid() + " Inicia obtenerOrdenesCompra idSocio " + idSocio);
        listaOrdenCompra = new ArrayList<OrdenesCompra>();
        try {
            itemsOrdenCompra = new ArrayList<SelectItem>();
            selectItem = null;
            listaOrdenCompra = daoOrdenCompra.listaOrdenByHql("select new OrdenesCompra (idOrdenCompra, numeroOc) from OrdenesCompra where idEmpresa = " + empresa.getIdEmpresa() + " and idSocioComercial = " + idSocio);
            intIdSocio = idSocio;
            if (listaOrdenCompra.isEmpty()) {
                logger.info("[CFDI_RECIBIDO - obtenerOrdenesCompra] - No se han encontrado resultados para Ordenes de compra");
            } else {
                for (OrdenesCompra var : listaOrdenCompra) {
                    selectItem = new SelectItem(var.getIdOrdenCompra(), var.getNumeroOc());
                    itemsOrdenCompra.add(selectItem);
                }
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - obtenerOrdenesCompra] " + usuario.getUserid() + " Error obteniendo lista de ordenes de compra. ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void crearOrdenCompra() {
        ordenCompra = new OrdenesCompra();
        crearOrden = true;
    }

    public void cancelarOrdenCompra() {
        crearOrden = false;
    }

    public void saveOrdenCompra() {
        logger.info(usuario.getUserid() + " Inicia saveOrdenCompra");
        try {
            if (ordenCompra.getNumeroOc() != null && !ordenCompra.getNumeroOc().trim().isEmpty()) {
                if (ordenCompra.getTotal() != null && ordenCompra.getTotal().compareTo(BigDecimal.ZERO) > 0) {
                    if (ordenCompra.getRfcVendedor() != null && !ordenCompra.getRfcVendedor().trim().isEmpty()) {
                        if (ordenCompra.getTipoMoneda() != null && !ordenCompra.getTipoMoneda().trim().isEmpty()) {
                            ordenCompra.setTipoMoneda(ordenCompra.getTipoMoneda().toUpperCase());
                        }
                        ordenCompra.setIdEmpresa(empresa.getIdEmpresa());
                        ordenCompra.setIdSocioComercial(intIdSocio);
                        if (daoOrdenCompra.insertOrdenCompra(ordenCompra)) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Orden de Compra", "Orden de Compra " + ordenCompra.getNumeroOc() + " se agrego correctamente."));
                            descBitacora = "[CFDI_RECIBIDO - saveOrdenCompra] " + usuario.getUserid() + " inserto la orden de compra " + ordenCompra.getNumeroOc() + " idEmpresa " + ordenCompra.getIdEmpresa() + " idSocioComercial " + ordenCompra.getIdSocioComercial();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                            logger.info(descBitacora);
                            obtenerOrdenesCompra(ordenCompra.getIdSocioComercial());
                            crearOrden = false;
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al guardar la Orden de Compra " + ordenCompra.getNumeroOc() + "."));
                            descBitacora = "[CFDI_RECIBIDO - saveOrdenCompra] " + usuario.getUserid() + " error al insertar la orden de compra " + ordenCompra.getNumeroOc() + " idEmpresa " + ordenCompra.getIdEmpresa() + " idSocioComercial " + ordenCompra.getIdSocioComercial();
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
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - saveOrdenCompra] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        intIdSocio = 0;
    }

    private boolean validarPDFSocio(CfdisRecibidos cfdi) {
        logger.info(usuario.getUserid() + ". Inicia validarPDFSocio del cfdi id " + cfdi.getIdCfdiRecibido());
        boolean pdfValido = false;
        String strPdf;
        String[] arrayPdf;
        LeerPDF leerPdf = new LeerPDF();
        strPdf = leerPdf.pdftoText(cfdi.getPdf());
        arrayPdf = strPdf.split("\n");
        int a = 0;
        String uuid = null;
        for (String item : arrayPdf) {

            a = item.indexOf("||1.0|");

            if (a != -1) {
                uuid = item.substring(item.indexOf("||1.0|") + 6, item.indexOf("||1.0|") + 6 + 36);
                if (uuid != null) {
                    if (cfdi.getUuid().equals(uuid)) {
                        logger.info(usuario.getUserid() + ". PDF corresponde a este UUID " + cfdi.getUuid() + " uuid encontrado " + uuid);
                        pdfValido = true;
                    } else {
                        logger.info(usuario.getUserid() + ". PDF NO corresponde a este UUID " + cfdi.getUuid() + " uuid encontrado " + uuid);
                    }
                }
            } else {
                logger.error(usuario.getUserid() + ". PDF con nombre " + cfdi.getNombreArchivo() + ".pdf No se encontro la cadena ||1.0|");
            }

        }
        if (pdfValido) {
            logger.info(usuario.getUserid() + ". CFDI con id " + cfdi.getIdCfdiRecibido() + " PDF corresponde al uuid");
        } else {
            logger.info(usuario.getUserid() + ". CFDI con id " + cfdi.getIdCfdiRecibido() + " PDF NO corresponde al uuid");
        }
        try {
            cfdi.setStatusPdf(pdfValido);
            if (daoCfdiRecibido.actualizarCfdi(cfdi)) {
                descBitacora = "[CFDI_RECIBIDO - validarPDFSocio] " + usuario.getUserid() + " modifico correctamente CFDI con id " + cfdi.getIdCfdiRecibido() + " uuid " + cfdi.getUuid() + " statusPDF " + pdfValido;
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(descBitacora);
            } else {
                descBitacora = "[CFDI_RECIBIDO - validarPDFSocio] " + usuario.getUserid() + " Error al modificar CFDI con id " + cfdi.getIdCfdiRecibido() + " uuid " + cfdi.getUuid() + " statusPDF " + pdfValido;
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - validarPDFSocio] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
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
            descBitacora = "[CFDI_RECIBIDO - servletDescarga] " + usuario.getUserid() + " genero/descargo " + strNombreArchivo;
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(descBitacora);
            logger.info(usuario.getUserid() + ". Termina servletDescarga");
        } catch (IOException e) {
            descBitacora = "[CFDI_RECIBIDO - servletDescarga] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
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
            descBitacora = "[CFDI_RECIBIDO - buscarArchivo] " + usuario.getUserid() + " Directorio no existe " + strDir;
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
            logger.warn(descBitacora);
        }
        if (respuesta) {
            FileInputStream fis = null;
            try {
                File f = new File(strDir + strNombreArchivo);
                logger.info(usuario.getUserid() + ". Archivo cargado " + f.getAbsolutePath());
                byte[] b = getBytesFromFile(f);

                cfdi.setPdf(b);
                if (daoCfdiRecibido.actualizarCfdi(cfdi)) {
                    manejorArchivos.eliminar(strDir + strNombreArchivo);
                    descBitacora = "[CFDI_RECIBIDO - buscarArchivo] " + usuario.getUserid() + " modifico el cfdi con id " + cfdi.getIdCfdiRecibido() + " uuid " + cfdi.getUuid() + " inserto pdf " + strNombreArchivo;
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                } else {
                    descBitacora = "[CFDI_RECIBIDO - buscarArchivo] " + usuario.getUserid() + " Error al modificar el cfdi con id " + cfdi.getIdCfdiRecibido() + " uuid " + cfdi.getUuid() + " pdf " + strNombreArchivo;
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } catch (FileNotFoundException ex) {
                descBitacora = "[CFDI_RECIBIDO - buscarArchivo] " + usuario.getUserid() + " FileNotFoundException ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
            } catch (IOException ex) {
                descBitacora = "[CFDI_RECIBIDO - buscarArchivo] " + usuario.getUserid() + " IOException ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.info(descBitacora);
            } catch (Exception ex) {
                descBitacora = "[CFDI_RECIBIDO - buscarArchivo] " + usuario.getUserid() + " ERROR " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.info(descBitacora);
            }
        }
        return respuesta;
    }

    private boolean generarReporte(List<CfdisRecibidos> lista) {
        logger.info("[CFDI_RECIBIDO - generarReporte] " + usuario.getUserid() + " Inicia Generar Reporte Excel");
        boolean reporte = false;
        try {
            String ruta = "/work/iqtb/validacionfiles/reportes/" + empresa.getRfc() + "/" + usuario.getUserid() + "/";
            DateFormat df = new SimpleDateFormat("ddMMyyyy");
            Date today = new Date();
            String reportDate = df.format(today);
            String strNombreReporte = "Reporte_" + reportDate + ".xls";
            File dir = new File(ruta);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Workbook libro = new HSSFWorkbook();
            FileOutputStream archivo = new FileOutputStream(ruta + strNombreReporte);
            logger.info("[CFDI_RECIBIDO - generarReporte] " + usuario.getUserid() + " Archivo creado " + ruta + strNombreReporte);
            Sheet hoja = libro.createSheet();
            int y = 1;
            for (CfdisRecibidos var : lista) {
                if (y == 1) {
                    logger.info("Inicia escritura de cabecera");
                    Row fila = hoja.createRow((int) 0);
                    Cell a = fila.createCell((int) 0);
                    a.setCellValue("Fecha Emisión");
                    Cell b = fila.createCell((int) 1);
                    b.setCellValue("Serie");
                    Cell c = fila.createCell((int) 2);
                    c.setCellValue("Folio");
                    Cell d = fila.createCell((int) 3);
                    d.setCellValue("Fecha Recepción");
                    Cell e = fila.createCell((int) 4);
                    e.setCellValue("RFC Socio Comercial");
                    Cell f = fila.createCell((int) 5);
                    f.setCellValue("Socio Comercial");
                    Cell g = fila.createCell((int) 6);
                    g.setCellValue("UUID");
                    Cell h = fila.createCell((int) 7);
                    h.setCellValue("Total");
                    Cell i = fila.createCell((int) 8);
                    i.setCellValue("Estado");
                    Cell j = fila.createCell((int) 9);
                    j.setCellValue("Warning");
                    Cell k = fila.createCell((int) 10);
                    k.setCellValue("Error");
                    Cell l = fila.createCell((int) 11);
                    l.setCellValue("Número Orden de Compra");
                    Cell m = fila.createCell((int) 12);
                    m.setCellValue("Total Orden de Compra");
                    Cell n = fila.createCell((int) 13);
                    n.setCellValue("Tipo de Moneda");
                    Cell o = fila.createCell((int) 14);
                    o.setCellValue("Entrada Almacén");
                    Cell p = fila.createCell((int) 15);
                    p.setCellValue("Control de Calidad");
                    Cell q = fila.createCell((int) 16);
                    q.setCellValue("Autorización");

                    logger.info("Termina escritura de cabecera");
                }
                Row fila1 = hoja.createRow((int) y);
                Cell a = fila1.createCell((int) 0);
                a.setCellValue(var.getFecha().toString());
                Cell b = fila1.createCell((int) 1);
                b.setCellValue(var.getSerie());
                Cell c = fila1.createCell((int) 2);
                if (var.getFolio() == null) {
                    c.setCellValue("");
                } else {
                    c.setCellValue(var.getFolio());
                }
                Cell d = fila1.createCell((int) 3);
                d.setCellValue(var.getFechaRecepcion().toString());
                Cell e = fila1.createCell((int) 4);
                e.setCellValue(daoSociosComerciales.getRFCSociobyIdSocio(var.getIdSocioComercial()));
                Cell f = fila1.createCell((int) 5);
                f.setCellValue(daoSociosComerciales.getNombreSociobyIdSocio(var.getIdSocioComercial()));
                Cell g = fila1.createCell((int) 6);
                g.setCellValue(var.getUuid());
                Cell h = fila1.createCell((int) 7);
                h.setCellValue(var.getTotal().toString());
                Cell i = fila1.createCell((int) 8);
                i.setCellValue(var.getEstado());
                Cell j = fila1.createCell((int) 9);
                j.setCellValue(var.getTiposWarn());
                Cell k = fila1.createCell((int) 10);
                k.setCellValue(var.getError());
                Cell l = fila1.createCell((int) 11);
                if (var.getIdOrdenCompra() != null) {
                    l.setCellValue(daoOrdenCompra.numOrdenCompraById(var.getIdOrdenCompra()));
                } else {
                    l.setCellValue("");
                }
                Cell m = fila1.createCell((int) 12);
                if (var.getTotalOc() == null) {
                    m.setCellValue("");
                } else {
                    m.setCellValue(var.getTotalOc().toString());
                }
                Cell n = fila1.createCell((int) 13);
                n.setCellValue(var.getTipoMoneda());
                Cell o = fila1.createCell((int) 14);
                o.setCellValue(var.getEntradaAlmacen());
                Cell p = fila1.createCell((int) 15);
                p.setCellValue(var.getControlCalidad());
                Cell q = fila1.createCell((int) 16);
                q.setCellValue(var.getAutorizacion());
                if (y == 50000) {
                    y = 1;
                    hoja = libro.createSheet();
                    logger.info("se ha creado una nueva hoja");
                } else {
                    y++;
                }
            }
            logger.info(usuario.getUserid() + ". Termina escritura de las filas");
            byte[] bytes = new byte[1024];
            int read = 0;
            libro.write(archivo);
            logger.info(usuario.getUserid() + ". Termina de escribir Reporte de excel");
            archivo.close();
            logger.info(usuario.getUserid() + ". Reporte Generado en la ruta " + ruta + strNombreReporte);
            File ficheroXLS = new File(ruta + strNombreReporte);
            FileInputStream fis = new FileInputStream(ficheroXLS);
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setContentType("application/vnd.ms-excel");
            String disposition = "attachment; fileName=" + strNombreReporte;
            response.setHeader("Content-Disposition", disposition);
            ServletOutputStream out = response.getOutputStream();
            while ((read = fis.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            ctx.responseComplete();
            reporte = true;
            descBitacora = "[CFDI_RECIBIDO - generarReporte] " + usuario.getUserid() + " Genero/Descargo Reporte Excel.";
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(descBitacora);
            manejorArchivos.eliminar(ruta);
        } catch (IOException e) {
            descBitacora = "[CFDI_RECIBIDO - generarReporte] " + usuario.getUserid() + " IOException ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception ex) {
            descBitacora = "[CFDI_RECIBIDO - generarReporte] " + usuario.getUserid() + " ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        return reporte;
    }

    private boolean notificarCancelacion(AuxCfdiRecibido cfdi) {
        logger.info(usuario.getUserid() + ". Inicia notificarCancelacion");
        boolean notificado = false;
        socioComercial = new SociosComerciales();
        try {

            configEmail = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.EMAIL_SMTP.name());
            configUser = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.USUARIO_SMTP.name());
            configPass = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PASS_SMTP.name());
            configHost = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.HOST_SMTP.name());
            configPort = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PUERTO_SMTP.name());
            configSSL = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.SSL_SMTP.name());
            logger.info(usuario.getUserid() + ". Configuraciones SMTP cargadas");

            if (configEmail.getValor().trim().isEmpty() || configHost.getValor().trim().isEmpty() || configPort.getValor().trim().isEmpty()) {
                descBitacora = "[CFDI_RECIBIDO - notificarCancelacion] " + usuario.getUserid() + " No es posible enviar el correo electronico, no existe configuracion para el servidor SMTP.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.WARNING.name());
                logger.error(descBitacora);
            } else {
                logger.info(usuario.getUserid() + ". Obteniedo direcciones de correo.");
                emailRecepcion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.EMAIL_RECEPCION.name());
                listaNotificacion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.EMAILS_RECIBEN_NOTIFICACION.name());
                socioComercial = daoSociosComerciales.getSocioComercialByIdEmpresaRFC(empresa.getIdEmpresa(), cfdi.getRfcSocioComercial());
                String destinatario = null;
                if (!emailRecepcion.getValor().trim().isEmpty()) {
                    destinatario = emailRecepcion.getValor();
                    logger.info(usuario.getUserid() + ". Email Recepcion " + emailRecepcion.getValor() + " agregado a destinatario");
                }
                if (!listaNotificacion.getValor().trim().isEmpty()) {
                    destinatario = destinatario + "," + listaNotificacion.getValor();
                    logger.info(usuario.getUserid() + ". lista notificacion " + listaNotificacion.getValor() + " agregado a destinatario");
                }
                if (socioComercial.getEmail() != null && !socioComercial.getEmail().trim().isEmpty()) {
                    destinatario = destinatario + "," + socioComercial.getEmail();
                    logger.info(usuario.getUserid() + ". Email Socio comercial " + socioComercial.getEmail() + " agregado a destinatario");
                }
                if (destinatario == null) {
                    descBitacora = "[CFDI_RECIBIDO - notificarCancelacion] " + usuario.getUserid() + " No existen destinatarios para notificar la cancelacion del documento con id " + cfdi.getIdCfdiRecibido();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                } else {
                    logger.info(usuario.getUserid() + ". Creando email para notificacion de cancelacion");
                    String asunto = "Notificación de Cancelación";
                    String contenido = "<p><strong>Notificación de cancelación del documento.</strong></p>\n"
                            + "<p>Serie: <strong>" + cfdi.getSerie() + "</strong></p>\n"
                            + "<p>Folio: <strong>" + cfdi.getFolio() + "</strong></p>\n"
                            + "<p>UUID: <strong>" + cfdi.getUuid() + "</strong></p>\n"
                            + "<p>Fecha de Emisión: <strong>" + cfdi.getFecha() + "</strong></p>\n"
                            + "<p>Monto total: <strong>" + cfdi.getTotal() + "</strong></p>\n"
                            + "<p>Estado: <strong>" + cfdi.getEstado() + "</strong></p>\n"
                            + "<p>Socio Comercial: <strong>" + cfdi.getRfcSocioComercial() + "</strong></p>\n"
                            + "<p>Empresa: <strong>" + empresa.getRfc() + "</strong></p>\n"
                            + "<p>ERROR: <strong>" + cfdi.getError() + "</strong></p>";
                    boolean ssl = Boolean.valueOf(configSSL.getValor());
                    String pw = "";
                    if (!configPass.getValor().trim().isEmpty()) {
                        pw = decrypt(configPass.getValor());
                    }
                    ConexionSMTP conSMTP = new ConexionSMTP(configHost.getValor(), configPort.getValor(), ssl, true);

                    conSMTP.setUsername(configEmail.getValor());
                    conSMTP.setPassword(pw);
                    conSMTP.getSession();
                    conSMTP.connect();
                    conSMTP.createMessage(destinatario, asunto, contenido, true);
                    String conn;
                    conn = conSMTP.sendMessage();
                    if (conn.trim().charAt(0) == '2') {
                        cfdiNotificado(cfdi.getIdCfdiRecibido());
                        notificado = true;
                        descBitacora = "[CFDI_RECIBIDO - notificarCancelacion] " + usuario.getUserid() + " Notificacion de cancelacion del cfdi con id " + cfdi.getIdCfdiRecibido() + " uuid " + cfdi.getUuid() + " enviada a " + destinatario;
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                        logger.info(descBitacora);
                    } else {
                        descBitacora = "[CFDI_RECIBIDO - notificarCancelacion] " + usuario.getUserid() + " ERROR enviado Notificacion de cancelacion del cfdi con id " + cfdi.getIdCfdiRecibido() + " uuid " + cfdi.getUuid() + " destinatario " + destinatario;
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    }
                }
            }
        } catch (Exception e) {
            descBitacora = "[CFDI_RECIBIDO - notificarCancelacion] " + usuario.getUserid() + " ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        configEmail = null;
        configUser = null;
        configPass = null;
        configHost = null;
        configPort = null;
        configSSL = null;
        return notificado;
    }

    private void cfdiNotificado(Integer idCfdi) {
        cfdiRecibido = null;
        try {
            cfdiRecibido = daoCfdiRecibido.getCfdiByID(idCfdi);
            cfdiRecibido.setNombreArchivo(NotificacionCfdiR.NOTIFICADO.name());
            if (daoCfdiRecibido.actualizarCfdi(cfdiRecibido)) {
                descBitacora = "[CFDI_RECIBIDO - cfdiNotificado] " + usuario.getUserid() + " CFDI con uuid: " + cfdiRecibido.getUuid() + " serie " + cfdiRecibido.getSerie() + " folio " + cfdiRecibido.getFolio() + " estado de notificacion: " + cfdiRecibido.getEstadoNotificacion();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(descBitacora);
            } else {
                descBitacora = "[CFDI_RECIBIDO - cfdiNotificado] " + usuario.getUserid() + " Error al actualizar CFDI con id: " + cfdiRecibido.getIdCfdiRecibido();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
            }
        } catch (Exception ex) {
            descBitacora = "[CFDI_RECIBIDO - cfdiNotificado] " + usuario.getUserid() + " ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

}
