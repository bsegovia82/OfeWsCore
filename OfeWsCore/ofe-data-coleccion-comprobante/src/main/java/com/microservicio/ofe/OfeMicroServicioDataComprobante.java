package com.microservicio.ofe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OfeMicroServicioDataComprobante //implements CommandLineRunner
{
	


	public static void main(String[] args) {
		SpringApplication.run(OfeMicroServicioDataComprobante.class, args);
	}

//	public void run(String... arg0) throws Exception {
//		
//		OfeDataColeccionComprobante lOComprobanteElectronico = new OfeDataColeccionComprobante();
//		lOComprobanteElectronico.setLLID(lRepositorioOfeDataSecuencia.getNextSequenceId("ofe_data_coleccion_comprobante"));
//		lOComprobanteElectronico.setLDFechaActualizacion(new Date());
//		lOComprobanteElectronico.setLSClaveAcceso("12345778933150");
//		lRRepositorioDataColeccionComprobante.insert(lOComprobanteElectronico);
//		
//	}
}
