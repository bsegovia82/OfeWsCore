package com.microservicio.ofe.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Document(collection = "ofe_comprobantes_fisicos_sec")
public class OfeDataColeccionComprobanteSecuencia {
	@Id
	private String lSId;
	private Long lLSec;
	
	
	
}
