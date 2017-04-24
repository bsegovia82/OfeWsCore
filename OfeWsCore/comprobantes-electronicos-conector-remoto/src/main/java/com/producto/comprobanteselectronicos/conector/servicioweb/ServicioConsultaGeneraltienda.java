package com.producto.comprobanteselectronicos.conector.servicioweb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import com.producto.comprobanteselectronicos.conector.IServicioConsultaGeneralTienda;
import com.producto.comprobanteselectronicos.eao.util.ServicioPropertie;
import com.producto.comprobanteselectronicos.repositorio.IConsultaComprobanteRepositorio;
import com.producto.comprobanteselectronicos.repositorio.modelo.ReporteCadena;
import com.producto.comprobanteselectronicos.repositorio.modelo.ReporteTienda;

@Stateless
@WebService
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ServicioConsultaGeneraltienda implements IServicioConsultaGeneralTienda {

	@EJB
	private IConsultaComprobanteRepositorio consultaComprobante;

	@Resource
	private WebServiceContext lContextoWs;

	@WebMethod(operationName = "reporteComprobantes")
	@WebResult(name = "REPORTE")
	public List<ReporteTienda> consultaGeneral(@WebParam(name = "TIENDA") String tienda,
											   @WebParam(name = "FECHA_EMISION") String fechaEmision,
											   @WebParam(name = "PUNTO_EMISION") String puntoEmision,
											   @WebParam(name = "SUSCRIPTOR") String suscriptor,
											   @WebParam(name = "USUARIO") String usuario,
											   @WebParam(name = "CLAVE") String clave,
											   @WebParam(name = "TOKEN") String token) {
			List<ReporteTienda> reportes = new ArrayList<ReporteTienda>();
			if(tienda==null || tienda.equals("")){
				ReporteTienda reporte = new ReporteTienda();
				reporte.setMensaje("Debe ingresar la tienda a consultar.");
				reportes.add(reporte);
				return reportes;
			}
			if(suscriptor==null || suscriptor.equals("")){
				ReporteTienda reporte = new ReporteTienda();
				reporte.setMensaje("Debe ingresar el codigo suscriptor.");
				reportes.add(reporte);
				return reportes;
			}
			if(fechaEmision==null || fechaEmision.equals("")){
				ReporteTienda reporte = new ReporteTienda();
				reporte.setMensaje("Debe ingresar la fecha de emision a consultar");
				reportes.add(reporte);
				return reportes;
			}
			if(usuario==null || usuario.equals("")){
				ReporteTienda reporte = new ReporteTienda();
				reporte.setMensaje("Debe ingresar el usuario");
				reportes.add(reporte);
				return reportes;
			}
			if(clave==null || clave.equals("")){
				ReporteTienda reporte = new ReporteTienda();
				reporte.setMensaje("Debe ingresar la clave");
				reportes.add(reporte);
				return reportes;
			}
			if(token==null || token.equals("")){
				ReporteTienda reporte = new ReporteTienda();
				reporte.setMensaje("Debe ingresar el token");
				reportes.add(reporte);
				return reportes;
			}
			
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date = null;
			try {			
				 date = formatter.parse(fechaEmision);				
			} catch (ParseException e) {
				ReporteTienda reporte = new ReporteTienda();
				reporte.setMensaje("La fecha ingresada debe tener el siguiente formato dd/MM/yyyy");
				reportes.add(reporte);
				return reportes;
			}
			
			if(!validarParametrosAutenticacion(usuario,clave, token)){
				ReporteTienda reporte = new ReporteTienda();
				reporte.setMensaje("Usuario, clave incorrcetos");
				reportes.add(reporte);
				return reportes;
			}
			List<String> objetos = consultaComprobante.consultaGeneral(tienda, date, suscriptor,puntoEmision);
						
			for (String trama : objetos) {
				ReporteTienda reporte = new ReporteTienda();
				String tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setNumeroTransacciones(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setVentaTotal(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setVentaNeta(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				tmp = tmp.equals("01") ? "FACTURA" : "NOTA CREDITO";
				reporte.setTipoDocumento(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setEstado(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setMinSecuencia(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setMaxSecuencia(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setEstablecimiento(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setPuntoEmision(tmp);
				reportes.add(reporte);
			}
			return reportes;
	}
	
	@WebMethod(operationName = "reporteComprobantesCadenas")
	@WebResult(name = "REPORTE-CADENA")
	public List<ReporteCadena> consultaGeneralCadena(@WebParam(name = "CADENA") String cadena,
											   @WebParam(name = "FECHA_EMISION") String fechaEmision,
											   @WebParam(name = "SUSCRIPTOR") String suscriptor,
											   @WebParam(name = "USUARIO") String usuario,
											   @WebParam(name = "CLAVE") String clave,
											   @WebParam(name = "TOKEN") String token) {

			List<ReporteCadena> reportes = new ArrayList<ReporteCadena>();
			if(cadena==null || cadena.equals("")){
				ReporteCadena reporte = new ReporteCadena();
				reporte.setMensaje("Debe ingresar la cadena a consultar.");
				reportes.add(reporte);
				return reportes;
			}
			if(suscriptor==null || suscriptor.equals("")){
				ReporteCadena reporte = new ReporteCadena();
				reporte.setMensaje("Debe ingresar el codigo suscriptor.");
				reportes.add(reporte);
				return reportes;
			}
			if(fechaEmision==null || fechaEmision.equals("")){
				ReporteCadena reporte = new ReporteCadena();
				reporte.setMensaje("Debe ingresar la fecha de emision a consultar");
				reportes.add(reporte);
				return reportes;
			}
			if(usuario==null || usuario.equals("")){
				ReporteCadena reporte = new ReporteCadena();
				reporte.setMensaje("Debe ingresar el usuario");
				reportes.add(reporte);
				return reportes;
			}
			if(clave==null || clave.equals("")){
				ReporteCadena reporte = new ReporteCadena();
				reporte.setMensaje("Debe ingresar la clave");
				reportes.add(reporte);
				return reportes;
			}
			if(token==null || token.equals("")){
				ReporteCadena reporte = new ReporteCadena();
				reporte.setMensaje("Debe ingresar el token");
				reportes.add(reporte);
				return reportes;
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date date = null;
			try {			
				 date = formatter.parse(fechaEmision);				
			} catch (ParseException e) {
				ReporteCadena reporte = new ReporteCadena();
				reporte.setMensaje("La fecha ingresada debe tener el siguiente formato dd/MM/yyyy");
				reportes.add(reporte);
				return reportes;
			}
			
			if(!validarParametrosAutenticacion(usuario,clave, token)){
				ReporteCadena reporte = new ReporteCadena();
				reporte.setMensaje("Usuario, clave incorrcetos");
				reportes.add(reporte);
				return reportes;
			}
			List<String> objetos = consultaComprobante.consultaGeneralCadenas(cadena, date, suscriptor);
			
			System.out.println("cantidad de objetos es "+objetos.size());
						
			for (String trama : objetos) {				
				System.out.println("trama es "+trama);
				ReporteCadena reporte = new ReporteCadena();
				String tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setNumeroTransacciones(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setVentaTotal(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setVentaNeta(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				tmp = tmp.equals("01") ? "FACTURA" : "NOTA CREDITO";
				reporte.setTipoDocumento(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setEstado(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setMinSecuencia(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setMaxSecuencia(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setEstablecimiento(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(trama.indexOf(";") + 1);
				reporte.setPuntoEmision(tmp);
				tmp = trama.substring(0, trama.indexOf(";"));
				trama = trama.substring(0, trama.indexOf(";") + 1);
				reporte.setNombreLocal(tmp);
				System.out.println("información para local "+reporte.getNombreLocal());
				reportes.add(reporte);
			}
			System.out.println("registros de reporte es "+reportes.size());
			return reportes;
	}

	

	private boolean validarParametrosAutenticacion(String pUsuario, String pClave, String pToken) {
		String user =ServicioPropertie.getPropiedad("usuario");
		String clave =ServicioPropertie.getPropiedad("password");
		String token =ServicioPropertie.getPropiedad("token");
		return pUsuario.equals(user) && pClave.equals(clave) && pToken.equals(token)  ? true : false;
	}
	
}
