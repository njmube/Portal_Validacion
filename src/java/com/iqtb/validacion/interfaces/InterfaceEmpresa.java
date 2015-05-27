/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Empresas;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceEmpresa {
    
    public List<Empresas> getEmpresaById(Integer idUsuario) throws Exception;
    public Empresas getEmpresaByRFC(String rfc) throws Exception;
    public boolean updateEmpresa(Empresas empresa) throws Exception;
    public Empresas getEmpresaByidEmpresa(int idEmpresa) throws Exception;
    public List<Empresas> getEmpresas() throws Exception;
    public boolean deleteEmpresa(Empresas empresa) throws Exception;
    
}
