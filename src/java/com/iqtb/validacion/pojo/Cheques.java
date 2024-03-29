package com.iqtb.validacion.pojo;
// Generated 12-mar-2015 19:22:00 by Hibernate Tools 4.3.1


import java.math.BigDecimal;
import java.util.Date;

/**
 * Cheques generated by hbm2java
 */
public class Cheques  implements java.io.Serializable {


     private Integer idCheque;
     private int idMetodosPago;
     private String numero;
     private String bancoEmisorNal;
     private String bancoEmisorExt;
     private String cuentaOrigen;
     private Date fecha;
     private String beneficiario;
     private String rfc;
     private BigDecimal monto;
     private String moneda;
     private BigDecimal tipoCambio;

    public Cheques() {
    }

	
    public Cheques(int idMetodosPago, String bancoEmisorNal, String cuentaOrigen, Date fecha, String beneficiario, String rfc, BigDecimal monto) {
        this.idMetodosPago = idMetodosPago;
        this.bancoEmisorNal = bancoEmisorNal;
        this.cuentaOrigen = cuentaOrigen;
        this.fecha = fecha;
        this.beneficiario = beneficiario;
        this.rfc = rfc;
        this.monto = monto;
    }
    public Cheques(int idMetodosPago, String numero, String bancoEmisorNal, String bancoEmisorExt, String cuentaOrigen, Date fecha, String beneficiario, String rfc, BigDecimal monto, String moneda, BigDecimal tipoCambio) {
       this.idMetodosPago = idMetodosPago;
       this.numero = numero;
       this.bancoEmisorNal = bancoEmisorNal;
       this.bancoEmisorExt = bancoEmisorExt;
       this.cuentaOrigen = cuentaOrigen;
       this.fecha = fecha;
       this.beneficiario = beneficiario;
       this.rfc = rfc;
       this.monto = monto;
       this.moneda = moneda;
       this.tipoCambio = tipoCambio;
    }
   
    public Integer getIdCheque() {
        return this.idCheque;
    }
    
    public void setIdCheque(Integer idCheque) {
        this.idCheque = idCheque;
    }
    public int getIdMetodosPago() {
        return this.idMetodosPago;
    }
    
    public void setIdMetodosPago(int idMetodosPago) {
        this.idMetodosPago = idMetodosPago;
    }
    public String getNumero() {
        return this.numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public String getBancoEmisorNal() {
        return this.bancoEmisorNal;
    }
    
    public void setBancoEmisorNal(String bancoEmisorNal) {
        this.bancoEmisorNal = bancoEmisorNal;
    }
    public String getBancoEmisorExt() {
        return this.bancoEmisorExt;
    }
    
    public void setBancoEmisorExt(String bancoEmisorExt) {
        this.bancoEmisorExt = bancoEmisorExt;
    }
    public String getCuentaOrigen() {
        return this.cuentaOrigen;
    }
    
    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
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




}


