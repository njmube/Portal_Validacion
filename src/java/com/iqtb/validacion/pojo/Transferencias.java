package com.iqtb.validacion.pojo;
// Generated 12-mar-2015 19:22:00 by Hibernate Tools 4.3.1


import java.math.BigDecimal;
import java.util.Date;

/**
 * Transferencias generated by hbm2java
 */
public class Transferencias  implements java.io.Serializable {


     private Integer idTransferencia;
     private int idMetodosPago;
     private String cuentaOrigen;
     private String bancoOrigenNal;
     private String bancoOrigenExt;
     private String cuentaDestino;
     private String bancoDestinoNal;
     private String bancoDestinoExt;
     private Date fecha;
     private String beneficiario;
     private String rfc;
     private BigDecimal monto;
     private String moneda;
     private BigDecimal tipoCambio;

    public Transferencias() {
    }

	
    public Transferencias(int idMetodosPago, String bancoOrigenNal, String cuentaDestino, String bancoDestinoNal, Date fecha, String beneficiario, String rfc, BigDecimal monto) {
        this.idMetodosPago = idMetodosPago;
        this.bancoOrigenNal = bancoOrigenNal;
        this.cuentaDestino = cuentaDestino;
        this.bancoDestinoNal = bancoDestinoNal;
        this.fecha = fecha;
        this.beneficiario = beneficiario;
        this.rfc = rfc;
        this.monto = monto;
    }
    public Transferencias(int idMetodosPago, String cuentaOrigen, String bancoOrigenNal, String bancoOrigenExt, String cuentaDestino, String bancoDestinoNal, String bancoDestinoExt, Date fecha, String beneficiario, String rfc, BigDecimal monto, String moneda, BigDecimal tipoCambio) {
       this.idMetodosPago = idMetodosPago;
       this.cuentaOrigen = cuentaOrigen;
       this.bancoOrigenNal = bancoOrigenNal;
       this.bancoOrigenExt = bancoOrigenExt;
       this.cuentaDestino = cuentaDestino;
       this.bancoDestinoNal = bancoDestinoNal;
       this.bancoDestinoExt = bancoDestinoExt;
       this.fecha = fecha;
       this.beneficiario = beneficiario;
       this.rfc = rfc;
       this.monto = monto;
       this.moneda = moneda;
       this.tipoCambio = tipoCambio;
    }
   
    public Integer getIdTransferencia() {
        return this.idTransferencia;
    }
    
    public void setIdTransferencia(Integer idTransferencia) {
        this.idTransferencia = idTransferencia;
    }
    public int getIdMetodosPago() {
        return this.idMetodosPago;
    }
    
    public void setIdMetodosPago(int idMetodosPago) {
        this.idMetodosPago = idMetodosPago;
    }
    public String getCuentaOrigen() {
        return this.cuentaOrigen;
    }
    
    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }
    public String getBancoOrigenNal() {
        return this.bancoOrigenNal;
    }
    
    public void setBancoOrigenNal(String bancoOrigenNal) {
        this.bancoOrigenNal = bancoOrigenNal;
    }
    public String getBancoOrigenExt() {
        return this.bancoOrigenExt;
    }
    
    public void setBancoOrigenExt(String bancoOrigenExt) {
        this.bancoOrigenExt = bancoOrigenExt;
    }
    public String getCuentaDestino() {
        return this.cuentaDestino;
    }
    
    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }
    public String getBancoDestinoNal() {
        return this.bancoDestinoNal;
    }
    
    public void setBancoDestinoNal(String bancoDestinoNal) {
        this.bancoDestinoNal = bancoDestinoNal;
    }
    public String getBancoDestinoExt() {
        return this.bancoDestinoExt;
    }
    
    public void setBancoDestinoExt(String bancoDestinoExt) {
        this.bancoDestinoExt = bancoDestinoExt;
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

