package com.producto.comprobanteselectronicos.servicio.jms.implementacion;


import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.MensajeDespachoDetalles;
import com.producto.comprobanteselectronicos.notificaciones.modelo.entidades.GeMensajeNotificacion;
import com.producto.comprobanteselectronicos.notificaciones.modelo.entidades.GeTipoNotificacion;
import com.producto.comprobanteselectronicos.parametros.modelo.entidades.GeDatosGeneralesCompania;
import com.producto.comprobanteselectronicos.parametros.modelo.entidades.GeSuscriptor;
import com.producto.comprobanteselectronicos.servicio.jms.IServicioGeneradorNotificacionRideComprobante;
import com.producto.comprobanteselectronicos.servicio.notificaciones.IServiciosNotificaciones;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;
import com.producto.comprobanteselectronicos.servicio.parametros.IServicioParametros;

@Stateless
@Asynchronous
public class ServicioGeneradorNotificacionRideComprobante implements IServicioGeneradorNotificacionRideComprobante
{
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_NOTIFICADOR_RIDE_PDF)
	private IServiciosNotificaciones servicioNotificacion;
	
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_PARAMETROS)
	private IServicioParametros servicioParametros;
	
	private String asunto;
	private String emisor;  
	private String cuerpo;
	private String contactos;
	private String idTipo;
	private String copiaOculta;
	
	@PostConstruct
	private void init()
	{
//		asunto = servicioParametros.getValorParametro(INombresParametros.NOMBRE_PARAMETRO_NOTIFICACION_RIDE_ASUNTO);
//		emisor = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_EMISOR);
//		cuerpo = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_CUERPO);		
//		contactos = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_CONTACTOS_ADICIONALES);	
//		idTipo = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_IDTIPONOTIFICACION);	
//		//Puede ir a un repositorio
//		copiaOculta = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_CONTACTOS_OCULTOS);	
	}
	
	public void generarNotificacionRideComprobante(
			MensajeDespachoDetalles mensaje) 
	{
		GeDatosGeneralesCompania empresa = servicioParametros.datosCompania(mensaje.getIdCompania()); 
		asunto = servicioParametros.getValorParametro(INombresParametros.NOMBRE_PARAMETRO_NOTIFICACION_RIDE_ASUNTO,empresa.getIdSuscriptor());
		emisor = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_EMISOR,empresa.getIdSuscriptor());
		cuerpo = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_CUERPO,empresa.getIdSuscriptor());		
		contactos = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_CONTACTOS_ADICIONALES,empresa.getIdSuscriptor());	
		idTipo = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_IDTIPONOTIFICACION,empresa.getIdSuscriptor());	
		//Puede ir a un repositorio
		copiaOculta = servicioParametros.getValorParametro(INombresParametros.NOTIFICACION_RIDE_CONTACTOS_OCULTOS,empresa.getIdSuscriptor());	
		
		GeMensajeNotificacion mensajeMail = new GeMensajeNotificacion();
		mensajeMail.setArchivosAdjuntos(mensaje.getDetalleDocumento().getRutaPDF());
		mensajeMail.setAsunto(asunto+ " - " + decodeComprobante(mensaje.getTipoDocumento()) + " - " +  mensaje.getDetalleDocumento().getClaveAcceso());
		
		mensajeMail.setClaveAccesoReferencialDocumento(mensaje.getDetalleDocumento().getClaveAcceso());
		
		mensajeMail.setClaveAccesoReferencialLote(mensaje.getDetalleDocumento().getCabeceraProcesoLoteMasivo().getClaveAcceso());
		mensajeMail.setClaveNovedad(mensaje.getClaveNovedad());
		mensajeMail.setCuerpoMensaje(cuerpo);
		mensajeMail.setDestinatariosCopiaOculta(copiaOculta);
		mensajeMail.setDetinatarios(mensaje.getDetalleDocumento().getEmailCliente());
		mensajeMail.setEstado("E");
		mensajeMail.setFechaRegistro(new Date());
		mensajeMail.setIdCompania(mensaje.getIdCompania());
		mensajeMail.setMailEmisor(emisor);
		mensajeMail.setDestinatariosCopia(contactos);
		mensajeMail.setNombreProceso(mensaje.getNombreProceso());
		GeTipoNotificacion tipo = new GeTipoNotificacion();
		tipo.setIdTipoNotificacion(new Long(idTipo));
		mensajeMail.setTipoNotificacion(tipo);
		GeSuscriptor suscriptor = servicioParametros.consultaSuscriptor(empresa.getIdSuscriptor());
		servicioNotificacion.enviarNotificacionRide(mensajeMail,suscriptor.getTrackingNavegacion());
	}
	
	//ojo con esto
	
	private String decodeComprobante(String tipoDoc)
	{
		if (tipoDoc.equals("01"))
		{
			return "FACTURA";
		}
		return "";
	}
	

}
