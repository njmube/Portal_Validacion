/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceConectorAdaptador;
import com.iqtb.validacion.pojo.ConectorAdaptador;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoConectorAdaptador implements InterfaceConectorAdaptador{
private Logger logger = Logger.getLogger(DaoConectorAdaptador.class);
    @Override
    public ConectorAdaptador getConectorAdaptadorByIdEmpresaNombre(Integer idEmpresa, String nombre) throws Exception {
        ConectorAdaptador conectorAdaptador = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "from ConectorAdaptador where idEmpresa = :IDEMPRESA and nombre = :NOMBRE";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            query.setParameter("NOMBRE", nombre);
            conectorAdaptador = (ConectorAdaptador) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getConectorAdaptadorByIdEmpresaNombre ERROR: " + he);
            tx.rollback();
        }finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return conectorAdaptador;
    }

    @Override
    public boolean updateConectorAdaptador(ConectorAdaptador conectorAdaptador) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.saveOrUpdate(conectorAdaptador);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("updateConectorAdaptador ERROR: " + he);
            tx.rollback();
        }finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }
    
}
