package com.producto.comprobanteselectronicos.servicio.etapas;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import com.producto.comprobanteselectronicos.comprobador.desarrollo.masivo.ObjectFactory;
import com.producto.comprobanteselectronicos.comprobador.desarrollo.masivo.RespuestaSolicitud;
import com.producto.comprobanteselectronicos.comprobador.servicios.IComprobacionMasiva;
import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.Etapas;
import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.MensajeLoteMasivo;
import com.producto.comprobanteselectronicos.servicio.jms.IServicioClienteJMSLoteMasivo;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioComprobacionWS;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioInterpretacionRespuestas;
import com.producto.comprobanteselectronicos.servicio.logs.ILogging;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;

@Stateless
public class ServicioComprobacion {
	
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_LOGGIN)
	private ILogging loggin;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_JMS_MASIVO_FACTURAS)
	private IServicioClienteJMSLoteMasivo clienteJMS;

	@EJB
	private IComprobacionMasiva comprobadorMasivo;

	@EJB
	private ServicioInterpretacionRespuestas interpretadorComprobaciones;

	@EJB
	private ServicioComprobacionWS comprobadorWS;

	@Asynchronous
	public void comprobacionLoteMasivoColaJMS(
			MensajeLoteMasivo mensajeLoteMasivo) {
		String nombreMetodo = this.getClass().getCanonicalName()+".comprobacionLoteMasivoColaJMS";
		String estado = null;
		loggin.registroNovedadesAplicacion(
				mensajeLoteMasivo.getNombreProceso(), mensajeLoteMasivo
						.getClaveNovedad(), mensajeLoteMasivo.getUsuario(),
				nombreMetodo, "INICIO DE METODO ENVIO A COMPROBACION "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso() + ", USUARIO "
						+ mensajeLoteMasivo.getUsuario() + ", COMPAÑIA "
						+ mensajeLoteMasivo.getCompania(), null,
				mensajeLoteMasivo.getNotificar(), null,
				"INICIO DE METODO ENVIO A COMPROBACION "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso() + ", USUARIO "
						+ mensajeLoteMasivo.getUsuario() + ", COMPAÑIA "
						+ mensajeLoteMasivo.getCompania(), new Date());

		if (comprobadorWS.servicioComprobacionLoteMasivoDesarrolloDisponible()) {
			RespuestaSolicitud respuesta = null;
			boolean continuar = false;
			String observacion = null;
			try {
				respuesta = comprobadorMasivo.comprobarLote(
						mensajeLoteMasivo.getUrlArchivoXML(),
						mensajeLoteMasivo.getAmbiente(),
						RespuestaSolicitud.class);
				continuar = true;
			}catch(TimeoutException error)
			{
				continuar = false;
				estado = "TDC";
				observacion = "ERROR AL CONSIMIR EL SERVICIO DE COMPROBACION DEL SRI, POSIBLEMENTE EL SERVICIO NO SE ENCUENTRE DISPONIBLE o NO TENGA CONEXION A INTERNET "
						+ "ESTE TIPO DE EXCEPTION SE DA CUANDO EL SERVICIO WEB SUPERA EL TIEMPO PERMITIDO "
						+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
								: "PRODUCCION, ")
						+ error.getMessage()
						+ ", CLAVE ACCESO LOTE "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso();
				error.printStackTrace();
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"NO TENEMOS ACCESO AL SERVICIO WEB DE COMPROBACION DEL SRI, NO SE ENCUENTRA DISPONIBLE O NO TIENE CONEXION A INTERNET, "
								+ "POR FAVOR REVISAR URGENTE NO SE ESTA REALIZANDO LA TRANSMICION DE DOCUMENTOS"
								+ "ESTE TIPO DE EXCEPTION SE DA CUANDO EL SERVICIO WEB SUPERA EL TIEMPO PERMITIDO "
								+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
										: "PRODUCCION, ")
								+ error.getMessage()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(),
						null,
						true,// Esto se debe notificar necesariamente
						error,
						"NO TENEMOS ACCESO AL SERVICIO WEB DE COMPROBACION DEL SRI, NO SE ENCUENTRA DISPONIBLE O NO TIENE CONEXION A INTERNET, "
								+ "POR FAVOR REVISAR URGENTE NO SE ESTA REALIZANDO LA TRANSMICION DE DOCUMENTOS"
								+ "ESTE TIPO DE EXCEPTION SE DA CUANDO EL SERVICIO WEB SUPERA EL TIEMPO PERMITIDO "
								+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
										: "PRODUCCION, ")
								+ error.getMessage()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());
			}
			
			catch (SocketTimeoutException error) {
				continuar = false;
				estado = "TSC";
				observacion = "ERROR AL CONSIMIR EL SERVICIO DE COMPROBACION DEL SRI, POSIBLEMENTE EL SERVICIO NO SE ENCUENTRE DISPONIBLE o NO TENGA CONEXION A INTERNET "
						+ "ESTE TIPO DE EXCEPTION SE DA CUANDO EL SERVICIO WEB SUPERA EL TIEMPO PERMITIDO "
						+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
								: "PRODUCCION, ")
						+ error.getMessage()
						+ ", CLAVE ACCESO LOTE "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso();
				error.printStackTrace();
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"NO TENEMOS ACCESO AL SERVICIO WEB DE COMPROBACION DEL SRI, NO SE ENCUENTRA DISPONIBLE O NO TIENE CONEXION A INTERNET, "
								+ "POR FAVOR REVISAR URGENTE NO SE ESTA REALIZANDO LA TRANSMICION DE DOCUMENTOS"
								+ "ESTE TIPO DE EXCEPTION SE DA CUANDO EL SERVICIO WEB SUPERA EL TIEMPO PERMITIDO "
								+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
										: "PRODUCCION, ")
								+ error.getMessage()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(),
						null,
						true,// Esto se debe notificar necesariamente
						error,
						"NO TENEMOS ACCESO AL SERVICIO WEB DE COMPROBACION DEL SRI, NO SE ENCUENTRA DISPONIBLE O NO TIENE CONEXION A INTERNET, "
								+ "POR FAVOR REVISAR URGENTE NO SE ESTA REALIZANDO LA TRANSMICION DE DOCUMENTOS"
								+ "ESTE TIPO DE EXCEPTION SE DA CUANDO EL SERVICIO WEB SUPERA EL TIEMPO PERMITIDO "
								+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
										: "PRODUCCION, ")
								+ error.getMessage()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());
			} catch (UnknownHostException error) {
				estado = "EHC";
				observacion = "ERROR AL CONSIMIR EL SERVICIO DE COMPROBACION DEL SRI, POSIBLEMENTE EL SERVICIO NO SE ENCUENTRE DISPONIBLE o NO TENGA CONEXION A INTERNET, "
						+ "ESTE TIPO DE EXCEPTION SE DA CUANDO ESTA DETENIDO EL AMBIENTE DE "
						+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
								: "PRODUCCION, ")
						+ error.getMessage()
						+ ", CLAVE ACCESO LOTE "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso();
				error.printStackTrace();
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"NO TENEMOS ACCESO AL SERVICIO WEB DE COMPROBACION DEL SRI, NO SE ENCUENTRA DISPONIBLE O NO TIENE CONEXION A INTERNET, "
								+ "POR FAVOR REVISAR URGENTE NO SE ESTA REALIZANDO LA TRANSMICION DE DOCUMENTOS"
								+ "ESTE TIPO DE EXCEPTION SE DA CUANDO ESTA DETENIDO EL AMBIENTE DE "
								+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
										: "PRODUCCION, ")
								+ error.getMessage()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(),
						null,
						true,// Esto se debe notificar necesariamente
						error,
						"NO TENEMOS ACCESO AL SERVICIO WEB DE COMPROBACION DEL SRI, NO SE ENCUENTRA DISPONIBLE O NO TIENE CONEXION A INTERNET, "
								+ "POR FAVOR REVISAR URGENTE NO SE ESTA REALIZANDO LA TRANSMICION DE DOCUMENTOS"
								+ "ESTE TIPO DE EXCEPTION SE DA CUANDO ESTA DETENIDO EL AMBIENTE DE "
								+ (mensajeLoteMasivo.getAmbiente().equals("1") ? "PRUEBAS, "
										: "PRODUCCION, ")
								+ error.getMessage()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());
			} catch (Throwable e) {
				estado = "EDC";
				continuar = false;
				StringWriter str = new StringWriter();
				e.printStackTrace(new PrintWriter(str));
				observacion = str.toString().substring(0, 2999); 
				e.printStackTrace();
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(), nombreMetodo,
						"ERROR AL CONSIMIR EL SERVICIO DE COMPROBACION DEL SRI, CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), null,
						mensajeLoteMasivo.getNotificar(), e,
						"ERROR AL CONSIMIR EL SERVICIO DE COMPROBACION DEL SRI, CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());
			}

			if (continuar) {

				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"COMPROBACION RECIBIDA DESDE EL SRI "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"COMPROBACION RECIBIDA DESDE EL SRI "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(), new Date());

				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"EL PROCESO CAMBIARA DE ESTADO, CAMPO ULTIMA ETAPA COMPROBACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"EL PROCESO CAMBIARA DE ESTADO, CAMPO ULTIMA ETAPA COMPROBACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(), new Date());
				try {
					String xml = respuestaXml(respuesta);
					loggin.avanzarEtapaProceso(mensajeLoteMasivo
							.getLoteMasivoComprobante().getClaveAcceso(),
							mensajeLoteMasivo.getNombreProceso(),
							mensajeLoteMasivo.getEtapa().name(),
							mensajeLoteMasivo.getUrlArchivoXML(), respuesta
									.getEstado(), xml);
					mensajeLoteMasivo.setEtapa(Etapas.COMPROBADO);
					continuar = true;
				} catch (Throwable e) {
					estado = "EEC";
					continuar = false;
					StringWriter str = new StringWriter();
					e.printStackTrace(new PrintWriter(str));
					observacion = str.toString().substring(0, 2999); 
					e.printStackTrace();
					loggin.registroNovedadesAplicacion(
							mensajeLoteMasivo.getNombreProceso(),
							mensajeLoteMasivo.getClaveNovedad(),
							mensajeLoteMasivo.getUsuario(),
							nombreMetodo,
							"ERROR ACTUALIZACION DE DATOS DE PROCESO MASIVO, AVANZAR ETAPA DE AUTORIZACION, CLAVE ACCESO LOTE "
									+ mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso(),
							null,
							mensajeLoteMasivo.getNotificar(),
							e,
							"ERROR ACTUALIZACION DE DATOS DE PROCESO MASIVO, AVANZAR ETAPA DE AUTORIZACION, CLAVE ACCESO LOTE "
									+ mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso(), new Date());
				}
			}

			if (continuar) {

				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"ACTUALIZACION DE DATOS DE PROCESO MASIVO, CAMPO ULTIMA ETAPA COMPROBACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"ACTUALIZACION DE DATOS DE PROCESO MASIVO, CAMPO ULTIMA ETAPA COMPROBACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(), new Date());
				if (respuesta.getEstado().toUpperCase().equals(INombresParametros.RESPUESTA_RECIBIDA_COMPROBACION_SRI)) {
					loggin.registroNovedadesAplicacion(
							mensajeLoteMasivo.getNombreProceso(),
							mensajeLoteMasivo.getClaveNovedad(),
							mensajeLoteMasivo.getUsuario(),
							nombreMetodo,
							"EL PROCESO ENVIARA EL MENSAJE JMS PARA CONTINUAR CON LA AUTORIZACION"
									+ mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso() + ", USUARIO "
									+ mensajeLoteMasivo.getUsuario()
									+ ", COMPAÑIA "
									+ mensajeLoteMasivo.getCompania(),
							null,
							mensajeLoteMasivo.getNotificar(),
							null,
							"EL PROCESO ENVIARA EL MENSAJE JMS PARA CONTINUAR CON LA AUTORIZACION"
									+ mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso() + ", USUARIO "
									+ mensajeLoteMasivo.getUsuario()
									+ ", COMPAÑIA "
									+ mensajeLoteMasivo.getCompania(),
							new Date());
					try {
						clienteJMS.registrarLoteMasivo(mensajeLoteMasivo);
						continuar = true;
					} catch (Throwable e) {
						estado = "ECC";
						continuar = false;
						StringWriter str = new StringWriter();
						e.printStackTrace(new PrintWriter(str));
						observacion = str.toString().substring(0, 2999); 
						e.printStackTrace();
						loggin.registroNovedadesAplicacion(mensajeLoteMasivo
								.getNombreProceso(), mensajeLoteMasivo
								.getClaveNovedad(), mensajeLoteMasivo
								.getUsuario(), nombreMetodo,
								"ERROR ENVIAR MENSAJE PARA AUTORIZACION, CLAVE ACCESO LOTE "
										+ mensajeLoteMasivo
												.getLoteMasivoComprobante()
												.getClaveAcceso(), null,
								mensajeLoteMasivo.getNotificar(), e,
								"ERROR ENVIAR MENSAJE PARA COMPROBACION, CLAVE ACCESO LOTE "
										+ mensajeLoteMasivo
												.getLoteMasivoComprobante()
												.getClaveAcceso(), new Date());
					}
				} else 
				{
					continuar = false;
					estado = "RC";
					observacion = "EL DOCUMENTO FUE RECHAZADO POR EL SRI, POR FAVOR REVISAR LA RESPUESTA, CLAVE ACCESO LOTE "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso();
					interpretadorComprobaciones.interpretarComprobaciones(
							respuesta.getComprobantes().getComprobante(),
							mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso(), estado);
				}
			}
			if (!continuar) {
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"ETAPA DEL PROCESO COMPROBACION FALLIDA, SE CAMBIARAN DE ESTADO LOS REGISTROS PARA VOLVER A INTERNTAR LA COMPROBACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"ETAPA DEL PROCESO COMPROBACION FALLIDA, SE CAMBIARAN DE ESTADO LOS REGISTROS PARA VOLVER A INTERNTAR LA COMPROBACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());
						loggin.inactivarCabeceraLoteMasivoEtapas(mensajeLoteMasivo
						.getLoteMasivoComprobante().getClaveAcceso(),
						observacion, estado==null?"XC":estado);
				
			} else {
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"ENVIO DE MENSAJE, ETAPA DE AUTORIZACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"ENVIO DE MENSAJE, ETAPA DE AUTORIZACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(), new Date());
			}
			loggin.registroNovedadesAplicacion(mensajeLoteMasivo
					.getNombreProceso(), mensajeLoteMasivo.getClaveNovedad(),
					mensajeLoteMasivo.getUsuario(), nombreMetodo,
					"FINALIZACION DE METODO ENVIO A COMPROBACION "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso() + ", USUARIO "
							+ mensajeLoteMasivo.getUsuario() + ", COMPAÑIA "
							+ mensajeLoteMasivo.getCompania(), null,
					mensajeLoteMasivo.getNotificar(), null,
					"FINALIZACION DE METODO ENVIO A COMPROBACION "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso() + ", USUARIO "
							+ mensajeLoteMasivo.getUsuario() + ", COMPAÑIA "
							+ mensajeLoteMasivo.getCompania(), new Date());

		} else {
			loggin.registroNovedadesAplicacion(
					mensajeLoteMasivo.getNombreProceso(),
					mensajeLoteMasivo.getClaveNovedad(),
					mensajeLoteMasivo.getUsuario(),
					nombreMetodo,
					"EL SERVICIO DE COMPROBACION DE LOTE MASIVO NO SE ENCUENTRA DISPONIBLE, LOTE MASIVO NO COMPROBADO NO SE ENVIO AL SRI : "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso(),
					null,
					mensajeLoteMasivo.getNotificar(),
					null,
					"EL SERVICIO DE COMPROBACION DE LOTE MASIVO NO SE ENCUENTRA DISPONIBLE, LOTE MASIVO NO COMPROBADO NO SE ENVIO AL SRI : "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso(), new Date());

			loggin.inactivarCabeceraLoteMasivoEtapas(
					mensajeLoteMasivo.getLoteMasivoComprobante()
							.getClaveAcceso(),
					"EL SERVICIO DE COMPROBACION DE LOTE MASIVO NO SE ENCUENTRA DISPONIBLE, LOTE MASIVO NO COMPROBADO NO SE ENVIO AL SRI",
					"TAC");//Timeout antes Comprobacion
		}
	}

	private String respuestaXml(RespuestaSolicitud respuesta) {
		ObjectFactory objectFactoryRecepcion = new ObjectFactory();
		try {
			StringWriter writer = new StringWriter();
			JAXBContext contextRecepcion = JAXBContext
					.newInstance("com.producto.comprobanteselectronicos.comprobador.desarrollo.masivo");
			JAXBElement<RespuestaSolicitud> eRespuestaSolicitud = objectFactoryRecepcion
					.createRespuestaSolicitud(respuesta);
			Marshaller marshallerRecepcion = contextRecepcion
					.createMarshaller();
			marshallerRecepcion.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					true);
			marshallerRecepcion.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshallerRecepcion.marshal(eRespuestaSolicitud, writer);
			return writer.toString();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void comprobarComprobanteIndividual(String archivo, String string, String compania)
	{
		
	}

}
