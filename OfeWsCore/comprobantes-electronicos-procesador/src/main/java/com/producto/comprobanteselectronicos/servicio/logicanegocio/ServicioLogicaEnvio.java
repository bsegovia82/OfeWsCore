package com.producto.comprobanteselectronicos.servicio.logicanegocio;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;
import com.producto.comprobanteselectronicos.servicio.parametros.IServicioParametros;


@Stateless
public class ServicioLogicaEnvio 
{

	private String tamanioMaximoArchivo;
	
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_PARAMETROS)
	private IServicioParametros servicioParametros;
	
	@PostConstruct
	public void init()
	{
		tamanioMaximoArchivo = servicioParametros.getValorParametro(INombresParametros.PARAMETRO_TAMANIO_ARCHIVO);
	}
	public Boolean tamanioPermitidoArchivo(String rutaArchivo)
	{
		File file = new File (rutaArchivo);
		long tamanioKBS = Math.round(Math.ceil(file.length()/1024.0));
		return tamanioKBS <= Long.parseLong(tamanioMaximoArchivo);
	}
}
