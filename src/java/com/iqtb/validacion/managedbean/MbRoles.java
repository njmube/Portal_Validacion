/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoRoles;
import com.iqtb.validacion.dao.DaoRolesOpciones;
import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.pojo.Opciones;
import com.iqtb.validacion.pojo.Roles;
import com.iqtb.validacion.pojo.RolesHasOpciones;
import com.iqtb.validacion.pojo.RolesHasOpcionesId;
import com.iqtb.validacion.pojo.Usuarios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbRoles implements Serializable {

    private Roles rol;
    private List<Roles> listaRoles;
    private List<Roles> rolesSeleccionados;
    private List<Opciones> listaOpciones;
    private List<Opciones> opcionesSeleccionadas;
    private List<Opciones> allOpciones;
    private List<Opciones> tipoOpciones;
    private DualListModel<String> dualListOpciones;
    private List<String> strOpciones;
    private List<String> strOpcionesSeleccionadas;
    private boolean chkOpcion;
    private final String sessionUsuario;
    private FacesMessage msg;
    private static final Logger logger = Logger.getLogger(MbUsuario.class);

    public MbRoles() {
        this.rol = new Roles();
        this.rolesSeleccionados = new ArrayList<Roles>();
        opcionesSeleccionadas = new ArrayList<Opciones>();

        this.sessionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
    }

    @PostConstruct
    public void init() {
        try {
            allOpciones = new DaoRolesOpciones().getOpciones();
            strOpciones = new ArrayList<String>();
            for (Opciones var : allOpciones) {
                strOpciones.add(var.getDescripcion());
            }

            strOpcionesSeleccionadas = new ArrayList<String>();
            dualListOpciones = new DualListModel<String>(strOpciones, strOpcionesSeleccionadas);

        } catch (Exception ex) {
            logger.error("init - ERROR " + ex);
        }
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public List<Roles> getListaRoles() {
        try {
            this.listaRoles = new DaoRoles().getAllRoles();
        } catch (Exception e) {
            logger.error("getListaRoles - ERROR: " + e);
        }
        return listaRoles;
    }

    public List<Opciones> getListaOpciones() {
        return listaOpciones;
    }

    public void setListaOpciones(List<Opciones> listaOpciones) {
        this.listaOpciones = listaOpciones;
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

    public List<Opciones> getAllOpciones() {
        return allOpciones;
    }

    public void setAllOpciones(List<Opciones> allOpciones) {
        this.allOpciones = allOpciones;
    }

    public DualListModel<String> getDualListOpciones() {
        return dualListOpciones;
    }

    public void setDualListOpciones(DualListModel<String> dualListOpciones) {
        this.dualListOpciones = dualListOpciones;
    }

    public List<String> getStrOpciones() {
        return strOpciones;
    }

    public void setStrOpciones(List<String> strOpciones) {
        this.strOpciones = strOpciones;
    }

    public List<Opciones> getOpcionesSeleccionadas() {
        return opcionesSeleccionadas;
    }

    public void setOpcionesSeleccionadas(List<Opciones> opcionesSeleccionadas) {
        this.opcionesSeleccionadas = opcionesSeleccionadas;
    }

    public List<Opciones> getTipoOpciones() {
        return tipoOpciones;
    }

    public void setTipoOpciones(List<Opciones> tipoOpciones) {
        this.tipoOpciones = tipoOpciones;
    }

    public void limpiarRol() {
        rol = new Roles();
        strOpciones = new ArrayList<String>();
        strOpcionesSeleccionadas = new ArrayList<String>();
        strOpcionesSeleccionadas = new ArrayList<String>();
        dualListOpciones = new DualListModel<String>(strOpciones, strOpcionesSeleccionadas);
    }

    public void insertRol() {
        boolean insert = false;
        opcionesSeleccionadas = new ArrayList<Opciones>();
        if (dualListOpciones.getTarget().size() <= 0) {
            logger.warn("insertRol - dualListOpcionesTarget se encuentra vacia");
        } else {
            for (String item : dualListOpciones.getTarget()) {
                logger.info("OPCION SELECCIONADA " + item);
                for (Opciones opcion : allOpciones) {
                    logger.info("Opciones " + opcion.getDescripcion());
                    if (item.equals(opcion.getDescripcion()) && rol.getTipo().equals(opcion.getTipo())) {
                        opcionesSeleccionadas.add(opcion);
                        logger.info("updateRol - Opcion " + opcion.getDescripcion() + " agredada a Opciones selecionadas");
                    }
                }
            }
        }
        try {
            if (rol.getNombre() != null && !rol.getNombre().isEmpty()) {
                if (!rol.getTipo().equals("")) {
                    DaoRoles daoRol = new DaoRoles();
                    insert = daoRol.insertRol(rol);
                    if (insert) {
                        DaoRolesOpciones daoRolesOpciones = new DaoRolesOpciones();
                        if (opcionesSeleccionadas.size() > 0) {
                            RolesHasOpcionesId rolesOpcionesId = new RolesHasOpcionesId();
                            RolesHasOpciones rolesOpciones = new RolesHasOpciones();
                            for (Opciones op : opcionesSeleccionadas) {
                                rolesOpcionesId.setIdRol(rol.getIdRol());
                                rolesOpcionesId.setIdOpcion(op.getIdOpcion());
                                rolesOpciones.setId(rolesOpcionesId);
                                if (daoRolesOpciones.insertRolesOpciones(rolesOpciones)) {
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", rol.getNombre() + " se la agrego la opcion " + op.getDescripcion());
                                    logger.info("updateRol - " + rol.getNombre() + " se la agrego la opcion " + op.getDescripcion());
                                } else {
                                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la opcion " + op.getDescripcion() + " con el usuario " + rol.getNombre());
                                    logger.error("updateUsuario - Error al asignar la opcion " + op.getDescripcion() + " con el usuario " + rol.getNombre());
                                }
                            }
                        } else {
                            logger.info("No existen opciones para este rol "+rol.getNombre());
                        }
//                        rol = new Roles();
                    } else {
                        logger.error("insertRol - Error al insertar el Rol " + rol.getNombre());
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ha ocurrido un error al crear el rol " + rol.getNombre());
                    }
                } else {
                    logger.warn("insertRol - Por favor, introduzca un valor para Tipo");
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Tipo.");
                }
            } else {
                logger.warn("insertRol - Por favor, introduzca un valor para Nombre");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Nombre.");
            }
        } catch (Exception e) {
            logger.error("insertRol - ERROR "+e);
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("insertRol", insert);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updateRol() {
        boolean update = false;
        opcionesSeleccionadas = new ArrayList<Opciones>();
        if (dualListOpciones.getTarget().size() <= 0) {
            logger.warn("updateRol - dualListOpcionesTarget se encuentra vacia");
        } else {
            for (String item : dualListOpciones.getTarget()) {
                logger.debug("OPCION SELECCIONADA " + item);
                for (Opciones var : tipoOpciones) {
                    logger.debug("Opciones " + var.getDescripcion());
                    if (item.equals(var.getDescripcion())) {
                        opcionesSeleccionadas.add(var);
                        logger.debug("updateRol - Opcion " + var.getDescripcion() + " agredada a Opciones selecionadas");
                        break;
                    }
                }
            }
        }
        try {
            DaoRolesOpciones daoRolesOpciones = new DaoRolesOpciones();
            if (daoRolesOpciones.deleteByIRol(rol.getIdRol())) {
                logger.info("updateRol - Se han eliminado todas las opciones que tenia asignadas el rol con id: " + rol.getIdRol());
            }
            if (rol.getNombre() != null && !rol.getNombre().isEmpty()) {
                DaoRoles daoRol = new DaoRoles();
                update = daoRol.updateRol(rol);
                if (update) {
                    if (opcionesSeleccionadas.size() > 0) {
                        RolesHasOpcionesId rolesOpcionesId = new RolesHasOpcionesId();
                        RolesHasOpciones rolesOpciones = new RolesHasOpciones();
                        for (Opciones op : opcionesSeleccionadas) {
                            rolesOpcionesId.setIdRol(rol.getIdRol());
                            rolesOpcionesId.setIdOpcion(op.getIdOpcion());
                            rolesOpciones.setId(rolesOpcionesId);
                            if (daoRolesOpciones.insertRolesOpciones(rolesOpciones)) {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Rol "+rol.getNombre()+" almacenado con "+opcionesSeleccionadas.size()+" opcion(es)");
                                logger.debug("updateRol - " + rol.getNombre() + " se la agrego la opcion " + op.getDescripcion());
                                logger.info("updateRol - " + rol.getNombre() + " se la agrego la opcion " + op.getDescripcion());
                            } else {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al asignar la opcion " + op.getDescripcion() + " con el usuario " + rol.getNombre());
                                logger.error("updateUsuario - Error al asignar la opcion " + op.getDescripcion() + " con el usuario " + rol.getNombre());
                            }
                        }
                    } else {
                        this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Rol almacenado sin opciones");
                        logger.info("No existen Opciones para este Rol "+rol.getNombre());
                    }
//                    rol = new Roles();
                } else {
                    this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al modificar los datos.");
                    logger.error("updateRol - Error al modificar los datos");
                }
            } else {
                logger.warn("updateRol - Por favor, introduzca un valor para Nombre");
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Por favor, introduzca un valor para Nombre.");
            }
        } catch (Exception e) {
            logger.error("updateRol - ERROR "+e);
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("updateRol", update);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void deleteRol() {
        boolean delRol = false;
        List<Usuarios> listUsuarios;
        for (Roles r : rolesSeleccionados) {
            this.rol = r;
            try {
                listUsuarios = new DaoUsuario().getAllUsuarios();
                if (listUsuarios != null && listUsuarios.size() > 0) {
                    for (Usuarios usuario : listUsuarios) {
                        if (usuario.getIdRol() == rol.getIdRol()) {
                            this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No es posible eliminar el Rol. Existen usuarios con este Rol asignado.");
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                            logger.warn("deleteRol - No es posible eliminar el Rol "+rol.getNombre()+" Existen usuarios con este Rol asignado.");
                            return;
                        } else {
                            logger.info("deleteRol -  Eliminar rol " + rol.getNombre());
                            delRol = new DaoRoles().deleteRol(rol);
                            if (delRol) {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Corrrecto", "Rol eliminado.");
                                logger.info("deleteRol - Rol Eliminado");
                            } else {
                                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar Rol.");
                                logger.error("deleteRol - Error al eliminar Rol");
                            }
                        }
                    }
                } else {
                    logger.error("deleteRol - Error al obtener la lista de usuarios");
                }
            } catch (Exception e) {
                this.msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ha ocurrido un error al intentar eliminar el Rol " + rol.getNombre());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                logger.error("deleteRol - ERROR" + e);
            }
        }
//        rol = new Roles();
    }

    public void updateOpciones() {
        if (rol.getTipo() != null && !rol.getTipo().isEmpty()) {
            try {
                allOpciones = new DaoRolesOpciones().getOpciones();
                strOpciones = new ArrayList<String>();
                for (Opciones var : allOpciones) {
                    if (var.getTipo().equals(rol.getTipo())) {
                        strOpciones.add(var.getDescripcion());
                    }
                }
                strOpcionesSeleccionadas = new ArrayList<String>();
                dualListOpciones = new DualListModel<String>(strOpciones, strOpcionesSeleccionadas);

            } catch (Exception ex) {
                logger.error("init - ERROR " + ex);
            }
        } else {
            strOpciones = new ArrayList<String>();
            strOpcionesSeleccionadas = new ArrayList<String>();
            dualListOpciones = new DualListModel<String>(strOpciones, strOpcionesSeleccionadas);
        }
    }

//    public boolean rolOpcionPermitida(Integer idOpcion) {
//        System.out.println("entro rolOpcionPermitida");
//        System.out.println("valor a buscar " + idOpcion);
//        if (idOpcion == null) {
//            System.out.println("IdOpcion es nulo");
//            return false;
//        }
//        if (listaOpciones != null && listaOpciones.size() > 0) {
//            for (Opciones opcion : listaOpciones) {
//                System.out.println("Opcion " + opcion.getIdOpcion());
//                if (opcion.getIdOpcion().equals(idOpcion)) {
//                    System.out.println("Son iguales");
//                    return true;
//                }
//            }
//            System.out.println("No son iguales");
//            return false;
//        }
//        System.out.println("lista nula o vacia");
//        return false;
//    }
//    public boolean tipoOpcion(String tipoRol, String desc) {
//        boolean opcion = false;
//        List<Opciones> listOpciones;
//        try {
//            listOpciones = new DaoRolesOpciones().getOpcionesByTipo(tipoRol);
//            for (Opciones opciones : listOpciones) {
//                if (opciones.getDescripcion().equals(desc)) {
//                    opcion = true;
//                }
//            }
//        } catch (Exception ex) {
//            System.out.println("tipoOpcion - ERROR " + ex);
//        }
//        return opcion;
//    }

    public String existeSeleccionRol() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map params = facesContext.getExternalContext().getRequestParameterMap();

        String parametro = (String) params.get("parametro");

        boolean estadoRol = false;
        if (rolesSeleccionados.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione un Rol.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (parametro != null) {
                if (parametro.equals("eliminar")) {
                    estadoRol = true;
                }
            } else if (rolesSeleccionados.size() > 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Precaución", "Por favor, seleccione solo un Rol.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {

                try {
                    tipoOpciones = new DaoRolesOpciones().getOpcionesByTipo(rolesSeleccionados.get(0).getTipo());
                    opcionesSeleccionadas = new DaoRolesOpciones().getOpcionesByIdRol(rolesSeleccionados.get(0).getIdRol());
                    estadoRol = true;
                } catch (Exception ex) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al cargar las opciones asignadas a este Rol.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    logger.error("existeSeleccionRol - Error al cargar las opciones asignadas a este Rol" + ex);
                    estadoRol = false;
                }
                strOpcionesSeleccionadas = new ArrayList<String>();
                strOpciones = new ArrayList<String>();
                for (Opciones var : tipoOpciones) {
                    strOpciones.add(var.getDescripcion());
                }
                for (Opciones var : opcionesSeleccionadas) {
                    strOpcionesSeleccionadas.add(var.getDescripcion());
                    strOpciones.remove(var.getDescripcion());
                }
                dualListOpciones = new DualListModel<String>(strOpciones, strOpcionesSeleccionadas);
                for (Roles r : rolesSeleccionados) {
                    this.rol = r;
                }
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("estadoRol", estadoRol);

        return "/Usuario/roles?faces-redirect=true";
    }

}
