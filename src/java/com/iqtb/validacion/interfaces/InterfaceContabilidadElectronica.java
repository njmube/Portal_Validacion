/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.ContabilidadElectronica;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceContabilidadElectronica {
    public List<ContabilidadElectronica> listaContabilidadElectronica(Integer idEmpresa) throws Exception;
    public ContabilidadElectronica getContabilidadElectronica(Integer idContabilidad) throws Exception;
    public boolean insertContabilidadElectonica(ContabilidadElectronica contabilidadElectonica) throws Exception;
    public boolean updateContabilidadElectonica(ContabilidadElectronica contabilidadElectonica) throws Exception;
    public boolean deleteContabilidadElectronica(ContabilidadElectronica contabilidadElectonica) throws Exception;
    public ContabilidadElectronica getContElectronicaByHql(String hql) throws Exception;
}
