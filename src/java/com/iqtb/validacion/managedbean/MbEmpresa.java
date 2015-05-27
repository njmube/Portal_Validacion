/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConectorAdaptador;
import com.iqtb.validacion.dao.DaoConfiguracionEmpresa;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoPlantilla;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.ConfigEmpresa;
import static com.iqtb.validacion.encrypt.Encrypt.encrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.mail.Email;
import com.iqtb.validacion.pojo.ConectorAdaptador;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Plantillas;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.Imagen;
import com.iqtb.validacion.util.ManejoArchivos;
import com.iqtb.validacion.util.Subreporte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import net.sourceforge.lightcrypto.CryptoException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbEmpresa implements Serializable {

    private Empresas empresa;
    private List<Empresas> listaEmpresas;
    private List<Empresas> empresasSeleccionadas;
    private Usuarios usuario;
    private boolean xmlValido;
    private boolean xmlInvalido;
    private boolean validoEmail;
    private boolean invalidoEmail;
    private List<String> emails;
    private String newEmail;
    private String configValor;
    private String strHostSMTP;
    private String strPortSMTP;
    private String strNombreSMTP;
    private String strEmailSMTP;
    private String strUserSMTP;
    private String strPassSMTP;
    private String strEmailCodigo;
    private boolean sslSMTP;
    private String passEncryptSMTP;
    private String hash;
    private String hashSMTP;
    private boolean connectSMTP;
    private boolean confirSMTP;
    private String strTipoServidor;
    private String strservidorRecepcion;
    private String strPuertoRecepcion;
    private String strUsuarioRecepcion;
    private String strPassRecepcion;
    private boolean sslRecepcion;
    private String passEncRecepcion;
    private boolean confirmPOP;
    private String msgSMTP;
    private String msgPOP;
    private final String sessionUsuario;
    private FacesMessage msg;
    private String descBitacora;
    private ConfiguracionesEmpresas configEmpresa;
    private ConectorAdaptador conectorAdaptador;
    private DaoConectorAdaptador daoConectorAdaptador;
    private DaoConfiguracionEmpresa daoConfigEmpresa;
    private DaoEmpresa daoEmpresa;
    private String userFtpV;
    private String passFtpV;
    private String confirmFtpV;
    private String enPassFtpV;
    private boolean existFtp;
    private String hostFtp;
    private String adaptadorBal;
    private String conectorBal;
    private String adaptadorCat;
    private String conectorCat;
    private String adaptadorPol;
    private String conectorPol;
    private String adaptadorFolios;
    private String conectorFolios;
    private String adaptadorCuentas;
    private String conectorCuentas;
    private final String destino = "/work/iqtb/validacionfiles/plantillas/tmp/";
    private String carpeta;
    private String principal;
    private String nombrePlantilla;
    private List<Subreporte> listaSubreportes;
    private Subreporte subreporteSeleccionado;
    private List<Imagen> listaImagenes;
    private Plantillas plantilla;
    private DaoPlantilla daoPlantilla;
    private boolean guardarPlantilla;
    private static final Logger logger = Logger.getLogger(MbEmpresa.class);

    public MbEmpresa() {
        this.empresa = new Empresas();
        this.listaEmpresas = new ArrayList<Empresas>();
        this.empresasSeleccionadas = new ArrayList<Empresas>();
        xmlValido = false;
        xmlInvalido = false;
        validoEmail = false;
        invalidoEmail = false;
        newEmail = "";
        emails = new ArrayList<String>();
        confirSMTP = false;
        connectSMTP = false;
        confirmPOP = false;
        msgSMTP = "";
        msgPOP = "";
        configEmpresa = new ConfiguracionesEmpresas();
        conectorAdaptador = new ConectorAdaptador();
        daoConectorAdaptador = new DaoConectorAdaptador();
        daoConfigEmpresa = new DaoConfiguracionEmpresa();
        daoEmpresa = new DaoEmpresa();
        existFtp = false;
        hostFtp = "";
        userFtpV = "";
        enPassFtpV = "";
        principal = "";
        nombrePlantilla = "";
        carpeta = "";
        listaSubreportes = new ArrayList<Subreporte>();
        subreporteSeleccionado = new Subreporte();
        listaImagenes = new ArrayList<Imagen>();
        plantilla = new Plantillas();
        daoPlantilla = new DaoPlantilla();
        guardarPlantilla = false;
        configValor = "";

        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();

        try {
            this.usuario = new DaoUsuario().getByUserid(sessionUsuario);
        } catch (Exception e) {
            descBitacora = "MbEmpresa ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("Error al obtener el Usuario ERROR " + e);
        }
    }

//    @PostConstruct
//    public void init() {
//        try {
//            listaEmpresas = new DaoEmpresa().getEmpresas();
//        } catch (Exception e) {
//            logger.error("init - ERROR " + e);
//        }
//    }
    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public List<Empresas> getListaEmpresas() {
        try {
            listaEmpresas = daoEmpresa.getEmpresas();
        } catch (Exception e) {
            descBitacora = usuario.getUserid() + ". getListaEmpresas - ERROR " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". getListaEmpresas - ERROR " + e);
        }
        return listaEmpresas;
    }

    public void setListaEmpresas(List<Empresas> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public List<Empresas> getEmpresasSeleccionadas() {
        return empresasSeleccionadas;
    }

    public void setEmpresasSeleccionadas(List<Empresas> empresasSeleccionadas) {
        this.empresasSeleccionadas = empresasSeleccionadas;
    }

    public boolean isXmlValido() {
        return xmlValido;
    }

    public void setXmlValido(boolean xmlValido) {
        this.xmlValido = xmlValido;
    }

    public boolean isXmlInvalido() {
        return xmlInvalido;
    }

    public void setXmlInvalido(boolean xmlInvalido) {
        this.xmlInvalido = xmlInvalido;
    }

    public boolean isValidoEmail() {
        return validoEmail;
    }

    public void setValidoEmail(boolean validoEmail) {
        this.validoEmail = validoEmail;
    }

    public boolean isInvalidoEmail() {
        return invalidoEmail;
    }

    public void setInvalidoEmail(boolean invalidoEmail) {
        this.invalidoEmail = invalidoEmail;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getStrHostSMTP() {
        return strHostSMTP;
    }

    public void setStrHostSMTP(String strHostSMTP) {
        this.strHostSMTP = strHostSMTP;
    }

    public String getStrPortSMTP() {
        return strPortSMTP;
    }

    public void setStrPortSMTP(String strPortSMTP) {
        this.strPortSMTP = strPortSMTP;
    }

    public String getStrEmailSMTP() {
        return strEmailSMTP;
    }

    public void setStrEmailSMTP(String strEmailSMTP) {
        this.strEmailSMTP = strEmailSMTP;
    }

    public String getStrUserSMTP() {
        return strUserSMTP;
    }

    public void setStrUserSMTP(String strUserSMTP) {
        this.strUserSMTP = strUserSMTP;
    }

    public String getStrPassSMTP() {
        return strPassSMTP;
    }

    public void setStrPassSMTP(String strPassSMTP) {
        this.strPassSMTP = strPassSMTP;
    }

    public String getStrEmailCodigo() {
        return strEmailCodigo;
    }

    public void setStrEmailCodigo(String strEmailCodigo) {
        this.strEmailCodigo = strEmailCodigo;
    }

    public String getStrNombreSMTP() {
        return strNombreSMTP;
    }

    public void setStrNombreSMTP(String strNombreSMTP) {
        this.strNombreSMTP = strNombreSMTP;
    }

    public boolean isSslSMTP() {
        return sslSMTP;
    }

    public void setSslSMTP(boolean sslSMTP) {
        this.sslSMTP = sslSMTP;
    }

    public String getHashSMTP() {
        return hashSMTP;
    }

    public void setHashSMTP(String hashSMTP) {
        this.hashSMTP = hashSMTP;
    }

    public boolean isConnectSMTP() {
        return connectSMTP;
    }

    public void setConnectSMTP(boolean connectSMTP) {
        this.connectSMTP = connectSMTP;
    }

    public String getStrTipoServidor() {
        return strTipoServidor;
    }

    public void setStrTipoServidor(String strTipoServidor) {
        this.strTipoServidor = strTipoServidor;
    }

    public String getStrservidorRecepcion() {
        return strservidorRecepcion;
    }

    public void setStrservidorRecepcion(String strservidorRecepcion) {
        this.strservidorRecepcion = strservidorRecepcion;
    }

    public String getStrPuertoRecepcion() {
        return strPuertoRecepcion;
    }

    public void setStrPuertoRecepcion(String strPuertoRecepcion) {
        this.strPuertoRecepcion = strPuertoRecepcion;
    }

    public String getStrUsuarioRecepcion() {
        return strUsuarioRecepcion;
    }

    public void setStrUsuarioRecepcion(String strUsuarioRecepcion) {
        this.strUsuarioRecepcion = strUsuarioRecepcion;
    }

    public String getStrPassRecepcion() {
        return strPassRecepcion;
    }

    public void setStrPassRecepcion(String strPassRecepcion) {
        this.strPassRecepcion = strPassRecepcion;
    }

    public boolean isSslRecepcion() {
        return sslRecepcion;
    }

    public void setSslRecepcion(boolean sslRecepcion) {
        this.sslRecepcion = sslRecepcion;
    }

    public String getConfigValor() {
        return configValor;
    }

    public void setConfigValor(String configValor) {
        this.configValor = configValor;
    }

    public boolean isConfirmPOP() {
        return confirmPOP;
    }

    public void setConfirmPOP(boolean confirmPOP) {
        this.confirmPOP = confirmPOP;
    }

    public String getMsgSMTP() {
        return msgSMTP;
    }

    public void setMsgSMTP(String msgSMTP) {
        this.msgSMTP = msgSMTP;
    }

    public String getMsgPOP() {
        return msgPOP;
    }

    public void setMsgPOP(String msgPOP) {
        this.msgPOP = msgPOP;
    }

    public String getUserFtpV() {
        return userFtpV;
    }

    public void setUserFtpV(String userFtpV) {
        this.userFtpV = userFtpV;
    }

    public String getPassFtpV() {
        return passFtpV;
    }

    public void setPassFtpV(String passFtpV) {
        this.passFtpV = passFtpV;
    }

    public String getConfirmFtpV() {
        return confirmFtpV;
    }

    public void setConfirmFtpV(String confirmFtpV) {
        this.confirmFtpV = confirmFtpV;
    }

    public boolean isExistFtp() {
        return existFtp;
    }

    public void setExistFtp(boolean existFtp) {
        this.existFtp = existFtp;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getNombrePlantilla() {
        return nombrePlantilla;
    }

    public void setNombrePlantilla(String nombrePlantilla) {
        this.nombrePlantilla = nombrePlantilla;
    }

    public List<Subreporte> getListaSubreportes() {
        return listaSubreportes;
    }

    public void setListaSubreportes(List<Subreporte> listaSubreportes) {
        this.listaSubreportes = listaSubreportes;
    }

    public List<Imagen> getListaImagenes() {
        return listaImagenes;
    }

    public void setListaImagenes(List<Imagen> listaImagenes) {
        this.listaImagenes = listaImagenes;
    }

    public Subreporte getSubreporteSeleccionado() {
        return subreporteSeleccionado;
    }

    public void setSubreporteSeleccionado(Subreporte subreporteSeleccionado) {
        this.subreporteSeleccionado = subreporteSeleccionado;
    }

    public boolean isGuardarPlantilla() {
        return guardarPlantilla;
    }

    public void setGuardarPlantilla(boolean guardarPlantilla) {
        this.guardarPlantilla = guardarPlantilla;
    }

    public void resetEmpresa() {
        this.empresa = new Empresas();
    }

    public boolean insertEmpresa() {
        boolean insert = false;

        try {
            if (empresa.getNombre() != null && !empresa.getNombre().trim().isEmpty()) {
                if (empresa.getRfc() != null && !empresa.getRfc().trim().isEmpty()) {
                    if (empresa.getCalle() != null && !empresa.getCalle().trim().isEmpty()) {
                        if (daoEmpresa.getEmpresaByRFC(empresa.getRfc()) == null) {
                            insert = true;
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa correctos");
//                            insert = daoEmpresa.updateEmpresa(this.empresa);
//                            if (insert) {
//                                insertConfigEmpresa();
//                                String descripcion = "[EMPRESA] Usuario: " + this.usuario.getUserid() + " registro la Empresa " + this.empresa.getRfc() + ".";
//                                registrarBitacora(this.usuario.getIdUsuario(), 4, this.empresa.getIdEmpresa(), descripcion, "INFO");
//
//                                logger.info("insertEmpresa - " + this.usuario.getUserid() + " registro la Empresa " + this.empresa.getRfc());
//                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa han sido Registrados.");
//
//                            } else {
//                                logger.error("insertEmpresa - Error al registar los datos de la Empresa " + this.empresa.getRfc());
//                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registar los datos de la Empresa.");
//                            }
                        } else {
                            logger.warn("insertEmpresa - Ya se encuetra registrado este RFC " + empresa.getRfc());
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "RFC " + empresa.getRfc() + " ya se encuentra registrado.");
                        }
                    } else {
                        logger.warn("insertEmpresa - Calle de la Empresa es requerido");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                    }
                } else {
                    logger.warn("insertEmpresa - RFC de la Empresa es requerido");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para RFC.");
                }
            } else {
                logger.warn("insertEmpresa - Nombre de la Empresa es requerido");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
            }

        } catch (Exception e) {
            descBitacora = usuario.getUserid() + ". insertEmpresa - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". insertEmpresa - ERROR: " + e);
        }

//        RequestContext context = RequestContext.getCurrentInstance();
//        context.addCallbackParam("insert", insert);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return insert;
    }

    public void updateEmpresa() {
        boolean updateEmpresa = false;

        try {
            if (empresa.getNombre() != null && !empresa.getNombre().trim().isEmpty()) {
                if (empresa.getRfc() != null && !empresa.getRfc().trim().isEmpty()) {
                    if (empresa.getCalle() != null && !empresa.getCalle().trim().isEmpty()) {
                        updateEmpresa = daoEmpresa.updateEmpresa(this.empresa);
                        if (updateEmpresa) {
                            descBitacora = "[EMPRESA] Usuario: " + usuario.getUserid() + " modifico los datos de la Empresa " + empresa.getRfc() + ".";
                            registrarBitacora(this.usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());

                            logger.info(usuario.getUserid() + ". updateEmpresa - " + usuario.getUserid() + " ha modificado los datos de la Empresa " + empresa.getRfc());
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa han sido Modificados.");

                        } else {
                            descBitacora = usuario.getUserid() + ". updateEmpresa - Error al modificar los datos de la Empresa " + empresa.getRfc();
                            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                            logger.error(usuario.getUserid() + ". updateEmpresa - Error al modificar los datos de la Empresa " + empresa.getRfc());
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos de la Empresa.");
                        }

                    } else {
                        logger.warn("updateEmpresa - Calle de la Empresa es requerido");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                    }
                } else {
                    logger.warn("updateEmpresa - RFC de la Empresa es requerido");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para RFC.");
                }
            } else {
                logger.warn("updateEmpresa - Nombre de la Empresa es requerido");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
            }

        } catch (Exception e) {
            descBitacora = "[EMPRESA] updateEmpresa - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". updateEmpresa - ERROR: " + e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", updateEmpresa);
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void deleteEmpresas() {
        boolean delete = false;
        for (Empresas emp : empresasSeleccionadas) {
            this.empresa = emp;
            try {
                delete = daoEmpresa.deleteEmpresa(empresa);
                if (delete) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La Empresa " + empresa.getRfc() + " ha sido eliminada");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    descBitacora = usuario.getUserid() + ". deleteEmpresas - Empresa " + empresa.getRfc() + " ha sido eliminada";
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                    logger.info(usuario.getUserid() + ". deleteEmpresas - Empresa " + empresa.getRfc() + " ha sido eliminada");
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede eliminar la Empresa " + empresa.getRfc());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    descBitacora = usuario.getUserid() + ". deleteEmpresas - Error al eliminar la Empresa " + empresa.getRfc();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". deleteEmpresas - Error al eliminar la Empresa " + empresa.getRfc());
                }
            } catch (Exception e) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ha ocurrido un error al intentar eliminar la Empresa " + empresa.getRfc());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                descBitacora = usuario.getUserid() + ". deleteEmpresas - ERROR " + e.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". deleteEmpresas - ERROR " + e);
            }
        }
    }

    public String insertNewEmail() {
        String str = "";

        try {

            if (this.newEmail != null && !this.newEmail.trim().isEmpty()) {
                if (this.emails.contains(this.newEmail)) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Duplicado", "Dirección de correo electrónico ya está registrada.");
                    logger.warn(usuario.getUserid() + ". inserterNewEmail - dirección de correo electrónico ya existe en la lista " + newEmail);
                } else {
                    this.emails.add(this.newEmail);
                    logger.info(usuario.getUserid() + ". inserterNewEmail - dirección de correo electrónico agregado a la lista " + newEmail);
                    this.newEmail = "";
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Dirección de correo electrónico ha sido agradada.");
                    int i = 0;
                    for (String string : emails) {
                        if (i == 0) {
                            str += string;
                        } else {
                            str += "," + string;
                        }
                        i++;
                    }
                    this.configValor = str.trim();
                    logger.info(usuario.getUserid() + ". String con email: " + this.configValor);
                }
            } else {
                logger.warn(usuario.getUserid() + ". inserterNewEmail - debe insertar un email valido");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese una dirección de correo.");
            }
        } catch (Exception e) {
            descBitacora = "[EMPRESA] insertNewEmail - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al agregar la dirección de correo.");
            logger.error(usuario.getUserid() + ". inserterNewEmail - ERROR: " + e);
        }
        newEmail = "";
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return this.configValor;
    }

    public String removeEmail() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String email = (String) params.get("emailRemove");
        String str = "";
        this.emails.remove(email);
        logger.info(usuario.getUserid() + ". removeEmail - dirección de correo electrónico ha sido removida de la lista " + email);
        int i = 0;
        for (String string : emails) {
            if (i == 0) {
                str += string;
            } else {
                str += "," + string;
            }
            i++;
        }
        this.configValor = str.trim();

        return this.configValor;
    }

    public void conexionSMTP() {
        String codigo;
//        logger.info("conexionSMTP");
        try {
            if (strHostSMTP != null && !strHostSMTP.isEmpty()) {
                if (strPortSMTP != null && !strPortSMTP.isEmpty()) {
                    if (strEmailSMTP != null && !strEmailSMTP.isEmpty()) {
                        if (strEmailCodigo != null && !strEmailCodigo.isEmpty()) {
                            codigo = strHostSMTP + strPortSMTP;
                            logger.info("encrypt contraseña " + strPassSMTP);
                            passEncryptSMTP = encrypt(strPassSMTP);
                            logger.info(usuario.getUserid() + ". conexionSMTP - Generando codigo de confirmación");
                            hash = encrypt(codigo);
                            logger.info(usuario.getUserid() + ". conexionSMTP - Encryptando codigo de confirmación");
                            String asunto = "Prueba de conexión SMTP";
                            String contenido = "Código de confirmación: " + hash;
                            ConexionSMTP conSMTP = new ConexionSMTP(strHostSMTP, strPortSMTP, sslSMTP, true);
                            conSMTP.setUsername(strEmailSMTP);
                            conSMTP.setPassword(strPassSMTP);
                            conSMTP.getSession();
                            conSMTP.connect();
                            conSMTP.createMessage(strEmailCodigo, asunto, contenido, false);
                            String conn;
                            conn = conSMTP.sendMessage();
                            logger.info(usuario.getUserid() + ". Respuesta servidor SMTP " + conn);
                            conSMTP.closeConnection();
                            if (conn.trim().charAt(0) == '2') {
                                connectSMTP = true;
                                descBitacora = usuario.getUserid() + ". conexionSMTP - Prueba de Conexion SMTP exitosa. Código de confirmación fue enviado a " + strEmailCodigo;
                                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                                logger.info(usuario.getUserid() + ". conexionSMTP - Prueba de Conexion SMTP exitosa. Código de confirmación fue enviado a " + strEmailCodigo);
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Prueba de conexión exitosa. Por favor, ingrese el código de confirmación.");
                            } else {
                                descBitacora = usuario.getUserid() + ". conexionSMTP - Prueba de Conexion SMTP Fallida.";
                                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                                logger.error(usuario.getUserid() + ". conexionSMTP - Prueba de Conexion SMTP Fallida.");
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Falló. Por favor, revise los datos.");
                            }
                        } else {
                            logger.warn("conexionSMTP - Por favor, ingrese un valor para Enviar código a");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Por favor, ingrese un valor para Enviar código a.");
                        }
                    } else {
                        logger.warn("conexionSMTP - Por favor, ingrese un valor para De");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Por favor, ingrese un valor para De.");
                    }
                } else {
                    logger.warn("conexionSMTP - Por favor, ingrese un valor para Puerto");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Por favor, ingrese un valor para Puerto.");
                }
            } else {
                logger.warn("conexionSMTP - Por favor, ingrese un valor para Servidor");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Por favor, ingrese un valor para Servidor.");
            }
        } catch (CryptoException e) {
            descBitacora = "[EMPRESA] conexionSMTP - CryptoException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". conexionSMTP - ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + e.getMessage());
        } catch (IOException e) {
            descBitacora = "[EMPRESA] conexionSMTP - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". conexionSMTP - ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + e.getMessage());
        } catch (MessagingException ex) {
            descBitacora = "[EMPRESA] conexionSMTP - ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + ex.getMessage());
            logger.error(usuario.getUserid() + ". conexionSMTP - MessagingException ERROR: " + ex);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void confirmSMTP() {

        try {
            if (hashSMTP != null && !hashSMTP.trim().equals("")) {
                if (hashSMTP.equals(hash)) {
                    logger.info(usuario.getUserid() + ". updateSMTP - " + usuario.getUserid() + " Código de confirmacion es correcto");
                    confirSMTP = true;
                    msgSMTP = "Servidor SMTP configurado correctamente.";
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Código de confirmacion es correcto.");
                } else {
                    logger.warn("Error código de confirmación no es válido.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error código de confirmación no es válido.");
                }
            } else {
                logger.info(usuario.getUserid() + ". introduzca el código de confirmación.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese el código de confirmación.");
            }

        } catch (Exception e) {
            descBitacora = "[EMPRESA] confirmSMTP - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar las configuraciones para le servidor SMTP.");
            logger.error(usuario.getUserid() + ". confirmSMTP - ERROR: " + e);
        }
        this.hashSMTP = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("smtp", confirSMTP);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void cancelarSMTP() {
        connectSMTP = false;
        confirSMTP = false;
        msgSMTP = "";
    }

    public String close() {
        xmlValido = false;
        xmlInvalido = false;
        validoEmail = false;
        invalidoEmail = false;
        emails = new ArrayList<String>();
        configValor = "";
        strHostSMTP = "";
        strPortSMTP = "";
        strNombreSMTP = "";
        strEmailSMTP = "";
        strUserSMTP = "";
        strPassSMTP = "";
        strEmailCodigo = "";
        sslSMTP = false;
        connectSMTP = false;
        confirSMTP = false;
        msgSMTP = "";
        strTipoServidor = "";
        strservidorRecepcion = "";
        strPuertoRecepcion = "";
        strUsuarioRecepcion = "";
        strPassRecepcion = "";
        sslRecepcion = false;
        confirmPOP = false;
        msgPOP = "";
        userFtpV = "";
        passFtpV = "";
        confirmFtpV = "";
        existFtp = false;
        enPassFtpV = "";
        hostFtp = "";
        adaptadorBal = null;
        conectorBal = null;
        adaptadorCat = null;
        conectorCat = null;
        adaptadorPol = null;
        conectorPol = null;
        adaptadorFolios = null;
        conectorFolios = null;
        adaptadorCuentas = null;
        conectorCuentas = null;

        return "/Empresa/empresas?faces-redirect=true";
    }

    public void conexionPOP() {
        boolean recepcion = false;
        logger.info(usuario.getUserid() + ". conexionPOP/IMAP");
        try {
            if (!strTipoServidor.equals("")) {
                if (strservidorRecepcion != null && !strservidorRecepcion.trim().isEmpty()) {
                    if (strPuertoRecepcion != null && !strPuertoRecepcion.trim().isEmpty()) {
                        if (strUsuarioRecepcion != null && !strUsuarioRecepcion.trim().isEmpty()) {
                            List<ConfiguracionesEmpresas> configEmpresaPOP = daoConfigEmpresa.getConfiguracionEmpresaByPropiedadValor("EMAIL_RECEPCION", strUsuarioRecepcion);
                            if (configEmpresaPOP != null && configEmpresaPOP.size() > 0) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección de correo ya se encuentra registrada. Por favor utilice otra cuenta de correo.");
                                FacesContext.getCurrentInstance().addMessage(null, msg);
                                return;
                            }
                            recepcion = Email.connect(strservidorRecepcion, strUsuarioRecepcion, strPassRecepcion, strPuertoRecepcion, sslRecepcion, strTipoServidor);
                            if (recepcion) {
                                logger.info("encrypt contraseña recepcion " + strPassRecepcion);
                                passEncRecepcion = encrypt(strPassRecepcion);
                                confirmPOP = true;
                                msgPOP = "Servidor de Recepción configurado Correctamente.";
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Conexion POP3 exitosa.");
                                descBitacora = usuario.getUserid() + ". conexionPOP - Conexion POP3 correcto.";
                                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                                logger.info(usuario.getUserid() + ". conexionPOP - Conexion POP3 correcto.");
                            } else {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexion POP3 Fallo. Por favor, revise los datos.");
                                descBitacora = usuario.getUserid() + ". conexionPOP/IMAP - Conexion POP3 fallo.";
                                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                                logger.error(usuario.getUserid() + ". conexionPOP/IMAP - Conexion POP3 fallo.");
                            }
                        } else {
                            logger.warn("Error Usuario es un campo requerido.");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Usuario.");
                        }
                    } else {
                        logger.warn("Error Puerto es un campo requerido.");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Puerto.");
                    }
                } else {
                    logger.warn("Error Servidor es un campo requerido.");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Servidor.");
                }
            } else {
                logger.warn("Error debe seleccionar IMAP/POP3.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un cliente de correo IMAP/POP3.");
            }
        } catch (CryptoException e) {
            descBitacora = "[EMPRESA] conexionPOP - CryptoException ERROR:" + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". conexionPOP CryptoException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + e.getMessage());
        } catch (IOException e) {
            descBitacora = "[EMPRESA] conexionPOP - IOException ERROR:" + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". conexionPOP IOException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + e.getMessage());
        } catch (Exception ex) {
            descBitacora = "[EMPRESA] conexionPOP - IOException ERROR:" + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". conexionPOP ERROR " + ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void cancelarPOP() {
        confirmPOP = false;
        msgPOP = "";
    }

    public String saveEmpresaConfig() {

        try {
            if (daoEmpresa.updateEmpresa(empresa)) {
                guardarPlantilla();
                crearUsuarioFtp();
                insertConfigEmpresa();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa han sido Registrados.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                descBitacora = "[EMPRESA] Usuario: " + usuario.getUserid() + " registro la Empresa " + empresa.getRfc() + ".";
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". insertEmpresa - " + usuario.getUserid() + " registro la Empresa " + empresa.getRfc());
                return "/Empresa/empresas?faces-redirect=true";
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registar los datos de la Empresa.");
                FacesContext.getCurrentInstance().addMessage(null, this.msg);
                descBitacora = "[EMPRESA] Usuario: " + usuario.getUserid() + " registro la Empresa " + empresa.getRfc() + ".";
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". insertEmpresa - Error al registar los datos de la Empresa " + this.empresa.getRfc());
            }
        } catch (Exception ex) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registar los datos de la Empresa.");
            FacesContext.getCurrentInstance().addMessage(null, this.msg);
            descBitacora = usuario.getUserid() + ". saveEmpresaConfig - ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". saveEmpresaConfig - ERROR " + ex);
        }
        return null;
    }

    public String onFlowProcess(FlowEvent event) {

        if (event.getNewStep().equals("notificacion")) {
            if (insertEmpresa()) {
                return event.getNewStep();
            } else {
                return event.getOldStep();
            }
        }
        if (event.getNewStep().equals("pop")) {
            if (confirSMTP) {
//                connectSMTP = false;
                return event.getNewStep();
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Configure un servidor para envio de correo electrónico.");
                logger.warn(usuario.getUserid() + ". Por favor, Configure un servidor para envio de correo electrónico.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return event.getOldStep();
            }
        }
        if (event.getNewStep().equals("confirm")) {
            if (confirmPOP) {
                return event.getNewStep();
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, Configure un servidor para recepción de correo electrónico.");
                logger.warn(usuario.getUserid() + ". Por favor, Configure un servidor para recepción de correo electrónico.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return event.getOldStep();
            }
        }
        return event.getNewStep();
    }

    public void uploadAdaptadorBal(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xsl"));
                return;
            }
            if (!nombreArchivo.endsWith(".xsl")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xsl"));
                return;
            }
            if (event.getFile().getSize() > 5242880) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 5MB"));
                return;
            }

            inputStream = event.getFile().getInputstream();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                adaptadorBal = adaptadorBal + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Adaptador Balanza " + adaptadorBal);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Adaptador Balanza cargado correctamente.");

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ocurrio un error al cargar el archivo."));
            descBitacora = usuario.getUserid() + ". uploadAdaptadorBal -  IOException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadAdaptadorBal -  IOException ERROR: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    logger.debug(usuario.getUserid() + ". cerrando inputStream");
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". uploadFile - ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". uploadFile - ERROR: " + ex);
                }
            }
        }
    }

    public void uploadConectorBal(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xconv"));
                return;
            }
            if (!nombreArchivo.endsWith(".xconv")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xconv"));
                return;
            }
            if (event.getFile().getSize() > 5242880) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 5MB"));
                return;
            }

            inputStream = event.getFile().getInputstream();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                conectorBal = conectorBal + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Conector Balanza " + conectorBal);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Conector Balanza cargado correctamente.");

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ocurrio un error al cargar el archivo."));
            descBitacora = usuario.getUserid() + ". uploadConectorBal -  IOException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadConectorBal -  IOException ERROR: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    logger.debug(usuario.getUserid() + ". cerrando inputStream");
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". uploadFile - ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". uploadFile - ERROR: " + ex);
                }
            }
        }
    }

    public void uploadAdaptadorCat(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xsl"));
                return;
            }
            if (!nombreArchivo.endsWith(".xsl")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xsl"));
                return;
            }
            if (event.getFile().getSize() > 5242880) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 5MB"));
                return;
            }

            inputStream = event.getFile().getInputstream();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                adaptadorCat = adaptadorCat + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Adaptador Catalogo " + adaptadorCat);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Adaptador Catalogo cargado correctamente.");
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ocurrio un error al cargar el archivo."));
            descBitacora = usuario.getUserid() + ". uploadAdaptadorCat -  IOException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadAdaptadorCat -  IOException ERROR: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    logger.debug(usuario.getUserid() + ". cerrando inputStream");
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". uploadFile - ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". uploadFile - ERROR: " + ex);
                }
            }
        }
    }

    public void uploadConectorCat(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xconv"));
                return;
            }
            if (!nombreArchivo.endsWith(".xconv")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xconv"));
                return;
            }
            if (event.getFile().getSize() > 5242880) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 5MB"));
                return;
            }

            inputStream = event.getFile().getInputstream();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                conectorCat = conectorCat + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Conector Catalogo " + conectorCat);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Conector Catalogo cargado correctamente.");

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ocurrio un error al cargar el archivo."));
            descBitacora = usuario.getUserid() + ". uploadConectorCat -  IOException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadConectorCat -  IOException ERROR: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    logger.debug(usuario.getUserid() + ". cerrando inputStream");
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". uploadFile - ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". uploadFile - ERROR: " + ex);
                }
            }
        }
    }

    public void uploadConectorPol(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xconv"));
                return;
            }
            if (!nombreArchivo.endsWith(".xconv")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xconv"));
                return;
            }
            if (event.getFile().getSize() > 5242880) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 5MB"));
                return;
            }

            inputStream = event.getFile().getInputstream();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                conectorPol = conectorPol + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Conector Poliza " + conectorPol);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Conector Poliza cargado correctamente.");

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ocurrio un error al cargar el archivo."));
            descBitacora = usuario.getUserid() + ". uploadConectorPol -  IOException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadConectorPol -  IOException ERROR: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    logger.debug(usuario.getUserid() + ". cerrando inputStream");
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". uploadFile - ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". uploadFile - ERROR: " + ex);
                }
            }
        }
    }

    public void uploadAdaptadorPol(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xsl"));
                return;
            }
            if (!nombreArchivo.endsWith(".xsl")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo .xsl"));
                return;
            }
            if (event.getFile().getSize() > 5242880) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 5MB"));
                return;
            }

            inputStream = event.getFile().getInputstream();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                adaptadorPol = adaptadorPol + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Adaptador Poliza " + adaptadorPol);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Adaptador Poliza cargado correctamente.");
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ocurrio un error al cargar el archivo."));
            descBitacora = usuario.getUserid() + ". uploadAdaptadorPol -  IOException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadAdaptadorPol -  IOException ERROR: " + e);
        } finally {
            if (inputStream != null) {
                try {
                    logger.debug(usuario.getUserid() + ". cerrando inputStream");
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". uploadFile - ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". uploadFile - ERROR: " + ex);
                }
            }
        }
    }

    public void uploadFoliosAdaptador(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xsl\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xsl Adaptador Auxiliar Folios");
                return;
            }

            if (!nombreArchivo.endsWith(".xsl")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo debe ser con extensión \".xsl\""));
                logger.info(usuario.getUserid() + ". tipo de archivo no valida " + nombreArchivo);
                return;
            }

            if (event.getFile().getSize() > 2097152) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 2MB"));
                logger.warn(usuario.getUserid() + ". El archivo es mayor a 2mb archivo " + nombreArchivo + " " + event.getFile().getSize());
                return;
            }
            inputStream = event.getFile().getInputstream();
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                adaptadorFolios = adaptadorFolios + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Adaptador auxiliar folios " + adaptadorFolios);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Adaptador auxiliar folios cargado correctamente.");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadFoliosAdaptador -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadFoliosAdaptador -  IOException ERROR: " + ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". Ha ocurrido un error inputStream.close ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". Ha ocurrido un error inputStream.close ERROR: " + ex);
                }
            }
        }
    }

    public void uploadFoliosConector(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xconv\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xconv conector folios");
                return;
            }

            if (!nombreArchivo.endsWith(".xconv")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo debe ser con extensión \".xconv\""));
                logger.info(usuario.getUserid() + ". tipo de archivo no valida " + nombreArchivo);
                return;
            }

            if (event.getFile().getSize() > 2097152) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 2MB"));
                logger.warn(usuario.getUserid() + ". El archivo es mayor a 2mb archivo " + nombreArchivo + " " + event.getFile().getSize());
                return;
            }
            inputStream = event.getFile().getInputstream();
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                conectorFolios = conectorFolios + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Conector auxiliar folios " + conectorFolios);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Conector auxiliar folios cargado correctamente.");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadFoliosConector -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadFoliosConector -  IOException ERROR: " + ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". Ha ocurrido un error inputStream.close ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". Ha ocurrido un error inputStream.close ERROR: " + ex);
                }
            }
        }
    }

    public void uploadCuentasAdaptador(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xsl\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xsl Adaptador Auxiliar cuentas");
                return;
            }

            if (!nombreArchivo.endsWith(".xsl")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo debe ser con extensión \".xsl\""));
                logger.info(usuario.getUserid() + ". tipo de archivo no valida " + nombreArchivo);
                return;
            }

            if (event.getFile().getSize() > 2097152) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 2MB"));
                logger.warn(usuario.getUserid() + ". El archivo es mayor a 2mb archivo " + nombreArchivo + " " + event.getFile().getSize());
                return;
            }
            inputStream = event.getFile().getInputstream();
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                adaptadorCuentas = adaptadorCuentas + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Adaptador auxiliar cuentas " + adaptadorCuentas);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Adaptador auxiliar cuentas cargado correctamente.");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCuentasAdaptador -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCuentasAdaptador -  IOException ERROR: " + ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". Ha ocurrido un error inputStream.close ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". Ha ocurrido un error inputStream.close ERROR: " + ex);
                }
            }
        }
    }

    public void uploadCuentasConector(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xconv\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xconv conector auxiliar cuentas");
                return;
            }

            if (!nombreArchivo.endsWith(".xconv")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo debe ser con extensión \".xconv\""));
                logger.info(usuario.getUserid() + ". tipo de archivo no valida " + nombreArchivo);
                return;
            }

            if (event.getFile().getSize() > 2097152) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 2MB"));
                logger.warn(usuario.getUserid() + ". El archivo es mayor a 2mb archivo " + nombreArchivo + " " + event.getFile().getSize());
                return;
            }
            inputStream = event.getFile().getInputstream();
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                conectorCuentas = conectorCuentas + new String(bytes, 0, read, "UTF8");
            }
            logger.debug("Conector auxiliar cuentas " + adaptadorCuentas);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
            logger.info(usuario.getUserid() + ". Conector auxiliar cuentas cargado correctamente.");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCuentasConector -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCuentasConector -  IOException ERROR: " + ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". Ha ocurrido un error inputStream.close ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". Ha ocurrido un error inputStream.close ERROR: " + ex);
                }
            }
        }
    }

    public void createUserVal() {
        try {
            if (userFtpV != null && !userFtpV.trim().isEmpty()) {
                if (daoConfigEmpresa.existConfiguracionEmpresa(ConfigEmpresa.VAL_USER_FTP.name(), userFtpV) == null) {
                    if (passFtpV != null && !passFtpV.trim().isEmpty()) {
                        if (passFtpV.equals(confirmFtpV)) {
                            logger.info(usuario.getUserid() + ". user: " + userFtpV);
                            logger.info(usuario.getUserid() + ". pass: " + passFtpV);
                            logger.info(usuario.getUserid() + ". Usuario " + userFtpV + " FTP validacion agregado correctamente");
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Usuario FTP " + userFtpV + " agregado correctamente");
                            existFtp = true;
                        } else {
                            passFtpV = "";
                            confirmFtpV = "";
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Contraseña.");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario " + userFtpV + " ya se encuentra registrado. Por favor, ingrese otro usuario.");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Usuario.");
            }
        } catch (Exception e) {
            logger.error(usuario.getUserid() + ". createUserVal - ERROR " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear el usaurio FTP. Por favor, pongase en contacto con el administrador.");
            descBitacora = usuario.getUserid() + ". createUserVal - ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void cancelarFtp() {
        userFtpV = "";
        passFtpV = "";
        confirmFtpV = "";
        existFtp = false;
    }

    private void crearUsuarioFtp() {
        try {
            if (existFtp) {
                logger.info("Creando Usuario FTP validacion");
                ejecutarScript(userFtpV, passFtpV);
                enPassFtpV = encrypt(passFtpV);
            } else {
                logger.info("no se cargo configuracion para usuario FTP validacion");
            }
        } catch (CryptoException ex) {
            descBitacora = "[EMPRESA - crearUsuarioFtp] " + usuario.getUserid() + " CryptoException ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        } catch (IOException ex) {
            descBitacora = "[EMPRESA - crearUsuarioFtp] " + usuario.getUserid() + " IOException ERROR " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    private void ejecutarScript(String user, String pass) {
        hostFtp = "localhost";
        try {
            logger.info(usuario.getUserid() + ". host: " + hostFtp);
            logger.info(usuario.getUserid() + ". user: " + user);
            logger.info(usuario.getUserid() + ". pass: " + pass);
            Process cmd = Runtime.getRuntime().exec("/work/iqtb/ftp-useradd.sh " + user + " " + pass + " " + empresa.getRfc());
            cmd.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
            String linea = "";
            while ((linea = buf.readLine()) != null) {
                logger.info(linea.toString());
            }
            logger.info(usuario.getUserid() + ". Usuario " + user + " FTP validacion agregado correctamente");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Usuario FTP " + user + " agregado correctamente");
            descBitacora = usuario.getUserid() + ". Usuario " + user + " FTP validacion agregado correctamente";
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
            String ruta = "/work/ftp/" + empresa.getRfc();
            File folder = new File(ruta + "/VALIDACION");
            folder.mkdirs();
            logger.info(usuario.getUserid() + ". createUserVal - carpeta VALIDACION " + folder.getAbsolutePath());
            folder = new File(ruta + "/BALANZA");
            folder.mkdirs();
            logger.info(usuario.getUserid() + ". createUserVal - carpeta BALANZA " + folder.getAbsolutePath());
            folder = new File(ruta + "/CATALOGO");
            folder.mkdirs();
            logger.info(usuario.getUserid() + ". createUserVal - carpeta CATALOGO " + folder.getAbsolutePath());
            folder = new File(ruta + "/POLIZA");
            folder.mkdirs();
            logger.info(usuario.getUserid() + ". createUserVal - carpeta POLIZA " + folder.getAbsolutePath());
            folder = new File(ruta + "/AUXFOLIOS");
            folder.mkdirs();
            logger.info(usuario.getUserid() + ". createUserVal - carpeta AUXFOLIOS " + folder.getAbsolutePath());
            folder = new File(ruta + "/AUXCUENTAS");
            folder.mkdirs();
            logger.info(usuario.getUserid() + ". createUserVal - carpeta AUXCUENTAS " + folder.getAbsolutePath());
            folder = new File(ruta + "/REPORTES");
            folder.mkdirs();
            logger.info(usuario.getUserid() + ". createUserVal - carpeta REPORTES " + folder.getAbsolutePath());
            cmd = Runtime.getRuntime().exec("chmod -R 777 " + ruta);
            cmd.waitFor();
            logger.info(usuario.getUserid() + ". createUserVal - Aplicando permisos chmod -R 777 " + ruta);
        } catch (IOException e) {
            logger.error("ejecutarScript-IOException ERROR " + e);
        } catch (InterruptedException e) {
            logger.error("ejecutarScript-InterruptedException ERROR " + e);
        }
    }

    public String onFlowProcessPlantilla(FlowEvent event) {
        if ("Subreportes".equals(event.getNewStep())) {
            if ((this.principal == null) || (this.principal.isEmpty()) || (principal.length() <= 0)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Por favor, Seleccione un reporte Principal");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.info(usuario.getUserid() + ". No se ha seleccionado ningun Reporte Principal");
                return event.getOldStep();
            }
        }
        if ("updateSubreportes".equals(event.getNewStep())) {
            if ((principal == null) || (principal.isEmpty()) || (principal.length() <= 0)) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Por favor, Seleccione un reporte Principal");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.info(usuario.getUserid() + ". No se ha seleccionado ningun Reporte Principal");
                return event.getOldStep();
            }
        }
        if ("confirmacion".equals(event.getNewStep())) {
            for (Imagen ima : listaImagenes) {
                if (ima.getEstado().equals("Sin seleccionar")) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Por favor, cargue todas las imagenes");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.info(usuario.getUserid() + ". Existen imagenes sin cargar");
                    return event.getOldStep();
                }
            }

            this.listaSubreportes = new ArrayList<Subreporte>();
            ManejoArchivos ma = new ManejoArchivos();
            List<String> listaSub = ma.getListaSubreportes(destino + empresa.getRfc() + "/" + carpeta);

            for (String string : listaSub) {
                Subreporte subreprote = new Subreporte();
                subreprote.setNombre(string);
                listaSubreportes.add(subreprote);
            }
        }
        if ("updateConfirmacion".equals(event.getNewStep())) {
            for (Imagen ima : listaImagenes) {
                if (ima.getEstado().equals("Sin seleccionar")) {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaucion", "Aún no han cargado todas la imagenes");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return event.getOldStep();
                }
            }

            this.listaSubreportes = new ArrayList<Subreporte>();
            ManejoArchivos ma = new ManejoArchivos();
            List<String> listaSub = ma.getListaSubreportes(destino + empresa.getRfc() + "/" + carpeta);

            for (String string : listaSub) {
                Subreporte subr = new Subreporte();
                subr.setNombre(string);
                listaSubreportes.add(subr);
            }
        }
        return event.getNewStep();
    }

    public void uploadPrincipal(FileUploadEvent event) {
        logger.info("Inicia carga de Reporte Principal");
        logger.debug("Nombre event " + event.getFile().getFileName());
        logger.debug("Empresa " + empresa.getRfc());
        ManejoArchivos ma = new ManejoArchivos();

        ma.eliminar(destino);

        carpeta = ma.quirtarExtension(event.getFile().getFileName()); //asignamos el nombre de la carpeta con el nombre de la plantilla
        logger.info("carpeta " + carpeta);
        logger.info("destino " + destino);
        logger.info("Nombre carpeta " + destino + empresa.getRfc() + "/" + carpeta + "/");
        File direccion = new File(destino + empresa.getRfc() + "/" + carpeta + "/");

        boolean ban = direccion.mkdirs(); // creamos la ruta temporal de la plantilla
        if (ban) {
            logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Se creo correctamente el directorio de la plantilla: " + direccion.getAbsolutePath());
            InputStream in = null;
            try {
                in = event.getFile().getInputstream();

            } catch (IOException ex) {
                descBitacora = "ERROR " + ex.getMessage();
                registrarBitacora(null, null, null, descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Error al cargar la plantilla " + ex.getMessage());
            }
            try {
                OutputStream out = new FileOutputStream(new File(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint"));
                int reader = 0;
                byte[] bytes = new byte[(int) event.getFile().getSize()];
                while ((reader = in.read(bytes)) != -1) {
                    out.write(bytes, 0, reader);
                }
                in.close();
                out.flush();
                out.close();
                File f2 = new File(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
                if (f2.isFile()) {
                    logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Ruta del reporte Principal: " + f2.getAbsolutePath());
                    boolean compila = ma.compilarPlantilla(f2.getAbsolutePath());
                    if (compila) {
                        listaSubreportes.clear();// Se limpia la lista de subreportes
                        logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Se compilo corractamente el reporte principal: " + f2.getName());
                        this.principal = event.getFile().getFileName(); //asignamos el nombre de la planilla
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La plantilla: " + event.getFile().getFileName() + " a sido cargada y compilada");
                        try {
                            this.listaImagenes = ma.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
//                            this.listaImagenes = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/main.xprint");
                            for (Imagen ima : listaImagenes) {
                                logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Imagen de reporte principal: " + ima.getNombre());
                            }
                        } catch (IOException e) {
                            logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Error al obtener la imagenes del reporte principal");
                        }
                    } else {
                        logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Error al compilar la plantillas: " + f2.getName());
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La plantilla: " + event.getFile().getFileName() + " no ha sido compilada");
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (IOException e) {
                descBitacora = "ERROR " + e.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Error al cargar la plantilla " + e.getMessage());
            }
        } else {
            descBitacora = "[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "ERROR");
            logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MbEmpresa ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath());
        }
    }

    public void asignarReportePrincipalPNulo() {
        if (this.principal.length() > 0) {
            new ManejoArchivos().eliminar(destino);
            this.principal = "";
            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se elimino satisfactoriamente el Reporte Principal");
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un Reporte Principal");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadSubreportes(FileUploadEvent event) {
        InputStream in = null;

        if (!event.getFile().getFileName().equals(principal)) {
            try {
                in = event.getFile().getInputstream();
            } catch (IOException ex) {
                logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte " + ex);
            }
            try {
                OutputStream out = new FileOutputStream(new File(destino + empresa.getRfc() + "/" + carpeta + "/" + event.getFile().getFileName()));
                int reader = 0;
                byte[] bytes = new byte[(int) event.getFile().getSize()];
                while ((reader = in.read(bytes)) != -1) {
                    out.write(bytes, 0, reader);
                }
                in.close();
                out.flush();
                out.close();
                ManejoArchivos ma = new ManejoArchivos();
                File subRepor = new File(destino + empresa.getRfc() + "/" + carpeta + "/" + event.getFile().getFileName());
                logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Ruta del subreporte " + subRepor.getAbsolutePath());
                if (subRepor.isFile()) {

                    boolean compila = ma.compilarPlantilla(subRepor.getAbsolutePath());
                    if (compila) {
                        logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Se compilo corractamente el subreporte: " + subRepor.getName());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", event.getFile().getFileName() + " a sido subido y compilado");

                        listarSubreportes(); //metodo para obtener los subreporte

                        try {
                            List<Imagen> listaImaSub = ma.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/" + event.getFile().getFileName());
//                            List<Imagen> listaImaSub = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + event.getFile().getFileName());
                            for (Imagen imagen1 : listaImaSub) {
                                logger.info("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Imagen de subreporte: " + imagen1.getNombre());
                                this.listaImagenes.add(imagen1);
                            }
                        } catch (IOException ex) {
                            descBitacora = "[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte";
                            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "ERROR");
                            logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte");
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Subreporte: ", event.getFile().getFileName() + " no ha sido cargado correctamente y no pudo compilar");
                    }
                }
            } catch (IOException e) {
                descBitacora = "[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte ";
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte ");
            }
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución ", "Esta intentando subir un subreporte con el mismo nombre del reporte principal: " + event.getFile().getFileName());
            logger.error(usuario.getUserid() + ". tratando de subir un subreporte con el mismo nombre del reporte principal: " + event.getFile().getFileName());
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void listarSubreportes() {
        ManejoArchivos ma = new ManejoArchivos();
        List<String> lista = ma.getListaSubreportes(destino + empresa.getRfc() + "/" + carpeta + "/");
        listaSubreportes.clear();
        for (String string : lista) {
            Subreporte sub = new Subreporte();
            sub.setNombre(string);
            listaSubreportes.add(sub);
        }
    }

    public void borrarSubreporte() {
        ManejoArchivos ma = new ManejoArchivos();
        logger.info("subreporte a borrar: " + subreporteSeleccionado.getNombre());

        List<Imagen> listIma = null;
        try {
            listIma = ma.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());
//            listIma = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());
            for (Imagen imagen1 : listIma) {
                logger.info("Imagen a borrar: " + imagen1.getNombre());
            }
        } catch (IOException ex) {
            ex.getMessage();
        }

        boolean correcto = ma.eliminaArchivo(destino + empresa.getRfc() + "/" + carpeta + "/" + subreporteSeleccionado.getNombre());

        if (correcto) {
            for (Imagen imagen1 : listIma) {
                boolean ban = listaImagenes.remove(imagen1);
                logger.info("Se elimino coprrectamente la imagen: " + imagen1.getNombre());
            }

            this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se elimino satisfactoriamente el subreporte");
            listarSubreportes();
        } else {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el subreporte");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadImagenes(FileUploadEvent event) {
        Boolean ban = false;
        Integer pos = null;
        int totalImagenes = listaImagenes.size();
        int contador = 0;
        for (Imagen ima1 : listaImagenes) {
            if (ima1.getNombre().equals(event.getFile().getFileName())) {
                pos = listaImagenes.indexOf(ima1);
                ban = true;
            }
            if (ima1.getEstado().equals("Cargado")) {
                contador++;
            }
        }
        if (contador >= totalImagenes) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Se han cargador todas la imagenes en la tabla", null);
        } else {
            if (!ban) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Imagen: " + event.getFile().getFileName() + " no se encuentra en la tabla iamagenes");
            } else {
                Imagen ima1 = listaImagenes.get(pos);
                String folder = ima1.getCarpeta();

                File f1 = new File(destino + empresa.getRfc() + "/" + carpeta + "/" + folder);
                f1.mkdirs();
                InputStream in = null;
                try {
                    in = event.getFile().getInputstream();
                } catch (IOException ex) {
                    logger.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + ex.getMessage());
                }
                try {
                    OutputStream out = new FileOutputStream(new File(destino + empresa.getRfc() + "/" + carpeta + "/" + folder + "/" + event.getFile().getFileName()));
                    int reader = 0;
                    byte[] bytes = new byte[(int) event.getFile().getSize()];
                    while ((reader = in.read(bytes)) != -1) {
                        out.write(bytes, 0, reader);
                    }
                    in.close();
                    out.flush();
                    out.close();
                    ima1.setEstado("Cargado");
                    this.msg = new FacesMessage("Imagen: ", event.getFile().getFileName() + " a sido cargado correctamente.");
                } catch (IOException e) {
                    descBitacora = "[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "ERROR");
                    logger.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage());
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void configurarPlantillas() {
        guardarPlantilla = true;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Plantilla agregada."));
    }

    public void resetPlantilla() {
        guardarPlantilla = false;
//        principal = "";
//        nombrePlantilla = "";
//        carpeta = "";
//        listaSubreportes = new ArrayList<Subreporte>();
//        subreporteSeleccionado = new Subreporte();
//        listaImagenes = new ArrayList<Imagen>();
//        plantilla = new Plantillas();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Plantilla eliminada."));
    }

    private void guardarPlantilla() {
        if (guardarPlantilla) {
            ManejoArchivos ma = new ManejoArchivos();
            plantilla = new Plantillas();
            try {
                String rutaFinal = "/work/iqtb/validacionfiles/plantillas/";
                ma.mover(destino + empresa.getRfc(), rutaFinal + empresa.getRfc());
                plantilla.setIdEmpresa(empresa.getIdEmpresa());
                if (nombrePlantilla != null && !nombrePlantilla.trim().isEmpty()) {
                    plantilla.setNombre(nombrePlantilla);
                } else {
                    plantilla.setNombre("Plantilla " + empresa.getRfc());
                }
                plantilla.setRuta(rutaFinal + empresa.getRfc() + "/" + carpeta + "/main.jasper");
                logger.info("[ " + sessionUsuario + " - metodo_guardarPlantilla MbEmpresas ] Plantilla a insertar, nombre: " + plantilla.getNombre() + ", ruta: " + plantilla.getRuta());
                boolean ban = daoPlantilla.insertPlantilla(plantilla);
                if (ban) {
                    descBitacora = sessionUsuario + " - metodo_guardarPlantilla MbEmpresas ] Se inserto correctamente la plantilla en la BD: " + plantilla.getNombre();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "INFO");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha cargado correctamente la plantilla: " + plantilla.getNombre());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.info(sessionUsuario + " - metodo_guardarPlantilla MbEmpresas ] Se inserto correctamente la plantilla en la BD: " + plantilla.getNombre());
                } else {
                    descBitacora = "[ " + sessionUsuario + " - metodo_guardaPlantilla MbEmpresas ] No se inserto la plantilla en la BD: " + plantilla.getNombre();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "ERROR");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error guardando la plantilla " + plantilla.getNombre());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.error("[ " + sessionUsuario + " - metodo_guardaPlantilla MbEmpresas ] No se inserto la plantilla en la BD: " + plantilla.getNombre());
                }
            } catch (IOException ex) {
                descBitacora = "[ " + sessionUsuario + " - metodo_guardarPlantilla MbEmpresas ] Error al crear el zip: " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - metodo_guardarPlantilla MbEmpresas ] Error al crear el zip: " + ex.getMessage());
            } catch (Exception ex) {
                descBitacora = "[ " + sessionUsuario + " - metodo_guardarPlantilla MbEmpresas ] Error al crear el zip: " + ex.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - metodo_guardarPlantilla MbEmpresas ] Error al crear el zip: " + ex.getMessage());
            }
        } else {
            logger.info("[ " + sessionUsuario + " - metodo_guardarPlantilla MbEmpresas ] No existe configuracion para Plantilla.");
        }
    }

    public String existeSeleccionEmpresa() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");
        logger.debug("parametro: " + parametro);

        boolean estadoEmpresa = false;
        if (this.empresasSeleccionadas.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione una empresa.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoEmpresa = true;
                }
            } else if (empresasSeleccionadas.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo una empresa.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                for (Empresas empresas : empresasSeleccionadas) {
                    this.empresa = empresas;
                }
                estadoEmpresa = true;
            }

        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoEmpresa", estadoEmpresa);

        return "/Empresa/empresas?faces-redirect=true";
    }

    private void insertConfigEmpresa() {
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.NOTIFICAR_XML_VALIDO.name());
        String strXmlValido = String.valueOf(xmlValido);
        configEmpresa.setValor(strXmlValido);
        configEmpresa.setDescripcion("Enviar notificación de XML válido al remitente.");
        configEmpresa.setTipo("BOOLEANO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.NOTIFICAR_XML_INVALIDO.name());
        String strXmlInvalido = String.valueOf(xmlInvalido);
        configEmpresa.setValor(strXmlInvalido);
        configEmpresa.setDescripcion("Enviar notificación de XML inválido al remitente.");
        configEmpresa.setTipo("BOOLEANO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.XML_VALIDO_EMAIL.name());
        String strValidoEmail = String.valueOf(validoEmail);
        configEmpresa.setValor(strValidoEmail);
        configEmpresa.setDescripcion("Enviar notificación de XML válido al correo registrado.");
        configEmpresa.setTipo("BOOLEANO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.XML_INVALIDO_EMAIL.name());
        String strInvalidoEmail = String.valueOf(invalidoEmail);
        configEmpresa.setValor(strInvalidoEmail);
        configEmpresa.setDescripcion("Enviar notificación de XML inválido al correo registrado.");
        configEmpresa.setTipo("BOOLEANO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.EMAILS_RECIBEN_NOTIFICACION.name());
        configEmpresa.setValor(configValor);
        configEmpresa.setDescripcion("Lista de correos que reciben notificaciones (separados por coma).");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.EMAIL_RECEPCION.name());
        configEmpresa.setValor(strUsuarioRecepcion);
        configEmpresa.setDescripcion("Direccion de recepcion de correo.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.RECEPCION_PASS.name());
        configEmpresa.setValor(passEncRecepcion);
        configEmpresa.setDescripcion("Pass de recepcion de correo.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.HOST_RECEPCION.name());
        configEmpresa.setValor(strservidorRecepcion);
        configEmpresa.setDescripcion("Host de recepcion de correo.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.PUERTO_RECEPCION.name());
        configEmpresa.setValor(strPuertoRecepcion);
        configEmpresa.setDescripcion("Indica el puerto a utilizar para la recepcion.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.START_TLS.name());
        configEmpresa.setValor("false");
        configEmpresa.setDescripcion("Informa si se utiliza TLS.");
        configEmpresa.setTipo("BOOLEANO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.PROTOCOLO_RECEPCION.name());
        configEmpresa.setValor(strTipoServidor);
        configEmpresa.setDescripcion("Indica el protocolo a utilizar para recepcion.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.UTILIZAR_SSL.name());
        String sslR = String.valueOf(sslRecepcion);
        configEmpresa.setValor(sslR);
        configEmpresa.setDescripcion("Indica si se utiliza SSL.");
        configEmpresa.setTipo("BOOLEANO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.EMAIL_SMTP.name());
        configEmpresa.setValor(strEmailSMTP);
        configEmpresa.setDescripcion("Direccion de correo electrónico para SMTP.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.PASS_SMTP.name());
        configEmpresa.setValor(passEncryptSMTP);
        configEmpresa.setDescripcion("Contraseña de correo electrónico para SMTP.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.HOST_SMTP.name());
        configEmpresa.setValor(strHostSMTP);
        configEmpresa.setDescripcion("Servidor SMTP.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.USUARIO_SMTP.name());
        configEmpresa.setValor(strUserSMTP);
        configEmpresa.setDescripcion("Usuario de correo electrónico para SMTP.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.PUERTO_SMTP.name());
        configEmpresa.setValor(strPortSMTP);
        configEmpresa.setDescripcion("Indica el puerto a utilizar para SMTP.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.NOMBRE_SMTP.name());
        configEmpresa.setValor(strNombreSMTP);
        configEmpresa.setDescripcion("Nombre configuracion de SMTP.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.SSL_SMTP.name());
        String ssl = String.valueOf(sslSMTP);
        configEmpresa.setValor(ssl);
        configEmpresa.setDescripcion("Informa si se utiliza SSL en SMTP.");
        configEmpresa.setTipo("BOOLEANO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.VAL_HOST_FTP.name());
        configEmpresa.setValor(hostFtp);
        configEmpresa.setDescripcion("Servidor FTP para Validación de CFDI's de proveedores.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.VAL_USER_FTP.name());
        configEmpresa.setValor(userFtpV);
        configEmpresa.setDescripcion("Usuario FTP para Validación de CFDI's de proveedores.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.VAL_PASS_FTP.name());
        configEmpresa.setValor(enPassFtpV);
        configEmpresa.setDescripcion("Contraseña FTP para Validación de CFDI's de proveedores.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.IP_ORACLE.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Ip para hacer la conexión con Oracle.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.PUERTO_ORACLE.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Puerto del servidor de Oracle.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.SID_NAME.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Nombre del servicio donde se encuentra la tabla Oracle.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.USER.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Usuario para acceder a Oracle.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.PASS.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Contraseña para el usuario Oracle.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.TIEMPO_PAUSA.name());
        configEmpresa.setValor("30000");
        configEmpresa.setDescripcion("Tiempo de espera entre cada ciclo.");
        configEmpresa.setTipo("NUMERO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.TIEMPO_REC_CONFIG.name());
        configEmpresa.setValor("90000");
        configEmpresa.setDescripcion("Tiempo de espera al recargar la configuración.");
        configEmpresa.setTipo("NUMERO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.VALIDAR_ADDENDA.name());
        configEmpresa.setValor("3");
        configEmpresa.setDescripcion("Configuración para decidir como validar la adenda de un XML.");
        configEmpresa.setTipo("NUMERO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.REPORTAR_A.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Configuración para reportar CFDI Recibidos ERP|ENVIAR_FTP|DISPONIBLE_FTP.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.HOST_FTP.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Servidor FTP para reportar CFDI Recibidos.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.USER_FTP.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Usuario FTP para reportar CFDI Recibidos.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.PASS_FTP.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Contraseña FTP para reportar CFDI Recibidos.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.PORT_FTP.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Puerto FTP para reportar CFDI Recibidos.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        configEmpresa = new ConfiguracionesEmpresas();
        configEmpresa.setIdEmpresa(empresa.getIdEmpresa());
        configEmpresa.setPropiedad(ConfigEmpresa.DIR_FTP.name());
        configEmpresa.setValor("");
        configEmpresa.setDescripcion("Directorio FTP para reportar CFDI Recibidos.");
        configEmpresa.setTipo("TEXTO");
        registrarConfigEmpresa(configEmpresa);

        try {
            conectorAdaptador = new ConectorAdaptador();
            conectorAdaptador.setIdEmpresa(empresa.getIdEmpresa());
            conectorAdaptador.setNombre("BALANZA");
            conectorAdaptador.setDescripcion("Conector/Adaptador para textos tipo Balanza de comprobación.");
            conectorAdaptador.setConector(conectorBal);
            conectorAdaptador.setAdaptador(adaptadorBal);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador BALANZA para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador BALANZA para la empresa " + empresa.getRfc());
            } else {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador BALANZA para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador BALANZA para la empresa " + empresa.getRfc());
            }
            conectorAdaptador = new ConectorAdaptador();
            conectorAdaptador.setIdEmpresa(empresa.getIdEmpresa());
            conectorAdaptador.setNombre("CATALOGO");
            conectorAdaptador.setDescripcion("Conector/Adaptador para textos tipo Catálogo de cuentas.");
            conectorAdaptador.setConector(conectorCat);
            conectorAdaptador.setAdaptador(adaptadorCat);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador CATALOGO para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador CATALOGO para la empresa " + empresa.getRfc());
            } else {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador CATALOGO para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador CATALOGO para la empresa " + empresa.getRfc());
            }

            conectorAdaptador = new ConectorAdaptador();
            conectorAdaptador.setIdEmpresa(empresa.getIdEmpresa());
            conectorAdaptador.setNombre("POLIZA");
            conectorAdaptador.setDescripcion("Conector/Adaptador para textos tipo Pólizas del periodo.");
            conectorAdaptador.setConector(conectorPol);
            conectorAdaptador.setAdaptador(adaptadorPol);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador POLIZA para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador POLIZA para la empresa " + empresa.getRfc());
            } else {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador POLIZA para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador POLIZA para la empresa " + empresa.getRfc());
            }

            conectorAdaptador = new ConectorAdaptador();
            conectorAdaptador.setIdEmpresa(empresa.getIdEmpresa());
            conectorAdaptador.setNombre("AUXFOLIOS");
            conectorAdaptador.setDescripcion("Conector/Adaptador para textos tipo Auxiliar de folios de comprobantes fiscales.");
            conectorAdaptador.setConector(conectorFolios);
            conectorAdaptador.setAdaptador(adaptadorFolios);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador AUXFOLIOS para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador AUXFOLIOS para la empresa " + empresa.getRfc());
            } else {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador AUXFOLIOS para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador AUXFOLIOS para la empresa " + empresa.getRfc());
            }

            conectorAdaptador = new ConectorAdaptador();
            conectorAdaptador.setIdEmpresa(empresa.getIdEmpresa());
            conectorAdaptador.setNombre("AUXCUENTAS");
            conectorAdaptador.setDescripcion("Conector/Adaptador para textos tipo Auxiliar de cuenta y subcuenta.");
            conectorAdaptador.setConector(conectorCuentas);
            conectorAdaptador.setAdaptador(adaptadorCuentas);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador AUXCUENTAS para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". insertConfigEmpresa - Se inserto ConectorAdaptador AUXCUENTAS para la empresa " + empresa.getRfc());
            } else {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador AUXCUENTAS para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". insertConfigEmpresa - Error al insertar ConectorAdaptador AUXCUENTAS para la empresa " + empresa.getRfc());
            }
        } catch (Exception e) {
            descBitacora = usuario.getUserid() + ". insertConfigEmpresa - ConectorAdaptador ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". insertConfigEmpresa - ConectorAdaptador ERROR " + e);
        }
    }

    private boolean registrarConfigEmpresa(ConfiguracionesEmpresas configEmpresa) {
        boolean respuesta = false;
        try {
            respuesta = daoConfigEmpresa.insertConfiguracionEmpresa(configEmpresa);
            if (respuesta) {
                descBitacora = usuario.getUserid() + ". insertConfigEmpresa - Se inserto la configuracion " + configEmpresa.getPropiedad() + " para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". insertConfigEmpresa - Se inserto la configuracion " + configEmpresa.getPropiedad() + " para la empresa " + empresa.getRfc());
            } else {
                descBitacora = usuario.getUserid() + ". Error al insertar la configuracion " + configEmpresa.getPropiedad() + " para la empresa " + empresa.getRfc();
                registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al insertar la configuracion " + configEmpresa.getPropiedad() + " para la empresa " + empresa.getRfc());
            }
        } catch (Exception e) {
            descBitacora = usuario.getUserid() + ". registrarConfigEmpresa ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". registrarConfigEmpresa ERROR " + e);
        }
        return respuesta;
    }

}
