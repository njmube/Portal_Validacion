/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceEmpresa;
import com.iqtb.validacion.pojo.Empresas;
import com.iqtb.validacion.pojo.UsuariosHasEmpresasId;
import java.util.ArrayList;
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
public class DaoEmpresa implements InterfaceEmpresa {

    private Logger logger = Logger.getLogger(DaoEmpresa.class);

    @Override
    public List<Empresas> getEmpresaById(Integer idUsuario) throws Exception {
        List<Empresas> listaEmpresas = new ArrayList<Empresas>();
        Empresas empresa;
        List<UsuariosHasEmpresasId> lista = new DaoUsuarioEmpresa().getEmpresasByIdUsuario(idUsuario);

        for (UsuariosHasEmpresasId usuariosHasEmpresasId : lista) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            try {
                String hql = "from Empresas where idEmpresa = :IDEMPRESA";
                Query query = session.createQuery(hql);
                query.setParameter("IDEMPRESA", usuariosHasEmpresasId.getIdEmpresa());
                empresa = (Empresas) query.uniqueResult();
                listaEmpresas.add(empresa);
                tx.commit();
            } catch (HibernateException he) {
                logger.error("getEmpresaById ERROR: " + he);
                tx.rollback();
            } finally {
                if (session.isOpen()) {
                    session.close();
                }
            }
        }
        return listaEmpresas;
    }

    @Override
    public Empresas getEmpresaByRFC(String rfc) throws Exception {
        Empresas empresa = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "from Empresas where rfc = :RFC";
            Query query = session.createQuery(hql);
            query.setParameter("RFC", rfc);
            empresa = (Empresas) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getEmpresaByRFC ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return empresa;
    }

    @Override
    public boolean updateEmpresa(Empresas empresa) throws Exception {
        boolean update = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.saveOrUpdate(empresa);
            tx.commit();
            update = true;
        } catch (HibernateException he) {
            logger.error("updateEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return update;
    }

    @Override
    public Empresas getEmpresaByidEmpresa(int idEmpresa) throws Exception {
        Empresas empresa = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from Empresas where idEmpresa = :IDEMPRESA";
            Query query = session.createQuery(hql);
            query.setParameter("IDEMPRESA", idEmpresa);
            empresa = (Empresas) query.uniqueResult();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getEmpresaByidEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return empresa;
    }

    @Override
    public List<Empresas> getEmpresas() throws Exception {
        List<Empresas> listaEmpresas = new ArrayList<Empresas>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "from Empresas";
            Query query = session.createQuery(hql);
            listaEmpresas = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getEmpresas ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaEmpresas;
    }

    public String obtenerRFC(int idEmpresa){
        String rfc = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try{
            String hql = "SELECT rfc FROM Empresas where idEmpresa="+idEmpresa;
            Query q = session.createQuery(hql);
            rfc = (String) q.uniqueResult();
            tx.commit();
        }catch(HibernateException he){
            logger.error("No se puede obtener el id del socio comercial");
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return rfc;  
    }

    @Override
    public boolean deleteEmpresa(Empresas empresa) throws Exception {
        boolean delete = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.delete(empresa);
            tx.commit();
            delete = true;
        } catch (HibernateException he) {
            logger.error("deleteEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }
    
}
