/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.io.Serializable;

/**
 *
 * @author danielromero
 */
public class ImprimirPlantilla implements Serializable {

    private Integer idPlantillas;
    private int idEmpresa;
    private String nombre;
    private String ruta;
    private String rfcEmpresa;

    public Integer getIdPlantillas() {
        return idPlantillas;
    }

    public void setIdPlantillas(Integer idPlantillas) {
        this.idPlantillas = idPlantillas;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getRfcEmpresa() {
        return rfcEmpresa;
    }

    public void setRfcEmpresa(String rfcEmpresa) {
        this.rfcEmpresa = rfcEmpresa;
    }

}
