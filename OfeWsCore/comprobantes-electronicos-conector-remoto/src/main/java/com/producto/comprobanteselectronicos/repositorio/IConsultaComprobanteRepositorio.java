package com.producto.comprobanteselectronicos.repositorio;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

@Local
public interface IConsultaComprobanteRepositorio {

	public String consultarComprobanteSeed(String claveAcceso);
	
	public List<String> consultaGeneral(String nombreLocal, Date fechaEmision,String  suscriptor, String puntoEmision);
	
	public int actualizarEstadoRecepcionErp(String claveAcceso, String estado);
	
	//ARO-CONSULTA WS CADENA
	public List<String> consultaGeneralCadenas(String cadena, Date fechaEmision,String  suscriptor);
	
	//ARO-INTEGRACION-TRAMA
	public String enviaTramaComprobante(String pTramaComprobante, String pSuscriptores);

}