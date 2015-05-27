/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionEmpresa;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.ConfigEmpresa;
import static com.iqtb.validacion.encrypt.Encrypt.encrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.mail.Email;
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import net.sourceforge.lightcrypto.CryptoException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbConfiguracion implements Serializable {

    private Empresas empresa;
    private ConfiguracionesEmpresas ceListaEmails;
    private ConfiguracionesEmpresas listaEmail;
    private ConfiguracionesEmpresas validoRemitente;
    private ConfiguracionesEmpresas invalidoRemitente;
    private ConfiguracionesEmpresas validoEmail;
    private ConfiguracionesEmpresas invalidoEmail;
    private String newEmail;
    private List<String> emails;
    private String configValor;
    private ConfiguracionesEmpresas servidorSMTP;
    private ConfiguracionesEmpresas puertoSMTP;
    private ConfiguracionesEmpresas nombreSMTP;
    private ConfiguracionesEmpresas emailSMTP;
    private ConfiguracionesEmpresas usuarioSMTP;
    private ConfiguracionesEmpresas passSMTP;
    private ConfiguracionesEmpresas SSL_SMTP;
    private String enviarCodigo;
    private String hashSMTP;
    private String hash;
    private ConfiguracionesEmpresas servidorRecepcion;
    private ConfiguracionesEmpresas puertoRecepcion;
    private ConfiguracionesEmpresas usuarioRecepcion;
    private ConfiguracionesEmpresas passRecepcion;
    private ConfiguracionesEmpresas tlsRecepcion;
    private ConfiguracionesEmpresas sslRecepcion;
    private ConfiguracionesEmpresas protocoloRecepcion;
    private String tipoServidor;
    private String pw;
    private boolean connectSMTP;
    private final String sessionUsuario;
    private Usuarios usuario;
    private String descBitacora;

    private ConfiguracionesEmpresas validarAddenda;
    private FacesMessage msg;
    private DaoConfiguracionEmpresa daoConfigEmpresa;
    private DaoUsuario daoUsuario;
    private DaoEmpresa daoEmpresa;
    private static final Logger logger = Logger.getLogger(MbConfiguracion.class);

    public MbConfiguracion() {
        empresa = new Empresas();
        ceListaEmails = new ConfiguracionesEmpresas();
        listaEmail = new ConfiguracionesEmpresas();
        emails = new ArrayList<String>();
        newEmail = "";
        usuario = new Usuarios();
        validoRemitente = new ConfiguracionesEmpresas();
        invalidoRemitente = new ConfiguracionesEmpresas();
        validoEmail = new ConfiguracionesEmpresas();
        invalidoEmail = new ConfiguracionesEmpresas();
        connectSMTP = false;
        daoConfigEmpresa = new DaoConfiguracionEmpresa();
        daoUsuario = new DaoUsuario();
        daoEmpresa = new DaoEmpresa();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = daoUsuario.getByUserid(sessionUsuario);
            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
            configNotificaciones();
            configEmail();
            configSMTP();
            configRecepcion();
            configValidarAddenda();
            logger.info(usuario.getUserid() + ". Se han cargado las Configuraciones de la Empresa " + empresa.getRfc());
        } catch (Exception e) {
            descBitacora = "MConfiguracion ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("Error al obtener USUARIO/EMPRESA. ERROR: " + e);
        }
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public ConfiguracionesEmpresas getCeListaEmails() {
        try {
            ceListaEmails = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.EMAILS_RECIBEN_NOTIFICACION.name());
        } catch (Exception e) {
            descBitacora = "getceListaEmails ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("getceListaEmails - ERROR " + e);
        }
        return ceListaEmails;
    }

    public void setCeListaEmails(ConfiguracionesEmpresas ceListaEmails) {
        this.ceListaEmails = ceListaEmails;
    }

    public ConfiguracionesEmpresas getListaEmail() {
        return listaEmail;
    }

    public void setListaEmail(ConfiguracionesEmpresas listaEmail) {
        this.listaEmail = listaEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public ConfiguracionesEmpresas getValidoRemitente() {
        return validoRemitente;
    }

    public void setValidoRemitente(ConfiguracionesEmpresas validoRemitente) {
        this.validoRemitente = validoRemitente;
    }

    public ConfiguracionesEmpresas getInvalidoRemitente() {
        return invalidoRemitente;
    }

    public void setInvalidoRemitente(ConfiguracionesEmpresas invalidoRemitente) {
        this.invalidoRemitente = invalidoRemitente;
    }

    public ConfiguracionesEmpresas getValidoEmail() {
        return validoEmail;
    }

    public void setValidoEmail(ConfiguracionesEmpresas validoEmail) {
        this.validoEmail = validoEmail;
    }

    public ConfiguracionesEmpresas getInvalidoEmail() {
        return invalidoEmail;
    }

    public void setInvalidoEmail(ConfiguracionesEmpresas invalidoEmail) {
        this.invalidoEmail = invalidoEmail;
    }

    public ConfiguracionesEmpresas getServidorSMTP() {
        return servidorSMTP;
    }

    public void setServidorSMTP(ConfiguracionesEmpresas servidorSMTP) {
        this.servidorSMTP = servidorSMTP;
    }

    public ConfiguracionesEmpresas getPuertoSMTP() {
        return puertoSMTP;
    }

    public void setPuertoSMTP(ConfiguracionesEmpresas puertoSMTP) {
        this.puertoSMTP = puertoSMTP;
    }

    public ConfiguracionesEmpresas getNombreSMTP() {
        return nombreSMTP;
    }

    public void setNombreSMTP(ConfiguracionesEmpresas nombreSMTP) {
        this.nombreSMTP = nombreSMTP;
    }

    public ConfiguracionesEmpresas getEmailSMTP() {
        return emailSMTP;
    }

    public void setEmialSMTP(ConfiguracionesEmpresas emialSMTP) {
        this.emailSMTP = emialSMTP;
    }

    public ConfiguracionesEmpresas getUsuarioSMTP() {
        return usuarioSMTP;
    }

    public void setUsuarioSMTP(ConfiguracionesEmpresas usuarioSMTP) {
        this.usuarioSMTP = usuarioSMTP;
    }

    public ConfiguracionesEmpresas getPassSMTP() {
        return passSMTP;
    }

    public void setPassSMTP(ConfiguracionesEmpresas passSMTP) {
        this.passSMTP = passSMTP;
    }

    public ConfiguracionesEmpresas getSSL_SMTP() {
        return SSL_SMTP;
    }

    public void setSSL_SMTP(ConfiguracionesEmpresas SSL_SMTP) {
        this.SSL_SMTP = SSL_SMTP;
    }

    public String getEnviarCodigo() {
        return enviarCodigo;
    }

    public void setEnviarCodigo(String enviarCodigo) {
        this.enviarCodigo = enviarCodigo;
    }

    public String getHashSMTP() {
        return hashSMTP;
    }

    public void setHashSMTP(String hashSMTP) {
        this.hashSMTP = hashSMTP;
    }

    public String getTipoServidor() {
        return tipoServidor;
    }

    public void setTipoServidor(String tipoServidor) {
        this.tipoServidor = tipoServidor;
    }

    public ConfiguracionesEmpresas getServidorRecepcion() {
        return servidorRecepcion;
    }

    public void setServidorRecepcion(ConfiguracionesEmpresas servidorRecepcion) {
        this.servidorRecepcion = servidorRecepcion;
    }

    public ConfiguracionesEmpresas getPuertoRecepcion() {
        return puertoRecepcion;
    }

    public void setPuertoRecepcion(ConfiguracionesEmpresas puertoRecepcion) {
        this.puertoRecepcion = puertoRecepcion;
    }

    public ConfiguracionesEmpresas getUsuarioRecepcion() {
        return usuarioRecepcion;
    }

    public void setUsuarioRecepcion(ConfiguracionesEmpresas usuarioRecepcion) {
        this.usuarioRecepcion = usuarioRecepcion;
    }

    public ConfiguracionesEmpresas getPassRecepcion() {
        return passRecepcion;
    }

    public void setPassRecepcion(ConfiguracionesEmpresas passRecepcion) {
        this.passRecepcion = passRecepcion;
    }

    public ConfiguracionesEmpresas getTlsRecepcion() {
        return tlsRecepcion;
    }

    public void setTlsRecepcion(ConfiguracionesEmpresas tlsRecepcion) {
        this.tlsRecepcion = tlsRecepcion;
    }

    public ConfiguracionesEmpresas getSslRecepcion() {
        return sslRecepcion;
    }

    public void setSslRecepcion(ConfiguracionesEmpresas sslRecepcion) {
        this.sslRecepcion = sslRecepcion;
    }

    public boolean isConnectSMTP() {
        return connectSMTP;
    }

    public void setConnectSMTP(boolean connectSMTP) {
        this.connectSMTP = connectSMTP;
    }

    public ConfiguracionesEmpresas getValidarAddenda() {
        return validarAddenda;
    }

    public void setValidarAddenda(ConfiguracionesEmpresas validarAdenda) {
        this.validarAddenda = validarAdenda;
    }

    public void reinit() {
        this.newEmail = "";
    }

    public void updateEmpresa() {
        boolean updateEmpresa = false;

        try {
            if (empresa.getNombre() != null && !empresa.getNombre().trim().isEmpty()) {
                if (empresa.getCalle() != null && !empresa.getCalle().trim().isEmpty()) {
                    updateEmpresa = daoEmpresa.updateEmpresa(empresa);
                    if (updateEmpresa) {
                        descBitacora = "[CONFIG_EMPRESA] " + usuario.getUserid() + ". Modifico los datos de la Empresa " + empresa.getRfc() + ".";
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los datos de la Empresa han sido Modificados.");
                        logger.info("updateEmpresa - " + usuario.getUserid() + ". Modificado los datos de la Empresa " + empresa.getRfc());
                    } else {
                        descBitacora = "[CONFIG_EMPRESA] " + usuario.getUserid() + ". Error Modificando los datos de la Empresa " + empresa.getRfc() + ".";
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos de la Empresa.");
                        logger.error("updateEmpresa " + usuario.getUserid() + ".  Error al modificar los datos de la Empresa " + empresa.getRfc());
                    }
                } else {
                    logger.debug("updateEmpresa - Calle de la Empresa es requerido");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Calle.");
                }
            } else {
                logger.debug("updateEmpresa - Nombre de la Empresa es requerido");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Nombre.");
            }

        } catch (Exception e) {
            descBitacora = "[CONFIG_EMPRESA] updateEmpresa - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". updateEmpresa - ERROR: " + e);
        }
        System.out.println("updateEmpresa " + updateEmpresa);
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("actualiza", updateEmpresa);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String insertNewEmail() {
        String str = "";
        try {
            if (newEmail != null && !newEmail.trim().isEmpty()) {
                if (emails.contains(newEmail)) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Duplicado", "Dirección de correo electrónico ya se ha agregado.");
                    logger.info("inserterNewEmail " + usuario.getUserid() + ". Direccion de correo electronico " + newEmail + " ya existe en la lista.");
                } else {
                    emails.add(newEmail);
                    logger.info("inserterNewEmail - dirección de correo electrónico agregado a la lista " + newEmail);
                    newEmail = "";
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Dirección de correo electrónico agradada.");
                    int i = 0;
                    for (String string : emails) {
                        if (i == 0) {
                            str += string;
                        } else {
                            str += "," + string;
                        }
                        i++;
                    }
                    configValor = str.trim();
                    logger.info(usuario.getUserid() + ". String con email: " + configValor);
                }
            } else {
                logger.warn("inserterNewEmail " + usuario.getUserid() + ".  Debe insertar un email valido");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, agregue una dirección de correo válida.");
            }
        } catch (Exception e) {
            descBitacora = "[CONFIG_EMPRESA] insertNewEmail - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al agregar la dirección de correo.");
            logger.error("inserterNewEmail " + usuario.getUserid() + " ERROR: " + e);
        }
        newEmail = "";
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
        return configValor;
    }

    public String removeEmail() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String email = (String) params.get("emailRemove");
        String str = "";
        emails.remove(email);
        logger.info("removeEmail " + usuario.getUserid() + ". Dirección de correo electrónico " + email + " ha sido removida de la lista.");
        int i = 0;
        for (String string : emails) {
            if (i == 0) {
                str += string;
            } else {
                str += "," + string;
            }
            i++;
        }
        configValor = str.trim();

        return configValor;
    }

    public void updateCE() {
        boolean addEmail = false;

        try {
            ceListaEmails.setValor(configValor);
            logger.debug("configvalor: " + configValor);
            logger.info("direcciones de correo electrónico " + ceListaEmails.getValor());

            addEmail = daoConfigEmpresa.updateConfiguracionEmpresa(ceListaEmails);

            if (addEmail) {
                descBitacora = "[CONFIG_EMPRESA] Usuario: " + this.usuario.getUserid() + " modifico la lista de correo que reciben notificaciones.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Dirección de correo electrónico ha sido registrada.");
                logger.info("updateCE - " + this.usuario.getUserid() + " ha modificado las direcciones de correo que reciben notificacioens");
            } else {
                logger.error("updateCE - " + this.usuario.getUserid() + " ha ocurrido un error al modificar correos que reciben notificaciones");
            }

        } catch (Exception e) {
            descBitacora = "[CONFIG_EMPRESA] updateCE - ERROR: " + e;
            registrarBitacora(this.usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". updateCE - ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al agregar la dirección de correo.");
        }
        newEmail = "";
        FacesContext.getCurrentInstance().addMessage(null, this.msg);
    }

    public void checkConfiguracionEmpresa() {
        logger.debug(usuario.getUserid() + ". Inicia checkConfiguracionEmpresa");
        updateConfigEmpresa(validoRemitente);
        updateConfigEmpresa(invalidoRemitente);
        updateConfigEmpresa(validoEmail);
        if (updateConfigEmpresa(invalidoEmail)) {
            descBitacora = "[CONFIG_EMPRESA] " + usuario.getUserid() + ". Modifico las notificaciones para XMLs válidos/inválidos Empresa seleccionada: " + empresa.getRfc();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Notificaciones se han modificado.");
            logger.info(usuario.getUserid() + ". Modifico las notificaciones para XMLs válidos/inválidos");
        } else {
            descBitacora = usuario.getUserid() + ". Error al modificar las notificaciones para XMLs válidos/inválidos";
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al modificar las Notificaciones");
            logger.error(usuario.getUserid() + ". Error al modificar las notificaciones para XMLs válidos/inválidos");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void conexionSMTP() {
        String codigo;
        logger.info(usuario.getUserid() + ". Inicia Prueba de conexion SMTP");
        try {
            if (servidorSMTP.getValor() != null && !servidorSMTP.getValor().isEmpty()) {
                if (puertoSMTP.getValor() != null && !puertoSMTP.getValor().isEmpty()) {
                    if (emailSMTP.getValor() != null && !emailSMTP.getValor().isEmpty()) {
                        if (enviarCodigo != null && !enviarCodigo.isEmpty()) {
                            logger.info("conexionSMTP " + usuario.getUserid() + ".  Generando codigo de confirmacion");
                            codigo = servidorSMTP.getValor() + puertoSMTP.getValor();

                            pw = encrypt(passSMTP.getValor());
                            logger.info("conexionSMTP " + usuario.getUserid() + ".  Encryptando contraseña del usuario SMTP " + passSMTP.getValor());

                            hash = encrypt(codigo);
                            logger.info("conexionSMTP " + usuario.getUserid() + ".  Encryptando codigo de confirmacion");
                            String asunto = "Prueba de conexión SMTP";
                            String contenido = "Código de confirmación: " + hash;
                            boolean ssl = Boolean.valueOf(SSL_SMTP.getValor());
                            ConexionSMTP conSMTP = new ConexionSMTP(servidorSMTP.getValor(), puertoSMTP.getValor(), ssl, true);
                            conSMTP.setUsername(emailSMTP.getValor());
                            conSMTP.setPassword(passSMTP.getValor());
                            conSMTP.getSession();
                            conSMTP.connect();
                            conSMTP.createMessage(enviarCodigo, asunto, contenido, false);
                            String conn;
                            conn = conSMTP.sendMessage();
                            logger.info(usuario.getUserid() + " Respuesta Conexion SMTP " + conn);
                            conSMTP.closeConnection();
                            if (conn.trim().charAt(0) == '2') {
                                connectSMTP = true;
                                descBitacora = "conexionSMTP " + usuario.getUserid() + " Prueba de Conexion SMTP exitosa. Codigo de confirmacion fue enviado a " + enviarCodigo;
                                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Prueba de conexión exitosa", "Por favor, ingrese el código de confirmación. Código enviado a " + enviarCodigo);
                                logger.info("conexionSMTP " + usuario.getUserid() + " Prueba de Conexion SMTP exitosa. Codigo de confirmacion fue enviado a " + enviarCodigo);
                            } else {
                                descBitacora = "conexionSMTP " + usuario.getUserid() + " Prueba de Conexion SMTP Fallo. Respuesta " + conn;
                                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Falló. Por favor, revise los datos.");
                                logger.error("conexionSMTP " + usuario.getUserid() + " Prueba de Conexion SMTP Fallida. Respuesta " + conn);
                            }
                        } else {
                            logger.warn("conexionSMTP - Por favor, ingrese un valor para Enviar código a");
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error Por favor, ingrese un valor para Enviar código a.");
                        }
                    } else {
                        logger.warn("conexionSMTP - Por favor, ingrese un valor para De");
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error Por favor, ingrese un valor para De.");
                    }
                } else {
                    logger.warn("conexionSMTP - Por favor, ingrese un valor para Puerto");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error Por favor, ingrese un valor para Puerto.");
                }
            } else {
                logger.warn("conexionSMTP - Por favor, ingrese un valor para Servidor");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Error Por favor, ingrese un valor para Servidor.");
            }
        } catch (CryptoException e) {
            descBitacora = "[CONFIG_EMPRESA] conexionSMTP CryptoException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + e.getMessage());
            logger.error("conexionSMTP " + usuario.getUserid() + ". CryptoException ERROR: " + e);
        } catch (IOException e) {
            descBitacora = "[CONFIG_EMPRESA] conexionSMTP - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + e.getMessage());
            logger.error("conexionSMTP " + usuario.getUserid() + ". IOException ERROR: " + e);
        } catch (MessagingException ex) {
            descBitacora = "[CONFIG_EMPRESA] conexionSMTP MessagingException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión fallo. Por favor, revise los datos. " + ex.getMessage());
            logger.error("conexionSMTP " + usuario.getUserid() + ". MessagingException ERROR: " + ex);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updateSMTP() {
        boolean smtp = false;
        logger.info(usuario.getUserid() + ". Inicia Update SMTP");
        if (hashSMTP != null && !hashSMTP.trim().equals("")) {
            if (hashSMTP.equals(hash)) {
                logger.info("updateSMTP - " + usuario.getUserid() + ". Código de confirmacion es correcto");
                updateConfigEmpresa(servidorSMTP);
                updateConfigEmpresa(puertoSMTP);
                updateConfigEmpresa(nombreSMTP);
                updateConfigEmpresa(emailSMTP);
                updateConfigEmpresa(usuarioSMTP);
                passSMTP.setValor(pw);
                updateConfigEmpresa(passSMTP);
                smtp = updateConfigEmpresa(SSL_SMTP);
                if (smtp) {
                    descBitacora = "[CONFIG_EMPRESA] " + usuario.getUserid() + ". Modifico las configuraciones de SMTP. Empresa " + empresa.getRfc();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Servidor SMTP configurado.");
                    logger.info("updateSMTP - " + usuario.getUserid() + ". Modifico las configuraciones de SMTP. Empresa " + empresa.getRfc());
                } else {
                    descBitacora = "[CONFIG_EMPRESA] " + usuario.getUserid() + ". Error al modificar las configuraciones de SMTP. Empresa " + empresa.getRfc();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Error al guardar las configuraciones para le servidor SMTP.");
                    logger.error("updateSMTP - " + usuario.getUserid() + ". Error al guardar las configuraciones para le servidor SMTP. Empresa " + empresa.getRfc());
                }
            } else {
                logger.info("Error código de confirmación no es válido.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error código de confirmación no es válido.");
            }
        } else {
            logger.info("introduzca el código de confirmación.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca el código de confirmación.");
        }
        this.hashSMTP = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("smtp", smtp);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void cancelarSMTP() {
        connectSMTP = false;
        hashSMTP = "";
    }

    public void conexionPOP() {
        boolean recepcion = false;
        logger.info(usuario.getUserid() + ". Inicia Prueba de Conexion POP/IMAP conexionPOP");
        try {
            if (!tipoServidor.equals("")) {
                if (servidorRecepcion.getValor() != null && !servidorRecepcion.getValor().trim().isEmpty()) {
                    if (puertoRecepcion.getValor() != null && !puertoRecepcion.getValor().trim().isEmpty()) {
                        if (usuarioRecepcion.getValor() != null && !usuarioRecepcion.getValor().trim().isEmpty()) {
                            List<ConfiguracionesEmpresas> configEmpresaPOP = daoConfigEmpresa.getConfiguracionEmpresaByPropiedadValor(ConfigEmpresa.EMAIL_RECEPCION.name(), usuarioRecepcion.getValor());
                            if (configEmpresaPOP != null && configEmpresaPOP.size() > 0) {
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dirección de correo ya se encuentra registrada. Por favor utilice otra cuenta de correo.");
                                FacesContext.getCurrentInstance().addMessage(null, msg);
                                logger.info(usuario.getUserid() + " Direccion de correo " + usuarioRecepcion.getValor() + " ya se encuentra registrada");
                                return;
                            }
                            boolean sslCon = Boolean.valueOf(sslRecepcion.getValor());
                            recepcion = Email.connect(servidorRecepcion.getValor(), usuarioRecepcion.getValor(), passRecepcion.getValor(), puertoRecepcion.getValor(), sslCon, tipoServidor);
                            if (recepcion) {
                                logger.info(usuario.getUserid() + ". conexionPOP - Encriptando contraseña");
                                pw = encrypt(passRecepcion.getValor());
                                logger.info(usuario.getUserid() + ". conexionPOP - Prueba de conexion " + tipoServidor + " exitosa");
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Prueba de Conexión " + tipoServidor + " exitosa.");
                            } else {
                                logger.error(usuario.getUserid() + ". conexionPOP - Conexion " + tipoServidor + " fallo.");
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexion " + tipoServidor + " Fallo. Por favor, revise los datos.");
                            }
                        } else {
                            logger.warn("Error Usuario es un campo requerido.");
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error usuario es un campo requerido.");
                        }
                    } else {
                        logger.warn("Error Puerto es un campo requerido.");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error puerto es un campo requerido.");
                    }
                } else {
                    logger.warn("Error Servidor es un campo requerido.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error servidor es un campo requerido.");
                }
            } else {
                logger.warn("Error debe seleccionar IMAP/POP3.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, seleccione un cliente de correo IMAP/POP3.");
            }
        } catch (CryptoException e) {
            descBitacora = "[CONFIG_EMPRESA] conexionPOP - CryptoException ERROR:" + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage());
            logger.error(usuario.getUserid() + ". conexionPOP CryptoException ERROR: " + e);
        } catch (IOException e) {
            descBitacora = "[CONFIG_EMPRESA] conexionPOP - IOException ERROR:" + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". conexionPOP IOException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + e.getMessage());
        } catch (Exception ex) {
            descBitacora = "[CONFIG_EMPRESA] conexionPOP - IOException ERROR:" + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error: " + ex.getMessage());
            logger.error(usuario.getUserid() + ". conexionPOP ERROR " + ex);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("recepcion", recepcion);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updatePOP() {
        boolean pop = false;
        logger.info(usuario.getUserid() + ". Inicia updatePOP");
        updateConfigEmpresa(servidorRecepcion);
        updateConfigEmpresa(puertoRecepcion);
        updateConfigEmpresa(usuarioRecepcion);
        passRecepcion.setValor(pw);
        updateConfigEmpresa(passRecepcion);
        protocoloRecepcion.setValor(tipoServidor);
        updateConfigEmpresa(protocoloRecepcion);
        pop = updateConfigEmpresa(sslRecepcion);
        if (pop) {
            descBitacora = "[CONFIG_EMPRESA] " + usuario.getUserid() + " modifico las configuraciones de " + tipoServidor + " de la empresa " + empresa.getRfc();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuración " + tipoServidor + " ha sido guardada.");
            logger.info(usuario.getUserid() + " modifico las configuraciones de " + tipoServidor + " de la empresa " + empresa.getRfc());
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar la configuracion para " + tipoServidor + ".");
            logger.error(usuario.getUserid() + ". Error al modificar las configuraciones de " + tipoServidor + " de la empresa " + empresa.getRfc());
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("pop", pop);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updateConfigAddenda() {
        logger.info("Inicia update Configuracion Addenda");
        if (updateConfigEmpresa(validarAddenda)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Validar Addenda", "Configuración para Addenda guardada correctamente."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validar Addenda", "Ocurrio un error al guardar la configuración de addenda."));
        }
    }

    private void configNotificaciones() {
        try {
            validoRemitente = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.NOTIFICAR_XML_VALIDO.name());
            invalidoRemitente = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.NOTIFICAR_XML_INVALIDO.name());
            validoEmail = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.XML_VALIDO_EMAIL.name());
            invalidoEmail = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.XML_INVALIDO_EMAIL.name());
            logger.info(usuario.getUserid() + ". Empresa " + empresa.getRfc() + " Configuracion de Notificaciones cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_EMPRESA] configNotificaciones - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            logger.error("configNotificaciones " + usuario.getUserid() + " ERROR al cargar configuraciones de notificaciones " + e);
        }
    }

    private void configEmail() {
        try {
            listaEmail = new ConfiguracionesEmpresas();
            listaEmail = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), "EMAILS_RECIBEN_NOTIFICACION");
            String allEmial[];
            allEmial = listaEmail.getValor().split(",");

            for (String string : allEmial) {
                if (!string.isEmpty()) {
                    emails.add(string);
                }
            }
            logger.debug(usuario.getUserid() + ". Empresa " + empresa.getRfc() + " Configuracion lista de correos que reciben notificaciones cargadas");

        } catch (Exception e) {
            descBitacora = "[CONFIG_EMPRESA] configEmail - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error("configEmail " + usuario.getUserid() + " ERROR al cargar configuraciones de Email " + e);
        }
    }

    private void configSMTP() {

        try {
            servidorSMTP = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.HOST_SMTP.name());
            puertoSMTP = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PUERTO_SMTP.name());
            nombreSMTP = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.NOMBRE_SMTP.name());
            emailSMTP = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.EMAIL_SMTP.name());
            usuarioSMTP = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.USUARIO_SMTP.name());
            passSMTP = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PASS_SMTP.name());
            SSL_SMTP = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.SSL_SMTP.name());
            logger.debug(usuario.getUserid() + ". Empresa " + empresa.getRfc() + " Configuracion servidor SMTP cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_EMPRESA] configSMTP - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error("configSMTP " + usuario.getUserid() + " ERROR al cargar configuraciones SMTP " + e);
        }
    }

    private void configRecepcion() {
        try {
            servidorRecepcion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.HOST_RECEPCION.name());
            puertoRecepcion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PUERTO_RECEPCION.name());
            usuarioRecepcion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.EMAIL_RECEPCION.name());
            passRecepcion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.RECEPCION_PASS.name());
