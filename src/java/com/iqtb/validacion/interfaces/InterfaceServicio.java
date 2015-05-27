/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Servicios;

/**
 *
 * @author danielromero
 */
public interface InterfaceServicio {
    public Servicios getServicoByNombre(String nombre) throws Exception;
    public boolean updateServicio(Servicios servicio) throws Exception;
    
}
