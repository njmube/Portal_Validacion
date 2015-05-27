/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceOrdenCompra;
import com.iqtb.validacion.pojo.OrdenesCompra;
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
public class DaoOrdenCompra implements InterfaceOrdenCompra {

    private Logger logger = Logger.getLogger(DaoOrdenCompra.class);

    @Override
    public OrdenesCompra getOrdenById(Integer idOrdenCompra) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OrdenesCompra> listaOrdenByHql(String hql) throws Exception {
        List<OrdenesCompra> lista = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query = session.createQuery(hql);
            lista = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaOrdenByHql ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return lista;
    }

    @Override
    public boolean insertOrdenCompra(OrdenesCompra ordenCompra) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.save(ordenCompra);
            tx.commit();
            return true;
        } catch (HibernateException he) {
            logger.error("insertOrdenCompra ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    @Override
    public boolean updateOrdenCompra(OrdenesCompra ordenCompra) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.update(ordenCompra);
            tx.commit();
            return true;
        } catch (HibernateException he) {
            logger.error("updateOrdenCompra ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    @Override
    public boolean deleteOrdenCompra(OrdenesCompra ordenCompra) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.delete(ordenCompra);
            tx.commit();
            return true;
        } catch (HibernateException he) {
            logger.error("deleteOrdenCompra ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    @Override
    public OrdenesCompra obtenerOrden(String ordenDeCompra, Integer idEmpresa, Integer idSocioComercial) throws Exception {
        OrdenesCompra ordenCompra = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from OrdenesCompra where idEmpresa = :IDEMPRESA and idSocioComercial = :IDSOCIO and numeroOc = :NUMORDEN";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            query.setParameter("IDSOCIO", idSocioComercial);
            query.setParameter("NUMORDEN", ordenDeCompra);
            ordenCompra = (OrdenesCompra) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("obtenerOrden ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return ordenCompra;
    }

    @Override
    public String numOrdenCompraById(Integer idOrenCompra) throws Exception {
        String numOrdenCompra = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select numeroOc from OrdenesCompra where idOrdenCompra = :IDORDEN";
            Query query = session.createQuery(hql);
            query.setParameter("IDORDEN", idOrenCompra);
            numOrdenCompra = (String) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("numOrdenCompraById ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return numOrdenCompra;
    }

}
