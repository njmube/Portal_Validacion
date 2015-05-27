/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.util;

import java.net.URL;

/**
 *
 * @author danielromero
 */
public class ActualizaWS {
    private URL url;

    public ActualizaWS(URL url) {
        this.url = url;
    }

    public Boolean verificarConexion(java.lang.String keyServidor) {
        com.iqtb.validacion.ws.ActualizacionCfds_Service service = new com.iqtb.validacion.ws.ActualizacionCfds_Service(url);
        com.iqtb.validacion.ws.ActualizacionCfds port = service.getActualizacionCfdsPort();
        return port.verificarConexion(keyServidor);
    }
}
