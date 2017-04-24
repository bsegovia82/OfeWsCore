package com.producto.comprobanteselectronicos.servicio.jms.cdi;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import com.producto.comprobanteselectronicos.generales.ConstanteInfoTributaria;
import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.MensajeIndividual;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;

@Named
public class ServicioClienteJMSMensajeIndividualCDI {

	
	@Resource(lookup = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(lookup = INombresParametros.NOMBRE_COLA_FACUTURAS_INDIVIDUAL)
	private Queue feColaFacturaLoteMasivo;

	@Resource(lookup = INombresParametros.NOMBRE_COLA_RETENCION_INDIVIDUAL)
	private Queue feColaRetencionLoteMasivo;

	@Resource(lookup = INombresParametros.NOMBRE_COLA_GUIAS_INDIVIDUAL)
	private Queue feColaGuiaLoteMasivo;

	@Resource(lookup = INombresParametros.NOMBRE_NOTA_DEBITO_INDIVIDUAL)
	private Queue feColaNotaDebitoLoteMasivo;

	@Resource(lookup = INombresParametros.NOMBRE_COLA_NOTA_CREDITO_INDIVIDUAL)
	private Queue feColaNotaCreditoLoteMasivo;
	
	@Resource(lookup = INombresParametros.NOMBRE_COLA_LOTE_VIRTUAL)
	private Queue feColaLotesVirtuales;
	
	
	public void registrarMensajeIndividual(MensajeIndividual mensajeIndividual) 
	throws Throwable
	{
		try {
			cargarDatosAdicionalesMensaje(mensajeIndividual);
			enviarMensajeIndividual(mensajeIndividual);

		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void cargarDatosAdicionalesMensaje(
			MensajeIndividual mensajeIndividual) {

	}

	private void enviarLoteVirtualMensajeIndividual(ArrayList<MensajeIndividual> litaMensajeIndividual)
			throws Throwable {
			CoreClienteJMSCDI.envioMensajeJMS(
					connectionFactory.createConnection(),
					feColaLotesVirtuales, litaMensajeIndividual);
	}
	
	private void enviarMensajeIndividual(MensajeIndividual mensajeIndividual)
			throws Throwable {
		switch (mensajeIndividual.getTipoDocumento()) {
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA:
			CoreClienteJMSCDI.envioMensajeJMS(
					connectionFactory.createConnection(),
					feColaFacturaLoteMasivo, mensajeIndividual);
			break;
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION:
			CoreClienteJMSCDI.envioMensajeJMS(
					connectionFactory.createConnection(),
					feColaRetencionLoteMasivo, mensajeIndividual);
			break;
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION:
			CoreClienteJMSCDI.envioMensajeJMS(
					connectionFactory.createConnection(), feColaGuiaLoteMasivo,
					mensajeIndividual);
			break;
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO:
			CoreClienteJMSCDI.envioMensajeJMS(
					connectionFactory.createConnection(),
					feColaNotaDebitoLoteMasivo, mensajeIndividual);
			break;
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO:
			CoreClienteJMSCDI.envioMensajeJMS(
					connectionFactory.createConnection(),
					feColaNotaCreditoLoteMasivo, mensajeIndividual);
			break;
		}
	}

	public void registrarLoteVirtualMensajeIndividual(
			ArrayList<MensajeIndividual> mensajeIndividual) throws Throwable {
		try {
			enviarLoteVirtualMensajeIndividual(mensajeIndividual);
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}
}
