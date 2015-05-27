/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template catConector, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoConectorAdaptador;
import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.emun.Conector_Adaptador;
import com.iqtb.validacion.pojo.ConectorAdaptador;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbConectorAdaptador implements Serializable {

//    private ConectorAdaptador catalogo;
//    private ConectorAdaptador balanza;
//    private ConectorAdaptador poliza;
//    private ConectorAdaptador auxFolios;
//    private ConectorAdaptador auxCuentas;
    private ConectorAdaptador conectorAdaptador;
    private Usuarios usuario;
    private Empresas empresa;
    private DaoConectorAdaptador daoConectorAdaptador;
    private final String sessionUsuario;
    private FacesMessage msg;
    private String descBitacora;
    private static final Logger logger = Logger.getLogger(MbConectorAdaptador.class);

    public MbConectorAdaptador() {
        conectorAdaptador = new ConectorAdaptador();
        usuario = new Usuarios();
        daoConectorAdaptador = new DaoConectorAdaptador();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = new DaoUsuario().getByUserid(sessionUsuario);
            empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
        } catch (Exception e) {
            descBitacora = "Error al obtener el Usuario/Empresa ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error("Error al obtener el Usuario/Empresa ERROR: " + e);
        }
    }

    public void uploadBalanzaAdaptador(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String archivo = "";

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xsl\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xsl Adaptador Balanza");
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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.BALANZA.name());
            conectorAdaptador.setAdaptador(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Adaptador balanza guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Adaptador balanza guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar Adaptador balanza en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar Adaptador balanza en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadBalanzaAdaptador -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadBalanzaAdaptador -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadBalanzaAdaptador - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadBalanzaAdaptador - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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

    public void uploadBalanzaConector(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String archivo = "";

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xconv\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xconv conector Balanza");
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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.BALANZA.name());
            conectorAdaptador.setConector(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Conector balanza guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Conector balanza guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar Adaptador balanza en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar Adaptador balanza en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadBalanzaConector -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadBalanzaConector -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadBalanzaConector - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadBalanzaConector - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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

    public void uploadCatalogoAdaptador(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String archivo = "";

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xsl\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xsl Adaptador catalogo");
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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.CATALOGO.name());
            conectorAdaptador.setAdaptador(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Adaptador catalogo guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Adaptador catalogo guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar Adaptador catalogo en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar Adaptador catalogo en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCatalogoAdaptador -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCatalogoAdaptador -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCatalogoAdaptador - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCatalogoAdaptador - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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

    public void uploadCatalogoConector(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String archivo = "";

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xconv\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xconv conector catalogo");
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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.CATALOGO.name());
            conectorAdaptador.setConector(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Conector catalogo guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Conector catalogo guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar Adaptador catalogo en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar Adaptador catalogo en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCatalogoConector -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCatalogoConector -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCatalogoConector - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCatalogoConector - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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

    public void uploadPolizaAdaptador(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String archivo = "";

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xsl\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xsl Adaptador poliza");
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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.POLIZA.name());
            conectorAdaptador.setAdaptador(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Adaptador poliza guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Adaptador poliza guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar poliza catalogo en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar poliza catalogo en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadPolizaAdaptador -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadPolizaAdaptador -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadPolizaAdaptador - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadPolizaAdaptador - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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

    public void uploadPolizaConector(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String archivo = "";

        try {
            if (event.getFile().getSize() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", "Por favor, seleccione un archivo de imagen \".xconv\""));
                logger.info(usuario.getUserid() + ". Seleccione un archivo xconv conector poliza");
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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.POLIZA.name());
            conectorAdaptador.setConector(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Conector poliza guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Conector poliza guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar Adaptador poliza en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar Adaptador poliza en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadPolizaConector -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadPolizaConector -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadPolizaConector - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadPolizaConector - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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

    public void uploadFoliosAdaptador(FileUploadEvent event) {
        InputStream inputStream = null;
        String nombreArchivo = event.getFile().getFileName().toLowerCase();
        String archivo = "";

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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.AUXFOLIOS.name());
            conectorAdaptador.setAdaptador(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Adaptador auxiliar folios guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Adaptador auxiliar folios guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar auxiliar folios catalogo en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar auxiliar folios catalogo en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadFoliosAdaptador -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadFoliosAdaptador -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadFoliosAdaptador - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadFoliosAdaptador - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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
        String archivo = "";

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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.AUXFOLIOS.name());
            conectorAdaptador.setConector(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Conector auxiliar folios guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Conector auxiliar folios guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar Adaptador auxiliar folios en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar Adaptador auxiliar folios en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadFoliosConector -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadFoliosConector -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadFoliosConector - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadFoliosConector - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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
        String archivo = "";

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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.AUXCUENTAS.name());
            conectorAdaptador.setAdaptador(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Adaptador auxiliar cuentas guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Adaptador auxiliar cuentas guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar auxiliar cuentas catalogo en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar auxiliar cuentas catalogo en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCuentasAdaptador -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCuentasAdaptador -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCuentasAdaptador - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCuentasAdaptador - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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
        String archivo = "";

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
                archivo = archivo + new String(bytes, 0, read, "UTF8");
            }
            conectorAdaptador = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.AUXCUENTAS.name());
            conectorAdaptador.setConector(archivo);
            if (daoConectorAdaptador.updateConectorAdaptador(conectorAdaptador)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto:", event.getFile().getFileName() + " cargado correctamente."));
                descBitacora = usuario.getUserid() + ". Conector auxiliar cuentas guadado en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.INFO.name());
                logger.info(usuario.getUserid() + ". Conector auxiliar cuentas guadado en base de datos.");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", event.getFile().getFileName() + " ha ocurrido un error. Por favor intentelo de nuevo"));
                descBitacora = usuario.getUserid() + ". Error al guardar Adaptador auxiliar cuentas en base de datos.";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
                logger.error(usuario.getUserid() + ". Error al guardar Adaptador auxiliar cuentas en base de datos.");
            }
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCuentasConector -  IOException ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCuentasConector -  IOException ERROR: " + ex);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al subir el archivo."));
            descBitacora = usuario.getUserid() + ". uploadCuentasConector - Exception ERROR: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
            logger.error(usuario.getUserid() + ". uploadCuentasConector - Exception ERROR: " + ex);
        } finally {
            conectorAdaptador = new ConectorAdaptador();
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

//    private void configAdaptadorConector() {
//        try {
//            catalogo = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.CATALOGO.name());
//            balanza = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.BALANZA.name());
//            poliza = daoConectorAdaptador.getConectorAdaptadorByIdEmpresaNombre(empresa.getIdEmpresa(), Conector_Adaptador.POLIZA.name());
//        } catch (Exception e) {
//            descBitacora = usuario.getUserid() + ". configAdaptadorConector - Error " + e.getMessage();
//            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, BitacoraTipo.ERROR.name());
//            logger.error(usuario.getUserid() + ". configAdaptadorConector - Error " + e);
//        }
//    }
}
