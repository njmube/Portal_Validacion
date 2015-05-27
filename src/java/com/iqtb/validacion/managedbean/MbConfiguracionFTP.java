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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbConfiguracionFTP implements Serializable {

    private Usuarios usuario;
    private Empresas empresa;
//    private ConfiguracionesEmpresas catHost;
//    private ConfiguracionesEmpresas catUser;
//    private ConfiguracionesEmpresas catPass;
    private String host;
    private String user;
    private String pass;
    private String confirmPass;
    private String pw;
//    private ConfiguracionesEmpresas balHost;
//    private ConfiguracionesEmpresas balUser;
//    private ConfiguracionesEmpresas balPass;
//    private ConfiguracionesEmpresas polHost;
//    private ConfiguracionesEmpresas polUser;
//    private ConfiguracionesEmpresas polPass;
    private ConfiguracionesEmpresas valHost;
    private ConfiguracionesEmpresas valUser;
    private ConfiguracionesEmpresas valPass;
    DaoConfiguracionEmpresa daoConfigEmpresa;
    private FacesMessage msg;
    private String descBitacora;
    private final String sessionUsuario;
    private static final Logger logger = Logger.getLogger(MbConfiguracionFTP.class);

    public MbConfiguracionFTP() {
        usuario = new Usuarios();
        empresa = new Empresas();
        daoConfigEmpresa = new DaoConfiguracionEmpresa();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();

        try {
            usuario = new DaoUsuario().getByUserid(sessionUsuario);
            empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
            configValFTP();
        } catch (Exception e) {
            descBitacora = "Error MbConfiguracionFTP ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
    }

    public void createUserVal() {
        host = "localhost";
        try {
            if (user != null && !user.trim().isEmpty()) {
                if (daoConfigEmpresa.existConfiguracionEmpresa(ConfigEmpresa.VAL_USER_FTP.name(), user) == null) {
                    if (pass != null && !pass.trim().isEmpty()) {
                        if (pass.equals(confirmPass)) {
                            logger.info(usuario.getUserid() + ". host: " + host);
                            logger.info(usuario.getUserid() + ". user: " + user);
                            logger.info(usuario.getUserid() + ". pass: " + pass);
                            Process cmd = Runtime.getRuntime().exec("/work/iqtb/ftp-useradd.sh " + user + " " + pass + " " + empresa.getRfc());
                            cmd.waitFor();
                            BufferedReader buf = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
                            String linea = "";
                            while ((linea = buf.readLine()) != null) {
                                logger.info(linea.toString());
                            }
                            pw = encrypt(pass);
                            valHost.setValor(host);
                            valUser.setValor(user);
                            valPass.setValor(pw);
                            daoConfigEmpresa.updateConfiguracionEmpresa(valHost);
                            daoConfigEmpresa.updateConfiguracionEmpresa(valUser);
                            daoConfigEmpresa.updateConfiguracionEmpresa(valPass);
                            logger.info(usuario.getUserid() + ". Usuario " + user + " FTP validacion agregado correctamente");
                            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Usuario FTP " + user + " agregado correctamente");
                            descBitacora = usuario.getUserid() + ". Usuario " + user + " FTP validacion agregado correctamente";
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
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
                        } else {
                            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden.");
                        }
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Contraseña.");
                    }
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario " + user + " ya se encuentra registrado. Por favor, introduzca otro usuario.");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Usuario.");
            }
        } catch (Exception e) {
            logger.error(usuario.getUserid() + ". createUserVal - ERROR " + e.getMessage());
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al crear el usaurio FTP. Por favor, pongase en contacto con el administrador.");
            descBitacora = usuario.getUserid() + ". createUserVal - ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
        }
        user = "";
        pass = "";
        confirmPass = "";
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void deleteUserVal() {
        try {
            Process cmd = Runtime.getRuntime().exec("/work/iqtb/ftp-userdel.sh " + valUser.getValor() + " " + empresa.getRfc());
            cmd.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
            String linea = "";
            while ((linea = buf.readLine()) != null) {
                logger.info(linea.toString());
            }
            valHost.setValor("");
            valUser.setValor("");
            valPass.setValor("");
            DaoConfiguracionEmpresa daoConfigEmpresa = new DaoConfiguracionEmpresa();
            daoConfigEmpresa.updateConfiguracionEmpresa(valHost);
            daoConfigEmpresa.updateConfiguracionEmpresa(valUser);
            daoConfigEmpresa.updateConfiguracionEmpresa(valPass);
            logger.info(usuario.getUserid() + ". Usuario FTP ha sido eliminado correctamente.");
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Usuario FTP validacion ha sido eliminado correctamente.");
            descBitacora = usuario.getUserid() + ". Usuario FTP ha sido eliminado correctamente.";
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
        } catch (IOException e) {
            logger.error(usuario.getUserid() + ". deleteUserVal - IOException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, intende de nuevo.");
            descBitacora = usuario.getUserid() + ". deleteUserVal - IOException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
        } catch (InterruptedException e) {
            logger.error(usuario.getUserid() + ". deleteUserVal - InterruptedException ERROR: " + e);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, intente de nuevo.");
            descBitacora = usuario.getUserid() + ". deleteUserVal - InterruptedException ERROR: " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
        } catch (Exception ex) {
            logger.error(usuario.getUserid() + ". deleteUserVal - ERROR: " + ex);
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar el usaurio FTP. Por favor, pongase en contacto con el administrador.");
            descBitacora = usuario.getUserid() + ". deleteUserVal - ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String reloadUsuariosFtp() {
        host = "";
        user = "";
        pass = "";
        pw = "";
        return "/ContabilidadElectronica/usuariosftp?faces-redirect=true";
    }

//    private void configCatFTP() {
//        try {
//            DaoConfiguracionEmpresa configEmpresa = new DaoConfiguracionEmpresa();
//            catHost = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.CAT_HOST_FTP.name());
//            catUser = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.CAT_USER_FTP.name());
//            catPass = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.CAT_PASS_FTP.name());
//        } catch (Exception e) {
//            descBitacora = usuario.getUserid() + ". Error al obtener configuraciones FTP para Catalogo. ERROR " + e.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(usuario.getUserid() + ". Error al obtener configuraciones FTP para Catalogo. ERROR " + e);
//        }
//    }
//
//    private void configBalFTP() {
//        try {
//            DaoConfiguracionEmpresa configEmpresa = new DaoConfiguracionEmpresa();
//            balHost = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.BAL_HOST_FTP.name());
//            balUser = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.BAL_USER_FTP.name());
//            balPass = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.BAL_PASS_FTP.name());
//        } catch (Exception e) {
//            descBitacora = usuario.getUserid() + ". Error al obtener configuraciones FTP para balanza. ERROR " + e.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(usuario.getUserid() + ". Error al obtener configuraciones FTP para balanza. ERROR " + e);
//        }
//    }
//
//    private void configPolFTP() {
//        try {
//            DaoConfiguracionEmpresa configEmpresa = new DaoConfiguracionEmpresa();
//            polHost = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.POL_HOST_FTP.name());
//            polUser = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.POL_USER_FTP.name());
//            polPass = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.POL_PASS_FTP.name());
//        } catch (Exception e) {
//            descBitacora = usuario.getUserid() + ". Error al obtener configuraciones FTP para Polizas. ERROR " + e.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(usuario.getUserid() + ". Error al obtener configuraciones FTP para Polizas. ERROR " + e);
//        }
//    }
    private void configValFTP() {
        try {
            DaoConfiguracionEmpresa configEmpresa = new DaoConfiguracionEmpresa();
            valHost = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.VAL_HOST_FTP.name());
            valUser = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.VAL_USER_FTP.name());
            valPass = configEmpresa.getConfiguracionEmpresa(empresa.getIdEmpresa(), ConfigEmpresa.VAL_PASS_FTP.name());
        } catch (Exception e) {
            descBitacora = usuario.getUserid() + ". Error al obtener configuraciones FTP para Validación. ERROR " + e.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". Error al obtener configuraciones FTP para Validación. ERROR " + e);
        }
    }

    public ConfiguracionesEmpresas getValHost() {
        return valHost;
    }

    public void setValHost(ConfiguracionesEmpresas valHost) {
        this.valHost = valHost;
    }

    public ConfiguracionesEmpresas getValUser() {
        return valUser;
    }

    public void setValUser(ConfiguracionesEmpresas valUser) {
        this.valUser = valUser;
    }

    public ConfiguracionesEmpresas getValPass() {
        return valPass;
    }

    public void setValPass(ConfiguracionesEmpresas valPass) {
        this.valPass = valPass;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

}
