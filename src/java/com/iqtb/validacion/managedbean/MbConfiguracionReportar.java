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
import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
public class MbConfiguracionReportar implements Serializable {

    private ConfiguracionesEmpresas reportarA;
    private boolean opErp;
    private ConfiguracionesEmpresas ipRetorno;
    private ConfiguracionesEmpresas portRetorno;
    private ConfiguracionesEmpresas sidNameRetorno;
    private ConfiguracionesEmpresas userRetorno;
    private ConfiguracionesEmpresas passRetorno;
    private String pwRetorno;
    private ConfiguracionesEmpresas teRetorno;
    private ConfiguracionesEmpresas trcRetorno;
    private boolean opEnviarFtp;
    private ConfiguracionesEmpresas hostFtp;
    private ConfiguracionesEmpresas userFtp;
    private ConfiguracionesEmpresas passFtp;
    private ConfiguracionesEmpresas portFtp;
    private ConfiguracionesEmpresas dirFtp;
    private boolean defaultPort;
    private String pwFtp;
    private DaoConfiguracionEmpresa daoConfigEmpresa;
    private Empresas empresa;
    private Usuarios usuario;
    private DaoEmpresa daoEmpresa;
    private DaoUsuario daoUsuario;
    private String empresaSeleccionada;
    private String sessionUsuario;
    private String descBitacora;
    private static final Logger logger = Logger.getLogger(MbConfiguracionReportar.class);

