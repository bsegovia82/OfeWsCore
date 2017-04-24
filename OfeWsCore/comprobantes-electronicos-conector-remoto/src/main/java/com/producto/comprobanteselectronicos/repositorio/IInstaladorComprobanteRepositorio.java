package com.producto.comprobanteselectronicos.repositorio;

import javax.ejb.Local;


import com.producto.comprobanteselectronicos.repositorio.modelo.RegistroComprobante;

@Local
public interface IInstaladorComprobanteRepositorio {

	public String registrarComprobanteSeed(RegistroComprobante datosAcionales) throws Throwable;

	public String registrarXmlAutorizadoSinSoap(String xml);

}