package com.producto.comprobanteselectronicos.conector.servicioweb;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.producto.comprobanteselectronicos.conector.IServicioConectorRepositorio;
import com.producto.comprobanteselectronicos.repositorio.IInstaladorComprobanteRepositorio;
import com.producto.comprobanteselectronicos.repositorio.modelo.RegistroComprobante;

@Stateless
@WebService(portName = "ConectorRepositorioPort", serviceName = "ConectorRepositorioService", 
	endpointInterface = "com.producto.comprobanteselectronicos.conector.IServicioConectorRepositorio", targetNamespace="http://seed.conector.com/repositorio")
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ServicioConectorRepositorio implements IServicioConectorRepositorio{
	
	@EJB
	private IInstaladorComprobanteRepositorio instaladorComprobante;

	@WebMethod(operationName="recepcionComprobante")
	@WebResult(name="MENSAJEINGRESO")
	public String recibirComprobante(@WebParam(name="DATOS_ADICIONALES", targetNamespace="http://seed.conector.com/repositorio") RegistroComprobante datos){
		String mensaje = "";
		try {
			String respuesta = instaladorComprobante.registrarComprobanteSeed(datos);			
			if ("1".equals(respuesta)) {
				return "1";
			} else {
				return "ERROR " + respuesta;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			mensaje = "ERROR - " + e.getMessage();
		}
		return mensaje;
	}
}
