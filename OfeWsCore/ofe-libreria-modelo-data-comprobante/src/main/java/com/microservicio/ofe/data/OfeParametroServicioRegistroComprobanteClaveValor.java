package com.microservicio.ofe.data;

import java.io.Serializable;

public class OfeParametroServicioRegistroComprobanteClaveValor
implements Serializable
{

	private static final long serialVersionUID = 1L;
	private String lsClave;
	private String lsValor;
	
	public String getLsClave() {
		return lsClave;
	}
	public void setLsClave(String lsClave) {
		this.lsClave = lsClave;
	}
	public String getLsValor() {
		return lsValor;
	}
	public void setLsValor(String lsValor) {
		this.lsValor = lsValor;
	}
	
	
	
}
