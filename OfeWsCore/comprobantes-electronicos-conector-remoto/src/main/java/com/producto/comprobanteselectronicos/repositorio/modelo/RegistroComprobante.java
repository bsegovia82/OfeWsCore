package com.producto.comprobanteselectronicos.repositorio.modelo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistroComprobante implements Serializable
{

	private static final long serialVersionUID = 1L;
		
	private String comprobante;
	private String periodo;
	private String auxiliar;
	
	public RegistroComprobante()
	{}
	
	public String getComprobante() {
		return comprobante;
	}
	
	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
	
	
}
