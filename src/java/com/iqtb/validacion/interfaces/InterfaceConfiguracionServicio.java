/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.ConfiguracionesServicios;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceConfiguracionServicio {
    public ConfiguracionesServicios getConfigServicioByIdServicioPropiedad(int idServicio, String propiedad) throws Exception;
    public boolean updateConfigServicios(ConfiguracionesServicios configServicios) throws Exception;
    public List<ConfiguracionesServicios> listaServiciosByIdServicio(Integer idServicio) throws Exception;
    
}
