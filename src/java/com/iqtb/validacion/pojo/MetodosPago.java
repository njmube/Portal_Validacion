package com.iqtb.validacion.pojo;
// Generated 12-mar-2015 19:22:00 by Hibernate Tools 4.3.1



/**
 * MetodosPago generated by hbm2java
 */
public class MetodosPago  implements java.io.Serializable {


     private Integer idMetodosPago;
     private int idCfdiRecibido;
     private String tipo;

    public MetodosPago() {
    }

    public MetodosPago(int idCfdiRecibido, String tipo) {
       this.idCfdiRecibido = idCfdiRecibido;
       this.tipo = tipo;
    }
   
    public Integer getIdMetodosPago() {
        return this.idMetodosPago;
    }
    
    public void setIdMetodosPago(Integer idMetodosPago) {
        this.idMetodosPago = idMetodosPago;
    }
    public int getIdCfdiRecibido() {
        return this.idCfdiRecibido;
    }
    
    public void setIdCfdiRecibido(int idCfdiRecibido) {
        this.idCfdiRecibido = idCfdiRecibido;
    }
    public String getTipo() {
        return this.tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }




}

