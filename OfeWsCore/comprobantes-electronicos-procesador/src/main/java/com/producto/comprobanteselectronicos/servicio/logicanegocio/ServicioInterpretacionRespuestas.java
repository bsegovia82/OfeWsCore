package com.producto.comprobanteselectronicos.servicio.logicanegocio;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;




import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.Autorizacion;
import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.Mensaje;
import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.ObjectFactory;
import com.producto.comprobanteselectronicos.comprobador.desarrollo.masivo.Comprobante;
import com.producto.comprobanteselectronicos.logs.modelo.entidades.DetalleDocumentosLoteMasivo;
import com.producto.comprobanteselectronicos.servicio.logs.ILogging;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;

@Stateless
public class ServicioInterpretacionRespuestas {

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_LOGGIN)
	private ILogging loggin;

	public static void main (String args[])
	{
		String evaluar = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<factura id=\"comprobante\" version=\"1.1.2\">";
		System.out.println(evaluar.substring(evaluar.indexOf("?>")+2));
	}
	
	@Asynchronous
	public void interpretarAutorizaciones(List<Autorizacion> autorizaciones,
			String claveLote) {
		for (Autorizacion autorizacion : autorizaciones) {

			String claveAccesoComprobante = autorizacion.getClaveAcceso();
			String respuestaAutorizacion = autorizacion.getEstado() + " - "
					+ autorizacion.getAmbiente();
			String numeroAutorizacion = autorizacion.getNumeroAutorizacion();
			Date fechaAutorizacion = autorizacion.getFechaAutorizacion()
					.toGregorianCalendar().getTime();
			String estado = autorizacion.getEstado().equals("AUTORIZADO") ? "A"
					: "XA";
			String observacion = "";
			String mensajeError = "";
			
			if (estado.equals("A")) {
				observacion = "AUTORIZACION EXITOSA, POR FAVOR REVISE EL NUMERO DE AUTORIZACION";
				DetalleDocumentosLoteMasivo det= loggin.obtenerDetalleComprobantePorClaveAcceso(claveAccesoComprobante);
				String xmlComprobante = det.getXmlComprobante().substring(det.getXmlComprobante().indexOf("?>")+2);
				autorizacion.setComprobante(xmlComprobante);
			} else {
				observacion = "ERROR AL REALIZAR LA AUTORIZACION, REVISE EL XML CON LOS MENSAJES";
			}
			String xmlAutorizacion = generarXMLAutorizacion(autorizacion);
			for (Mensaje mensaje : autorizacion.getMensajes().getMensaje()) {
				mensajeError = mensajeError + mensaje.getIdentificador() + "|"
						+ mensaje.getMensaje() + "|"
						+ mensaje.getInformacionAdicional() + "|"
						+ mensaje.getTipo() + "\n";
			}

			// Actualizar el detalle de documentos
			loggin.actualizacionDetallesPorAutorizacion(claveLote,
					claveAccesoComprobante, respuestaAutorizacion,
					numeroAutorizacion, fechaAutorizacion, xmlAutorizacion,
					estado, observacion, mensajeError);
			// Actualizar los registros tomados
		}
	}

	@Asynchronous
	public void interpretarComprobaciones(List<Comprobante> comprobante,
			String claveLote, String estado) {
		String mensajeError = "";
		for (Comprobante com : comprobante) {
			for (com.producto.comprobanteselectronicos.comprobador.desarrollo.masivo.Mensaje mensaje : com
					.getMensajes().getMensaje()) {
				mensajeError = mensajeError + mensaje.getIdentificador() + "|"
						+ mensaje.getMensaje() + "|"
						+ mensaje.getInformacionAdicional() + "|"
						+ mensaje.getTipo() + "\n";
			}
			
			loggin.actualizacionPorErrorComprobacion(claveLote,
					com.getClaveAcceso(), mensajeError,
					"ERROR EN LA COMPROBACION, POR FAVOR REVISAR LOS MENSAJES", estado);
			// Actualizar los registros tomados estos con error
		}
	}

	private String generarXMLAutorizacion(Autorizacion autorizacion) {
		try {
			StringWriter str = new StringWriter();
			ObjectFactory objectFactoryAutorizacion = new ObjectFactory();
			JAXBContext contextAutorizacion = JAXBContext
					.newInstance("com.producto.comprobanteselectronicos.autorizador.desarrollo");
			JAXBElement<Object> eRespuestaComprobante = objectFactoryAutorizacion
					.createRespuestaAutorizacion(autorizacion);
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
