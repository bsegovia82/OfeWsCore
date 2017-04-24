package com.producto.comprobanteselectronicos.servicio.etapas;

import java.io.StringWriter;
import java.util.Date;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.ObjectFactory;
import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.RespuestaLote;
import com.producto.comprobanteselectronicos.autorizador.servicios.IAutorizacion;
import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.MensajeLoteMasivo;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioComprobacionWS;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioInterpretacionRespuestas;
import com.producto.comprobanteselectronicos.servicio.logs.ILogging;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;

@Stateless
@Asynchronous
public class ServicioAutorizacion {

//	@EJB
//	private IAutorizacion autorizador;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_LOGGIN)
	private ILogging loggin;

	@EJB
	private ServicioInterpretacionRespuestas interpretadorAutorizaciones;

	@EJB
	private ServicioComprobacionWS comprobadorWS;

	public void autorizarLoteMasivoColaJMS(MensajeLoteMasivo mensajeLoteMasivo) {
		String nombreMetodo = this.getClass().getCanonicalName()+".autorizarLoteMasivoColaJMS";
		String estado = null;
		loggin.registroNovedadesAplicacion(
				mensajeLoteMasivo.getNombreProceso(), mensajeLoteMasivo
						.getClaveNovedad(), mensajeLoteMasivo.getUsuario(),
				nombreMetodo,
				"INICIO DE METODO DE AUTORIZACION DE COMPROBANTE LOTE MASIVO "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso() + ", USUARIO "
						+ mensajeLoteMasivo.getUsuario() + ", COMPAÑIA "
						+ mensajeLoteMasivo.getCompania(), null,
				mensajeLoteMasivo.getNotificar(), null,
				"INICIO DE METODO DE AUTORIZACION DE COMPROBANTE LOTE MASIVO "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso() + ", USUARIO "
						+ mensajeLoteMasivo.getUsuario() + ", COMPAÑIA "
						+ mensajeLoteMasivo.getCompania(), new Date());
		if (comprobadorWS.servicioAutorizacionLoteMasivoDesarrolloDisponible()) {
			String observacion = null;
			boolean continuar = false;
			RespuestaLote respuesta = null;
			try {
//				respuesta = autoriza.obtieneAutorizacionLoteMasivo(
//						mensajeLoteMasivo.getLoteMasivoComprobante()
//								.getClaveAcceso(), mensajeLoteMasivo
//								.getAmbiente(), new Long(mensajeLoteMasivo
//								.getLoteMasivoComprobante().getComprobantes()
//								.getComprobante().size()), RespuestaLote.class);
				continuar = true;
			} catch (Throwable e) {
				estado = "EDA";//Volver a consultar
				continuar = false;
				e.printStackTrace();
				observacion = "ERROR AL CONSIMIR EL SERVICIO DE AUTORIZACION DEL SRI, CLAVE ACCESO LOTE "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso() + "<" + e.getMessage() + ">";
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(), nombreMetodo,
						"ERROR AL CONSIMIR EL SERVICIO DE AUTORIZACION DEL SRI, CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), null,
						mensajeLoteMasivo.getNotificar(), e,
						"ERROR AL CONSIMIR EL SERVICIO DE AUTORIZACION DEL SRI, CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());
			}

			if (continuar) {
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"RESPUESTA RECIBIDA DESDE EL SERVICIO DE AUTORIZACION SRI, COMPROBANTE LOTE MASIVO "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso()
								+ ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"RESPUESTA RECIBIDA DESDE EL SERVICIO DE AUTORIZACION SRI, COMPROBANTE LOTE MASIVO "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso()
								+ ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(), new Date());

				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"INICIO DE METODO INTERPRETACION DE AUTORIZACIONES DE COMPROBANTE LOTE MASIVO "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"INICIO DE METODO INTERPRETACION DE AUTORIZACIONES DE COMPROBANTE LOTE MASIVO "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania(), new Date());

				interpretadorAutorizaciones.interpretarAutorizaciones(respuesta
						.getAutorizaciones().getAutorizacion(),
						mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso());
			}

