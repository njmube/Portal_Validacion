package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionServicios;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoRoles;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoSociosComerciales;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.dao.DaoUsuarioEmpresa;
import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.RolTipo;
import com.iqtb.validacion.emun.ServSMTP;
import com.iqtb.validacion.emun.Servicio;
import com.iqtb.validacion.emun.UsuarioEstado;
import com.iqtb.validacion.encrypt.Encrypt;
import static com.iqtb.validacion.encrypt.Encrypt.decrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Roles;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.SociosComerciales;
import com.iqtb.validacion.pojo.Usuarios;
import com.iqtb.validacion.pojo.UsuariosHasEmpresas;
import com.iqtb.validacion.pojo.UsuariosHasEmpresasId;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.DateTime;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import com.iqtb.validacion.util.AuxUsuario;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
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
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbUsuario implements Serializable {
    
    private Usuarios usuarioActivo;
    private Usuarios usuario;
    private List<Usuarios> listaUsuarios;
    private List<AuxUsuario> usuariosSeleccionados;
    private Roles rol;
    private List<SelectItem> selectOneItemsRol;
    private List<SelectItem> selectOneItemsEmpresa;
    private Integer idEmpresa;
    private List<SelectItem> selectOneItemsSocio;
    private List<Empresas> empresasSeleccionadas;
    private List<Empresas> empresas;
    private List<String> strEmpresas;
    private List<String> strEmpresasSeleccionadas;
    private DualListModel<String> dualListEmpresas;
    private boolean chkPass;
    private String pass;
    private String newPass;
    private String confirmarPass;
    private Servicios servicio;
    private ConfiguracionesServicios longPass;
    private ConfiguracionesServicios expPass;
    private List<Roles> listaRoles;
    private List<Roles> rolesSeleccionados;
    private boolean rolEmpresa;
    private boolean rolSocio;
    private final String sessionUsuario;
    private FacesMessage msg;
    private String descBitacora;
    private LazyDataModel<AuxUsuario> lazyDataUsuario;
    private Session session;
    private Transaction tx;
    private Integer num;
    private String tipoRol;
    private List<Integer> idRolByTipo;
    private DaoRoles daoRoles;
    private List<AuxUsuario> lista;
    private SelectItem[] tipoRolSelectItems;
    private SelectItem[] estadoSelectItems;
    private boolean crearSocio;
    private SociosComerciales socioComercial;
    private DaoSociosComerciales daoSocioComercial;
    private DaoUsuario daoUsuario;
    private DaoUsuarioEmpresa daoUsuarioEmpresa;
    private DaoEmpresa daoEmpresa;
    private UsuariosHasEmpresasId usuariosEmpresas;
    private UsuariosHasEmpresas usuariosHasEmpresas;
    private Servicios servSMTP;
    private ConfiguracionesServicios configEmail;
    private ConfiguracionesServicios configUser;
    private ConfiguracionesServicios configPass;
    private ConfiguracionesServicios configHost;
    private ConfiguracionesServicios configPort;
    private ConfiguracionesServicios configSSL;
    private AuxUsuario auxUsuario;
    private DaoServicio daoServicio;
    private DaoConfiguracionServicios daoConfigServicio;
    private static final Logger logger = Logger.getLogger(MbUsuario.class);
    
    public MbUsuario() {
        usuarioActivo = new Usuarios();
        usuario = new Usuarios();
        usuariosSeleccionados = new ArrayList<AuxUsuario>();
        rol = new Roles();
        rolesSeleccionados = new ArrayList<Roles>();
        empresas = new ArrayList<Empresas>();
        empresasSeleccionadas = new ArrayList<Empresas>();
        rolEmpresa = false;
        rolSocio = false;
        crearSocio = false;
        socioComercial = new SociosComerciales();
        daoSocioComercial = new DaoSociosComerciales();
        daoUsuario = new DaoUsuario();
        daoUsuarioEmpresa = new DaoUsuarioEmpresa();
        daoRoles = new DaoRoles();
        daoEmpresa = new DaoEmpresa();
        daoServicio = new DaoServicio();
        daoConfigServicio = new DaoConfiguracionServicios();
        
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuarioActivo = daoUsuario.getByUserid(this.sessionUsuario);
            configServicios();
        } catch (Exception ex) {
            logger.error("Error al Obteber Usuario ERROR " + ex);
        }
        tipoRolSelectItems = new SelectItem[4];
        tipoRolSelectItems[0] = new SelectItem("", "TODOS");
        tipoRolSelectItems[1] = new SelectItem(RolTipo.EMPRESA.name(), RolTipo.EMPRESA.name());
        tipoRolSelectItems[2] = new SelectItem(RolTipo.SERVIDOR.name(), RolTipo.SERVIDOR.name());
        tipoRolSelectItems[3] = new SelectItem(RolTipo.SOCIO_COMERCIAL.name(), RolTipo.SOCIO_COMERCIAL.name());
        
        estadoSelectItems = new SelectItem[8];
        estadoSelectItems[0] = new SelectItem("", "TODOS");
        estadoSelectItems[1] = new SelectItem(UsuarioEstado.ACTIVO.name(), UsuarioEstado.ACTIVO.name());
        estadoSelectItems[2] = new SelectItem(UsuarioEstado.AUTENTICADO.name(), UsuarioEstado.AUTENTICADO.name());
        estadoSelectItems[3] = new SelectItem(UsuarioEstado.BAJA.name(), UsuarioEstado.BAJA.name());
        estadoSelectItems[4] = new SelectItem(UsuarioEstado.BLOQUEADO.name(), UsuarioEstado.BLOQUEADO.name());
        estadoSelectItems[5] = new SelectItem(UsuarioEstado.EXPIRADO.name(), UsuarioEstado.EXPIRADO.name());
        estadoSelectItems[6] = new SelectItem(UsuarioEstado.INACTIVO.name(), UsuarioEstado.INACTIVO.name());
        estadoSelectItems[7] = new SelectItem(UsuarioEstado.NUEVO.name(), UsuarioEstado.NUEVO.name());
    }
    
    @PostConstruct
    public void init() {
        
        lazyDataUsuario = new LazyDataModel<AuxUsuario>() {
            
            @Override
            public List<AuxUsuario> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {
                
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                listaUsuarios = null;
                lista = null;
                lista = new ArrayList<AuxUsuario>();
                
                Criteria cr = session.createCriteria(Usuarios.class);
                Criteria crCount = session.createCriteria(Usuarios.class);
                crCount.setProjection(Projections.rowCount());
                
                if (sortField != null && !sortField.isEmpty()) {
                    if (sortOrder.equals(SortOrder.ASCENDING)) {
                        cr.addOrder(Order.asc(sortField));
                    } else {
                        cr.addOrder(Order.desc(sortField));
                    }
                } else {
                    cr.addOrder(Order.desc("lastAction"));
                }
                
                if (!filters.isEmpty()) {
                    Iterator it = filters.keySet().iterator();
                    while (it.hasNext()) {
                        String filterProperty = (String) it.next();
                        String filterValue = filters.get(filterProperty);
                        if (filterProperty.equals("tipoRol")) {
                            Disjunction or = Restrictions.disjunction();
                            idRolByTipo = null;
                            try {
                                idRolByTipo = daoRoles.listaIdRolbyTipoRol(filterValue);
                            } catch (Exception e) {
                                logger.error("Error obteniendo filtro por Tipo de Rol ERROR " + e);
                            }
                            if (idRolByTipo != null && !idRolByTipo.isEmpty()) {
                                for (Integer item : idRolByTipo) {
                                    or.add(Restrictions.eq("idRol", item));
                                }
                            } else {
                                or.add(Restrictions.eq("idRol", 0));
                            }
                            cr.add(or);
                            crCount.add(or);
                        } else {
                            cr.add(Restrictions.like(filterProperty, "" + filterValue + "%"));
                            crCount.add(Restrictions.like(filterProperty, "" + filterValue + "%"));
                        }
                    }
                }
                
                num = ((Long) crCount.uniqueResult()).intValue();
                this.setRowCount(num);
                cr.setFirstResult(first);
                cr.setMaxResults(pageSize + first);
                listaUsuarios = cr.list();
                if (listaUsuarios == null || listaUsuarios.size() <= 0) {
                    logger.info("lazyDataUsuario - No existen Usuarios para mostrar");
                    return lista;
                }
                for (Usuarios usuarios : listaUsuarios) {
                    try {
                        auxUsuario = new AuxUsuario();
                        auxUsuario.setIdUsuario(usuarios.getIdUsuario());
                        auxUsuario.setIdSocioComercial(usuarios.getIdSocioComercial());
                        auxUsuario.setIdRol(usuarios.getIdRol());
                        auxUsuario.setEmail(usuarios.getEmail());
                        auxUsuario.setNombre(usuarios.getNombre());
                        auxUsuario.setApaterno(usuarios.getApaterno());
                        auxUsuario.setAmaterno(usuarios.getAmaterno());
                        auxUsuario.setPasskey(usuarios.getPasskey());
                        auxUsuario.setSalt(usuarios.getSalt());
                        auxUsuario.setDateExpirationPass(usuarios.getDateExpirationPass());
                        auxUsuario.setLastAction(usuarios.getLastAction());
                        auxUsuario.setEstado(usuarios.getEstado());
                        auxUsuario.setUserid(usuarios.getUserid());
                        auxUsuario.setFechaAlta(usuarios.getFechaAlta());
                        auxUsuario.setIntentosFallidos(usuarios.getIntentosFallidos());
                        auxUsuario.setTipoRol(daoRoles.getTipoRolbyIdRol(usuarios.getIdRol()));
                        auxUsuario.setNombreoRol(daoRoles.getNombreRolbyIdRol(usuarios.getIdRol()));
                        lista.add(auxUsuario);
                    } catch (Exception ex) {
                        logger.error("init - ERROR obteniendo tipo/nombre Rol " + ex.getMessage());
                    }
                }
                if (session.isOpen()) {
                    session.clear();
                    session.close();
                }
                return lista;
            }
        };
        
        try {
            empresas = daoEmpresa.getEmpresas();
            strEmpresas = new ArrayList<String>();
            for (Empresas var : empresas) {
                strEmpresas.add(var.getRfc());
            }
            
            strEmpresasSeleccionadas = new ArrayList<String>();
            dualListEmpresas = new DualListModel<String>(strEmpresas, strEmpresasSeleccionadas);
            
        } catch (Exception ex) {
            logger.error("init - ERROR obteniendo lista de empresas " + ex);
        }
    }
    
    public LazyDataModel<AuxUsuario> getLazyDataUsuario() {
        return lazyDataUsuario;
    }
    
    public void setLazyDataUsuario(LazyDataModel<AuxUsuario> lazyDataUsuario) {
        this.lazyDataUsuario = lazyDataUsuario;
    }
    
    public SelectItem[] getTipoRolSelectItems() {
        return tipoRolSelectItems;
    }
    
    public void setTipoRolSelectItems(SelectItem[] tipoRolSelectItems) {
        this.tipoRolSelectItems = tipoRolSelectItems;
    }
    
    public SelectItem[] getEstadoSelectItems() {
        return estadoSelectItems;
    }
    
    public void setEstadoSelectItems(SelectItem[] estadoSelectItems) {
        this.estadoSelectItems = estadoSelectItems;
    }
    
    public Usuarios getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }
    
    public List<AuxUsuario> getUsuariosSeleccionados() {
        return usuariosSeleccionados;
    }
    
    public void setUsuariosSeleccionados(List<AuxUsuario> usuariosSeleccionados) {
        this.usuariosSeleccionados = usuariosSeleccionados;
    }
    
    public List<SelectItem> getSelectOneItemsRol() {
        this.selectOneItemsRol = new ArrayList<SelectItem>();
        List<Roles> roles;
        try {
            roles = daoRoles.getAllRoles();
            for (Roles rol : roles) {
                SelectItem selectItem = new SelectItem(rol.getIdRol(), rol.getNombre());
                this.selectOneItemsRol.add(selectItem);
            }
        } catch (Exception ex) {
            logger.error("getSelectOneItemsRol ERROR " + ex);
        }
        
        return selectOneItemsRol;
    }
    
    public boolean isChkPass() {
        return chkPass;
    }
    
    public void setChkPass(boolean chkPass) {
        this.chkPass = chkPass;
    }
    
    public String getPass() {
        return pass;
    }
    
    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public String getNewPass() {
        return newPass;
    }
    
    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }
    
    public String getConfirmarPass() {
        return confirmarPass;
    }
    
    public void setConfirmarPass(String confirmarPass) {
        this.confirmarPass = confirmarPass;
    }
    
    public List<Empresas> getEmpresas() {
        return empresas;
    }
    
    public List<Empresas> getEmpresasSeleccionadas() {
        return empresasSeleccionadas;
    }
    
    public void setEmpresasSeleccionadas(List<Empresas> empresasSeleccionadas) {
        this.empresasSeleccionadas = empresasSeleccionadas;
    }
    
    public boolean isRolEmpresa() {
        rolEmpresa = false;
        Roles re;
        try {
            re = daoRoles.getRolById(this.usuario.getIdRol());
            if (re != null) {
                if (re.getTipo().equals("EMPRESA")) {
                    this.rolEmpresa = true;
                }
            } else {
                rolEmpresa = false;
            }
        } catch (Exception ex) {
            logger.error("isRolEmpresa - ERROR " + ex);
        }
        return rolEmpresa;
    }
    
    public void setRolEmpresa(boolean rolEmpresa) {
        this.rolEmpresa = rolEmpresa;
    }
    
    public String tipoRolByid(int idRol) {
        try {
            tipoRol = daoRoles.getTipoRolbyIdRol(idRol);
        } catch (Exception e) {
            logger.error("tipoRolByid - ERROR: " + e);
        }
        return tipoRol;
    }
    
    public void limpiar() {
        rolEmpresa = false;
        rolSocio = false;
        idEmpresa = null;
        crearSocio = false;
        usuario = new Usuarios();
        try {
            empresas = daoEmpresa.getEmpresas();
            strEmpresas = new ArrayList<String>();
            for (Empresas var : empresas) {
                strEmpresas.add(var.getRfc());
            }
            
            strEmpresasSeleccionadas = new ArrayList<String>();
            dualListEmpresas = new DualListModel<String>(strEmpresas, strEmpresasSeleccionadas);
            
        } catch (Exception ex) {
            logger.error("limpiar - ERROR obteniendo lista de empresas " + ex);
        }
    }
    
    public List<String> getStrEmpresas() {
        return strEmpresas;
    }
    
    public void setStrEmpresas(List<String> strEmpresas) {
        this.strEmpresas = strEmpresas;
    }
    
    public DualListModel<String> getDualListEmpresas() {
        return dualListEmpresas;
    }
    
    public void setDualListEmpresas(DualListModel<String> dualListEmpresas) {
        this.dualListEmpresas = dualListEmpresas;
    }
    
    public List<Roles> getListaRoles() {
        try {
            this.listaRoles = daoRoles.getAllRoles();
        } catch (Exception e) {
            logger.error("getListaRoles - ERROR: " + e);
        }
        return listaRoles;
    }
    
    public void setListaRoles(List<Roles> listaRoles) {
        this.listaRoles = listaRoles;
    }
    
    public List<Roles> getRolesSeleccionados() {
        return rolesSeleccionados;
    }
    
    public void setRolesSeleccionados(List<Roles> rolesSeleccionados) {
        this.rolesSeleccionados = rolesSeleccionados;
    }
    
    public List<SelectItem> getSelectOneItemsSocio() {
        selectOneItemsSocio = onEmpresaChange();
        return selectOneItemsSocio;
    }
    
    public void setSelectOneItemsSocio(List<SelectItem> selectOneItemsSocio) {
        this.selectOneItemsSocio = selectOneItemsSocio;
    }
    
    public List<SelectItem> getSelectOneItemsEmpresa() {
        selectOneItemsEmpresa = new ArrayList<SelectItem>();
        List<Empresas> empresas;
        try {
            empresas = daoEmpresa.getEmpresas();
            for (Empresas item : empresas) {
                SelectItem selectItem = new SelectItem(item.getIdEmpresa(), item.getRfc());
                selectOneItemsEmpresa.add(selectItem);
            }
        } catch (Exception e) {
            logger.error("getSelectOneItemsEmpresa ERROR " + e);
        }
        return selectOneItemsEmpresa;
    }
    
    public Integer getIdEmpresa() {
        return idEmpresa;
    }
    
    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
    
    public void setSelectOneItemsEmpresa(List<SelectItem> selectOneItemsEmpresa) {
        this.selectOneItemsEmpresa = selectOneItemsEmpresa;
    }
    
    public boolean isRolSocio() {
        return rolSocio;
    }
    
    public boolean isCrearSocio() {
        return crearSocio;
    }
    
    public void setCrearSocio(boolean crearSocio) {
        this.crearSocio = crearSocio;
    }
    
    public SociosComerciales getSocioComercial() {
        return socioComercial;
    }
    
    public void setSocioComercial(SociosComerciales socioComercial) {
        this.socioComercial = socioComercial;
    }
    
    public void updateUsuario() {
        boolean updateUsuario = false;
        int nPass;
        int diasExp = Integer.parseInt(this.expPass.getValor());
        Date test = new Date();
        Date dateExp = DateTime.sumarRestarDiasFecha(test, diasExp);
        Usuarios u;
        empresasSeleccionadas = new ArrayList<Empresas>();
        if (dualListEmpresas.getTarget().size() <= 0) {
            logger.warn("updateUsuario - dualListEmpresasTarget se encuentra vacia");
        } else {
            for (String item : dualListEmpresas.getTarget()) {
                logger.debug("EMPRESA SELECCIONADA " + item);
                for (Empresas emp : empresas) {
                    logger.debug("EMPRESAS " + emp.getRfc());
                    if (item.equals(emp.getRfc())) {
                        empresasSeleccionadas.add(emp);
                        logger.info("updateUsuario - Empresa " + emp.getRfc() + " agredada a empresas selecionadas");
                    }
                }
            }
        }
        try {
            
            if (usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
                if (this.usuario.getApaterno() != null && !this.usuario.getApaterno().isEmpty()) {
                    if (this.usuario.getEmail() != null && !this.usuario.getEmail().isEmpty()) {
                        u = daoUsuario.getUsuarioByEmail(usuario.getEmail());
                        if (!this.usuario.getEstado().equals("")) {
                            if (chkPass) {
                                logger.info("Cambiar contraseña " + chkPass);
                                if (this.newPass != null && !this.newPass.isEmpty()) {
                                    nPass = Integer.parseInt(longPass.getValor());
                                    if (this.newPass.length() >= nPass) {
                                        if (this.newPass.equals(this.confirmarPass)) {
                                            rol = daoRoles.getRolById(usuario.getIdRol());
                                            if (rol.getTipo().equals("EMPRESA")) {
                                                logger.info("updateUsuario - Tipo de rol " + rol.getTipo());
                                                if (empresasSeleccionadas.size() <= 0) {
                                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                                    logger.warn("updateUsuario - Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                                } else {
                                                    if (u == null || u.getUserid().equals(usuario.getUserid())) {
                                                        usuario.setSalt(Encrypt.getSALT(20));
                                                        usuario.setPasskey(Encrypt.getSHA512(this.newPass + this.usuario.getSalt()));
                                                        usuario.setDateExpirationPass(dateExp);
                                                        updateUsuario = daoUsuario.updateUsuario(this.usuario);
                                                        if (updateUsuario) {
                                                            logger.info("Actualizo los datos de la tabla usuarios");
                                                            if (daoUsuarioEmpresa.deleteByIdUsuario(usuario.getIdUsuario())) {
                                                                logger.info("updateUsuario - Se han eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
                                                            } else {
                                                                logger.error("updateUsuario - Error eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
                                                            }
                                                            usuariosEmpresas = new UsuariosHasEmpresasId();
                                                            usuariosHasEmpresas = new UsuariosHasEmpresas();
                                                            for (Empresas emp : empresasSeleccionadas) {
                                                                usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                                usuariosEmpresas.setIdEmpresa(emp.getIdEmpresa());
                                                                usuariosHasEmpresas.setId(usuariosEmpresas);
                                                                if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                                    logger.info("updateUsuario - " + usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                                } else {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                                    logger.error("updateUsuario - Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                                }
                                                            }
                                                            descBitacora = "[USUARIOS] Usuario: " + usuarioActivo.getUserid() + " modifico la informacion del usuario " + usuario.getUserid() + ".";
                                                            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "INFO");
                                                            logger.info("mensaje de info");
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                                            logger.info(usuarioActivo.getUserid() + " updateUsuario - Se han modificado los datos del Usuario " + usuario.getUserid());
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
                                                            logger.error("updateUsuario - Error al modificar los datos");
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "La direccion de correo ya se encuentra registrada.");
                                                        logger.info("la direccion de correo electronico ya existe registrada");
                                                    }
                                                }
                                            } else {
                                                if (rol.getTipo().equals("SOCIO_COMERCIAL")) {
                                                    logger.info("insertUsuario - Tipo de rol " + rol.getTipo());
                                                    if (idEmpresa != null && idEmpresa > 0) {
                                                        if (usuario.getIdSocioComercial() != 0) {
                                                            if (u == null || u.getUserid().equals(usuario.getUserid())) {
                                                                usuario.setSalt(Encrypt.getSALT(20));
                                                                usuario.setPasskey(Encrypt.getSHA512(newPass + usuario.getSalt()));
                                                                usuario.setFechaAlta(getTimestamp());
                                                                usuario.setDateExpirationPass(dateExp);
                                                                updateUsuario = daoUsuario.updateUsuario(usuario);
                                                                if (updateUsuario) {
                                                                    if (daoUsuarioEmpresa.deleteByIdUsuario(usuario.getIdUsuario())) {
                                                                        logger.info("updateUsuario - Se han eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
                                                                    } else {
                                                                        logger.error("updateUsuario - Error eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
                                                                    }
                                                                    usuariosEmpresas = new UsuariosHasEmpresasId();
                                                                    usuariosHasEmpresas = new UsuariosHasEmpresas();
                                                                    usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                                    usuariosEmpresas.setIdEmpresa(idEmpresa);
                                                                    usuariosHasEmpresas.setId(usuariosEmpresas);
                                                                    if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                                        logger.info(usuarioActivo.getUserid() + " updateUsuario - Se han modificado los datos del Usuario " + usuario.getUserid());
                                                                        descBitacora = "[USUARIOS] Usuario: " + usuarioActivo.getUserid() + " modifico la informacion del usuario " + this.usuario.getUserid() + ".";
                                                                        registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "INFO");
                                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                                                    } else {
                                                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la empresa id " + idEmpresa + " con el usuario " + usuario.getUserid());
                                                                        logger.error("insertUsuario - Error al asignar la empresa id " + idEmpresa + " con el usuario " + usuario.getUserid());
                                                                    }
                                                                }
                                                            } else {
                                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "La direccion de correo ya se encuentra registrada.");
                                                                logger.info("la direccion de correo electronico ya existe registrada");
                                                            }
                                                            
                                                        } else {
                                                            logger.warn("insertUsuario - Usuarios con rol " + rol.getTipo() + " deben tener un socio Comercial");
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un valor para Socio Comercial");
                                                        }
                                                    } else {
                                                        logger.warn("insertUsuario - Usuarios con rol " + rol.getTipo() + " deben tener una Empresa seleccionada");
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un valor para Empresa");
                                                    }
                                                } else {
                                                    if (u == null || u.getUserid().equals(usuario.getUserid())) {
                                                        logger.info("updateUsuario - Tipo de rol " + rol.getTipo());
                                                        this.usuario.setSalt(Encrypt.getSALT(20));
                                                        this.usuario.setPasskey(Encrypt.getSHA512(this.newPass + this.usuario.getSalt()));
                                                        this.usuario.setDateExpirationPass(dateExp);
                                                        updateUsuario = daoUsuario.updateUsuario(this.usuario);
                                                        if (updateUsuario) {
                                                            
                                                            descBitacora = "[USUARIOS] Usuario: " + usuarioActivo.getUserid() + " modifico la informacion del usuario " + usuario.getUserid() + ".";
                                                            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "INFO");
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                                            logger.info(usuarioActivo.getUserid() + " updateUsuario - Se han modificado los datos del Usuario " + usuario.getUserid());
                                                        } else {
                                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del usuario.");
                                                            logger.error("updateUsuario - Error al modificar los datos del usuario");
                                                        }
                                                    } else {
                                                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "La direccion de correo ya se encuentra registrada.");
                                                        logger.info("la direccion de correo electronico ya existe registrada");
                                                    }
                                                }
                                            }
                                        } else {
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
                                            logger.warn("updateUsuario - Las contraseñas no coinciden");
                                        }
                                    } else {
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
                                        logger.warn("updateUsuario - La contraseña debe contener al menos " + nPass + " caracteres.");
                                    }
                                } else {
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nueva Contraseña.");
                                    logger.warn("updateUsuario - Error nueva contraseña campo requerido");
                                }
                            } else {
                                rol = daoRoles.getRolById(usuario.getIdRol());
                                if (rol.getTipo().equals("EMPRESA")) {
                                    logger.info("updateUsuario - Tipo de rol " + rol.getTipo());
                                    if (empresasSeleccionadas.size() <= 0) {
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                        logger.warn("updateUsuario - Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
                                    } else {
                                        if (u == null || u.getUserid().equals(usuario.getUserid())) {
                                            updateUsuario = daoUsuario.updateUsuario(this.usuario);
                                            if (updateUsuario) {
                                                if (daoUsuarioEmpresa.deleteByIdUsuario(usuario.getIdUsuario())) {
                                                    logger.info("updateUsuario - Se han eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
                                                } else {
                                                    logger.error("updateUsuario - Error eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
                                                }
                                                usuariosEmpresas = new UsuariosHasEmpresasId();
                                                usuariosHasEmpresas = new UsuariosHasEmpresas();
                                                for (Empresas emp : empresasSeleccionadas) {
                                                    usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                    usuariosEmpresas.setIdEmpresa(emp.getIdEmpresa());
                                                    usuariosHasEmpresas.setId(usuariosEmpresas);
                                                    if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                        logger.info("updateUsuario - " + usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                    } else {
                                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                        logger.error("updateUsuario - Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                    }
                                                }
                                                descBitacora = "[USUARIOS] Usuario: " + usuarioActivo.getUserid() + " modifico la informacion del usuario " + this.usuario.getUserid() + ".";
                                                registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "INFO");
                                                logger.info("updateUsuario - Se han modificado los datos del Usuario.");
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                            } else {
                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del usuario.");
                                                logger.error("updateUsuario - Error al modificar los datos del usuario");
                                            }
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "La direccion de correo ya se encuentra registrada.");
                                            logger.info("la direccion de correo electronico ya existe registrada");
                                        }
                                    }
                                } else {
                                    if (rol.getTipo().equals("SOCIO_COMERCIAL")) {
                                        logger.info("insertUsuario - Tipo de rol " + rol.getTipo());
                                        if (idEmpresa != null && idEmpresa > 0) {
                                            if (usuario.getIdSocioComercial() != 0) {
                                                if (u == null || u.getUserid().equals(usuario.getUserid())) {
                                                    updateUsuario = daoUsuario.updateUsuario(usuario);
                                                    if (updateUsuario) {
                                                        if (daoUsuarioEmpresa.deleteByIdUsuario(usuario.getIdUsuario())) {
                                                            logger.info("updateUsuario - Se han eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
                                                        } else {
                                                            logger.error("updateUsuario - Error eliminado todos los accesos a empresas del usuario  " + usuario.getUserid());
                                                        }
                                                        usuariosEmpresas = new UsuariosHasEmpresasId();
                                                        usuariosHasEmpresas = new UsuariosHasEmpresas();
                                                        usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                        usuariosEmpresas.setIdEmpresa(idEmpresa);
                                                        usuariosHasEmpresas.setId(usuariosEmpresas);
                                                        if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                            logger.info("updateUsuario - Se han modificado los datos del Usuario.");
                                                            descBitacora = "[USUARIOS] Usuario: " + usuarioActivo.getUserid() + " modifico los datos del usuario " + usuario.getUserid() + ".";
                                                            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "INFO");
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                                        } else {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del  usuario " + usuario.getUserid());
                                                            logger.error("insertUsuario - Error al asignar la empresa id " + idEmpresa + " con el usuario " + usuario.getUserid());
                                                        }
                                                    }
                                                } else {
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "La direccion de correo ya se encuentra registrada.");
                                                    logger.info("la direccion de correo electronico ya existe registrada");
                                                }
                                            } else {
                                                logger.warn("insertUsuario - Usuarios con rol " + rol.getTipo() + " deben tener un socio Comercial");
                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un valor para Socio Comercial");
                                            }
                                        } else {
                                            logger.warn("insertUsuario - Usuarios con rol " + rol.getTipo() + " deben tener una Empresa seleccionada");
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un valor para Empresa");
                                        }
                                    } else {
                                        if (u == null || u.getUserid().equals(usuario.getUserid())) {
                                            logger.info("updateUsuario - Tipo de rol " + rol.getTipo());
                                            usuario.setLastAction(getTimestamp());
                                            updateUsuario = daoUsuario.updateUsuario(this.usuario);
                                            if (updateUsuario) {
                                                descBitacora = "[USUARIOS] Usuario: " + usuarioActivo.getUserid() + " modifico la informacion del usuario " + this.usuario.getUserid() + ".";
                                                registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "INFO");
                                                logger.info("updateUsuario - Se han modificado los datos del Usuario.");
                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se han modificado los datos del Usuario.");
                                            } else {
                                                logger.error("updateUsuario - Error al modificar los datos del usuario");
                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos del usuario.");
                                            }
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "La direccion de correo ya se encuentra registrada.");
                                            logger.info("la direccion de correo electronico ya existe registrada");
                                        }
                                    }
                                }
                            }
                        } else {
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un estado.");
                            logger.warn("updateUsuario - Error debe selleccionar un estado");
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para correo electrónico.");
                        logger.warn("updateUsuario - Error correo electrónico es un campo requerido");
                    }
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Apellido Paterno.");
                    logger.warn("updateUsuario - Error Apellido Paterno es un campo requerido");
                }
            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
                logger.warn("updateUsuario - Error Nombre es un campo requerido");
            }
            
        } catch (NoSuchAlgorithmException ex) {
            descBitacora = "[USUARIO] updateUsuario - Encrypt ERROR: " + ex.getMessage();
            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar usuario.");
            logger.error("updateUsuario - Encrypt ERROR " + ex);
        } catch (Exception e) {
            descBitacora = "[USUARIO] updateUsuario - ERROR: " + e.getMessage();
            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "ERROR");
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar usuario.");
            logger.error("updateUsuario - ERROR: " + e.getMessage());
        }
        newPass = "";
        confirmarPass = "";
        chkPass = false;
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("updateUsuario", updateUsuario);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void insertUsuario() {
        boolean insertUsuario = false;
        int nPass = Integer.parseInt(longPass.getValor());
        empresasSeleccionadas = new ArrayList<Empresas>();
        if (dualListEmpresas.getTarget().size() <= 0) {
            logger.warn(usuarioActivo.getUserid() + ". insertUsuario - daultarget es NULL");
        } else {
            for (String item : dualListEmpresas.getTarget()) {
                logger.debug(usuarioActivo.getUserid() + ". EMPRESA SELECCIONADA " + item);
                for (Empresas emp : empresas) {
                    logger.debug(usuarioActivo.getUserid() + ". EMPRESAS " + emp.getRfc());
                    if (item.equals(emp.getRfc())) {
                        empresasSeleccionadas.add(emp);
                        logger.info(usuarioActivo.getUserid() + ". insertUsuario - Empresa " + emp.getRfc() + " agredada a empresas selecionadas");
                    }
                }
            }
            logger.info(usuarioActivo.getUserid() + ". Empresas a almacenar " + dualListEmpresas.getTarget().toString());
            logger.info(usuarioActivo.getUserid() + ". No. Empresas seleccionadas " + empresasSeleccionadas.size());
        }
        
        try {
            
            if (this.usuario.getNombre() != null && !this.usuario.getNombre().isEmpty()) {
                if (this.usuario.getApaterno() != null && !this.usuario.getApaterno().isEmpty()) {
                    if (this.usuario.getEmail() != null && !this.usuario.getEmail().isEmpty()) {
                        if (daoUsuario.getUsuarioByEmail(this.usuario.getEmail()) == null) {
                            if (this.usuario.getUserid() != null && !this.usuario.getUserid().isEmpty()) {
                                if (daoUsuario.getByUserid(this.usuario.getUserid()) == null) {
                                    if (usuario.getIdRol() != 0) {
                                        if (this.pass != null && !this.pass.trim().isEmpty()) {
                                            if (this.pass.length() >= nPass) {
                                                if (this.pass.equals(this.confirmarPass)) {
                                                    rol = daoRoles.getRolById(usuario.getIdRol());
                                                    if (rol.getTipo().equals("EMPRESA")) {
                                                        logger.info(usuarioActivo.getUserid() + ". insertUsuario - Tipo de rol " + rol.getTipo());
                                                        if (empresasSeleccionadas.size() <= 0) {
                                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, asigne al menos una empresa.");
                                                            logger.warn(usuarioActivo.getUserid() + ". insertUsuario - Usuarios con rol " + rol.getTipo() + " deben tener al menos una Empresa asignada");
//                                                            return;
                                                        } else {
                                                            insertUsuario = registrarUsuario(usuario, pass);
                                                            if (insertUsuario) {
                                                                usuariosEmpresas = new UsuariosHasEmpresasId();
                                                                usuariosHasEmpresas = new UsuariosHasEmpresas();
                                                                for (Empresas emp : empresasSeleccionadas) {
                                                                    logger.debug("dentro del for IdUsuario " + usuario.getIdUsuario());
                                                                    usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                                    usuariosEmpresas.setIdEmpresa(emp.getIdEmpresa());
                                                                    usuariosHasEmpresas.setId(usuariosEmpresas);
                                                                    if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                                        descBitacora = usuarioActivo.getUserid() + ". Usuario " + usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc();
                                                                        registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                                                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                                        logger.info(usuarioActivo.getUserid() + ". Usuario " + usuario.getUserid() + " con acceso a la Empresa " + emp.getRfc());
                                                                    } else {
                                                                        descBitacora = usuarioActivo.getUserid() + ". insertUsuario - Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid();
                                                                        registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                                                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                                        logger.error(usuarioActivo.getUserid() + ". insertUsuario - Error al asignar la empresa " + emp.getRfc() + " con el usuario " + usuario.getUserid());
                                                                    }
                                                                }
                                                                if (!notificarNuevoUsuario(usuario, pass)) {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al enviar la notificación de Acceso al Usuario " + usuario.getUserid() + ".");
                                                                    logger.error(usuarioActivo.getUserid() + ". insertUsuario - Error al enviar la notificacion de Acceso al Usuario " + usuario.getUserid() + " email " + usuario.getEmail());
                                                                }
                                                            } else {
                                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registrar Usuario.");
                                                                logger.error(usuarioActivo.getUserid() + ". insertUsuario - Error al registrar Usuario");
                                                            }
                                                        }
                                                    } else {
                                                        if (rol.getTipo().equals("SOCIO_COMERCIAL")) {
                                                            logger.info(usuarioActivo.getUserid() + ". insertUsuario - Tipo de rol " + rol.getTipo());
                                                            if (idEmpresa != null && idEmpresa > 0) {
                                                                if (usuario.getIdSocioComercial() != 0) {
                                                                    insertUsuario = registrarUsuario(usuario, pass);
                                                                    if (insertUsuario) {
                                                                        usuariosEmpresas = new UsuariosHasEmpresasId();
                                                                        usuariosHasEmpresas = new UsuariosHasEmpresas();
                                                                        usuariosEmpresas.setIdUsuario(usuario.getIdUsuario());
                                                                        usuariosEmpresas.setIdEmpresa(idEmpresa);
                                                                        usuariosHasEmpresas.setId(usuariosEmpresas);
                                                                        if (daoUsuarioEmpresa.insertUsuarioEmpresa(usuariosHasEmpresas)) {
                                                                            descBitacora = usuarioActivo.getUserid() + ". Usuario " + usuario.getUserid() + " con acceso a la Empresa id" + idEmpresa;
                                                                            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                                                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", usuario.getUserid() + " regsitrado");
                                                                            logger.info(usuarioActivo.getUserid() + ". Usuario " + usuario.getUserid() + " registrado");
                                                                        } else {
                                                                            descBitacora = usuarioActivo.getUserid() + ". insertUsuario - Error al asignar la empresa id " + idEmpresa + " con el usuario " + usuario.getUserid();
                                                                            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                                                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registrar al usuario " + usuario.getUserid());
                                                                            logger.error(usuarioActivo.getUserid() + ". insertUsuario - Error al asignar la empresa id " + idEmpresa + " con el usuario " + usuario.getUserid());
                                                                        }
                                                                        if (!notificarNuevoUsuario(usuario, pass)) {
                                                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al enviar la notificación de Acceso al Usuario " + usuario.getUserid() + ".");
                                                                            logger.error(usuarioActivo.getUserid() + ". insertUsuario - Error al enviar la notificacion de Acceso al Usuario " + usuario.getUserid() + " email " + usuario.getEmail());
                                                                        }
                                                                    } else {
                                                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registrar Usuario.");
                                                                        logger.error(usuarioActivo.getUserid() + ". insertUsuario - Error al registrar Usuario");
                                                                    }
                                                                } else {
                                                                    logger.warn("insertUsuario - Usuarios con rol " + rol.getTipo() + " deben tener un socio Comercial");
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un valor para Socio Comercial");
                                                                }
                                                            } else {
                                                                logger.warn("insertUsuario - Usuarios con rol " + rol.getTipo() + " deben tener una Empresa seleccionada");
                                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un valor para Empresa");
                                                            }
                                                        } else {
                                                            logger.info(usuarioActivo.getUserid() + ". insertUsuario - Tipo de rol " + rol.getTipo());
                                                            insertUsuario = registrarUsuario(usuario, pass);
                                                            if (insertUsuario) {
                                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Usuario registrado.");
                                                                if (!notificarNuevoUsuario(usuario, pass)) {
                                                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al enviar la notificación de Acceso al Usuario " + usuario.getUserid() + ".");
                                                                    logger.error(usuarioActivo.getUserid() + ". insertUsuario - Error al enviar la notificacion de Acceso al Usuario " + usuario.getUserid() + " email " + usuario.getEmail());
                                                                }
                                                            } else {
                                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registrar Usuario.");
                                                                logger.error(usuarioActivo.getUserid() + ". insertUsuario - Error al registrar Usuario");
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas contraseñas no coinciden.");
                                                    logger.warn("insertUsuario - Error Las contraseñas contraseñas no coinciden");
                                                }
                                            } else {
                                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
                                                logger.warn("insertUsuario - Error Contraseña debe contener mas caracteres");
                                            }
                                        } else {
                                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Contraseña.");
                                            logger.warn("insertUsuario - Error Contraseña es un campo requerido");
                                        }
                                    } else {
                                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Rol.");
                                        logger.warn("insertUsuario - Por favor, ingrese un valor para Rol");
                                    }
                                } else {
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Identificador de usuario ya ha sido utilizado.");
                                    logger.warn("insertUsuario - Este identificador de usuario ya se ha utilizado");
                                }
                            } else {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Identificador de Usuario.");
                                logger.warn("insertUsuario - Error Identificador de usuario es un campo requerido");
                            }
                        } else {
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Correo electrónico ya ha sido utilizado.");
                            logger.warn("insertUsuario - Este correo electrónico ya se ha utilizado");
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Correo Electrónico.");
                        logger.warn("insertUsuario - Error Email es un campo requerido");
                    }
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Apellido Paterno.");
                    logger.warn("insertUsuario - Error Apellido Paterno es un campo requerido");
                }
            } else {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
                logger.warn("insertUsuario - Error Nombre es un campo requerido");
            }
        } catch (NoSuchAlgorithmException ex) {
            logger.error("insertUsuario - Encrypt ERROR " + ex);
        } catch (Exception e) {
            logger.error("insertUsuario - ERROR insertUsuario");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("validarUsuario", insertUsuario);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void deleteUsuario() {
        boolean deleteUsuario = false;
        for (AuxUsuario impUser : usuariosSeleccionados) {
            usuario.setIdUsuario(impUser.getIdUsuario());
            usuario.setIdRol(impUser.getIdRol());
            usuario.setIdSocioComercial(impUser.getIdSocioComercial());
            usuario.setEmail(impUser.getEmail());
            usuario.setNombre(impUser.getNombre());
            usuario.setApaterno(impUser.getApaterno());
            usuario.setAmaterno(impUser.getAmaterno());
            usuario.setPasskey(impUser.getPasskey());
            usuario.setSalt(impUser.getSalt());
            usuario.setDateExpirationPass(impUser.getDateExpirationPass());
            usuario.setLastAction(impUser.getLastAction());
            usuario.setEstado(impUser.getEstado());
            usuario.setUserid(impUser.getUserid());
            usuario.setFechaAlta(impUser.getFechaAlta());
            usuario.setIntentosFallidos(impUser.getIntentosFallidos());
            try {
                if (!usuario.getEstado().equals("AUTENTICADO")) {
                    deleteUsuario = daoUsuario.deleteUsuario(this.usuario);
                    if (deleteUsuario) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Corrrecto", "Usuario " + usuario.getUserid() + " eliminado.");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        logger.info("deleteUsuario - Usuario Eliminado");
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar usuario.");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        logger.error("deleteUsuario - Error al eliminar usuario " + usuario.getUserid());
                    }
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No es posible eliminar usuarios con sesión activa. Usuario " + usuario.getUserid() + " Estado " + this.usuario.getEstado() + ".");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.warn("deleteUsuario - No es posible eliminar usuarios con sesión activa.");
                }
            } catch (Exception e) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ha ocurrido un error al intentar eliminar el usuario " + usuario.getUserid());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.error("deleteUsuario - ERROR" + e);
            }
        }
    }
    
    public List<SelectItem> onEmpresaChange() {
        selectOneItemsSocio = new ArrayList<SelectItem>();
        if (idEmpresa != null && idEmpresa > 0) {
            try {
                List<SociosComerciales> socios;
                socios = new DaoSociosComerciales().getSociosComercialesByIdEmpresa(idEmpresa);
                if (socios.isEmpty()) {
                    logger.warn("onEmpresaChange - No se han encontrado resultados para Socios Comerciales");
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "No se han encontrado resultados para Socios Comerciales");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    for (SociosComerciales var : socios) {
                        SelectItem selectItem = new SelectItem(var.getIdSocioComercial(), var.getRfc());
                        selectOneItemsSocio.add(selectItem);
                    }
                }
            } catch (Exception ex) {
                logger.error("onEmpresaChange ERROR " + ex);
            }
        } else {
            logger.debug("onEmpresaChange - Registadodo usuario Rol SocioComercial idEmpresa " + idEmpresa);
        }
        return selectOneItemsSocio;
    }
    
    public void renderRol() {
        rolEmpresa = false;
        rolSocio = false;
        idEmpresa = 0;
        if (usuario.getIdRol() != 0) {
            try {
                rol = daoRoles.getRolById(usuario.getIdRol());
                if (rol.getTipo().equals("EMPRESA")) {
                    rolEmpresa = true;
                }
                if (rol.getTipo().equals("SOCIO_COMERCIAL")) {
                    rolSocio = true;
                }
            } catch (Exception ex) {
                logger.error("renderRol ERROR " + ex);
            }
        }
    }
    
    public String existeSeleccionUsuario() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();
        
        String parametro = (String) params.get("nombreParametro");
        rolSocio = false;
        rolEmpresa = false;
        boolean estadoUsuario = false;
        if (this.usuariosSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un usuario.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoUsuario = true;
                }
            } else if (usuariosSeleccionados.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo un usuario.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                try {
                    rol = daoRoles.getRolById(usuariosSeleccionados.get(0).getIdRol());
                    if (rol.getTipo().equals("SOCIO_COMERCIAL")) {
                        List<Empresas> listEmpresas;
                        listEmpresas = daoEmpresa.getEmpresaById(usuariosSeleccionados.get(0).getIdUsuario());
                        idEmpresa = listEmpresas.get(0).getIdEmpresa();
                        onEmpresaChange();
                        rolSocio = true;
                        estadoUsuario = true;
                    } else {
                        logger.info("EMPRESA/SERVIDOR");
                        try {
                            empresas = daoEmpresa.getEmpresas();
                            empresasSeleccionadas = daoEmpresa.getEmpresaById(usuariosSeleccionados.get(0).getIdUsuario());
                            rolEmpresa = true;
                            estadoUsuario = true;
                        } catch (Exception ex) {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al cargar las empresas asignadas a este usuario.");
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            logger.error("existeSeleccionUsuario - ERROR" + ex);
                            estadoUsuario = false;
                        }
                        strEmpresasSeleccionadas = new ArrayList<String>();
                        strEmpresas = new ArrayList<String>();
                        for (Empresas var : empresas) {
                            strEmpresas.add(var.getRfc());
                        }
                        for (Empresas var : empresasSeleccionadas) {
                            strEmpresasSeleccionadas.add(var.getRfc());
                            strEmpresas.remove(var.getRfc());
                        }
                        empresasSeleccionadas = new ArrayList<Empresas>();
                        dualListEmpresas = new DualListModel<String>(strEmpresas, strEmpresasSeleccionadas);
                    }
                    for (AuxUsuario impUser : usuariosSeleccionados) {
                        usuario.setIdUsuario(impUser.getIdUsuario());
                        usuario.setIdRol(impUser.getIdRol());
                        usuario.setIdSocioComercial(impUser.getIdSocioComercial());
                        usuario.setEmail(impUser.getEmail());
                        usuario.setNombre(impUser.getNombre());
                        usuario.setApaterno(impUser.getApaterno());
                        usuario.setAmaterno(impUser.getAmaterno());
                        usuario.setPasskey(impUser.getPasskey());
                        usuario.setSalt(impUser.getSalt());
                        usuario.setDateExpirationPass(impUser.getDateExpirationPass());
                        usuario.setLastAction(impUser.getLastAction());
                        usuario.setEstado(impUser.getEstado());
                        usuario.setUserid(impUser.getUserid());
                        usuario.setFechaAlta(impUser.getFechaAlta());
                        usuario.setIntentosFallidos(impUser.getIntentosFallidos());
                    }
                } catch (Exception ex) {
                    logger.error("existeSeleccionUsuario Error al obtener rol ERROR " + ex);
                }
                
            }
            
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoUsuario", estadoUsuario);
        
        return "/Usuario/usuarios?faces-redirect=true";
    }
    
    public String btnModificarUsuario() {
        rolSocio = false;
        rolEmpresa = false;
        boolean estadoUsuario = false;
        if (this.usuariosSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un usuario.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (usuariosSeleccionados.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo un usuario.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                if (usuariosSeleccionados.get(0).getEstado().equals(UsuarioEstado.AUTENTICADO.name())) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "No es posible modificar los datos de un Usuario autenticado.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    try {
                        rol = daoRoles.getRolById(usuariosSeleccionados.get(0).getIdRol());
                        if (rol.getTipo().equals("SOCIO_COMERCIAL")) {
                            List<Empresas> listEmpresas;
                            listEmpresas = daoEmpresa.getEmpresaById(usuariosSeleccionados.get(0).getIdUsuario());
                            idEmpresa = listEmpresas.get(0).getIdEmpresa();
                            onEmpresaChange();
                            rolSocio = true;
                            estadoUsuario = true;
                        } else {
                            logger.info("EMPRESA/SERVIDOR");
                            try {
                                empresas = daoEmpresa.getEmpresas();
                                empresasSeleccionadas = daoEmpresa.getEmpresaById(usuariosSeleccionados.get(0).getIdUsuario());
                                rolEmpresa = true;
                                estadoUsuario = true;
                            } catch (Exception ex) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al cargar las empresas asignadas a este usuario.");
                                FacesContext.getCurrentInstance().addMessage(null, msg);
                                logger.error("existeSeleccionUsuario - ERROR" + ex);
                                estadoUsuario = false;
                            }
                            strEmpresasSeleccionadas = new ArrayList<String>();
                            strEmpresas = new ArrayList<String>();
                            for (Empresas var : empresas) {
                                strEmpresas.add(var.getRfc());
                            }
                            for (Empresas var : empresasSeleccionadas) {
                                strEmpresasSeleccionadas.add(var.getRfc());
                                strEmpresas.remove(var.getRfc());
                            }
                            empresasSeleccionadas = new ArrayList<Empresas>();
                            dualListEmpresas = new DualListModel<String>(strEmpresas, strEmpresasSeleccionadas);
                        }
                        for (AuxUsuario impUser : usuariosSeleccionados) {
                            usuario.setIdUsuario(impUser.getIdUsuario());
                            usuario.setIdRol(impUser.getIdRol());
                            usuario.setIdSocioComercial(impUser.getIdSocioComercial());
                            usuario.setEmail(impUser.getEmail());
                            usuario.setNombre(impUser.getNombre());
                            usuario.setApaterno(impUser.getApaterno());
                            usuario.setAmaterno(impUser.getAmaterno());
                            usuario.setPasskey(impUser.getPasskey());
                            usuario.setSalt(impUser.getSalt());
                            usuario.setDateExpirationPass(impUser.getDateExpirationPass());
                            usuario.setLastAction(impUser.getLastAction());
                            usuario.setEstado(impUser.getEstado());
                            usuario.setUserid(impUser.getUserid());
                            usuario.setFechaAlta(impUser.getFechaAlta());
                            usuario.setIntentosFallidos(impUser.getIntentosFallidos());
                        }
                    } catch (Exception ex) {
                        logger.error("existeSeleccionUsuario Error al obtener rol ERROR " + ex);
                    }
                }
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoUsuario", estadoUsuario);
        
        return "/Usuario/usuarios?faces-redirect=true";
    }
    
    public boolean rolEmpresa(int idRol) {
        try {
            rol = daoRoles.getRolById(idRol);
            if (rol.getTipo().equals("EMPRESA")) {
                return true;
            }
            
        } catch (Exception ex) {
            logger.error("rolEmpresa AQUI - ERROR " + ex);
        }
        return false;
    }
    
    private void configServicios() {
        try {
            servicio = daoServicio.getServicoByNombre("ADMINISTRACION_ACCESO");
            longPass = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicio.getIdServicio(), "LONGITUD_MIN_CONT");
            expPass = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicio.getIdServicio(), "DIAS_EXPIRAR_CONT");
        } catch (Exception e) {
            logger.error("ERROR configServicios " + e);
        }
    }
    
    private boolean registrarUsuario(Usuarios user, String pw) {
        boolean registro = false;
        int diasExp = Integer.parseInt(expPass.getValor());
        Date test = new Date();
        Date dateExp = DateTime.sumarRestarDiasFecha(test, diasExp);
        try {
            user.setEstado(UsuarioEstado.NUEVO.name());
            user.setSalt(Encrypt.getSALT(20));
            user.setPasskey(Encrypt.getSHA512(pw + user.getSalt()));
            user.setLastAction(getTimestamp());
            user.setFechaAlta(getTimestamp());
            user.setDateExpirationPass(dateExp);
            user.setIntentosFallidos(0);
            if (daoUsuario.insertUsuario(user)) {
                registro = true;
                descBitacora = usuarioActivo.getUserid() + ". Usuario " + user.getUserid() + " registrado correctamente";
                registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuarioActivo.getUserid() + ". Usuario " + user.getUserid() + " registrado correctamente");
            } else {
                descBitacora = usuarioActivo.getUserid() + ". Error al registrar Usuario " + user.getUserid();
                registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.info(usuarioActivo.getUserid() + ". Error al registrar Usuario " + user.getUserid());
            }
        } catch (Exception e) {
            descBitacora = usuarioActivo.getUserid() + ". Error al registrar Usuario " + user.getUserid();
            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.info(usuarioActivo.getUserid() + ". Error al registrar Usuario " + user.getUserid());
        }
        return registro;
    }
    
    private boolean notificarNuevoUsuario(Usuarios nuevoUsuario, String pass) {
        logger.info(usuarioActivo.getUserid() + ". Inicia notificarNuevoUsuario");
        boolean notificado = false;
        
        try {
            servSMTP = daoServicio.getServicoByNombre(Servicio.SERVIDOR_SMTP.name());
            logger.info(usuarioActivo.getUserid() + ". servicio " + servSMTP.getNombre() + " cargado");
            configEmail = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.EMAIL_SMTP.name());
            configUser = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.USER_SMTP.name());
            configPass = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.PASS_SMTP.name());
            configHost = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.HOST_SMTP.name());
            configPort = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.PORT_SMTP.name());
            configSSL = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.SSL_SMTP.name());
            logger.info(usuarioActivo.getUserid() + ". Configuraciones del servicio cargadas");
            
            if (configEmail.getValor().trim().isEmpty() || configHost.getValor().trim().isEmpty() || configPort.getValor().trim().isEmpty()) {
                logger.error(usuarioActivo.getUserid() + ". No es posible enviar el correo electronico, no existe configuracion para el servidor SMTP.");
                descBitacora = usuarioActivo.getUserid() + ". No es posible enviar el correo electronico, no existe configuracion para el servidor SMTP.";
                registrarBitacora(usuarioActivo.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.WARNING.name());
            } else {
                logger.info(usuarioActivo.getUserid() + ". Obteniedo direccion de correo.");
                String nombreUsuario = nuevoUsuario.getNombre() + " " + nuevoUsuario.getApaterno() + " " + nuevoUsuario.getAmaterno();
                logger.info(usuarioActivo.getUserid() + ". Creando email para notificacion de datos de acceso");
                String asunto = "Datos de tu cuenta de Acceso para el Portal de Validación";
                String contenido = "<p>Estimad@ <strong>" + nombreUsuario + "</strong></p>\n"
                        + "<p>Se le ha generado una cuenta de acceso al Portal de Validación, con los siguientes datos:</p>\n"
                        + "<p>Usuario: <strong>" + nuevoUsuario.getUserid() + "</strong></p>\n"
                        + "<p>Contraseña: <strong>" + pass + "</strong></p>\n\n"
                        + "<p>Cuando ingrese por primera vez al sistema, por seguridad cambie su contraseña.</p>";
                boolean ssl = Boolean.valueOf(configSSL.getValor());
                String pw = "";
                if (!configPass.getValor().trim().isEmpty()) {
                    pw = decrypt(configPass.getValor());
                    logger.info("desencriptando contraseña");
                }
                ConexionSMTP conSMTP = new ConexionSMTP(configHost.getValor(), configPort.getValor(), ssl, true);
                conSMTP.setUsername(configEmail.getValor());
                conSMTP.setPassword(pw);
                conSMTP.getSession();
                conSMTP.connect();
                conSMTP.createMessage(nuevoUsuario.getEmail(), asunto, contenido, false);
                String conn;
                conn = conSMTP.sendMessage();
                logger.info(usuarioActivo.getUserid() + ". Respuesta del servidor SMTP " + conn);
                if (conn.trim().charAt(0) == '2') {
                    notificado = true;
                    descBitacora = usuarioActivo.getUserid() + " notificarNuevoUsuario Notificacion cuenta nueva del usuario " + nuevoUsuario.getUserid() + " enviada a " + nuevoUsuario.getEmail();
                    registrarBitacora(usuarioActivo.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                    logger.info(usuarioActivo.getUserid() + " notificarNuevoUsuario Notificacion cuenta nueva del usuario " + nuevoUsuario.getUserid() + " enviada a " + nuevoUsuario.getEmail());
                } else {
                    descBitacora = usuarioActivo.getUserid() + ". notificarNuevoUsuario Error enviado Notificacion cuenta nueva del usuario " + nuevoUsuario.getUserid() + " a " + nuevoUsuario.getEmail();
                    registrarBitacora(usuarioActivo.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuarioActivo.getUserid() + " Error enviado Notificacion cuenta nueva del usuario " + nuevoUsuario.getUserid() + " a " + nuevoUsuario.getEmail());
                }
            }
        } catch (Exception e) {
            descBitacora = usuarioActivo.getUserid() + ". notificarNuevoUsuario - ERROR " + e.getMessage();
            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuarioActivo.getUserid() + ". notificarNuevoUsuario - ERROR " + e);
        }
        return notificado;
    }
    
    public void crearSocioComercial() {
        socioComercial = new SociosComerciales();
        crearSocio = true;
    }
    
    public void insertSocioComercial() {
        
        try {
            if (socioComercial.getNombre() != null && !socioComercial.getNombre().trim().isEmpty()) {
                if (socioComercial.getRfc() != null && !socioComercial.getRfc().trim().isEmpty()) {
                    if (socioComercial.getCalle() != null && !socioComercial.getCalle().trim().isEmpty()) {
                        if (idEmpresa != null && idEmpresa > 0) {
                            if (daoSocioComercial.getSocioComercialByIdEmpresaRFC(idEmpresa, socioComercial.getRfc()) == null) {
                                socioComercial.setIdEmpresa(idEmpresa);
                                if (daoSocioComercial.insertSocioComercial(socioComercial)) {
                                    descBitacora = "[Usuarios] " + usuarioActivo.getUserid() + " registro al socio Comercial " + socioComercial.getRfc() + " Para la empresa id " + idEmpresa;
                                    registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "INFO");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Socio Comercial ha sido Registrado.");
                                    logger.info("insertSocioComercial - " + usuarioActivo.getUserid() + " registro al socio comercial " + socioComercial.getRfc() + " Para la empresa id " + idEmpresa);
                                    crearSocio = false;
                                } else {
                                    descBitacora = "[Usuarios] " + usuarioActivo.getUserid() + " Error al registar los datos del socio comercial " + socioComercial.getRfc() + " Para la empresa id " + idEmpresa;
                                    registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "ERROR");
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registar los datos del socio comercial.");
                                    logger.error("insertSocioComercial - Error al registar los datos del socio comercial " + socioComercial.getRfc() + " Para la empresa id " + idEmpresa);
                                }
                            } else {
                                logger.warn("insertSocioComercial - RFC " + socioComercial.getRfc() + " ya existe registrado para la empresa con id " + idEmpresa);
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un socio comercial registrado con este RFC " + socioComercial.getRfc());
                            }
                        } else {
                            logger.warn("insertSocioComercial - Empresa del SocioComercial es requerido");
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Empresa.");
                        }
                    } else {
                        logger.warn("insertSocioComercial - Calle del SocioComercial es requerido");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                    }
                } else {
                    logger.warn("insertSocioComercial - RFC del Socio Comercial es requerido");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para RFC.");
                }
            } else {
                logger.warn("insertSocioComercial - Nombre del Socio Comercial es requerido");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
            }
        } catch (Exception e) {
            descBitacora = "[Usuarios] insertSocioComercial - ERROR: " + e.getMessage();
            registrarBitacora(usuarioActivo.getIdUsuario(), null, null, descBitacora, "ERROR");
            logger.error("insertSocioComercial " + usuarioActivo.getUserid() + " ERROR: " + e);
        }
        
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void cancelarSocio() {
        crearSocio = false;
    }
    
}
