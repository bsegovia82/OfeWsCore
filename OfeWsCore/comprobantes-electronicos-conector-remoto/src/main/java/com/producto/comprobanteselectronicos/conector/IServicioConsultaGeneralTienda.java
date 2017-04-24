package com.producto.comprobanteselectronicos.conector;

import java.util.List;

import com.producto.comprobanteselectronicos.repositorio.modelo.ReporteCadena;
import com.producto.comprobanteselectronicos.repositorio.modelo.ReporteTienda;

public interface IServicioConsultaGeneralTienda {

	public List<ReporteTienda> consultaGeneral(String tienda, String fechaEmision, String suscriptor,String puntoEmision,
			String usuario, String clave,String token);
	
	public List<ReporteCadena> consultaGeneralCadena(String cadena, String fechaEmision, String suscriptor, String usuario, String clave,String token);

}