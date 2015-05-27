/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.managedbean;

import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.emun.BitacoraTipo;
import com.iqtb.validacion.pojo.Script;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author danielromero
 */
@ManagedBean
@ViewScoped
public class MbScript implements Serializable {

    private Script script;
    private List<Script> listaScript;
    private LazyDataModel<Script> lazyDataScript;
    private SelectItem[] itemsHabilitado;
    private String query;
    private String queryCount;
    private String filterProperty;
    private String filterValue;
    private boolean isFirst;
    private Usuarios usuario;
    private String sesionUsuario;
    private String descBitacora;
    private Session session;
    private Transaction tx;
    private List<Integer> listaDias;
    private static final Logger logger = Logger.getLogger("MbScript");

    public MbScript() {
        script = new Script();
        usuario = new Usuarios();
        listaDias = new ArrayList<Integer>();

        sesionUsuario = ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid();
        try {
            usuario = new DaoUsuario().getByUserid(sesionUsuario);
        } catch (Exception e) {
            descBitacora = "[MbScript] Error al obtener el usuario. ERROR: " + e.getMessage();
            registrarBitacora(null, null, null, descBitacora, BitacoraTipo.ERROR.name());
            logger.error(descBitacora);
        }
        itemsHabilitado = new SelectItem[3];
        itemsHabilitado[0] = new SelectItem("", "TODOS");
        itemsHabilitado[1] = new SelectItem("true", "Habilitado");
        itemsHabilitado[2] = new SelectItem("false", "Deshabilitado");
    }

    @PostConstruct
    public void init() {
        lazyDataScript = new LazyDataModel<Script>() {
            @Override
            public List<Script> load(int first, int pageSize, String sortField,
                    SortOrder sortOrder, Map<String, String> filters) {
                listaScript = new ArrayList<Script>();
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();
                logger.info("sortField " + sortField);
                logger.info("sortOrder " + sortOrder);
                logger.info("filters " + filters);
                queryCount = "select count(idScript) ";
                query = "from Script";
                isFirst = true;
                if (!filters.isEmpty()) {
                    Iterator it = filters.keySet().iterator();
                    while (it.hasNext()) {
                        filterProperty = (String) it.next();
                        filterValue = filters.get(filterProperty);
                        if (filterProperty.equals("habilitado")) {
                            if (isFirst) {
                                query = query + " where " + filterProperty + " = " + filterValue;
                            } else {
                                query = query + " and " + filterProperty + " = " + filterValue;
                            }
                            isFirst = false;
                        }
                        if (filterProperty.equals("ultimaFecha") || filterProperty.equals("proximaFecha") || filterProperty.equals("nombre")) {
                            if (isFirst) {
                                query = query + " where " + filterProperty + " like '" + filterValue + "%'";
                            } else {
                                query = query + " and " + filterProperty + " like '" + filterValue + "%'";
                            }
                            isFirst = false;
                        }
                    }
                }
                if (sortField != null && !sortField.isEmpty()) {
                    if (sortOrder.equals(SortOrder.ASCENDING)) {
                        query = query + " ORDER BY " + sortField + " ASC";
                    } else {
                        query = query + " ORDER BY " + sortField + " DESC";
                    }
                } else {
                    query = query + " ORDER BY ultimaFecha DESC";
                }
                logger.info("Query " + query);
                queryCount = queryCount + query;
                Query result = session.createQuery(query);
                this.setRowCount(((Long) session.createQuery(queryCount).uniqueResult()).intValue());
                listaScript = result.list();
                if (session.isOpen()) {
                    session.clear();
                    session.close();
                }
                return listaScript;
            }
        };
    }

    public void verScript() {
        logger.info("Inicia Ver script");
        logger.info("script " + script.getIdScript());
        logger.info("fin Ver script");
    }

    public void crearScript() {
        logger.info("Inicia Crear script");
        String strDias = "";
        for (Integer item : listaDias) {
            strDias = strDias + item + ",";
        }
        logger.info("Nombre " + script.getNombre());
        logger.info("descripcion " + script.getDescripcion());
        logger.info("lista dias " + strDias);
        logger.info("ejecutar " + script.getEjecutar());
        logger.info("hora " + script.getHora());
        logger.info("min " + script.getMinuto());
        logger.info("seg " + script.getSegundo());
    }

    public LazyDataModel<Script> getLazyDataScript() {
        return lazyDataScript;
    }

    public void setLazyDataScript(LazyDataModel<Script> lazyDataScript) {
        this.lazyDataScript = lazyDataScript;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public SelectItem[] getItemsHabilitado() {
        return itemsHabilitado;
    }

    public void setItemsHabilitado(SelectItem[] itemsHabilitado) {
        this.itemsHabilitado = itemsHabilitado;
    }

    public List<Integer> getListaDias() {
        return listaDias;
    }

    public void setListaDias(List<Integer> listaDias) {
        this.listaDias = listaDias;
    }

}
