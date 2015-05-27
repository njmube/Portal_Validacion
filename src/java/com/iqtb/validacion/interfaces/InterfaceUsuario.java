package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Usuarios;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceUsuario {
    public List<Usuarios> getUsuariosAutenticados() throws Exception;
    public Usuarios getByUserid(String userid) throws Exception;
    public boolean updateUsuario(Usuarios usuario) throws Exception;
    public List<Usuarios> getAllUsuarios() throws Exception;
    public boolean insertUsuario(Usuarios usuario) throws Exception;
    public boolean deleteUsuario(Usuarios usuario) throws Exception;
    public Usuarios getUsuarioByEmail(String email) throws Exception;
    public List<Usuarios> getUsuariosByIdRol(int idRol) throws Exception;
    
    
}
