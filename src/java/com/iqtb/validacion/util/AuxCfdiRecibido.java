/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author danielromero
 */
public class AuxCfdiRecibido implements Serializable {

    private Integer idCfdiRecibido;
    private int idSocioComercial;
    private int idEmpresa;
    private Integer idOrdenCompra;
    private String serie;
    private Long folio;
    private String uuid;
    private Date fecha;
    private BigDecimal total;
    private String xmlSat;
    private Date fechaRecepcion;
    private String estado;
    private String error;
    private Boolean reportado;
    private String estadoNotificacion;
    private String nombreArchivo;
    private byte[] pdf;
    private Boolean statusPdf;
    private Boolean esCfd;
    private Boolean reportadoXsa;
    private BigDecimal totalOc;
    private String tipoMoneda;
    private BigDecimal tipoCambio;
    private String entradaAlmacen;
    private String controlCalidad;
    private String autorizacion;
    private String tiposWarn;
    private String rfcSocioComercial;
    private String nombreSocioComercial;
    private String numeroOrdenCompra;

    public Integer getIdCfdiRecibido() {
        return idCfdiRecibido;
    }

    public void setIdCfdiRecibido(Integer idCfdiRecibido) {
        this.idCfdiRecibido = idCfdiRecibido;
    }

    public int getIdSocioComercial() {
        return idSocioComercial;
    }

    public void setIdSocioComercial(int idSocioComercial) {
        this.idSocioComercial = idSocioComercial;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Integer getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Long getFolio() {
        return folio;
    }

    public void setFolio(Long folio) {
        this.folio = folio;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getXmlSat() {
        return xmlSat;
    }

    public void setXmlSat(String xmlSat) {
        this.xmlSat = xmlSat;
    }

    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean isReportado() {
        return reportado;
    }

    public void setReportado(Boolean reportado) {
        this.reportado = reportado;
    }

    public String getEstadoNotificacion() {
        return estadoNotificacion;
    }

    public void setEstadoNotificacion(String estadoNotificacion) {
        this.estadoNotificacion = estadoNotificacion;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public Boolean isStatusPdf() {
        return statusPdf;
    }

    public void setStatusPdf(Boolean statusPdf) {
        this.statusPdf = statusPdf;
    }

    public Boolean isEsCfd() {
        return esCfd;
    }

    public void setEsCfd(Boolean esCfd) {
        this.esCfd = esCfd;
    }

    public Boolean isReportadoXsa() {
        return reportadoXsa;
    }

    public void setReportadoXsa(Boolean reportadoXsa) {
        this.reportadoXsa = reportadoXsa;
    }

    public BigDecimal getTotalOc() {
        return totalOc;
    }

    public void setTotalOc(BigDecimal totalOc) {
        this.totalOc = totalOc;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getEntradaAlmacen() {
        return entradaAlmacen;
    }

    public void setEntradaAlmacen(String entradaAlmacen) {
        this.entradaAlmacen = entradaAlmacen;
    }

    public String getControlCalidad() {
        return controlCalidad;
    }

    public void setControlCalidad(String controlCalidad) {
        this.controlCalidad = controlCalidad;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getTiposWarn() {
        return tiposWarn;
    }

    public void setTiposWarn(String tiposWarn) {
        this.tiposWarn = tiposWarn;
    }

    public String getRfcSocioComercial() {
        return rfcSocioComercial;
    }

    public void setRfcSocioComercial(String rfcSocioComercial) {
        this.rfcSocioComercial = rfcSocioComercial;
    }

    public String getNombreSocioComercial() {
        return nombreSocioComercial;
    }

    public void setNombreSocioComercial(String nombreSocioComercial) {
        this.nombreSocioComercial = nombreSocioComercial;
    }

    public String getNumeroOrdenCompra() {
        return numeroOrdenCompra;
    }

    public void setNumeroOrdenCompra(String numeroOrdenCompra) {
        this.numeroOrdenCompra = numeroOrdenCompra;
    }

}
