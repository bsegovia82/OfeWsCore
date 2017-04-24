package com.microservicio.ofe.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.microservicio.ofe.data.OfeParametroServicioRegistroComprobante;
import com.microservicio.ofe.data.OfeRespuestaServicioRegistroComprobante;
import com.microservicio.ofe.servicios.ServicioOfeDataColeccionComprobante;

@Controller
@RequestMapping("/ofe_data_coleccion_comprobante")
public class RestOfeDataColeccionComprobante {

	@Autowired
	private ServicioOfeDataColeccionComprobante lServicio;

	@RequestMapping(method = RequestMethod.POST, path = "/registrar", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody OfeRespuestaServicioRegistroComprobante registrarComprobanteFisico(
			@RequestBody(required = true) OfeParametroServicioRegistroComprobante comprobante) {

		OfeRespuestaServicioRegistroComprobante lResultado = new OfeRespuestaServicioRegistroComprobante();

		System.out.println(
				"-------------------------------------------------------------------------------------------------------");
		
		try {
			System.out.println("***********VALIDANDO");
			lServicio.validacionesDatosBasicas(comprobante);
		} catch (Exception lError) {
			lResultado.setCodigoRespuesta(-2L);
			lResultado.setLMensajeRespuesta(lError.getMessage());
			lError.printStackTrace();
			return lResultado;
		}
		
		try {
			
			System.out.println("***********REGISTRANDO");
			lServicio.ingresarComprobanteFisico(comprobante);
			lResultado.setCodigoRespuesta(1L);
			lResultado.setLMensajeRespuesta("EJECUCION CORRECTA");
		} catch (Exception lError) {
			lError.printStackTrace();
			lResultado.setCodigoRespuesta(-1L);
			lResultado.setLMensajeRespuesta(lError.getMessage());
		}
		System.out.println(
				"-------------------------------------------------------------------------------------------------------");
		return lResultado;
	}

}
