/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoCfdis;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoSociosComerciales;
import com.iqtb.validacion.dao.DaoSucursales;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.pojo.Cfdis;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.SociosComerciales;
import com.iqtb.validacion.pojo.Sucursales;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.AuxCfdiEmitido;
import com.iqtb.validacion.util.ManejoArchivos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbCfdisEmitidos implements Serializable {

    private static final long serialVersionUID = 1L;
    private AuxCfdiEmitido cfdiEmitido;
    private List<Cfdis> listaCfdi;
    private LazyDataModel<AuxCfdiEmitido> lazyDataCfdis;
    private List<AuxCfdiEmitido> listaCfdiE;
    private AuxCfdiEmitido cfdiE;
//    private List<Cfdis> cfdiSeleccionados;
    private DaoCfdis daoCfdi;
    private boolean reporteTodos;
    private Date fechaMin;
    private Date fechaMax;
    private Integer intIdSocio;
    private List<SelectItem> selectOneItemsSocio;
    private List<SociosComerciales> listaSocios;
    private SociosComerciales socioComercial;
    private SelectItem selectItem;
    private SelectItem[] itemsEstado;
    private DaoSociosComerciales daoSociosComerciales;
    private Sucursales sucursal;
    private DaoSucursales daoSucursales;
    private List<Integer> listaIdSocio;
    private List<Integer> listaidSucursal;
    private Session session;
    private Transaction tx;
    private String query;
    private String queryCount;
    private String filterProperty;
    private String filterValue;
    private Usuarios usuario;
    private Empresas empresa;
    private DaoUsuario daoUsuario;
    private DaoEmpresa daoEmpresa;
    private String empresaSeleccionada;
    private String sessionUsuario;
    private FacesContext facesContext;
    private HttpServletRequest httpServletRequest;
    private String descBitacora;
    private static final Logger logger = Logger.getLogger(MbCfdisEmitidos.class);

    public MbCfdisEmitidos() {
        empresa = new Empresas();
        usuario = new Usuarios();
        cfdiEmitido = new AuxCfdiEmitido();
        listaCfdi = new ArrayList<Cfdis>();
        sucursal = new Sucursales();
        listaSocios = new ArrayList<SociosComerciales>();
        socioComercial = new SociosComerciales();
//        cfdiSeleccionados = new ArrayList<Cfdis>();
        daoUsuario = new DaoUsuario();
        daoEmpresa = new DaoEmpresa();
        daoCfdi = new DaoCfdis();
        daoSociosComerciales = new DaoSociosComerciales();
        daoSucursales = new DaoSucursales();

        facesContext = FacesContext.getCurrentInstance();
        httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = daoUsuario.getByUserid(sessionUsuario);
            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
        } catch (Exception e) {
            descBitacora = "[CFDI_EMITIDO - MbCfdisEmitidos] Error al obtener USUARIO EMPRESA ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        itemsEstado = new SelectItem[11];
        itemsEstado[0] = new SelectItem("", "TODOS");
        itemsEstado[1] = new SelectItem("ALMACENADO", "ALMACENADO");
        itemsEstado[2] = new SelectItem("ERROR_ENVIO", "ERROR_ENVIO");
        itemsEstado[3] = new SelectItem("ADDENDA", "ADDENDA");
        itemsEstado[4] = new SelectItem("CANCELADO", "CANCELADO");
        itemsEstado[5] = new SelectItem("ENVIANDO", "ENVIANDO");
        itemsEstado[6] = new SelectItem("ENVIADO", "ENVIADO");
        itemsEstado[7] = new SelectItem("RECIBIDO", "RECIBIDO");
        itemsEstado[8] = new SelectItem("RECHAZADO", "RECHAZADO");
        itemsEstado[9] = new SelectItem("ERROR_PROCESO", "ERROR_PROCESO");
        itemsEstado[10] = new SelectItem("POR_ENVIAR", "POR_ENVIAR");
    }

    @PostConstruct
    public void init() {
        lazyDataCfdis = new LazyDataModel<AuxCfdiEmitido>() {
            @Override
            public List<AuxCfdiEmitido> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                listaCfdiE = new ArrayList<AuxCfdiEmitido>();
                listaCfdi = new ArrayList<Cfdis>();
                try {
                    queryCount = "select count(idCfdis) ";
                    query = "from Cfdis c, Sucursales s where c.idSucursal = s.idSucursal and s.idEmpresa = " + empresa.getIdEmpresa();
                    if (!filters.isEmpty()) {
                        Iterator it = filters.keySet().iterator();
                        while (it.hasNext()) {
                            filterProperty = (String) it.next();
                            filterValue = filters.get(filterProperty);
                            if (filterProperty.equals("folio")) {
                                query = query + " and c." + filterProperty + " = " + filterValue;
                            }
                            if (filterProperty.equals("serie") || filterProperty.equals("estado")) {
                                query = query + " and c." + filterProperty + " = '" + filterValue + "'";
                            }
                            if (filterProperty.equals("fecha") || filterProperty.equals("rfcEmisor") || filterProperty.equals("rfcReceptor")) {
                                query = query + " and c." + filterProperty + " like '" + filterValue + "%'";
                            }
                            if (filterProperty.equals("rfcSocioComercial")) {
                                listaIdSocio = daoSociosComerciales.filtroSocioComercialByRFC(empresa.getIdEmpresa(), filterValue);
                                if (listaIdSocio != null) {
                                    boolean isFirst = true;
                                    for (Integer idSocio : listaIdSocio) {
                                        if (isFirst) {
                                            query = query + " and (c.idSocioComercial = " + idSocio;
                                            isFirst = false;
                                        } else {
                                            query = query + " or c.idSocioComercial = " + idSocio;
                                        }
                                    }
                                    query = query + ")";
                                }
                            }
                            if (filterProperty.equals("nombreSucursal")) {
                                listaidSucursal = daoSucursales.listaIdSucursalByNombre(empresa.getIdEmpresa(), filterValue);
                                if (listaidSucursal != null) {
                                    boolean isFirst = true;
                                    for (Integer idSucursal : listaidSucursal) {
                                        if (isFirst) {
                                            query = query + " and (c.idSucursal = " + idSucursal;
                                        } else {
                                            query = query + " or c.idSucursal = " + idSucursal;
                                        }
                                    }
                                    query = query + ")";
                                }
                            }
                        }
                    }
                    if (sortField != null && !sortField.isEmpty()) {
                        if (sortOrder.equals(SortOrder.ASCENDING)) {
                            query = query + " ORDER BY c." + sortField + " ASC";
                        } else {
                            query = query + " ORDER BY c." + sortField + " DESC";
                        }
                    } else {
                        query = query + " ORDER BY c.fecha DESC";
                    }
                    queryCount = queryCount + query;
                    Query result = session.createQuery("select c " + query);
                    result.setFirstResult(first);
                    result.setMaxResults(pageSize + first);
                    this.setRowCount(((Long) session.createQuery(queryCount).uniqueResult()).intValue());
                    listaCfdi = result.list();
                    tx.commit();
                    if (listaCfdi.isEmpty()) {
                        logger.info("No existen CFDI Emitidos para mostar");
                    } else {
                        for (Cfdis cfdi : listaCfdi) {
                            cfdiE = new AuxCfdiEmitido();
                            cfdiE.setIdCfdis(cfdi.getIdCfdis());
                            cfdiE.setIdSucursal(cfdi.getIdSucursal());
                            cfdiE.setIdSocioComercial(cfdi.getIdSocioComercial());
                            cfdiE.setSerie(cfdi.getSerie());
                            cfdiE.setFolio(cfdi.getFolio());
                            cfdiE.setUuid(cfdi.getUuid());
                            cfdiE.setFecha(cfdi.getFecha());
                            cfdiE.setNumeroAprobacion(cfdi.getNumeroAprobacion());
                            cfdiE.setAnioAprobacion(cfdi.getAnioAprobacion());
                            cfdiE.setSubtotal(cfdi.getSubtotal());
                            cfdiE.setDescuento(cfdi.getDescuento());
                            cfdiE.setTotal(cfdi.getTotal());
                            cfdiE.setTipoMoneda(cfdi.getTipoMoneda());
                            cfdiE.setTipoCambio(cfdi.getTipoCambio());
                            cfdiE.setIva(cfdi.getIva());
                            cfdiE.setEstadoFiscal(cfdi.getEstadoFiscal());
                            cfdiE.setEstado(cfdi.getEstado());
                            cfdiE.setFechaCancelacion(cfdi.getFechaCancelacion());
                            cfdiE.setFechaModificacion(cfdi.getFechaModificacion());
                            cfdiE.setEstadoImpresion(cfdi.getEstadoImpresion());
                            cfdiE.setPedimento(cfdi.getPedimento());
                            cfdiE.setFechaPedimento(cfdi.getFechaPedimento());
                            cfdiE.setAduana(cfdi.getAduana());
                            cfdiE.setGenerationDate(cfdi.getGenerationDate());
                            cfdiE.setTicket(cfdi.getTicket());
                            cfdiE.setEstadoContable(cfdi.getEstadoContable());
                            cfdiE.setFechaVencimiento(cfdi.getFechaVencimiento());
                            cfdiE.setRfcEmisor(cfdi.getRfcEmisor());
                            cfdiE.setRfcReceptor(cfdi.getRfcReceptor());
                            cfdiE.setNombreSocioComercial(daoSociosComerciales.getNombreSociobyIdSocio(cfdi.getIdSocioComercial()));
                            cfdiE.setNombreSucursal(daoSucursales.nombreSucursalByIdSucursal(cfdi.getIdSucursal()));
                            listaCfdiE.add(cfdiE);
                        }
                    }
                } catch (HibernateException he) {
                    descBitacora = "[CFDI_EMITIDO - init] HibernateException ERROR: " + he.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                    tx.rollback();
                } catch (Exception ex) {
                    descBitacora = "[CFDI_EMITIDO - init] ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                } finally {
                    if (session.isOpen()) {
                        session.clear();
                        session.close();
                    }
                }
                return listaCfdiE;
            }
        };
    }

