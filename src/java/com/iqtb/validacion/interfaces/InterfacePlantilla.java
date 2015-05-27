/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iqtb.validacion.interfaces;

import com.iqtb.validacion.pojo.Plantillas;
import java.util.List;

/**
 *
 * @author danielromero
 */
public interface InterfacePlantilla {
    public boolean insertPlantilla(Plantillas plantilla) throws Exception;
    public boolean updatePlantilla(Plantillas plantilla) throws Exception;
    public boolean deletePlantilla(Plantillas plantilla) throws Exception;
    public Plantillas getPlantillaById(int idPlantilla) throws Exception;
    public Plantillas getPlantillaByNombre(String nombre) throws Exception;
    public List<Plantillas> listaPlantillas() throws Exception;
    public List<Plantillas> listaPlantillasByIdEmpresa(int idEmpresa) throws Exception;
    
}
