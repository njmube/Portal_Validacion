/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfacePlantilla;
import com.iqtb.validacion.pojo.Plantillas;
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
public class DaoPlantilla implements InterfacePlantilla{
private Logger logger = Logger.getLogger(DaoPlantilla.class);
    @Override
    public boolean insertPlantilla(Plantillas plantilla) throws Exception {
        boolean insert = false;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.save(plantilla);
            tx.commit();
            insert = true;
        } catch (HibernateException he) {
            logger.error("insertPlantilla ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }

    @Override
    public boolean updatePlantilla(Plantillas plantilla) throws Exception {
        boolean update = false;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.update(plantilla);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("updatePlantilla ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public boolean deletePlantilla(Plantillas plantilla) throws Exception {
        boolean delete = false;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.delete(plantilla);
            tx.commit();
            delete = true;
        } catch (HibernateException he) {
            logger.error("deletePlantilla ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public Plantillas getPlantillaById(int idPlantilla) throws Exception {
        Plantillas plantilla = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Plantillas where idPlantillas = :IDPLANTILLA";
            Query query = session.createQuery(hql);
            query.setParameter("IDPLANTILLAS", idPlantilla);
            plantilla = (Plantillas) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getPlantillaById ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return plantilla;
    }

    @Override
    public Plantillas getPlantillaByNombre(String nombre) throws Exception {
        Plantillas plantilla = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Plantillas where nombre = :NOMBRE";
            Query query = session.createQuery(hql);
            query.setParameter("NOMBRE", nombre);
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getPlantillaByNombre ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return plantilla;
    }

    @Override
    public List<Plantillas> listaPlantillas() throws Exception {
        List<Plantillas> listaPlantillas = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Plantillas";
            Query query = session.createQuery(hql);
            listaPlantillas = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaPlantillas - ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaPlantillas;
    }

    @Override
    public List<Plantillas> listaPlantillasByIdEmpresa(int idEmpresa) throws Exception {
        List<Plantillas> listaPlantillas = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from Plantillas where idEmpresa = :IDEMPRESA";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaPlantillas = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaPlantillasByIdEmpresa - ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaPlantillas;
    }
    
}
