/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.UsuariosHasEmpresas;
import com.iqtb.validacion.pojo.UsuariosHasEmpresasId;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceUsuarioEmpresa {
    public List<UsuariosHasEmpresasId> getEmpresasByIdUsuario(Integer idUsuario) throws Exception;
    public boolean insertUsuarioEmpresa(UsuariosHasEmpresas usuarioEmpresa) throws Exception;
    public boolean deleteUsuarioEmpresa(UsuariosHasEmpresas usuarioEmpresa) throws Exception;
    public boolean deleteByIdUsuario(int idUsuario) throws Exception;
    public List<UsuariosHasEmpresas> listaUsuariosHasEmpresas() throws Exception;
    
}
