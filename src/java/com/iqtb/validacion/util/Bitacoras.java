/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import com.iqtb.validacion.dao.DaoBitacora;
import com.iqtb.validacion.pojo.Bitacora;
import static com.iqtb.validacion.util.DateTime.getTimestamp;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class Bitacoras {
    private static Logger logger = Logger.getLogger(Bitacoras.class);

    public static void registrarBitacora(Integer idUsuario, Integer idServicio, Integer idEmpresa, String descripcion, String tipo) {
        boolean registro = false;
        Bitacora bitacora = new Bitacora();
        DaoBitacora daoBitacora = new DaoBitacora();

        try {
            bitacora.setIdUsuario(idUsuario);
            bitacora.setIdServicio(idServicio);
            bitacora.setIdEmpresa(idEmpresa);
            bitacora.setDescripcion(descripcion);
            bitacora.setFecha(getTimestamp());
            bitacora.setTipo(tipo);
            registro = daoBitacora.registarBitacora(bitacora);

            if (registro) {
                logger.debug("BITACORA_REG Registro insertado en Bitacora");
            } else {
                logger.error("BITACORA_REG Error al registrar en la Bitacora");
            }
        } catch (Exception e) {
            logger.error("BITACORA_REG Error al registrar en la Bitacora ERROR: " + e);
        }
    }

}
