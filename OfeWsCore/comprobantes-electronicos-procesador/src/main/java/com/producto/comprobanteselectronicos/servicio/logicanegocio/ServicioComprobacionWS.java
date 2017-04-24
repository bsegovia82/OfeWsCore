package com.producto.comprobanteselectronicos.servicio.logicanegocio;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.Stateless;

import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;



@Stateless
public class ServicioComprobacionWS {
	private static boolean disponible(String urlWS, int timeUp) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlWS);
			connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("Connection", "close");
			connection.setConnectTimeout(timeUp);
			connection.connect();
			if (connection.getResponseCode() == 200) {
				return true;
			} else
				return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (connection!=null)
			{
				connection.disconnect();
			}
		}
	}
	
	public boolean servicioComprobacionLoteMasivoDesarrolloDisponible()
	{
		return disponible(INombresParametros.URL_WS_COMPROBACION_MASIVO_PRUEBAS, Integer.parseInt(INombresParametros.URL_WS_TIME_OUT));
	}
	
	public boolean servicioAutorizacionLoteMasivoDesarrolloDisponible()
	{
		return disponible(INombresParametros.URL_WS_AUTORIZACION_MASIVO_PRUEBAS, Integer.parseInt(INombresParametros.URL_WS_TIME_OUT));
	}
	
	public static boolean servicioComprobacionIndividual(String url)
	{
		return disponible(url, Integer.parseInt(INombresParametros.URL_WS_TIME_OUT));
	}
	
	public static boolean servicioAutorizacionIndividual(String url)
	{
		return disponible(url, Integer.parseInt(INombresParametros.URL_WS_TIME_OUT));
	}
	
}
