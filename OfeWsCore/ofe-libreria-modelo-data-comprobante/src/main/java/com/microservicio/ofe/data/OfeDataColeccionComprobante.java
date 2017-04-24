package com.microservicio.ofe.data;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "ofe_comprobantes_fisicos")
public class OfeDataColeccionComprobante {
	@Id
	private Long lLID;
	@Indexed(unique = true)
	private String lSClaveAcceso;
	private String lSEstadoRegistro;
	private String lSEstadoProceso;
	private String lSSuscriptor;
	private Integer lIOrden;
	private String lSRucEmpresa;
	@Indexed(unique = false)
	private Date lDFechaRegistro;
	private Date lDFechaActualizacion;
	@Indexed(unique = false)
	private Integer lIProceso;
	private String lSXmlSerializado;
	private String lSNumeroDocumento;
	@Indexed(unique = false)
	private String lSAmbiente;
}
