/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceSociosComerciales;
import com.iqtb.validacion.pojo.SociosComerciales;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.SortOrder;

/**
 *
 * @author danielromero
 */
public class DaoSociosComerciales implements InterfaceSociosComerciales {

    private Logger logger = Logger.getLogger(DaoSociosComerciales.class);

    @Override
    public SociosComerciales getSocioComercialByID(Integer idSocioComercial) throws Exception {
        SociosComerciales socioComercial = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from SociosComerciales where idSocioComercial = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idSocioComercial);
            socioComercial = (SociosComerciales) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getSocioComercialByID ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return socioComercial;
    }

    @Override
    public List<Integer> filtroSocioComercialByRFC(Integer idEmpresa, String rfc) throws Exception {
        List<Integer> sc = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select idSocioComercial from SociosComerciales where idEmpresa = :IDEMPRESA and rfc like '" + rfc + "%'";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            sc = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("filtroSocioComercialByRFC ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return sc;
    }

    @Override
    public List<SociosComerciales> getSociosComercialesByIdEmpresa(Integer idEmpresa) throws Exception {
        List<SociosComerciales> listaSC = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from SociosComerciales where idEmpresa = :IDEMPRESA";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaSC = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getSociosComercialesByIdEmpresa - ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaSC;
    }

    @Override
    public boolean insertSocioComercial(SociosComerciales socioComercial) throws Exception {
        boolean insert = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.save(socioComercial);
            tx.commit();
            insert = true;
        } catch (HibernateException he) {
            logger.error("insertSocioComercial - ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }

    @Override
    public boolean updateSocioComercial(SociosComerciales socioComercial) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.update(socioComercial);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("updateSocioComercial - ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public boolean deleteSocioComercial(SociosComerciales socioComercial) throws Exception {
        boolean delete = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.delete(socioComercial);
            tx.commit();
            delete = true;
        } catch (HibernateException he) {
            logger.error("deleteSocioComercial - ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public SociosComerciales getSocioComercialByIdEmpresaRFC(Integer idEmpresa, String rfc) throws Exception {
        SociosComerciales socioComercial = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from SociosComerciales where idEmpresa = :IDEMPRESA and rfc = :RFC";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            query.setParameter("RFC", rfc);
            socioComercial = (SociosComerciales) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getSocioComercialByIdEmpresaRFC ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return socioComercial;
    }

    @Override
    public Integer IntegerSociosByHql(String hql) throws Exception {
        Integer num = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            num = ((Long) session.createQuery(hql).uniqueResult()).intValue();
        } catch (HibernateException he) {
            logger.error("IntegerSociosByHql ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return num;
    }

    @Override
    public List<SociosComerciales> listaSociosCriteria(Integer idEmpresa, Integer inicio, Integer fin, String sortField, SortOrder sortOrder, Map<String, String> filters) throws Exception {
        List<SociosComerciales> lista = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Criteria cr = session.createCriteria(SociosComerciales.class);
        cr.add(Restrictions.eq("idEmpresa", idEmpresa));

        if (sortField != null && !sortField.isEmpty()) {
            if (sortOrder.equals(SortOrder.ASCENDING)) {
                cr.addOrder(Order.asc(sortField));
            } else {
                cr.addOrder(Order.desc(sortField));
            }
        }
        if (!filters.isEmpty()) {
            Iterator it = filters.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                cr.add(Restrictions.like(key, "" + filters.get(key) + "%"));
            }
        }
        cr.setFirstResult(inicio);
        cr.setMaxResults(inicio + fin);
        lista = cr.list();

        return lista;
    }

    @Override
    public List<SociosComerciales> listaSociosHQL(String hql) throws Exception {
        List<SociosComerciales> socios = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query = session.createQuery(hql);
            socios = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaSociosHQL - ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return socios;
    }

    @Override
    public String getRFCSociobyIdSocio(Integer idSocioComercial) throws Exception {
        String rfc = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select rfc from SociosComerciales where idSocioComercial = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idSocioComercial);
            rfc = (String) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getRFCSociobyIdSocio ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return rfc;
    }

    @Override
    public SociosComerciales getSocioByHql(String hql) throws Exception {
        SociosComerciales socio = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query = session.createQuery(hql);
            socio = (SociosComerciales) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getSocioByHql - ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return socio;
    }

    @Override
    public String getNombreSociobyIdSocio(Integer idSocioComercial) throws Exception {
        String nombre = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select nombre from SociosComerciales where idSocioComercial = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idSocioComercial);
            nombre = (String) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getNombreSociobyIdSocio ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return nombre;
    }

    @Override
    public List<Integer> filtroSocioComercialByNombre(Integer idEmpresa, String nombre) throws Exception {
        List<Integer> sc = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select idSocioComercial from SociosComerciales where idEmpresa = :IDEMPRESA and nombre like '" + nombre + "%'";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            sc = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("filtroSocioComercialByNombre ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return sc;
    }

}
