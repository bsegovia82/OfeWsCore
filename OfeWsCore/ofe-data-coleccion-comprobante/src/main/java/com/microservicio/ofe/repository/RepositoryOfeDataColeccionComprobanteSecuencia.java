package com.microservicio.ofe.repository;

import com.microservicio.ofe.excepciones.ExcepcionMicroservicioOfe;

public interface RepositoryOfeDataColeccionComprobanteSecuencia {

	Long getNextSequenceId(String lSClave) throws ExcepcionMicroservicioOfe;
	
}