    public MbConfiguracionReportar() {
        reportarA = new ConfiguracionesEmpresas();
        ipRetorno = new ConfiguracionesEmpresas();
        portRetorno = new ConfiguracionesEmpresas();
        sidNameRetorno = new ConfiguracionesEmpresas();
        userRetorno = new ConfiguracionesEmpresas();
        passRetorno = new ConfiguracionesEmpresas();
        teRetorno = new ConfiguracionesEmpresas();
        trcRetorno = new ConfiguracionesEmpresas();
        hostFtp = new ConfiguracionesEmpresas();
        userFtp = new ConfiguracionesEmpresas();
        passFtp = new ConfiguracionesEmpresas();
        portFtp = new ConfiguracionesEmpresas();
        dirFtp = new ConfiguracionesEmpresas();
        usuario = new Usuarios();
        empresa = new Empresas();
        daoConfigEmpresa = new DaoConfiguracionEmpresa();
        daoEmpresa = new DaoEmpresa();
        daoUsuario = new DaoUsuario();
        defaultPort = true;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = daoUsuario.getByUserid(sessionUsuario);
            empresa = daoEmpresa.getEmpresaByRFC(empresaSeleccionada);
            configEmpresaReportar();
        } catch (Exception e) {
            logger.error(usuario.getUserid() + " MbConfiguracionReportar ERROR " + e.getMessage());
        }
    }

    public void onReportarChange() {
        if (reportarA != null) {
            if (reportarA.getValor().trim().isEmpty()) {
                opErp = false;
                opEnviarFtp = false;
            }
            if (reportarA.getValor().equals("ERP")) {
                opErp = true;
                opEnviarFtp = false;
            }
            if (reportarA.getValor().equals("ENVIAR_FTP")) {
                opEnviarFtp = true;
                opErp = false;
            }
            if (reportarA.getValor().equals("DISPONIBLE_FTP")) {
                //aqui no se que se hace
                opErp = false;
                opEnviarFtp = false;
            }
        }
    }

    public void updateConfigReportar() {
        if (reportarA.getValor().equals("") || reportarA.getValor().equals("DISPONIBLE_FTP")) {
            logger.info(usuario.getUserid() + " No Reportar|DISPONIBLE_FTP");
            if (updateConfigEmpresa(reportarA)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuración ha sido guardada."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al guardar la configuración."));
            }
        }
        if (reportarA.getValor().equals("ERP")) {
            if (validarReportarERP()) {
                logger.info(usuario.getUserid() + " ERP Configuraciones correctas");
                if (updateConfigEmpresa(reportarA)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuración ha sido guardada."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al guardar la configuración."));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configurar ERP", "Por favor, configure el Retorno ERP."));
                logger.error(usuario.getUserid() + " faltan configuraciones de ERP");
            }
        }
        if (reportarA.getValor().equals("ENVIAR_FTP")) {
            if (validarEnvioFtp()) {
                logger.info(usuario.getUserid() + " ENVIAR_FTP Configuraciones correctas");
                if (updateConfigEmpresa(reportarA)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuración ha sido guardada."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al guardar la configuración."));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Configurar Enviar FTP", "Por favor, configure el Envío por FTP."));
                logger.error(usuario.getUserid() + " faltan configuraciones de envio por ftp");
            }
        }
    }

    public boolean validarReportarERP() {
        logger.info("validarReportarERP valor " + ipRetorno.getValor() != null && !ipRetorno.getValor().trim().isEmpty() && portRetorno.getValor() != null && !portRetorno.getValor().trim().isEmpty() && sidNameRetorno.getValor() != null && !sidNameRetorno.getValor().trim().isEmpty() && userRetorno.getValor() != null && !userRetorno.getValor().trim().isEmpty() && teRetorno.getValor() != null && !teRetorno.getValor().trim().isEmpty() && trcRetorno.getValor() != null && !trcRetorno.getValor().trim().isEmpty());
        if (ipRetorno.getValor() != null && !ipRetorno.getValor().trim().isEmpty() && portRetorno.getValor() != null && !portRetorno.getValor().trim().isEmpty() && sidNameRetorno.getValor() != null && !sidNameRetorno.getValor().trim().isEmpty() && userRetorno.getValor() != null && !userRetorno.getValor().trim().isEmpty() && teRetorno.getValor() != null && !teRetorno.getValor().trim().isEmpty() && trcRetorno.getValor() != null && !trcRetorno.getValor().trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validarEnvioFtp() {
        logger.info("validarEnvioFtp valor " + hostFtp.getValor() != null && !hostFtp.getValor().trim().isEmpty() && userFtp.getValor() != null && !userFtp.getValor().trim().isEmpty());
        if (hostFtp.getValor() != null && !hostFtp.getValor().trim().isEmpty() && userFtp.getValor() != null && !userFtp.getValor().trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void updateConfRetorno() {
        boolean retorno = false;
        try {
            if (ipRetorno.getValor() != null && !ipRetorno.getValor().trim().isEmpty()) {
                if (portRetorno.getValor() != null && !portRetorno.getValor().trim().isEmpty()) {
                    if (sidNameRetorno.getValor() != null && !sidNameRetorno.getValor().trim().isEmpty()) {
                        if (userRetorno.getValor() != null && !userRetorno.getValor().trim().isEmpty()) {
                            if (teRetorno.getValor() != null && !teRetorno.getValor().trim().isEmpty()) {
                                if (trcRetorno.getValor() != null && !trcRetorno.getValor().trim().isEmpty()) {
                                    updateConfigEmpresa(ipRetorno);
                                    updateConfigEmpresa(portRetorno);
                                    updateConfigEmpresa(sidNameRetorno);
                                    updateConfigEmpresa(userRetorno);
                                    if (pwRetorno != null) {
                                        passRetorno.setValor(encrypt(pwRetorno));
                                        updateConfigEmpresa(passRetorno);
                                    }
                                    updateConfigEmpresa(teRetorno);
                                    retorno = updateConfigEmpresa(trcRetorno);
                                    if (retorno) {
                                        descBitacora = "[CONFIG_REPORTAR] " + usuario.getUserid() + " Las configuraciones para el Retorno ERP han sido modificadas.";
                                        registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.INFO.name());
                                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuraciones de Retorno ERP modificadas."));
                                        logger.info("updateServRetorno - " + usuario.getUserid() + ". Las configuraciones para el Retorno ERP han sido modificadas.");
                                    } else {
                                        descBitacora = "[CONFIG_REPORTAR] " + usuario.getUserid() + " Error modificando las configuraciones para el Retorno ERP.";
                                        registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
                                        logger.error("updateServRetorno - " + usuario.getUserid() + " Error modificando las configuraciones para el Retorno ERP.");
                                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando las configuraciones para el Retorno ERP."));
                                    }
                                } else {
                                    logger.info("updateServRetorno - tiempo recargar configuracion es reuerido.");
                                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera para recargar configuración."));
                                }
                            } else {
                                logger.info("updateServRetorno - timpo de espera es reuerido.");
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Tiempo de espera entre cada ciclo."));
                            }
                        } else {
                            logger.info("updateServRetorno - Usuario Retorno es reuerido.");
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Usuario."));
                        }
                    } else {
                        logger.info("updateServRetorno - service name es un campo requerido.");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Service name."));
                    }
                } else {
                    logger.info("updateServRetorno - Puerto Retorno es un campo requerido.");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Puerto."));
                }
            } else {
                logger.info("updateServRetorno - IP Retorno es un campo requerido.");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para IP."));
            }
        } catch (CryptoException ex) {
            descBitacora = usuario.getUserid() + ". updateConfRetorno CryptoException Error " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando las configuraciones para el Retorno ERP."));
        } catch (IOException ex) {
            descBitacora = usuario.getUserid() + ". updateConfRetorno IOException Error " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando las configuraciones para el Retorno ERP."));
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("servicioRetorno", retorno);
    }

    public String reloadReportar() {
        return "/Configuracion/reportar?faces-redirect=true";
    }

    public void updateConfigEnvioFTP() {
        boolean envioFtp = false;
        try {
            if (hostFtp.getValor() != null && !hostFtp.getValor().trim().isEmpty()) {
                if (userFtp.getValor() != null && !userFtp.getValor().trim().isEmpty()) {
                    if (defaultPort || !portFtp.getValor().trim().isEmpty()) {
                        if (pwFtp != null) {
                            passFtp.setValor(encrypt(pwFtp));
                            updateConfigEmpresa(passFtp);
                        }
                        if (defaultPort) {
                            portFtp.setValor("21");
                        }
                        updateConfigEmpresa(portFtp);
                        updateConfigEmpresa(dirFtp);
                        updateConfigEmpresa(hostFtp);
                        envioFtp = updateConfigEmpresa(userFtp);
                        if (envioFtp) {
                            logger.info(usuario.getUserid() + " updateConfigEnvioFTP - Configuraciones Envio por FTP modificadas");
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Envio por FTP", "Configuraciones de Envío por FTP modificadas."));
                        } else {
                            logger.info(usuario.getUserid() + " updateConfigEnvioFTP - Error al modificar Configuraciones Envio por FTP");
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al modificar las configuraciones de Envio por FTP."));
                        }
                    } else {
                        logger.info("updateConfigEnvioFTP - si no se usa el pueto por default se debe insertar un valor");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Puerto."));
                    }
                } else {
                    logger.info("updateConfigEnvioFTP - usuario ftp es un campo requerido");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Usuario."));
                }
            } else {
                logger.info("updateConfigEnvioFTP - servidor ftp es un campo requerido");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, ingrese un valor para Servidor."));
            }
        } catch (IOException e) {
            descBitacora = usuario.getUserid() + ". updateConfigEnvioFTP IOException Error " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando las configuraciones para el Envio por FTP."));
        } catch (CryptoException e) {
            descBitacora = usuario.getUserid() + ". updateConfigEnvioFTP CryptoExceptionF Error " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error modificando las configuraciones para el Envio por FTP."));
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("envioFtp", envioFtp);
    }

    private boolean updateConfigEmpresa(ConfiguracionesEmpresas configEmpresa) {
        boolean respuesta = false;
        try {
            if (daoConfigEmpresa.updateConfiguracionEmpresa(configEmpresa)) {
                descBitacora = "[CONFIG_REPORTAR] updateConfigEmpresa - Modifico Configuraciones de la empresa " + empresa.getRfc() + " Configuracion " + configEmpresa.getPropiedad();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Modifico Configuraciones de la empresa " + empresa.getRfc() + " Configuracion " + configEmpresa.getPropiedad());
                respuesta = true;
            } else {
                descBitacora = "[CONFIG_REPORTAR] updateConfigEmpresa - Error al modificar Configuraciones de la empresa " + empresa.getRfc() + " Conifuracion " + configEmpresa.getPropiedad();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al modificar Configuraciones de la empresa " + empresa.getRfc() + " Conifuracion " + configEmpresa.getPropiedad());
            }
        } catch (Exception e) {
            descBitacora = "[CONFIG_REPORTAR] updateConfigEmpresa - ERROR:" + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        return respuesta;
    }

    private void configEmpresaReportar() {
        try {
            reportarA = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), "REPORTAR_A");
            logger.info(usuario.getUserid() + " configuracion " + reportarA.getPropiedad() + " cargada");
            configRetornoERP();
            configEnvioFTP();
            if (reportarA.getValor().equals("ERP")) {
                opErp = true;
            }
            if (reportarA.getValor().equals("ENVIAR_FTP")) {
                opEnviarFtp = true;
            }
        } catch (Exception e) {
            descBitacora = "[CONFIG_REPORTAR] configEmpresaReportar - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    private void configRetornoERP() {
        try {
            ipRetorno = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.IP_ORACLE.name());
            portRetorno = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PUERTO_ORACLE.name());
            sidNameRetorno = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.SID_NAME.name());
            userRetorno = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.USER.name());
            passRetorno = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PASS.name());
            teRetorno = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.TIEMPO_PAUSA.name());
            trcRetorno = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.TIEMPO_REC_CONFIG.name());
            logger.info(usuario.getUserid() + ". Empresa " + empresa.getRfc() + " Configuracion Retorno ERP cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_REPORTAR] " + usuario.getUserid() + " configRetornoERP - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    private void configEnvioFTP() {
        try {
            hostFtp = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.HOST_FTP.name());
            userFtp = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.USER_FTP.name());
            passFtp = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PASS_FTP.name());
            portFtp = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.PORT_FTP.name());
            dirFtp = daoConfigEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.DIR_FTP.name());
            logger.info(usuario.getUserid() + ". Empresa " + empresa.getRfc() + " Configuracion Envio por FTP cargadas");
        } catch (Exception e) {
            descBitacora = "[CONFIG_REPORTAR] " + usuario.getUserid() + "configEnvioFTP - ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public ConfiguracionesEmpresas getReportarA() {
        return reportarA;
    }

    public void setReportarA(ConfiguracionesEmpresas reportarA) {
        this.reportarA = reportarA;
    }

    public boolean isOpErp() {
        return opErp;
    }

    public void setOpErp(boolean opErp) {
        this.opErp = opErp;
    }

    public boolean isOpEnviarFtp() {
        return opEnviarFtp;
    }

    public void setOpEnviarFtp(boolean opEnviarFtp) {
        this.opEnviarFtp = opEnviarFtp;
    }

    public ConfiguracionesEmpresas getIpRetorno() {
        return ipRetorno;
    }

    public void setIpRetorno(ConfiguracionesEmpresas ipRetorno) {
        this.ipRetorno = ipRetorno;
    }

    public ConfiguracionesEmpresas getPortRetorno() {
        return portRetorno;
    }

    public void setPortRetorno(ConfiguracionesEmpresas portRetorno) {
        this.portRetorno = portRetorno;
    }

    public ConfiguracionesEmpresas getUserRetorno() {
        return userRetorno;
    }

    public void setUserRetorno(ConfiguracionesEmpresas userRetorno) {
        this.userRetorno = userRetorno;
    }

    public ConfiguracionesEmpresas getPassRetorno() {
        return passRetorno;
    }

    public void setPassRetorno(ConfiguracionesEmpresas passRetorno) {
        this.passRetorno = passRetorno;
    }

    public String getPwRetorno() {
        return pwRetorno;
    }

    public void setPwRetorno(String pwRetorno) {
        this.pwRetorno = pwRetorno;
    }

    public ConfiguracionesEmpresas getTeRetorno() {
        return teRetorno;
    }

    public void setTeRetorno(ConfiguracionesEmpresas teRetorno) {
        this.teRetorno = teRetorno;
    }

    public ConfiguracionesEmpresas getTrcRetorno() {
        return trcRetorno;
    }

    public void setTrcRetorno(ConfiguracionesEmpresas trcRetorno) {
        this.trcRetorno = trcRetorno;
    }

    public ConfiguracionesEmpresas getSidNameRetorno() {
        return sidNameRetorno;
    }

    public void setSidNameRetorno(ConfiguracionesEmpresas sidNameRetorno) {
        this.sidNameRetorno = sidNameRetorno;
    }

    public ConfiguracionesEmpresas getHostFtp() {
        return hostFtp;
    }

    public void setHostFtp(ConfiguracionesEmpresas hostFtp) {
        this.hostFtp = hostFtp;
    }

    public ConfiguracionesEmpresas getUserFtp() {
        return userFtp;
    }

    public void setUserFtp(ConfiguracionesEmpresas userFtp) {
        this.userFtp = userFtp;
    }

    public ConfiguracionesEmpresas getPassFtp() {
        return passFtp;
    }

    public void setPassFtp(ConfiguracionesEmpresas passFtp) {
        this.passFtp = passFtp;
    }

    public ConfiguracionesEmpresas getPortFtp() {
        return portFtp;
    }

    public void setPortFtp(ConfiguracionesEmpresas portFtp) {
        this.portFtp = portFtp;
    }

    public ConfiguracionesEmpresas getDirFtp() {
        return dirFtp;
    }

    public void setDirFtp(ConfiguracionesEmpresas dirFtp) {
        this.dirFtp = dirFtp;
    }

    public String getPwFtp() {
        return pwFtp;
    }

    public void setPwFtp(String pwFtp) {
        this.pwFtp = pwFtp;
    }

    public boolean isDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(boolean defaultPort) {
        this.defaultPort = defaultPort;
    }

}
