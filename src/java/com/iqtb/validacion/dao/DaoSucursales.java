/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceSucursales;
import com.iqtb.validacion.pojo.Sucursales;
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
public class DaoSucursales implements InterfaceSucursales {

    private Logger logger = Logger.getLogger(DaoSucursales.class);

    @Override
    public List<Sucursales> listaSucursalesByIdEmpresa(Integer idEmpresa) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List<Sucursales> listaSucursales = null;
        try {
            String hql = "from Sucursales where idEmpresa = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idEmpresa);
            listaSucursales = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaSucursalesByIdEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaSucursales;
    }

    @Override
    public List<Sucursales> listaSucursalesByHql(String hql) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List<Sucursales> listaSucursales = null;
        try {
            Query query = session.createQuery(hql);
            listaSucursales = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaSucursalesByHql ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaSucursales;
    }

    @Override
    public Sucursales getSucursalByHql(String hql) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Sucursales sucursal = null;
        try {
            Query query = session.createQuery(hql);
            sucursal = (Sucursales) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getSucursalByHql ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return sucursal;
    }

    @Override
    public List<Integer> listaIdSucursalByNombre(Integer idEmpresa, String nombreSucursal) throws Exception {
        List<Integer> ids = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select idSucursal from Sucursales where idEmpresa = :IDEMPRESA and nombre like '" + nombreSucursal + "%'";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            ids = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaIdSucursalByNombre ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return ids;
    }

    @Override
    public String nombreSucursalByIdSucursal(Integer idSucursal) throws Exception {
        String nombre = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select nombre from Sucursales where idSucursal = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idSucursal);
            nombre =  (String) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("nombreSucursalByIdSucursal ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return nombre;
    }

}
