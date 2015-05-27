package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoCfdisRecibidos;
import com.iqtb.validacion.dao.DaoConfiguracionServicios;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoRoles;
import com.iqtb.validacion.dao.DaoRolesOpciones;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.EstadoCfdiR;
import com.iqtb.validacion.emun.Opcion;
import com.iqtb.validacion.emun.RolTipo;
import com.iqtb.validacion.emun.ServAcceso;
import com.iqtb.validacion.emun.ServSMTP;
import com.iqtb.validacion.emun.Servicio;
import com.iqtb.validacion.emun.UsuarioEstado;
import com.iqtb.validacion.encrypt.Encrypt;
import static com.iqtb.validacion.encrypt.Encrypt.decrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Opciones;
import com.iqtb.validacion.pojo.Roles;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.DateTime;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 *
 * @author danielromero
 */
@ManagedBean
@SessionScoped
public class MbAcceso implements Serializable {

    private String user;
    private String pass;
    private String confirmarPass;
    private Usuarios usuario;
    private Usuarios usuarioContraseña;
    private Empresas empresa;
    private String empresaSeleccionada;
    private Map<String, String> listaEmpresas;
    private Servicios servicio;
    private ConfiguracionesServicios DIAS_INACTIVIDAD;
    private ConfiguracionesServicios NUM_INT_FALLIDOS;
    private ConfiguracionesServicios LONGITUD_MIN_CONT;
    private boolean chkBoolean;
    private boolean intentarLogin;
    private String newPass;
    private List<Opciones> opciones;
    private boolean rolEmpresa;
    private boolean rolServidor;
    private boolean rolSocioComercial;
    private boolean VER_ESTADISTICAS_EMPRESA;
    private boolean CARGAR_LOGOS;
    private boolean VER_ESTADISTICAS_SOCIO;
    private boolean MENU_CFDIS;
    private boolean VER_CFDIS_RECIBIDOS;
    private boolean BTN_NUEVO_CFDI;
    private boolean BTN_VALIDAR_CFDI;
    private boolean BTN_CANCELAR_CFDI;
    private boolean BTN_ORDENCOMPRA_CFDI;
    private boolean BTN_ELIMINAR_CFDI;
    private boolean BTN_REPORTE_CFDI;
    private boolean LINK_REPORTE_CFDI;
    private boolean VER_CFDIS_ELIMINADOS;
    private boolean BTN_RESTAURAR_ELIMINADO;
    private boolean BTN_DELETE_CFDI;
    private boolean BTN_REPORTE_ELIMINADOS;
    private boolean LINK_REPORTE_ELIMINADOS;
    private boolean VER_CFDIS_EMITIDOS;
    private boolean BTN_REPORTE_EMITIDOS;
    private boolean LINK_REPORTE_EMITIDOS;
    private boolean VER_ORDEN_COMPRA;
    private boolean BTN_NUEVO_ORDEN;
    private boolean MENU_CONFIGURACION;
    private boolean VER_CONFIG_EMPRESA;
    private boolean BTN_MODIFICAR_EMPRESA;
    private boolean VER_CONFIG_NOTIFICACION;
    private boolean BTN_MODIFICAR_NOTIFICACION;
    private boolean VER_CONFIG_PLANTILLA;
    private boolean CONFIGURAR_PLANTILLA;
    private boolean VER_CONFIG_EMAIL;
    private boolean BTN_MODIFICAR_SMTP;
    private boolean BTN_MODIFICAR_RECEPCION;
    private boolean VER_CONFIG_FTP;
    private boolean CONFIGURAR_FTP;
    private boolean VER_CONFIG_REPORTAR;
    private boolean CONFIGURAR_REPORTAR;
    private boolean VER_CONFIG_ADDENDA;
    private boolean CONFIGURAR_ADDENDA;
    private boolean VER_CONFIG_ADAPTADOR_CONECTOR;
    private boolean VER_SOCIOS_COMERCIALES;
    private boolean BTN_NUEVO_SC;
    private boolean BTN_VER_SC;
    private boolean BTN_MODIFICAR_SC;
    private boolean BTN_ELIMINAR_SC;
    private boolean VER_CONTABILIDAD_ELECTRONICA;
    private boolean BTN_NUEVO_CAT;
    private boolean BTN_NUEVO_BAL;
    private boolean BTN_NUEVO_POL;
    private boolean BTN_NUEVO_FOL;
    private boolean BTN_NUEVO_CUE;
    private boolean BTN_PROCESAR_CE;
    private boolean BTN_ELIMINAR_CE;
    private boolean VER_CONFIG_SERVICIOS;
    private boolean CONFIG_SERVICIOS;
    private boolean VER_SERV_RECEPCION;
    private boolean VER_SERV_VALIDACION;
    private boolean VER_SERV_NOTIFICACIONES;
    private boolean VER_SERV_ACCESO;
    private boolean VER_SERV_SMTP;
    private boolean VER_SERV_CONTABILIDAD_ELECTRONICA;
    private boolean VER_SERV_SINCRONIZAR;
    private boolean VER_SERV_CONFIG_XSA;
    private boolean VER_SERV_CENTINELA;
    private boolean VER_SERV_SCRIPT;
    private boolean VER_USUARIOS;
    private boolean ADMINISTRAR_USUARIOS;
    private boolean VER_ROLES;
    private boolean ADMINISTRAR_ROLES;
    private boolean VER_EMPRESAS;
    private boolean ADMINISTRAR_EMPRESAS;
    private boolean MENU_CFDIS_SC;
    private boolean VER_CFDIS_RECIBIDOS_SC;
    private boolean BTN_NUEVO_CFDI_SC;
    private boolean BTN_VALIDAR_CFDI_SC;
    private boolean BTN_CANCELAR_CFDI_SC;
    private boolean BTN_ELIMINAR_CFDI_SC;
    private boolean BTN_REPORTE_CFDI_SC;
    private boolean LINK_REPORTE_CFDI_SC;
    private boolean VER_CFDIS_ELIMINADOS_SC;
    private boolean BTN_REPORTE_ELIMINADOS_SC;
    private boolean LINK_REPORTE_ELIMINADOS_SC;
    private Integer cfdisValidos;
    private int cfdisInvalidos;
    private int cfdisDuplicados;
    private Integer cfdisTotal;
    private int cfdisSinValidar;
    private int cfdisCancelados;
    private int cfdisEliminados;
    private String descBitacora;
    private Roles rolUsuario;
    private ConfiguracionesServicios expPass;
    private DaoServicio daoServicio;
    private DaoConfiguracionServicios daoConfigServicio;
    private DaoEmpresa daoEmpresa;
    private DaoRoles daoRoles;
    private DaoRolesOpciones daoRolOpcion;
    private DaoUsuario daoUsuario;
    private DaoCfdisRecibidos daoCfdiRecibido;
    private Servicios servSMTP;
    private ConfiguracionesServicios configEmail;
    private ConfiguracionesServicios configPass;
    private ConfiguracionesServicios configHost;
    private ConfiguracionesServicios configPort;
    private ConfiguracionesServicios configSSL;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbAcceso.class);
