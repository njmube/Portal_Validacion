/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.dao;

import com.iqtb.validacion.dto.HibernateUtil;
import com.iqtb.validacion.interfaces.InterfaceBitacora;
import com.iqtb.validacion.pojo.Bitacora;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author danielromero
 */
public class DaoBitacora implements InterfaceBitacora{
private Logger logger = Logger.getLogger(DaoBitacora.class);
    @Override
    public boolean registarBitacora(Bitacora bitacora) throws Exception {
        boolean registro = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        
        try {
            session.save(bitacora);
            tx.commit();
            registro = true;
        } catch (HibernateException he) {
            logger.error("registarBitacora ERROR: "+he);
            tx.rollback();
        }finally{
            if (session.isOpen()) {
                session.close();
            }
        }
        return registro;
    }
    
}
