/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.servlet;

import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class SomeHttpSessionListener implements HttpSessionListener {

    private Logger logger = Logger.getLogger(SomeHttpSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
//        System.err.println("Sesion Creada: " + se.getSession().getId());
        logger.info("[SomeHttpSessionListener - sessionCreated] - Sesion Creada: " + se.getSession().getId());
        //        ... Cualquier otra cosa ...
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        DaoUsuario daoUsuario;
        Usuarios usuario = (Usuarios) se.getSession().getAttribute("usuario");

        if (usuario != null) {
            usuario.setLastAction(getTimestamp());
            usuario.setEstado("ACTIVO");

            try {
                daoUsuario = new DaoUsuario();
                if (daoUsuario.updateUsuario(usuario)) {
                    logger.info("[SomeHttpSessionListener - sessionDestroyed]Usuario " + usuario.getUserid() + " estado actualizado ACTIVO");
                } else {
                    logger.info("[SomeHttpSessionListener - sessionDestroyed]Error al actualizar el estado del Usuario " + usuario.getUserid());
                }
            } catch (Exception e) {
                logger.error("[SomeHttpSessionListener - sessionDestroyed] - ERROR " + e.getMessage());
            }
        } else {
            logger.warn("Usuario es null");
        }
//        System.err.println("Sesion Destruida: " + se.getSession().getId());
        logger.info("[SomeHttpSessionListener - sessionDestroyed] - Sesion Destruida: " + se.getSession().getId());
//        ... Cualquier otra cosa ...
    }

}
