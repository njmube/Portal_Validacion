/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Opciones;
import com.iqtb.validacion.pojo.RolesHasOpciones;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceRolesOpciones {
    public List<Opciones> getOpcionesByIdRol(int idRol) throws Exception;
    public List<Opciones> getOpciones() throws Exception;
    public List<Opciones> getOpcionesByTipo(String tipo) throws Exception;
    public boolean deleteByIRol(int idRol) throws Exception;
    public boolean insertRolesOpciones(RolesHasOpciones rolesOpciones) throws Exception;
}
