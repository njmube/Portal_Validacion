/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.SociosComerciales;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author danielromero
 */
public interface InterfaceSociosComerciales {
    public SociosComerciales getSocioComercialByID(Integer idSocioComercial) throws Exception;
    public List<Integer> filtroSocioComercialByRFC(Integer idEmpresa, String rfc) throws Exception;
    public List<SociosComerciales> getSociosComercialesByIdEmpresa(Integer idEmpresa) throws Exception;
    public boolean insertSocioComercial(SociosComerciales socioComercial) throws Exception;
    public boolean updateSocioComercial(SociosComerciales socioComercial) throws Exception;
    public boolean deleteSocioComercial(SociosComerciales socioComercial) throws Exception;
    public SociosComerciales getSocioComercialByIdEmpresaRFC(Integer idEmpresa, String rfc) throws Exception;
    public Integer IntegerSociosByHql(String hql) throws Exception;
    public List<SociosComerciales> listaSociosCriteria(Integer idEmpresa, Integer inicio, Integer fin, String sortField, SortOrder sortOrder, Map<String, String> filters) throws Exception;
    public List<SociosComerciales> listaSociosHQL(String hql) throws Exception;
    public String getRFCSociobyIdSocio(Integer idSocioComercial) throws Exception;
    public SociosComerciales getSocioByHql(String hql) throws Exception;
    public String getNombreSociobyIdSocio(Integer idSocioComercial) throws Exception;
    public List<Integer> filtroSocioComercialByNombre(Integer idEmpresa, String nombre) throws Exception;
}
