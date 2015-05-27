/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Script;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfaceScript {

    public Script getScriptById(Integer idScript) throws Exception;

    public List<Script> listaScriptByHql(String hql) throws Exception;
    public boolean insertScript(Script script) throws Exception;
    public boolean updateScript(Script script) throws Exception;
    public boolean deleteScript(Script script) throws Exception;

}
