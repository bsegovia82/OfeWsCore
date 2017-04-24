package com.microservicio.ofe.data;

import java.util.List;

public class OfeParametroServicioRegistroComprobante 
{
	private String lSClaveAcceso;
	private String lSSuscriptor;
	private Integer lIOrden;
	private String lSRucEmpresa;
	private String lSXmlSerializado;
	private String lSNumeroDocumento;
	private String lSAmbiente;
	private List<OfeParametroServicioRegistroComprobanteClaveValor> lDatosAdicionales;
	
	public String getlSClaveAcceso() {
		return lSClaveAcceso;
	}
	public void setlSClaveAcceso(String lSClaveAcceso) {
		this.lSClaveAcceso = lSClaveAcceso;
	}
	public String getlSSuscriptor() {
		return lSSuscriptor;
	}
	public void setlSSuscriptor(String lSSuscriptor) {
		this.lSSuscriptor = lSSuscriptor;
	}
	public Integer getlIOrden() {
		return lIOrden;
	}
	public void setlIOrden(Integer lIOrden) {
		this.lIOrden = lIOrden;
	}
	public String getlSRucEmpresa() {
		return lSRucEmpresa;
	}
	public void setlSRucEmpresa(String lSRucEmpresa) {
		this.lSRucEmpresa = lSRucEmpresa;
	}
	public String getlSXmlSerializado() {
		return lSXmlSerializado;
	}
	public void setlSXmlSerializado(String lSXmlSerializado) {
		this.lSXmlSerializado = lSXmlSerializado;
	}
	public String getlSNumeroDocumento() {
		return lSNumeroDocumento;
	}
	public void setlSNumeroDocumento(String lSNumeroDocumento) {
		this.lSNumeroDocumento = lSNumeroDocumento;
	}
	public String getlSAmbiente() {
		return lSAmbiente;
	}
	public void setlSAmbiente(String lSAmbiente) {
		this.lSAmbiente = lSAmbiente;
	}
	
	public List<OfeParametroServicioRegistroComprobanteClaveValor> getlDatosAdicionales() {
		return lDatosAdicionales;
	}
	
	public void setlDatosAdicionales(List<OfeParametroServicioRegistroComprobanteClaveValor> lDatosAdicionales) {
		this.lDatosAdicionales = lDatosAdicionales;
	}

}
