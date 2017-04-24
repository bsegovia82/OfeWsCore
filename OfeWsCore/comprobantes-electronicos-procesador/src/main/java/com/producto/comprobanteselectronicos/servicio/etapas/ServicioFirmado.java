package com.producto.comprobanteselectronicos.servicio.etapas;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.producto.comprobanteselectronicos.generadores.servicios.IServiciosFirmado;
import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.Etapas;
import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.MensajeLoteMasivo;
import com.producto.comprobanteselectronicos.parametros.modelo.entidades.GeDatosGeneralesCompania;
import com.producto.comprobanteselectronicos.servicio.jms.IServicioClienteJMSLoteMasivo;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioLogicaEnvio;
import com.producto.comprobanteselectronicos.servicio.logs.ILogging;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;
import com.producto.comprobanteselectronicos.servicio.parametros.IServicioParametros;

@Stateless
public class ServicioFirmado {

	private String rutaArchivoGenerado;

	@EJB
	private ServicioLogicaEnvio servicioLogica;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_JMS_MASIVO_FACTURAS)
	private IServicioClienteJMSLoteMasivo clienteJMS;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_LOGGIN)
	private ILogging loggin;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_PARAMETROS)
	private IServicioParametros servicioParametros;

	@EJB
	private IServiciosFirmado firmadorXML;

	@PostConstruct
	public void inicializar() {
		rutaArchivoGenerado = servicioParametros
				.getValorParametro(INombresParametros.PARAMETRO_RUTA_ARCHIVO_XML_FIRMADOS);
	}

	@Asynchronous
	public void firmarLoteMasivoColaJMS(MensajeLoteMasivo mensajeLoteMasivo) {
		String nombreMetodo = this.getClass().getCanonicalName()
				+ ".firmarLoteMasivoColaJMS";

		boolean continuar = false;
		GeDatosGeneralesCompania datosCompania = null;
		String observacion = null;

		loggin.registroNovedadesAplicacion(
				mensajeLoteMasivo.getNombreProceso(), mensajeLoteMasivo
						.getClaveNovedad(), mensajeLoteMasivo.getUsuario(),
				nombreMetodo,
				"INICIO DE METODO DE FIRMADO DE COMPROBANTE LOTE MASIVO "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso() + ", USUARIO "
						+ mensajeLoteMasivo.getUsuario() + ", COMPANIA "
						+ mensajeLoteMasivo.getCompania(), null,
				mensajeLoteMasivo.getNotificar(), null,
				"INICIO DE METODO DE FIRMADO DE COMPROBANTE LOTE MASIVO "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso() + ", USUARIO "
						+ mensajeLoteMasivo.getUsuario() + ", COMPANIA "
						+ mensajeLoteMasivo.getCompania(), new Date());

		try {
			datosCompania = servicioParametros.datosCompania(mensajeLoteMasivo
					.getCompania());
			loggin.registroNovedadesAplicacion(mensajeLoteMasivo
					.getNombreProceso(), mensajeLoteMasivo.getClaveNovedad(),
					mensajeLoteMasivo.getUsuario(), nombreMetodo,
					"CONSULTA DE DATOS DE COMPAÑIA LOTE MASIVO ETAPA DE FIRMADO "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso() + ", USUARIO "
							+ mensajeLoteMasivo.getUsuario() + ", COMPANIA "
							+ mensajeLoteMasivo.getCompania(), null,
					mensajeLoteMasivo.getNotificar(), null,
					"CONSULTA DE DATOS DE COMPAÑIA LOTE MASIVO ETAPA DE FIRMADO "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso() + ", USUARIO "
							+ mensajeLoteMasivo.getUsuario() + ", COMPANIA "
							+ mensajeLoteMasivo.getCompania(), new Date());
			continuar = true;
		} catch (Throwable e) {
			observacion = "ERROR AL OBTENER LOS DATOS DE LA COMPANIA, "
					+ mensajeLoteMasivo.getCompania()
					+ ", CLAVE ACCESO LOTE "
					+ mensajeLoteMasivo.getLoteMasivoComprobante()
							.getClaveAcceso() + " - " + e.getMessage();
			continuar = false;
			e.printStackTrace();
			loggin.registroNovedadesAplicacion(
					mensajeLoteMasivo.getNombreProceso(),
					mensajeLoteMasivo.getClaveNovedad(),
					mensajeLoteMasivo.getUsuario(), nombreMetodo,
					"ERROR AL OBTENER LOS DATOS DE LA COMPANIA, "
							+ mensajeLoteMasivo.getCompania()
							+ ", CLAVE ACCESO LOTE "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso(), null,
					mensajeLoteMasivo.getNotificar(), e,
					"ERROR AL OBTENER LOS DATOS DE LA COMPANIA, "
							+ mensajeLoteMasivo.getCompania()
							+ ", CLAVE ACCESO LOTE "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso(), new Date());

		}
		if (continuar) {
			if (datosCompania == null) {
				continuar = false;
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(), nombreMetodo,
						"NO EXISTEN DATOS PARA LA CONSULTA DE COMPANIA, "
								+ mensajeLoteMasivo.getCompania()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), null,
						mensajeLoteMasivo.getNotificar(), null,
						"NO EXISTEN DATOS PARA LA CONSULTA DE COMPANIA, "
								+ mensajeLoteMasivo.getCompania()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());
			}
		}
		if (continuar) {
			try {
				firmadorXML.firmarDocumentoXML(mensajeLoteMasivo
						.getLoteMasivoComprobante(), datosCompania
						.getRutaArchivoCertificado(), /* Encriptar */
						datosCompania.getPasswordCertificado(),
						generadorNombre(mensajeLoteMasivo
								.getLoteMasivoComprobante().getClaveAcceso()),
						INombresParametros.NODO_LOTE_FIRMAR,
						INombresParametros.NODO_LOTE_DESCRIPCION);
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"FIRMADO DE DOCUMENTO LOTE MASIVO "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso()
								+ ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania()
								+ ", NOMBRE DE ARCHIVO FIRMADO : "
								+ generadorNombre(mensajeLoteMasivo
										.getLoteMasivoComprobante()
										.getClaveAcceso()),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"FIRMADO DE DOCUMENTO LOTE MASIVO "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso()
								+ ", USUARIO "
								+ mensajeLoteMasivo.getUsuario()
								+ ", COMPAÑIA "
								+ mensajeLoteMasivo.getCompania()
								+ ", NOMBRE DE ARCHIVO FIRMADO : "
								+ generadorNombre(mensajeLoteMasivo
										.getLoteMasivoComprobante()
										.getClaveAcceso()), new Date());

				continuar = true;
				mensajeLoteMasivo
						.setUrlArchivoXML(generadorNombre(mensajeLoteMasivo
								.getLoteMasivoComprobante().getClaveAcceso()));
			} catch (Throwable e) {
				continuar = false;
				observacion = "ERROR AL FIRMAR EL LOTE MASIVO, CLAVE ACCESO LOTE "
						+ mensajeLoteMasivo.getLoteMasivoComprobante()
								.getClaveAcceso() + " - " + e.getMessage();
				e.printStackTrace();
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(), nombreMetodo,
						"ERROR AL FIRMAR EL LOTE MASIVO, CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), null,
						mensajeLoteMasivo.getNotificar(), e,
						"ERROR AL FIRMAR EL LOTE MASIVO, CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());

			}
		}

		if (continuar) {
			if (servicioLogica.tamanioPermitidoArchivo(mensajeLoteMasivo
					.getUrlArchivoXML())) {

				try {

					loggin.avanzarEtapaProceso(mensajeLoteMasivo
							.getLoteMasivoComprobante().getClaveAcceso(),
							mensajeLoteMasivo.getNombreProceso(),
							mensajeLoteMasivo.getEtapa().name(),
							mensajeLoteMasivo.getUrlArchivoXML(), null, null);
					loggin.registroNovedadesAplicacion(
							mensajeLoteMasivo.getNombreProceso(),
							mensajeLoteMasivo.getClaveNovedad(),
							mensajeLoteMasivo.getUsuario(),
							nombreMetodo,
							"ACTUALIZACION DE DATOS DE PROCESO MASIVO, AVANZAR ETAPA DE COMPROBACION "
									+ mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso()
									+ ", USUARIO "
									+ mensajeLoteMasivo.getUsuario()
									+ ", COMPAÑIA "
									+ mensajeLoteMasivo.getCompania()
									+ ", NOMBRE DE ARCHIVO FIRMADO : "
									+ generadorNombre(mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso()),
							null,
							mensajeLoteMasivo.getNotificar(),
							null,
							"ACTUALIZACION DE DATOS DE PROCESO MASIVO, AVANZAR ETAPA DE COMPROBACION "
									+ mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso()
									+ ", USUARIO "
									+ mensajeLoteMasivo.getUsuario()
									+ ", COMPAÑIA "
									+ mensajeLoteMasivo.getCompania()
									+ ", NOMBRE DE ARCHIVO FIRMADO : "
									+ generadorNombre(mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso()), new Date());

					mensajeLoteMasivo.setEtapa(Etapas.FIRMADO);
					continuar = true;
				} catch (Throwable e) {
					continuar = false;
					observacion = "ERROR AL ACTUALIZAR LOS DATOS DEL PROCESO MASIVO, AVANCE DE ETAPA, CLAVE ACCESO LOTE "
							+ mensajeLoteMasivo.getLoteMasivoComprobante()
									.getClaveAcceso() + " - " + e.getMessage();
					e.printStackTrace();
					loggin.registroNovedadesAplicacion(mensajeLoteMasivo
							.getNombreProceso(), mensajeLoteMasivo
							.getClaveNovedad(), mensajeLoteMasivo.getUsuario(),
							nombreMetodo,
							"ERROR AL ACTUALIZAR LOS DATOS DEL PROCESO MASIVO, CLAVE ACCESO LOTE "
									+ mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso(), null,
							mensajeLoteMasivo.getNotificar(), e,
							"ERROR AL ACTUALIZAR LOS DATOS DEL PROCESO MASIVO, CLAVE ACCESO LOTE "
									+ mensajeLoteMasivo
											.getLoteMasivoComprobante()
											.getClaveAcceso(), new Date());
				}
				if (continuar) {

					try {
						clienteJMS.registrarLoteMasivo(mensajeLoteMasivo);
						loggin.registroNovedadesAplicacion(
								mensajeLoteMasivo.getNombreProceso(),
								mensajeLoteMasivo.getClaveNovedad(),
								mensajeLoteMasivo.getUsuario(),
								nombreMetodo,
								"ENVIO DE MENSAJE, ETAPA DE COMPROBACION "
										+ mensajeLoteMasivo
												.getLoteMasivoComprobante()
												.getClaveAcceso()
										+ ", USUARIO "
										+ mensajeLoteMasivo.getUsuario()
										+ ", COMPAÑIA "
										+ mensajeLoteMasivo.getCompania()
										+ ", NOMBRE DE ARCHIVO FIRMADO : "
										+ generadorNombre(mensajeLoteMasivo
												.getLoteMasivoComprobante()
												.getClaveAcceso()),
								null,
								mensajeLoteMasivo.getNotificar(),
								null,
								"ENVIO DE MENSAJE, ETAPA DE COMPROBACION "
										+ mensajeLoteMasivo
												.getLoteMasivoComprobante()
												.getClaveAcceso()
										+ ", USUARIO "
										+ mensajeLoteMasivo.getUsuario()
										+ ", COMPAÑIA "
										+ mensajeLoteMasivo.getCompania()
										+ ", NOMBRE DE ARCHIVO FIRMADO : "
										+ generadorNombre(mensajeLoteMasivo
												.getLoteMasivoComprobante()
												.getClaveAcceso()), new Date());
						continuar = true;
					} catch (Throwable e) {
						continuar = false;
						observacion = "ERROR ENVIAR MENSAJE PARA COMPROBACION, CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso()
								+ " - "
								+ e.getMessage();
						e.printStackTrace();
						loggin.registroNovedadesAplicacion(mensajeLoteMasivo
								.getNombreProceso(), mensajeLoteMasivo
								.getClaveNovedad(), mensajeLoteMasivo
								.getUsuario(), nombreMetodo,
								"ERROR ENVIAR MENSAJE PARA COMPROBACION, CLAVE ACCESO LOTE "
										+ mensajeLoteMasivo
												.getLoteMasivoComprobante()
												.getClaveAcceso(), null,
								mensajeLoteMasivo.getNotificar(), e,
								"ERROR ENVIAR MENSAJE PARA COMPROBACION, CLAVE ACCESO LOTE "
										+ mensajeLoteMasivo
												.getLoteMasivoComprobante()
												.getClaveAcceso(), new Date());
					}
				}

			} else {
				continuar = false;
				observacion = "EL ARCHIVO FIRMADO SUPERA EL PESO EN KBS, CONFIGURADO EN EL PARAMETRO "
						+ INombresParametros.PARAMETRO_TAMANIO_ARCHIVO;
				loggin.registroNovedadesAplicacion(
						mensajeLoteMasivo.getNombreProceso(),
						mensajeLoteMasivo.getClaveNovedad(),
						mensajeLoteMasivo.getUsuario(),
						nombreMetodo,
						"VALIDACION DE DATOS, EL ARCHIVO: "
								+ mensajeLoteMasivo.getUrlArchivoXML()
								+ ", TIENE UN PESO SUPERIOR AL ESTABLECIDO EN EL PARAMETRO, "
								+ INombresParametros.PARAMETRO_TAMANIO_ARCHIVO
								+ ", COMPAÑIA"
								+ mensajeLoteMasivo.getCompania()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(),
						null,
						mensajeLoteMasivo.getNotificar(),
						null,
						"VALIDACION DE DATOS, EL ARCHIVO: "
								+ mensajeLoteMasivo.getUrlArchivoXML()
								+ ", TIENE UN PESO SUPERIOR AL ESTABLECIDO EN EL PARAMETRO, "
								+ INombresParametros.PARAMETRO_TAMANIO_ARCHIVO
								+ ", COMPAÑIA"
								+ mensajeLoteMasivo.getCompania()
								+ ", CLAVE ACCESO LOTE "
								+ mensajeLoteMasivo.getLoteMasivoComprobante()
										.getClaveAcceso(), new Date());
			}
		}

		if (!continuar) {
			loggin.inactivarCabeceraLoteMasivoEtapas(mensajeLoteMasivo
					.getLoteMasivoComprobante().getClaveAcceso(), observacion,
					"XF");
		}

	}

	private String generadorNombre(String claveAcceso) {
		return rutaArchivoGenerado + claveAcceso + "-SIGNED.xml";
	}

}
