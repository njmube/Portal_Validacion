/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Roles;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceRoles {
    public Roles getRolByNombre(String nombre) throws Exception;
    public Roles getRolById(int idRol) throws Exception;
    public List<Roles> getAllRoles() throws Exception;
    public boolean updateRol(Roles rol) throws Exception;
    public boolean deleteRol(Roles rol) throws Exception;
    public boolean insertRol(Roles rol) throws Exception;
    public String getTipoRolbyIdRol(Integer idRol) throws Exception;
    public List<Integer> listaIdRolbyTipoRol(String tipoRol) throws Exception;
    public String getNombreRolbyIdRol(Integer idRol) throws Exception;
    
}
