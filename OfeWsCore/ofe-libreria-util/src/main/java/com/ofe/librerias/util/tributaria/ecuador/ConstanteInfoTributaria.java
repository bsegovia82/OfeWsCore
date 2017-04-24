package com.ofe.librerias.util.tributaria.ecuador;

public interface ConstanteInfoTributaria {

	/**
	 * Pruebas
	 */
	public final static String TIPO_AMBIENTE_PRUEBAS = "1";

	/**
	 * Produccion
	 */
	public final static String TIPO_AMBIENTE_PRODUCCION = "2";

	/**
	 * Emision Normal
	 */
	public final static String TIPO_EMISION_NORMAL = "1";
	/**
	 * Emision por Indisponibilidad del Sistema
	 */
	//Es para las claves de contingencia
	public final static String TIPO_EMISION_INDISPONIBILIDAD_SISTEMA = "2";

	/**
	 * FACTURA
	 */
	public final static String TIPO_COMPROBANTE_FACTURA = "01";
	/**
	 * NOTA DE CRDITO
	 */
	public final static String TIPO_COMPROBANTE_NOTA_CREDITO = "04";
	/**
	 * NOTA DE DBITO
	 */
	public final static String TIPO_COMPROBANTE_NOTA_DEBITO = "05";
	/**
	 * GUiA DE REMISIN
	 */
	public final static String TIPO_COMPROBANTE_GUIA_REMISION = "06";
	/**
	 * COMPROBANTE DE RETENCIN
	 */
	public final static String TIPO_COMPROBANTE_RETENCION = "07";

}