/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.OrdenesCompra;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceOrdenCompra {
    public OrdenesCompra getOrdenById(Integer idOrdenCompra) throws Exception;
    public List<OrdenesCompra> listaOrdenByHql(String hql) throws Exception;
    public boolean insertOrdenCompra(OrdenesCompra ordenCompra) throws Exception;
    public boolean updateOrdenCompra(OrdenesCompra ordenCompra) throws Exception;
    public boolean deleteOrdenCompra(OrdenesCompra ordenCompra) throws Exception;
    public OrdenesCompra obtenerOrden(String ordenDeCompra, Integer idEmpresa, Integer idSocioComercial) throws Exception;
    public String numOrdenCompraById(Integer idOrenCompra) throws Exception;
    
    
}
