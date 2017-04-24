package com.producto.comprobanteselectronicos.conector;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.producto.comprobanteselectronicos.repositorio.modelo.RegistroComprobante;

@WebService(targetNamespace="http://seed.conector.com/repositorio")
public interface IServicioConectorRepositorio {
	
	@WebResult(name="MENSAJEINGRESO")
	public String recibirComprobante( @WebParam(name="REGISTRO_COMPROBANTE_XML", targetNamespace="http://seed.conector.com/repositorio")
    		RegistroComprobante datos);
	
}

