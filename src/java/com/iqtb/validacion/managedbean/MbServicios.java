/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConfiguracionServicios;
import com.iqtb.validacion.dao.DaoServicio;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.ServAcceso;
import com.iqtb.validacion.emun.ServActualizacion;
import com.iqtb.validacion.emun.ServConfigXSA;
import com.iqtb.validacion.emun.ServEnviador;
import com.iqtb.validacion.emun.ServGenerador;
import com.iqtb.validacion.emun.ServReceptor;
import com.iqtb.validacion.emun.ServSMTP;
import com.iqtb.validacion.emun.ServValidador;
import com.iqtb.validacion.emun.Servicio;
import static com.iqtb.validacion.encrypt.Encrypt.encrypt;
import com.iqtb.validacion.mail.ConexionSMTP;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import com.iqtb.validacion.pojo.Servicios;
import com.iqtb.validacion.pojo.Usuarios;
import com.iqtb.validacion.util.ActualizaWS;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.TratarConexiones;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import net.sourceforge.lightcrypto.CryptoException;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbServicios implements Serializable {

    private Servicios servicioRecepcion;
    private Servicios servicioEnviador;
    private Servicios servicioValidador;
    private Servicios servicioAcceso;
    private ConfiguracionesServicios teRecepcion;
    private ConfiguracionesServicios trcRecepcion;
    private ConfiguracionesServicios teValidacion;
    private ConfiguracionesServicios trcValidacion;
    private ConfiguracionesServicios wsValidacion;
    private ConfiguracionesServicios teEnvio;
    private ConfiguracionesServicios trcEnvio;
    private ConfiguracionesServicios mdEnvio;
    private ConfiguracionesServicios expPass;
    private ConfiguracionesServicios inactividad;
    private ConfiguracionesServicios minPass;
    private ConfiguracionesServicios minIntentos;
    private Servicios servSMTP;
    private ConfiguracionesServicios hostSMTP;
    private ConfiguracionesServicios portSMTP;
    private ConfiguracionesServicios emailSMTP;
    private ConfiguracionesServicios userSMTP;
    private ConfiguracionesServicios passSMTP;
    private ConfiguracionesServicios sslSMTP;
    private ConfiguracionesServicios nombreSMTP;
    private String codigoEmail;
    private String passEncrypt;
    private String hash;
    private String hashSMTP;
    private Servicios servGenerador;
    private ConfiguracionesServicios teContabilidad;
    private ConfiguracionesServicios trcContabilidad;
//    private boolean btnFavicon;
    private Servicios servActualizar;
    private ConfiguracionesServicios teActualizar;
    private ConfiguracionesServicios trcActualizar;
    private Servicios servXSA;
    private ConfiguracionesServicios wsActuzliza;
    private ConfiguracionesServicios wsValida;
    private ConfiguracionesServicios keyServ;
    private String ipXsa;
    private String portXsa;
    private Usuarios usuario;
    private final String sessionUsuario;
    private FacesMessage msg;
    private String descBitacora;
    private boolean connectSMTP;
    private boolean connectWS;
    private static final Logger logger = Logger.getLogger(MbServicios.class);

    public MbServicios() {
        servSMTP = new Servicios();
        usuario = new Usuarios();
//        btnFavicon = false;
        connectSMTP = false;
        connectWS = false;
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = new DaoUsuario().getByUserid(sessionUsuario);
            servRecepcion();
            servValidacion();
            servEnvio();
            servAcceso();
            configSMTP();
            servContabilidad();
            servActualizar();
            servConfigXSA();
        } catch (Exception e) {
            descBitacora = "[MbServicios] ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("Error al obtener el Usuario ERROR: " + e);
        }

    }

    public void updateServRecepcion() {
        boolean servRe = false;

        if (teRecepcion.getValor() != null && !teRecepcion.getValor().isEmpty()) {
            if (trcRecepcion.getValor() != null && !trcRecepcion.getValor().isEmpty()) {
                updateCondfigServicio(teRecepcion);
                servRe = updateCondfigServicio(trcRecepcion);
                if (servRe) {
                    descBitacora = "[CONFIG_SERVICIOS] " + usuario.getUserid() + ". Las configuraciones para el servicio " + servicioRecepcion.getNombre() + " han sido modificadas.";
                    registrarBitacora(usuario.getIdUsuario(), servicioRecepcion.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                    logger.info("updateServRecepcion - " + usuario.getUserid() + ". Las configuraciones para el servicio " + this.servicioRecepcion.getNombre() + " han sido modificadas.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para el servicio de Recepción han sido modificadas.");
                } else {
                    logger.error("updateServRecepcion - " + usuario.getUserid() + ". ERROR al modificar las configuraciones para el servicio de recepcion.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar los cambios. Por favor, vuelva a intentarlo.");
                }
            } else {
                logger.warn("updateServRecepcion - Error Tiempo de espera es un campo requerido.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración.");
            }
        } else {
            logger.warn("updateServRecepcion - Tiempo de espera entre cada ciclo.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo.");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioRecepcion", servRe);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String reloadRecepcion() {
        return "/Configuracion/servicioRecepcion?faces-redirect=true";
    }

    public void updateServValidacion() {
        boolean servVa = false;

        if (teValidacion.getValor() != null && !teValidacion.getValor().isEmpty()) {
            if (trcValidacion.getValor() != null && !trcValidacion.getValor().isEmpty()) {
                if (wsValidacion.getValor() != null && !wsValidacion.getValor().isEmpty()) {
                    updateCondfigServicio(teValidacion);
                    updateCondfigServicio(trcValidacion);
                    servVa = updateCondfigServicio(wsValidacion);
                    if (servVa) {
                        descBitacora = "[CONFIG_SERVICIOS] " + usuario.getUserid() + " Las configuraciones para el servicio " + servicioValidador.getNombre() + " han sido modificadas.";
                        registrarBitacora(usuario.getIdUsuario(), servicioValidador.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                        logger.info("updateServValidacion - " + usuario.getUserid() + " Las configuraciones para el servicio " + servicioValidador.getNombre() + " han sido modificadas.");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para el servicio de Validación han sido modificadas.");
                    } else {
                        logger.error("updateServValidacion - " + usuario.getUserid() + ". ERROR al modificar las configuraciones para el servicio de validacion.");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar los cambios. Por favor, vuelva a intentarlo.");
                    }
                } else {
                    logger.warn("updateServValidacion - Error WebService es un campo requerido.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Webservice.");
                }
            } else {
                logger.warn("updateServValidacion - Error Tiempo de espera es un campo requerido.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración.");
            }
        } else {
            logger.warn("updateServValidacion - Tiempo de espera entre cada ciclo es un campo requerido.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo.");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioValidacion", servVa);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String reloadValidacion() {
        return "/Configuracion/servicioValidacion?faces-redirect=true";
    }

    public void updateServEnvio() {
        boolean servEn = false;

        if (teEnvio.getValor() != null && !teEnvio.getValor().isEmpty()) {
            if (trcEnvio.getValor() != null && !trcEnvio.getValor().isEmpty()) {
                if (mdEnvio.getValor() != null && !mdEnvio.getValor().isEmpty()) {
                    updateCondfigServicio(teEnvio);
                    updateCondfigServicio(trcEnvio);
                    servEn = updateCondfigServicio(mdEnvio);
                    if (servEn) {
                        descBitacora = "[CONFIG_SERVICIOS] " + usuario.getUserid() + " Las configuraciones para el servicio " + servicioEnviador.getNombre() + " han sido modificadas.";
                        registrarBitacora(usuario.getIdUsuario(), servicioEnviador.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                        logger.info("updateServEnvio - " + usuario.getUserid() + " Las configuraciones para el servicio " + servicioEnviador.getNombre() + " han sido modificadas.");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para el servicio de envío de notificaciones han sido modificadas.");
                    } else {
                        logger.error("updateServEnvio - " + usuario.getUserid() + ". ERROR al modificar las configuraciones para el servicio Enviador.");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar los cambios. Por favor, vuelva a intentarlo.");
                    }
                } else {
                    logger.warn("updateServEnvio - Error minimo de documentos es un campo requerido.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Mínimo de documentos encolados.");
                }
            } else {
                logger.warn("updateServEnvio - Error Tiempo de espera es un campo requerido.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración.");
            }
        } else {
            logger.warn("updateServEnvio - Tiempo de espera entre cada ciclo es un campo requerido.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo.");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioEnvio", servEn);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String reloadServNotificacion() {
        return "/Configuracion/servicioNotificaciones?faces-redirect=true";
    }

    public void updateServAcceso() {
        boolean servAcceso = false;

        if (expPass.getValor() != null && !expPass.getValor().isEmpty()) {
            if (inactividad.getValor() != null && !inactividad.getValor().isEmpty()) {
                if (minPass.getValor() != null && !minPass.getValor().isEmpty()) {
                    if (minIntentos.getValor() != null && !minIntentos.getValor().isEmpty()) {
                        updateCondfigServicio(expPass);
                        updateCondfigServicio(inactividad);
                        updateCondfigServicio(minPass);
                        servAcceso = updateCondfigServicio(minIntentos);
                        if (servAcceso) {
                            descBitacora = "[CONFIG_SERVICIOS] " + usuario.getUserid() + " Las configuraciones para el servicio " + servicioAcceso.getNombre() + " han sido modificadas.";
                            registrarBitacora(usuario.getIdUsuario(), servicioAcceso.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                            logger.info("updateServAcceso - " + usuario.getUserid() + " Las configuraciones para el servicio " + servicioAcceso.getNombre() + " han sido modificadas.");
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para Cuentas de usuario han sido modificadas.");
                        } else {
                            logger.error("updateServAcceso - " + usuario.getUserid() + ". ERROR al modificar las configuraciones para el servicio acceso.");
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar los cambios. Por favor, vuelva a intentarlo.");
                        }
                    } else {
                        logger.warn("updateServAcceso - Número de intentos fallidos es un campo requerido.");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Número de intentos fallidos.");
                    }
                } else {
                    logger.warn("updateServAcceso - Longitud mínima de contraseñas es un campo requerido.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Longitud mínima de contraseñas.");
                }
            } else {
                logger.warn("updateServAcceso - Días de inactividad permitidos es un campo requerido.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Días de inactividad permitidos.");
            }
        } else {
            logger.warn("updateServAcceso - Dias para expirar la contraseña es un campo requerido.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Días para expirar contraseña.");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioAcceso", servAcceso);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String reloadServAcceso() {
        return "/Configuracion/servicioUsuarios?faces-redirect=true";
    }

    public void conexionSMTP() {
        String codigo;
        try {
            if (hostSMTP.getValor() != null && !hostSMTP.getValor().isEmpty()) {
                if (portSMTP.getValor() != null && !portSMTP.getValor().isEmpty()) {
                    if (emailSMTP.getValor() != null && !emailSMTP.getValor().isEmpty()) {
                        if (codigoEmail != null && !codigoEmail.isEmpty()) {
                            codigo = hostSMTP.getValor() + portSMTP.getValor();
                            logger.info("encrypt contraseña " + passSMTP.getValor());
                            passEncrypt = encrypt(passSMTP.getValor());
                            logger.info("conexionSMTP " + usuario.getUserid() + " Generando codigo de confirmacion");
                            hash = encrypt(codigo);
                            logger.info("conexionSMTP " + usuario.getUserid() + " Encryptando codigo de confirmacion");
                            String asunto = "Prueba de conexión SMTP";
                            String contenido = "Código de confirmación: " + hash;
                            boolean ssl = Boolean.valueOf(sslSMTP.getValor());
                            ConexionSMTP conSMTP = new ConexionSMTP(hostSMTP.getValor(), portSMTP.getValor(), ssl, true);
                            conSMTP.setUsername(emailSMTP.getValor());
                            conSMTP.setPassword(passSMTP.getValor());
                            conSMTP.getSession();
                            conSMTP.connect();
                            conSMTP.createMessage(codigoEmail, asunto, contenido, false);
                            String conn;
                            conn = conSMTP.sendMessage();
                            logger.info(usuario.getUserid() + "Respuesta del servidor SMTP " + conn);
                            conSMTP.closeConnection();
                            if (conn.trim().charAt(0) == '2') {
                                connectSMTP = true;
                                descBitacora = "conexionSMTP " + usuario.getUserid() + " Prueba de Conexion SMTP exitosa. Codigo de confirmacion fue enviado a " + codigoEmail;
                                registrarBitacora(usuario.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Prueba de conexión exitosa", "Por favor, ingrese el código de confirmación. Código enviado a " + codigoEmail);
                                logger.info("conexionSMTP " + usuario.getUserid() + " Prueba de Conexion SMTP exitosa. Codigo de confirmacion fue enviado a " + codigoEmail);
                            } else {
                                descBitacora = "conexionSMTP " + usuario.getUserid() + " Prueba de Conexion SMTP Fallo. Respuesta " + conn;
                                registrarBitacora(usuario.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Falló. Por favor, revise los datos.");
                                logger.error("conexionSMTP " + usuario.getUserid() + " Prueba de Conexion SMTP Fallida. Respuesta " + conn);
                            }
                        } else {
                            logger.warn("conexionSMTP - Por favor, ingrese un valor para Enviar código a");
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error Por favor, ingrese un valor para Enviar código a.");
                        }
                    } else {
                        logger.warn("conexionSMTP - Por favor, ingrese un valor para De");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error Por favor, ingrese un valor para De.");
                    }
                } else {
                    logger.warn("conexionSMTP - Por favor, ingrese un valor para Puerto");
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error Por favor, ingrese un valor para Puerto.");
                }
            } else {
                logger.warn("conexionSMTP - Por favor, ingrese un valor para Servidor");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error Por favor, ingrese un valor para Servidor.");
            }
        } catch (CryptoException e) {
            descBitacora = "[SERVICIO] conexionSMTP - CryptoException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + e.getMessage());
            logger.error("conexionSMTP " + usuario.getUserid() + " CryptoException ERROR: " + e);
        } catch (IOException e) {
            descBitacora = "[SERVICIO] conexionSMTP - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + e.getMessage());
            logger.error("conexionSMTP " + usuario.getUserid() + " IOException ERROR: " + e);
        } catch (MessagingException ex) {
            descBitacora = "[SERVICIO] conexionSMTP - ERROR: " + ex.getLocalizedMessage();
            registrarBitacora(usuario.getIdUsuario(), servSMTP.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Prueba de conexión Fallo. Por favor, revise los datos. " + ex.getMessage());
            logger.error("conexionSMTP " + usuario.getUserid() + " MessagingException ERROR: " + ex);
        }
        codigoEmail = "";
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updateSMTP() {
        boolean smtp = false;

        if (hashSMTP != null && !hashSMTP.trim().equals("")) {
            logger.info("updateSMTP - " + usuario.getUserid() + " Código de confirmacion es correcto");
            if (hashSMTP.equals(hash)) {
                updateCondfigServicio(hostSMTP);
                updateCondfigServicio(portSMTP);
                updateCondfigServicio(nombreSMTP);
                updateCondfigServicio(emailSMTP);
                updateCondfigServicio(userSMTP);
                passSMTP.setValor(passEncrypt);
                updateCondfigServicio(passSMTP);
                smtp = updateCondfigServicio(sslSMTP);
                if (smtp) {
                    String descripcion = "[SERVICIOS] " + usuario.getUserid() + " modifico las configuraciones de SMTP.";
                    registrarBitacora(usuario.getIdUsuario(), servSMTP.getIdServicio(), null, descripcion, BitacoraTipo.INFO.name());
                    logger.info("updateSMTP - Usuario: " + usuario.getUserid() + " modifico las configuraciones de SMTP");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Servidor SMTP configurado.");
                    hash = "";
                } else {
                    logger.error("updateSMTP - Usuario: " + usuario.getUserid() + " Error al guardar las configuraciones para le servidor SMTP.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Error al guardar las configuraciones para le servidor SMTP.");
                }
            } else {
                logger.warn("Error código de confirmación no es válido.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error código de confirmación es incorrecto.");
            }
        } else {
            logger.warn("introduzca el código de confirmación.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca el código de confirmación.");
        }
        hashSMTP = "";
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("smtp", smtp);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String reloadSMTP() {
        return "/Configuracion/servicioSMTP?faces-redirect=true";
    }

    public void cancelarSMTP() {
        connectSMTP = false;
        hashSMTP = "";
    }

    public void uploadLogo(FileUploadEvent event) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".png\""));
                logger.warn(usuario.getUserid() + ". Seleccione un archivo para logo");
                return;
            }

            if (!nombreArchivo.endsWith(".png")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo debe ser con extensión \".png\""));
                logger.warn(usuario.getUserid() + ". tipo de imagen no valida " + nombreArchivo);
                return;
            }

            if (event.getFile().getSize() > 2097152) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 2MB"));
                logger.warn(usuario.getUserid() + ". El archivo es mayor a 2mb archivo " + nombreArchivo + " " + event.getFile().getSize());
                return;
            }

            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String carpeta = (String) servletContext.getRealPath("/resources/images");

            outputStream = new FileOutputStream(new File(carpeta + "/logo.png"));
            inputStream = event.getFile().getInputstream();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", "Logo modificado correctamente."));
            descBitacora = usuario.getUserid() + ". Logo modificado correctamente.";
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
            logger.info(usuario.getUserid() + ". Logo modificado correctamente.");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al subir la imagen ERROR: " + ex.getMessage()));
            descBitacora = usuario.getUserid() + ". Ha ocurrido un error al subir la imagen ERROR: " + ex;
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". Ha ocurrido un error al subir la imagen ERROR: " + ex);
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
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". Ha ocurrido un error outputStream.close ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". Ha ocurrido un error outputStream.close ERROR: " + ex);
                }
            }
        }
    }

    public void uploadIco(FileUploadEvent event) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".ico\""));
                logger.warn(usuario.getUserid() + ". Seleccione un archivo para favicon");
                return;
            }

            if (!nombreArchivo.endsWith(".ico")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo debe ser con extensión \".ico\""));
                logger.warn(usuario.getUserid() + ". tipo de imagen no valida " + nombreArchivo);
                return;
            }

            if (event.getFile().getSize() > 2097152) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "El archivo no puede ser más de 2MB"));
                logger.warn(usuario.getUserid() + ". El archivo es mayor a 2mb archivo " + nombreArchivo + " " + event.getFile().getSize());
                return;
            }

            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String carpeta = (String) servletContext.getRealPath("/resources/images");

            outputStream = new FileOutputStream(new File(carpeta + "/favicon.ico"));
            inputStream = event.getFile().getInputstream();

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", "Icono modificado correctamente."));
            descBitacora = usuario.getUserid() + ". Icono modificado correctamente.";
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
            logger.info(usuario.getUserid() + "Icono modificado correctamente.");
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al subir la imagen ERROR: " + ex.getMessage()));
            descBitacora = usuario.getUserid() + ". actualizarIcono - Ha ocurrido un error al subir la imagen ERROR: " + ex;
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + "actualizarIcono - Ha ocurrido un error al subir la imagen ERROR: " + ex);
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
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    descBitacora = usuario.getUserid() + ". Ha ocurrido un error outputStream.close ERROR: " + ex.getMessage();
                    registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                    logger.error(usuario.getUserid() + ". Ha ocurrido un error outputStream.close ERROR: " + ex);
                }
            }
        }
    }

    public void updateServContabilidad() {
        boolean servCont = false;

        if (teContabilidad.getValor() != null && !teContabilidad.getValor().isEmpty()) {
            if (trcContabilidad.getValor() != null && !trcContabilidad.getValor().isEmpty()) {
                updateCondfigServicio(teContabilidad);
                servCont = updateCondfigServicio(trcContabilidad);
                if (servCont) {
                    String descripcion = "[CONFIG_SERVICIOS] " + usuario.getUserid() + " Las configuraciones para el servicio " + servGenerador.getNombre() + " han sido modificadas.";
                    registrarBitacora(usuario.getIdUsuario(), servGenerador.getIdServicio(), null, descripcion, BitacoraTipo.INFO.name());
                    logger.info("updateServContabilidad - " + usuario.getUserid() + " Las configuraciones para el servicio " + servGenerador.getNombre() + " han sido modificadas.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones para el servicio de Contabilidad Electrónica han sido modificadas.");
                } else {
                    logger.error("updateServContabilidad - " + usuario.getUserid() + " Error modificando las configuraciones para el servicio " + servGenerador.getNombre());
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando as configuraciones para el servicio de Contabilidad Electrónica.");
                }
            } else {
                logger.warn("updateServContabilidad - Error Tiempo de espera es un campo requerido.");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración.");
            }
        } else {
            logger.warn("updateServContabilidad - Tiempo de espera entre cada ciclo es un campo requerido.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo.");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioGenerador", servCont);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String reloadContabilidad() {
        return "/Configuracion/servicioContabilidad?faces-redirect=true";
    }

    public void updateServXSA() {
        boolean xsa = false;
        if (portXsa != null && !portXsa.trim().isEmpty()) {
            logger.info("colocando puerto " + portXsa);
            wsActuzliza.setValor("http://" + ipXsa + ":" + portXsa + "/contabilidadWS/actualizacionCfds?wsdl");
            wsValida.setValor("http://" + ipXsa + ":" + portXsa + "/xsamanager/servicesb2b/RecepcionXMLService?wsdl");
        } else {
            wsActuzliza.setValor("http://" + ipXsa + "/contabilidadWS/actualizacionCfds?wsdl");
            wsValida.setValor("http://" + ipXsa + "/xsamanager/servicesb2b/RecepcionXMLService?wsdl");
        }
        logger.info("ws Actualiza " + wsActuzliza.getValor());
        logger.info("ws Valida " + wsValida.getValor());
        updateCondfigServicio(wsActuzliza);
        updateCondfigServicio(wsValida);
        xsa = updateCondfigServicio(keyServ);
        if (xsa) {
            String descripcion = "[CONFIG_SERVICIOS] " + usuario.getUserid() + " Las configuraciones para el servicio " + servXSA.getNombre() + " han sido modificadas.";
            registrarBitacora(usuario.getIdUsuario(), servXSA.getIdServicio(), null, descripcion, BitacoraTipo.INFO.name());
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones del servicio han sido modificadas.");
            logger.info("updateServXSA - " + usuario.getUserid() + " Las configuraciones para el servicio " + servXSA.getNombre() + " han sido modificadas.");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando las configuraciones del servicio.");
            logger.info("updateServXSA - " + usuario.getUserid() + " Error modificando las configuraciones para el servicio " + servXSA.getNombre());
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioXSA", xsa);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void conexionWS() {
        if (ipXsa != null && !ipXsa.isEmpty()) {
            if (keyServ.getValor() != null && !keyServ.getValor().isEmpty()) {
                try {
                    URL urlActualiza = null;
                    if (portXsa != null && !portXsa.trim().isEmpty()) {
                        urlActualiza = new URL("http://" + ipXsa + ":" + portXsa + "/contabilidadWS/actualizacionCfds?wsdl");
                    } else {
                        urlActualiza = new URL("http://" + ipXsa + "/contabilidadWS/actualizacionCfds?wsdl");
                    }
                    logger.info("url " + urlActualiza);
                    ActualizaWS actualiza = new ActualizaWS(urlActualiza);
                    TratarConexiones tratarConexion = new TratarConexiones();
                    if (tratarConexion.revisarURL(urlActualiza)) {
                        connectWS = actualiza.verificarConexion(keyServ.getValor());
                        logger.info("connectWS " + connectWS);
                        if (connectWS) {
                            logger.info("conexionWS - Prueba de conexión con WS correcto.");
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Conexión Exitosa", "");
                        } else {
                            logger.error("conexionWS - Prueba de conexión con WS fallo.");
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conexión Fallo", "Verifique Key del servidor.");
                        }
                    } else {
                        logger.error("conexionWS - URL conexion faliida. Verifique IP XSA");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Prueba de Conexión Fallo", "Verifique IP XSA.");
                    }
                } catch (MalformedURLException e) {
                    logger.error("ERROR " + e);
                }
            } else {
                logger.warn("conexionWS - key servidor es un campo requerido.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Key Servidor.");
            }
        } else {
            logger.warn("conexionWS - ip XSA  es un campo requerido.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para IP XSA.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void resetWS() {
        ipXsa = "";
        portXsa = "";
        connectWS = false;
    }

    public void updateServActualizar() {
        boolean servActualiza = false;

        if (teActualizar.getValor() != null && !teActualizar.getValor().isEmpty()) {
            if (trcActualizar.getValor() != null && !trcActualizar.getValor().isEmpty()) {
                updateCondfigServicio(teActualizar);
                servActualiza = updateCondfigServicio(trcActualizar);
                if (servActualiza) {
                    String descripcion = "[CONFIG_SERVICIOS] Usuario: " + this.usuario.getUserid() + " Las configuraciones para el servicio " + servActualizar.getNombre() + " han sido modificadas.";
                    registrarBitacora(usuario.getIdUsuario(), servActualizar.getIdServicio(), null, descripcion, BitacoraTipo.INFO.name());
                    logger.info("updateServActualizar - " + usuario.getUserid() + " Las configuraciones para el servicio " + servActualizar.getNombre() + " han sido modificadas.");
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Las configuraciones del servicio han sido modificadas.");
                } else {
                    logger.error("updateServActualizar - " + usuario.getUserid() + " Error al modificar las configuraciones para el servicio " + servActualizar.getNombre());
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al modificar las configuraciones del servicio");
                }
            } else {
                logger.warn("updateServActualizar - Error Tiempo de espera es un campo requerido.");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración.");
            }
        } else {
            logger.warn("updateServActualizar - Tiempo de espera entre cada ciclo es un campo requerido.");
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo.");
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioActuzliza", servActualiza);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String reloadActualizar() {
        return "/Configuracion/servicioActualizar?faces-redirect=true";
    }

    public String reloadConfigXsa() {
        return "/Configuracion/servicioConfigXSA?faces-redirect=true";
    }

    private void configSMTP() {

        try {
            servSMTP = new DaoServicio().getServicoByNombre(Servicio.SERVIDOR_SMTP.name());
            DaoConfiguracionServicios daoConfigServicios = new DaoConfiguracionServicios();
            hostSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.HOST_SMTP.name());
            if (hostSMTP == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion HOST_SMTP para el servicio " + servSMTP.getNombre());
                hostSMTP = new ConfiguracionesServicios();
                hostSMTP.setIdServicio(servSMTP.getIdServicio());
                hostSMTP.setPropiedad("HOST_SMTP");
                hostSMTP.setValor("");
                hostSMTP.setDescripcion("Indica el puerto a utilizar para SMTP.");
                hostSMTP.setTipo("TEXTO");
                updateCondfigServicio(hostSMTP);
            }
            portSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.PORT_SMTP.name());
            if (portSMTP == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion PORT_SMTP para el servicio " + servSMTP.getNombre());
                portSMTP = new ConfiguracionesServicios();
                portSMTP.setIdServicio(servSMTP.getIdServicio());
                portSMTP.setPropiedad("PORT_SMTP");
                portSMTP.setValor("");
                portSMTP.setDescripcion("Indica el puerto a utilizar para SMTP.");
                portSMTP.setTipo("TEXTO");
                updateCondfigServicio(portSMTP);
            }
            emailSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.EMAIL_SMTP.name());
            if (emailSMTP == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion EMAIL_SMTP para el servicio " + servSMTP.getNombre());
                emailSMTP = new ConfiguracionesServicios();
                emailSMTP.setIdServicio(servSMTP.getIdServicio());
                emailSMTP.setPropiedad("EMAIL_SMTP");
                emailSMTP.setValor("");
                emailSMTP.setDescripcion("Direccion de correo electrónico para SMTP.");
                emailSMTP.setTipo("TEXTO");
                updateCondfigServicio(emailSMTP);
            }
            userSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.USER_SMTP.name());
            if (userSMTP == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion USER_SMTP para el servicio " + servSMTP.getNombre());
                userSMTP = new ConfiguracionesServicios();
                userSMTP.setIdServicio(servSMTP.getIdServicio());
                userSMTP.setPropiedad("USER_SMTP");
                userSMTP.setValor("");
                userSMTP.setDescripcion("Usuario SMTP.");
                userSMTP.setTipo("TEXTO");
                updateCondfigServicio(userSMTP);
            }
            passSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.PASS_SMTP.name());
            if (passSMTP == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion PASS_SMTP para el servicio " + servSMTP.getNombre());
                passSMTP = new ConfiguracionesServicios();
                passSMTP.setIdServicio(servSMTP.getIdServicio());
                passSMTP.setPropiedad("PASS_SMTP");
                passSMTP.setValor("");
                passSMTP.setDescripcion("Contraseña de correo electrónico para SMTP.");
                passSMTP.setTipo("TEXTO");
                updateCondfigServicio(passSMTP);
            }
            sslSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.SSL_SMTP.name());
            if (sslSMTP == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion SSL_SMTP para el servicio " + servSMTP.getNombre());
                sslSMTP = new ConfiguracionesServicios();
                sslSMTP.setIdServicio(servSMTP.getIdServicio());
                sslSMTP.setPropiedad("SSL_SMTP");
                sslSMTP.setValor("");
                sslSMTP.setDescripcion("Informa si se utiliza SSL en SMTP.");
                sslSMTP.setTipo("BOOLEANO");
                updateCondfigServicio(sslSMTP);
            }
            nombreSMTP = daoConfigServicios.getConfigServicioByIdServicioPropiedad(servSMTP.getIdServicio(), ServSMTP.NOMBRE_SMTP.name());
            if (nombreSMTP == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion NOMBRE_SMTP para el servicio " + servSMTP.getNombre());
                nombreSMTP = new ConfiguracionesServicios();
                nombreSMTP.setIdServicio(servSMTP.getIdServicio());
                nombreSMTP.setPropiedad("NOMBRE_SMTP");
                nombreSMTP.setValor("");
                nombreSMTP.setDescripcion("Nombre SMTP.");
                nombreSMTP.setTipo("TEXTO");
                updateCondfigServicio(nombreSMTP);
            }
            logger.debug(usuario.getUserid() + ". Configuraciones de servicio SMTP cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_SERV] configSMTP - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("configSMTP - ERROR al cargar configuraciones SMTP " + e);
        }
    }

    private void servRecepcion() {
        try {
            servicioRecepcion = new DaoServicio().getServicoByNombre(Servicio.RECEPTOR.name());
            DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
            teRecepcion = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioRecepcion.getIdServicio(), ServReceptor.TIEMPO_ESPERA.name());
            if (teRecepcion == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_ESPERA para el servicio " + servicioRecepcion.getNombre());
                teRecepcion = new ConfiguracionesServicios();
                teRecepcion.setIdServicio(servSMTP.getIdServicio());
                teRecepcion.setPropiedad("TIEMPO_ESPERA");
                teRecepcion.setValor("");
                teRecepcion.setDescripcion("Tiempo de espera entre cada ciclo.");
                teRecepcion.setTipo("NUMERO");
                updateCondfigServicio(teRecepcion);
            }
            trcRecepcion = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioRecepcion.getIdServicio(), ServReceptor.TIEMPO_REC_CONFIG.name());
            if (trcRecepcion == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_REC_CONFIG para el servicio " + servicioRecepcion.getNombre());
                trcRecepcion = new ConfiguracionesServicios();
                trcRecepcion.setIdServicio(servicioRecepcion.getIdServicio());
                trcRecepcion.setPropiedad("TIEMPO_REC_CONFIG");
                trcRecepcion.setValor("");
                trcRecepcion.setDescripcion("Tiempo de espera para recargar configuración.");
                trcRecepcion.setTipo("NUMERO");
                updateCondfigServicio(trcRecepcion);
            }
            logger.debug(usuario.getUserid() + ". Configuraciones de servicio Receptor cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_SERV] servRecepcion - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("servRecepcion " + usuario.getUserid() + " ERROR al cargar las configuraciones Servicio RECEPTOR " + e);
        }
    }

    private void servValidacion() {
        try {
            servicioValidador = new DaoServicio().getServicoByNombre(Servicio.VALIDADOR.name());
            DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
            teValidacion = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioValidador.getIdServicio(), ServValidador.TIEMPO_ESPERA.name());
            if (teValidacion == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_ESPERA para el servicio " + servicioValidador.getNombre());
                teValidacion = new ConfiguracionesServicios();
                teValidacion.setIdServicio(servicioValidador.getIdServicio());
                teValidacion.setPropiedad("TIEMPO_ESPERA");
                teValidacion.setValor("");
                teValidacion.setDescripcion("Tiempo de espera entre cada ciclo.");
                teValidacion.setTipo("NUMERO");
                updateCondfigServicio(teValidacion);
            }
            trcValidacion = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioValidador.getIdServicio(), ServValidador.TIEMPO_REC_CONFIG.name());
            if (trcValidacion == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_REC_CONFIG para el servicio " + servicioValidador.getNombre());
                trcValidacion = new ConfiguracionesServicios();
                trcValidacion.setIdServicio(servicioValidador.getIdServicio());
                trcValidacion.setPropiedad("TIEMPO_REC_CONFIG");
                trcValidacion.setValor("");
                trcValidacion.setDescripcion("Tiempo de espera para recargar configuración.");
                trcValidacion.setTipo("NUMERO");
                updateCondfigServicio(trcValidacion);
            }
            wsValidacion = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioValidador.getIdServicio(), ServValidador.CSD_WEBSERVICE.name());
            if (wsValidacion == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion CSD_WEBSERVICE para el servicio " + servicioValidador.getNombre());
                wsValidacion = new ConfiguracionesServicios();
                wsValidacion.setIdServicio(servicioValidador.getIdServicio());
                wsValidacion.setPropiedad("CSD_WEBSERVICE");
                wsValidacion.setValor("");
                wsValidacion.setDescripcion("Webservice para validar CSDs.");
                wsValidacion.setTipo("URL");
                updateCondfigServicio(wsValidacion);
            }
            logger.debug(usuario.getUserid() + ". Configuraciones de servicio Validador cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_SERV] servValidacion - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("servValidacion " + usuario.getUserid() + " ERROR al cargar las configuraciones Servicio VALIDADOR " + e);
        }
    }

    private void servEnvio() {
        try {
            servicioEnviador = new DaoServicio().getServicoByNombre(Servicio.ENVIADOR.name());
            DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
            teEnvio = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioEnviador.getIdServicio(), ServEnviador.TIEMPO_ESPERA.name());
            if (teEnvio == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_ESPERA para el servicio " + servicioEnviador.getNombre());
                teEnvio = new ConfiguracionesServicios();
                teEnvio.setIdServicio(servicioEnviador.getIdServicio());
                teEnvio.setPropiedad("TIEMPO_ESPERA");
                teEnvio.setValor("");
                teEnvio.setDescripcion("Tiempo de espera entre cada ciclo.");
                teEnvio.setTipo("NUMERO");
                updateCondfigServicio(teEnvio);
            }
            trcEnvio = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioEnviador.getIdServicio(), ServEnviador.TIEMPO_REC_CONFIG.name());
            if (trcEnvio == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_REC_CONFIG para el servicio " + servicioEnviador.getNombre());
                trcEnvio = new ConfiguracionesServicios();
                trcEnvio.setIdServicio(servicioEnviador.getIdServicio());
                trcEnvio.setPropiedad("TIEMPO_REC_CONFIG");
                trcEnvio.setValor("");
                trcEnvio.setDescripcion("Tiempo de espera para recargar configuración.");
                trcEnvio.setTipo("NUMERO");
                updateCondfigServicio(trcEnvio);
            }
            mdEnvio = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioEnviador.getIdServicio(), ServEnviador.MIN_DOCTOS.name());
            if (mdEnvio == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion MIN_DOCTOS para el servicio " + servicioEnviador.getNombre());
                mdEnvio = new ConfiguracionesServicios();
                mdEnvio.setIdServicio(servicioEnviador.getIdServicio());
                mdEnvio.setPropiedad("MIN_DOCTOS");
                mdEnvio.setValor("");
                mdEnvio.setDescripcion("Mínimo de documentos encolados para volver a encolar más.");
                mdEnvio.setTipo("NUMERO");
                updateCondfigServicio(mdEnvio);
            }
            logger.debug(usuario.getUserid() + ". Configuraciones de servicio Enviador cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_SERV] servEnvio - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("servEnvio " + usuario.getUserid() + " ERROR al cargar las configuraciones Servicio ENVIADOR " + e);
        }
    }

    private void servAcceso() {
        try {
            servicioAcceso = new DaoServicio().getServicoByNombre(Servicio.ADMINISTRACION_ACCESO.name());
            DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
            expPass = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioAcceso.getIdServicio(), ServAcceso.DIAS_EXPIRAR_CONT.name());
            if (expPass == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion DIAS_EXPIRAR_CONT para el servicio " + servicioAcceso.getNombre());
                expPass = new ConfiguracionesServicios();
                expPass.setIdServicio(servicioAcceso.getIdServicio());
                expPass.setPropiedad("DIAS_EXPIRAR_CONT");
                expPass.setValor("");
                expPass.setDescripcion("Días para expirar contraseña.");
                expPass.setTipo("NUMERO");
                updateCondfigServicio(expPass);
            }
            inactividad = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioAcceso.getIdServicio(), ServAcceso.DIAS_INACTIVIDAD.name());
            if (inactividad == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion DIAS_INACTIVIDAD para el servicio " + servicioAcceso.getNombre());
                inactividad = new ConfiguracionesServicios();
                inactividad.setIdServicio(servicioAcceso.getIdServicio());
                inactividad.setPropiedad("DIAS_INACTIVIDAD");
                inactividad.setValor("");
                inactividad.setDescripcion("Días de inactividad antes de deshabilitar cuenta.");
                inactividad.setTipo("NUMERO");
                updateCondfigServicio(inactividad);
            }
            minPass = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioAcceso.getIdServicio(), ServAcceso.LONGITUD_MIN_CONT.name());
            if (minPass == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion LONGITUD_MIN_CONT para el servicio " + servicioAcceso.getNombre());
                minPass = new ConfiguracionesServicios();
                minPass.setIdServicio(servicioAcceso.getIdServicio());
                minPass.setPropiedad("LONGITUD_MIN_CONT");
                minPass.setValor("");
                minPass.setDescripcion("Longitud mínima de contraseñas.");
                minPass.setTipo("NUMERO");
                updateCondfigServicio(minPass);
            }
            minIntentos = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servicioAcceso.getIdServicio(), ServAcceso.NUM_INT_FALLIDOS.name());
            if (minIntentos == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion NUM_INT_FALLIDOS para el servicio " + servicioAcceso.getNombre());
                minIntentos = new ConfiguracionesServicios();
                minIntentos.setIdServicio(servicioAcceso.getIdServicio());
                minIntentos.setPropiedad("NUM_INT_FALLIDOS");
                minIntentos.setValor("");
                minIntentos.setDescripcion("Número de intentos fallidos antes de bloquear cuenta.");
                minIntentos.setTipo("NUMERO");
                updateCondfigServicio(minIntentos);
            }
            logger.debug(usuario.getUserid() + ". Configuraciones de servicio Acceso cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_SERV] servAcceso - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("servAcceso " + usuario.getUserid() + " ERROR al cargar las configuraciones Servicio ADMINISTRACION_ACCESO " + e);
        }
    }

    private void servContabilidad() {
        try {
            servGenerador = new DaoServicio().getServicoByNombre(Servicio.GENERADOR.name());
            DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
            teContabilidad = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servGenerador.getIdServicio(), ServGenerador.TIEMPO_ESPERA.name());
            if (teContabilidad == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_ESPERA para el servicio " + servGenerador.getNombre());
                teContabilidad = new ConfiguracionesServicios();
                teContabilidad.setIdServicio(servGenerador.getIdServicio());
                teContabilidad.setPropiedad("TIEMPO_ESPERA");
                teContabilidad.setValor("");
                teContabilidad.setDescripcion("Tiempo de espera entre cada ciclo.");
                teContabilidad.setTipo("NUMERO");
                updateCondfigServicio(teContabilidad);
            }
            trcContabilidad = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servGenerador.getIdServicio(), ServGenerador.TIEMPO_REC_CONFIG.name());
            if (trcContabilidad == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_REC_CONFIG para el servicio " + servGenerador.getNombre());
                trcContabilidad = new ConfiguracionesServicios();
                trcContabilidad.setIdServicio(servGenerador.getIdServicio());
                trcContabilidad.setPropiedad("TIEMPO_REC_CONFIG");
                trcContabilidad.setValor("");
                trcContabilidad.setDescripcion("Tiempo de espera para recargar configuración.");
                trcContabilidad.setTipo("NUMERO");
                updateCondfigServicio(trcContabilidad);
            }
            logger.debug(usuario.getUserid() + ". Configuraciones de servicio Contabilidad electronica cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_SERV] servContabilidad - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("servContabilidad - ERROR al cargar las configuraciones de servicio GENERADOR ERROR " + e);
        }
    }

    private void servActualizar() {
        try {
            servActualizar = new DaoServicio().getServicoByNombre(Servicio.ACTUALIZACION.name());
            DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
            teActualizar = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servActualizar.getIdServicio(), ServActualizacion.TIEMPO_ESPERA.name());
            if (teActualizar == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_ESPERA para el servicio " + servActualizar.getNombre());
                teActualizar = new ConfiguracionesServicios();
                teActualizar.setIdServicio(servActualizar.getIdServicio());
                teActualizar.setPropiedad("TIEMPO_ESPERA");
                teActualizar.setValor("");
                teActualizar.setDescripcion("Tiempo de espera entre cada ciclo.");
                teActualizar.setTipo("NUMERO");
                updateCondfigServicio(teActualizar);
            }
            trcActualizar = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servActualizar.getIdServicio(), ServActualizacion.TIEMPO_REC_CONFIG.name());
            if (trcActualizar == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion TIEMPO_REC_CONFIG para el servicio " + servActualizar.getNombre());
                trcActualizar = new ConfiguracionesServicios();
                trcActualizar.setIdServicio(servActualizar.getIdServicio());
                trcActualizar.setPropiedad("TIEMPO_REC_CONFIG");
                trcActualizar.setValor("");
                trcActualizar.setDescripcion("Tiempo de espera para recargar configuración.");
                trcActualizar.setTipo("NUMERO");
                updateCondfigServicio(trcActualizar);
            }
            logger.debug(usuario.getUserid() + ". Configuraciones de servicio Actualizacion cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_SERV] servActualizar - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("servActuzlizar - ERROR al cargar las configuraciones de servicio ACTUALIZACION ERROR " + e);
        }
    }

    private void servConfigXSA() {
        try {
            servXSA = new DaoServicio().getServicoByNombre(Servicio.CONFIG_XSA.name());
            DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
            wsActuzliza = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servXSA.getIdServicio(), ServConfigXSA.WS_ACTUALIZA_CFDS.name());
            if (wsActuzliza == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion WS_ACTUALIZA_CFDS para el servicio " + servXSA.getNombre());
                wsActuzliza = new ConfiguracionesServicios();
                wsActuzliza.setIdServicio(servXSA.getIdServicio());
                wsActuzliza.setPropiedad("WS_ACTUALIZA_CFDS");
                wsActuzliza.setValor("");
                wsActuzliza.setDescripcion("WebService para actualizar las tablas de Cfds");
                wsActuzliza.setTipo("URL");
                updateCondfigServicio(wsActuzliza);
            }
            wsValida = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servXSA.getIdServicio(), ServConfigXSA.WS_VALIDA_CFDS.name());
            if (wsValida == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion WS_VALIDA_CFDS para el servicio " + servXSA.getNombre());
                wsValida = new ConfiguracionesServicios();
                wsValida.setIdServicio(servXSA.getIdServicio());
                wsValida.setPropiedad("WS_VALIDA_CFDS");
                wsValida.setValor("");
                wsValida.setDescripcion("WebService para validar los Cfds");
                wsValida.setTipo("URL");
                updateCondfigServicio(wsValida);
            }
            keyServ = daoConfigServicio.getConfigServicioByIdServicioPropiedad(servXSA.getIdServicio(), ServConfigXSA.KEY_SERVIDOR.name());
            if (keyServ == null) {
                logger.info(usuario.getUserid() + ". No existe configuracion KEY_SERVIDOR para el servicio " + servXSA.getNombre());
                keyServ = new ConfiguracionesServicios();
                keyServ.setIdServicio(servXSA.getIdServicio());
                keyServ.setPropiedad("KEY_SERVIDOR");
                keyServ.setValor("");
                keyServ.setDescripcion("Llave del servidor XSA");
                keyServ.setTipo("TEXTO");
                updateCondfigServicio(keyServ);
            }
            logger.debug(usuario.getUserid() + ". Configuraciones de servicio configuraciones XSA cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_SERV] servConfigXSA - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("servConfigXSA - ERROR al cargar las configuraciones de servicio CONFIG_XSA ERROR " + e);
        }
    }

    private boolean updateCondfigServicio(ConfiguracionesServicios configServicio) {
        boolean respuesta = false;
        try {
            DaoConfiguracionServicios daoConfigServicio = new DaoConfiguracionServicios();
            if (daoConfigServicio.updateConfigServicios(configServicio)) {
                descBitacora = "[SERVICIOS] updateCondfigServicio - Modifico Configuraciones de Servicio id " + configServicio.getIdServicio() + " Configuracion " + configServicio.getPropiedad();
                registrarBitacora(usuario.getIdUsuario(), configServicio.getIdServicio(), null, descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Modifico Configuraciones de Servicio id " + configServicio.getIdServicio() + " Configuracion " + configServicio.getPropiedad());
                respuesta = true;
            } else {
                descBitacora = "[SERVICIOS] updateCondfigServicio - Error al modificar Configuraciones de Servicio id " + configServicio.getIdServicio() + " Conifuracion " + configServicio.getPropiedad();
                registrarBitacora(usuario.getIdUsuario(), configServicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al modificar Configuraciones de Servicio id " + configServicio.getIdServicio() + " Conifuracion " + configServicio.getPropiedad());
            }
        } catch (Exception e) {
            descBitacora = "[SERVICIOS] updateCondfigServicio - ERROR:" + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), configServicio.getIdServicio(), null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". updateCondfigServicio - ERROR: " + e);
        }
        return respuesta;
    }

    public ConfiguracionesServicios getTeContabilidad() {
        return teContabilidad;
    }

    public void setTeContabilidad(ConfiguracionesServicios teContabilidad) {
        this.teContabilidad = teContabilidad;
    }

    public ConfiguracionesServicios getTrcContabilidad() {
        return trcContabilidad;
    }

    public void setTrcContabilidad(ConfiguracionesServicios trcContabilidad) {
        this.trcContabilidad = trcContabilidad;
    }

    public ConfiguracionesServicios getHostSMTP() {
        return hostSMTP;
    }

    public void setHostSMTP(ConfiguracionesServicios hostSMTP) {
        this.hostSMTP = hostSMTP;
    }

    public ConfiguracionesServicios getPortSMTP() {
        return portSMTP;
    }

    public void setPortSMTP(ConfiguracionesServicios portSMTP) {
        this.portSMTP = portSMTP;
    }

    public ConfiguracionesServicios getEmailSMTP() {
        return emailSMTP;
    }

    public void setEmailSMTP(ConfiguracionesServicios emailSMTP) {
        this.emailSMTP = emailSMTP;
    }

    public ConfiguracionesServicios getUserSMTP() {
        return userSMTP;
    }

    public void setUserSMTP(ConfiguracionesServicios userSMTP) {
        this.userSMTP = userSMTP;
    }

    public ConfiguracionesServicios getPassSMTP() {
        return passSMTP;
    }

    public void setPassSMTP(ConfiguracionesServicios passSMTP) {
        this.passSMTP = passSMTP;
    }

    public ConfiguracionesServicios getSslSMTP() {
        return sslSMTP;
    }

    public void setSslSMTP(ConfiguracionesServicios sslSMTP) {
        this.sslSMTP = sslSMTP;
    }

    public ConfiguracionesServicios getNombreSMTP() {
        return nombreSMTP;
    }

    public void setNombreSMTP(ConfiguracionesServicios nombreSMTP) {
        this.nombreSMTP = nombreSMTP;
    }

    public String getCodigoEmail() {
        return codigoEmail;
    }

    public void setCodigoEmail(String codigoEmail) {
        this.codigoEmail = codigoEmail;
    }

    public String getHashSMTP() {
        return hashSMTP;
    }

    public void setHashSMTP(String hashSMTP) {
        this.hashSMTP = hashSMTP;
    }

