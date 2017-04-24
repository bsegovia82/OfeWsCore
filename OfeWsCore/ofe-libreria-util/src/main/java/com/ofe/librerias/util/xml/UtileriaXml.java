package com.ofe.librerias.util.xml;

public class UtileriaXml {

	public static String obtenerValorTags(String lsXml, String lsTagInicial, String lsTagFinal) throws Exception {
		lsXml = lsXml == null ? "" : lsXml;
		if (lsXml.length() < (lsTagInicial.length() + lsTagFinal.length()))
			throw new Exception("XML INVALIDO, LOS TAMANIOS DE LOS TAGS SUPERAN AL XML");
		return lsXml.substring(lsXml.indexOf(lsTagInicial) + lsTagInicial.length(), lsXml.indexOf(lsTagFinal));
	}

	public static String insertarValorTag(String lsTagFinal, String lsValores, String lsXml) {
		return new StringBuilder(lsXml).insert(lsXml.indexOf(lsTagFinal) - 1, lsValores).toString();   
	}
}
