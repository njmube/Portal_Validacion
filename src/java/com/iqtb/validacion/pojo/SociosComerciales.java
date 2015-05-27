package com.iqtb.validacion.pojo;
// Generated 12-mar-2015 19:22:00 by Hibernate Tools 4.3.1

/**
 * SociosComerciales generated by hbm2java
 */
public class SociosComerciales implements java.io.Serializable {

    private Integer idSocioComercial;
    private int idEmpresa;
    private String nombre;
    private String rfc;
    private String calle;
    private String numExterior;
    private String numInterior;
    private String colonia;
    private String localidad;
    private String referencia;
    private String municipio;
    private String estado;
    private String pais;
    private String cp;
    private String codigogln;
    private String email;
    private String telefono;
    private String curp;
    private Integer idClienteXsa;

    public SociosComerciales() {
    }

    public SociosComerciales(Integer idSocioComercial, String rfc) {
        this.idSocioComercial = idSocioComercial;
        this.rfc = rfc;
    }

    public SociosComerciales(int idEmpresa, String nombre, String rfc, String calle) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.rfc = rfc;
        this.calle = calle;
    }

    public SociosComerciales(int idEmpresa, String nombre, String rfc, String calle, String numExterior, String numInterior, String colonia, String localidad, String referencia, String municipio, String estado, String pais, String cp, String codigogln, String email, String telefono, String curp, Integer idClienteXsa) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.rfc = rfc;
        this.calle = calle;
        this.numExterior = numExterior;
        this.numInterior = numInterior;
        this.colonia = colonia;
        this.localidad = localidad;
        this.referencia = referencia;
        this.municipio = municipio;
        this.estado = estado;
        this.pais = pais;
        this.cp = cp;
        this.codigogln = codigogln;
        this.email = email;
        this.telefono = telefono;
        this.curp = curp;
        this.idClienteXsa = idClienteXsa;
    }

    public Integer getIdSocioComercial() {
        return this.idSocioComercial;
    }

    public void setIdSocioComercial(Integer idSocioComercial) {
        this.idSocioComercial = idSocioComercial;
    }

    public int getIdEmpresa() {
        return this.idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return this.rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCalle() {
        return this.calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumExterior() {
        return this.numExterior;
    }

    public void setNumExterior(String numExterior) {
        this.numExterior = numExterior;
    }

    public String getNumInterior() {
        return this.numInterior;
    }

    public void setNumInterior(String numInterior) {
        this.numInterior = numInterior;
    }

    public String getColonia() {
        return this.colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getLocalidad() {
        return this.localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getReferencia() {
        return this.referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getMunicipio() {
        return this.municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return this.pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCp() {
        return this.cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getCodigogln() {
        return this.codigogln;
    }

    public void setCodigogln(String codigogln) {
        this.codigogln = codigogln;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCurp() {
        return this.curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public Integer getIdClienteXsa() {
        return this.idClienteXsa;
    }

    public void setIdClienteXsa(Integer idClienteXsa) {
        this.idClienteXsa = idClienteXsa;
    }

}
