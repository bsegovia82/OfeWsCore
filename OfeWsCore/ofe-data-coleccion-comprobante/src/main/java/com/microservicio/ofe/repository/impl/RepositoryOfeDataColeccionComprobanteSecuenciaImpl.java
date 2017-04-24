package com.microservicio.ofe.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.microservicio.ofe.data.OfeDataColeccionComprobanteSecuencia;
import com.microservicio.ofe.excepciones.ExcepcionMicroservicioOfe;
import com.microservicio.ofe.repository.RepositoryOfeDataColeccionComprobanteSecuencia;

@Repository
public class RepositoryOfeDataColeccionComprobanteSecuenciaImpl implements RepositoryOfeDataColeccionComprobanteSecuencia
{

	@Autowired
	private MongoOperations lMOperacion;
	
	public Long getNextSequenceId(String lSClave) throws ExcepcionMicroservicioOfe {
		
		Query lQconsultaSecuencia = new Query(Criteria.where("_id").is(lSClave));
		Update lUSentenciaActualizacion = new Update();
		lUSentenciaActualizacion.inc("lLSec", 1);
		FindAndModifyOptions lFacciones = new FindAndModifyOptions();
		lFacciones.returnNew(true);
		OfeDataColeccionComprobanteSecuencia lOfeSecuencia =
				lMOperacion.findAndModify(lQconsultaSecuencia, lUSentenciaActualizacion, lFacciones, OfeDataColeccionComprobanteSecuencia.class);
		
		if (lOfeSecuencia == null) {
			throw new ExcepcionMicroservicioOfe("Imposible encontrar la secuencia para : " + lSClave);
		  }

		  return lOfeSecuencia.getLLSec();
		
	}

}