//    public boolean isBtnFavicon() {
//        btnFavicon = false;
//        String sFichero = "/usr/local/tomcat/webapps/Validacion/resources/images/favicon.ico";
//        File fichero = new File(sFichero);
//        if (fichero.exists()) {
//            btnFavicon = true;
//            logger.info("El fichero " + sFichero + " existe");
//        } else {
//            logger.info("el fichero no existe");
//        }
//        return btnFavicon;
//    }
//
//    public void setBtnFavicon(boolean btnFavicon) {
//        this.btnFavicon = btnFavicon;
//    }
    public ConfiguracionesServicios getTeActualizar() {
        return teActualizar;
    }

    public void setTeActualizar(ConfiguracionesServicios teActualizar) {
        this.teActualizar = teActualizar;
    }

    public ConfiguracionesServicios getTrcActualizar() {
        return trcActualizar;
    }

    public void setTrcActualizar(ConfiguracionesServicios trcActualizar) {
        this.trcActualizar = trcActualizar;
    }

    public ConfiguracionesServicios getWsActuzliza() {
        return wsActuzliza;
    }

    public void setWsActuzliza(ConfiguracionesServicios wsActuzliza) {
        this.wsActuzliza = wsActuzliza;
    }

    public ConfiguracionesServicios getWsValida() {
        return wsValida;
    }

    public void setWsValida(ConfiguracionesServicios wsValida) {
        this.wsValida = wsValida;
    }

    public ConfiguracionesServicios getKeyServ() {
        return keyServ;
    }

    public void setKeyServ(ConfiguracionesServicios keyServ) {
        this.keyServ = keyServ;
    }

    public ConfiguracionesServicios getTeRecepcion() {
        return teRecepcion;
    }

    public void setTeRecepcion(ConfiguracionesServicios teRecepcion) {
        this.teRecepcion = teRecepcion;
    }

    public ConfiguracionesServicios getTrcRecepcion() {
        return trcRecepcion;
    }

    public void setTrcRecepcion(ConfiguracionesServicios trcRecepcion) {
        this.trcRecepcion = trcRecepcion;
    }

    public ConfiguracionesServicios getTeValidacion() {
        return teValidacion;
    }

    public void setTeValidacion(ConfiguracionesServicios teValidacion) {
        this.teValidacion = teValidacion;
    }

    public ConfiguracionesServicios getTrcValidacion() {
        return trcValidacion;
    }

    public void setTrcValidacion(ConfiguracionesServicios trcValidacion) {
        this.trcValidacion = trcValidacion;
    }

    public ConfiguracionesServicios getWsValidacion() {
        return wsValidacion;
    }

    public void setWsValidacion(ConfiguracionesServicios wsValidacion) {
        this.wsValidacion = wsValidacion;
    }

    public ConfiguracionesServicios getTeEnvio() {
        return teEnvio;
    }

    public void setTeEnvio(ConfiguracionesServicios teEnvio) {
        this.teEnvio = teEnvio;
    }

    public ConfiguracionesServicios getTrcEnvio() {
        return trcEnvio;
    }

    public void setTrcEnvio(ConfiguracionesServicios trcEnvio) {
        this.trcEnvio = trcEnvio;
    }

    public ConfiguracionesServicios getMdEnvio() {
        return mdEnvio;
    }

    public void setMdEnvio(ConfiguracionesServicios mdEnvio) {
        this.mdEnvio = mdEnvio;
    }

    public ConfiguracionesServicios getExpPass() {
        return expPass;
    }

    public void setExpPass(ConfiguracionesServicios expPass) {
        this.expPass = expPass;
    }

    public ConfiguracionesServicios getInactividad() {
        return inactividad;
    }

    public void setInactividad(ConfiguracionesServicios inactividad) {
        this.inactividad = inactividad;
    }

    public ConfiguracionesServicios getMinPass() {
        return minPass;
    }

    public void setMinPass(ConfiguracionesServicios minPass) {
        this.minPass = minPass;
    }

    public ConfiguracionesServicios getMinIntentos() {
        return minIntentos;
    }

    public void setMinIntentos(ConfiguracionesServicios minIntentos) {
        this.minIntentos = minIntentos;
    }

    public String getIpXsa() {
        return ipXsa;
    }

    public void setIpXsa(String ipXsa) {
        this.ipXsa = ipXsa;
    }

    public boolean isConnectSMTP() {
        return connectSMTP;
    }

    public void setConnectSMTP(boolean connectSMTP) {
        this.connectSMTP = connectSMTP;
    }

    public boolean isConnectWS() {
        return connectWS;
    }

    public void setConnectWS(boolean connectWS) {
        this.connectWS = connectWS;
    }

    public String getPortXsa() {
        return portXsa;
    }

    public void setPortXsa(String portXsa) {
        this.portXsa = portXsa;
    }

}
