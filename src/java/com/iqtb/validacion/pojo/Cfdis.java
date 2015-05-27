package com.iqtb.validacion.pojo;
// Generated 12-mar-2015 19:22:00 by Hibernate Tools 4.3.1


import java.math.BigDecimal;
import java.util.Date;

/**
 * Cfdis generated by hbm2java
 */
public class Cfdis  implements java.io.Serializable {


     private Integer idCfdis;
     private int idSucursal;
     private int idSocioComercial;
     private Integer idTipocfd;
     private String serie;
     private Long folio;
     private String uuid;
     private Date fecha;
     private Long numeroAprobacion;
     private Integer anioAprobacion;
     private BigDecimal subtotal;
     private BigDecimal descuento;
     private BigDecimal total;
     private String tipoMoneda;
     private BigDecimal tipoCambio;
     private BigDecimal iva;
     private String estadoFiscal;
     private String estado;
     private Date fechaCancelacion;
     private Date fechaModificacion;
     private Boolean tienePdf;
     private String estadoImpresion;
     private Integer idArchivoSincoronizacion;
     private Integer imprimirEn;
     private Integer idDireccion;
     private Integer idPlantilla;
     private String tipoCfd;
     private String pedimento;
     private String fechaPedimento;
     private String aduana;
     private Date generationDate;
     private String ticket;
     private Integer idConfiguracion;
     private String cadenaOriginal;
     private String estadoContable;
     private Date fechaVencimiento;
     private String rfcEmisor;
     private String rfcReceptor;
     private Boolean reportado;

    public Cfdis() {
    }

	
    public Cfdis(int idSucursal, int idSocioComercial, Date fecha, Date generationDate) {
        this.idSucursal = idSucursal;
        this.idSocioComercial = idSocioComercial;
        this.fecha = fecha;
        this.generationDate = generationDate;
    }
    public Cfdis(int idSucursal, int idSocioComercial, Integer idTipocfd, String serie, Long folio, String uuid, Date fecha, Long numeroAprobacion, Integer anioAprobacion, BigDecimal subtotal, BigDecimal descuento, BigDecimal total, String tipoMoneda, BigDecimal tipoCambio, BigDecimal iva, String estadoFiscal, String estado, Date fechaCancelacion, Date fechaModificacion, Boolean tienePdf, String estadoImpresion, Integer idArchivoSincoronizacion, Integer imprimirEn, Integer idDireccion, Integer idPlantilla, String tipoCfd, String pedimento, String fechaPedimento, String aduana, Date generationDate, String ticket, Integer idConfiguracion, String cadenaOriginal, String estadoContable, Date fechaVencimiento, String rfcEmisor, String rfcReceptor, Boolean reportado) {
       this.idSucursal = idSucursal;
       this.idSocioComercial = idSocioComercial;
       this.idTipocfd = idTipocfd;
       this.serie = serie;
       this.folio = folio;
       this.uuid = uuid;
       this.fecha = fecha;
       this.numeroAprobacion = numeroAprobacion;
       this.anioAprobacion = anioAprobacion;
       this.subtotal = subtotal;
       this.descuento = descuento;
       this.total = total;
       this.tipoMoneda = tipoMoneda;
       this.tipoCambio = tipoCambio;
       this.iva = iva;
       this.estadoFiscal = estadoFiscal;
       this.estado = estado;
       this.fechaCancelacion = fechaCancelacion;
       this.fechaModificacion = fechaModificacion;
       this.tienePdf = tienePdf;
       this.estadoImpresion = estadoImpresion;
       this.idArchivoSincoronizacion = idArchivoSincoronizacion;
       this.imprimirEn = imprimirEn;
       this.idDireccion = idDireccion;
       this.idPlantilla = idPlantilla;
       this.tipoCfd = tipoCfd;
       this.pedimento = pedimento;
       this.fechaPedimento = fechaPedimento;
       this.aduana = aduana;
       this.generationDate = generationDate;
       this.ticket = ticket;
       this.idConfiguracion = idConfiguracion;
       this.cadenaOriginal = cadenaOriginal;
       this.estadoContable = estadoContable;
       this.fechaVencimiento = fechaVencimiento;
       this.rfcEmisor = rfcEmisor;
       this.rfcReceptor = rfcReceptor;
       this.reportado = reportado;
    }
   
    public Integer getIdCfdis() {
        return this.idCfdis;
    }
    
