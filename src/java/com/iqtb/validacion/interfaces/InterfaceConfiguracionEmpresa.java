/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.ConfiguracionesEmpresas;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceConfiguracionEmpresa {
    public ConfiguracionesEmpresas getConfiguracionEmpresa(int idEmpresa, String propiedad) throws Exception;
    public boolean updateConfiguracionEmpresa(ConfiguracionesEmpresas configEmpresa) throws Exception;
    public boolean insertConfiguracionEmpresa(ConfiguracionesEmpresas configEmpresa) throws Exception;
    public ConfiguracionesEmpresas existConfiguracionEmpresa(String propiedad, String valor) throws Exception;
    public List<ConfiguracionesEmpresas> getConfiguracionEmpresaByPropiedadValor(String propiedad, String valor) throws Exception;
    
}
