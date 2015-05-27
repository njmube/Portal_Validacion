package com.iqtb.validacion.pojo;
// Generated 12-mar-2015 19:22:00 by Hibernate Tools 4.3.1


import java.math.BigDecimal;
import java.util.Date;

/**
 * OtroMetodoPago generated by hbm2java
 */
public class OtroMetodoPago  implements java.io.Serializable {


     private Integer idOtrosPagos;
     private String metodoPago;
     private Date fecha;
     private String beneficiario;
     private String rfc;
     private BigDecimal monto;
     private String moneda;
     private BigDecimal tipoCambio;
     private int idMetodosPago;

    public OtroMetodoPago() {
    }

	
    public OtroMetodoPago(String metodoPago, Date fecha, String beneficiario, String rfc, BigDecimal monto, int idMetodosPago) {
        this.metodoPago = metodoPago;
        this.fecha = fecha;
        this.beneficiario = beneficiario;
        this.rfc = rfc;
        this.monto = monto;
        this.idMetodosPago = idMetodosPago;
    }
    public OtroMetodoPago(String metodoPago, Date fecha, String beneficiario, String rfc, BigDecimal monto, String moneda, BigDecimal tipoCambio, int idMetodosPago) {
       this.metodoPago = metodoPago;
       this.fecha = fecha;
       this.beneficiario = beneficiario;
       this.rfc = rfc;
       this.monto = monto;
       this.moneda = moneda;
       this.tipoCambio = tipoCambio;
       this.idMetodosPago = idMetodosPago;
    }
   
    public Integer getIdOtrosPagos() {
        return this.idOtrosPagos;
    }
    
    public void setIdOtrosPagos(Integer idOtrosPagos) {
        this.idOtrosPagos = idOtrosPagos;
    }
    public String getMetodoPago() {
        return this.metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    public Date getFecha() {
        return this.fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public String getBeneficiario() {
        return this.beneficiario;
    }
    
    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }
    public String getRfc() {
        return this.rfc;
    }
    
    public void setRfc(String rfc) {
        this.rfc = rfc;
    }
    public BigDecimal getMonto() {
        return this.monto;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    public String getMoneda() {
        return this.moneda;
    }
    
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    public BigDecimal getTipoCambio() {
        return this.tipoCambio;
    }
    
    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    public int getIdMetodosPago() {
        return this.idMetodosPago;
    }
    
    public void setIdMetodosPago(int idMetodosPago) {
        this.idMetodosPago = idMetodosPago;
    }




}


