package com.microservicio.ofe.configuracion;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix="aplicacion")
@Data
public class ConfiguracionOfeDataColeccionComprobante 
{
	private String nombresecuencia;
	private String estdoinicial;
	private String estdoregistroinsert;
	private String numeroprocesosini;
	private String numeroprocesosfin;
	private String claveaccesodefecto;
	private String ordendefecto;
}
   