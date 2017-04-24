package com.producto.comprobanteselectronicos.servicio.jms.cdi;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

public class CoreClienteJMSCDI {/*
							 * Connection conexion = null; Session sesion =
							 * null;
							 * cargarDatosAdicionalesMensaje(mensajeLoteMasivo);
							 * try {
							 * 
							 * conexion = connectionFactory.createConnection();
							 * conexion.start(); sesion =
							 * conexion.createSession(false,
							 * Session.AUTO_ACKNOWLEDGE); MessageProducer
							 * producer = sesion
							 * .createProducer(feColaFacturaLoteMasivo);
							 * producer
							 * .setDeliveryMode(DeliveryMode.NON_PERSISTENT);
							 * ObjectMessage mensaje = sesion
							 * .createObjectMessage(mensajeLoteMasivo);
							 * producer.send(mensaje); } catch (Throwable error)
							 * { error.printStackTrace(); } finally { try { if
							 * (sesion != null) sesion.close(); if (conexion !=
							 * null) conexion.close(); } catch (Throwable error)
							 * { error.printStackTrace(); } }
							 */

	public static void envioMensajeJMS(Connection conexion,
			Queue feColaFacturaLoteMasivo, Serializable obj) throws Throwable {
		Session sesion = null;
		try {

			conexion.start();
			sesion = conexion.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = sesion
					.createProducer(feColaFacturaLoteMasivo);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			ObjectMessage mensaje = sesion.createObjectMessage(obj);
			producer.send(mensaje);
			producer.close();
		} finally {
			try {
				if (sesion != null)
					sesion.close();
				if (conexion != null)
					conexion.close();
			} catch (Throwable error) {
				error.printStackTrace();
			}
		}
	}

}
