/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceCfdisRecibidos;
import com.iqtb.validacion.pojo.CfdisRecibidos;
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
public class DaoCfdisRecibidos implements InterfaceCfdisRecibidos {

    private Logger logger = Logger.getLogger(DaoCfdisRecibidos.class);

    @Override
    public List<CfdisRecibidos> getCfdisByidEmpresa(int idEmpresa) throws Exception {
        List<CfdisRecibidos> listaCFDIs = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from CfdisRecibidos where idEmpresa = :IDEMPRESA ORDER BY fechaRecepcion DESC";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaCFDIs = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getCfdisByidEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaCFDIs;
    }

    @Override
    public boolean delete(CfdisRecibidos cfdi) throws Exception {
        boolean del = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.delete(cfdi);
            tx.commit();
            del = true;
        } catch (HibernateException he) {
            logger.error("delete. ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return del;
    }

    @Override
    public CfdisRecibidos getCfdiByID(int idCfdi) throws Exception {
        CfdisRecibidos cfdi = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from CfdisRecibidos where idCfdiRecibido = :ID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", idCfdi);
            cfdi = (CfdisRecibidos) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getCfdiByID ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return cfdi;
    }

    @Override
    public Boolean actualizarCfdi(CfdisRecibidos cfdi) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.saveOrUpdate(cfdi);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            logger.error("Ocurrio un error al actualizar el Cfdi: " + e);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return false;
    }

    @Override
    public List<CfdisRecibidos> listaCfdisByIdEmpresaEstado(int idEmpresa, String estado) throws Exception {
        List<CfdisRecibidos> listaCFDIs = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from CfdisRecibidos where idEmpresa = :IDEMPRESA and estado = :ESTADO";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            query.setParameter("ESTADO", estado);
            listaCFDIs = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaCfdisByIdEmpresaEstado ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaCFDIs;
    }

    @Override
    public List<CfdisRecibidos> listaCfdisErrorByIdEmpresa(int idEmpresa) throws Exception {
        List<CfdisRecibidos> listaCFDIs = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from CfdisRecibidos where idEmpresa = :IDEMPRESA and (estado = 'CORRUPTO' or estado = 'SELLO_INVALIDO' or estado = 'RECEPTOR_INVALIDO' or estado = 'SERIE_FOLIO_INVALIDO' or estado = 'XML_INVALIDO' or estado = 'CERTIFICADO_INVALIDO')";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaCFDIs = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaCfdisError ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaCFDIs;
    }

    @Override
    public List<CfdisRecibidos> listaCfdisValidosByIdEmpresa(int idEmpresa) throws Exception {
        List<CfdisRecibidos> listaCFDIs = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from CfdisRecibidos where idEmpresa = :IDEMPRESA and error not like '%ERROR:%'";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaCFDIs = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaCfdisValidosByIdEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaCFDIs;
    }

    @Override
    public List<String> listaEstadosCFDIsByIdEmpresa(Integer idEmpresa) throws Exception {
        List<String> listaEstados = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select distinct estado from CfdisRecibidos where idEmpresa = :IDEMPRESA";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            listaEstados = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaEstadosCFDIsByIdEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaEstados;
    }

    @Override
    public List<CfdisRecibidos> getCfdisByIdEmpresaIdSocioComercial(Integer idEmpresa, Integer idSocio) throws Exception {
        List<CfdisRecibidos> listaCFDIs = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from CfdisRecibidos where idEmpresa = :IDEMPRESA and idSocioComercial = :IDSOCIO";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            query.setParameter("IDSOCIO", idSocio);
            listaCFDIs = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getCfdisByIdEmpresaIdSocioComercial ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaCFDIs;
    }

    @Override
    public List<String> listaEstadosCFDIsByIdEmpresaIdSocio(Integer idEmpresa, Integer idSocio) throws Exception {
        List<String> listaEstados = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "select distinct estado from CfdisRecibidos where idEmpresa = :IDEMPRESA and idSocioComercial = :IDSOCIO";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            query.setParameter("IDSOCIO", idSocio);
            listaEstados = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaEstadosCFDIsByIdEmpresaIdSocio ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaEstados;
    }

    @Override
    public List<CfdisRecibidos> listaCfdisByHql(String hql) throws Exception {
        List<CfdisRecibidos> listaCFDIs = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query = session.createQuery(hql);
            listaCFDIs = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaCfdisByHql ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaCFDIs;
    }

    @Override
    public Integer IntegerCfdisByHql(String hql) throws Exception {
        Integer num = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            num = ((Long) session.createQuery(hql).uniqueResult()).intValue();
        } catch (HibernateException he) {
            logger.error("IntegerCfdisByHql ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return num;
    }

    @Override
    public List<Integer> listaIdsCfdisRByHQL(String hql) throws Exception {
        List<Integer> idsCfdiR = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Query query = session.createQuery(hql);
            idsCfdiR = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaIdsCfdisRByHQL ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return idsCfdiR;
    }
    
}
