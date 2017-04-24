package com.producto.comprobanteselectronicos.servicio.logicanegocio;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.MensajeLoteMasivo;

@Stateless
@Asynchronous
public class ServicioControlFallos 
{
	//Injectar parametros
	//Injectar logs
	
	//Injectar el clienteJMS
	
	//injectar cliente notificacion
	
	public void dobleProcesoLoteMasivoColaJMS(MensajeLoteMasivo mensajeLoteMasivo)
	{
		//Registrar en cola de mensaje lote masivo
		
	}
	
	public void etapaNoProgramadaLoteMasivoColaJMS(MensajeLoteMasivo mensajeLoteMasivo)
	{
		//Registrar en cola de mensaje lote masivo
		System.out.println("*************************************************");
		System.out.println("Etapa no programada " + mensajeLoteMasivo.getLoteMasivoComprobante());
		System.out.println("*************************************************");
	}
	
}
