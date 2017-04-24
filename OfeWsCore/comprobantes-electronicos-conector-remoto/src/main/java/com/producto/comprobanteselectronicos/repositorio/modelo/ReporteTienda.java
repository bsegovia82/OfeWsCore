package com.producto.comprobanteselectronicos.repositorio.modelo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReporteTienda implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String numeroTransacciones;
	private String ventaTotal;
	private String ventaNeta;
	private String tipoDocumento;
	private String estado;
	private String minSecuencia;
	private String maxSecuencia;
	private String mensaje;
	private String puntoEmision;
	private String establecimiento;
	
	public ReporteTienda()
	{}
	
	public String getNumeroTransacciones() {
		return numeroTransacciones;
	}
	public void setNumeroTransacciones(String numeroTransacciones) {
		this.numeroTransacciones = numeroTransacciones;
	}
	public String getVentaTotal() {
		return ventaTotal;
	}
	public void setVentaTotal(String ventaTotal) {
		this.ventaTotal = ventaTotal;
	}
	public String getVentaNeta() {
		return ventaNeta;
	}
	public void setVentaNeta(String ventaNeta) {
		this.ventaNeta = ventaNeta;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getMinSecuencia() {
		return minSecuencia;
	}
	public void setMinSecuencia(String minSecuencia) {
		this.minSecuencia = minSecuencia;
	}
	public String getMaxSecuencia() {
		return maxSecuencia;
	}
	public void setMaxSecuencia(String maxSecuencia) {
		this.maxSecuencia = maxSecuencia;
	}
	public String getMensaje() {
		return mensaje;
	}	
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getPuntoEmision() {
		return puntoEmision;
	}

	public void setPuntoEmision(String puntoEmision) {
		this.puntoEmision = puntoEmision;
	}

	public String getEstablecimiento() {
		return establecimiento;
	}

	public void setEstablecimiento(String establecimiento) {
		this.establecimiento = establecimiento;
	}	

	
}
