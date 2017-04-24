package com.microservicio.ofe.util;

public interface EtiquetasXml {
	final String TAG_FIN_COD_DOC = "</codDoc>";
	final String TAG_INICIO_COD_DOC = "<codDoc>";
	final String TAG_INICIO_INFORMACION_ADICIONAL = "<infoAdicional>";
	final String TAG_FIN_INFORMACION_ADICIONAL = "</infoAdicional>";
	final String SEG_XML_CAMPO_ADICIONAL = "<campoAdicional nombre=\"@CLAVE@\">@VALOR@</campoAdicional>";
	final String TAG_INICIO_CLAVE_ACCESO = "<claveAcceso>";
	final String TAG_FIN_CLAVE_ACCESO = "</claveAcceso>";
	final String TAG_INICIO_FECHA_EMISION = "<fechaEmision>";
	final String TAG_FIN_FECHA_EMISION = "</fechaEmision>";
	final String TAG_INICIO_RUC = "<ruc>";
	final String TAG_FIN_RUC = "</ruc>";
	final String TAG_INICIO_AMBIENTE = "<ambiente>";
	final String TAG_FIN_AMBIENTE = "</ambiente>";

	final String TAG_INICIO_ESTABLECIMIENTO = "<estab>";
	final String TAG_FIN_ESTABLECIMIENTO = "</estab>";
	final String TAG_INICIO_PTO_EMISION = "<ptoEmi>";
	final String TAG_FIN_PTO_EMISION = "</ptoEmi>";
	final String TAG_INICIO_SECUENCIAL = "<secuencial>";
	final String TAG_FIN_SECUENCIAL = "</secuencial>";
	final String TAG_INICIO_EMISION = "<tipoEmision>";
	final String TAG_FIN_EMISION = "<tipoEmision>";
}