    public void setIdCfdis(Integer idCfdis) {
        this.idCfdis = idCfdis;
    }
    public int getIdSucursal() {
        return this.idSucursal;
    }
    
    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }
    public int getIdSocioComercial() {
        return this.idSocioComercial;
    }
    
    public void setIdSocioComercial(int idSocioComercial) {
        this.idSocioComercial = idSocioComercial;
    }
    public Integer getIdTipocfd() {
        return this.idTipocfd;
    }
    
    public void setIdTipocfd(Integer idTipocfd) {
        this.idTipocfd = idTipocfd;
    }
    public String getSerie() {
        return this.serie;
    }
    
    public void setSerie(String serie) {
        this.serie = serie;
    }
    public Long getFolio() {
        return this.folio;
    }
    
    public void setFolio(Long folio) {
        this.folio = folio;
    }
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public Date getFecha() {
        return this.fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public Long getNumeroAprobacion() {
        return this.numeroAprobacion;
    }
    
    public void setNumeroAprobacion(Long numeroAprobacion) {
        this.numeroAprobacion = numeroAprobacion;
    }
    public Integer getAnioAprobacion() {
        return this.anioAprobacion;
    }
    
    public void setAnioAprobacion(Integer anioAprobacion) {
        this.anioAprobacion = anioAprobacion;
    }
    public BigDecimal getSubtotal() {
        return this.subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    public BigDecimal getDescuento() {
        return this.descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    public BigDecimal getTotal() {
        return this.total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public String getTipoMoneda() {
        return this.tipoMoneda;
    }
    
    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }
    public BigDecimal getTipoCambio() {
        return this.tipoCambio;
    }
    
    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    public BigDecimal getIva() {
        return this.iva;
    }
    
    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }
    public String getEstadoFiscal() {
        return this.estadoFiscal;
    }
    
    public void setEstadoFiscal(String estadoFiscal) {
        this.estadoFiscal = estadoFiscal;
    }
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public Date getFechaCancelacion() {
        return this.fechaCancelacion;
    }
    
    public void setFechaCancelacion(Date fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }
    public Date getFechaModificacion() {
        return this.fechaModificacion;
    }
    
    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
    public Boolean getTienePdf() {
        return this.tienePdf;
    }
    
    public void setTienePdf(Boolean tienePdf) {
        this.tienePdf = tienePdf;
    }
    public String getEstadoImpresion() {
        return this.estadoImpresion;
    }
    
    public void setEstadoImpresion(String estadoImpresion) {
        this.estadoImpresion = estadoImpresion;
    }
    public Integer getIdArchivoSincoronizacion() {
        return this.idArchivoSincoronizacion;
    }
    
    public void setIdArchivoSincoronizacion(Integer idArchivoSincoronizacion) {
        this.idArchivoSincoronizacion = idArchivoSincoronizacion;
    }
    public Integer getImprimirEn() {
        return this.imprimirEn;
    }
    
    public void setImprimirEn(Integer imprimirEn) {
        this.imprimirEn = imprimirEn;
    }
    public Integer getIdDireccion() {
        return this.idDireccion;
    }
    
    public void setIdDireccion(Integer idDireccion) {
        this.idDireccion = idDireccion;
    }
    public Integer getIdPlantilla() {
        return this.idPlantilla;
    }
    
    public void setIdPlantilla(Integer idPlantilla) {
        this.idPlantilla = idPlantilla;
    }
    public String getTipoCfd() {
        return this.tipoCfd;
    }
    
    public void setTipoCfd(String tipoCfd) {
        this.tipoCfd = tipoCfd;
    }
    public String getPedimento() {
        return this.pedimento;
    }
    
    public void setPedimento(String pedimento) {
        this.pedimento = pedimento;
    }
    public String getFechaPedimento() {
        return this.fechaPedimento;
    }
    
    public void setFechaPedimento(String fechaPedimento) {
        this.fechaPedimento = fechaPedimento;
    }
    public String getAduana() {
        return this.aduana;
    }
    
    public void setAduana(String aduana) {
        this.aduana = aduana;
    }
    public Date getGenerationDate() {
        return this.generationDate;
    }
    
    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }
    public String getTicket() {
        return this.ticket;
    }
    
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    public Integer getIdConfiguracion() {
        return this.idConfiguracion;
    }
    
    public void setIdConfiguracion(Integer idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }
    public String getCadenaOriginal() {
        return this.cadenaOriginal;
    }
    
    public void setCadenaOriginal(String cadenaOriginal) {
        this.cadenaOriginal = cadenaOriginal;
    }
    public String getEstadoContable() {
        return this.estadoContable;
    }
    
    public void setEstadoContable(String estadoContable) {
        this.estadoContable = estadoContable;
    }
    public Date getFechaVencimiento() {
        return this.fechaVencimiento;
    }
    
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    public String getRfcEmisor() {
        return this.rfcEmisor;
    }
    
    public void setRfcEmisor(String rfcEmisor) {
        this.rfcEmisor = rfcEmisor;
    }
    public String getRfcReceptor() {
        return this.rfcReceptor;
    }
    
    public void setRfcReceptor(String rfcReceptor) {
        this.rfcReceptor = rfcReceptor;
    }
    public Boolean getReportado() {
        return this.reportado;
    }
    
    public void setReportado(Boolean reportado) {
        this.reportado = reportado;
    }




}


