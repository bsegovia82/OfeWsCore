package com.microservicio.ofe.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.microservicio.ofe.data.OfeDataColeccionComprobante;

public interface  RepositoryOfeDataColeccionComprobante extends MongoRepository<OfeDataColeccionComprobante, Long> 
{

	OfeDataColeccionComprobante findFirstBylSClaveAcceso(String lSClaveAcceso);

    @Query("{lSClaveAcceso:'?0'}")
    OfeDataColeccionComprobante findCustomBylSClaveAcceso(String lSClaveAcceso);
}
