package com.producto.comprobanteselectronicos.cliente;

public class ClenteCosumoWsErp {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		WsFELocator serviceLocator = null;
		WsFESoap port = null;
		WsFE countryData = null;
		try {
			serviceLocator = new WsFELocator();
			port = serviceLocator.getWsFESoap();
			int respuesta = port.updateAutorizacion("asd", "01", "sd", "sd","s", "d", "s", "d");

			System.out.println(respuesta);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int updateAutorizacion(String dataareaid, String tipocomprobante,
			String establecimientoEmision, String secuencial, String status,
			String autorizacion, String motivo, String fecharecepcion) {

		WsFELocator serviceLocator = null;
		WsFESoap port = null;
		int respuesta = -1;
		String autorizacionStr = autorizacion==null?"":autorizacion;
		try {
			serviceLocator = new WsFELocator();
			port = serviceLocator.getWsFESoap();
			System.out.println(dataareaid+" - "+ tipocomprobante+" - "+
					establecimientoEmision+" - "+ secuencial+" - "+ status+" - "+ autorizacion+" - "+
					motivo+" - "+ fecharecepcion);
			respuesta = port.updateAutorizacion(dataareaid, tipocomprobante,
					establecimientoEmision, secuencial, status, autorizacionStr,
					motivo, fecharecepcion);

			System.out.println(respuesta);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return respuesta;
	}
}
