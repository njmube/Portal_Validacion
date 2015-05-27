/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceUsuarioEmpresa;
import com.iqtb.validacion.pojo.UsuariosHasEmpresas;
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
public class DaoUsuarioEmpresa implements InterfaceUsuarioEmpresa {

    private Logger logger = Logger.getLogger(DaoUsuarioEmpresa.class);

    @Override
    public List<UsuariosHasEmpresasId> getEmpresasByIdUsuario(Integer idUsuario) {
        List<UsuariosHasEmpresasId> uei = null;

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List<UsuariosHasEmpresasId> listaUsuariosEmpresas = new ArrayList<UsuariosHasEmpresasId>();
        try {
            String hql = "select id from UsuariosHasEmpresas";
            Query query = session.createQuery(hql);
            uei = query.list();

            for (UsuariosHasEmpresasId usuariosHasEmpresas : uei) {
                if (usuariosHasEmpresas.getIdUsuario() == idUsuario) {
                    listaUsuariosEmpresas.add(usuariosHasEmpresas);
                }
            }
            tx.commit();
        } catch (HibernateException he) {
            logger.error("getEmpresasByIdUsuario ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaUsuariosEmpresas;
    }

    @Override
    public boolean insertUsuarioEmpresa(UsuariosHasEmpresas usuarioEmpresa) throws Exception {
        boolean insert = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.save(usuarioEmpresa);
            tx.commit();
            insert = true;
        } catch (HibernateException he) {
            logger.error("insertUsuarioEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return insert;
    }

    @Override
    public boolean deleteUsuarioEmpresa(UsuariosHasEmpresas usuarioEmpresa) throws Exception {
        boolean delete = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.delete(usuarioEmpresa);
            tx.commit();
            delete = true;
        } catch (HibernateException he) {
            logger.error("deleteUsuarioEmpresa ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public boolean deleteByIdUsuario(int idUsuario) throws Exception {
        boolean delete = false;
        List<UsuariosHasEmpresas> listaUsuariosEmpresas;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            String hql = "from UsuariosHasEmpresas where id.idUsuario = :IDUSUARIO";
            Query query = session.createQuery(hql);
            query.setParameter("IDUSUARIO", idUsuario);
            listaUsuariosEmpresas = query.list();
            for (UsuariosHasEmpresas usuariosHasEmpresas : listaUsuariosEmpresas) {
                session.delete(usuariosHasEmpresas);
            }
            tx.commit();
            delete = true;
        } catch (HibernateException he) {
            logger.error("deleteByIdUsuario ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return delete;
    }

    @Override
    public List<UsuariosHasEmpresas> listaUsuariosHasEmpresas() throws Exception {
        List<UsuariosHasEmpresas> listaUsuariosHasEmpresas = null;
        List<UsuariosHasEmpresasId> lista = null;
        Session session= HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            String hql = "from UsuariosHasEmpresasId";
            Query query = session.createQuery(hql);
            listaUsuariosHasEmpresas = query.list();
            tx.commit();
        } catch (HibernateException he) {
            logger.error("listaUsuariosHasEmpresas ERROR: " + he);
            tx.rollback();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return listaUsuariosHasEmpresas;
    }

}