//          tlsRecepcion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), "START_TLS");
            sslRecepcion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.UTILIZAR_SSL.name());
            protocoloRecepcion = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PROTOCOLO_RECEPCION.name());
            logger.debug(usuario.getUserid() + ". Empresa " + empresa.getRfc() + " Configuracion servidor recepcion cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_EMPRESA] configRecepcion - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error("configRecepcion " + usuario.getUserid() + " ERROR al cargar configuraciones Recepcion " + e);
        }
    }

    private void configValidarAddenda() {
        try {
            validarAddenda = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), "VALIDAR_ADDENDA");
        } catch (Exception ex) {
            descBitacora = "[CONFIG_EMPRESA] configValidarAdenda - ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error("configValidarAdenda " + usuario.getUserid() + " ERROR al cargar configuraciones Validar adenda " + ex);
        }
    }

    public String reloadNotificacion() {
        return "/Configuracion/notificaciones?faces-redirect=true";
    }

    public String reloadEmail() {
        return "/Configuracion/email?faces-redirect=true";
    }

    public String reloadConfEmpresa() {
        return "/Configuracion/empresa?faces-redirect=true";
    }

    private boolean updateConfigEmpresa(ConfiguracionesEmpresas configEmpresa) {
        boolean respuesta = false;
        try {
            if (daoConfigEmpresa.updateConfiguracionEmpresa(configEmpresa)) {
                descBitacora = "[CONFIG_EMPRESA] updateConfigEmpresa - Modifico Configuraciones de la empresa " + empresa.getRfc() + " Configuracion " + configEmpresa.getPropiedad();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Modifico Configuraciones de la empresa " + empresa.getRfc() + " Configuracion " + configEmpresa.getPropiedad());
                respuesta = true;
            } else {
                descBitacora = "[CONFIG_EMPRESA] updateConfigEmpresa - Error al modificar Configuraciones de la empresa " + empresa.getRfc() + " Conifuracion " + configEmpresa.getPropiedad();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al modificar Configuraciones de la empresa " + empresa.getRfc() + " Conifuracion " + configEmpresa.getPropiedad());
            }
        } catch (Exception e) {
            descBitacora = "[CONFIG_EMPRESA] updateConfigEmpresa - ERROR:" + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". updateConfigEmpresa - ERROR: " + e);
        }
        return respuesta;
    }

}