//    public void verCFDI() {
//        facesContext = FacesContext.getCurrentInstance();
//        Map params = facesContext.getExternalContext().getRequestParameterMap();
//
//        int id = Integer.valueOf((String) params.get("idCfdi"));
//        boolean estadoCFDI = false;
//        try {
//            cfdiEmitido = daoCfdi.getCfdiById(id);
//            estadoCFDI = true;
//        } catch (Exception e) {
//            logger.error("verCFDI ERROR " + e);
//        }
//
//        RequestContext context = RequestContext.getCurrentInstance();
//        context.addCallbackParam("estadoCFDI", estadoCFDI);
//    }
    public void obtenerRfcSocios() {
        logger.info("Inicia obtenerRfcSocios");
        try {
            selectOneItemsSocio = null;
            selectOneItemsSocio = new ArrayList<SelectItem>();
            listaSocios = null;
            selectItem = null;
            listaSocios = daoSociosComerciales.listaSociosHQL("select new SociosComerciales(idSocioComercial, rfc) from SociosComerciales where idEmpresa = " + empresa.getIdEmpresa());
            if (listaSocios.isEmpty()) {
                logger.warn("obtenerRfcSocios - No se han encontrado resultados para Socios Comerciales");
            } else {
                for (SociosComerciales var : listaSocios) {
                    selectItem = new SelectItem(var.getIdSocioComercial(), var.getRfc());
                    selectOneItemsSocio.add(selectItem);
                }
            }
        } catch (Exception ex) {
            descBitacora = "[CFDI_EMITIDO - obtenerRfcSocios] ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void descargarReporte() {
        logger.info(sessionUsuario + ". Inicia descargar reporte");
        boolean donwload;
        String hql = "select c from Cfdis c, Sucursales s where c.idSucursal = s.idSucursal and s.idEmpresa = " + empresa.getIdEmpresa();
        try {
            if (!reporteTodos) {
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
            hql = hql + " ORDER BY fecha DESC";
            logger.debug("hql " + hql);
            listaCfdi = daoCfdi.listaCfdisByHql(hql);
            if (!listaCfdi.isEmpty()) {
                donwload = generarReporte(listaCfdi);
                if (donwload) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Generando Reporte."));
                    descBitacora = "[CFDI_EMITIDO - descargarReporte] " + usuario.getUserid() + " Reporte Generado Corectamente. ";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    logger.info(descBitacora);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error generando Reporte."));
                    descBitacora = "[CFDI_EMITIDO - descargarReporte] " + usuario.getUserid() + " Error generando Reporte. ";
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(descBitacora);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Reporte", "No se han encontrado resultados."));
                logger.info(usuario.getUserid() + " .La cosulta no devuleve ningun registro");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error generando Reporte."));
            descBitacora = "[CFDI_EMITIDO - descargarReporte] " + usuario.getUserid() + " ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    private boolean generarReporte(List<Cfdis> lista) {
        logger.info(usuario.getUserid() + ". Inicia Generar Reporte Excel");
        boolean reporte = false;
        try {
            String ruta = "/work/iqtb/validacionfiles/reportes/" + empresa.getRfc() + "/" + usuario.getUserid() + "/";
            DateFormat df = new SimpleDateFormat("ddMMyyyy");
            Date today = new Date();
            String reportDate = df.format(today);
            String strNombreReporte = "EmitidosReporte_" + reportDate + ".xls";
            File dir = new File(ruta);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Workbook libro = new HSSFWorkbook();
            FileOutputStream archivo = new FileOutputStream(ruta + strNombreReporte);
            logger.info(usuario.getUserid() + ". Archivo creado " + ruta + strNombreReporte);
            Sheet hoja = libro.createSheet();
            int aux = 1;
            for (Cfdis var : lista) {
                if (aux == 1) {
                    logger.info("Inicia escritura de cabecera");
                    Row fila = hoja.createRow((int) 0);
                    Cell a = fila.createCell((int) 0);
                    a.setCellValue("Sucursal");
                    Cell b = fila.createCell((int) 1);
                    b.setCellValue("RFC Socio Comercial");
                    Cell c = fila.createCell((int) 2);
                    c.setCellValue("Serie");
                    Cell d = fila.createCell((int) 3);
                    d.setCellValue("Folio");
                    Cell e = fila.createCell((int) 4);
                    e.setCellValue("UUID");
                    Cell f = fila.createCell((int) 5);
                    f.setCellValue("Fecha");
                    Cell g = fila.createCell((int) 6);
                    g.setCellValue("Numero Aprobación");
                    Cell h = fila.createCell((int) 7);
                    h.setCellValue("Año Aprobación");
                    Cell i = fila.createCell((int) 8);
                    i.setCellValue("SubTotal");
                    Cell j = fila.createCell((int) 9);
                    j.setCellValue("Descuento");
                    Cell k = fila.createCell((int) 10);
                    k.setCellValue("Total");
                    Cell l = fila.createCell((int) 11);
                    l.setCellValue("Tipo Moneda");
                    Cell m = fila.createCell((int) 12);
                    m.setCellValue("Tipo Cambio");
                    Cell n = fila.createCell((int) 13);
                    n.setCellValue("IVA");
                    Cell nn = fila.createCell((int) 14);
                    nn.setCellValue("Estado Fiscal");
                    Cell o = fila.createCell((int) 15);
                    o.setCellValue("Estado");
                    Cell p = fila.createCell((int) 16);
                    p.setCellValue("Fecha Cancelación");
                    Cell q = fila.createCell((int) 17);
                    q.setCellValue("Fecha Modificación");
                    Cell r = fila.createCell((int) 18);
                    r.setCellValue("Estado Impresión");
                    Cell s = fila.createCell((int) 19);
                    s.setCellValue("Pedimento");
                    Cell t = fila.createCell((int) 20);
                    t.setCellValue("Fecha Pedimento");
                    Cell u = fila.createCell((int) 21);
                    u.setCellValue("Aduana");
                    Cell v = fila.createCell((int) 22);
                    v.setCellValue("Fecha Generación");
                    Cell w = fila.createCell((int) 23);
                    w.setCellValue("Estado Contable");
                    Cell x = fila.createCell((int) 24);
                    x.setCellValue("Fecha Vencimineto");
                    Cell y = fila.createCell((int) 25);
                    y.setCellValue("RFC Emisor");
                    Cell z = fila.createCell((int) 26);
                    z.setCellValue("RFC Receptor");
                    Cell a1 = fila.createCell((int) 27);
                    a1.setCellValue("Cadena Original");
                    logger.info("Termina escritura de cabecera");
                }
                Row fila1 = hoja.createRow((int) aux);
                sucursal = daoSucursales.getSucursalByHql("select new Sucursales(idSucursal, nombre) from Sucursales where idSucursal = " + var.getIdSucursal());
                Cell a = fila1.createCell((int) 0);
                if (sucursal != null) {
                    a.setCellValue(sucursal.getNombre());
                } else {
                    a.setCellValue(var.getIdSucursal());
                }
                socioComercial = null;
                socioComercial = daoSociosComerciales.getSocioByHql("select new SociosComerciales(idSocioComercial, rfc) from SociosComerciales where idSocioComercial = " + var.getIdSocioComercial());
                Cell b = fila1.createCell((int) 1);
                if (socioComercial != null) {
                    b.setCellValue(socioComercial.getRfc());
                } else {
                    b.setCellValue(var.getIdSocioComercial());
                }
                Cell c = fila1.createCell((int) 2);
                c.setCellValue(var.getSerie());
                Cell d = fila1.createCell((int) 3);
                if (var.getFolio() != null) {
                    d.setCellValue(var.getFolio());
                } else {
                    d.setCellValue("");
                }
                Cell e = fila1.createCell((int) 4);
                e.setCellValue(var.getUuid());
                Cell f = fila1.createCell((int) 5);
                if (var.getFecha() != null) {
                    f.setCellValue(var.getFecha().toString());
                } else {
                    f.setCellValue("");
                }
                Cell g = fila1.createCell((int) 6);
                if (var.getNumeroAprobacion() != null) {
                    g.setCellValue(var.getNumeroAprobacion());
                } else {
                    g.setCellValue("");
                }
                Cell h = fila1.createCell((int) 7);
                if (var.getNumeroAprobacion() != null) {
                    h.setCellValue(var.getAnioAprobacion());
                } else {
                    h.setCellValue("");
                }
                Cell i = fila1.createCell((int) 8);
                if (var.getSubtotal() != null) {
                    i.setCellValue(var.getSubtotal().toString());
                } else {
                    i.setCellValue("");
                }
                Cell j = fila1.createCell((int) 9);
                if (var.getDescuento() != null) {
                    j.setCellValue(var.getDescuento().toString());
                } else {
                    j.setCellValue("");
                }
                Cell k = fila1.createCell((int) 10);
                if (var.getTotal() != null) {
                    k.setCellValue(var.getTotal().toString());
                } else {
                    k.setCellValue("");
                }
                Cell l = fila1.createCell((int) 11);
                l.setCellValue(var.getTipoMoneda());
                Cell m = fila1.createCell((int) 12);
                if (var.getTipoCambio() != null) {
                    m.setCellValue(var.getTipoCambio().toString());
                } else {
                    m.setCellValue("");
                }
                Cell n = fila1.createCell((int) 13);
                if (var.getIva() != null) {
                    n.setCellValue(var.getIva().toString());
                } else {
                    n.setCellValue("");
                }
                Cell nn = fila1.createCell((int) 14);
                nn.setCellValue(var.getEstadoFiscal());
                Cell o = fila1.createCell((int) 15);
                o.setCellValue(var.getEstado());
                Cell p = fila1.createCell((int) 16);
                if (var.getFechaVencimiento() != null) {
                    p.setCellValue(var.getFechaCancelacion().toString());
                } else {
                    p.setCellValue("");
                }
                Cell q = fila1.createCell((int) 17);
                if (var.getFechaVencimiento() != null) {
                    q.setCellValue(var.getFechaModificacion().toString());
                } else {
                    q.setCellValue("");
                }
                q.setCellValue(var.getFechaModificacion().toString());
                Cell r = fila1.createCell((int) 18);
                r.setCellValue(var.getEstadoImpresion());
                Cell s = fila1.createCell((int) 19);
                s.setCellValue(var.getPedimento());
                Cell t = fila1.createCell((int) 20);
                t.setCellValue(var.getFechaPedimento());
                Cell u = fila1.createCell((int) 21);
                u.setCellValue(var.getAduana());
                Cell v = fila1.createCell((int) 22);
                if (var.getGenerationDate() != null) {
                    v.setCellValue(var.getGenerationDate().toString());
                } else {
                    v.setCellValue("");
                }
                Cell w = fila1.createCell((int) 23);
                w.setCellValue(var.getEstadoContable());
                Cell x = fila1.createCell((int) 24);
                if (var.getFechaVencimiento() != null) {
                    x.setCellValue(var.getFechaVencimiento().toString());
                } else {
                    x.setCellValue("");
                }
                Cell y = fila1.createCell((int) 25);
                y.setCellValue(var.getRfcEmisor());
                Cell z = fila1.createCell((int) 26);
                z.setCellValue(var.getRfcReceptor());
                Cell a1 = fila1.createCell((int) 27);
                a1.setCellValue(var.getCadenaOriginal());
                if (aux == 50000) {
                    aux = 1;
                    hoja = libro.createSheet();
                    logger.debug("se ha creado una nueva hoja");
                } else {
                    aux++;
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

            logger.info(usuario.getUserid() + ". Termina Generar Reporte Excel");
            new ManejoArchivos().eliminar(ruta);
        } catch (IOException e) {
            descBitacora = "[CFDI_EMITIDO - generarReporte] " + usuario.getUserid() + " IOException ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception ex) {
            descBitacora = "[CFDI_EMITIDO - generarReporte] " + usuario.getUserid() + " ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        return reporte;
    }

    public LazyDataModel<AuxCfdiEmitido> getLazyDataCfdis() {
        return lazyDataCfdis;
    }

    public void setLazyDataCfdis(LazyDataModel<AuxCfdiEmitido> lazyDataCfdis) {
        this.lazyDataCfdis = lazyDataCfdis;
    }

    public AuxCfdiEmitido getCfdiEmitido() {
        return cfdiEmitido;
    }

    public void setCfdiEmitido(AuxCfdiEmitido cfdiEmitido) {
        this.cfdiEmitido = cfdiEmitido;
    }

    public boolean isReporteTodos() {
        return reporteTodos;
    }

    public void setReporteTodos(boolean reporteTodos) {
        this.reporteTodos = reporteTodos;
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

    public SelectItem[] getItemsEstado() {
        return itemsEstado;
    }

    public void setItemsEstado(SelectItem[] itemsEstado) {
        this.itemsEstado = itemsEstado;
    }

}
