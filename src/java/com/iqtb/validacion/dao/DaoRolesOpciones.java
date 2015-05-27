/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceRolesOpciones;
import com.iqtb.validacion.pojo.Opciones;
import com.iqtb.validacion.pojo.RolesHasOpciones;
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
public class DaoRolesOpciones implements InterfaceRolesOpciones{
    private Logger logger = Logger.getLogger(DaoRolesOpciones.class);

    @Override
    public List<Opciones> getOpcionesByIdRol(int idRol) throws Exception {
        List<Opciones> listaOpciones = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "select o from Opciones o, RolesHasOpciones r where r.id.idOpcion = o.idOpcion and r.id.idRol = :IDROL";
            Query query = session.createQuery(hql);
            query.setParameter("IDROL", idRol);
            listaOpciones = query.list();
            tx.commit();
        } catch (HibernateException he) {
            tx.rollback();
            logger.error("getOpcionesByIdRol - ERROR "+he);
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaOpciones;
    }

    @Override
    public List<Opciones> getOpciones() throws Exception {
        List<Opciones> listaOpciones = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Opciones";
            Query query = session.createQuery(hql);
            listaOpciones = query.list();
            tx.commit();
        } catch (HibernateException he) {
            tx.rollback();
            logger.error("getOpcionesByIdRol - ERROR "+he);
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaOpciones;
    }

    @Override
    public List<Opciones> getOpcionesByTipo(String tipo) throws Exception {
        List<Opciones> listaOpciones = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Opciones where tipo = :TIPO";
            Query query = session.createQuery(hql);
            query.setParameter("TIPO", tipo);
            listaOpciones = query.list();
            tx.commit();
        } catch (HibernateException he) {
            tx.rollback();
            logger.error("getOpcionesByTipo - ERROR "+he);
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaOpciones;
    }

    @Override
    public boolean deleteByIRol(int idRol) throws Exception {
        boolean delete = false;
        List<RolesHasOpciones> listaRolesOpciones;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from RolesHasOpciones where id.idRol = :IDROL";
            Query query = session.createQuery(hql);
            query.setParameter("IDROL", idRol);
            listaRolesOpciones = query.list();
            for (RolesHasOpciones rolesHasOpciones : listaRolesOpciones) {
                session.delete(rolesHasOpciones);
            }
            tx.commit();
            delete = true;
        } catch (HibernateException he) {
            logger.error("deleteByIRol ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public boolean insertRolesOpciones(RolesHasOpciones rolesOpciones) throws Exception {
        boolean insert = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.saveOrUpdate(rolesOpciones);
            tx.commit();
            insert = true;
        } catch (HibernateException he) {
            logger.error("insertRolesOpciones ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }
    
}
