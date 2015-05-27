/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceContabilidadElectronica;
import com.iqtb.validacion.pojo.ContabilidadElectronica;
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
public class DaoContabilidadElectronica implements InterfaceContabilidadElectronica {

    private Logger logger = Logger.getLogger(DaoContabilidadElectronica.class);

    @Override
    public List<ContabilidadElectronica> listaContabilidadElectronica(Integer idEmpresa) throws Exception {
        List<ContabilidadElectronica> listaContabilidadElectronica = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from ContabilidadElectronica where idEmpresa = :IDEMPRESA";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaContabilidadElectronica = query.list();
            tx.commit();
        } catch (HibernateException e) {
            logger.error("listaContabilidadElectronica - ERROR " + e);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaContabilidadElectronica;
    }

    @Override
    public ContabilidadElectronica getContabilidadElectronica(Integer idContabilidad) throws Exception {
        ContabilidadElectronica contabilidadElectronica = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from ContabilidadElectronica where idContabilidad = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idContabilidad);
            contabilidadElectronica = (ContabilidadElectronica) query.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            logger.error("getContabilidadElectronica - ERROR " + e);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return contabilidadElectronica;
    }

    @Override
    public boolean insertContabilidadElectonica(ContabilidadElectronica contabilidadElectonica) throws Exception {
        boolean insert = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.save(contabilidadElectonica);
            tx.commit();
            insert = true;
        } catch (HibernateException e) {
            logger.error("insertContabilidadElectonica - ERROR " + e);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }

    @Override
    public boolean updateContabilidadElectonica(ContabilidadElectronica contabilidadElectonica) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.update(contabilidadElectonica);
            tx.commit();
            update = true;
        } catch (HibernateException e) {
            logger.error("updateContabilidadElectonica - ERROR " + e);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public boolean deleteContabilidadElectronica(ContabilidadElectronica contabilidadElectonica) throws Exception {
        boolean delete = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.delete(contabilidadElectonica);
            tx.commit();
            delete = true;
        } catch (HibernateException e) {
            logger.error("deleteContabilidadElectronica - ERROR " + e);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public ContabilidadElectronica getContElectronicaByHql(String hql) throws Exception {
        ContabilidadElectronica contabilidadElectronica = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query = session.createQuery(hql);
            contabilidadElectronica = (ContabilidadElectronica) query.uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            logger.error("getContElectronicaByHql - ERROR " + e);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return contabilidadElectronica;
    }

}
