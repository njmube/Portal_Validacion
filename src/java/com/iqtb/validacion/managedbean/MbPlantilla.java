/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoEmpresa;
import com.iqtb.validacion.dao.DaoPlantilla;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.Plantillas;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import com.iqtb.validacion.util.Imagen;
import com.iqtb.validacion.util.ImprimirPlantilla;
import com.iqtb.validacion.util.ManejoArchivos;
import com.iqtb.validacion.util.Subreporte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author danielromero
 */
@ManagedBean
@SessionScoped
public class MbPlantilla implements Serializable {

    private Plantillas plantilla;
    private List<Plantillas> listaPlantillas;
    private List<ImprimirPlantilla> plantillasSeleccionadas;
    private final String destino = "/work/iqtb/validacionfiles/plantillas/tmp/";
    private String carpeta = "";
    private String principal = "";
    private String nombrePlantilla;
    private List<Imagen> listaImagenes;
    private Subreporte subreporteSeleccionado;
    private List<Subreporte> listaSubreportes;
    private Usuarios usuario;
    private Empresas empresa;
    private boolean existePlantilla;
    private final String sessionUsuario;
    private String descBitacora;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbPlantilla.class);

    public MbPlantilla() {
        this.plantilla = new Plantillas();
        this.listaPlantillas = new ArrayList<Plantillas>();
        this.plantillasSeleccionadas = new ArrayList<ImprimirPlantilla>();
        this.listaImagenes = new ArrayList<Imagen>();
        this.subreporteSeleccionado = new Subreporte();
        this.listaSubreportes = new ArrayList<Subreporte>();
        File f1 = new File(destino);
        if (!f1.isDirectory()) {
            f1.mkdirs();
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String empresaSeleccionada = (String) httpServletRequest.getSession().getAttribute("empresaSeleccionada");
        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = new DaoUsuario().getByUserid(this.sessionUsuario);
            empresa = new DaoEmpresa().getEmpresaByRFC(empresaSeleccionada);
        } catch (Exception e) {
            descBitacora = "ERROR " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, "ERROR");
            logger.error("Error al obtener USUARIO y EMPRESA. ERROR: " + e);
        }

    }

    public Plantillas getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(Plantillas plantilla) {
        this.plantilla = plantilla;
    }

    public List<ImprimirPlantilla> getListaPlantillas() {
        List<ImprimirPlantilla> imprimirPlantilla = new ArrayList<ImprimirPlantilla>();
        try {
            this.listaPlantillas = new DaoPlantilla().listaPlantillasByIdEmpresa(empresa.getIdEmpresa());
            if (this.listaPlantillas == null || this.listaPlantillas.size() <= 0) {
                logger.info("getListaPlantillas - No existen Plantillas para mostrar");
                return imprimirPlantilla;
            }
        } catch (Exception ex) {
            logger.error(usuario.getUserid() + ". getListaPlantillas - ERROR " + ex);
        }
        for (Plantillas plantilla : listaPlantillas) {
            ImprimirPlantilla iPlantilla = new ImprimirPlantilla();
            iPlantilla.setIdPlantillas(plantilla.getIdPlantillas());
            iPlantilla.setIdEmpresa(plantilla.getIdEmpresa());
            iPlantilla.setNombre(plantilla.getNombre());
            iPlantilla.setRuta(plantilla.getRuta());
            iPlantilla.setRfcEmpresa(empresaById(plantilla.getIdEmpresa()));
            imprimirPlantilla.add(iPlantilla);
        }
        return imprimirPlantilla;
    }

    public void setListaPlantillas(List<Plantillas> listaPlantillas) {
        this.listaPlantillas = listaPlantillas;
    }

    public List<ImprimirPlantilla> getPlantillasSeleccionadas() {
        return plantillasSeleccionadas;
    }

    public void setPlantillasSeleccionadas(List<ImprimirPlantilla> plantillasSeleccionadas) {
        this.plantillasSeleccionadas = plantillasSeleccionadas;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public List<Subreporte> getListaSubreportes() {
        return listaSubreportes;
    }

    public void setListaSubreportes(List<Subreporte> listaSubreportes) {
        this.listaSubreportes = listaSubreportes;
    }

    public Subreporte getSubreporteSeleccionado() {
        return subreporteSeleccionado;
    }

    public void setSubreporteSeleccionado(Subreporte subreporteSeleccionado) {
        this.subreporteSeleccionado = subreporteSeleccionado;
    }

    public List<Imagen> getListaImagenes() {
        return listaImagenes;
    }

    public void setListaImagenes(List<Imagen> listaImagenes) {
        this.listaImagenes = listaImagenes;
    }

    public String getNombrePlantilla() {
        return nombrePlantilla;
    }

    public void setNombrePlantilla(String nombrePlantilla) {
        this.nombrePlantilla = nombrePlantilla;
    }

    public boolean isExistePlantilla() {
        existePlantilla = existePlantilla();
        return existePlantilla;
    }

    public void setExistePlantilla(boolean existePlantilla) {
        this.existePlantilla = existePlantilla;
    }

    public void resetPlantilla() {
        this.carpeta = "";
        this.principal = "";
        principal = "";
        this.listaImagenes = new ArrayList<Imagen>();
        this.listaSubreportes = new ArrayList<Subreporte>();
        ManejoArchivos managed = new ManejoArchivos();
        managed.eliminar(destino);

    }

    public String irCargarPlantilla() {
        return "/principal?faces-redirect=true";
    }

    public String onFlowProcess(FlowEvent event) {
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

        this.carpeta = ma.quirtarExtension(event.getFile().getFileName()); //asignamos el nombre de la carpeta con el nombre de la plantilla
        logger.info("carpeta " + carpeta);
        logger.info("destino " + destino);
        logger.info("Nombre carpeta " + destino + empresa.getRfc() + "/" + carpeta + "/");
        File direccion = new File(destino + empresa.getRfc() + "/" + carpeta + "/");

        boolean ban = direccion.mkdirs(); // creamos la ruta temporal de la plantilla
        if (ban) {
            logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Se creo correctamente el directorio de la plantilla: " + direccion.getAbsolutePath());
            InputStream in = null;
            try {
                in = event.getFile().getInputstream();

            } catch (IOException ex) {
                descBitacora = "ERROR " + ex.getMessage();
                registrarBitacora(null, null, null, descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + ex.getMessage());
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
                    logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Ruta del reporte Principal: " + f2.getAbsolutePath());
                    boolean compila = ma.compilarPlantilla(f2.getAbsolutePath());
                    if (compila) {
                        listaSubreportes.clear();// Se limpia la lista de subreportes
                        logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Se compilo corractamente el reporte principal: " + f2.getName());
                        this.principal = event.getFile().getFileName(); //asignamos el nombre de la planilla
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La plantilla: " + event.getFile().getFileName() + " a sido cargada y compilada");
                        try {
                            this.listaImagenes = ma.ListaImagenes(destino + empresa.getRfc() + "/" + carpeta + "/main.xprint");
//                            this.listaImagenes = ma.listaImagenes(destino + sessionUsuario + "/" + carpeta + "/main.xprint");
                            for (Imagen ima : listaImagenes) {
                                logger.info("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Imagen de reporte principal: " + ima.getNombre());
                            }
                        } catch (IOException e) {
                            logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al obtener la imagenes del reporte principal");
                        }
                    } else {
                        logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al compilar la plantillas: " + f2.getName());
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La plantilla: " + event.getFile().getFileName() + " no ha sido compilada");
                    }
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (IOException e) {
                descBitacora = "ERROR " + e.getMessage();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al cargar la plantilla " + e.getMessage());
            }
        } else {
            descBitacora = "[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            logger.error("[ " + sessionUsuario + " - metodo_uploadPrincipal MBPlantillas ] Error al crear el directorio de la plantilla: " + direccion.getAbsolutePath());
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

    public String eliminarPlantilla() {
        boolean bandera;

        for (ImprimirPlantilla iPlantillas : plantillasSeleccionadas) {
            Plantillas plantilla = new Plantillas();
            plantilla.setIdPlantillas(iPlantillas.getIdPlantillas());
            plantilla.setIdEmpresa(iPlantillas.getIdEmpresa());
            plantilla.setNombre(iPlantillas.getNombre());
            plantilla.setRuta(iPlantillas.getRuta());
            this.plantilla = plantilla;

            try {
                DaoPlantilla daoPlantillas = new DaoPlantilla();
                bandera = daoPlantillas.deletePlantilla(this.plantilla);
                if (bandera) {
                    logger.info("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el registro de plantilla en la BD: " + this.plantilla.getNombre());
                    String rutaPlantilla = "/work/iqtb/validacionfiles/plantillas/" + empresa.getRfc(); // /xsa/tomcat/webapps/ROOT/iqtb/Plantillas/

                    boolean eliminaZip = new ManejoArchivos().eliminar(rutaPlantilla);
                    if (eliminaZip) {
                        descBitacora = "[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el archivo de plantilla, ruta: " + this.plantilla.getRuta();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "INFO");
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se elimino la plantilla: " + this.plantilla.getNombre());
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        logger.info("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Se elimino correctamente el archivo de plantilla, ruta: " + this.plantilla.getRuta());
                    } else {
                        descBitacora = "[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Erro al eliminar el archivo de la plantilla, ruta: " + this.plantilla.getRuta();
                        registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error eliminando la plantilla: " + this.plantilla.getNombre());
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        logger.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Erro al eliminar el archivo de la plantilla, ruta: " + this.plantilla.getRuta());
                    }
                } else {
                    descBitacora = "[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre();
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                    logger.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "no se pudo eliminar la plantilla: " + this.plantilla.getNombre());
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (Exception ex) {
                descBitacora = "[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - metodo_eliminarPlantilla MBPlantillas ] Error al eliminar registro de plantilla en la BD: " + this.plantilla.getNombre());
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error al intentar eliminar la plantilla: " + this.plantilla.getNombre());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        this.plantilla = new Plantillas();
        return "/Configuracion/plantilla?faces-redirect=true";
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
                            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                            logger.error("[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al obtener las imagenes del subreporte");
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Subreporte: ", event.getFile().getFileName() + " no ha sido cargado correctamente y no pudo compilar");
                    }
                }
            } catch (IOException e) {
                descBitacora = "[ " + sessionUsuario + " - metodo_uploadSubreportes MBPlantillas ] Error al cargar el subreporte ";
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
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
//        return "/Plantillas/plantilla";
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
                    registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                    logger.error("[ " + sessionUsuario + " - metodo_uploadImagenes MBPlantillas ] Error al cargar la imagen: " + e.getMessage());
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void guardarPlantilla() {
        ManejoArchivos ma = new ManejoArchivos();
        Plantillas p = new Plantillas();
        try {
            String rutaFinal = "/work/iqtb/validacionfiles/plantillas/";
            ma.mover(destino + empresa.getRfc(), rutaFinal + empresa.getRfc());
            p.setIdEmpresa(empresa.getIdEmpresa());
            if (nombrePlantilla != null && !nombrePlantilla.trim().isEmpty()) {
                p.setNombre(nombrePlantilla);
            } else {
                p.setNombre("Plantilla " + empresa.getRfc());
            }
            p.setRuta(rutaFinal + empresa.getRfc() + "/" + carpeta + "/main.jasper");
            logger.info("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Plantilla a insertar, nombre: " + p.getNombre() + ", ruta: " + p.getRuta());
            boolean ban = new DaoPlantilla().insertPlantilla(p);
            if (ban) {
                descBitacora = sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Se inserto correctamente la plantilla en la BD: " + p.getNombre();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "INFO");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha cargado correctamente la plantilla: " + p.getNombre());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.info(sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Se inserto correctamente la plantilla en la BD: " + p.getNombre());
            } else {
                descBitacora = "[ " + sessionUsuario + " - metodo_guardaPlantilla MBPlantillas ] No se inserto la plantilla en la BD: " + p.getNombre();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrio un error guardando la plantilla " + p.getNombre());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.error("[ " + sessionUsuario + " - metodo_guardaPlantilla MBPlantillas ] No se inserto la plantilla en la BD: " + p.getNombre());
            }
        } catch (IOException ex) {
            descBitacora = "[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            logger.error("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
        } catch (Exception ex) {
            descBitacora = "[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            logger.error("[ " + sessionUsuario + " - metodo_guardarPlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
        }
    }

    public void updatePlantilla() {
        ManejoArchivos ma = new ManejoArchivos();
        Plantillas p = new Plantillas();
        try {
            String rutaPlantilla = "/work/iqtb/validacionfiles/plantillas/" + empresa.getRfc();
            boolean eliminaPlantilla = new ManejoArchivos().eliminar(rutaPlantilla);
            if (eliminaPlantilla) {
                logger.info("Se Elimino la Plantilla existente " + rutaPlantilla);
            } else {
                logger.error("Error al eliminar la Plantilla existente " + rutaPlantilla);
                return;
            }
            String rutaFinal = "/work/iqtb/validacionfiles/plantillas/";
            ma.mover(destino + empresa.getRfc(), rutaFinal + empresa.getRfc());
            plantilla.setIdEmpresa(empresa.getIdEmpresa());
            if (nombrePlantilla != null && !nombrePlantilla.trim().isEmpty()) {
                plantilla.setNombre(nombrePlantilla);
            } else {
                plantilla.setNombre("Plantilla " + empresa.getRfc());
            }
            plantilla.setRuta(rutaFinal + empresa.getRfc() + "/" + carpeta + "/" + "main" + ".jasper");

            logger.info("[ " + sessionUsuario + " - updatePlantilla MBPlantillas ] Plantilla a insertar, nombre: " + plantilla.getNombre() + ", ruta: " + plantilla.getRuta());
            boolean ban = new DaoPlantilla().updatePlantilla(plantilla);
            Plantillas plantilla1;
            if (ban) {
                descBitacora = sessionUsuario + " - updatePlantilla MBPlantillas ] Se inserto correctamente la plantilla en la BD: " + plantilla.getNombre();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "INFO");
                logger.info(sessionUsuario + " - updatePlantilla MBPlantillas ] Se inserto correctamente la plantilla en la BD: " + plantilla.getNombre());
            } else {
                descBitacora = "[ " + sessionUsuario + " - updatePlantilla MBPlantillas ] No se inserto la plantilla en la BD: " + plantilla.getNombre();
                registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
                logger.error("[ " + sessionUsuario + " - updatePlantilla MBPlantillas ] No se inserto la plantilla en la BD: " + plantilla.getNombre());
            }
        } catch (IOException ex) {
            descBitacora = "[ " + sessionUsuario + " - updatePlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            logger.error("[ " + sessionUsuario + " - updatePlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
        } catch (Exception ex) {
            descBitacora = "[ " + sessionUsuario + " - updatePlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage();
            registrarBitacora(usuario.getIdUsuario(), null, empresa.getIdEmpresa(), descBitacora, "ERROR");
            logger.error("[ " + sessionUsuario + " - updatePlantilla MBPlantillas ] Error al crear el zip: " + ex.getMessage());
        }

        this.msg = new FacesMessage("Correcto", " Se ha cargado correctamente la plantilla: " + plantilla.getNombre());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void existeSeleccionPlantilla() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("nombreParametro");
        logger.debug("parametro: " + parametro);

        boolean estadoPlantilla = false;
        if (this.plantillasSeleccionadas.isEmpty()) {
            this.msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione una Plantilla");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoPlantilla = true;
                }
            } else if (this.plantillasSeleccionadas.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione una Plantilla");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                estadoPlantilla = true;
                for (ImprimirPlantilla iPlantillas : plantillasSeleccionadas) {
                    Plantillas plantilla = new Plantillas();
                    plantilla.setIdPlantillas(iPlantillas.getIdPlantillas());
                    plantilla.setIdEmpresa(iPlantillas.getIdEmpresa());
                    plantilla.setNombre(iPlantillas.getNombre());
                    plantilla.setRuta(iPlantillas.getRuta());
                    this.plantilla = plantilla;
                }
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoPlantilla", estadoPlantilla);
//        return "/Configuracion/plantilla?faces-redirect=true";
    }

    public String empresaById(int idEmpresa) {
        Empresas empresa = null;
        try {
            empresa = new DaoEmpresa().getEmpresaByidEmpresa(idEmpresa);
        } catch (Exception ex) {
            logger.error("empresaById - ERROR " + ex);
        }
        if (empresa == null) {
            return "";
        }
        return empresa.getRfc();
    }

    public boolean existePlantilla() {
        if (listaPlantillas.size() <= 0) {
            return true;
        } else {
            return false;
        }
    }

}
