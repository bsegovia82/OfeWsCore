package com.microservicio.ofe.excepciones;

public class ExcepcionMicroservicioOfe extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long lSCodigoError;
	private String lSMensajeError;
	
	public ExcepcionMicroservicioOfe(Long lSCodigoError, String lSMensajeError) {
		this.lSCodigoError = lSCodigoError;
		this.lSMensajeError = lSMensajeError;
	}
	
	public ExcepcionMicroservicioOfe(String lSMensajeError) {
		this.lSCodigoError = 1L;
		this.lSMensajeError = lSMensajeError;
	}
	
	public ExcepcionMicroservicioOfe(Long lSCodigoError) {
		this.lSCodigoError = lSCodigoError;
		this.lSMensajeError = "Imposible realizar la operacion";
	}

	public Long getlSCodigoError() {
		return lSCodigoError;
	}

	public String getlSMensajeError() {
		return lSMensajeError;
	}
	
	
	
	
	
}
