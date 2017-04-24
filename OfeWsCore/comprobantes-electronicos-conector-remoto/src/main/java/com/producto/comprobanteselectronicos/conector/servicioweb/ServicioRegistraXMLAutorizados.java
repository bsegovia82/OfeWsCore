package com.producto.comprobanteselectronicos.conector.servicioweb;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.producto.comprobanteselectronicos.conector.IServicioRegistraXMLAutorizados;
import com.producto.comprobanteselectronicos.repositorio.IInstaladorComprobanteRepositorio;

@Stateless
@WebService
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ServicioRegistraXMLAutorizados implements IServicioRegistraXMLAutorizados{

	@EJB
	private IInstaladorComprobanteRepositorio instaladorComprobante;


	@Override
	@WebMethod(operationName="recepcionXmlAutorizadoSinSoap")
	public String registraXmlAutorizadoSinSoap(@WebParam(name="xml") String xml) {
		try {
			return instaladorComprobante.registrarXmlAutorizadoSinSoap(xml);
		} catch (Throwable e) {
			e.printStackTrace();
			return "ERROR AL REGISTRAR XML AUTORIZADO";
		}
	}

}
