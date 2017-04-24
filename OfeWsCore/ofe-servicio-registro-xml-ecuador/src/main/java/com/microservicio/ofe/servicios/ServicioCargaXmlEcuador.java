package com.microservicio.ofe.servicios;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microservicio.ofe.configuracion.ConfiguracionOfeDataColeccionComprobante;
import com.microservicio.ofe.data.OfeDataColeccionComprobante;
import com.microservicio.ofe.data.OfeParametroServicioRegistroComprobante;
import com.microservicio.ofe.data.OfeParametroServicioRegistroComprobanteClaveValor;
import com.microservicio.ofe.util.EtiquetasXml;
import com.ofe.librerias.util.tributaria.ecuador.GeneradorClaveAcceso;
import com.ofe.librerias.util.xml.UtileriaXml;

@Component
public class ServicioCargaXmlEcuador implements OfeInterfazServicioIngresoXML, EtiquetasXml {

	private static final String PATRON_VALOR = "@VALOR@";
	private static final String PATRON_CLAVE = "@CLAVE@";
	@Autowired
	private ConfiguracionOfeDataColeccionComprobante lConfiguracion;

	private String generarDatosAdicionalesXML(List<OfeParametroServicioRegistroComprobanteClaveValor> listaClaveValor) {
		listaClaveValor = listaClaveValor == null ? new ArrayList<>() : listaClaveValor;
		String lResultado = "";
		for (OfeParametroServicioRegistroComprobanteClaveValor claveValor : listaClaveValor) {
			lResultado += SEG_XML_CAMPO_ADICIONAL.replace(PATRON_CLAVE, claveValor.getLsClave()).replace(PATRON_VALOR,
					claveValor.getLsValor());
		}
		return lResultado;
	}

	@Override
	public void cargarDatosAdicionales(OfeParametroServicioRegistroComprobante lOfeComprobanteFisico,
			OfeDataColeccionComprobante lOfeComproFisico) throws Exception {

		String lSXml = lOfeComproFisico.getLSXmlSerializado();
		String lSDatosAdicionales = generarDatosAdicionalesXML(lOfeComprobanteFisico.getlDatosAdicionales());
		if (lSDatosAdicionales.length() > 3)
			lOfeComproFisico.setLSXmlSerializado(
					UtileriaXml.insertarValorTag(TAG_FIN_INFORMACION_ADICIONAL, lSDatosAdicionales, lSXml));

	}

	@Override
	public void asignacionClaveAcceso(OfeDataColeccionComprobante lOfeComprobanteFisico) throws Exception {
		String lClaveAcceso = lOfeComprobanteFisico.getLSClaveAcceso();
		String lXml = lOfeComprobanteFisico.getLSXmlSerializado();
		String lsClaveAcceso = "";
		if (lClaveAcceso.equals(lConfiguracion.getClaveaccesodefecto()))
			try {
				lsClaveAcceso = UtileriaXml.obtenerValorTags(lXml, TAG_INICIO_CLAVE_ACCESO, TAG_FIN_CLAVE_ACCESO);
				if (lsClaveAcceso.length() < 3)
					throw new Exception("No existe clave de acceso registrado");
			} catch (Exception lError) {
				lError.printStackTrace();
				lsClaveAcceso = generarClaveAcceso(lXml);
				lXml = UtileriaXml.insertarValorTag(TAG_FIN_CLAVE_ACCESO, lsClaveAcceso,
						lOfeComprobanteFisico.getLSXmlSerializado());
			}
		lOfeComprobanteFisico.setLSClaveAcceso(lsClaveAcceso);
		lOfeComprobanteFisico.setLSXmlSerializado(lXml);
	}

	private String generarClaveAcceso(String lXml) throws Exception {
		try {
			return GeneradorClaveAcceso.generarClaveDeAcceso(
					UtileriaXml.obtenerValorTags(lXml, TAG_INICIO_FECHA_EMISION, TAG_FIN_FECHA_EMISION).replace("/",
							""),
					obtenerTipoDocumento(lXml), UtileriaXml.obtenerValorTags(lXml, TAG_INICIO_RUC, TAG_FIN_RUC),
					UtileriaXml.obtenerValorTags(lXml, TAG_INICIO_AMBIENTE, TAG_FIN_AMBIENTE),
					Long.valueOf(UtileriaXml.obtenerValorTags(lXml, TAG_INICIO_ESTABLECIMIENTO, TAG_FIN_ESTABLECIMIENTO)
							+ UtileriaXml.obtenerValorTags(lXml, TAG_INICIO_PTO_EMISION, TAG_FIN_PTO_EMISION)),
					Long.valueOf(UtileriaXml.obtenerValorTags(lXml, TAG_INICIO_SECUENCIAL, TAG_FIN_SECUENCIAL)),
					12345678L, // codigoNumerico,
					UtileriaXml.obtenerValorTags(lXml, TAG_INICIO_EMISION, TAG_FIN_EMISION));
		} catch (Exception lError) {
			lError.printStackTrace();
			throw new Exception("Imposible realizar la generacion de la clave de acceso");
		}
	}

	private static String obtenerTipoDocumento(String lsXml) throws Exception {

		if (lsXml.contains(TAG_INICIO_COD_DOC) && lsXml.contains(TAG_FIN_COD_DOC)) {
			return UtileriaXml.obtenerValorTags(TAG_INICIO_COD_DOC, TAG_FIN_COD_DOC, lsXml);
		} else {
			throw new Exception("EL XML NO CUENTA CON TIPO DOCUMENTO, XML INVALiDO");
		}

	}

}
