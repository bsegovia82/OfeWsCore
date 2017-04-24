package com.corlasosa.com;

public class Test {

	public static void main(String[] args) {
		
		System.out.println(extraePorcentaje(",20.0,2.0,10.0"));
	}
	
	public static String extraePorcentaje(String pValor) {
		  String lResultado = new String();
		  if (pValor == null)
		   return "0%";

		  pValor = pValor.replace(",", "");
		  pValor = pValor.replace(".0", "&");
		  //pValor = pValor.replace("0", "");
		  String datos[] = pValor.split("&");
		  for (int i = 0; i <= datos.length - 1; i++) {
		   if (datos[i] != null) {
		    lResultado = lResultado + datos[i] + "%";
		   } else {
		    lResultado = lResultado + "0%";
		   }
		  }
		  return lResultado;
		 }

}
