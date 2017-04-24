package com.producto.comprobanteselectronicos.repositorio.impl;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.producto.comprobanteselectronicos.extractor.datos.archivos.directorio.interfaces.IProcesoExtractorComprobantesDirectorio;
import com.producto.comprobanteselectronicos.logs.modelo.entidades.ComprobanteProcesadoIndividual;
import com.producto.comprobanteselectronicos.repositorio.IConsultaComprobanteRepositorio;
import com.producto.comprobanteselectronicos.servicio.individual.IProcesamientoComprobanteIndividual;
import com.producto.comprobanteselectronicos.servicio.logs.ILogging;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;
import com.producto.comprobanteselectronicos.servicios.IServicioFacturaLoteMasivo;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ConsultaComprobanteRepositorio implements IConsultaComprobanteRepositorio {

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_PROCESADOR_INDIVIDUAL)
	private IProcesamientoComprobanteIndividual proceso;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_LOGGIN)
	private ILogging loggin;
	
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_EXTRACION_LOTE)
	private IServicioFacturaLoteMasivo servicioFacturaLoteMasivo;
	
	//ARO - INTEGRACION TRAMA 
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_PROCESADOR_TRAMA)
	private IProcesoExtractorComprobantesDirectorio procesoExtractorComprobante;

	public String consultarComprobanteSeed(String claveAcceso) {
		String claveInd = "", resultado = ""; 
		StringTokenizer tokens=new StringTokenizer(claveAcceso, ";");		
		while(tokens.hasMoreTokens()){
			String lNumeroAutorizacion="";
			claveInd = tokens.nextToken();
            System.out.println("claveInd "+claveInd);
            ComprobanteProcesadoIndividual comp = loggin.obtenerComprobanteFinalizado(claveInd);
         ///   if(comp!=null){
          // SOLO SETEAMOS EL NUMERO DE AUTORIZACION SI ESTA EN ESTADO AUTORIZADO
             // if("A".equals(comp.getEstado())){
            	 lNumeroAutorizacion = comp.getAutorizacion()==null?comp.getClaveAcceso():comp.getAutorizacion(); 
              //}
              resultado = resultado + lNumeroAutorizacion +"-"+comp.getEstado()+"|";
            						
			/*}else{ 
				resultado = resultado + claveInd +"-NE|"; 
			}*/
            //System.out.println("resultado "+resultado);
        }
		return resultado;
	}
	
	public List<String> consultaGeneral(String nombreLocal, Date fechaEmision,String  suscriptor,String puntoemision){
		return loggin.consultaGeneral(nombreLocal, fechaEmision, suscriptor,puntoemision);
	}
	
	public int actualizarEstadoRecepcionErp(String claveAcceso, String estado){
		  return servicioFacturaLoteMasivo.actualizarEstadoRecepcionErp(claveAcceso, estado);
	}
	
	//ARO-CONSULTA WS CADENA
	public List<String> consultaGeneralCadenas(String cadena, Date fechaEmision,String  suscriptor){
		return loggin.consultaGeneralCadenas(cadena, fechaEmision, suscriptor);
	}
	
	public String enviaTramaComprobante(String pTramaComprobante, String pSuscriptores) {
		return procesoExtractorComprobante.procesoGeneracionXMLviaWS(pTramaComprobante, Long.parseLong(pSuscriptores));
	}
}
