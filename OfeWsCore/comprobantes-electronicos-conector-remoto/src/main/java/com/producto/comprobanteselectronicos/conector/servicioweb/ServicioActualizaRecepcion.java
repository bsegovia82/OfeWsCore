package com.producto.comprobanteselectronicos.conector.servicioweb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.WebServiceContext;

import com.producto.comprobanteselectronicos.conector.IServicioActualizacionRecepcion;
import com.producto.comprobanteselectronicos.repositorio.IConsultaComprobanteRepositorio;

@Stateless
@WebService
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ServicioActualizaRecepcion implements IServicioActualizacionRecepcion {

	@EJB
	private IConsultaComprobanteRepositorio consultaComprobante;

	@Resource
	private WebServiceContext lContextoWs;

	@WebMethod(operationName = "actualizaRecepcion")
	@WebResult(name = "RESULTADO")
	public String actualizaRecepcion(@WebParam(name = "EMPRESA") @XmlElement(required = true) String empresa,
			@WebParam(name = "CLAVE_ACCESO") @XmlElement(required = true) String claveAcceso,
			@WebParam(name = "ESTADO") @XmlElement(required = true) String estado) {
		int resultado = 0;
		String respuesta = "";
		if (claveAcceso != null && estado != null)
			if (!(claveAcceso.equals("") || estado.equals(""))) {
				for (String clave : claveAcceso.split(";")) {
					resultado = consultaComprobante.actualizarEstadoRecepcionErp(clave, estado);
					if (!(resultado == 1)) {
						respuesta = respuesta + clave + ";";
					}
				}

			}else{
				return claveAcceso;
			}
			
		if (respuesta.equals(""))
			respuesta = "1";
		return respuesta;

	}

	public static void main(String[] args) {
		String trama = "autorizacion1";
		for (String s : trama.split(";"))
			System.out.println(s);
	}

}
