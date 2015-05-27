/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.servlet;

import com.iqtb.validacion.dao.DaoUsuario;
import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import java.util.List;
import java.util.TimerTask;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class DaemonSession extends TimerTask implements ServletContextListener{
    private static Logger logger = Logger.getLogger(DaemonSession.class);
    int cont;

    @Override
    public void run() {
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean update = false;
        List<Usuarios> listaUsuarios;
        try {
            listaUsuarios = new DaoUsuario().getUsuariosAutenticados();
            if (!listaUsuarios.isEmpty()) {
                for (Usuarios usuarios : listaUsuarios) {
                    usuarios.setLastAction(getTimestamp());
                    usuarios.setEstado("ACTIVO");
                    update = new DaoUsuario().updateUsuario(usuarios);
                    
                    if (update) {
                        logger.info("DaemonSession Actualizo el estado de los usuarios AUTENTICADOS "+usuarios.getUserid());
                    }else{
                        logger.error("DaemonSession Error al actualizar el estado de los usuarios AUTENTICADOS "+usuarios.getUserid());
                    }
                }
                
            } else {
                logger.info("DaemonSession No existen Usuarios AUTENTICADOS");
            }
        } catch (Exception e) {
            logger.error("DaemonSession Error al obtener los usuarios AUTENTICADOS "+e);
        }
        
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
    
}