//    private HttpServletRequest httpServletRequest;
//    private FacesContext faceContext;

    public MbAcceso() {
        usuario = new Usuarios();
        listaEmpresas = new HashMap<String, String>();
        empresa = new Empresas();
        servicio = new Servicios();
        rolUsuario = new Roles();
        opciones = new ArrayList<Opciones>();
        cfdisTotal = 0;
        cfdisDuplicados = 0;
        cfdisInvalidos = 0;
        cfdisValidos = 0;
        cfdisSinValidar = 0;
        cfdisCancelados = 0;
        cfdisEliminados = 0;
        daoServicio = new DaoServicio();
        daoConfigServicio = new DaoConfiguracionServicios();
        daoEmpresa = new DaoEmpresa();
        daoRoles = new DaoRoles();
        daoRolOpcion = new DaoRolesOpciones();
        daoUsuario = new DaoUsuario();
        daoCfdiRecibido = new DaoCfdisRecibidos();
        DIAS_INACTIVIDAD = new ConfiguracionesServicios();
        NUM_INT_FALLIDOS = new ConfiguracionesServicios();
        LONGITUD_MIN_CONT = new ConfiguracionesServicios();
        expPass = new ConfiguracionesServicios();
        rolEmpresa = false;
        rolServidor = false;
        rolSocioComercial = false;
        intentarLogin = false;
//        resetListaOpciones();

        try {
            servicio = daoServicio.getServicoByNombre(Servicio.ADMINISTRACION_ACCESO.name());
            DIAS_INACTIVIDAD = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicio.getIdServicio(), ServAcceso.DIAS_INACTIVIDAD.name());
            NUM_INT_FALLIDOS = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicio.getIdServicio(), ServAcceso.NUM_INT_FALLIDOS.name());
            LONGITUD_MIN_CONT = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicio.getIdServicio(), ServAcceso.LONGITUD_MIN_CONT.name());
            expPass = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicio.getIdServicio(), ServAcceso.DIAS_EXPIRAR_CONT.name());
            intentarLogin = true;
            logger.info("[ACCESO - MbAcceso] Servicio " + servicio.getNombre() + " cargado.");
        } catch (Exception e) {
            descBitacora = "[ACCESO - MbAcceso] Ocurrio un error cargando servicio ERROR " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public Usuarios getUsuarioContraseña() {
        return usuarioContraseña;
    }

    public void setUsuarioContraseña(Usuarios usuarioContraseña) {
        this.usuarioContraseña = usuarioContraseña;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public String getEmpresaSeleccionada() {
        return empresaSeleccionada;
    }

    public void setEmpresaSeleccionada(String empresaSeleccionada) {
        this.empresaSeleccionada = empresaSeleccionada;
    }

    public Map<String, String> getListaEmpresas() {
        return listaEmpresas;
    }

    public void setListaEmpresas(Map<String, String> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public String getConfirmarPass() {
        return confirmarPass;
    }

    public void setConfirmarPass(String confirmarPass) {
        this.confirmarPass = confirmarPass;
    }

    public boolean isChkBoolean() {
        return chkBoolean;
    }

    public void setChkBoolean(boolean chkBoolean) {
        this.chkBoolean = chkBoolean;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public boolean isRolEmpresa() {
//        rolEmpresa = rolEmpresa();
        return rolEmpresa;
    }

    public void setRolEmpresa(boolean rolEmpresa) {
        this.rolEmpresa = rolEmpresa;
    }

    public boolean isRolServidor() {
//        rolServidor = rolServidor();
        return rolServidor;
    }

    public void setRolServidor(boolean rolServidor) {
        this.rolServidor = rolServidor;
    }

    public Integer getCfdisValidos() {
        return cfdisValidos;
    }

    public void setCfdisValidos(Integer cfdisValidos) {
        this.cfdisValidos = cfdisValidos;
    }

    public int getCfdisInvalidos() {
        return cfdisInvalidos;
    }

    public void setCfdisInvalidos(int cfdisInvalidos) {
        this.cfdisInvalidos = cfdisInvalidos;
    }

    public int getCfdisDuplicados() {
        return cfdisDuplicados;
    }

    public void setCfdisDuplicados(int cfdisDuplicados) {
        this.cfdisDuplicados = cfdisDuplicados;
    }

    public Integer getCfdisTotal() {
        return cfdisTotal;
    }

    public void setCfdisTotal(Integer cfdisTotal) {
        this.cfdisTotal = cfdisTotal;
    }

    public int getCfdisSinValidar() {
        return cfdisSinValidar;
    }

    public void setCfdisSinValidar(int cfdisSinValidar) {
        this.cfdisSinValidar = cfdisSinValidar;
    }

    public boolean isRolSocioComercial() {
//        rolSocioComercial = rolSocio();
        return rolSocioComercial;
    }

    public void setRolSocioComercial(boolean rolSocioComercial) {
        this.rolSocioComercial = rolSocioComercial;
    }

    public int getCfdisCancelados() {
        return cfdisCancelados;
    }

    public void setCfdisCancelados(int cfdisCancelados) {
        this.cfdisCancelados = cfdisCancelados;
    }

    public int getCfdisEliminados() {
        return cfdisEliminados;
    }

    public void setCfdisEliminados(int cfdisEliminados) {
        this.cfdisEliminados = cfdisEliminados;
    }

    public String login() {
        logger.info("LOGIN " + intentarLogin);
        if (!intentarLogin) {
            descBitacora = "[ACCESO - login] Error No se ha podido realizar el login";
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede realizar el login. Por favor, intentelo mas tarde.");
            logger.error(descBitacora);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "/login?faces-redirect=true";
        }
        FacesContext context = FacesContext.getCurrentInstance();
        boolean mostarDialog = false;
        boolean update = false;
        int fallidos;
        try {
            if (user != null && !user.isEmpty()) {
                usuario = daoUsuario.getByUserid(user);
                if (usuario != null) {
                    logger.debug("Reset Roles");
                    rolEmpresa = false;
                    rolServidor = false;
                    rolSocioComercial = false;
                    fallidos = usuario.getIntentosFallidos();
                    logger.info("[ACCESO - login] Usuario: " + usuario.getUserid());
                    if (usuario.getPasskey().equals(Encrypt.getSHA512(pass + usuario.getSalt()))) {
                        user = "";
                        pass = "";
                        Date test = new Date();
                        int diasInactivo = Integer.parseInt(DIAS_INACTIVIDAD.getValor());
                        Date lastAction = DateTime.sumarRestarDiasFecha(test, -diasInactivo);

                        if ((usuario.getLastAction().getTime() - (lastAction.getTime())) > 0) {

                            if ((usuario.getDateExpirationPass().getTime() - (new Date().getTime())) > 0) {

                                if (usuario.getEstado().equals(UsuarioEstado.ACTIVO.name()) || usuario.getEstado().equals(UsuarioEstado.AUTENTICADO.name())) {
                                    rolUsuario = daoRoles.getRolById(usuario.getIdRol());
                                    if (rolUsuario.getTipo().equals(RolTipo.SERVIDOR.name())) {
                                        logger.info("[ACCESO - login] SERVIDOR");
                                        rolServidor = true;
                                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
                                        usuario.setEstado(UsuarioEstado.AUTENTICADO.name());
                                        usuario.setIntentosFallidos(0);
                                        usuario.setLastAction(getTimestamp());
                                        update = daoUsuario.updateUsuario(usuario);

                                        if (update) {
                                            String inicioServ = "/principal?faces-redirect=true";
                                            Servicios servSMTP;
                                            servSMTP = daoServicio.getServicoByNombre(Servicio.SERVIDOR_SMTP.name());
                                            ConfiguracionesServicios cServ;
                                            cServ = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.HOST_SMTP.name());
                                            opciones = daoRolOpcion.getOpcionesByIdRol(usuario.getIdRol());
                                            asignarOpciones();
                                            descBitacora = "[ACCESO - login] Inicio de sesion: Usuario " + usuario.getUserid() + ".";
                                            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido " + usuario.getUserid(), "");
                                            FacesContext.getCurrentInstance().addMessage(null, msg);
                                            context.getExternalContext().getFlash().setKeepMessages(true);
                                            logger.info(descBitacora);
                                            if (cServ != null && cServ.getValor().trim().isEmpty()) {
                                                inicioServ = "/Configuracion/servicioSMTP?faces-redirect=true";
                                                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Importante", "Por favor, configure un servidor SMTP.");
                                                FacesContext.getCurrentInstance().addMessage(null, msg);
                                                context.getExternalContext().getFlash().setKeepMessages(true);
                                            }
                                            return inicioServ;
                                        } else {
                                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al iniciar sesión. Por favor, intentelo mas tarde.");
                                            FacesContext.getCurrentInstance().addMessage(null, msg);
                                            descBitacora = "[ACCESO - login] No se ha podido iniciar sesion, Error modificando los datos del usuario " + usuario.getUserid();
                                            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                            logger.error(descBitacora);
                                            return null;
                                        }
                                    } else {
                                        if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                                            logger.info("[ACCESO - login] SOCIO_COMERCIAL");
                                            rolSocioComercial = true;
                                            List<Empresas> lista = daoEmpresa.getEmpresaById(usuario.getIdUsuario());
                                            for (Empresas empresas : lista) {
                                                listaEmpresas.put(empresas.getRfc(), empresas.getRfc());
                                            }
                                            empresaSeleccionada = lista.get(0).getRfc();
                                            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
                                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
                                            usuario.setEstado(UsuarioEstado.AUTENTICADO.name());
                                            usuario.setIntentosFallidos(0);
                                            usuario.setLastAction(getTimestamp());
                                            update = daoUsuario.updateUsuario(usuario);

                                            if (update) {
                                                opciones = daoRolOpcion.getOpcionesByIdRol(usuario.getIdRol());
                                                asignarOpciones();
                                                descBitacora = "[ACCESO - login] Inicio de sesion: " + usuario.getUserid() + " Empresa Seleccionada " + empresa.getRfc() + ".";
                                                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido " + usuario.getUserid(), "");
                                                FacesContext.getCurrentInstance().addMessage(null, msg);
                                                context.getExternalContext().getFlash().setKeepMessages(true);
                                                logger.info(descBitacora);
                                                cfdisRecibidos();
                                                cfdisValidos();
//                                                cfdisDuplicados();
                                                cfdisError();
//                                                cfdisSinValidar();
                                                cfdisCancelados();
                                                cfdisEliminados();
                                                irCDFIs();
                                                return "/principal?faces-redirect=true";
                                            } else {
                                                descBitacora = "[ACCESO - login] ERROR Inicio de sesion: " + usuario.getUserid() + " error actualizando usuario";
                                                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al iniciar sesión. Por favor, intentelo mas tarde.");
                                                FacesContext.getCurrentInstance().addMessage(null, msg);
                                                logger.error(descBitacora);
                                                return null;
                                            }
                                        } else {
                                            logger.info("[ACCESO - login] EMPRESA");
                                            rolEmpresa = true;
                                            List<Empresas> lista = daoEmpresa.getEmpresaById(usuario.getIdUsuario());
                                            for (Empresas empresas : lista) {
                                                listaEmpresas.put(empresas.getRfc(), empresas.getRfc());
                                            }
                                            if (listaEmpresas.size() > 1) {
                                                mostarDialog = true;
                                                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
                                                usuario.setEstado(UsuarioEstado.AUTENTICADO.name());
                                                usuario.setIntentosFallidos(0);
                                                usuario.setLastAction(getTimestamp());
                                                update = daoUsuario.updateUsuario(this.usuario);

                                                if (update) {
                                                    opciones = daoRolOpcion.getOpcionesByIdRol(usuario.getIdRol());
                                                    asignarOpciones();
                                                    descBitacora = "[ACCESO - login] Inicio de sesion: " + usuario.getUserid();
                                                    registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido " + usuario.getUserid(), "Por favor, seleccione una empresa para continuar.");
                                                    logger.info(descBitacora);
                                                } else {
                                                    descBitacora = "[ACCESO - login]] ERROR Inicio de sesion: " + usuario.getUserid() + " error modificando usuario";
                                                    registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al iniciar sesión. Por favor, intentelo mas tarde.");
                                                    logger.error(descBitacora);
                                                }
                                            } else {
                                                empresaSeleccionada = lista.get(0).getRfc();

                                                empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
                                                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
                                                usuario.setEstado(UsuarioEstado.AUTENTICADO.name());
                                                usuario.setIntentosFallidos(0);
                                                usuario.setLastAction(getTimestamp());
                                                update = daoUsuario.updateUsuario(usuario);

                                                if (update) {
                                                    opciones = daoRolOpcion.getOpcionesByIdRol(usuario.getIdRol());
                                                    asignarOpciones();
                                                    descBitacora = "[ACCESO - login] Inicio de sesion: " + usuario.getUserid() + " Empresa Seleccionada " + empresa.getRfc() + ".";
                                                    registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido " + usuario.getUserid(), "");
                                                    FacesContext.getCurrentInstance().addMessage(null, msg);
                                                    context.getExternalContext().getFlash().setKeepMessages(true);
                                                    logger.info(descBitacora);
                                                    cfdisRecibidos();
                                                    cfdisValidos();
//                                                    cfdisDuplicados();
                                                    cfdisError();
//                                                    cfdisSinValidar();
                                                    cfdisCancelados();
                                                    cfdisEliminados();
                                                    irCDFIs();
                                                    return "/principal?faces-redirect=true";
                                                } else {
                                                    descBitacora = "[ACCESO - login] ERROR Inicio de sesion: " + usuario.getUserid() + " error modificando usuario";
                                                    registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al iniciar sesión. Por favor, intentelo mas tarde.");
                                                    FacesContext.getCurrentInstance().addMessage(null, msg);
                                                    logger.error(descBitacora);
                                                    return null;
                                                }
                                            }
                                        }
                                    }
                                } else {

                                    if (usuario.getEstado().equals(UsuarioEstado.NUEVO.name()) || usuario.getEstado().equals(UsuarioEstado.EXPIRADO.name())) {
                                        descBitacora = "[ACCESO - login] " + usuario.getUserid() + " Estado: " + usuario.getEstado() + " se redigira al formulario para crear una nueva contraseña.";
                                        registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                                        pass = null;
                                        logger.info(descBitacora);
                                        return "/Usuario/cambiar?faces-redirect=true";

                                    } else {
                                        descBitacora = "[ACCESO - login]] " + usuario.getUserid() + " No puede iniciar sesion, el estado es: " + usuario.getEstado() + ".";
                                        registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.WARNING.name());
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede iniciar sesión, el estado es: " + usuario.getEstado());
                                        logger.warn(descBitacora);
                                    }
                                }
                            } else {
                                usuario.setEstado(UsuarioEstado.EXPIRADO.name());
                                daoUsuario.updateUsuario(usuario);
                                descBitacora = "[ACCESO - login] " + usuario.getUserid() + " la contraseña ha expirado, el estado es " + usuario.getEstado() + ".";
                                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.WARNING.name());
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario " + usuario.getUserid() + " la contraseña ha expirado.");
                                logger.warn(descBitacora);
                                return "/Usuario/cambiar?faces-redirect=true";

                            }
                        } else {
                            usuario.setEstado(UsuarioEstado.INACTIVO.name());
                            daoUsuario.updateUsuario(usuario);
                            descBitacora = "[ACCESO - login] Usuario " + usuario.getUserid() + " han trancurido mas de " + DIAS_INACTIVIDAD.getValor() + " dias de inactividad, el estado es " + usuario.getEstado() + ".";
                            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.WARNING.name());
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario " + usuario.getUserid() + " se encuntra inactivo, consulte al Administrador.");
                            logger.warn(descBitacora);
                        }
                    } else {

                        fallidos++;
                        int intentos = Integer.parseInt(NUM_INT_FALLIDOS.getValor());

                        if (fallidos >= intentos) {
                            usuario.setIntentosFallidos(fallidos);
                            usuario.setLastAction(getTimestamp());
                            usuario.setEstado(UsuarioEstado.BLOQUEADO.name());
                            update = daoUsuario.updateUsuario(usuario);
                            if (update) {
                                descBitacora = "[ACCESO - login] " + usuario.getUserid() + " alcanzo el número de intentos para acceder, el estado es: " + usuario.getEstado() + ".";
                                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.WARNING.name());
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario bloqueado", "Usuario/Contraseña incorrecto");
                                logger.warn(descBitacora);
                            } else {
                                descBitacora = "[ACCESO - login] Error al modificar al usuario: " + usuario.getUserid() + " alcanzo el número de intentos para acceder";
                                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                logger.error(descBitacora);
                            }
                        } else {
                            usuario.setIntentosFallidos(fallidos);
                            usuario.setLastAction(getTimestamp());
                            update = daoUsuario.updateUsuario(usuario);
                            if (update) {
                                logger.info(usuario.getUserid() + " Intentos fallidos: " + fallidos + "/" + intentos);
                                descBitacora = "[ACCESO - login] " + usuario.getUserid() + " intento acceso, contraseña incorrecta, intentos fallidos: " + usuario.getIntentosFallidos() + "/" + intentos + ".";
                                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.WARNING.name());
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
                                logger.warn(descBitacora);
                            } else {
                                descBitacora = "[ACCESO - login] Error al modificar al usuario: " + usuario.getUserid() + " intento accesar, contraseña incorrecta, intentos fallidos: " + usuario.getIntentosFallidos() + "/" + intentos;
                                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                logger.error(descBitacora);
                            }
                        }
                    }
                } else {
                    logger.warn("LOGIN Usuario: " + user + " no existe en base de datos");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
                }

            } else {
                logger.warn("LOGIN No existe un valor para usuario");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario/Contraseña incorrecto");
            }

        } catch (NoSuchAlgorithmException e) {
            descBitacora = "[ACCESO - login] NoSuchAlgorithmException ERROR: " + e.getMessage();
            registrarBitacora(null, servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al iniciar sesión");
            logger.error(descBitacora);
        } catch (Exception ex) {
            descBitacora = "[ACCESO - login] ERROR: " + ex.getMessage();
            registrarBitacora(null, servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al iniciar sesión");
            logger.error(descBitacora);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        RequestContext Rcontext = RequestContext.getCurrentInstance();
        Rcontext.addCallbackParam("mostarDialog", mostarDialog);
        pass = "";
        return "/login";
    }

    public String logout() {
        boolean update = false;
        String sesioinUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        try {
            this.usuario.setEstado(UsuarioEstado.ACTIVO.name());
            this.usuario.setLastAction(getTimestamp());
            update = daoUsuario.updateUsuario(this.usuario);

            if (update) {
                FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
                descBitacora = "[ACCESO - logout] Fin de Sesion: " + usuario.getUserid();
                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Sesión cerrada correctamente", null);
                logger.info(descBitacora);
                resetSession();
                return "/login?faces-redirect=true";
            } else {
                descBitacora = "[ACCESO - logout] Error al modificar los datos del Usuario " + usuario.getUserid() + " en el cierre de sesion";
                registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(descBitacora);
            }
        } catch (Exception e) {
            descBitacora = "[ACCESO - logout] ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al cerrar sesion");
            logger.error(descBitacora);
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return "/login?faces-redirect=true";
    }

    public void resetSession() {
        logger.info("limpiando objetos de sesion");
        usuario = new Usuarios();
        listaEmpresas = new HashMap<String, String>();
        empresa = new Empresas();
        servicio = new Servicios();
        rolUsuario = new Roles();
        opciones = new ArrayList<Opciones>();
        DIAS_INACTIVIDAD = new ConfiguracionesServicios();
        NUM_INT_FALLIDOS = new ConfiguracionesServicios();
        LONGITUD_MIN_CONT = new ConfiguracionesServicios();
        expPass = new ConfiguracionesServicios();
    }

    public boolean getSession() {
        boolean estado;
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario") == null) {
            estado = false;
        } else {
            estado = true;
        }
        return estado;
    }

    public String existeSeleccionEmpresa() {
        boolean existeSeleccionEmpresa = false;
        if (!empresaSeleccionada.equals("vacio")) {
            existeSeleccionEmpresa = true;
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione una empresa");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.warn("[ACCESO - existeSeleccionEmpresa] No existe seleccion de Empresa");
            return null;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("existeSeleccionEmpresa", existeSeleccionEmpresa);
        try {
            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
            descBitacora = "[ACCESO - existeSeleccionEmpresa] Inicio de sesion: Usuario " + usuario.getUserid() + " Empresa Seleccionada " + empresa.getRfc() + ".";
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(descBitacora);
            cfdisRecibidos();
            cfdisValidos();
//            cfdisDuplicados();
            cfdisError();
//            cfdisSinValidar();
            cfdisCancelados();
            cfdisEliminados();
            irCDFIs();
            return "/principal?faces-redirect=true";
        } catch (Exception ex) {
            descBitacora = "[ACCESO - existeSeleccionEmpresa] Inicio de sesion: " + usuario.getUserid() + " error obteniendo la empresa " + empresaSeleccionada + " ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al iniciar sesión. Por favor, intentelo mas tarde.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            logger.error(descBitacora);
        }
        return null;
    }

    public String cambiarEmpresa() {
        boolean existeSeleccionEmpresa = false;
        if (!empresaSeleccionada.equals("vacio")) {
            existeSeleccionEmpresa = true;
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione una empresa");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            logger.warn("[ACCESO - cabiarEmpresa] No existe seleccion de Empresa");
            return null;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("existeSeleccionEmpresa", existeSeleccionEmpresa);
        try {
            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
//            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", this.usuario);
            descBitacora = "[ACCESO - cabiarEmpresa] " + usuario.getUserid() + " Empresa Seleccionada " + empresa.getRfc() + ".";
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            logger.info(descBitacora);
            cfdisRecibidos();
            cfdisValidos();
//            cfdisDuplicados();
            cfdisError();
//            cfdisSinValidar();
            cfdisCancelados();
            cfdisEliminados();
            irCDFIs();
            return "/principal?faces-redirect=true";
        } catch (Exception ex) {
            descBitacora = "[ACCESO - cabiarEmpresa] " + usuario.getUserid() + " error obteniendo la empresa " + empresaSeleccionada + " ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al cambiar de Empresa.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            logger.error(descBitacora);
        }
        return null;
    }

    public void restablecerContrasenia() {
        String newPass;
        String newPassKey;

        boolean respuestaEmail = false;
        int diasExp = Integer.parseInt(this.expPass.getValor());
        Date test = new Date();
        Date dateExp = DateTime.sumarRestarDiasFecha(test, diasExp);
        int nPass = Integer.parseInt(this.LONGITUD_MIN_CONT.getValor());
        try {

            if (user == null || user.trim().isEmpty()) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor introduzca usuario valido para restablecer contraseña.");
//                FacesContext.getCurrentInstance().addMessage(null, this.msg);
//                return;
            } else {
                usuarioContraseña = daoUsuario.getByUserid(user);

                if (usuarioContraseña == null) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor introduzca usuario valido para restablecer contraseña.");
                    logger.warn("Usuario " + user + " no existe en la base de datos");
                } else {
                    servSMTP = daoServicio.getServicoByNombre(Servicio.SERVIDOR_SMTP.name());
                    configEmail = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.EMAIL_SMTP.name());
                    configPass = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.PASS_SMTP.name());
                    configHost = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.HOST_SMTP.name());
                    configPort = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.PORT_SMTP.name());
                    configSSL = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.SSL_SMTP.name());
                    if (configEmail.getValor().trim().isEmpty() || configHost.getValor().trim().isEmpty() || configPort.getValor().trim().isEmpty()) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Por el momento no es posible restablecer la contraseña, no existe un servidor SMTP configurado. Consulte a su administrador.");
                        descBitacora = "[ACCESO - restablecerContrasenia] " + usuarioContraseña.getUserid() + ". No es posible enviar la contraseña por email, no existe un servidor SMTP configurado.";
                        registrarBitacora(usuarioContraseña.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                        logger.error(descBitacora);
                    } else {
                        newPass = Encrypt.getContraseniaAleatoria(nPass);
                        logger.info(usuarioContraseña.getUserid() + ". Se ha generado una nueva contraseña para el usuario.");
                        newPassKey = Encrypt.getSHA512(newPass + this.usuarioContraseña.getSalt());
                        logger.info(usuarioContraseña.getUserid() + ". La nueva contraseña ha sido encriptada.");

                        usuarioContraseña.setPasskey(newPassKey);
                        usuarioContraseña.setLastAction(getTimestamp());
                        usuarioContraseña.setDateExpirationPass(dateExp);
                        usuarioContraseña.setEstado(UsuarioEstado.EXPIRADO.name());
                        boolean update = daoUsuario.updateUsuario(usuarioContraseña);
                        if (update) {
                            logger.info("Usuario " + usuarioContraseña.getUserid() + " actualizado en base de datos.");
                            logger.info("Generando correo electronico con la nueva contraseña");
                            String asunto = "Restablecer contraseña";
                            String contenido = "Usuario: " + usuarioContraseña.getUserid() + "<br/>Contraseña: " + newPass + "<br/><br/>Cuando ingrese al Portal WEB, por seguridad cambie su contraseña.";
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
                            conSMTP.createMessage(usuarioContraseña.getEmail(), asunto, contenido, false);
                            String conn;
                            conn = conSMTP.sendMessage();
                            logger.info(usuarioContraseña.getUserid() + ". Respueta servidor SMTP " + conn);
                            if (conn.trim().charAt(0) == '2') {
                                respuestaEmail = true;
                                logger.info("Correo electrónico enviado a " + usuarioContraseña.getEmail());
                                descBitacora = "[ACCESO - restablecerContrasenia] " + usuarioContraseña.getUserid() + " la contraseña ha sido restablecida y enviada al correo electronico " + usuarioContraseña.getEmail();
                                registrarBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La contraseña se ha enviado al correo electrónico.");
                                logger.info(descBitacora);
                            } else {
                                descBitacora = "[ACCESO - restablecerContrasenia] " + usuarioContraseña.getUserid() + " Error al enviar correo con la nueva contraseña.";
                                registrarBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al restablecer la contraseña, Por favor intente de nuevo.");
                                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                                logger.error(descBitacora);
                            }
                        } else {
                            descBitacora = "[ACCESO - restablecerContrasenia] " + usuarioContraseña.getUserid() + " Error al modificar usuario en base de datos.";
                            registrarBitacora(usuarioContraseña.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo restablecer la contraseña, Por favor intente de nuevo.");
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            logger.error(descBitacora);
//                            return;
                        }
                    }
                }
            }
        } catch (NoSuchAlgorithmException ex) {
            descBitacora = "[ACCESO - restablecerContrasenia] NoSuchAlgorithmException ERROR: " + ex.getMessage();
            registrarBitacora(null, servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al restablecer la contraseña, Por favor intente de nuevo.");
            logger.error(descBitacora);
//            return;
        } catch (Exception e) {
            descBitacora = "[ACCESO - restablecerContrasenia] ERROR: " + e.getMessage();
            registrarBitacora(null, servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al restablecer la contraseña, Por favor intente de nuevo.");
            logger.error(descBitacora);
//            return;
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", respuestaEmail);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public String recuperarContrasenia() {
        user = "";
        return "/Usuario/restablecer?faces-redirect=true";
    }

    public String irLogin() {
        usuarioContraseña = null;
        servSMTP = null;
        configEmail = null;
        configPass = null;
        configHost = null;
        configPort = null;
        configSSL = null;
        user = "";
        return "/login?faces-redirect=true";
    }

    public String irPrincipal() {
        if (usuario.getUserid() != null) {
            if (!rolUsuario.getTipo().equals(RolTipo.SERVIDOR.name())) {
                cfdisRecibidos();
                cfdisValidos();
//                cfdisDuplicados();
                cfdisError();
//                cfdisSinValidar();
                cfdisCancelados();
                cfdisEliminados();
            }
        }
        return "/principal?faces-redirect=true";
    }

    public void nuevaContrasenia() {
        boolean update = false;
        int nPass = Integer.parseInt(LONGITUD_MIN_CONT.getValor());
        int diasExp = Integer.parseInt(expPass.getValor());
        Date test = new Date();
        Date dateExp = DateTime.sumarRestarDiasFecha(test, diasExp);
        logger.info(usuario.getUserid() + ". Inicia NUEVA_CONTRASEÑA");

        try {

            if (this.pass == null || this.pass.trim().isEmpty()) {
                logger.info("Introduzca un contraseña valida");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Introduzca una contraseña valida.");
            } else {
                if (this.pass.length() >= nPass) {
                    logger.info("Contraseña longitud correcta");
                    if (this.pass.equals(this.confirmarPass)) {
//                        this.usuario = new DaoUsuario().getByUserid(this.user);
//                        logger.info(usuario.getUserid() + ". Usuario obtenido");

                        usuario.setSalt(Encrypt.getSALT(20));
                        usuario.setPasskey(Encrypt.getSHA512(this.pass + usuario.getSalt()));
                        usuario.setEstado(UsuarioEstado.ACTIVO.name());
                        usuario.setIntentosFallidos(0);
                        usuario.setDateExpirationPass(dateExp);
                        usuario.setLastAction(getTimestamp());
                        update = daoUsuario.updateUsuario(usuario);
                        if (update) {
                            descBitacora = "[ACCESO - nuevaContrasenia] " + usuario.getUserid() + " ha modificado la contraseña, el estado es " + usuario.getEstado();
                            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Contraseña modificada.");
                            logger.info(descBitacora);
                        } else {
                            descBitacora = "[ACCESO - nuevaContrasenia] " + usuario.getUserid() + " Error modificado la contraseña";
                            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando la contraseña.");
                            logger.info(descBitacora);
                        }
                    } else {
                        logger.warn("las contraseñas no son iguales.");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
                    }
                } else {
                    logger.warn("La contraseña debe contener al menos " + nPass + " caracteres.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
                }
            }

        } catch (NoSuchAlgorithmException ex) {
            descBitacora = "[ACCESO - nuevaContrasenia] NoSuchAlgorithmException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando la contraseña, Por favor, intentelo de nuevo.");
            logger.error(descBitacora);
        } catch (Exception e) {
            descBitacora = "[ACCESO - nuevaContrasenia] ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando la contraseña, Por favor, intentelo de nuevo.");
            logger.error(descBitacora);
        }
        pass = "";
        confirmarPass = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", update);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void modContasenia() {
        boolean update = false;
        int nPass = Integer.parseInt(this.LONGITUD_MIN_CONT.getValor());
        Usuarios u;
        try {
            if (chkBoolean) {
                if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
                    u = daoUsuario.getUsuarioByEmail(usuario.getEmail());
                    if (u == null || u.getUserid().equals(usuario.getUserid())) {
                        if (usuario.getPasskey().equals(Encrypt.getSHA512(this.pass + usuario.getSalt()))) {
                            if (this.newPass.length() >= nPass) {
                                if (this.newPass.equals(this.confirmarPass)) {
                                    this.usuario.setSalt(Encrypt.getSALT(20));
                                    this.usuario.setPasskey(Encrypt.getSHA512(this.newPass + this.usuario.getSalt()));
                                    this.usuario.setLastAction(getTimestamp());
                                    update = daoUsuario.updateUsuario(this.usuario);
                                    if (update) {
                                        descBitacora = "[ACCESO - modContasenia] " + usuario.getUserid() + " ha modificado sus datos.";
                                        registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos se han modificado.");
                                        logger.info(descBitacora);
                                    } else {
                                        descBitacora = "[ACCESO - modContasenia] " + usuario.getUserid() + " Error modificado sus datos.";
                                        registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
                                        logger.error(descBitacora);
                                    }
                                } else {
                                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
                                }
                            } else {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La contraseña debe contener al menos " + nPass + " caracteres.");
                            }
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca su contraseña actual.");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "La direccion de correo ya se encuentra registrada.");
                        logger.info("la direccion de correo electronico ya existe registrada");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca su correo electrónico.");
                    logger.info("Email no puede ser vacio");
                }
            } else {
                if (usuario.getEmail() != null && !usuario.getEmail().trim().isEmpty()) {
                    u = daoUsuario.getUsuarioByEmail(usuario.getEmail());
                    if (u == null || u.getUserid().equals(usuario.getUserid())) {
                        update = daoUsuario.updateUsuario(this.usuario);
                        if (update) {
                            descBitacora = "[ACCESO - modContasenia] " + usuario.getUserid() + " modifico sus datos.";
                            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos se han modificado.");
                            logger.info(descBitacora);
                        } else {
                            descBitacora = "[ACCESO - modContasenia] " + usuario.getUserid() + " Error modificando sus datos.";
                            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
                            logger.error(descBitacora);
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "La direccion de correo ya se encuentra registrada.");
                        logger.info("la direccion de correo electronico ya existe registrada");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca su correo electrónico.");
                    logger.debug("Email no puede ser null");
                }
            }
        } catch (NoSuchAlgorithmException ex) {
            descBitacora = "[ACCESO - modContasenia] NoSuchAlgorithmException Error: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (Exception e) {
            descBitacora = "[ACCESO - modContasenia] Error: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String irCDFIs() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a CFDIs: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/CFDI/recibidos?faces-redirect=true";
    }

    public String irCDFIsEmitidos() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a CFDIsEmitidos: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/CFDI/emitidos?faces-redirect=true";
    }

    public String irCDFIsEliminados() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a CFDIs Eliminados: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/CFDI/eliminados?faces-redirect=true";
    }

    public String irOrdenCompra() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a CFDIs Eliminados: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/CFDI/ordenCompra?faces-redirect=true";
    }

    public String irConfiguracionEmpresa() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a ConfiguracionEmpresa: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Configuracion/empresa?faces-redirect=true";
    }

    public String irConfiguracionNotificaciones() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a ConfiguracionNotificaciones: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Configuracion/notificaciones?faces-redirect=true";
    }

    public String irConfiguracionPlantilla() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a ConfiguracionPlantilla: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Configuracion/plantilla?faces-redirect=true";
    }

    public String irConfiguracionEmail() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a ConfiguracionEmail: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Configuracion/email?faces-redirect=true";
    }

    public String irConfiguracionAddenda() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a ConfiguracionAdenda: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Configuracion/addenda?faces-redirect=true";
    }

    public String irSociosComerciales() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a SociosComerciales: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Socios/sociosComerciales?faces-redirect=true";
    }

    public String irSocioMisDatos() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a SocioMisDatos: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Socios/socio?faces-redirect=true";
    }

    public String irUsuarioValidacionFTP() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a UsuariosFTP: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Configuracion/usuarioftp?faces-redirect=true";
    }

    public String irReportar() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a Reportar: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/Configuracion/reportar?faces-redirect=true";
    }

    public String irUsuariosFTP() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a UsuariosFTP: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/ContabilidadElectronica/usuariosftp?faces-redirect=true";
    }

    public String irConectorAdaptador() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a ConectorAdaptador: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/ContabilidadElectronica/adaptadorConector?faces-redirect=true";
    }

    public String irContabilidadElectronica() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) faceContext.getExternalContext().getRequest();
        logger.debug("MbAceso Empresa Seleccionada enviada a ContabilidadElectronica: " + empresaSeleccionada);
        httpServletRequest.getSession().setAttribute("empresaSeleccionada", empresaSeleccionada);
        return "/ContabilidadElectronica/administrarArchivos?faces-redirect=true";
    }

