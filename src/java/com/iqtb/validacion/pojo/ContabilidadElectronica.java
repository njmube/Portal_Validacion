package com.iqtb.validacion.pojo;
// Generated 12-mar-2015 19:22:00 by Hibernate Tools 4.3.1

import java.util.Date;

/**
 * ContabilidadElectronica generated by hbm2java
 */
public class ContabilidadElectronica implements java.io.Serializable {

    private Integer idContabilidad;
    private int idEmpresa;
    private String estado;
    private String tipoArchivo;
    private String nombreRecibido;
    private String nombreGenerado;
    private String archivoRecibido;
    private String xmlGenerado;
    private Date fechaRecepcion;
    private String error;
    private String mes;
    private Integer anio;

    public ContabilidadElectronica() {
    }

    public ContabilidadElectronica(Integer idContabilidad, String estado, String tipoArchivo, String nombreRecibido, String nombreGenerado, Date fechaRecepcion, String mes, Integer anio) {
        this.idContabilidad = idContabilidad;
        this.estado = estado;
        this.tipoArchivo = tipoArchivo;
        this.nombreRecibido = nombreRecibido;
        this.nombreGenerado = nombreGenerado;
        this.fechaRecepcion = fechaRecepcion;
        this.mes = mes;
        this.anio = anio;
    }

    public ContabilidadElectronica(Integer idContabilidad, String estado, String tipoArchivo, String nombreRecibido, String nombreGenerado, Date fechaRecepcion, String error, String mes, Integer anio) {
        this.idContabilidad = idContabilidad;
        this.estado = estado;
        this.tipoArchivo = tipoArchivo;
        this.nombreRecibido = nombreRecibido;
        this.nombreGenerado = nombreGenerado;
        this.fechaRecepcion = fechaRecepcion;
        this.error = error;
        this.mes = mes;
        this.anio = anio;
    }

    public ContabilidadElectronica(int idEmpresa, String estado, String tipoArchivo, String nombreRecibido, String archivoRecibido, Date fechaRecepcion) {
        this.idEmpresa = idEmpresa;
        this.estado = estado;
        this.tipoArchivo = tipoArchivo;
        this.nombreRecibido = nombreRecibido;
        this.archivoRecibido = archivoRecibido;
        this.fechaRecepcion = fechaRecepcion;
    }

    public ContabilidadElectronica(int idEmpresa, String estado, String tipoArchivo, String nombreRecibido, String nombreGenerado, String archivoRecibido, String xmlGenerado, Date fechaRecepcion, String error, String mes, Integer anio) {
        this.idEmpresa = idEmpresa;
        this.estado = estado;
        this.tipoArchivo = tipoArchivo;
        this.nombreRecibido = nombreRecibido;
        this.nombreGenerado = nombreGenerado;
        this.archivoRecibido = archivoRecibido;
        this.xmlGenerado = xmlGenerado;
        this.fechaRecepcion = fechaRecepcion;
        this.error = error;
        this.mes = mes;
        this.anio = anio;
    }

    public Integer getIdContabilidad() {
        return this.idContabilidad;
    }

    public void setIdContabilidad(Integer idContabilidad) {
        this.idContabilidad = idContabilidad;
    }

    public int getIdEmpresa() {
        return this.idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoArchivo() {
        return this.tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public String getNombreRecibido() {
        return this.nombreRecibido;
    }

    public void setNombreRecibido(String nombreRecibido) {
        this.nombreRecibido = nombreRecibido;
    }

    public String getNombreGenerado() {
        return this.nombreGenerado;
    }

    public void setNombreGenerado(String nombreGenerado) {
        this.nombreGenerado = nombreGenerado;
    }

    public String getArchivoRecibido() {
        return this.archivoRecibido;
    }

    public void setArchivoRecibido(String archivoRecibido) {
        this.archivoRecibido = archivoRecibido;
    }

    public String getXmlGenerado() {
        return this.xmlGenerado;
    }

    public void setXmlGenerado(String xmlGenerado) {
        this.xmlGenerado = xmlGenerado;
    }

    public Date getFechaRecepcion() {
        return this.fechaRecepcion;
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMes() {
        return this.mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return this.anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

}
