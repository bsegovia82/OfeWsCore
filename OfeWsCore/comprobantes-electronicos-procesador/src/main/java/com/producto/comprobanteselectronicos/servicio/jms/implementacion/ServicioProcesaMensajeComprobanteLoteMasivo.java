package com.producto.comprobanteselectronicos.servicio.jms.implementacion;

import java.util.Date;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.MensajeLoteMasivo;
import com.producto.comprobanteselectronicos.servicio.etapas.ServicioAutorizacion;
import com.producto.comprobanteselectronicos.servicio.etapas.ServicioComprobacion;
import com.producto.comprobanteselectronicos.servicio.etapas.ServicioFirmado;
import com.producto.comprobanteselectronicos.servicio.jms.IServicioProcesaMensajeComprobanteLoteMasivo;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioControlFallos;

@Stateless
@Asynchronous
public class ServicioProcesaMensajeComprobanteLoteMasivo implements IServicioProcesaMensajeComprobanteLoteMasivo
{
	
	@EJB
	private ServicioControlFallos controlFallo;
	
	@EJB
	private ServicioFirmado servicioFirmado;
	
	@EJB
	private ServicioComprobacion servicioComprobacion;
	
	@EJB
	private ServicioAutorizacion servicioAutorizacion;
	
	public void procesarEtapasMensajeLoteMasivo(
			MensajeLoteMasivo loteMasivo) 
	{
		switch (loteMasivo.getEtapa()) {
		case EXTRACCION:
			//Se debe controlar por un parametro para solo firmar el documento ono firmar
			System.out.println("***********************Empieza el firmado del documento "+ new Date().getTime()+"*******************************************");
			servicioFirmado.firmarLoteMasivoColaJMS(loteMasivo);
			System.out.println("***********************liberacion de hilo firmado del documento "+ new Date().getTime()+"*******************************************");
			break;
		case FIRMADO:
			//Debes comprobar
			//parametro para no enviar a comprobar
			System.out.println("***********************Empieza la comprobacion del lote masivo "+ new Date().getTime()+"*******************************************");
			servicioComprobacion.comprobacionLoteMasivoColaJMS(loteMasivo);
			System.out.println("***********************liberacion de hilo comprobacion del documento "+ new Date().getTime()+"*******************************************");
			break;
		case COMPROBADO:
			//Debes autorizar
			//parametro para autorizar
			System.out.println("***********************Empieza la autorizacion del lote masivo "+ new Date().getTime()+"*******************************************");
			servicioAutorizacion.autorizarLoteMasivoColaJMS(loteMasivo);
			System.out.println("***********************liberacion de hilo autorizacion del documento "+ new Date().getTime()+"*******************************************");
			break;
		case AUTORIZADO:
			//parametro para reportear
			//El proceso ya fue terminado para este lote masivo
			controlFallo.dobleProcesoLoteMasivoColaJMS(loteMasivo);
		default:
			//Mensaje desconocido mal envio de mensaje, parametros invalidos
			controlFallo.etapaNoProgramadaLoteMasivoColaJMS(loteMasivo);
			break;
		}
	}	
}