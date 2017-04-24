package com.producto.comprobanteselectronicos.conector.servicioweb;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.producto.comprobanteselectronicos.conector.IServicioRegistraTrama;
import com.producto.comprobanteselectronicos.eao.util.ServicioPropertie;
import com.producto.comprobanteselectronicos.repositorio.IConsultaComprobanteRepositorio;

@Stateless
@WebService
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ServicioRegistraTrama implements IServicioRegistraTrama{
	
	@EJB
	private IConsultaComprobanteRepositorio consultaComprobante;

	@WebMethod(operationName="recepcionComprobante")
	@WebResult(name="INTEGRACION_TRAMA")
	public String registraTramaComprobante(@WebParam(name="TRAMA_COMPROBANTE") String pTramaComprobante, @WebParam(name = "TOKEN") String pToken, 
			@WebParam(name = "SUSCRIPTOR") String pSuscriptor){
		String respuestaTrama = "";
		try {
			
			if(pToken==null || "".equals(pToken) ){
				return "TOKEN VACIO";
			}
			
			if(!validarParametrosAutenticacion(pToken)){
				return "TOKEN INCORRECTO";
			}
			
			respuestaTrama = consultaComprobante.enviaTramaComprobante(pTramaComprobante, pSuscriptor); 
			return respuestaTrama;
			
		} catch (Throwable e){
			System.err.println("ERROR ENVIANDO TRAMA");
			e.printStackTrace();
			return respuestaTrama;
		}
	}
	
	private boolean validarParametrosAutenticacion(String pToken) {
		String token =ServicioPropertie.getPropiedad("token");
		return pToken.equals(token)  ? true : false;
	}
}
