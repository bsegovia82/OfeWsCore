package com.producto.comprobanteselectronicos.repositorio.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.producto.comprobanteselectronicos.repositorio.IInstaladorComprobanteRepositorio;
import com.producto.comprobanteselectronicos.repositorio.modelo.RegistroComprobante;
import com.producto.comprobanteselectronicos.servicio.individual.IProcesamientoComprobanteIndividual;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class InstaladorComprobanteRepositorio implements IInstaladorComprobanteRepositorio {
	
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_PROCESADOR_INDIVIDUAL)
	private IProcesamientoComprobanteIndividual proceso;
	
	public String registrarComprobanteSeed(RegistroComprobante datosAcionales) throws Throwable {
		try {			
			String infoAdicional = datosAcionales.getAuxiliar()+datosAcionales.getPeriodo();
			System.err.println("INFOADICIONAL --------- "+infoAdicional);
			String resp = proceso.armarXmlWebServices(datosAcionales.getComprobante(), infoAdicional);		
			return resp;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Throwable("Error al registrar el archivo en la cola de proceso individual "+ e.getMessage());
		} 
		
	}


	@Override
	public String registrarXmlAutorizadoSinSoap(String xml) { 
		String lRespuesta="";
	try {
		lRespuesta=proceso.registrarXmlAutorizadoMigracion(xml);
		
		if(lRespuesta==null){
		   lRespuesta="REGISTRADO";
		}	  	

	}catch (Throwable lError)
	{ 
		lError.printStackTrace(); 
		lRespuesta="Error General "+lError.getMessage();
	}
		return lRespuesta;
	}
	
}