//    public boolean rolEmpresa() {
//        boolean esEmpresa = false;
//        try {
//            if (rolUsuario.getTipo().equals(RolTipo.EMPRESA.name())) {
//                esEmpresa = true;
//            }
//
//        } catch (Exception ex) {
//            descBitacora = "[ACCESO - rolEmpresa] - ERROR " + ex.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(descBitacora);
//        }
//
//        return esEmpresa;
//    }
//
//    public boolean rolServidor() {
//        boolean esServidor = false;
//        try {
//            if (rolUsuario.getTipo().equals(RolTipo.SERVIDOR.name())) {
//                esServidor = true;
//            }
//        } catch (Exception ex) {
//            descBitacora = "[ACCESO - rolServidor] - ERROR " + ex.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(descBitacora);
//        }
//        return esServidor;
//    }
//
//    public boolean rolSocio() {
//        boolean socio = false;
//        try {
//            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
//                socio = true;
//            }
//        } catch (Exception ex) {
//            descBitacora = "[ACCESO - rolSocio] - ERROR " + ex.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(descBitacora);
//        }
//        return socio;
//    }
    public void asignarOpciones() {
        resetListaOpciones();
        if (opciones == null && opciones.size() <= 0) {
            descBitacora = "[ACCESO - asignarOpciones] " + usuario.getUserid() + " no se encontraron Opciones para el rol " + usuario.getIdRol();
            registrarBitacora(usuario.getIdUsuario(), servicio.getIdServicio(), null, descBitacora, BitacoraTipo.WARNING.name());
            logger.warn(descBitacora);
        } else {
            if (rolEmpresa) {
                logger.info("[ACCESO - asignarOpciones] " + usuario.getUserid() + " asignando opciones tipo empresa");
                for (Opciones opcion : opciones) {
                    if (opcion.getOpcion().equals(Opcion.VER_ESTADISTICAS_EMPRESA.name())) {
                        VER_ESTADISTICAS_EMPRESA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.MENU_CFDIS.name())) {
                        MENU_CFDIS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CFDIS_RECIBIDOS.name())) {
                        VER_CFDIS_RECIBIDOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_CFDI.name())) {
                        BTN_NUEVO_CFDI = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_VALIDAR_CFDI.name())) {
                        BTN_VALIDAR_CFDI = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_CANCELAR_CFDI.name())) {
                        BTN_CANCELAR_CFDI = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_ORDENCOMPRA_CFDI.name())) {
                        BTN_ORDENCOMPRA_CFDI = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_ELIMINAR_CFDI.name())) {
                        BTN_ELIMINAR_CFDI = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_REPORTE_CFDI.name())) {
                        BTN_REPORTE_CFDI = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.LINK_REPORTE_CFDI.name())) {
                        LINK_REPORTE_CFDI = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CFDIS_ELIMINADOS.name())) {
                        VER_CFDIS_ELIMINADOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_RESTAURAR_ELIMINADO.name())) {
                        BTN_RESTAURAR_ELIMINADO = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CFDIS_ELIMINADOS.name())) {
                        VER_CFDIS_ELIMINADOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_DELETE_CFDI.name())) {
                        BTN_DELETE_CFDI = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_REPORTE_ELIMINADOS.name())) {
                        BTN_REPORTE_ELIMINADOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.LINK_REPORTE_ELIMINADOS.name())) {
                        LINK_REPORTE_ELIMINADOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CFDIS_EMITIDOS.name())) {
                        VER_CFDIS_EMITIDOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_REPORTE_EMITIDOS.name())) {
                        BTN_REPORTE_EMITIDOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.LINK_REPORTE_EMITIDOS.name())) {
                        LINK_REPORTE_EMITIDOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_ORDEN_COMPRA.name())) {
                        VER_ORDEN_COMPRA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_ORDEN.name())) {
                        BTN_NUEVO_ORDEN = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.MENU_CONFIGURACION.name())) {
                        MENU_CONFIGURACION = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_EMPRESA.name())) {
                        VER_CONFIG_EMPRESA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_MODIFICAR_EMPRESA.name())) {
                        BTN_MODIFICAR_EMPRESA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_NOTIFICACION.name())) {
                        VER_CONFIG_NOTIFICACION = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_MODIFICAR_NOTIFICACION.name())) {
                        BTN_MODIFICAR_NOTIFICACION = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_PLANTILLA.name())) {
                        VER_CONFIG_PLANTILLA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.CONFIGURAR_PLANTILLA.name())) {
                        CONFIGURAR_PLANTILLA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_EMAIL.name())) {
                        VER_CONFIG_EMAIL = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_MODIFICAR_SMTP.name())) {
                        BTN_MODIFICAR_SMTP = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_MODIFICAR_RECEPCION.name())) {
                        BTN_MODIFICAR_RECEPCION = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_FTP.name())) {
                        VER_CONFIG_FTP = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.CONFIGURAR_FTP.name())) {
                        CONFIGURAR_FTP = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_REPORTAR.name())) {
                        VER_CONFIG_REPORTAR = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.CONFIGURAR_REPORTAR.name())) {
                        CONFIGURAR_REPORTAR = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_ADDENDA.name())) {
                        VER_CONFIG_ADDENDA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.CONFIGURAR_ADDENDA.name())) {
                        CONFIGURAR_ADDENDA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_ADAPTADOR_CONECTOR.name())) {
                        VER_CONFIG_ADAPTADOR_CONECTOR = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SOCIOS_COMERCIALES.name())) {
                        VER_SOCIOS_COMERCIALES = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_SC.name())) {
                        BTN_NUEVO_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_VER_SC.name())) {
                        BTN_VER_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_MODIFICAR_SC.name())) {
                        BTN_MODIFICAR_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_ELIMINAR_SC.name())) {
                        BTN_ELIMINAR_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONTABILIDAD_ELECTRONICA.name())) {
                        VER_CONTABILIDAD_ELECTRONICA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_CAT.name())) {
                        BTN_NUEVO_CAT = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_BAL.name())) {
                        BTN_NUEVO_BAL = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_POL.name())) {
                        BTN_NUEVO_POL = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_FOL.name())) {
                        BTN_NUEVO_FOL = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_CUE.name())) {
                        BTN_NUEVO_CUE = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_PROCESAR_CE.name())) {
                        BTN_PROCESAR_CE = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_ELIMINAR_CE.name())) {
                        BTN_ELIMINAR_CE = true;
                    }
                }
            }
            if (rolServidor) {
                logger.info("[ACCESO - asignarOpciones] " + usuario.getUserid() + " asignando opciones tipo servidor");
                for (Opciones opcion : opciones) {
                    if (opcion.getOpcion().equals(Opcion.CARGAR_LOGOS.name())) {
                        CARGAR_LOGOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CONFIG_SERVICIOS.name())) {
                        VER_CONFIG_SERVICIOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.CONFIG_SERVICIOS.name())) {
                        CONFIG_SERVICIOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_RECEPCION.name())) {
                        VER_SERV_RECEPCION = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_VALIDACION.name())) {
                        VER_SERV_VALIDACION = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_NOTIFICACIONES.name())) {
                        VER_SERV_NOTIFICACIONES = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_ACCESO.name())) {
                        VER_SERV_ACCESO = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_SMTP.name())) {
                        VER_SERV_SMTP = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_CONTABILIDAD_ELECTRONICA.name())) {
                        VER_SERV_CONTABILIDAD_ELECTRONICA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_SINCRONIZAR.name())) {
                        VER_SERV_SINCRONIZAR = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_CONFIG_XSA.name())) {
                        VER_SERV_CONFIG_XSA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_CENTINELA.name())) {
                        VER_SERV_CENTINELA = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_SERV_SCRIPT.name())) {
                        VER_SERV_SCRIPT = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_USUARIOS.name())) {
                        VER_USUARIOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.ADMINISTRAR_USUARIOS.name())) {
                        ADMINISTRAR_USUARIOS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_ROLES.name())) {
                        VER_ROLES = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.ADMINISTRAR_ROLES.name())) {
                        ADMINISTRAR_ROLES = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_EMPRESAS.name())) {
                        VER_EMPRESAS = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.ADMINISTRAR_EMPRESAS.name())) {
                        ADMINISTRAR_EMPRESAS = true;
                    }
                }
            }
            if (rolSocioComercial) {
                logger.info("[ACCESO - asignarOpciones] " + usuario.getUserid() + " asignando opciones tipo socio comercial");
                for (Opciones opcion : opciones) {
                    if (opcion.getOpcion().equals(Opcion.VER_ESTADISTICAS_SOCIO.name())) {
                        VER_ESTADISTICAS_SOCIO = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.MENU_CFDIS_SC.name())) {
                        MENU_CFDIS_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CFDIS_RECIBIDOS_SC.name())) {
                        VER_CFDIS_RECIBIDOS_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_NUEVO_CFDI_SC.name())) {
                        BTN_NUEVO_CFDI_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_VALIDAR_CFDI_SC.name())) {
                        BTN_VALIDAR_CFDI_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_CANCELAR_CFDI_SC.name())) {
                        BTN_CANCELAR_CFDI_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_ELIMINAR_CFDI_SC.name())) {
                        BTN_ELIMINAR_CFDI_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_REPORTE_CFDI_SC.name())) {
                        BTN_REPORTE_CFDI_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.LINK_REPORTE_CFDI_SC.name())) {
                        LINK_REPORTE_CFDI_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.VER_CFDIS_ELIMINADOS_SC.name())) {
                        VER_CFDIS_ELIMINADOS_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.BTN_REPORTE_ELIMINADOS_SC.name())) {
                        BTN_REPORTE_ELIMINADOS_SC = true;
                    }
                    if (opcion.getOpcion().equals(Opcion.LINK_REPORTE_ELIMINADOS_SC.name())) {
                        LINK_REPORTE_ELIMINADOS_SC = true;
                    }
                }
            }
            logger.info("[ACCESO - asignarOpciones] " + usuario.getUserid() + " opciones asignadas");
        }
    }

    private void cfdisValidos() {
        String hql = "select count(idCfdiRecibido) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa();
        try {
            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                hql = hql + " and idSocioComercial = " + usuario.getIdSocioComercial() + " and (estado = '" + EstadoCfdiR.VALIDO.name() + "' or estado = '" + EstadoCfdiR.WARNING.name() + "')";
                cfdisValidos = daoCfdiRecibido.IntegerCfdisByHql(hql);
            } else {
                hql = hql + " and (estado = '" + EstadoCfdiR.VALIDO.name() + "' or estado = '" + EstadoCfdiR.WARNING.name() + "')";
                cfdisValidos = daoCfdiRecibido.IntegerCfdisByHql(hql);
            }
        } catch (Exception e) {
            descBitacora = "[ACCESO - cfdisValidos] " + usuario.getUserid() + " Error: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

//    private void cfdisDuplicados() {
//        String hql = "select count(idCfdiRecibido) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa();
//        try {
//            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
//                hql = hql + " and idSocioComercial = " + usuario.getIdSocioComercial() + " and estado = '" + EstadoCfdiR.DUPLICADO.name() + "'";
//                cfdisDuplicados = daoCfdiRecibido.IntegerCfdisByHql(hql);
//            } else {
//                hql = hql + " and estado = '" + EstadoCfdiR.DUPLICADO.name() + "'";
//                cfdisDuplicados = daoCfdiRecibido.IntegerCfdisByHql(hql);
//            }
//        } catch (Exception e) {
//            descBitacora = "[ACCESO] cfdisDuplicados Error: " + e.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(usuario.getUserid() + ". cfdisDuplicados - ERROR " + e);
//        }
//    }
    private void cfdisError() {
        String hql = "select count(idCfdiRecibido) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa();
        try {
            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                hql = hql + " and idSocioComercial = " + usuario.getIdSocioComercial() + "and (estado = 'CORRUPTO' or estado = 'SELLO_INVALIDO' or estado = 'RECEPTOR_INVALIDO' or estado = 'SERIE_FOLIO_INVALIDO' or estado = 'XML_INVALIDO' or estado = 'CERTIFICADO_INVALIDO')";
                cfdisInvalidos = daoCfdiRecibido.IntegerCfdisByHql(hql);
            } else {
                hql = hql + "and (estado = 'CORRUPTO' or estado = 'SELLO_INVALIDO' or estado = 'RECEPTOR_INVALIDO' or estado = 'SERIE_FOLIO_INVALIDO' or estado = 'XML_INVALIDO' or estado = 'CERTIFICADO_INVALIDO')";
                cfdisInvalidos = daoCfdiRecibido.IntegerCfdisByHql(hql);
            }
        } catch (Exception e) {
            descBitacora = "[ACCESO - cfdisError] " + usuario.getUserid() + " Error: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    private void cfdisRecibidos() {
        String hql = "select count(idCfdiRecibido) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa();
        try {
            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                hql = hql + " and idSocioComercial = " + usuario.getIdSocioComercial();
                cfdisTotal = daoCfdiRecibido.IntegerCfdisByHql(hql);
            } else {
                cfdisTotal = daoCfdiRecibido.IntegerCfdisByHql(hql);
            }
        } catch (Exception e) {
            descBitacora = "[ACCESO - cfdisRecibidos] " + usuario.getUserid() + " Error: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

//    private void cfdisSinValidar() {
//        String hql = "select count(idCfdiRecibido) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa();
//        try {
//            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
//                hql = hql + " and idSocioComercial = " + usuario.getIdSocioComercial() + " and (estado = '" + EstadoCfdiR.NUEVO.name() + "' or estado = '" + EstadoCfdiR.SIN_VALIDAR.name() + "')";
//                cfdisSinValidar = daoCfdiRecibido.IntegerCfdisByHql(hql);
//            } else {
//                hql = hql + " and (estado = '" + EstadoCfdiR.NUEVO.name() + "' or estado = '" + EstadoCfdiR.SIN_VALIDAR.name() + "')";
//                cfdisSinValidar = daoCfdiRecibido.IntegerCfdisByHql(hql);
//            }
//        } catch (Exception e) {
//            descBitacora = "[ACCESO] cfdisSinValidar Error: " + e.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(usuario.getUserid() + ". cfdisSinValidar - ERROR " + e);
//        }
//    }
    private void cfdisCancelados() {
        String hql = "select count(idCfdiRecibido) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa();
        try {
            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                hql = hql + " and idSocioComercial = " + usuario.getIdSocioComercial() + " and estado = '" + EstadoCfdiR.CANCELADO.name() + "'";
                cfdisCancelados = daoCfdiRecibido.IntegerCfdisByHql(hql);
            } else {
                hql = hql + " and estado = '" + EstadoCfdiR.CANCELADO.name() + "'";
                cfdisCancelados = daoCfdiRecibido.IntegerCfdisByHql(hql);
            }
        } catch (Exception e) {
            descBitacora = "[ACCESO - cfdisCancelados] " + usuario.getUserid() + " Error: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    private void cfdisEliminados() {
        String hql = "select count(idCfdiRecibido) from CfdisRecibidos where idEmpresa = " + empresa.getIdEmpresa();
        try {
            if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
                hql = hql + " and idSocioComercial = " + usuario.getIdSocioComercial() + " and estado = '" + EstadoCfdiR.ELIMINADO.name() + "'";
                cfdisEliminados = daoCfdiRecibido.IntegerCfdisByHql(hql);
            } else {
                hql = hql + " and estado = '" + EstadoCfdiR.ELIMINADO.name() + "'";
                cfdisEliminados = daoCfdiRecibido.IntegerCfdisByHql(hql);
            }
        } catch (Exception e) {
            descBitacora = "[ACCESO - cfdisEliminados] " + usuario.getUserid() + " Error: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    private void resetListaOpciones() {
        logger.debug("Reset Lista de Opciones");
        VER_ESTADISTICAS_EMPRESA = false;
        CARGAR_LOGOS = false;
        VER_ESTADISTICAS_SOCIO = false;
        MENU_CFDIS = false;
        VER_CFDIS_RECIBIDOS = false;
        BTN_NUEVO_CFDI = false;
        BTN_VALIDAR_CFDI = false;
        BTN_CANCELAR_CFDI = false;
        BTN_ORDENCOMPRA_CFDI = false;
        BTN_ELIMINAR_CFDI = false;
        BTN_REPORTE_CFDI = false;
        LINK_REPORTE_CFDI = false;
        VER_CFDIS_ELIMINADOS = false;
        BTN_RESTAURAR_ELIMINADO = false;
        BTN_DELETE_CFDI = false;
        BTN_REPORTE_ELIMINADOS = false;
        LINK_REPORTE_ELIMINADOS = false;
        VER_CFDIS_EMITIDOS = false;
        BTN_REPORTE_EMITIDOS = false;
        LINK_REPORTE_EMITIDOS = false;
        VER_ORDEN_COMPRA = false;
        BTN_NUEVO_ORDEN = false;
        MENU_CONFIGURACION = false;
        VER_CONFIG_EMPRESA = false;
        BTN_MODIFICAR_EMPRESA = false;
        VER_CONFIG_NOTIFICACION = false;
        BTN_MODIFICAR_NOTIFICACION = false;
        VER_CONFIG_PLANTILLA = false;
        CONFIGURAR_PLANTILLA = false;
        VER_CONFIG_EMAIL = false;
        BTN_MODIFICAR_SMTP = false;
        BTN_MODIFICAR_RECEPCION = false;
        VER_CONFIG_FTP = false;
        CONFIGURAR_FTP = false;
        VER_CONFIG_REPORTAR = false;
        CONFIGURAR_REPORTAR = false;
        VER_CONFIG_ADDENDA = false;
        CONFIGURAR_ADDENDA = false;
        VER_CONFIG_ADAPTADOR_CONECTOR = false;
        VER_SOCIOS_COMERCIALES = false;
        BTN_NUEVO_SC = false;
        BTN_VER_SC = false;
        BTN_MODIFICAR_SC = false;
        BTN_ELIMINAR_SC = false;
        VER_CONTABILIDAD_ELECTRONICA = false;
        BTN_NUEVO_CAT = false;
        BTN_NUEVO_BAL = false;
        BTN_NUEVO_POL = false;
        BTN_NUEVO_FOL = false;
        BTN_NUEVO_CUE = false;
        BTN_PROCESAR_CE = false;
        BTN_ELIMINAR_CE = false;
        VER_CONFIG_SERVICIOS = false;
        CONFIG_SERVICIOS = false;
        VER_SERV_RECEPCION = false;
        VER_SERV_VALIDACION = false;
        VER_SERV_NOTIFICACIONES = false;
        VER_SERV_ACCESO = false;
        VER_SERV_SMTP = false;
        VER_SERV_CONTABILIDAD_ELECTRONICA = false;
        VER_SERV_SINCRONIZAR = false;
        VER_SERV_CONFIG_XSA = false;
        VER_SERV_CENTINELA = false;
        VER_SERV_SCRIPT = false;
        VER_USUARIOS = false;
        ADMINISTRAR_USUARIOS = false;
        VER_ROLES = false;
        ADMINISTRAR_ROLES = false;
        VER_EMPRESAS = false;
        ADMINISTRAR_EMPRESAS = false;
        MENU_CFDIS_SC = false;
        VER_CFDIS_RECIBIDOS_SC = false;
        BTN_NUEVO_CFDI_SC = false;
        BTN_VALIDAR_CFDI_SC = false;
        BTN_CANCELAR_CFDI_SC = false;
        BTN_ELIMINAR_CFDI_SC = false;
        BTN_REPORTE_CFDI_SC = false;
        LINK_REPORTE_CFDI_SC = false;
        VER_CFDIS_ELIMINADOS_SC = false;
        BTN_REPORTE_ELIMINADOS_SC = false;
        LINK_REPORTE_ELIMINADOS_SC = false;
    }

    public boolean isVER_ESTADISTICAS_EMPRESA() {
        return VER_ESTADISTICAS_EMPRESA;
    }

    public void setVER_ESTADISTICAS_EMPRESA(boolean VER_ESTADISTICAS_EMPRESA) {
        this.VER_ESTADISTICAS_EMPRESA = VER_ESTADISTICAS_EMPRESA;
    }

    public boolean isCARGAR_LOGOS() {
        return CARGAR_LOGOS;
    }

    public void setCARGAR_LOGOS(boolean CARGAR_LOGOS) {
        this.CARGAR_LOGOS = CARGAR_LOGOS;
    }

    public boolean isVER_ESTADISTICAS_SOCIO() {
        return VER_ESTADISTICAS_SOCIO;
    }

    public void setVER_ESTADISTICAS_SOCIO(boolean VER_ESTADISTICAS_SOCIO) {
        this.VER_ESTADISTICAS_SOCIO = VER_ESTADISTICAS_SOCIO;
    }

    public boolean isMENU_CFDIS() {
        return MENU_CFDIS;
    }

    public void setMENU_CFDIS(boolean MENU_CFDIS) {
        this.MENU_CFDIS = MENU_CFDIS;
    }

    public boolean isVER_CFDIS_RECIBIDOS() {
        return VER_CFDIS_RECIBIDOS;
    }

    public void setVER_CFDIS_RECIBIDOS(boolean VER_CFDIS_RECIBIDOS) {
        this.VER_CFDIS_RECIBIDOS = VER_CFDIS_RECIBIDOS;
    }

    public boolean isBTN_NUEVO_CFDI() {
        return BTN_NUEVO_CFDI;
    }

    public void setBTN_NUEVO_CFDI(boolean BTN_NUEVO_CFDI) {
        this.BTN_NUEVO_CFDI = BTN_NUEVO_CFDI;
    }

    public boolean isBTN_VALIDAR_CFDI() {
        return BTN_VALIDAR_CFDI;
    }

    public void setBTN_VALIDAR_CFDI(boolean BTN_VALIDAR_CFDI) {
        this.BTN_VALIDAR_CFDI = BTN_VALIDAR_CFDI;
    }

    public boolean isBTN_CANCELAR_CFDI() {
        return BTN_CANCELAR_CFDI;
    }

    public void setBTN_CANCELAR_CFDI(boolean BTN_CANCELAR_CFDI) {
        this.BTN_CANCELAR_CFDI = BTN_CANCELAR_CFDI;
    }

    public boolean isBTN_ORDENCOMPRA_CFDI() {
        return BTN_ORDENCOMPRA_CFDI;
    }

    public void setBTN_ORDENCOMPRA_CFDI(boolean BTN_ORDENCOMPRA_CFDI) {
        this.BTN_ORDENCOMPRA_CFDI = BTN_ORDENCOMPRA_CFDI;
    }

    public boolean isBTN_ELIMINAR_CFDI() {
        return BTN_ELIMINAR_CFDI;
    }

    public void setBTN_ELIMINAR_CFDI(boolean BTN_ELIMINAR_CFDI) {
        this.BTN_ELIMINAR_CFDI = BTN_ELIMINAR_CFDI;
    }

    public boolean isBTN_REPORTE_CFDI() {
        return BTN_REPORTE_CFDI;
    }

    public void setBTN_REPORTE_CFDI(boolean BTN_REPORTE_CFDI) {
        this.BTN_REPORTE_CFDI = BTN_REPORTE_CFDI;
    }

    public boolean isLINK_REPORTE_CFDI() {
        return LINK_REPORTE_CFDI;
    }

    public void setLINK_REPORTE_CFDI(boolean LINK_REPORTE_CFDI) {
        this.LINK_REPORTE_CFDI = LINK_REPORTE_CFDI;
    }

    public boolean isVER_CFDIS_ELIMINADOS() {
        return VER_CFDIS_ELIMINADOS;
    }

    public void setVER_CFDIS_ELIMINADOS(boolean VER_CFDIS_ELIMINADOS) {
        this.VER_CFDIS_ELIMINADOS = VER_CFDIS_ELIMINADOS;
    }

    public boolean isBTN_RESTAURAR_ELIMINADO() {
        return BTN_RESTAURAR_ELIMINADO;
    }

    public void setBTN_RESTAURAR_ELIMINADO(boolean BTN_RESTAURAR_ELIMINADO) {
        this.BTN_RESTAURAR_ELIMINADO = BTN_RESTAURAR_ELIMINADO;
    }

    public boolean isBTN_DELETE_CFDI() {
        return BTN_DELETE_CFDI;
    }

    public void setBTN_DELETE_CFDI(boolean BTN_DELETE_CFDI) {
        this.BTN_DELETE_CFDI = BTN_DELETE_CFDI;
    }

    public boolean isBTN_REPORTE_ELIMINADOS() {
        return BTN_REPORTE_ELIMINADOS;
    }

    public void setBTN_REPORTE_ELIMINADOS(boolean BTN_REPORTE_ELIMINADOS) {
        this.BTN_REPORTE_ELIMINADOS = BTN_REPORTE_ELIMINADOS;
    }

    public boolean isLINK_REPORTE_ELIMINADOS() {
        return LINK_REPORTE_ELIMINADOS;
    }

    public void setLINK_REPORTE_ELIMINADOS(boolean LINK_REPORTE_ELIMINADOS) {
        this.LINK_REPORTE_ELIMINADOS = LINK_REPORTE_ELIMINADOS;
    }

    public boolean isVER_CFDIS_EMITIDOS() {
        return VER_CFDIS_EMITIDOS;
    }

    public void setVER_CFDIS_EMITIDOS(boolean VER_CFDIS_EMITIDOS) {
        this.VER_CFDIS_EMITIDOS = VER_CFDIS_EMITIDOS;
    }

    public boolean isBTN_REPORTE_EMITIDOS() {
        return BTN_REPORTE_EMITIDOS;
    }

    public void setBTN_REPORTE_EMITIDOS(boolean BTN_REPORTE_EMITIDOS) {
        this.BTN_REPORTE_EMITIDOS = BTN_REPORTE_EMITIDOS;
    }

    public boolean isLINK_REPORTE_EMITIDOS() {
        return LINK_REPORTE_EMITIDOS;
    }

    public void setLINK_REPORTE_EMITIDOS(boolean LINK_REPORTE_EMITIDOS) {
        this.LINK_REPORTE_EMITIDOS = LINK_REPORTE_EMITIDOS;
    }

    public boolean isVER_ORDEN_COMPRA() {
        return VER_ORDEN_COMPRA;
    }

    public void setVER_ORDEN_COMPRA(boolean VER_ORDEN_COMPRA) {
        this.VER_ORDEN_COMPRA = VER_ORDEN_COMPRA;
    }

    public boolean isBTN_NUEVO_ORDEN() {
        return BTN_NUEVO_ORDEN;
    }

    public void setBTN_NUEVO_ORDEN(boolean BTN_NUEVO_ORDEN) {
        this.BTN_NUEVO_ORDEN = BTN_NUEVO_ORDEN;
    }

    public boolean isMENU_CONFIGURACION() {
        return MENU_CONFIGURACION;
    }

    public void setMENU_CONFIGURACION(boolean MENU_CONFIGURACION) {
        this.MENU_CONFIGURACION = MENU_CONFIGURACION;
    }

    public boolean isVER_CONFIG_EMPRESA() {
        return VER_CONFIG_EMPRESA;
    }

    public void setVER_CONFIG_EMPRESA(boolean VER_CONFIG_EMPRESA) {
        this.VER_CONFIG_EMPRESA = VER_CONFIG_EMPRESA;
    }

    public boolean isBTN_MODIFICAR_EMPRESA() {
        return BTN_MODIFICAR_EMPRESA;
    }

    public void setBTN_MODIFICAR_EMPRESA(boolean BTN_MODIFICAR_EMPRESA) {
        this.BTN_MODIFICAR_EMPRESA = BTN_MODIFICAR_EMPRESA;
    }

    public boolean isVER_CONFIG_NOTIFICACION() {
        return VER_CONFIG_NOTIFICACION;
    }

    public void setVER_CONFIG_NOTIFICACION(boolean VER_CONFIG_NOTIFICACION) {
        this.VER_CONFIG_NOTIFICACION = VER_CONFIG_NOTIFICACION;
    }

    public boolean isBTN_MODIFICAR_NOTIFICACION() {
        return BTN_MODIFICAR_NOTIFICACION;
    }

    public void setBTN_MODIFICAR_NOTIFICACION(boolean BTN_MODIFICAR_NOTIFICACION) {
        this.BTN_MODIFICAR_NOTIFICACION = BTN_MODIFICAR_NOTIFICACION;
    }

    public boolean isVER_CONFIG_PLANTILLA() {
        return VER_CONFIG_PLANTILLA;
    }

    public void setVER_CONFIG_PLANTILLA(boolean VER_CONFIG_PLANTILLA) {
        this.VER_CONFIG_PLANTILLA = VER_CONFIG_PLANTILLA;
    }

    public boolean isCONFIGURAR_PLANTILLA() {
        return CONFIGURAR_PLANTILLA;
    }

    public void setCONFIGURAR_PLANTILLA(boolean CONFIGURAR_PLANTILLA) {
        this.CONFIGURAR_PLANTILLA = CONFIGURAR_PLANTILLA;
    }

    public boolean isVER_CONFIG_EMAIL() {
        return VER_CONFIG_EMAIL;
    }

    public void setVER_CONFIG_EMAIL(boolean VER_CONFIG_EMAIL) {
        this.VER_CONFIG_EMAIL = VER_CONFIG_EMAIL;
    }

    public boolean isBTN_MODIFICAR_SMTP() {
        return BTN_MODIFICAR_SMTP;
    }

    public void setBTN_MODIFICAR_SMTP(boolean BTN_MODIFICAR_SMTP) {
        this.BTN_MODIFICAR_SMTP = BTN_MODIFICAR_SMTP;
    }

    public boolean isBTN_MODIFICAR_RECEPCION() {
        return BTN_MODIFICAR_RECEPCION;
    }

    public void setBTN_MODIFICAR_RECEPCION(boolean BTN_MODIFICAR_RECEPCION) {
        this.BTN_MODIFICAR_RECEPCION = BTN_MODIFICAR_RECEPCION;
    }

    public boolean isVER_CONFIG_FTP() {
        return VER_CONFIG_FTP;
    }

    public void setVER_CONFIG_FTP(boolean VER_CONFIG_FTP) {
        this.VER_CONFIG_FTP = VER_CONFIG_FTP;
    }

    public boolean isCONFIGURAR_FTP() {
        return CONFIGURAR_FTP;
    }

    public void setCONFIGURAR_FTP(boolean CONFIGURAR_FTP) {
        this.CONFIGURAR_FTP = CONFIGURAR_FTP;
    }

    public boolean isVER_CONFIG_REPORTAR() {
        return VER_CONFIG_REPORTAR;
    }

    public void setVER_CONFIG_REPORTAR(boolean VER_CONFIG_REPORTAR) {
        this.VER_CONFIG_REPORTAR = VER_CONFIG_REPORTAR;
    }

    public boolean isCONFIGURAR_REPORTAR() {
        return CONFIGURAR_REPORTAR;
    }

    public void setCONFIGURAR_REPORTAR(boolean CONFIGURAR_REPORTAR) {
        this.CONFIGURAR_REPORTAR = CONFIGURAR_REPORTAR;
    }

    public boolean isVER_CONFIG_ADDENDA() {
        return VER_CONFIG_ADDENDA;
    }

    public void setVER_CONFIG_ADDENDA(boolean VER_CONFIG_ADDENDA) {
        this.VER_CONFIG_ADDENDA = VER_CONFIG_ADDENDA;
    }

    public boolean isCONFIGURAR_ADDENDA() {
        return CONFIGURAR_ADDENDA;
    }

    public void setCONFIGURAR_ADDENDA(boolean CONFIGURAR_ADDENDA) {
        this.CONFIGURAR_ADDENDA = CONFIGURAR_ADDENDA;
    }

    public boolean isVER_CONFIG_ADAPTADOR_CONECTOR() {
        return VER_CONFIG_ADAPTADOR_CONECTOR;
    }

    public void setVER_CONFIG_ADAPTADOR_CONECTOR(boolean VER_CONFIG_ADAPTADOR_CONECTOR) {
        this.VER_CONFIG_ADAPTADOR_CONECTOR = VER_CONFIG_ADAPTADOR_CONECTOR;
    }

    public boolean isVER_SOCIOS_COMERCIALES() {
        return VER_SOCIOS_COMERCIALES;
    }

    public void setVER_SOCIOS_COMERCIALES(boolean VER_SOCIOS_COMERCIALES) {
        this.VER_SOCIOS_COMERCIALES = VER_SOCIOS_COMERCIALES;
    }

    public boolean isBTN_NUEVO_SC() {
        return BTN_NUEVO_SC;
    }

    public void setBTN_NUEVO_SC(boolean BTN_NUEVO_SC) {
        this.BTN_NUEVO_SC = BTN_NUEVO_SC;
    }

    public boolean isBTN_VER_SC() {
        return BTN_VER_SC;
    }

    public void setBTN_VER_SC(boolean BTN_VER_SC) {
        this.BTN_VER_SC = BTN_VER_SC;
    }

    public boolean isBTN_MODIFICAR_SC() {
        return BTN_MODIFICAR_SC;
    }

    public void setBTN_MODIFICAR_SC(boolean BTN_MODIFICAR_SC) {
        this.BTN_MODIFICAR_SC = BTN_MODIFICAR_SC;
    }

    public boolean isBTN_ELIMINAR_SC() {
        return BTN_ELIMINAR_SC;
    }

    public void setBTN_ELIMINAR_SC(boolean BTN_ELIMINAR_SC) {
        this.BTN_ELIMINAR_SC = BTN_ELIMINAR_SC;
    }

    public boolean isVER_CONTABILIDAD_ELECTRONICA() {
        return VER_CONTABILIDAD_ELECTRONICA;
    }

    public void setVER_CONTABILIDAD_ELECTRONICA(boolean VER_CONTABILIDAD_ELECTRONICA) {
        this.VER_CONTABILIDAD_ELECTRONICA = VER_CONTABILIDAD_ELECTRONICA;
    }

    public boolean isBTN_NUEVO_CAT() {
        return BTN_NUEVO_CAT;
    }

    public void setBTN_NUEVO_CAT(boolean BTN_NUEVO_CAT) {
        this.BTN_NUEVO_CAT = BTN_NUEVO_CAT;
    }

    public boolean isBTN_NUEVO_BAL() {
        return BTN_NUEVO_BAL;
    }

    public void setBTN_NUEVO_BAL(boolean BTN_NUEVO_BAL) {
        this.BTN_NUEVO_BAL = BTN_NUEVO_BAL;
    }

    public boolean isBTN_NUEVO_POL() {
        return BTN_NUEVO_POL;
    }

    public void setBTN_NUEVO_POL(boolean BTN_NUEVO_POL) {
        this.BTN_NUEVO_POL = BTN_NUEVO_POL;
    }

    public boolean isBTN_NUEVO_FOL() {
        return BTN_NUEVO_FOL;
    }

    public void setBTN_NUEVO_FOL(boolean BTN_NUEVO_FOL) {
        this.BTN_NUEVO_FOL = BTN_NUEVO_FOL;
    }

    public boolean isBTN_NUEVO_CUE() {
        return BTN_NUEVO_CUE;
    }

    public void setBTN_NUEVO_CUE(boolean BTN_NUEVO_CUE) {
        this.BTN_NUEVO_CUE = BTN_NUEVO_CUE;
    }

    public boolean isBTN_PROCESAR_CE() {
        return BTN_PROCESAR_CE;
    }

    public void setBTN_PROCESAR_CE(boolean BTN_PROCESAR_CE) {
        this.BTN_PROCESAR_CE = BTN_PROCESAR_CE;
    }

    public boolean isBTN_ELIMINAR_CE() {
        return BTN_ELIMINAR_CE;
    }

    public void setBTN_ELIMINAR_CE(boolean BTN_ELIMINAR_CE) {
        this.BTN_ELIMINAR_CE = BTN_ELIMINAR_CE;
    }

    public boolean isVER_CONFIG_SERVICIOS() {
        return VER_CONFIG_SERVICIOS;
    }

    public void setVER_CONFIG_SERVICIOS(boolean VER_CONFIG_SERVICIOS) {
        this.VER_CONFIG_SERVICIOS = VER_CONFIG_SERVICIOS;
    }

    public boolean isCONFIG_SERVICIOS() {
        return CONFIG_SERVICIOS;
    }

    public void setCONFIG_SERVICIOS(boolean CONFIG_SERVICIOS) {
        this.CONFIG_SERVICIOS = CONFIG_SERVICIOS;
    }

    public boolean isVER_SERV_RECEPCION() {
        return VER_SERV_RECEPCION;
    }

    public void setVER_SERV_RECEPCION(boolean VER_SERV_RECEPCION) {
        this.VER_SERV_RECEPCION = VER_SERV_RECEPCION;
    }

    public boolean isVER_SERV_VALIDACION() {
        return VER_SERV_VALIDACION;
    }

    public void setVER_SERV_VALIDACION(boolean VER_SERV_VALIDACION) {
        this.VER_SERV_VALIDACION = VER_SERV_VALIDACION;
    }

    public boolean isVER_SERV_NOTIFICACIONES() {
        return VER_SERV_NOTIFICACIONES;
    }

    public void setVER_SERV_NOTIFICACIONES(boolean VER_SERV_NOTIFICACIONES) {
        this.VER_SERV_NOTIFICACIONES = VER_SERV_NOTIFICACIONES;
    }

    public boolean isVER_SERV_ACCESO() {
        return VER_SERV_ACCESO;
    }

    public void setVER_SERV_ACCESO(boolean VER_SERV_ACCESO) {
        this.VER_SERV_ACCESO = VER_SERV_ACCESO;
    }

    public boolean isVER_SERV_SMTP() {
        return VER_SERV_SMTP;
    }

    public void setVER_SERV_SMTP(boolean VER_SERV_SMTP) {
        this.VER_SERV_SMTP = VER_SERV_SMTP;
    }

    public boolean isVER_SERV_CONTABILIDAD_ELECTRONICA() {
        return VER_SERV_CONTABILIDAD_ELECTRONICA;
    }

    public void setVER_SERV_CONTABILIDAD_ELECTRONICA(boolean VER_SERV_CONTABILIDAD_ELECTRONICA) {
        this.VER_SERV_CONTABILIDAD_ELECTRONICA = VER_SERV_CONTABILIDAD_ELECTRONICA;
    }

    public boolean isVER_SERV_SINCRONIZAR() {
        return VER_SERV_SINCRONIZAR;
    }

    public void setVER_SERV_SINCRONIZAR(boolean VER_SERV_SINCRONIZAR) {
        this.VER_SERV_SINCRONIZAR = VER_SERV_SINCRONIZAR;
    }

    public boolean isVER_SERV_CONFIG_XSA() {
        return VER_SERV_CONFIG_XSA;
    }

    public void setVER_SERV_CONFIG_XSA(boolean VER_SERV_CONFIG_XSA) {
        this.VER_SERV_CONFIG_XSA = VER_SERV_CONFIG_XSA;
    }

    public boolean isVER_SERV_CENTINELA() {
        return VER_SERV_CENTINELA;
    }

    public void setVER_SERV_CENTINELA(boolean VER_SERV_CENTINELA) {
        this.VER_SERV_CENTINELA = VER_SERV_CENTINELA;
    }

    public boolean isVER_SERV_SCRIPT() {
        return VER_SERV_SCRIPT;
    }

    public void setVER_SERV_SCRIPT(boolean VER_SERV_SCRIPT) {
        this.VER_SERV_SCRIPT = VER_SERV_SCRIPT;
    }

    public boolean isVER_USUARIOS() {
        return VER_USUARIOS;
    }

    public void setVER_USUARIOS(boolean VER_USUARIOS) {
        this.VER_USUARIOS = VER_USUARIOS;
    }

    public boolean isADMINISTRAR_USUARIOS() {
        return ADMINISTRAR_USUARIOS;
    }

    public void setADMINISTRAR_USUARIOS(boolean ADMINISTRAR_USUARIOS) {
        this.ADMINISTRAR_USUARIOS = ADMINISTRAR_USUARIOS;
    }

    public boolean isVER_ROLES() {
        return VER_ROLES;
    }

    public void setVER_ROLES(boolean VER_ROLES) {
        this.VER_ROLES = VER_ROLES;
    }

    public boolean isADMINISTRAR_ROLES() {
        return ADMINISTRAR_ROLES;
    }

    public void setADMINISTRAR_ROLES(boolean ADMINISTRAR_ROLES) {
        this.ADMINISTRAR_ROLES = ADMINISTRAR_ROLES;
    }

    public boolean isVER_EMPRESAS() {
        return VER_EMPRESAS;
    }

    public void setVER_EMPRESAS(boolean VER_EMPRESAS) {
        this.VER_EMPRESAS = VER_EMPRESAS;
    }

    public boolean isADMINISTRAR_EMPRESAS() {
        return ADMINISTRAR_EMPRESAS;
    }

    public void setADMINISTRAR_EMPRESAS(boolean ADMINISTRAR_EMPRESAS) {
        this.ADMINISTRAR_EMPRESAS = ADMINISTRAR_EMPRESAS;
    }

    public boolean isMENU_CFDIS_SC() {
        return MENU_CFDIS_SC;
    }

    public void setMENU_CFDIS_SC(boolean MENU_CFDIS_SC) {
        this.MENU_CFDIS_SC = MENU_CFDIS_SC;
    }

    public boolean isVER_CFDIS_RECIBIDOS_SC() {
        return VER_CFDIS_RECIBIDOS_SC;
    }

    public void setVER_CFDIS_RECIBIDOS_SC(boolean VER_CFDIS_RECIBIDOS_SC) {
        this.VER_CFDIS_RECIBIDOS_SC = VER_CFDIS_RECIBIDOS_SC;
    }

    public boolean isBTN_NUEVO_CFDI_SC() {
        return BTN_NUEVO_CFDI_SC;
    }

    public void setBTN_NUEVO_CFDI_SC(boolean BTN_NUEVO_CFDI_SC) {
        this.BTN_NUEVO_CFDI_SC = BTN_NUEVO_CFDI_SC;
    }

    public boolean isBTN_VALIDAR_CFDI_SC() {
        return BTN_VALIDAR_CFDI_SC;
    }

    public void setBTN_VALIDAR_CFDI_SC(boolean BTN_VALIDAR_CFDI_SC) {
        this.BTN_VALIDAR_CFDI_SC = BTN_VALIDAR_CFDI_SC;
    }

    public boolean isBTN_CANCELAR_CFDI_SC() {
        return BTN_CANCELAR_CFDI_SC;
    }

    public void setBTN_CANCELAR_CFDI_SC(boolean BTN_CANCELAR_CFDI_SC) {
        this.BTN_CANCELAR_CFDI_SC = BTN_CANCELAR_CFDI_SC;
    }

    public boolean isBTN_REPORTE_CFDI_SC() {
        return BTN_REPORTE_CFDI_SC;
    }

    public void setBTN_REPORTE_CFDI_SC(boolean BTN_REPORTE_CFDI_SC) {
        this.BTN_REPORTE_CFDI_SC = BTN_REPORTE_CFDI_SC;
    }

    public boolean isLINK_REPORTE_CFDI_SC() {
        return LINK_REPORTE_CFDI_SC;
    }

    public void setLINK_REPORTE_CFDI_SC(boolean LINK_REPORTE_CFDI_SC) {
        this.LINK_REPORTE_CFDI_SC = LINK_REPORTE_CFDI_SC;
    }

    public boolean isVER_CFDIS_ELIMINADOS_SC() {
        return VER_CFDIS_ELIMINADOS_SC;
    }

    public void setVER_CFDIS_ELIMINADOS_SC(boolean VER_CFDIS_ELIMINADOS_SC) {
        this.VER_CFDIS_ELIMINADOS_SC = VER_CFDIS_ELIMINADOS_SC;
    }

    public boolean isBTN_REPORTE_ELIMINADOS_SC() {
        return BTN_REPORTE_ELIMINADOS_SC;
    }

    public void setBTN_REPORTE_ELIMINADOS_SC(boolean BTN_REPORTE_ELIMINADOS_SC) {
        this.BTN_REPORTE_ELIMINADOS_SC = BTN_REPORTE_ELIMINADOS_SC;
    }

    public boolean isLINK_REPORTE_ELIMINADOS_SC() {
        return LINK_REPORTE_ELIMINADOS_SC;
    }

    public void setLINK_REPORTE_ELIMINADOS_SC(boolean LINK_REPORTE_ELIMINADOS_SC) {
        this.LINK_REPORTE_ELIMINADOS_SC = LINK_REPORTE_ELIMINADOS_SC;
    }

    public boolean isBTN_ELIMINAR_CFDI_SC() {
        return BTN_ELIMINAR_CFDI_SC;
    }

    public void setBTN_ELIMINAR_CFDI_SC(boolean BTN_ELIMINAR_CFDI_SC) {
        this.BTN_ELIMINAR_CFDI_SC = BTN_ELIMINAR_CFDI_SC;
    }

}
