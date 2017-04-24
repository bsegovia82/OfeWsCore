package com.microservicio.ofe.servicios;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microservicio.ofe.configuracion.ConfiguracionOfeDataColeccionComprobante;
import com.microservicio.ofe.data.OfeDataColeccionComprobante;
import com.microservicio.ofe.data.OfeParametroServicioRegistroComprobante;
import com.microservicio.ofe.repository.RepositoryOfeDataColeccionComprobante;
import com.microservicio.ofe.repository.impl.RepositoryOfeDataColeccionComprobanteSecuenciaImpl;

@Component
public class ServicioOfeDataColeccionComprobante {

	private static final String FIN_TAG_VALIDO_XML = ">";

	private static final String INICIO_TAG_VALIDO_XML = "<";

	private static final String FIN_TAG_INVALIDO_XML = "&gt;";

	private static final String INICIO_TAG_INVALIDO_XML = "&lt;";

	@Autowired
	private OfeInterfazServicioIngresoXML lServicioMetodosAdicionales;
	
	@Autowired
	private RepositoryOfeDataColeccionComprobante lRRepositorioDataColeccionComprobante;

	@Autowired
	private RepositoryOfeDataColeccionComprobanteSecuenciaImpl lRepositorioOfeDataSecuencia;
   
	@Autowired
	private ConfiguracionOfeDataColeccionComprobante lConfiguracion;

	public void ingresarComprobanteFisico(OfeParametroServicioRegistroComprobante lOfeComprobanteFisico) throws Exception 
	{
		ingresarComprobanteFisico(mapeoParametrosDataComprobante(lOfeComprobanteFisico));
	}

	private OfeDataColeccionComprobante mapeoParametrosDataComprobante(
			OfeParametroServicioRegistroComprobante lOfeComprobanteFisico) throws Exception {
		
		OfeDataColeccionComprobante lOfeComproFisico = new OfeDataColeccionComprobante();
		lOfeComproFisico.setLSXmlSerializado(lOfeComprobanteFisico.getlSXmlSerializado().replaceAll(INICIO_TAG_INVALIDO_XML, INICIO_TAG_VALIDO_XML).replaceAll(FIN_TAG_INVALIDO_XML, FIN_TAG_VALIDO_XML));
		lOfeComproFisico.setLSClaveAcceso(lOfeComprobanteFisico.getlSClaveAcceso() == null
				? lConfiguracion.getClaveaccesodefecto() : lOfeComprobanteFisico.getlSClaveAcceso());
		lOfeComproFisico.setLIOrden(lOfeComprobanteFisico.getlIOrden() == null
				? new Integer(lConfiguracion.getOrdendefecto()) : lOfeComprobanteFisico.getlIOrden());
		lOfeComproFisico.setLSSuscriptor(lOfeComprobanteFisico.getlSSuscriptor());
		lOfeComproFisico.setLSAmbiente(lOfeComprobanteFisico.getlSAmbiente());
		lOfeComproFisico.setLSRucEmpresa(lOfeComprobanteFisico.getlSRucEmpresa());
		lOfeComproFisico.setLSNumeroDocumento(lOfeComprobanteFisico.getlSNumeroDocumento());
		cargarDatosAdicionales(lOfeComprobanteFisico, lOfeComproFisico);
		return lOfeComproFisico;
	}

	

	public void ingresarComprobanteFisico(OfeDataColeccionComprobante lOfeComprobanteFisico) throws Exception {
		cargarDatosGeneralesComprobanteFisico(lOfeComprobanteFisico);
		asignacionClaveAcceso(lOfeComprobanteFisico);
		lRRepositorioDataColeccionComprobante.insert(lOfeComprobanteFisico);

	}

	

	
	private void cargarDatosGeneralesComprobanteFisico(OfeDataColeccionComprobante lOfeComprobanteFisico) {
		lOfeComprobanteFisico
				.setLLID(lRepositorioOfeDataSecuencia.getNextSequenceId(lConfiguracion.getNombresecuencia()));
		lOfeComprobanteFisico.setLDFechaRegistro(new Date());
		lOfeComprobanteFisico.setLSEstadoProceso(lConfiguracion.getEstdoinicial());
		lOfeComprobanteFisico.setLIProceso(balancearCargaProcesos());
	}

	private Integer balancearCargaProcesos() {
		return (int) (Math.random() * Integer.parseInt(lConfiguracion.getNumeroprocesosfin()))
				+ Integer.parseInt(lConfiguracion.getNumeroprocesosini());
	}
	
	private void cargarDatosAdicionales(OfeParametroServicioRegistroComprobante lOfeComprobanteFisico,
			OfeDataColeccionComprobante lOfeComproFisico) throws Exception
	{
		lServicioMetodosAdicionales.cargarDatosAdicionales(lOfeComprobanteFisico, lOfeComproFisico);
	}
	
	private void asignacionClaveAcceso(OfeDataColeccionComprobante lOfeComprobanteFisico) throws Exception
	{
		lServicioMetodosAdicionales.asignacionClaveAcceso(lOfeComprobanteFisico);
	}
	
	public void validacionesDatosBasicas(OfeParametroServicioRegistroComprobante lParametro) throws Exception
	{
		String lsXml = lParametro.getlSXmlSerializado();
		lsXml=lsXml==null?"":lsXml;
		if (lsXml.length() < 3)
			throw new Exception("EL XML NO PUEDE SER NULO");
	}

	

}
