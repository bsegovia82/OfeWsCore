package com.producto.comprobanteselectronicos.eao.util;

import java.io.IOException;
import java.util.Properties;

public class ServicioPropertie {
	
	private static final Properties prop;
	
	static {
		
		 prop = new Properties();

		 try {
			prop.load(ServicioPropertie.class.getClassLoader().getResourceAsStream("application.properties") );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Valiste como siempre");
			e.printStackTrace();
		}
	}
	
	public static String getPropiedad(String clave)
	{
		return prop.getProperty(clave);
	}

}
