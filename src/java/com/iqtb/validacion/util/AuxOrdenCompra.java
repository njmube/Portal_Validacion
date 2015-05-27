/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.math.BigDecimal;

/**
 *
 * @author danielromero
 */
public class AuxOrdenCompra {

    private Integer idOrdenCompra;
    private int idEmpresa;
    private Integer idSocioComercial;
    private String numeroOc;
    private BigDecimal total;
    private String tipoMoneda;
    private BigDecimal tipoCambio;
    private String rfcVendedor;
    private String rfcSocioComercial;

    public Integer getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Integer getIdSocioComercial() {
        return idSocioComercial;
    }

    public void setIdSocioComercial(Integer idSocioComercial) {
        this.idSocioComercial = idSocioComercial;
    }

    public String getNumeroOc() {
        return numeroOc;
    }

    public void setNumeroOc(String numeroOc) {
        this.numeroOc = numeroOc;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    public String getRfcVendedor() {
        return rfcVendedor;
    }

    public void setRfcVendedor(String rfcVendedor) {
        this.rfcVendedor = rfcVendedor;
    }

    public String getRfcSocioComercial() {
        return rfcSocioComercial;
    }

    public void setRfcSocioComercial(String rfcSocioComercial) {
        this.rfcSocioComercial = rfcSocioComercial;
    }

    
}
