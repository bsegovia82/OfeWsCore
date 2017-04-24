package com.producto.comprobanteselectronicos.conector;

import javax.jws.WebParam;


public interface IServicioRegistraTrama {

	public String registraTramaComprobante(@WebParam(name="TRAMA_COMPROBANTE") String pTramaComprobante, @WebParam(name = "TOKEN") String pToken, 
			@WebParam(name = "SUSCRIPTOR") String pSuscriptor); 

}