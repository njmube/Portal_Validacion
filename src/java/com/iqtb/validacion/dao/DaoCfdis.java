/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceCfdis;
import com.iqtb.validacion.pojo.Cfdis;
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
public class DaoCfdis implements InterfaceCfdis{
    private Logger logger = Logger.getLogger(DaoCfdis.class);

    @Override
    public Cfdis getCfdiById(Integer idCfdi) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Cfdis> listaCfdisByHql(String hql) throws Exception {
        List<Cfdis> listaCFDIs = null;
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
    
}
