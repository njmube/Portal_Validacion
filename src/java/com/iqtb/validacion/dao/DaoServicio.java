/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceServicio;
import com.iqtb.validacion.pojo.Servicios;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoServicio implements InterfaceServicio{
    private Logger logger = Logger.getLogger(DaoServicio.class);

    @Override
    public Servicios getServicoByNombre(String nombre) throws Exception{
        Servicios servicio = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Servicios where nombre = :NOMBRE";
            Query query = session.createQuery(hql);
            query.setParameter("NOMBRE", nombre);
            servicio = (Servicios) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getServicoByNombre - Error al obtener el servicio. ERROR: "+he);
            tx.rollback();
//            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
               session.close();
            }
        }
        return servicio;
    }

    @Override
    public boolean updateServicio(Servicios servicio) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.saveOrUpdate(servicio);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("updateServicio - ERROR: "+he);
            tx.rollback();
//            session.getTransaction().rollback();
        }finally{
            if (session.isOpen()) {
               session.close();
            }
        }
        return update;
    }
    
}
