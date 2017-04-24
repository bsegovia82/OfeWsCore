package com.producto.comprobanteselectronicos.servicio.individual.impl;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.producto.comprobanteselectronicos.documentos.modelo.entidades.FeCabecera;
import com.producto.comprobanteselectronicos.modelo.individual.RespuestaTransmisionComprobante;

@WebService(targetNamespace="http://www.corlasosa.com/servicios/")
@Stateless
public class WsProcesoIndividual 
{
	@WebMethod(action="http://www.corlasosa.com/servicios/wsTransmisionComprobantes")
	@WebResult(name="RespuestaTransmision", targetNamespace="http://www.corlasosa.com/servicios/")
	public RespuestaTransmisionComprobante transmitirElectronicamente(
			@WebParam(targetNamespace="http://www.corlasosa.com/servicios/", name="CABECERA_COMPROBANTE")
			FeCabecera cabeceraComprobante)
	{
		RespuestaTransmisionComprobante resp = new RespuestaTransmisionComprobante();
		resp.setFechaAutorizacion("01/01/2014");
		resp.setNumeroAutorizacion("44545454545");
		return resp;
	}
}
