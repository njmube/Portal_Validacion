/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoSociosComerciales;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.SociosComerciales;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
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
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbSociosComerciales implements Serializable {

    private SociosComerciales socioComercial;
    private List<SociosComerciales> listaSocios;
    private List<SociosComerciales> sociosSeleccionados;
    private String empresaSeleccionada;
    private Empresas empresa;
    private Usuarios usuario;
    private SociosComerciales userSocio;
    private final String sessionUsuario;
    private FacesMessage msg;
    private String descBitacora;
    private LazyDataModel<SociosComerciales> socioDataList;
    private Session session;
    private Transaction tx;
    private DaoUsuario daoUsuario;
    private DaoEmpresa daoEmpresa;
    private DaoSociosComerciales daoSocioComercial;
    private static final Logger logger = Logger.getLogger(MbSociosComerciales.class);

    public MbSociosComerciales() {
        socioComercial = new SociosComerciales();
        userSocio = new SociosComerciales();
        sociosSeleccionados = new ArrayList<SociosComerciales>();
        empresa = new Empresas();
        usuario = new Usuarios();
        daoUsuario = new DaoUsuario();
        daoEmpresa = new DaoEmpresa();
        daoSocioComercial = new DaoSociosComerciales();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = daoUsuario.getByUserid(sessionUsuario);
            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
            if (usuario.getIdSocioComercial() != null) {
                logger.info("Cargando userSocioComercial");
                userSocio = daoSocioComercial.getSocioComercialByID(usuario.getIdSocioComercial());
            }
        } catch (Exception e) {
            descBitacora = "[SOCIO_COMERCIAL - MbSociosComerciales] ERROR " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    @PostConstruct
    public void init() {
        socioDataList = new LazyDataModel<SociosComerciales>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<SociosComerciales> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {
                listaSocios = new ArrayList<SociosComerciales>();
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();

                Criteria cr = session.createCriteria(SociosComerciales.class);
                Criteria crCount = session.createCriteria(SociosComerciales.class);
                crCount.setProjection(Projections.rowCount());
                cr.add(Restrictions.eq("idEmpresa", empresa.getIdEmpresa()));
                crCount.add(Restrictions.eq("idEmpresa", empresa.getIdEmpresa()));

                if (sortField != null && !sortField.isEmpty()) {
                    if (sortOrder.equals(SortOrder.ASCENDING)) {
                        cr.addOrder(Order.asc(sortField));
                    } else {
                        cr.addOrder(Order.desc(sortField));
                    }
                } else {
                    cr.addOrder(Order.desc("idSocioComercial"));
                }

                if (!filters.isEmpty()) {
                    Iterator it = filters.keySet().iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        cr.add(Restrictions.like(key, "" + filters.get(key) + "%"));
                        crCount.add(Restrictions.like(key, "" + filters.get(key) + "%"));
                    }
                }
                Integer num = ((Long) crCount.uniqueResult()).intValue();
                this.setRowCount(num);
                cr.setFirstResult(first);
                cr.setMaxResults(pageSize + first);
                listaSocios = cr.list();
                if (session.isOpen()) {
                    session.clear();
                    session.close();
                }
                return listaSocios;
            }
        };
    }

    public void resetSocioComercial() {
        socioComercial = new SociosComerciales();
    }

    public void insertSocioComercial() {
        boolean insert = false;

        try {
            if (socioComercial.getNombre() != null && !socioComercial.getNombre().trim().isEmpty()) {
                if (socioComercial.getRfc() != null && !socioComercial.getRfc().trim().isEmpty()) {
                    if (socioComercial.getCalle() != null && !socioComercial.getCalle().trim().isEmpty()) {
                        if (daoSocioComercial.getSocioComercialByIdEmpresaRFC(empresa.getIdEmpresa(), socioComercial.getRfc()) == null) {
                            socioComercial.setIdEmpresa(empresa.getIdEmpresa());
                            insert = daoSocioComercial.insertSocioComercial(socioComercial);
                            if (insert) {
                                descBitacora = "[SOCIOCOMERCIAL] " + usuario.getUserid() + " registro al socio Comercial " + socioComercial.getRfc() + " Para la empresa " + empresa.getRfc();
                                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "INFO");
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Socio Comercial ha sido Registrado.");
                                logger.info("insertSocioComercial - " + usuario.getUserid() + " registro al socio comercial " + socioComercial.getRfc() + " Para la empresa " + empresa.getRfc());
                            } else {
                                descBitacora = "[SOCIOCOMERCIAL] " + usuario.getUserid() + " Error al registar los datos del socio comercial " + socioComercial.getRfc() + " Para la empresa " + empresa.getRfc();
                                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registar los datos del socio comercial.");
                                logger.error("insertSocioComercial - Error al registar los datos del socio comercial " + socioComercial.getRfc() + " Para la empresa " + empresa.getRfc());
                            }
                        } else {
                            logger.warn("insertSocioComercial - RFC " + socioComercial.getRfc() + " ya existe registrado para la empresa " + empresa.getRfc());
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un socio comercial registrado con este RFC " + socioComercial.getRfc());
                        }
                    } else {
                        logger.warn("insertSocioComercial - Calle de la Empresa es requerido");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                    }
                } else {
                    logger.warn("insertSocioComercial - RFC de la Empresa es requerido");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para RFC.");
                }
            } else {
                logger.warn("insertSocioComercial - Nombre de la Empresa es requerido");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
            }
        } catch (Exception e) {
            descBitacora = "[SOCIOCOMERCIAL] insertSocioComercial - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            logger.error("insertSocioComercial " + usuario.getUserid() + " ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("insert", insert);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updateSocioComercial() {
        boolean update = false;

        try {
            if (socioComercial.getNombre() != null && !socioComercial.getNombre().trim().isEmpty()) {
                if (socioComercial.getCalle() != null && !socioComercial.getCalle().trim().isEmpty()) {
                    update = daoSocioComercial.updateSocioComercial(socioComercial);
                    if (update) {
                        descBitacora = "[SOCIOCOMERCIAL] " + usuario.getUserid() + " modifico al socio Comercial " + socioComercial.getRfc() + " Para la empresa " + empresa.getRfc();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "INFO");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Socio Comercial ha sido modificado.");
                        logger.info("updateSocioComercial - " + usuario.getUserid() + " modifico al socio comercial " + socioComercial.getRfc() + " Para la empresa " + empresa.getRfc());
                    } else {
                        descBitacora = "[SOCIOCOMERCIAL] " + usuario.getUserid() + " Error al modificar los datos del socio comercial " + socioComercial.getRfc() + " Para la empresa " + empresa.getRfc();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del socio comercial.");
                        logger.error("updateSocioComercial - Error al modificar los datos del socio comercial " + socioComercial.getRfc() + " Para la empresa " + empresa.getRfc());
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                    logger.warn("updateSocioComercial - Calle de la Empresa es requerido");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
                logger.warn("updateSocioComercial - Nombre de la Empresa es requerido");
            }
        } catch (Exception e) {
            descBitacora = "[SOCIOCOMERCIAL] updateSocioComercial - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando el socio Comercial " + socioComercial.getRfc());
            logger.error("updateSocioComercial " + usuario.getUserid() + " ERROR: " + e);
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("update", update);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void deleteSociosComerciales() {
        int num = 0;
        for (SociosComerciales sc : sociosSeleccionados) {
            socioComercial = sc;
            try {
                if (daoSocioComercial.deleteSocioComercial(socioComercial)) {
                    num++;
                    descBitacora = usuario.getUserid() + " elimino al socio comercial " + socioComercial.getRfc();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "INFO");
                    logger.info(usuario.getUserid() + " deleteSociosComerciales - Socio comercial" + socioComercial.getRfc() + " ha sido eliminado");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", num + " Socio(s) comercial(es) eliminado(s)");
                } else {
                    descBitacora = usuario.getUserid() + " Error eliminando socio comercial " + socioComercial.getRfc();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al intentar eliminar Socio Comercial " + socioComercial.getRfc());
                    logger.error("Usuario " + usuario.getUserid() + " deleteSociosComerciales - Error al intentar eliminar Socio Comercial id " + socioComercial.getIdSocioComercial() + " rfc " + socioComercial.getRfc());
                }
            } catch (Exception e) {
                descBitacora = usuario.getUserid() + " deleteSociosComerciales ERROR " + e.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al intentar eliminar el socio Comercial id " + socioComercial.getIdSocioComercial() + " rfc " + socioComercial.getRfc());
                logger.error(usuario.getUserid() + " deleteSociosComerciales - ERROR " + e);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String existeSeleccionSocio() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");

        boolean estadoSocio = false;
        if (this.sociosSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un socio comercial.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoSocio = true;
                }
            } else if (sociosSeleccionados.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo un socio comercial.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                for (SociosComerciales socio : sociosSeleccionados) {
                    socioComercial = socio;
                }
                estadoSocio = true;
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoSocio", estadoSocio);

        return "/Socios/sociosComerciales?faces-redirect=true";
    }

    public void updateUserSocio() {
        boolean updateSocio = false;

        try {
            if (userSocio.getNombre() != null && !userSocio.getNombre().trim().isEmpty()) {
                if (userSocio.getRfc() != null && !userSocio.getRfc().trim().isEmpty()) {
                    if (userSocio.getCalle() != null && !userSocio.getCalle().trim().isEmpty()) {
                        updateSocio = daoSocioComercial.updateSocioComercial(userSocio);
                        if (updateSocio) {
                            descBitacora = usuario.getUserid() + " modifico sus datos como socio comercial " + userSocio.getRfc();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "INFO");
                            logger.info("updateUserSocio - " + usuario.getUserid() + ". ha modificado sus datos como socio comercial " + userSocio.getRfc());
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos se han modificado exitosamente.");
                        } else {
                            descBitacora = "Usuario: " + usuario.getUserid() + ". Error modificando sus datos como socio comercial " + userSocio.getRfc();
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                            logger.error("updateUserSocio - " + usuario.getUserid() + ". Error modificado sus datos como socio comercial " + userSocio.getRfc());
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error modificando los datos.");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Calle.");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para RFC.");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Nombre.");
            }
        } catch (Exception e) {
            descBitacora = "updateUserSocio - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            logger.error(usuario.getUserid() + ". updateUserSocio - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("updateSocio", updateSocio);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String reloadSocios() {
        return "/Socios/sociosComerciales?faces-redirect=true";
    }

    public SociosComerciales getSocioComercial() {
        return socioComercial;
    }

    public void setSocioComercial(SociosComerciales socioComercial) {
        this.socioComercial = socioComercial;
    }

    public List<SociosComerciales> getSociosSeleccionados() {
        return sociosSeleccionados;
    }

    public void setSociosSeleccionados(List<SociosComerciales> sociosSeleccionados) {
        this.sociosSeleccionados = sociosSeleccionados;
    }

    public SociosComerciales getUserSocio() {
        return userSocio;
    }

    public void setUserSocio(SociosComerciales userSocio) {
        this.userSocio = userSocio;
    }

    public String getEmpresaSeleccionada() {
        return empresaSeleccionada;
    }

    public LazyDataModel<SociosComerciales> getSocioDataList() {
        return socioDataList;
    }

    public void setSocioDataList(LazyDataModel<SociosComerciales> socioDataList) {
        this.socioDataList = socioDataList;
    }

}
