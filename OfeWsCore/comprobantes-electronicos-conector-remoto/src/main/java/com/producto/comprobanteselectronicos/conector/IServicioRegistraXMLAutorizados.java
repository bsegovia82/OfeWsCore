package com.producto.comprobanteselectronicos.conector;


import javax.jws.WebParam;


public interface IServicioRegistraXMLAutorizados {
	

			
	public String registraXmlAutorizadoSinSoap(@WebParam(name="xml") String xml);

}
