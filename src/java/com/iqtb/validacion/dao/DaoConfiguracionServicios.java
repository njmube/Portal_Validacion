/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceConfiguracionServicio;
import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoConfiguracionServicios implements InterfaceConfiguracionServicio{
    private Logger logger = Logger.getLogger(DaoConfiguracionServicios.class);

    @Override
    public ConfiguracionesServicios getConfigServicioByIdServicioPropiedad(int idServicio, String propiedad) throws Exception {
        ConfiguracionesServicios configServicio = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from ConfiguracionesServicios where idServicio = :IDSERVICIO and propiedad = :PROPIEDAD";
            Query query = session.createQuery(hql);
            query.setParameter("IDSERVICIO", idServicio);
            query.setParameter("PROPIEDAD", propiedad);
            configServicio = (ConfiguracionesServicios) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("Error al obtener Configuraciones Servicios. ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return configServicio;
    }

    @Override
    public boolean updateConfigServicios(ConfiguracionesServicios configServicios) throws Exception {
        boolean update = false;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.saveOrUpdate(configServicios);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("Error al modificar Configuraciones Servicios. ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public List<ConfiguracionesServicios> listaServiciosByIdServicio(Integer idServicio) throws Exception {
        List<ConfiguracionesServicios> listaConfig = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from ConfiguracionesServicios where idServicio = :IDSERVICIO";
            Query query = session.createQuery(hql);
            query.setParameter("IDSERVICIO", idServicio);
            listaConfig = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaServiciosByIdServicio - ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaConfig;
    }
    
}
