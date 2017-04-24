package com.microservicio.ofe.servicios;

import com.microservicio.ofe.data.OfeDataColeccionComprobante;
import com.microservicio.ofe.data.OfeParametroServicioRegistroComprobante;

public interface OfeInterfazServicioIngresoXML 
{
	public void cargarDatosAdicionales(OfeParametroServicioRegistroComprobante lOfeComprobanteFisico,
			OfeDataColeccionComprobante lOfeComproFisico) throws Exception;
	
	public void asignacionClaveAcceso(OfeDataColeccionComprobante lOfeComprobanteFisico)throws Exception;
	
}
