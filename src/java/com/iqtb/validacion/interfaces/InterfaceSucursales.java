/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;


import com.iqtb.validacion.pojo.Sucursales;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceSucursales {
    
    public List<Sucursales> listaSucursalesByIdEmpresa(Integer idEmpresa) throws Exception;
    public List<Sucursales> listaSucursalesByHql(String hql) throws Exception;
    public Sucursales getSucursalByHql(String hql) throws Exception;
    public List<Integer> listaIdSucursalByNombre(Integer idEmpresa, String nombreSucursal) throws Exception;
    public String nombreSucursalByIdSucursal(Integer idSucursal) throws Exception;
}
