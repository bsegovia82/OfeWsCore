package com.ofe.librerias.util.tributaria.ecuador;

import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

public class GeneradorClaveAcceso {

	public static final int LIMITE_CODIGO_NUMERICO = 99999999;
	public static final int LIMITE_MAXIMO_NUMERO_COMPROBANTE = 999999999;
	public static final int LIMITE_SERIE = 999999;
	public static final int LIMITE_ESTABLECIMIENTO = 999;
	public static final BigInteger LIMITE_CODIGO_NUMERICO_LOTE_MASIVO = new BigInteger("99999999999999999999");

	private GeneradorClaveAcceso() {
	}
	
	public static String generarClaveDeAccesoLoteMasivoTipoComprobanteFacturaTipoAmbientePruebaTipoEmisionNormal(
			String ruc, Long establecimiento,
			BigInteger codigoNumerico)
	{
		return generarClaveDeAccesoLoteMasivo(
				ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA,ruc,ConstanteInfoTributaria.TIPO_AMBIENTE_PRUEBAS,
				establecimiento, codigoNumerico, ConstanteInfoTributaria.TIPO_EMISION_NORMAL);
	}

	public static String generarClaveDeAccesoLoteMasivo(
			String tipoComprobante,
			String ruc,
			String tipoAmbiente,
			Long establecimiento,
			BigInteger codigoNumerico,
			String tipoEmision){

		StringBuilder claveAcceso=new StringBuilder();
		
		String fechaEmision=DateFormatUtils.format(new Date(), "ddMMyyyy");
		
		claveAcceso.append(fechaEmision);
		claveAcceso.append(tipoComprobante);
		
		if(ruc==null){
			throw new IllegalArgumentException("El Ruc no debe ser nulo");
		}
		if(ruc.length()!=13){
			throw new IllegalArgumentException("El Ruc debe ser de 13 digitos");
		}
		claveAcceso.append(ruc);
		claveAcceso.append(tipoAmbiente);
		
		if(establecimiento==null){
			throw new IllegalArgumentException("El establecimiento no debe ser nulo");
		}
		if(establecimiento>LIMITE_ESTABLECIMIENTO){
			throw new IllegalArgumentException("El establecimiento no debe ser mayor que "+LIMITE_ESTABLECIMIENTO);
		}
		claveAcceso.append(StringUtils.leftPad(ObjectUtils.toString(establecimiento) , 3, "0"));
		
		if(codigoNumerico==null){
			throw new IllegalArgumentException("El codigo numerico no debe ser nulo");
		}
		if(codigoNumerico.compareTo(LIMITE_CODIGO_NUMERICO_LOTE_MASIVO)>=1){
			throw new IllegalArgumentException("El codigo numerico no debe ser mayor que "+LIMITE_CODIGO_NUMERICO_LOTE_MASIVO);
		}
		claveAcceso.append(StringUtils.leftPad(ObjectUtils.toString(codigoNumerico) , 20, "0"));
		claveAcceso.append(tipoEmision);
		claveAcceso.append(calcularDigitoVerificador(claveAcceso.toString()));

		return claveAcceso.toString();
	}
	
	public static String generarClaveDeAccesoTipoComprobanteFacturaTipoAmbientePruebaTipoEmisionNormal(
			String ruc,Long serie,Long numeroComprobante,Long codigoNumerico,String fechaEmision){
		return generarClaveDeAcceso(fechaEmision,ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA,ruc,ConstanteInfoTributaria.TIPO_AMBIENTE_PRUEBAS,
				serie,numeroComprobante,codigoNumerico,ConstanteInfoTributaria.TIPO_EMISION_NORMAL);
	}
	
	public static String generarClaveDeAcceso(String fechaEmision,String tipoComprobante,String ruc,String tipoAmbiente,Long serie,Long numeroComprobante,Long codigoNumerico,
			String tipoEmision){
		
		StringBuilder claveAcceso=new StringBuilder();
		
		claveAcceso.append(fechaEmision);
		claveAcceso.append(tipoComprobante);
		
		if(ruc==null){
			throw new IllegalArgumentException("El Ruc no debe ser nulo");
		}
		if(ruc.length()!=13){
			throw new IllegalArgumentException("El Ruc debe ser de 13 digitos");
		}
		claveAcceso.append(ruc);
		claveAcceso.append(tipoAmbiente);
		
		if(serie==null){
			throw new IllegalArgumentException("La Serie no debe ser nulo");
		}
		if(serie>LIMITE_SERIE){
			throw new IllegalArgumentException("La Serie no debe ser mayor que "+LIMITE_SERIE);
		}
		claveAcceso.append(StringUtils.leftPad(ObjectUtils.toString(serie) , 6, "0"));
		
		if(numeroComprobante==null){
			throw new IllegalArgumentException("El numero del comprobante no debe ser nulo");
		}
		if(numeroComprobante>LIMITE_MAXIMO_NUMERO_COMPROBANTE){
			throw new IllegalArgumentException("El numero del comprobante no debe ser mayor que "+LIMITE_MAXIMO_NUMERO_COMPROBANTE);
		}
		claveAcceso.append(StringUtils.leftPad(ObjectUtils.toString(numeroComprobante) , 9, "0"));
		
		if(codigoNumerico==null){
			throw new IllegalArgumentException("El codigo numerico no debe ser nulo");
		}
		if(codigoNumerico>LIMITE_CODIGO_NUMERICO){
			throw new IllegalArgumentException("El codigo numerico debe ser mayor que "+LIMITE_CODIGO_NUMERICO);
		}
		claveAcceso.append(StringUtils.leftPad(ObjectUtils.toString(codigoNumerico) , 8, "0"));
		claveAcceso.append(tipoEmision);
		claveAcceso.append(calcularDigitoVerificador(claveAcceso.toString()));

		return claveAcceso.toString();
	}
	
	public static String calcularDigitoVerificador(String cadena) {
		cadena=StringUtils.reverse(cadena);
		int pivote = 2;
		int longitudCadena = cadena.length();
		int cantidadTotal = 0;
		int b = 1;
		for (int i = 0; i < longitudCadena; i++) {
			if (pivote == 8) {
				pivote = 2;
			}
			int temporal = Integer.parseInt("" + cadena.substring(i, b));
			b++;
			temporal *= pivote;
			pivote++;
			cantidadTotal += temporal;
		}
		cantidadTotal = 11 - (cantidadTotal % 11);
		String digitoVerificador = ((cantidadTotal == 11) ? "0" : ((cantidadTotal == 10) ? "1" : Integer.toString(cantidadTotal)));
		return digitoVerificador;
	}
	
	public static void main (String args[]){
		String clave =generarClaveDeAcceso("01092016","01","1791415132001","1",new Long("061051"),new Long("000002024"),12345678L,
				"1");
		System.out.println(clave);
	}

}
