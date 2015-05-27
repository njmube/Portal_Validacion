/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Cfdis;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceCfdis {
    public Cfdis getCfdiById(Integer idCfdi) throws Exception;
    public List<Cfdis> listaCfdisByHql(String hql) throws Exception;
}