			if (!continuar) {
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"ETAPA DEL PROCESO AUTORIZACION FALLIDA, SE CAMBIARAN DE ESTADO LOS REGISTROS PARA VOLVER A INTERNTAR EL PROCESO "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + " - " + observacion,
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"ETAPA DEL PROCESO AUTORIZACION FALLIDA, SE CAMBIARAN DE ESTADO LOS REGISTROS PARA VOLVER A INTERNTAR EL PROCESO "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + " - " + observacion,
						new Date());

				loggin.inactivarCabeceraLoteMasivoEtapas(mensajeLoteMasivo
						.getLoteMasivoComprobante().getClaveAcceso(),
						observacion, estado==null?"XA":estado);

				// inactivar los servicios de la tabla para que vuelvan a
				// crearse

			} else {
				loggin.cerrarProceso(
						mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso(),
						"CIERRE DE PROCESO, SE EJECUTARON TODAS LAS ETAPAS, SE CAMBIA DE ESTADO AL PROCESO EN A, AUTORIZACION PARA LA CONSULTA",
						"A",
						"LOTE CONSULTADO: "
								+ respuesta.getClaveAccesoLoteConsultada()
								+ ", NUMERO COMPROBANTES ENVIADOS : "
								+ respuesta.getNumeroComprobantesLote()
								+ ", NUMERO DE DETALLES AUTORIZACIONES RECIBIDAS : "
								+ respuesta.getAutorizaciones()
										.getAutorizacion().size(),
						generarXMLRespuesta(respuesta));

				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"CIERRE DE PROCESO, SE REALIZARA EL ANALISIS DE LOS DETALLES DE LA RESPUESTA AUTORIZACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + " - " + observacion,
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"CIERRE DE PROCESO, SE REALIZARA EL ANALISIS DE LOS DETALLES DE LA RESPUESTA AUTORIZACION "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso() + " - " + observacion,
						new Date());
			}
			
			loggin.registroNovedadesAplicacion(mensajeLoteMasivo
					.getNombreProceso(), mensajeLoteMasivo.getClaveNovedad(),
					mensajeLoteMasivo.getUsuario(), nombreMetodo,
					"FIN DE METODO DE AUTORIZACION DE COMPROBANTE LOTE MASIVO "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso() + ", USUARIO "
							+ mensajeLoteMasivo.getUsuario() + ", COMPAÑIA "
							+ mensajeLoteMasivo.getCompania(), null,
					mensajeLoteMasivo.getNotificar(), null,
					"FIN DE METODO DE AUTORIZACION DE COMPROBANTE LOTE MASIVO "
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
					"NO SE REALIZO LA CONSULTA DE AUTORIZACION EL SERVICIO DEL SRI NO SE ENCUENTRA DISPONIBLE, LOTE MASIVO PENDIENTE DE AUTORIZACION"
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso(),
					null,
					mensajeLoteMasivo.getNotificar(),
					null,
					"NO SE REALIZO LA CONSULTA DE AUTORIZACION EL SERVICIO DEL SRI NO SE ENCUENTRA DISPONIBLE, LOTE MASIVO PENDIENTE DE AUTORIZACION"
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso(),
					new Date());

			loggin.inactivarCabeceraLoteMasivoEtapas(
					mensajeLoteMasivo.getLoteMasivoComprobante()
							.getClaveAcceso(),
					"NO SE REALIZO LA CONSULTA DE AUTORIZACION EL SERVICIO DEL SRI NO SE ENCUENTRA DISPONIBLE",
					"TAA");
		}
	}

	private String generarXMLRespuesta(RespuestaLote respuesta) {
		try {
			StringWriter str = new StringWriter();
			ObjectFactory objectFactoryAutorizacion = new ObjectFactory();
			JAXBContext contextAutorizacion = JAXBContext
					.newInstance("com.producto.comprobanteselectronicos.autorizador.desarrollo");
			JAXBElement<Object> eRespuestaComprobante = objectFactoryAutorizacion
					.createRespuestaAutorizacion(respuesta);
			Marshaller marshallerAutorizacion = contextAutorizacion
					.createMarshaller();
			marshallerAutorizacion.setProperty(
					Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshallerAutorizacion.setProperty(Marshaller.JAXB_ENCODING,
					"UTF-8");
			marshallerAutorizacion.marshal(eRespuestaComprobante, str);
			return str.toString();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

}
