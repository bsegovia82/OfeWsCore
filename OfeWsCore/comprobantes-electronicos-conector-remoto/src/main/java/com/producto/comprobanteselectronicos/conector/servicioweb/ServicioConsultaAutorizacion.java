package com.producto.comprobanteselectronicos.conector.servicioweb;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.producto.comprobanteselectronicos.conector.IServicioConsultaAutorizacion;
import com.producto.comprobanteselectronicos.repositorio.IConsultaComprobanteRepositorio;

@Stateless
@WebService
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ServicioConsultaAutorizacion implements IServicioConsultaAutorizacion{
	
	@EJB
	private IConsultaComprobanteRepositorio consultaComprobante;

	@WebMethod(operationName="recepcionComprobante")
	@WebResult(name="COMPROBANTE")
	public String consultarComprobantes(@WebParam(name="TRAMA_CLAVE_ACCESO") String claveAcceso){
		try {
			return consultaComprobante.consultarComprobanteSeed(claveAcceso);
		} catch (Throwable e){
			e.printStackTrace();
			return null;
		}
	}
}
