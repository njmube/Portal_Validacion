/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author danielromero
 */
public class AuxUsuario implements Serializable {

    private Integer idUsuario;
    private Integer idSocioComercial;
    private int idRol;
    private String email;
    private String nombre;
    private String apaterno;
    private String amaterno;
    private String passkey;
    private String salt;
    private Date dateExpirationPass;
    private Date lastAction;
    private String estado;
    private String userid;
    private Date fechaAlta;
    private int intentosFallidos;
    private String tipoRol;
    private String nombreoRol;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdSocioComercial() {
        return idSocioComercial;
    }

    public void setIdSocioComercial(Integer idSocioComercial) {
        this.idSocioComercial = idSocioComercial;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApaterno() {
        return apaterno;
    }

    public void setApaterno(String apaterno) {
        this.apaterno = apaterno;
    }

    public String getAmaterno() {
        return amaterno;
    }

    public void setAmaterno(String amaterno) {
        this.amaterno = amaterno;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getDateExpirationPass() {
        return dateExpirationPass;
    }

    public void setDateExpirationPass(Date dateExpirationPass) {
        this.dateExpirationPass = dateExpirationPass;
    }

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public String getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(String tipoRol) {
        this.tipoRol = tipoRol;
    }

    public String getNombreoRol() {
        return nombreoRol;
    }

    public void setNombreoRol(String nombreoRol) {
        this.nombreoRol = nombreoRol;
    }

}
