/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.CfdisRecibidos;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceCfdisRecibidos {
    
    public List<CfdisRecibidos> getCfdisByidEmpresa(int idEmpresa) throws Exception;
    public CfdisRecibidos getCfdiByID(int idCfdi) throws Exception;
    public boolean delete(CfdisRecibidos cfdi) throws Exception;
    public List<CfdisRecibidos> listaCfdisByIdEmpresaEstado(int idEmpresa, String estado) throws Exception;
    public List<CfdisRecibidos> listaCfdisErrorByIdEmpresa(int idEmpresa) throws Exception;
    public List<CfdisRecibidos> listaCfdisValidosByIdEmpresa(int idEmpresa) throws Exception;
    public Boolean actualizarCfdi(CfdisRecibidos cfdi) throws Exception;
    public List<String> listaEstadosCFDIsByIdEmpresa(Integer idEmpresa) throws Exception;
    public List<CfdisRecibidos> getCfdisByIdEmpresaIdSocioComercial(Integer idEmpresa, Integer idSocio) throws Exception;
    public List<String> listaEstadosCFDIsByIdEmpresaIdSocio(Integer idEmpresa, Integer idSocio) throws Exception;
    public List<CfdisRecibidos> listaCfdisByHql(String hql) throws Exception;
    public Integer IntegerCfdisByHql(String hql) throws Exception;
    public List<Integer> listaIdsCfdisRByHQL(String hql) throws Exception;
   
}
