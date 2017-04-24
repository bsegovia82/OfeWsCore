package com.producto.comprobanteselectronicos.servicio.individual.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.Autorizacion;
import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.AutorizacionComprobanteResponse;
import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.Mensaje;
import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.RespuestaComprobante;
import com.producto.comprobanteselectronicos.autorizador.servicios.implementacion.AutorizacionComprobante;
import com.producto.comprobanteselectronicos.cliente.ClenteCosumoWsErp;
import com.producto.comprobanteselectronicos.comprobador.desarrollo.normal.offline.RespuestaSolicitud;
import com.producto.comprobanteselectronicos.comprobador.servicios.implementacion.ComprobacionNormal;
import com.producto.comprobanteselectronicos.generales.ConstanteInfoTributaria;
import com.producto.comprobanteselectronicos.generales.GeneradorClaveAcceso;
import com.producto.comprobanteselectronicos.logs.modelo.entidades.ComprobanteProcesadoIndividual;
import com.producto.comprobanteselectronicos.logs.modelo.entidades.EfacturaXML;
import com.producto.comprobanteselectronicos.logs.modelo.entidades.EstadosComprobantesEmpresas;
import com.producto.comprobanteselectronicos.logs.modelo.util.ParametrosEstaticosBase;
import com.producto.comprobanteselectronicos.modelo.masivo.mensaje.MensajeIndividual;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.ComprobanteRetencion;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.NotaDebito;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.guiaremision.GuiaRemision;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.NotaCredito;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v210.factura.Factura;
import com.producto.comprobanteselectronicos.parametros.modelo.entidades.GeDatosGeneralesCompania;
import com.producto.comprobanteselectronicos.parametros.modelo.entidades.GeSuscriptor;
import com.producto.comprobanteselectronicos.servicio.individual.IProcesamientoComprobanteIndividual;
import com.producto.comprobanteselectronicos.servicio.jms.cdi.ServicioClienteJMSMensajeIndividualCDI;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioComprobacionWS;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioFirma;
import com.producto.comprobanteselectronicos.servicio.logs.ILogging;
import com.producto.comprobanteselectronicos.servicio.notificaciones.IServiciosNotificaciones;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;
import com.producto.comprobanteselectronicos.servicio.parametros.IServicioParametros;
import com.producto.comprobanteselectronicos.servicios.IServicioComprobanteIndividual;
import com.producto.comprobanteselectronicos.servicios.ride.IGenerarComprobantePDF;
import com.producto.comprobanteselectronicos.utilerias.excepciones.ExcepcionValidacionComprobantes;
import com.producto.comprobanteselectronicos.utilerias.generales.ModuladorComprobanteComprimido;
import com.producto.comprobanteselectronicos.utilerias.generales.OperacionesFile;
import com.producto.comprobanteselectronicos.utilerias.generales.ServiciosTransformacion;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ProcesamientoComprobanteIndividual implements IProcesamientoComprobanteIndividual {
	
	private String rutaArchivoGenerado;
	private String URLWSSRIAutorizacion;
	private String URLWSSRIComprobacion;
	private static final String ABRE_CAMPO_ADICIONAL = "<campoAdicional";
	private static final String COMILLAS = "\"";
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_PARAMETROS)
	private IServicioParametros servicioParametros;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_LOGGIN)
	private ILogging loggin;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_EXTRACION_INDIVIDUAL)
	private IServicioComprobanteIndividual comprobantesIndividual;

//	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_JMS_INDIVIDUAL)
//	private IServicioClienteJMSMensajeIndividual servicioJMS;
	
	@Inject
	private ServicioClienteJMSMensajeIndividualCDI servicioJMS;
	
	
	@EJB(lookup = INombresParametros.NOMBRE_JNDI_NOTIFICADOR_RIDE_PDF)
	private IServiciosNotificaciones servicioNotificacion;
	
	@EJB(lookup=INombresParametros.NOMBRE_JNDI_SERVICIO_GENERACION_PDF)
	private IGenerarComprobantePDF puerto;
	
	private static final String TAGCORREO = "CORREO 1";
	
	private static final String TAGCORREO2 = "MAILCLIENTE";
	
	private static final String TAGCORREO3 = "EMAILCLIENTE";
	
	private static final String TAGVENDEDOR = "VENDEDOR";
	
	@PostConstruct
	public void init(){
		URLWSSRIAutorizacion =ParametrosEstaticosBase.getURLWSSRIAutorizacion(); 
		URLWSSRIComprobacion = ParametrosEstaticosBase.getURLWSSRIComprobacion();
	}

	@Asynchronous
	public void procesarMensajeLoteVirtual(ArrayList<MensajeIndividual> mensaje) {
		for (MensajeIndividual mensajes : mensaje) {
			procesarMensajeIndividual(mensajes);
		}
	}

	private Document parseDocumentoComprobante(Object comprobante, Class<?> clase) throws Throwable {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document documento = db.newDocument();
		JAXBContext contexto = JAXBContext.newInstance(clase);
		Marshaller msh = contexto.createMarshaller();		
		msh.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
		msh.marshal(clase.cast(comprobante), documento);		
		return documento;
	}

	private String nombreComprobanteTipoDoc(String tipoDoc) {
		switch (tipoDoc) {
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA:
			return "factura";
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION:
			return "retencion";
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION:
			return "guiaRemision";
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO:
			return "notaCredito";
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO:
			return "notaDebito";
		default:
			return "otro";
		}
	}

	private String generadorNombre(String claveAcceso, String compania,	String tipoDoc, String prefijo, String sufijo) {
		rutaArchivoGenerado = servicioParametros.getValorParametro(INombresParametros.PARAMETRO_RUTA_ARCHIVO_XML_FIRMADOS);
		String directorio = rutaArchivoGenerado + File.separatorChar + compania
				+ File.separatorChar + INombresParametros.DIRECTORIO_INDIVIDUAL
				+ File.separatorChar + nombreComprobanteTipoDoc(tipoDoc);
		File archivo = new File(directorio);
		if (!archivo.exists()) {
			archivo.mkdirs();
		}
		return rutaArchivoGenerado + File.separatorChar + compania
				+ File.separatorChar + INombresParametros.DIRECTORIO_INDIVIDUAL
				+ File.separatorChar + nombreComprobanteTipoDoc(tipoDoc)
				+ File.separatorChar + claveAcceso + ".xml";
	}

	private String firmarComprobanteIndividual(Document documento, String claveAcceso, String compania, String nodo,
			String observacion, String tipoDoc, String rutaArchivoFirmado, boolean isNew) throws Throwable {
		GeDatosGeneralesCompania datosCompania = null;
		datosCompania = servicioParametros.datosCompania(compania);
		int tamMax = 1048576; //new Integer(servicioParametros.getValorParametro(INombresParametros.PARAMETRO_TAMAN_MAX_KB_XML));
		if (datosCompania == null) {
			throw new Throwable("NO EXISTEN DATOS PARA COMPANIA, CON CODIGO : "	+ compania
					+ ", POR FAVOR REVISE LA TABLA GE_DATOS_COMPANIA");
		} else {
			return ServicioFirma.firmarDocumentoXML(documento, claveAcceso,	datosCompania.getRutaArchivoCertificado(),
					datosCompania.getPasswordCertificado(), rutaArchivoFirmado,	nodo, observacion,tamMax,isNew);
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private ComprobanteProcesadoIndividual crearComprobanteEstadoInicial(String compania, String tipoDoc, String version, String ambiente,
			String claveAcceso, String establecimiento, String puntoEmision,String secuencial, Date fechaEmision,
			String identificacionReceptor, String nombreReceptor, Object xsd,
			Class<?> tipo, String emailsCliente, Double totalDocumento,Long idSuscriptor, String vendedor, Double subTotal,String documentoModificaddo,
			Double descuento,Double impuesto12,Double impuesto0, String nombreArchivo, String codigoInterno, Integer local, Integer cadena)//Se incluye el campo total BSE
	{
		ComprobanteProcesadoIndividual comp = new ComprobanteProcesadoIndividual();
		comp.setClaveAcceso(claveAcceso);
		comp.setNumDocumento(establecimiento + puntoEmision	+ StringUtils.leftPad(secuencial, 9, "0"));
		comp.setCompania(compania);
		comp.setEmailCliente(emailsCliente);
		comp.setEstado("E");
		comp.setFechaEmision(fechaEmision);
		comp.setFechaActualizacion(new Date());
		comp.setVersionComprobante(version);
		comp.setTipoDocumento(tipoDoc);
		comp.setFechaRegistro(new Date());
		comp.setIdentificacionCliente(identificacionReceptor);
		comp.setNumeroIntento(1);
		comp.setObservacion("REGISTRO INICIAL DEL PROCESO DE COMPROBANTE");
		comp.setNombreCliente(nombreReceptor);
		comp.setAmbiente(ambiente);
		comp.setTotalDocumento(totalDocumento);//BSE incluir el total del comprobante		
		comp.setIdSuscriptor(idSuscriptor);
		comp.setVendedor(vendedor);
		comp.setSubTotal(subTotal);
		comp.setDocumentoModificaddo(documentoModificaddo);
		comp.setDescuento(descuento);
		comp.setImpuesto12(impuesto12);
		comp.setImpuesto0(impuesto0);		
		comp.setNombreArchivoCliente(nombreArchivo);		
		comp.setIdEmpresaCliente(codigoInterno.toUpperCase());
		comp.setEstablecimiento(establecimiento);
		comp.setPuntoEmision(puntoEmision);
		comp.setLocal(local);
		comp.setCadena(cadena);
		comp.setSecuencial(StringUtils.leftPad(secuencial, 9, "0"));
		try {
			comp.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(obtenerStringDeComprobante(tipoDoc, xsd, tipo),claveAcceso));
		} catch (IOException e) {
			e.printStackTrace();
		}
		loggin.registrarInicioProcesoComprobanteIndividual(comp);
		return comp;
	}

	@Asynchronous
	public void procesarMensajeIndividual(MensajeIndividual mensaje) {
		switch (mensaje.getEstadoProceso()) {
		case "E":
			firmarComprobante(mensaje);
			break;
		case "F":
			envioRevisionComprobante(mensaje);
			break;
		case "C":
			consultarAutorizacion(mensaje);
			break;
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void firmarComprobante(MensajeIndividual mensaje) {
		switch (mensaje.getTipoDocumento()) {
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA: firmarFactura(mensaje);
			break;
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION: firmarComprobanteRetencion(mensaje);
			break;
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION: firmarComprobanteGuia(mensaje);
			break;
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO: firmarNotaDebito(mensaje);
			break;
		case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO: firmarNotaCredito(mensaje);
			break;
		}
	}

	//CAMBIO OFFLINE
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void consultarAutorizacion(MensajeIndividual mensaje) {
		ComprobanteProcesadoIndividual comprobante = loggin.obtenerComprobanteIndividual(mensaje.getComprobanteProcesando().getClaveAcceso(), mensaje.getComprobanteProcesando().getEstado());
	//	URLWSSRIAutorizacion = servicioParametros.getValorParametro(INombresParametros.PROCESO_AUTORIZACION_INDIVIDUAL_URL_OFFLINE);
		if (ServicioComprobacionWS.servicioAutorizacionIndividual(URLWSSRIAutorizacion)) {
			try {
				envioAutorizacion(comprobante);
			} catch (Throwable error) {
				comprobante.setEstado("EDA");
				comprobante.setError(parsearPilaErroresJava(error));
				error.printStackTrace();
			}
		} else {
			comprobante.setObservacion("EL SERVICIO DE AUTORIZACION DEL SRI NO SE ENCUENTRA DISPONIBLE");
			comprobante.setEstado("EDA");
		}
		comprobante.setFechaFinProceso(new Date());
		loggin.actualizarComprobanteIndividual(comprobante);		
	}

	//CAMBIO OFFLINE
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void envioRevisionComprobante(MensajeIndividual mensaje) {
		ComprobanteProcesadoIndividual comprobante = loggin.obtenerComprobanteIndividual(mensaje.getComprobanteProcesando().getClaveAcceso(), 
				mensaje.getComprobanteProcesando().getEstado());
		//URLWSSRIComprobacion = servicioParametros.getValorParametro(INombresParametros.PROCESO_COMPROBACION_INDIVIDUAL_URL_OFFLINE);
		if (ServicioComprobacionWS.servicioComprobacionIndividual(URLWSSRIComprobacion)) {
			try {
				envioComprobacion(comprobante);
			} catch (Throwable error) {
				comprobante.setEstado("XC");
				comprobante.setError(parsearPilaErroresJava(error));
				error.printStackTrace();
			}
		} else {
			comprobante.setObservacion("EL SERVICIO DE COMPROBACION DEL SRI NO SE ENCUENTRA DISPONIBLE");
			comprobante.setEstado("TAC");
			comprobante.setFechaFinProceso(new Date());
		}
		loggin.actualizarComprobanteIndividual(comprobante);
		if (comprobante.getEstado().equals("C")) {
			mensaje.setComprobanteProcesando(comprobante);
			mensaje.setEstadoProceso(comprobante.getEstado());
			try {
				servicioJMS.registrarMensajeIndividual(mensaje);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	//CAMBIO OFFLINE
	private ComprobanteProcesadoIndividual obtenerUltComprobante(
			String tipoDocumento, String claveAcceso, String compania,
			String version, String ambiente, String establecimiento,
			String puntoEmision, String secuencial, Date fechaEmision,
			String identificacionReceptor, String nombreReceptor, Object xsd,
			Class<?> clase, String emailsCliente, Double total,Long idSuscriptor,String vendedor, Double subTotal,String documentoModificaddo,
			Double descuento,Double impuesto12,Double impuesto0, String nombreArchivo, String codigoInterno,
			Integer local, Integer cadena)//Se incluye el campo total BSE 
	{
		
		ComprobanteProcesadoIndividual comprobante = loggin	.obtenerComprobanteAProcesar(claveAcceso);		
		if (comprobante == null) {			
			comprobante = crearComprobanteEstadoInicial(compania,
					tipoDocumento, version, ambiente, claveAcceso,
					establecimiento, puntoEmision, secuencial, fechaEmision,
					identificacionReceptor, nombreReceptor, xsd, clase,	emailsCliente, total,idSuscriptor,vendedor, subTotal,documentoModificaddo,
					 descuento, impuesto12,impuesto0, nombreArchivo, codigoInterno,local,cadena);//Se incluye el campo total BSE			
			comprobante = loggin.obtenerComprobanteIndividual(claveAcceso, INombresParametros.ESTADO_INICIAL_PROCESAMIENTO_INDIVIDUAL);			
		} else {			
			comprobante.setEstado("E");
			comprobante.setNumeroIntento(comprobante.getNumeroIntento() != null ? comprobante.getNumeroIntento() + 1 : 1);
			comprobante.setObservacion("REGISTRO RECICLADO PROCESO DE COMPROBANTE");
			comprobante.setAutorizacion(null);
			comprobante.setDocumentoDescargado(null);
			comprobante.setDocumentoEntregadoClienteMail(null);
			comprobante.setDocumentoEnviadoMail(comprobante.getDocumentoEnviadoMail());
			comprobante.setEmailCliente(emailsCliente);
			comprobante.setError(null);
			comprobante.setFechaActualizacion(new Date());
			comprobante.setFechaAutorizacion(null);
			comprobante.setFechaComprobacion(null);
			comprobante.setFechaEnvioMail(comprobante.getFechaEnvioMail());
			comprobante.setFechaFinFirmado(null);
			comprobante.setFechaFinGeneracionPDF(null);
			comprobante.setFechaFinProceso(null);
			comprobante.setFechaGeneracion_pdf(null);
			comprobante.setFechaInicioFirmado(null);
			comprobante.setFechaSolAutorizacion(null);
			comprobante.setOtpEntregaFactura(null);
			comprobante.setPdfGenerado(null);
			comprobante.setRespuestaAutorizacion(null);
			comprobante.setRespuestaComprobacion(null);
			comprobante.setRutaArchivoFirmado(null);
			comprobante.setRutaPDF(null);
			comprobante.setUltimoCodigoError(null);
			comprobante.setVersionComprobante(version);
			comprobante.setXmlComprobante(null);
			comprobante.setXmlRespuestaAutorizacion(null);
			comprobante.setXmlRespuestaComprobacion(null);
			comprobante.setAmbiente(ambiente);
			comprobante.setIdentificacionCliente(identificacionReceptor);
			comprobante.setNombreCliente(nombreReceptor);
			comprobante.setTotalDocumento(total);//Se incluye el campo total BSE
			comprobante.setIdSuscriptor(idSuscriptor);
			comprobante.setVendedor(vendedor);
			comprobante.setSubTotal(subTotal);
			comprobante.setDocumentoModificaddo(documentoModificaddo);
			comprobante.setDescuento(descuento);
			comprobante.setImpuesto12(impuesto12);
			comprobante.setImpuesto0(impuesto0);
			comprobante.setFechaEnvioConfirmacionCliente(null);			
			if(nombreArchivo!=null && !nombreArchivo.equals("")){				
				//verificar el estado del comprobante si es a borrar el nuevo archivo si es diferente de a buscar en error buscar en no autorizados
				//obtener ruta inicial en base a la compania
				String rutaInicial=servicioParametros.obtenerRutaInicialFtp(comprobante.getCompania());
				if(!rutaInicial.equals("")){
					System.out.println("se va a mover el archivo"+comprobante.getTipoDocumento() + " "+ comprobante.getEstado() + " " +rutaInicial+ " " +comprobante.getNombreArchivoCliente());
					if(comprobante.getNombreArchivoCliente()!=null && !comprobante.getNombreArchivoCliente().equals(""))
						deleteFileError(comprobante.getTipoDocumento(),comprobante.getEstado(),rutaInicial,comprobante.getNombreArchivoCliente(),nombreArchivo);
				}
				comprobante.setNombreArchivoCliente(nombreArchivo);
			}
			
			try {
				comprobante.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(obtenerStringDeComprobante(tipoDocumento, xsd, clase),claveAcceso));				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return comprobante;
	}
	
	public void deleteFileError(String tipoDocumento, String estado, String rutaInicial, String archivo, String nuevoArchivo){						
		//if estado = 'A' borrar el nuevo
		String rutaPrincipal = rutaInicial;
		if(estado.equals("A")){
			rutaInicial =rutaPrincipal +"ORIGINALES"+File.separator+"FACTURAS"+File.separator+"PROCESADOS";
			File srcFile = new File(rutaInicial+File.separator+nuevoArchivo);
			if(srcFile.exists()){
				System.out.println("se va a eliminar el nuevo archivo porue ya se encuentra en estado A"+ srcFile.getAbsolutePath());
				srcFile.delete();
			}
		}else{//verificar si existe en error o en no autorizados
			String rutaError   =rutaPrincipal +"ORIGINALES"+File.separator+"FACTURAS"+File.separator+"ERROR";
			String rutaDestino = "";
			File srcFile = new File(rutaError+File.separator+archivo);
			if(srcFile.exists()){
				System.out.println("se va a eliminar el archivo porque cambio el nombre de la carpeta de error "+ srcFile.getAbsolutePath());
				srcFile.delete();
			}			
			if(tipoDocumento.equals("01")){			
				rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"FACTURAS"+File.separator;
			}
			if(tipoDocumento.equals("07")){
				rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"RETENCIONES"+File.separator;
			}
			if(tipoDocumento.equals("04")){
				rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"NOTA_CREDITO"+File.separator;
			}
			if(tipoDocumento.equals("06")){
				rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"GUIAS"+File.separator;
			}
			if(tipoDocumento.equals("05")){				
				rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"NOTA_DEBITO"+File.separator;
			}
			File srcFileNoautorizado = new File(rutaDestino+File.separator+archivo);
			if(srcFileNoautorizado.exists()){
				System.out.println("se va a eliminar el archivo porque cambio el nombre"+ srcFileNoautorizado.getAbsolutePath());
				srcFileNoautorizado.delete();
			}
			
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void firmarFactura(MensajeIndividual mensaje) {
		boolean continuar = true;
		Factura facturaXSD = mensaje.getFactura();
		String emailsCliente = "";
		String vendedor = "";
		if (facturaXSD.getInfoAdicional() != null) {
			if (facturaXSD.getInfoAdicional().getCampoAdicional() != null) {
				List<com.producto.comprobanteselectronicos.modelo.normal.xsd.v210.factura.Factura.InfoAdicional.CampoAdicional> listaCampos = facturaXSD
						.getInfoAdicional().getCampoAdicional();
				for (com.producto.comprobanteselectronicos.modelo.normal.xsd.v210.factura.Factura.InfoAdicional.CampoAdicional campo : listaCampos) {
					if (campo.getNombre().toUpperCase().equals(TAGCORREO) || campo.getNombre().toUpperCase().equals(TAGCORREO3) ) {
						emailsCliente = campo.getValue();
						emailsCliente = emailsCliente.replaceAll(",", ";");						
					}else if(campo.getNombre().toUpperCase().equals(TAGVENDEDOR)){
						vendedor= campo.getValue();
					}
				}
			}
		}
		Double imp12=0.0;
	    Double imp0=0.0;
	    for(Factura.InfoFactura.TotalConImpuestos.TotalImpuesto imp :facturaXSD.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()){
	    	if(!imp.getCodigoPorcentaje().equals("0")){
	    		imp12=imp.getValor().doubleValue();
	    	}else if(imp.getCodigoPorcentaje().equals("0")){
	    		imp0=imp.getValor().doubleValue();
	    	}
	    }
		GeDatosGeneralesCompania buscarComp =  servicioParametros.obtenerCompaniaPorRUC(facturaXSD.getInfoTributaria().getRuc());		
		Long idSuscriptor=buscarComp.getIdSuscriptor();	
		HashMap<String, Object> datosCadenaLocal =servicioParametros.obtenerCadenaLocal(buscarComp.getId(),
				facturaXSD.getInfoTributaria().getEstab(),
				facturaXSD.getInfoTributaria().getPtoEmi());
		
		ComprobanteProcesadoIndividual comprobante = obtenerUltComprobante(
			ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA, facturaXSD.getInfoTributaria().getClaveAcceso(),
			mensaje.getCompania(), facturaXSD.getVersion(),	mensaje.getAmbiente(), facturaXSD.getInfoTributaria().getEstab(),
			facturaXSD.getInfoTributaria().getPtoEmi(),	facturaXSD.getInfoTributaria().getSecuencial(),	
			convierteCadenaAFecha(facturaXSD.getInfoFactura().getFechaEmision()), facturaXSD.getInfoFactura().getIdentificacionComprador(),
			facturaXSD.getInfoFactura().getRazonSocialComprador(),	facturaXSD, Factura.class, emailsCliente,
			facturaXSD.getInfoFactura().getImporteTotal()==null?0.0:facturaXSD.getInfoFactura().getImporteTotal().doubleValue(),
			idSuscriptor ,vendedor,
			facturaXSD.getInfoFactura().getTotalSinImpuestos()==null?0.0:facturaXSD.getInfoFactura().getTotalSinImpuestos().doubleValue(),"",
			facturaXSD.getInfoFactura().getTotalDescuento()==null?0.0:facturaXSD.getInfoFactura().getTotalDescuento().doubleValue(),imp12,imp0,
			mensaje.getNombreArchivo(), mensaje.getIdEmpresaCliente(),
			datosCadenaLocal.get("local")==null?null:((BigDecimal)datosCadenaLocal.get("local")).intValue(),
			datosCadenaLocal.get("cadena")==null?null:((BigDecimal)datosCadenaLocal.get("cadena")).intValue());//Se incluye el campo total BSE		
		boolean isNew = comprobante.getNumeroIntento() < 2;
		try {
			comprobante.setFechaInicioFirmado(new Date());
			Document documento = parseDocumentoComprobante(facturaXSD,Factura.class);			
			String archivoGenerado = generadorNombre(facturaXSD.getInfoTributaria().getClaveAcceso(), 
					mensaje.getCompania(), ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA,
					INombresParametros.PREFIJO_XML_FIRMADO,	INombresParametros.SUFIJO_XML_FIRMADO);
			String comprobanteFirmado = firmarComprobanteIndividual(documento,
					facturaXSD.getInfoTributaria().getClaveAcceso(),
					mensaje.getCompania(), "comprobante", "comprobateFirma",
					ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA,archivoGenerado,isNew);
			if (comprobanteFirmado==null){
				throw new ExcepcionValidacionComprobantes();
			}								
			comprobante.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(comprobanteFirmado,comprobante.getNumDocumento()));
			comprobante.setObservacion("ARCHIVO FIRMADO CORRECTAMENTE");
			comprobante.setEstado("F");
			comprobante.setFechaFinFirmado(new Date());
			comprobante.setRutaArchivoFirmado(archivoGenerado);
			continuar = true;
		}
		catch(ExcepcionValidacionComprobantes ev){
			comprobante.setObservacion("ERROR AL EL TAMANIO DEL ARCHIVO SUPERA LO PARAMETRIZADO ");
			comprobante.setEstado("RC");
			comprobante.setError("El tamanio del XML firmado supera al tamanio parametrizado en CANTIDAD_MAXIMA_KB_XML");
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
			armarEnvioCorreoErrorXml(comprobante, comprobante.getXmlRespuestaComprobacion());
		}catch (Throwable e) {
			System.out.println("En metodo de firmado [Throwable]");
			e.printStackTrace();
			comprobante.setObservacion("ERROR AL FIRMAR EL ARCHIVO DE COMPROBANTE ");
			comprobante.setEstado("XF");
			comprobante.setError(parsearPilaErroresJava(e));
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
		}
		loggin.actualizarComprobanteIndividual(comprobante);		
		if (continuar) {
			mensaje.setComprobanteProcesando(comprobante);
			mensaje.setEstadoProceso(comprobante.getEstado());
			try {
				servicioJMS.registrarMensajeIndividual(mensaje);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void firmarComprobanteRetencion(MensajeIndividual mensaje) {
		boolean continuar = true;
		ComprobanteRetencion retencion = mensaje.getComprobanteRetencion();
		String emailsCliente = "";
		if (retencion.getInfoAdicional() != null) {
			if (retencion.getInfoAdicional().getCampoAdicional() != null) {
				List<com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.ComprobanteRetencion.InfoAdicional.CampoAdicional> listaCampos = retencion
						.getInfoAdicional().getCampoAdicional();
				for (com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.ComprobanteRetencion.InfoAdicional.CampoAdicional campo : listaCampos) {
					if (campo.getNombre().toUpperCase().equals(TAGCORREO)  || campo.getNombre().toUpperCase().equals(TAGCORREO3)) {						
						emailsCliente = campo.getValue();
						emailsCliente = emailsCliente.replaceAll(",", ";");
						break;
					}
				}
			}
		}
		
		
		Double totalRetencion = 0D;//Se incluye el campo total BSE
		for (com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.Impuesto det : retencion.getImpuestos().getImpuesto()) {
			
			totalRetencion = totalRetencion + det.getValorRetenido().doubleValue();
					
			
		}		
		GeDatosGeneralesCompania buscarComp =  servicioParametros.obtenerCompaniaPorRUC(retencion.getInfoTributaria().getRuc()); 
		
		HashMap<String, Object> datosCadenaLocal =servicioParametros.obtenerCadenaLocal(buscarComp.getId(),
				retencion.getInfoTributaria().getEstab(),
				retencion.getInfoTributaria().getPtoEmi());
		
		ComprobanteProcesadoIndividual comprobante = obtenerUltComprobante(
				ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION, retencion.getInfoTributaria().getClaveAcceso(),
				mensaje.getCompania(), retencion.getVersion(),mensaje.getAmbiente(),
				retencion.getInfoTributaria().getEstab(), retencion
						.getInfoTributaria().getPtoEmi(), retencion
						.getInfoTributaria().getSecuencial(),
				convierteCadenaAFecha(retencion.getInfoCompRetencion()
						.getFechaEmision()), retencion.getInfoCompRetencion()
						.getIdentificacionSujetoRetenido(), retencion
						.getInfoCompRetencion().getRazonSocialSujetoRetenido(),
				retencion, ComprobanteRetencion.class, emailsCliente, totalRetencion,buscarComp.getIdSuscriptor(),"",0.0,"",0.0,0.0,0.0,mensaje.getNombreArchivo(), mensaje.getIdEmpresaCliente(),
				datosCadenaLocal.get("local")==null?null:((BigDecimal)datosCadenaLocal.get("local")).intValue(),
				datosCadenaLocal.get("cadena")==null?null:((BigDecimal)datosCadenaLocal.get("cadena")).intValue()
				); // se sumo todos los valores de la retencion
		boolean isNew = comprobante.getNumeroIntento() < 2;
		try {
			comprobante.setFechaInicioFirmado(new Date());
			Document documento = parseDocumentoComprobante(retencion, ComprobanteRetencion.class);				
			String archivoGenerado = generadorNombre(
					retencion.getInfoTributaria().getClaveAcceso(), 
					mensaje.getCompania(), 
					ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION,
					INombresParametros.PREFIJO_XML_FIRMADO,
					INombresParametros.SUFIJO_XML_FIRMADO);
			String comprobanteFirmado = firmarComprobanteIndividual(documento,
					retencion.getInfoTributaria().getClaveAcceso(),
					mensaje.getCompania(), "comprobante", "comprobateFirma",
					ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION,archivoGenerado,isNew);
			if (comprobanteFirmado==null){
				throw new ExcepcionValidacionComprobantes();
			}		
			comprobante.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(comprobanteFirmado,comprobante.getNumDocumento()));
			comprobante.setObservacion("ARCHIVO FIRMADO CORRECTAMENTE");
			comprobante.setEstado("F");
			comprobante.setFechaFinFirmado(new Date());
			comprobante.setRutaArchivoFirmado(archivoGenerado);
			continuar = true;
		}
		catch(ExcepcionValidacionComprobantes ev){			
			comprobante.setObservacion("ERROR EN TAMANIO DEL ARCHIVO SUPERA LO PARAMETRIZADO ");
			comprobante.setEstado("RC");
			comprobante.setError("El tamanio del XML firmado supera al tamanio parametrizado en CANTIDAD_MAXIMA_KB_XML");
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
			armarEnvioCorreoErrorXml(comprobante, comprobante.getXmlRespuestaComprobacion());
		}catch (Throwable e) {
			e.printStackTrace();
			comprobante.setObservacion("ERROR AL FIRMAR EL ARCHIVO DE COMPROBANTE "+ e.getMessage());
			comprobante.setEstado("XF");
			comprobante.setError(parsearPilaErroresJava(e));
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
		}
		loggin.actualizarComprobanteIndividual(comprobante);

		if (continuar) {
			mensaje.setComprobanteProcesando(comprobante);
			mensaje.setEstadoProceso(comprobante.getEstado());
			try {
				servicioJMS.registrarMensajeIndividual(mensaje);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void firmarNotaDebito(MensajeIndividual mensaje) {
		boolean continuar = true;
		NotaDebito retencion = mensaje.getNotaDebito();
		String emailsCliente = "";
		if (retencion.getInfoAdicional() != null) {
			if (retencion.getInfoAdicional().getCampoAdicional() != null) {
				List<com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.NotaDebito.InfoAdicional.CampoAdicional> listaCampos = retencion
						.getInfoAdicional().getCampoAdicional();
				for (com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.NotaDebito.InfoAdicional.CampoAdicional campo : listaCampos) {
					if (campo.getNombre().toUpperCase().equals(TAGCORREO) || campo.getNombre().toUpperCase().equals(TAGCORREO3)) {
						emailsCliente = campo.getValue();
						emailsCliente = emailsCliente.replaceAll(",", ";");
						break;
					}
//					else if (campo.getNombre().toUpperCase().equals(TAGCORREO2)) {
//						emailsCliente = campo.getValue();
//						emailsCliente = emailsCliente.replaceAll(",", ";");	
//						break;
//					}
				}
			}
		}
		
	
		GeDatosGeneralesCompania buscarComp =  servicioParametros.obtenerCompaniaPorRUC(retencion.getInfoTributaria().getRuc()); 
		HashMap<String, Object> datosCadenaLocal =servicioParametros.obtenerCadenaLocal(buscarComp.getId(),
				retencion.getInfoTributaria().getEstab(),
				retencion.getInfoTributaria().getPtoEmi());
		ComprobanteProcesadoIndividual comprobante = obtenerUltComprobante(
				ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO, retencion
						.getInfoTributaria().getClaveAcceso(),
				mensaje.getCompania(), retencion.getVersion(),
				mensaje.getAmbiente(),
				retencion.getInfoTributaria().getEstab(), retencion
						.getInfoTributaria().getPtoEmi(), retencion
						.getInfoTributaria().getSecuencial(),
				convierteCadenaAFecha(retencion.getInfoNotaDebito()
						.getFechaEmision()), retencion.getInfoNotaDebito()
						.getIdentificacionComprador(), retencion
						.getInfoNotaDebito().getRazonSocialComprador(),
				retencion, NotaDebito.class, emailsCliente, 
				retencion.getInfoNotaDebito().getValorTotal()==null?0.0:retencion.getInfoNotaDebito().getValorTotal().doubleValue(),buscarComp.getIdSuscriptor(),"",0.0,"",0.0,0.0,0.0
				,mensaje.getNombreArchivo(), mensaje.getIdEmpresaCliente(),
				datosCadenaLocal.get("local")==null?null:((BigDecimal)datosCadenaLocal.get("local")).intValue(),
						datosCadenaLocal.get("cadena")==null?null:((BigDecimal)datosCadenaLocal.get("cadena")).intValue());//Se incluye el campo total BSE
		boolean isNew = comprobante.getNumeroIntento() < 2;
		try {
			comprobante.setFechaInicioFirmado(new Date());
			Document documento = parseDocumentoComprobante(retencion, NotaDebito.class);
			String archivoGenerado = generadorNombre(
					retencion.getInfoTributaria().getClaveAcceso(), 
					mensaje.getCompania(), 
					ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO,
					INombresParametros.PREFIJO_XML_FIRMADO,
					INombresParametros.SUFIJO_XML_FIRMADO);
			String comprobanteFirmado = firmarComprobanteIndividual(documento,
					retencion.getInfoTributaria().getClaveAcceso(),
					mensaje.getCompania(), "comprobante", "comprobateFirma",
					ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO,archivoGenerado,isNew);
			if (comprobanteFirmado==null){
				throw new ExcepcionValidacionComprobantes();
			}				
			comprobante.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(comprobanteFirmado,comprobante.getNumDocumento()));
			comprobante.setObservacion("ARCHIVO FIRMADO CORRECTAMENTE");
			comprobante.setEstado("F");
			comprobante.setFechaFinFirmado(new Date());
			comprobante.setRutaArchivoFirmado(archivoGenerado);
			continuar = true;
		}
		catch(ExcepcionValidacionComprobantes ev){			
			comprobante.setObservacion("ERROR EN TAMANIO DEL ARCHIVO SUPERA LO PARAMETRIZADO ");
			comprobante.setEstado("RC");
			comprobante.setError("El tamanio del XML firmado supera al tamanio parametrizado en CANTIDAD_MAXIMA_KB_XML");
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
			armarEnvioCorreoErrorXml(comprobante, comprobante.getXmlRespuestaComprobacion());
		}catch (Throwable e) {
			e.printStackTrace();
			comprobante.setObservacion("ERROR AL FIRMAR EL ARCHIVO DE COMPROBANTE "	+ e.getMessage());
			comprobante.setEstado("XF");
			comprobante.setError(parsearPilaErroresJava(e));
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
		}
		loggin.actualizarComprobanteIndividual(comprobante);
//		comprobantesIndividual.actualizarDatosCabecera(
//				comprobante.getFechaSolAutorizacion(),
//				comprobante.getFechaAutorizacion(),
//				comprobante.getAutorizacion(), comprobante.getNumeroIntento(),
//				comprobante.getEstado(), comprobante.getUltimoCodigoError(),
//				comprobante.getClaveAcceso());
		if (continuar) {
			mensaje.setComprobanteProcesando(comprobante);
			mensaje.setEstadoProceso(comprobante.getEstado());
			try {
				servicioJMS.registrarMensajeIndividual(mensaje);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void firmarNotaCredito(MensajeIndividual mensaje) {
		boolean continuar = true;
		NotaCredito retencion = mensaje.getNotaCredito();
		String emailsCliente = "";
		String vendedor = "";
		if (retencion.getInfoAdicional() != null) {
			if (retencion.getInfoAdicional().getCampoAdicional() != null) {
				List<com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.NotaCredito.InfoAdicional.CampoAdicional> listaCampos = retencion
						.getInfoAdicional().getCampoAdicional();
				for (com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.NotaCredito.InfoAdicional.CampoAdicional campo : listaCampos) {
					if (campo.getNombre().toUpperCase().equals(TAGCORREO) || campo.getNombre().toUpperCase().equals(TAGCORREO3)) {
						emailsCliente = campo.getValue();
						emailsCliente = emailsCliente.replaceAll(",", ";");						
					}else if(campo.getNombre().toUpperCase().equals(TAGVENDEDOR)){
						vendedor= campo.getValue();
					}
//					else if (campo.getNombre().toUpperCase().equals(TAGCORREO2)) {
//						emailsCliente = campo.getValue();
//						emailsCliente = emailsCliente.replaceAll(",", ";");	
//					}
				}
			}
		}
		Double imp12=0.0;
	    Double imp0=0.0;
	    for(com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.TotalConImpuestos.TotalImpuesto  imp: retencion.getInfoNotaCredito().getTotalConImpuestos().getTotalImpuesto())
	    	{
	    	if(!imp.getCodigoPorcentaje().equals("0")){
	    		imp12=imp.getValor().doubleValue();
	    	}else if(imp.getCodigoPorcentaje().equals("0")){
	    		imp0=imp.getValor().doubleValue();
	    	}
	    }
		GeDatosGeneralesCompania buscarComp =  servicioParametros.obtenerCompaniaPorRUC(retencion.getInfoTributaria().getRuc());
		HashMap<String, Object> datosCadenaLocal =servicioParametros.obtenerCadenaLocal(buscarComp.getId(),
				retencion.getInfoTributaria().getEstab(),
				retencion.getInfoTributaria().getPtoEmi());
		
		ComprobanteProcesadoIndividual comprobante = obtenerUltComprobante(
				ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO,
				retencion.getInfoTributaria().getClaveAcceso(),
				mensaje.getCompania(), retencion.getVersion(),
				mensaje.getAmbiente(),
				retencion.getInfoTributaria().getEstab(), retencion
						.getInfoTributaria().getPtoEmi(), retencion
						.getInfoTributaria().getSecuencial(),
				convierteCadenaAFecha(retencion.getInfoNotaCredito()
						.getFechaEmision()), retencion.getInfoNotaCredito()
						.getIdentificacionComprador(), retencion
						.getInfoNotaCredito().getRazonSocialComprador(),
				retencion, NotaCredito.class, emailsCliente, 
				retencion.getInfoNotaCredito().getValorModificacion()==null?0.0:retencion.getInfoNotaCredito().getValorModificacion().doubleValue(),
						buscarComp.getIdSuscriptor(),vendedor,
				retencion.getInfoNotaCredito().getTotalSinImpuestos()==null?0.0:retencion.getInfoNotaCredito().getTotalSinImpuestos().doubleValue(),
				retencion.getInfoNotaCredito().getNumDocModificado(),0.0,imp12,imp0,mensaje.getNombreArchivo(), mensaje.getIdEmpresaCliente(),
				datosCadenaLocal.get("local")==null?null:((BigDecimal)datosCadenaLocal.get("local")).intValue(),
						datosCadenaLocal.get("cadena")==null?null:((BigDecimal)datosCadenaLocal.get("cadena")).intValue());
		boolean isNew = comprobante.getNumeroIntento() < 2;
		try {
			comprobante.setFechaInicioFirmado(new Date());
			Document documento = parseDocumentoComprobante(retencion,
					NotaCredito.class);
			String archivoGenerado = generadorNombre(
					retencion.getInfoTributaria().getClaveAcceso(), 
					mensaje.getCompania(), 
					ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO,
					INombresParametros.PREFIJO_XML_FIRMADO,
					INombresParametros.SUFIJO_XML_FIRMADO);
			String comprobanteFirmado = firmarComprobanteIndividual(documento,
					retencion.getInfoTributaria().getClaveAcceso(),
					mensaje.getCompania(), "comprobante", "comprobateFirma",
					ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO,archivoGenerado,isNew);
			if (comprobanteFirmado==null){
				throw new ExcepcionValidacionComprobantes();
			}		
			comprobante.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(comprobanteFirmado,comprobante.getNumDocumento()));
			comprobante.setObservacion("ARCHIVO FIRMADO CORRECTAMENTE");
			comprobante.setEstado("F");
			comprobante.setFechaFinFirmado(new Date());
			comprobante.setRutaArchivoFirmado(archivoGenerado);
			continuar = true;
		}
		catch(ExcepcionValidacionComprobantes ev){			
			comprobante.setObservacion("ERROR AL EL TAMANIO DEL ARCHIVO SUPERA LO PARAMETRIZADO ");
			comprobante.setEstado("RC");
			comprobante.setError("El tamanio del XML firmado supera al tamanio parametrizado en CANTIDAD_MAXIMA_KB_XML");
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
			armarEnvioCorreoErrorXml(comprobante, comprobante.getXmlRespuestaComprobacion());
		}catch (Throwable e) {
			e.printStackTrace();
			comprobante.setObservacion("ERROR AL FIRMAR EL ARCHIVO DE COMPROBANTE "	+ e.getMessage());
			comprobante.setEstado("XF");
			comprobante.setError(parsearPilaErroresJava(e));
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
		}
		loggin.actualizarComprobanteIndividual(comprobante);
//		comprobantesIndividual.actualizarDatosCabecera(
//				comprobante.getFechaSolAutorizacion(),
//				comprobante.getFechaAutorizacion(),
//				comprobante.getAutorizacion(), comprobante.getNumeroIntento(),
//				comprobante.getEstado(), comprobante.getUltimoCodigoError(),
//				comprobante.getClaveAcceso());
		if (continuar) {
			mensaje.setComprobanteProcesando(comprobante);
			mensaje.setEstadoProceso(comprobante.getEstado());
			try {
				servicioJMS.registrarMensajeIndividual(mensaje);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void firmarComprobanteGuia(MensajeIndividual mensaje) {
		boolean continuar = true;
		GuiaRemision retencion = mensaje.getGuia();
		String emailsCliente = "";
		if (retencion.getInfoAdicional() != null) {
			if (retencion.getInfoAdicional().getCampoAdicional() != null) {
				List<com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.guiaremision.GuiaRemision.InfoAdicional.CampoAdicional> listaCampos = retencion
						.getInfoAdicional().getCampoAdicional();
				for (com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.guiaremision.GuiaRemision.InfoAdicional.CampoAdicional campo : listaCampos) {
					if (campo.getNombre().toUpperCase().equals(TAGCORREO) || campo.getNombre().toUpperCase().equals(TAGCORREO3)) {
						emailsCliente = campo.getValue();
						emailsCliente = emailsCliente.replaceAll(",", ";");
						break;
					}
//					else if (campo.getNombre().toUpperCase().equals(TAGCORREO2)) {
//						emailsCliente = campo.getValue();
//						break;
//					}
				}
			}
		}
		GeDatosGeneralesCompania buscarComp =  servicioParametros.obtenerCompaniaPorRUC(retencion.getInfoTributaria().getRuc());
		
		HashMap<String, Object> datosCadenaLocal =servicioParametros.obtenerCadenaLocal(buscarComp.getId(),
				retencion.getInfoTributaria().getEstab(),
				retencion.getInfoTributaria().getPtoEmi());
		
		ComprobanteProcesadoIndividual comprobante = obtenerUltComprobante(
				ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION,
				retencion.getInfoTributaria().getClaveAcceso(),	mensaje.getCompania(), retencion.getVersion(),
				mensaje.getAmbiente(),retencion.getInfoTributaria().getEstab(), retencion.getInfoTributaria().getPtoEmi(), 
				retencion.getInfoTributaria().getSecuencial(),convierteCadenaAFecha(retencion.getInfoGuiaRemision().getFechaIniTransporte()), 
				retencion.getDestinatarios().getDestinatario().get(0).getIdentificacionDestinatario(), 
				retencion.getDestinatarios().getDestinatario().get(0).getRazonSocialDestinatario(), retencion, GuiaRemision.class, emailsCliente
				, 0D,buscarComp.getIdSuscriptor(),"",0.0,"",0.0,0.0,0.0,mensaje.getNombreArchivo(), mensaje.getIdEmpresaCliente(),
				datosCadenaLocal.get("local")==null?null:((BigDecimal)datosCadenaLocal.get("local")).intValue(),
						datosCadenaLocal.get("cadena")==null?null:((BigDecimal)datosCadenaLocal.get("cadena")).intValue()
				);
		boolean isNew = comprobante.getNumeroIntento() < 2;
		try {
			comprobante.setFechaInicioFirmado(new Date());
			Document documento = parseDocumentoComprobante(retencion,GuiaRemision.class);
			String archivoGenerado = generadorNombre(retencion.getInfoTributaria().getClaveAcceso(),mensaje.getCompania(), 
					ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION,
					INombresParametros.PREFIJO_XML_FIRMADO,
					INombresParametros.SUFIJO_XML_FIRMADO);
			String comprobanteFirmado = firmarComprobanteIndividual(documento,
					retencion.getInfoTributaria().getClaveAcceso(),
					mensaje.getCompania(), "comprobante", "comprobateFirma",
					ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION,archivoGenerado,isNew);
			if (comprobanteFirmado==null){
				throw new ExcepcionValidacionComprobantes();
			}				
			comprobante.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(comprobanteFirmado,comprobante.getNumDocumento()));
			comprobante.setObservacion("ARCHIVO FIRMADO CORRECTAMENTE");
			comprobante.setEstado("F");
			comprobante.setFechaFinFirmado(new Date());
			comprobante.setRutaArchivoFirmado(archivoGenerado);
			continuar = true;
		}
		catch(ExcepcionValidacionComprobantes ev){			
			comprobante.setObservacion("ERROR AL EL TAMANIO DEL ARCHIVO SUPERA LO PARAMETRIZADO ");
			comprobante.setEstado("RC");
			comprobante.setError("El tamanio del XML firmado supera al tamanio parametrizado en CANTIDAD_MAXIMA_KB_XML");
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
			armarEnvioCorreoErrorXml(comprobante, comprobante.getXmlRespuestaComprobacion());
		}catch (Throwable e) {
			e.printStackTrace();
			comprobante.setObservacion("ERROR AL FIRMAR EL ARCHIVO DE COMPROBANTE "+ e.getMessage());
			comprobante.setEstado("XF");
			comprobante.setError(parsearPilaErroresJava(e));
			continuar = false;
			comprobante.setFechaFinProceso(new Date());
		}
		loggin.actualizarComprobanteIndividual(comprobante);
//		comprobantesIndividual.actualizarDatosCabecera(
//				comprobante.getFechaSolAutorizacion(),
//				comprobante.getFechaAutorizacion(),
//				comprobante.getAutorizacion(), comprobante.getNumeroIntento(),
//				comprobante.getEstado(), comprobante.getUltimoCodigoError(),
//				comprobante.getClaveAcceso());
		if (continuar) {
			mensaje.setComprobanteProcesando(comprobante);
			mensaje.setEstadoProceso(comprobante.getEstado());
			try {
				servicioJMS.registrarMensajeIndividual(mensaje);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private String parsearPilaErroresJava(Throwable e) {
		StringWriter error = new StringWriter();
		StringWriter causa = new StringWriter();
		if(e.getCause() != null)
			e.getCause().printStackTrace(new PrintWriter(causa));
		e.printStackTrace(new PrintWriter(error));
		return (causa.toString() + "\n" + error.toString()).substring(0, 2999);
	}
	
	//CAMBIO OFFLINE 
	private String tranformarAutorizacionOffLine(com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.Autorizacion autorizacion, String clase) {
		try {			
			StringWriter str = new StringWriter();			
			com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.ObjectFactory objectFactoryAutorizacion = new com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.ObjectFactory();
			JAXBContext contextAutorizacion = JAXBContext.newInstance(clase);
			JAXBElement<Object> eRespuestaComprobante = objectFactoryAutorizacion.createRespuestaAutorizacion(autorizacion);
			Marshaller marshallerAutorizacion = contextAutorizacion.createMarshaller();
			marshallerAutorizacion.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshallerAutorizacion.setProperty(Marshaller.JAXB_ENCODING,"utf-8");			
			marshallerAutorizacion.marshal(eRespuestaComprobante, str);			
			return str.toString();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	//CAMBIO OFFLINE
	public String tranformarRespuestaComprobacionOffLine(com.producto.comprobanteselectronicos.comprobador.desarrollo.normal.offline.RespuestaSolicitud respuesta,String clase) {
		try {
			StringWriter str = new StringWriter();
			com.producto.comprobanteselectronicos.comprobador.desarrollo.normal.offline.ObjectFactory factry = new com.producto.comprobanteselectronicos.comprobador.desarrollo.normal.offline.ObjectFactory();
			JAXBContext contexto = JAXBContext.newInstance(clase);
			JAXBElement<com.producto.comprobanteselectronicos.comprobador.desarrollo.normal.offline.RespuestaSolicitud> test = factry.createRespuestaSolicitud(respuesta);
			Marshaller msh = contexto.createMarshaller();
			msh.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			msh.marshal(test, str);
			return str.toString();
		} catch (Throwable e) {
			return null;
		}
	}

	//AQUI CAMBIO OFFLINE
	private void envioAutorizacion(ComprobanteProcesadoIndividual comprobante)	throws Throwable {
		String cod_advertencia = null, cod_error = null;
		//obtener el suscriptor del comprobante
		//GeClientesSuscriptor clienteSuscriptor = servicioParametros.consultaSuscriptorCliente(comprobante.getIdSuscriptor());
		//bandera almacear archivo 
		String banderaAlmacenarFile = servicioParametros.getValorParametro("BANDERA_ALMACENAR_ARCHIVOS",comprobante.getIdSuscriptor()) ;
		banderaAlmacenarFile=banderaAlmacenarFile==null?"":banderaAlmacenarFile;
	//	URLWSSRIAutorizacion = servicioParametros.getValorParametro(INombresParametros.PROCESO_AUTORIZACION_INDIVIDUAL_URL_OFFLINE);
		Integer numerMaximoIntentos = new Integer(servicioParametros.getValorParametro("MAX_REINTENTOS_LIBERAR"));
		//RUTA ARCHIVOS AUT - NO AUT
		String rutaAlmacenarXml = servicioParametros.getValorParametro("RUTA_CARGA_COMPROBANTES_TMP",comprobante.getIdSuscriptor());
		File file = null;
		//String carpetaReferencia =clienteSuscriptor.getNombreContacto().toLowerCase().replace(" ", "_");
		String rutaAut = rutaAlmacenarXml+"/autorizado/";
		String rutaNoAut = rutaAlmacenarXml+"/no_autorizado/";
		String nomArchXml = getTipoDocumento(comprobante.getTipoDocumento())+"-"+comprobante.getNumDocumento()+ ".xml";
		try {
			comprobante.setFechaSolAutorizacion(new Date());
			RespuestaComprobante respuestaComprobante = AutorizacionComprobante.obtieneAutorizacionComprobante(comprobante.getClaveAcceso(), comprobante.getAmbiente(), RespuestaComprobante.class, URLWSSRIAutorizacion);			
			List<Autorizacion> autorizaciones = respuestaComprobante.getAutorizaciones().getAutorizacion();
			Autorizacion respuestaAutorizado = null;
			Autorizacion respuestaXA = null;
			if(autorizaciones.size() == 0){
				if(comprobante.getNumeroIntento() % numerMaximoIntentos == 0){
					throw new NullPointerException("No se pudo obtener la autorizacion "+ "del comprobante en los intentos configurados. "+ "Se volvera a enviar al SRI");
				}else
					throw new TimeoutException("El SRI no devolvio respuesta para el comprobante " + comprobante.getClaveAcceso());
			}
			for (Autorizacion aut : autorizaciones) {
				respuestaXA = autorizaciones.get(0);
				if (aut.getEstado().equals(INombresParametros.RESPUESTA_RECIBIDA_AUTORIZACION_SRI)) {
					respuestaAutorizado = aut;
				}
				else if (aut.getEstado().equals(INombresParametros.RESPUESTA_RECIBIDA_EN_PROCESO_SRI)) {
				     System.err.println("Se encontro clave de acceso en "+comprobante.getClaveAcceso()+" EN PROCESO");
				     comprobante.setEstado("E");
				     comprobante.setError("ERROR - NO SE OBTUVO LA AUTORIZACION PORQUE EL SRI DEVOLVIO RESPUESTA EN PROCESO Y NO DEVOLVIO CODIGO DE ERROR");
				     comprobante.setObservacion("SE REALIZO LA CONSULTA DE AUTORIZACION SIN TENER RESPUESTA, POSIBLE ERROR 70."
				         + "SE INSERTA EL COMPROBANTE EN ESTADO INICIAL (E) PARA COMENZAR EL PROCESO NUEVAMENTE ");
				     return;
				    }
			}
			try {
				for (Mensaje msj : autorizaciones.get(0).getMensajes().getMensaje()) {
					if (msj.getTipo().equals("ERROR"))
						cod_error = msj.getIdentificador();
					else
						cod_advertencia = msj.getIdentificador();
				}
				comprobante.setUltimoCodigoError(cod_error != null ? cod_error	: cod_advertencia);			
			} catch (Throwable t) {
				System.err.println("Warning obtener codigos de ultimo error " + t.getMessage());
			}
									
			if (respuestaAutorizado != null) {
				System.err.println("AUTORIZACION:envioAutorizacion se autorizo ["+comprobante.getClaveAcceso()+"]");
				comprobante.setEstado("A");
				Date fechaAutorizacion = respuestaAutorizado.getFechaAutorizacion().toGregorianCalendar().getTime();
				comprobante.setFechaAutorizacion(fechaAutorizacion);
				comprobante.setAutorizacion(respuestaAutorizado.getNumeroAutorizacion());
				comprobante.setObservacion("SERVICIO WEB AUTORIZACION - EL COMPROBANTE FUE AUTORIZADO POR EL SRI");
				
				String xmlAut = tranformarAutorizacionOffLine(respuestaAutorizado,INombresParametros.CLASE_AUTORIZACION_JAXB_DESA_OFFLINE);				
				comprobante.setXmlRespuestaAutorizacion(ModuladorComprobanteComprimido.comprimirXML(xmlAut,	comprobante.getNumDocumento()));
				/////////////////////////////////////////////////////////////////////////////
				//ELIMINAR XML FIRMADO
				System.err.println("la ruta del aricho firmado es "+comprobante.getRutaArchivoFirmado());
				File rutaXmlFirmado = new File(comprobante.getRutaArchivoFirmado());
				if(rutaXmlFirmado.exists()){
					if (rutaXmlFirmado.delete())
					   System.err.println("XML firmado borrado con exito");
					else
					   System.err.println("XML no puede ser borrado");
				}else
					System.err.println("EL archivo XML no existe");
								
			} else {
				comprobante.setEstado("XA");
				System.err.println("NO SE AUTORIZO: envioAutorizacion se quedo en XA ["+comprobante.getClaveAcceso()+"]");
				String xmlNoAut = tranformarAutorizacionOffLine(respuestaXA,INombresParametros.CLASE_AUTORIZACION_JAXB_DESA_OFFLINE);
				comprobante.setXmlRespuestaAutorizacion(ModuladorComprobanteComprimido.comprimirXML(xmlNoAut, comprobante.getNumDocumento()));
				comprobante.setObservacion("EL SRI NO AUTORIZO EL COMPROBANTE, POR FAVOR REVISE LA RESPUESTA XML RECIBIDA");
				comprobante.setRespuestaAutorizacion("NO AUTORIZADA");
				comprobante.setFechaEnvioMail(null);
				///////////////////ENVIO DE MAIL COMPROBANTE XA//////////////////////////
				System.out.println("SE ENVIA CORREO POR COMPROBANTE XA");
				armarEnvioCorreoErrorXml(comprobante, comprobante.getXmlRespuestaAutorizacion());	
			}
		} catch (NullPointerException e) {
			System.err.println("ERROR  en envioAutorizacion se quedo en TAA ["+comprobante.getClaveAcceso()+"]");
			comprobante.setEstado("TAA");
			comprobante.setError("ERROR - NO SE OBTUVO LA ATORIZACION POR QUE EL SRI RECIBIO EL COMPROBANTE CUANDO EL SERVICIO DE COMPROBACION"
					+ " SE ENCONTRABA INESTABLE, ES POSIBLE QUE EL COMPROBANTE EL SRI NO LO TENGA EN SU SISTEMA SE PUEDE VOLVER A ENVIAR");
			comprobante.setObservacion("SE REALIZO LA CONSULTA DE AUTORIZACION VARIAS VECES SIN TENER RESPUESTA, "
							+ "SI EL CODIGO DE ERROR ES 70 PUEDE SER QUE EL SRI NECESITE RECIBIR NUEVAMENTE EL COMPROBANTE "
							+ comprobante.getUltimoCodigoError());
		}catch(Exception error){
			comprobante.setEstado("EDA");
			System.err.println("ERROR en envioAutorizacion se quedo en EDA ["+comprobante.getClaveAcceso()+"] con error "+comprobante.getUltimoCodigoError());
			comprobante.setError(parsearPilaErroresJava(error));
			comprobante.setObservacion("ERROR al consultar al servicio web de AUTORIZACION");
			System.err.println("SE ELIMINO EL XML POR QUE ES EDA -- "+file.getName());
			file.delete();
		}
	}

	//CAMBIO OFFLINE 	
	private void envioComprobacion(ComprobanteProcesadoIndividual comprobante)throws Throwable {
		try {
			//URLWSSRIComprobacion = servicioParametros.getValorParametro(INombresParametros.PROCESO_COMPROBACION_INDIVIDUAL_URL_OFFLINE);
			RespuestaSolicitud respuesta = ComprobacionNormal.comprobarDocumento(comprobante.getRutaArchivoFirmado(), comprobante.getAmbiente(), 
					RespuestaSolicitud.class, URLWSSRIComprobacion);
			comprobante.setFechaComprobacion(new Date());
			comprobante.setXmlRespuestaComprobacion(ModuladorComprobanteComprimido.comprimirXML(tranformarRespuestaComprobacionOffLine(respuesta,
					INombresParametros.CLASE_COMPROBACION_JAXB_DESA_OFFLINE),comprobante.getNumDocumento()));
			comprobante.setRespuestaComprobacion(respuesta.getEstado());			
			try {
				comprobante.setUltimoCodigoError(respuesta.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getIdentificador());
			} catch (Throwable e) {
				System.err.println("Warning obtener codigos de ultimo error " + e.getMessage());
			}
			if (respuesta.getEstado().toUpperCase().equals(INombresParametros.RESPUESTA_RECIBIDA_COMPROBACION_SRI)) {
				System.err.println("comprobante clave acceso "+comprobante.getClaveAcceso());
				System.err.println("el comprobante tiene envio mail "+comprobante.getFechaEnvioMail());
				System.err.println("tiene doc enviado = "+comprobante.getDocumentoEnviadoMail());
				if(comprobante.getXmlComprobante() != null && comprobante.getDocumentoEnviadoMail() == null){
					System.err.println("--------- se va a enviar el correo con xml firmado -------------"); 
					enviarCorreoFirmado(comprobante);
					comprobante.setDocumentoEnviadoMail(1);
				}
				comprobante.setEstado("C");
				comprobante.setObservacion("SERVICIO WEB COMPROBACION - EL COMPROBANTE FUE RECIBIDO POR EL SRI ");
			} else {
				System.err.println("ES UN 70 O 43 ----------- NO PUDO SETEAR EN C -------- envioComprobacion");				
				if ("70".equals(comprobante.getUltimoCodigoError())){
					comprobante.setEstado("EDA");
					comprobante.setObservacion("COMPROBANTE RECIBIDO POR EL SRI - SERVICIO WEB ES INESTABLE ERROR 70");
					comprobante.setRespuestaComprobacion("RECIBIDA");
				} else if("43".equals(comprobante.getUltimoCodigoError())){
					comprobante.setEstado("EDA");
					comprobante.setObservacion("EL DOCUMENTO PUEDE YA ESTAR AUTORIZADO, SOLO SE CONSULTARA LA AUTORIZACION");
					comprobante.setRespuestaComprobacion("RECIBIDA");
				}else {
					comprobante.setEstado("RC");
					comprobante.setObservacion("EL DOCUMENTO FUE RECHAZADO POR EL SRI, POR FAVOR REVISAR LA RESPUESTA");
					armarEnvioCorreoErrorXml(comprobante, comprobante.getXmlRespuestaComprobacion());
				}				
				System.err.println("----------------- el comprobante "+comprobante.getClaveAcceso()+" se quedo con estado  "+comprobante.getEstado());				
			}
		} catch (TimeoutException error) {
			comprobante.setEstado("TDC");
			comprobante.setError(parsearPilaErroresJava(error));
			comprobante.setObservacion("ERROR DE TIMEOUT DURANTE LA COMPROBACION");
		} catch (SocketTimeoutException error) {
			comprobante.setEstado("TSC");
			comprobante.setError(parsearPilaErroresJava(error));
			comprobante.setObservacion("ERROR DE Socket Timeout Exception LA COMPROBACION");
		} catch (UnknownHostException error) {
			comprobante.setEstado("EHC");
			comprobante.setError(parsearPilaErroresJava(error));
			comprobante.setObservacion("ERROR DE Unknown Host Exception LA COMPROBACION");
		}
	}

	private void validacionClave(String claveAcceso) throws Throwable {
		System.err.println("++++++++++++++++++++++++++++++++++++Clave de acceso recibida "+ claveAcceso);
//		ComprobanteProcesadoIndividual comprobante = loggin.obtenerComprobanteIndividual(claveAcceso, INombresParametros.ESTADO_AUTORIZADO);
//		if (comprobante != null) {
//			throw new Throwable("El comprobante con clave de acceso "+ comprobante.getClaveAcceso()
//					+ ", ya tiene un numero autorizacion "+ comprobante.getAutorizacion()
//					+ ", no se permite enviar el comprobante nuevamente");
//		}
		Boolean existeClaveAccesoAut = loggin.validaExisteClaveAccesoAut(claveAcceso, INombresParametros.ESTADO_AUTORIZADO);
		if(existeClaveAccesoAut){
			System.out.println("El comprobante con clave de acceso "+ claveAcceso
					+ ", ya tiene un numero autorizacion "+ claveAcceso
					+ ", no se permite enviar el comprobante nuevamente");
			throw new Throwable("-1");
		}
	}
	
	//cambios bse
	@TransactionAttribute(value= TransactionAttributeType.SUPPORTS)
	public String recepcionXmlComprobante(String archivoXML, String nombreArchivo, String codigoInterno) {
		String tipoDocumento = null;
		String rucEmisor = null;
		String claveAcceso = null;
		String compania = null;		
		archivoXML=archivoXML.replaceAll(" & ", "&amp;");
		//String claveTmp =obtenerTagXml(archivoXML, "claveAcceso");
		
		try {
			if (archivoXML == null)
				throw new Throwable("El parametro xmlComprobante esta vacio");
			tipoDocumento = obtieneTipoDocumento(archivoXML);
			GeDatosGeneralesCompania buscarComp = null;
			GeDatosGeneralesCompania corlasosa = servicioParametros.obtenerCompaniaPorRUC("0992161175001");
			switch (tipoDocumento) {
			case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA:				
				Factura fac = convierteDocumentAClaseFactura(crearXMLComprobanteRetencion(archivoXML));				
				buscarComp = servicioParametros.obtenerCompaniaPorRUC(fac.getInfoTributaria().getRuc());			
				
				if (buscarComp != null) {
					compania = StringUtils.leftPad(String.valueOf(buscarComp.getId()), 2,"0");
					if (buscarComp.getCertificadoComprobante() == null) {
						compania = StringUtils.leftPad(String.valueOf(corlasosa.getId()), 2,"0");
						fac.getInfoTributaria().setPtoEmi("001");
						fac.getInfoTributaria().setEstab("001");
						fac.getInfoTributaria().setRuc(corlasosa.getIdentificacion());
					}
				}else{
					throw new Throwable("EL RUC DEL EMISOR : " + fac.getInfoTributaria().getRuc()+ " NO CORRESPONDE A NINGUNA EMPRESA CONFIGURADA");
				}
				if (fac.getInfoTributaria().getClaveAcceso() == null || fac.getInfoTributaria().getClaveAcceso().equals("")
						|| fac.getInfoTributaria().getClaveAcceso().equals("0")|| fac.getInfoTributaria().getClaveAcceso().equals("1")) {
					fac.getInfoTributaria().setClaveAcceso(GeneradorClaveAcceso.generarClaveDeAcceso(
						fac.getInfoFactura().getFechaEmision().replace("/", ""), ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA,
						fac.getInfoTributaria().getRuc(), fac.getInfoTributaria().getAmbiente(), new Long(fac.getInfoTributaria().getEstab()
						+ fac.getInfoTributaria().getPtoEmi()), new Long(fac.getInfoTributaria().getSecuencial()),12345678L, fac.getInfoTributaria().getTipoEmision()));
				}
				claveAcceso = fac.getInfoTributaria().getClaveAcceso();
				validacionClave(claveAcceso);

				if (fac.getId() == null || fac.getId().equals(""))
					fac.setId("comprobante");
				fac.getInfoTributaria().setSecuencial(StringUtils.leftPad(fac.getInfoTributaria().getSecuencial(), 9, "0"));
				for (Factura.Detalles.Detalle det : fac.getDetalles().getDetalle()) {
					if (det.getDetallesAdicionales() != null) {
						if (det.getDetallesAdicionales().getDetAdicional().size() > 3) {
							int i = 3;
							for (i = 3; i < det.getDetallesAdicionales().getDetAdicional().size(); i++) {
								det.getDetallesAdicionales().getDetAdicional().get(2).setNombre(det.getDetallesAdicionales().getDetAdicional().get(2).getNombre()
								+ "/"+ det.getDetallesAdicionales().getDetAdicional().get(i).getNombre());
								det.getDetallesAdicionales().getDetAdicional().get(2).setValor(det.getDetallesAdicionales().getDetAdicional().get(2).getValor()
								+ "/"+ det.getDetallesAdicionales().getDetAdicional().get(i).getValor());
							}							
							while (det.getDetallesAdicionales().getDetAdicional().size() > 3){
								det.getDetallesAdicionales().getDetAdicional().remove(det.getDetallesAdicionales().getDetAdicional().size()-1);
							}
						}
						if (det.getDetallesAdicionales().getDetAdicional().size() == 0) {
							det.setDetallesAdicionales(null);
						}
					}
				}
				registrarColaMensajes(compania,INombresParametros.PROCESO_COMPROBANTE_INDIVIDUAL_NOMBRE_FACTURA,"SEED_BILLING",
					ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA, "", false, fac.getInfoTributaria().getTipoEmision(),
					fac.getVersion(), fac.getInfoTributaria().getAmbiente(), fac, null, null, null, null, nombreArchivo, codigoInterno);
				break;
			case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION:
				ComprobanteRetencion ret = convierteDocumentARetencion(crearXMLComprobanteRetencion(archivoXML));
				buscarComp = servicioParametros.obtenerCompaniaPorRUC(ret.getInfoTributaria().getRuc());
				if(buscarComp != null){
					compania = StringUtils.leftPad(String.valueOf(buscarComp.getId()), 2, "0");
					if (buscarComp.getCertificadoComprobante() == null) {
						compania = StringUtils.leftPad(String.valueOf(corlasosa.getId()), 2,"0");
						ret.getInfoTributaria().setPtoEmi("001");
						ret.getInfoTributaria().setEstab("001");
						ret.getInfoTributaria().setRuc(corlasosa.getIdentificacion());
					}
				}else{
					throw new Throwable("EL RUC DEL EMISOR : " + rucEmisor
							+ " NO CORRESPONDE A NINGUNA EMPRESA CONFIGURADA");
				}
				if (ret.getInfoTributaria().getClaveAcceso() == null
						|| ret.getInfoTributaria().getClaveAcceso().equals("")
						|| ret.getInfoTributaria().getClaveAcceso().equals("0")|| ret.getInfoTributaria().getClaveAcceso().equals("1")) {
					ret.getInfoTributaria()
							.setClaveAcceso(
									GeneradorClaveAcceso
											.generarClaveDeAcceso(
													ret.getInfoCompRetencion()
															.getFechaEmision()
															.replace("/", ""),
													ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION,
													ret.getInfoTributaria()
															.getRuc(),
													ret.getInfoTributaria()
															.getAmbiente(),
													new Long(
															ret.getInfoTributaria()
																	.getEstab()
																	+ ret.getInfoTributaria()
																			.getPtoEmi()),
													new Long(
															ret.getInfoTributaria()
																	.getSecuencial()),
													12345678L,
													ret.getInfoTributaria()
															.getTipoEmision()));
				}
				claveAcceso = ret.getInfoTributaria().getClaveAcceso();
				validacionClave(claveAcceso);
				if (ret.getId() == null || ret.getId().equals(""))
					ret.setId("comprobante");
				ret.getInfoTributaria().setSecuencial(
						StringUtils.leftPad(ret.getInfoTributaria()
								.getSecuencial(), 9, "0"));
				registrarColaMensajes(
						compania,
						INombresParametros.PROCESO_COMPROBANTE_INDIVIDUAL_NOMBRE_RETENCION,
						"SEED_BILLING",
						ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION, "",
						false, ret.getInfoTributaria().getTipoEmision(),
						ret.getVersion(),
						ret.getInfoTributaria().getAmbiente(), null, ret, null,
						null, null, nombreArchivo, codigoInterno);
				break;
			case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION:
				GuiaRemision guiaRemi = convierteDocumentAClaseGuiaRemision(crearXMLComprobanteRetencion(archivoXML));
				buscarComp = servicioParametros.obtenerCompaniaPorRUC(guiaRemi.getInfoTributaria().getRuc());
				if(buscarComp != null){
					compania = StringUtils.leftPad(String.valueOf(buscarComp.getId()), 2,"0");
					if (buscarComp.getCertificadoComprobante() == null) {
						compania = StringUtils.leftPad(String.valueOf(corlasosa.getId()), 2,"0");
						guiaRemi.getInfoTributaria().setPtoEmi("001");
						guiaRemi.getInfoTributaria().setEstab("001");
						guiaRemi.getInfoTributaria().setRuc(corlasosa.getIdentificacion());
					}
				}else{
					throw new Throwable("EL RUC DEL EMISOR : " + rucEmisor
							+ " NO CORRESPONDE A NINGUNA EMPRESA CONFIGURADA");
				}				
				if (guiaRemi.getInfoTributaria().getClaveAcceso() == null
						|| guiaRemi.getInfoTributaria().getClaveAcceso().equals("")
						|| guiaRemi.getInfoTributaria().getClaveAcceso().equals("0")|| guiaRemi.getInfoTributaria().getClaveAcceso().equals("1")) {
					guiaRemi.getInfoTributaria()
							.setClaveAcceso(
									GeneradorClaveAcceso
											.generarClaveDeAcceso(
													guiaRemi.getInfoGuiaRemision()
															.getFechaIniTransporte()
															.replace("/", ""),
													ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION,
													guiaRemi.getInfoTributaria()
															.getRuc(),
													guiaRemi.getInfoTributaria()
															.getAmbiente(),
													new Long(
															guiaRemi.getInfoTributaria()
																	.getEstab()
																	+ guiaRemi
																			.getInfoTributaria()
																			.getPtoEmi()),
													new Long(
															guiaRemi.getInfoTributaria()
																	.getSecuencial()),
													12345678L,
													guiaRemi.getInfoTributaria()
															.getTipoEmision()));
				}
				claveAcceso = guiaRemi.getInfoTributaria().getClaveAcceso();
				validacionClave(claveAcceso);
				if (guiaRemi.getId() == null || guiaRemi.getId().equals(""))
					guiaRemi.setId("comprobante");
				guiaRemi.getInfoTributaria().setSecuencial(StringUtils.leftPad(guiaRemi.getInfoTributaria().getSecuencial(), 9, "0"));
				registrarColaMensajes(compania,	INombresParametros.PROCESO_COMPROBANTE_INDIVIDUAL_NOMBRE_GUIA,
						"SEED_BILLING",	ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION,
						"", false, guiaRemi.getInfoTributaria().getTipoEmision(), guiaRemi.getVersion(),
						guiaRemi.getInfoTributaria().getAmbiente(), null, null,	guiaRemi, null, null, nombreArchivo, codigoInterno);
				break;
			case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO:
				NotaCredito notaCredito = convierteDocumentAClaseNotaCredito(crearXMLComprobanteRetencion(archivoXML));
				buscarComp = servicioParametros.obtenerCompaniaPorRUC(notaCredito.getInfoTributaria().getRuc());
				if(buscarComp != null){
					compania = StringUtils.leftPad(String.valueOf(buscarComp.getId()), 2, "0");
					if (buscarComp.getCertificadoComprobante() == null) {
						compania = StringUtils.leftPad(String.valueOf(corlasosa.getId()), 2,"0");
						notaCredito.getInfoTributaria().setPtoEmi("001");
						notaCredito.getInfoTributaria().setEstab("001");
						notaCredito.getInfoTributaria().setRuc(corlasosa.getIdentificacion());
					}
				}else{
					throw new Throwable("EL RUC DEL EMISOR : " + rucEmisor
							+ " NO CORRESPONDE A NINGUNA EMPRESA CONFIGURADA");
				}
				if (notaCredito.getInfoTributaria().getClaveAcceso() == null
						|| notaCredito.getInfoTributaria().getClaveAcceso()
								.equals("")
						|| notaCredito.getInfoTributaria().getClaveAcceso().equals("0")|| notaCredito.getInfoTributaria().getClaveAcceso().equals("1")) {
					notaCredito
							.getInfoTributaria()
							.setClaveAcceso(
									GeneradorClaveAcceso
											.generarClaveDeAcceso(
													notaCredito
															.getInfoNotaCredito()
															.getFechaEmision()
															.replace("/", ""),
													ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO,
													notaCredito
															.getInfoTributaria()
															.getRuc(),
													notaCredito
															.getInfoTributaria()
															.getAmbiente(),
													new Long(
															notaCredito
																	.getInfoTributaria()
																	.getEstab()
																	+ notaCredito
																			.getInfoTributaria()
																			.getPtoEmi()),
													new Long(
															notaCredito
																	.getInfoTributaria()
																	.getSecuencial()),
													12345678L,
													notaCredito
															.getInfoTributaria()
															.getTipoEmision()));
				}

				claveAcceso = notaCredito.getInfoTributaria().getClaveAcceso();
				validacionClave(claveAcceso);

				if (notaCredito.getId() == null
						|| notaCredito.getId().equals(""))
					notaCredito.setId("comprobante");
				notaCredito.getInfoTributaria().setSecuencial(
						StringUtils.leftPad(notaCredito.getInfoTributaria()
								.getSecuencial(), 9, "0"));
				for (NotaCredito.Detalles.Detalle det : notaCredito
						.getDetalles().getDetalle()) {
					if (det.getDetallesAdicionales() != null) {
						if (det.getDetallesAdicionales().getDetAdicional()
								.size() > 3) {
							int i = 3;
							for (i = 3; i < det.getDetallesAdicionales()
									.getDetAdicional().size(); i++) {
								det.getDetallesAdicionales()
										.getDetAdicional()
										.get(2)
										.setNombre(
												det.getDetallesAdicionales()
														.getDetAdicional()
														.get(2).getNombre()
														+ "/"
														+ det.getDetallesAdicionales()
																.getDetAdicional()
																.get(i)
																.getNombre());
								det.getDetallesAdicionales()
										.getDetAdicional()
										.get(2)
										.setValor(
												det.getDetallesAdicionales()
														.getDetAdicional()
														.get(2).getValor()
														+ "/"
														+ det.getDetallesAdicionales()
																.getDetAdicional()
																.get(i)
																.getValor());
							}
							while (det.getDetallesAdicionales().getDetAdicional().size() > 3){
								det.getDetallesAdicionales().getDetAdicional()
										.remove(det.getDetallesAdicionales().getDetAdicional().size()-1);
							}

						}
						if (det.getDetallesAdicionales().getDetAdicional()
								.size() == 0) {
							det.setDetallesAdicionales(null);
						}
					}
				}
				registrarColaMensajes(compania,	INombresParametros.PROCESO_COMPROBANTE_INDIVIDUAL_NOMBRE_NOTA_CREDITO,	"SEED_BILLING",
						ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO,"", false, notaCredito.getInfoTributaria().getTipoEmision(), 
						notaCredito.getVersion(), notaCredito.getInfoTributaria().getAmbiente(), null, null, null, notaCredito, null, nombreArchivo, codigoInterno);
				break;
			case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO:
				NotaDebito notaDebito = convierteDocumentAClaseNotaDebito(crearXMLComprobanteRetencion(archivoXML));
				buscarComp = servicioParametros.obtenerCompaniaPorRUC(notaDebito.getInfoTributaria().getRuc());
				if(buscarComp != null){
					compania = StringUtils.leftPad(String.valueOf(buscarComp.getId()), 2, "0");
					if (buscarComp.getCertificadoComprobante() == null) {
						compania = StringUtils.leftPad(String.valueOf(corlasosa.getId()), 2,"0");
						notaDebito.getInfoTributaria().setPtoEmi("001");
						notaDebito.getInfoTributaria().setEstab("001");
						notaDebito.getInfoTributaria().setRuc(corlasosa.getIdentificacion());
					}
				}else{
					throw new Throwable("EL RUC DEL EMISOR : " + rucEmisor	+ " NO CORRESPONDE A NINGUNA EMPRESA CONFIGURADA");
				}
				if (notaDebito.getInfoTributaria().getClaveAcceso() == null
						|| notaDebito.getInfoTributaria().getClaveAcceso().equals("")
						|| notaDebito.getInfoTributaria().getClaveAcceso().equals("0")|| notaDebito.getInfoTributaria().getClaveAcceso().equals("1")) {
					notaDebito.getInfoTributaria()
							.setClaveAcceso(
									GeneradorClaveAcceso
											.generarClaveDeAcceso(
													notaDebito
															.getInfoNotaDebito()
															.getFechaEmision()
															.replace("/", ""),
													ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO,
													notaDebito
															.getInfoTributaria()
															.getRuc(),
													notaDebito
															.getInfoTributaria()
															.getAmbiente(),
													new Long(
															notaDebito
																	.getInfoTributaria()
																	.getEstab()
																	+ notaDebito
																			.getInfoTributaria()
																			.getPtoEmi()),
													new Long(
															notaDebito
																	.getInfoTributaria()
																	.getSecuencial()),
													12345678L,
													notaDebito
															.getInfoTributaria()
															.getTipoEmision()));
				}
				claveAcceso = notaDebito.getInfoTributaria().getClaveAcceso();
				validacionClave(claveAcceso);
				if (notaDebito.getId() == null || notaDebito.getId().equals(""))
					notaDebito.setId("comprobante");
				notaDebito.getInfoTributaria().setSecuencial(StringUtils.leftPad(notaDebito.getInfoTributaria().getSecuencial(), 9, "0"));
				registrarColaMensajes(compania,	INombresParametros.PROCESO_COMPROBANTE_INDIVIDUAL_NOMBRE_NOTA_DEBITO,
						"SEED_BILLING",	ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO,
						"", false, notaDebito.getInfoTributaria().getTipoEmision(), notaDebito.getVersion(),
						notaDebito.getInfoTributaria().getAmbiente(), null,	null, null, null, notaDebito, nombreArchivo, codigoInterno);
				break;
			default:
				throw new Throwable("El tipo de documento es invalido");
			}
			return "1";
		} catch (Exception sqle) {			
			sqle.printStackTrace();
			return "El formato del documento enviado no corresponde a un comprobante electronico";
		} catch (Throwable e) {
			return e.getMessage();
		}
	}

	@Override
	public void reprocesadoTomadosNoFinalizados(BigDecimal tiempoTomado, String accionDevolver) {
		List<ComprobanteProcesadoIndividual> listaReprocesar = loggin.listaDocumentosAReprocesar(tiempoTomado, accionDevolver);
		for (ComprobanteProcesadoIndividual comprobante : listaReprocesar) {
			try {
				recepcionXmlComprobante(new String(ModuladorComprobanteComprimido.descomprimirXML(comprobante.getXmlComprobante())),null,null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Date convierteCadenaAFecha(String fecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return sdf.parse(fecha);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String obtenerStringDeComprobante(String tipoDocumento,	Object comprobante, Class<?> clase) {
		try {
			JAXBContext jAXBContextFactura = JAXBContext.newInstance(clase);
			Marshaller marshallerFactura = jAXBContextFactura.createMarshaller();
			marshallerFactura.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshallerFactura.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,	true);
			StringWriter writer = new StringWriter();
			marshallerFactura.marshal(clase.cast(comprobante), writer);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private Document crearXMLComprobanteRetencion(String comprobante)throws ParserConfigurationException, JAXBException,
			TransformerException, SAXException, IOException {		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		
		DocumentBuilder db = dbf.newDocumentBuilder();		
		Document document = db.parse(new InputSource(new StringReader(comprobante)));		
		return document;
	}

	private ComprobanteRetencion convierteDocumentARetencion(Document xml)throws Throwable {
		JAXBContext jaxbcontext = JAXBContext.newInstance(ComprobanteRetencion.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		ComprobanteRetencion comp = (ComprobanteRetencion) unmarshaller.unmarshal(xml);
		return comp;
	}

	private Factura convierteDocumentAClaseFactura(Document xml)throws Throwable {		
		JAXBContext jaxbcontext = JAXBContext.newInstance(Factura.class);		
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();		
		Factura comp = (Factura) unmarshaller.unmarshal(xml);		
		return comp;
	}

	private NotaCredito convierteDocumentAClaseNotaCredito(Document xml)throws Throwable {
		JAXBContext jaxbcontext = JAXBContext.newInstance(NotaCredito.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		NotaCredito comp = (NotaCredito) unmarshaller.unmarshal(xml);
		return comp;
	}

	private NotaDebito convierteDocumentAClaseNotaDebito(Document xml)throws Throwable {
		JAXBContext jaxbcontext = JAXBContext.newInstance(NotaDebito.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		NotaDebito comp = (NotaDebito) unmarshaller.unmarshal(xml);
		return comp;
	}

	private GuiaRemision convierteDocumentAClaseGuiaRemision(Document xml)throws Throwable {
		JAXBContext jaxbcontext = JAXBContext.newInstance(GuiaRemision.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		GuiaRemision comp = (GuiaRemision) unmarshaller.unmarshal(xml);
		return comp;
	}

	public static String obtenerClaveAcceso(String dato) {
		if (dato.contains("<claveAcceso>") && dato.contains("</claveAcceso>")) {
			int idxof = dato.indexOf("<claveAcceso>");
			int idxOFFin = dato.indexOf("</claveAcceso>");
			if (idxOFFin - (idxof + 13) >= 0) {
				return "";
			}
			return dato.substring(idxof + 13, idxof + 62);
		}
		if (dato.contains("&lt;claveAcceso&gt;")
				&& dato.contains("&lt;/claveAcceso&gt;")) {
			int idxof = dato.indexOf("&lt;claveAcceso&gt;");
			int idxOFFin = dato.indexOf("&lt;/claveAcceso&gt;");
			if (idxOFFin - (idxof + 19) >= 0) {
				return "";
			}
			return dato.substring(idxof + 19, idxof + 68);
		}
		return "";
	}

	public static String obtieneTipoDocumento(String xml) {
		if (xml.contains("<codDoc>") && xml.contains("</codDoc>")) {
			int idxof = xml.indexOf("<codDoc>");
			return xml.substring(idxof + 8, idxof + 10);
		}
		if (xml.contains("&lt;codDoc&gt;") && xml.contains("&lt;/codDoc&gt;")) {
			int idxof = xml.indexOf("&lt;codDoc&gt;");
			return xml.substring(idxof + 14, idxof + 16);
		}
		return "";
	}
	
	//OJO CON ESTE QUE ENVIA A LA COLA
	public void registrarColaMensajes(String compania, String nombreProceso,
			String usuario, String tipoDocumento, String clave,
			Boolean notificar, String tipoEmisionNormal, String version,
			String ambiente, Factura fac, ComprobanteRetencion ret,
			GuiaRemision guia, NotaCredito nc, NotaDebito nd,String nombreArchivo, String codigoInterno) {
		ArrayList<MensajeIndividual> mensajes = new ArrayList<>();

		MensajeIndividual mensaje = new MensajeIndividual();
		mensaje.setAmbiente(ambiente);
		mensaje.setFactura(fac);
		mensaje.setGuia(guia);
		mensaje.setNotaCredito(nc);
		mensaje.setNotaDebito(nd);
		mensaje.setComprobanteRetencion(ret);
		mensaje.setVersion(version);
		mensaje.setCompania(compania);
		mensaje.setTipoDocumento(tipoDocumento);
		mensaje.setNombreProceso(nombreProceso);
		mensaje.setClave(clave);
		mensaje.setUsuario(usuario);
		mensaje.setNotificar(notificar);
		mensaje.setTipoEmisionNormal(tipoEmisionNormal);
		mensaje.setEstadoProceso("E");
		System.out.println("va a registrar en jms, nombre archivo es nuevo "+nombreArchivo);
		if(nombreArchivo==null ||"".equals(nombreArchivo)){
			mensaje.setNombreArchivo("");
			mensaje.setIdEmpresaCliente("");
		}else{
			mensaje.setNombreArchivo(nombreArchivo);
			mensaje.setIdEmpresaCliente(codigoInterno.toUpperCase());
		}		
		mensajes.add(mensaje);		
		try {
			servicioJMS.registrarLoteVirtualMensajeIndividual(mensajes);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void reprogramaAutorizacionPendientes(){
		String accion = servicioParametros.getValorParametro("ACCION_COMPROBANTES_REAUTORIZAR");
		Integer cantidadProcesar = new Integer(servicioParametros.getValorParametro("CANTIDAD_COMPROBANTES_REAUTORIZAR"));
		System.err.println("reprogramaAutorizacionPendientes -- Se envia a consultar comprobantes  accion ["+accion+"] , cantidadProcesar["+cantidadProcesar+"] ");
		List<ComprobanteProcesadoIndividual> lista = loggin.listaDocumentosAutorizar(accion, cantidadProcesar);
		System.err.println("En reprogramaAutorizacionPendientes el size de los comprobantes a conciliar :  "+lista.size());
		for (ComprobanteProcesadoIndividual comp : lista) {
			ComprobanteProcesadoIndividual comprobante = loggin.obtenerComprobanteAProcesar(comp.getClaveAcceso());
			comprobante.setNumeroIntento(comprobante.getNumeroIntento()+1);
			System.err.println("reprogramaAutorizacionPendientes -- Se actualizo el comprobante ["+comprobante.getClaveAcceso()+"] a "+comprobante.getNumeroIntento()+" Intentos");
			try {
				try{
				envioAutorizacion(comprobante);
				}catch(Exception t){
					System.err.println("reprogramaAutorizacionPendientes -- Error durante el envio a autorizar el comprobante se mantiene estado EDA y se aumenta intentos ");
					comprobante.setError(t.getMessage());
					t.printStackTrace();
				}
				comprobante.setFechaFinProceso(new Date());
				System.out.println("reprogramaAutorizacionPendientes -- Se intentara actualizar en FE_COMPROBANTES_PROC_INDIV");
				loggin.actualizarComprobanteIndividual(comprobante);
				System.out.println("reprogramaAutorizacionPendientes -- Se actualizo!! en FE_COMPROBANTES_PROC_INDIV");				
//				comprobantesIndividual.actualizarDatosCabecera(
//						comprobante.getFechaSolAutorizacion(),
//						comprobante.getFechaAutorizacion(), 
//						comprobante.getAutorizacion(),		
//						comprobante.getNumeroIntento(),
//						comprobante.getEstado(),
//						comprobante.getUltimoCodigoError(),
//						comprobante.getClaveAcceso());
			} catch (Throwable error) {
				System.out.println("reprogramaAutorizacionPendientes -- Error durante la actualizacion del comprobante");
				error.printStackTrace();
			}
		}
	}
	
	//CAMBIO OFFLINE
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Asynchronous
	public void enviarCorreoFirmado(ComprobanteProcesadoIndividual comprobanteFirmado){
		try{
		GeDatosGeneralesCompania empresa = servicioParametros.datosCompania(comprobanteFirmado.getCompania()); 
		String remitente = empresa.getCorreo();
		String asunto = servicioParametros.getValorParametro("ASUNTO_MAIL_COMP_ENVIADO_CLIENTE",empresa.getIdSuscriptor());
		String plantillaCorreo = servicioParametros.getValorParametro("CUERPO_MAIL_COMP_ENVIADO_CLIENTE",empresa.getIdSuscriptor());		
		Boolean consultaSeedUsuarios = false;
		String email = null;
		String emailTagCorreo = comprobanteFirmado.getEmailCliente();
		String emailsSecundarios = null;	
		String mailNoEnviar=servicioParametros.getValorParametro("CORREO_NO_ENVIAR",empresa.getIdSuscriptor());
		if(emailTagCorreo==null){
			consultaSeedUsuarios=true;
		}else{
			if(emailTagCorreo.trim().equals("")){
				consultaSeedUsuarios = true;
			}
		}
		if(consultaSeedUsuarios){
			System.err.println("CORREO TOMADO DE SEED_USUARIOS");
			email = loggin.obtenerCuentaCorreo(comprobanteFirmado.getIdentificacionCliente(), empresa.getIdSuscriptor());
			emailsSecundarios = loggin.listaCorreosSecundarios(comprobanteFirmado.getIdentificacionCliente(), empresa.getIdSuscriptor());
			emailsSecundarios =	emailsSecundarios==null?"":emailsSecundarios;
		}else{
			System.err.println("CORREO TOMADO DE TAG CORREO 1 o EMAIL");
			email = emailTagCorreo;
			emailsSecundarios = "";
		}
		
		if(email !=null && !email.equals("")){
			if(mailNoEnviar== null ||mailNoEnviar.equals("")||!(email.contains(mailNoEnviar))){
				try {
					plantillaCorreo = leerHtml(plantillaCorreo);
					for (Field field : empresa.getClass().getDeclaredFields()) {
						if (plantillaCorreo.contains("%s"+field.getName().toUpperCase()+"%s")) {
							plantillaCorreo = plantillaCorreo.replace("%s"+field.getName().toUpperCase()+"%s", PropertyUtils.getProperty(empresa, field.getName()).toString());
						}else if (plantillaCorreo.contains("%d"+field.getName().toUpperCase()+"%d")) {
							plantillaCorreo = plantillaCorreo.replace("%d"+field.getName().toUpperCase()+"%d", "" + (Integer)PropertyUtils.getProperty(empresa, field.getName()));
						}else if (plantillaCorreo.contains("%f"+field.getName().toUpperCase()+"%f")) {
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							plantillaCorreo = plantillaCorreo.replace("%f"+field.getName().toUpperCase()+"%f", formato.format((Date)PropertyUtils.getProperty(empresa, field.getName())));
						}
						if (asunto.contains("%s"+field.getName().toUpperCase()+"%s")) {
							asunto = asunto.replace("%s"+field.getName().toUpperCase()+"%s", PropertyUtils.getProperty(empresa, field.getName()).toString());
						}else if (asunto.contains("%d"+field.getName().toUpperCase()+"%d")) {
							asunto = asunto.replace("%d"+field.getName().toUpperCase()+"%d", "" + (Integer)PropertyUtils.getProperty(empresa, field.getName()));
						}else if (asunto.contains("%f"+field.getName().toUpperCase()+"%f")) {
							SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							asunto = plantillaCorreo.replace("%f"+field.getName().toUpperCase()+"%f", formato.format((Date)PropertyUtils.getProperty(empresa, field.getName())));
						}else if (asunto.contains("%%NUMFACTURA%%")){
							asunto = asunto.replace("%%NUMFACTURA%%", comprobanteFirmado.getNumDocumento());
						}else if (asunto.contains("%%DDDD%%")){
							asunto = asunto.replace("%%DDDD%%",  getTipoDocumento(comprobanteFirmado.getTipoDocumento()));
						}
					}										
					System.err.println("EMAIL AL QUE SE VA A ENVIAR EL FIRMADO "+email);
					int actualizarCampo=1;
					if (email != null) {
						String cuerpoCorreo = plantillaCorreo;
						cuerpoCorreo = cuerpoCorreo.replace("%%TIPODOCUMENTO%%", getTipoDocumento(comprobanteFirmado.getTipoDocumento()));
						cuerpoCorreo = cuerpoCorreo.replace("%%NUMFACTURA%%", comprobanteFirmado.getNumDocumento());
						cuerpoCorreo = cuerpoCorreo.replace("%%NOMBRECLIENTE%%", comprobanteFirmado.getNombreCliente());
						if(cuerpoCorreo.contains("%%URLCLIENTE%%")){
							GeDatosGeneralesCompania datosClientes = servicioParametros.obtenerDatosEmail(comprobanteFirmado.getCompania());
							cuerpoCorreo = cuerpoCorreo.replace("%%URLCLIENTE%%", datosClientes.getUrlPortal());
							if(cuerpoCorreo.contains("%%URLIMAGEN%%")){
								cuerpoCorreo = cuerpoCorreo.replace("%%URLIMAGEN%%", datosClientes.getObservacion());
							}
						}
						if(cuerpoCorreo.contains("%%PRESENTARCLIENTEPRIMERAVEZ%%")){
							boolean clientePrimeraVez = servicioParametros.clientePrimeraVez(comprobanteFirmado.getIdentificacionCliente(), comprobanteFirmado.getIdSuscriptor());
							if(clientePrimeraVez){
								cuerpoCorreo = cuerpoCorreo.replace("%%USUARIO%%", comprobanteFirmado.getIdentificacionCliente());
								cuerpoCorreo = cuerpoCorreo.replace("%%CLAVE%%", comprobanteFirmado.getIdentificacionCliente());
								cuerpoCorreo = cuerpoCorreo.replace("%%PRESENTARCLIENTEPRIMERAVEZ%%","true");
							}else{
								cuerpoCorreo = cuerpoCorreo.replace("%%PRESENTARCLIENTEPRIMERAVEZ%%","none");
							}
						}
						if(cuerpoCorreo.contains("%%URLDOWNLOAD%%")){
							String urlComprobante= servicioParametros.getValorParametro("RUTA_DESCARGA_COMPROBANTE");
							urlComprobante=urlComprobante+"?identificacion="+comprobanteFirmado.getIdentificacionCliente()+
									"&numComp="+comprobanteFirmado.getNumDocumento()+ "&tipoDoc=" + comprobanteFirmado.getTipoDocumento() + "&suscriptor="
											+ comprobanteFirmado.getIdSuscriptor();
							cuerpoCorreo = cuerpoCorreo.replace("%%URLDOWNLOAD%%", urlComprobante);
						}
						if(cuerpoCorreo.contains("%%DOMINIOCORREO%%")){
							//OBTENERLO DEL REMITENTE
							cuerpoCorreo = cuerpoCorreo.replace("%%DOMINIOCORREO%%", remitente==null?"":remitente.substring(remitente.indexOf("@")));
						}
						
						
						for(Field field : comprobanteFirmado.getClass().getDeclaredFields()){
							if (cuerpoCorreo.contains("%s"+field.getName().toUpperCase()+"%s")) {
								cuerpoCorreo = cuerpoCorreo.replace("%s"+field.getName().toUpperCase()+"%s", PropertyUtils.getProperty(comprobanteFirmado, field.getName()).toString());
							}else if (cuerpoCorreo.contains("%d"+field.getName().toUpperCase()+"%d")) {
								cuerpoCorreo = cuerpoCorreo.replace("%d"+field.getName().toUpperCase()+"%d", "" + (Integer)PropertyUtils.getProperty(comprobanteFirmado, field.getName()));
							}else if (cuerpoCorreo.contains("%f"+field.getName().toUpperCase()+"%f")) {
								SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								cuerpoCorreo = cuerpoCorreo.replace("%f"+field.getName().toUpperCase()+"%f", formato.format((Date)PropertyUtils.getProperty(comprobanteFirmado, field.getName())));
							}
						}
						
						try {			
							byte[] bufferData = new byte[0];
							bufferData = puerto.generarComprobantesPDF(comprobanteFirmado.getClaveAcceso(),empresa.getIdSuscriptor());
							if(bufferData.length >0){	
								System.out.println("ride si pudo ser cargado");
								GeSuscriptor suscriptor = servicioParametros.consultaSuscriptor(empresa.getIdSuscriptor());
								servicioNotificacion.notificacionRideConAdjuntos(remitente, email, emailsSecundarios, asunto, cuerpoCorreo, comprobanteFirmado.getXmlComprobante(), comprobanteFirmado.getClaveAcceso(), bufferData,suscriptor.getTrackingNavegacion());
								System.out.println("se envio correo del comprobante " + comprobanteFirmado.getClaveAcceso() + " al correo "+email);
							}else{
								actualizarCampo=0;
								System.out.println("el comprobante "+ comprobanteFirmado.getClaveAcceso() + " no se puede enviar el pdf esta danada revise");
							}
						} catch (Throwable e) {
							System.err.println("------------------------------ Error tratando de enviar el mail ");
							e.printStackTrace();
						}
					}
					comprobanteFirmado.setFechaEnvioMail(new Date());
					if(actualizarCampo==1)
						loggin.actualizarComprobanteFechaEnvioMail(comprobanteFirmado);					
				} catch (Exception e) {
					System.err.println("------------------------------ Error al armar correo "); 
					loggin.actualizarComprobanteEnvioMailBitacora(comprobanteFirmado, e.getMessage()==null?"ERROR AL ENVIAR EL CORREO ":e.getMessage(), "E");
					e.printStackTrace();
				}
			}else{
				System.out.println("no se envia correo es consumidor final "+comprobanteFirmado.getClaveAcceso());
			}
		}else{
			System.out.println("No existe correo para el comprobante" + comprobanteFirmado.getClaveAcceso());
		}
		}catch(Exception e){			
			e.printStackTrace();
		}
	}	
	
	public String getTipoDocumento(String tipoDocumento){
		switch (tipoDocumento) {
		case "01": return "FACTURA";
		case "04": return "NOTA DE CREDITO";
		case "05": return "NOTA DE DEBITO";		
		case "07": return "RETENCION";
		case "06": return "GUIA DE REMISION";
		default: return tipoDocumento;
		}
	}
	
	private String leerHtml(String rutaPlantilla) throws IOException {
		System.out.println(rutaPlantilla);
		File f = new File(rutaPlantilla);
		byte[] b = Files.readAllBytes(f.toPath());
		return new String(b);
	}
	
//	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
//	public List<String> leerAchivosDirectorio(String directorio,Integer cantidadArchivos){
//		File directorioXml = new File(directorio);
//		if(!directorioXml.exists()){
//			directorioXml.mkdirs();
//		}
//		File[] arrayFile = directorioXml.listFiles();
//		List<String> is = new ArrayList<String>();
//		int contador =1;
//		try{
//			for (File xml : arrayFile) {				
//				if(xml.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_ZIP)){
//					try{
//					ZipFile zipFile = new ZipFile(xml);
//					Enumeration<? extends ZipEntry> entries = zipFile.entries();
//					while (entries.hasMoreElements()) {
//						String s = null;
//						String tmp ="";
//						ZipEntry entry = entries.nextElement();
//						if(entry.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_XML)){
//							InputStream stream = zipFile.getInputStream(entry);
//							BufferedReader br = new BufferedReader(new InputStreamReader(
//									stream, "UTF-8"));			
//
//							while ((s = br.readLine()) != null) {					
//								tmp=tmp+s;								
//							}
//							br.close();			
//							stream.close();
//							is.add(tmp);
//							mueveArchivoProcesado(xml, directorio+File.separatorChar+"xml_Proc");
//							contador++;
//						}
//					}
//					}catch(Exception e){
//						  mueveArchivoProcesado(xml,directorioXml.getAbsolutePath()+"/ERROR");
//					}
//				}
//				//cambio para cls cuando los archivos no son zipeados
//				if(xml.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_XML)){
//					try {
//						  Path wiki_path = Paths.get(directorioXml.getAbsolutePath(), xml.getName());
//						  System.out.println(xml.getName());
//						  byte[] wikiArray = Files.readAllBytes(wiki_path);
//					      String wikiString = new String(wikiArray, "UTF-8");
//					      is.add(wikiString);
//					      mueveArchivoProcesado(xml, directorio+File.separatorChar+"xml_Proc");
//					      contador++;
//					} catch (Exception e) {
//						  mueveArchivoProcesado(xml,directorioXml.getAbsolutePath()+"/ERROR");
//					}
//				}
//				//fin cambio cls cuando los archivos no son zipeados
//				if(contador>=cantidadArchivos)
//					break;
//			}	
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return is;
//		
//		
//	}
		
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<String> leerAchivosDirectorio(String directorio,Integer cantidadArchivos){
		System.err.println("entro al metodo para leer los archivos");
		File directorioXml = new File(directorio);
		if(!directorioXml.exists()){
			directorioXml.mkdirs();
		}
		File[] arrayFile = directorioXml.listFiles();
		System.err.println("cantidas de archivos zip es "+arrayFile.length);
		List<String> is = new ArrayList<String>();
		int contador =1;
		try {
			for (File xml : arrayFile) {				
				if(xml.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_ZIP)){
					try {
					System.err.println("------------- el archivo zip es "+xml.getName());
					
					FileInputStream fis = new FileInputStream(xml);				
					
					ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis), Charset.forName("Cp437"));
					ZipEntry entry = null;
					while((entry = zis.getNextEntry()) != null){
						
						if(entry.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_XML)){
							System.err.println("es un xml "+entry.getName());
							final int BUFFER = 1;
						    byte data[] = new byte[BUFFER];
						    ByteArrayOutputStream out = new ByteArrayOutputStream();
						    while (zis.read(data) != -1) {
						        out.write(data);
						    }      
						   
						    InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
							is.add(getStringFromInputStream(inputStream));
						   
						}
						else{
							System.err.println("no es un xml el archivo "+entry.getName());
						}
					}
					zis.close();
					mueveArchivoProcesado(xml, directorio+File.separatorChar+"xml_Proc");
					contador++;
					} catch (Exception e) {
						  mueveArchivoProcesado(xml,directorioXml.getAbsolutePath()+"/ERROR");
					}
				}
				//cambio para cls cuando los archivos no son zipeados
				if(xml.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_XML)){
					try {
						  Path wiki_path = Paths.get(directorioXml.getAbsolutePath(), xml.getName());
						  System.out.println(xml.getName());
						  byte[] wikiArray = Files.readAllBytes(wiki_path);
					      String wikiString = new String(wikiArray, "UTF-8");
					      is.add(wikiString);
					      mueveArchivoProcesado(xml, directorio+File.separatorChar+"xml_Proc");
					      contador++;
					} catch (Exception e) {
						  mueveArchivoProcesado(xml,directorioXml.getAbsolutePath()+"/ERROR");
					}
				}
				//fin cambio cls cuando los archivos no son zipeados
				if(contador>=cantidadArchivos)
					break;
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}
		
		private String getStringFromInputStream(InputStream is) {		 
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder(); 
			String line;
			try { 
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				} 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} 
			
			
			return sb.toString();
		}
		
	public void mueveArchivoProcesado(File archivoTransferir, String rutaMueve) {
		File directorioXml = new File(rutaMueve);
		if(!directorioXml.exists()){
			directorioXml.mkdirs();
		}
			fileCopy(archivoTransferir.getAbsoluteFile().toString(), rutaMueve
					+ "/" + archivoTransferir.getName());
			archivoTransferir.delete();

		}

		public void fileCopy(String sourceFile, String destinationFile) {

			try {
				Path FROM = Paths.get(sourceFile);
				Path TO = Paths.get(destinationFile);
				// sobreescribir el fichero de destino, si existe, y copiar
				// los atributos, incluyendo los permisos rwx
				CopyOption[] options = new CopyOption[] {
						StandardCopyOption.REPLACE_EXISTING,
						StandardCopyOption.COPY_ATTRIBUTES };
				Files.copy(FROM, TO, options);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@TransactionAttribute(TransactionAttributeType.SUPPORTS)
		public void procesaComprobanteArchivo (String xml,String estado,String ambiente, GeSuscriptor suscriptor){
			ServiciosTransformacion st = new ServiciosTransformacion();			
			String  secuencial = "",tipoDoc="";		
			
			/// INI MBAS 12042017
			String lClaveAcceso= new String();
			/// FIN MBAS 12042017
			
			secuencial= st.obtenerTagXml(xml, "secuencial");
			tipoDoc = st.obtenerTagXml(xml, "codDoc");				
			
			EfacturaXML facturaXml = new EfacturaXML();
			
			facturaXml.setAmbiente(ambiente);//T
			facturaXml.setEstado(estado);//P
			facturaXml.setFechaHora(new Date());
			facturaXml.setNoDocumento(secuencial);
			facturaXml.setObservacion("PROCESO ALMACENAMIENTO DE ARCHIVOS");
			facturaXml.setTipo(tipoDoc);
			facturaXml.setXmlBase(xml);
			
		    /// INI MBAS 12042017
			lClaveAcceso=st.obtenerTagXml(xml, "claveAcceso");
			facturaXml.setClaveAcceso(lClaveAcceso);
			 /// FIN MBAS 12042017
			
			//ARO- CAMBIO PARA ALMACENAR SUSCRIPTOR Y PRIORIDAD
			facturaXml.setSuscriptor(suscriptor!=null ? suscriptor.getIdSuscriptor().intValue() : 0);
			facturaXml.setPrioridad(suscriptor!=null ? suscriptor.getPrioridad() : 2);			
			//ARO-FIN
			
			loggin.registrarEfactura(facturaXml);

		 
		}
		
		//KFC - Completar XML con informacion adicional
		public String armarXmlWebServices(String xml, String infoAdicional){
		
			if(infoAdicional!=null && !infoAdicional.trim().equals("")){
				if(!infoAdicional.endsWith(";"))
					infoAdicional=infoAdicional+";";											
			}
		 try {
			xml = agregarInfoAdicional(xml, infoAdicional);
					if("ERROR".equals(xml)){	
						xml = "- Revise por favor los campos adicionales que ingreso";
						//throw new Throwable("ERROR");				
					}else{
							// = recepcionXmlComprobante(xml,null,null);
						try{
							String estado = INombresParametros.ESTADO_INICIAL_INDIVIDUAL;
							String ambiente = INombresParametros.AMBIENTE_COMPROBANTE_INDIVIDUAL;					    
						    procesaComprobanteArchivo(xml, estado, ambiente, null);
						    xml = "1";
					    }catch(Exception e ){
					    	xml="-2";
					    }
					}
				} catch (Throwable e) {
					xml="-2";
					e.printStackTrace();
				}
				return xml;
		}
		
		@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
		public String armarXmlTrama(String xml, String infoAdicional, String nombreArchivo, String codigoInterno){
			try {
				if(infoAdicional!=null && !infoAdicional.trim().equals("")){
					if(!infoAdicional.endsWith(";"))
						infoAdicional=infoAdicional+";";											
				}
				xml = agregarInfoAdicionalTrama(xml, infoAdicional);			
				if("ERROR".equals(xml)){	
					xml = "- Revise por favor los campos adicionales que ingreso";
					throw new Throwable("ERROR");				
				}else{
					xml = recepcionXmlComprobante(xml, nombreArchivo, codigoInterno);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return xml;
		}
		
		public String agregarInfoAdicionalTrama(String xml, String valorAgregar) {
			if(valorAgregar!=null && !("".equals(valorAgregar))){
				//System.out.println("-----------XML no tiene tag infoAdicional, se procede agregar tag infoAdicional y campos adicionales------------------");
				String tipoDoc = obtieneTipoDocumento(xml), tagCierra = "";				
				if("01".equals(tipoDoc)){
					tagCierra = "</factura>";
				}else if("04".equals(tipoDoc)){
					tagCierra = "</notaCredito>";
				}else if("07".equals(tipoDoc)){
					tagCierra = "</comprobanteRetencion>";
				}else if("06".equals(tipoDoc)){
					tagCierra = "</guiaRemision>";
				}
				
				//recorrer las claves
				String tagCampoAdicional = "";
				String tagInfoAdicional = "";
				String cadenaCampoAdicional ="";
				for(String cadena : valorAgregar.split(";")){					
					String[] arreglo =  cadena.split("=");					
					if(arreglo!=null && arreglo.length>1){
						arreglo[0]= arreglo[0].trim().toUpperCase().equals("EMAILCLIENTE")?TAGCORREO:arreglo[0].trim();
						cadenaCampoAdicional=cadenaCampoAdicional+"<campoAdicional nombre=\""+arreglo[0]+"\">"+arreglo[1]+"</campoAdicional>";;
					}
					
					
				}
				if(cadenaCampoAdicional!=null && !cadenaCampoAdicional.trim().equals("")){
					cadenaCampoAdicional="<infoAdicional>"+cadenaCampoAdicional+"</infoAdicional>";
					//agregar al xml
					xml = xml.substring(0,xml.indexOf(tagCierra))+cadenaCampoAdicional+tagCierra;
				}
			}
			return xml;
		}
		
//		public String agregarInfoAdicionalTrama(String xml, String valorAgregar) {
//			System.err.println("valorAgregar "+valorAgregar);
//			String keyBase = servicioParametros.getValorParametro("KEY_CAMPO_ADICIONAL_TRAMA");
////			String keyBase = "CORREO 1;CODSOCIEDAD;CODINTERNOSAP";
//			String key[] =null ;//{"FECHAOPERACION","VENDEDOR"};
//			try{//				
//			}catch(Exception e ){}
//			if(valorAgregar!=null && !("".equals(valorAgregar))){
//				try{
//					String tagInfoAdicional = "", tagCampoAdicional = "", tagRestoXml = "",  valor= "";
//					int contador = 0;
//					
//					if (xml.contains("<infoAdicional>") && xml.contains("</infoAdicional>")) {
//						System.out.println("------------------XML con tag infoAdicional, se le agregaran mas campos adicionales------------------");
//						int posInfo = xml.indexOf("<infoAdicional>");
//						int posAdicional = xml.indexOf("<campoAdicional");
//						for(int x=0;x<valorAgregar.length();x++) {
//							if ((valorAgregar.charAt(x)==';')){
//										if(tagInfoAdicional.equals("")){
//											tagInfoAdicional = xml.substring(0, posInfo + 15);
//										}
//										//tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+nombre.trim()+"\">"+key[contador]+"</campoAdicional>";
//										tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+key[contador]+"\">"+valor.trim()+"</campoAdicional>";
//									}else{
//										tagInfoAdicional = xml.substring(0, posInfo + 15).concat("<campoAdicional nombre=\""+key[contador]+"\">"+valor.trim()+"</campoAdicional>");
//										
//									}						
//						}							
//						tagRestoXml = xml.substring(posAdicional);
//						xml = tagInfoAdicional+tagCampoAdicional+tagRestoXml;				
//					}else{		
//						if("01".equals(tipoDoc)){
//							tagCierra = "</factura>";
//						}else if("04".equals(tipoDoc)){
//							tagCierra = "</notaCredito>";
//						}else if("07".equals(tipoDoc)){
//							tagCierra = "</comprobanteRetencion>";
//						}else if("06".equals(tipoDoc)){
//							tagCierra = "</guiaRemision>";
//						}
//						int posTagCierra = xml.indexOf(tagCierra);
//						for(int x=0;x<valorAgregar.length();x++) {
//							if ((valorAgregar.charAt(x)==';')){
//								//nombre = valorAgregar.substring(0, valorAgregar.indexOf(":"));
//								valor = valorAgregar.substring(0, x);
//								valorAgregar = valorAgregar.substring(x+1);
//								System.out.println("campoAdicional # "+contador);	
//								if(valor!=null && !valor.trim().equals("")){
//								if(contador<key.length){								
//									if(contador>=1){
//										if(!tagInfoAdicional.contains("<infoAdicional>")){
//											tagCampoAdicional=tagCampoAdicional+"<infoAdicional>";
//											tagInfoAdicional="<infoAdicional>";
//										}
//										tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+key[contador]+"\">"+valor.trim()+"</campoAdicional>";
//									}else{
//										tagInfoAdicional = "<infoAdicional><campoAdicional nombre=\""+key[contador]+"\">"+valor.trim()+"</campoAdicional>";
//									}						
//								}else{								
//									if(contador>=1){
//										if(!tagInfoAdicional.contains("<infoAdicional>")){
//											tagCampoAdicional=tagCampoAdicional+"<infoAdicional>";
//											tagInfoAdicional="<infoAdicional>";
//										}
//										tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+"VALOR"+(contador+1)+"\">"+valor.trim()+"</campoAdicional>";
//									}else{
//										tagInfoAdicional = "<infoAdicional><campoAdicional nombre=\""+"VALOR"+(contador+1)+"\">"+valor.trim()+"</campoAdicional>";
//									}	
//								}
//								}
//								contador ++;
//							
//								x = 0;
//							}
//						}
//						tagRestoXml = xml.substring(posTagCierra);
//						if(!tagCampoAdicional.contains("<infoAdicional>"))
//							xml = xml.substring(0,xml.indexOf(tagCierra))+tagInfoAdicional+tagCampoAdicional+"</infoAdicional>"+tagRestoXml;	
//						else
//							xml = xml.substring(0,xml.indexOf(tagCierra))+tagCampoAdicional+"</infoAdicional>"+tagRestoXml;
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//					return "ERROR";				
//				}			
//			}
//			System.err.println("------------------------------------------------");
//			System.err.println(xml);
//			System.err.println("------------------------------------------------");
//			return xml;
//		}
		
		public String agregarInfoAdicional(String xml, String valorAgregar) {
			String keyBase ="";
			String tipoDocUMENTO = obtieneTipoDocumento(xml);
			  if(tipoDocUMENTO.equals("01"))
			   keyBase = servicioParametros.getValorParametro("KEY_CAMPO_ADICIONAL");
			  else
			   keyBase = servicioParametros.getValorParametro("KEY_CAMPO_ADICIONALNC");
			System.err.println("valorAgregar "+valorAgregar);
			String key[] =null ;
			try{
				key = keyBase.split(";") ;
			}catch(Exception e ){}
			
			if(valorAgregar!=null && !("".equals(valorAgregar))){
				try{
					String tagInfoAdicional = "", tagCampoAdicional = "", tagRestoXml = "", nombre = "", valor= "";
					int contador = 0;
					
					if (xml.contains("<infoAdicional>") && xml.contains("</infoAdicional>")) {
						System.out.println("------------------XML con tag infoAdicional, se le agregaran mas campos adicionales------------------");
						int posInfo = xml.indexOf("<infoAdicional>"); 
						int posAdicional = xml.indexOf("<campoAdicional");
						for(int x=0;x<valorAgregar.length();x++) {
							if ((valorAgregar.charAt(x)==';')){							
								valor = valorAgregar.substring(0, x);
								valorAgregar = valorAgregar.substring(x+1);
								System.out.println("campoAdicional # "+contador);
								if(valor!=null && !valor.trim().equals("")){
								if(contador<key.length){
									if(contador>=1){
										if(tagInfoAdicional.equals("")){
											tagInfoAdicional = xml.substring(0, posInfo + 15);
										}
										//tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+nombre.trim()+"\">"+key[contador]+"</campoAdicional>";
										tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+key[contador]+"\">"+valor.trim()+"</campoAdicional>";
									}else{
										tagInfoAdicional = xml.substring(0, posInfo + 15).concat("<campoAdicional nombre=\""+key[contador]+"\">"+valor.trim()+"</campoAdicional>");
										
									}						
								}else{
									if(contador>=1){
										if(tagInfoAdicional.equals("")){
											tagInfoAdicional = xml.substring(0, posInfo + 15);
										}
										//tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+"VALOR"+(contador+1)+"\">"+key[contador]+"</campoAdicional>";
										tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+"VALOR"+(contador+1)+"\">"+valor.trim()+"</campoAdicional>";
									}else{
										tagInfoAdicional = xml.substring(0, posInfo + 15).concat("<campoAdicional nombre=\""+"VALOR"+(contador+1)+"\">"+valor.trim()+"</campoAdicional>");
										
									}
								}
								}
								contador ++;
								
								x = 0;
							}
						}							
						tagRestoXml = xml.substring(posAdicional);
						xml = tagInfoAdicional+tagCampoAdicional+tagRestoXml;				
					}else{
						System.out.println("-----------XML no tiene tag infoAdicional, se procede agregar tag infoAdicional y campos adicionales------------------");
						String tipoDoc = obtieneTipoDocumento(xml), tagCierra = "";				
						if("01".equals(tipoDoc)){
							tagCierra = "</factura>";
						}else if("04".equals(tipoDoc)){
							tagCierra = "</notaCredito>";
						}
						int posTagCierra = xml.indexOf(tagCierra);
						for(int x=0;x<valorAgregar.length();x++) {
							if ((valorAgregar.charAt(x)==';')){								
								valor = valorAgregar.substring(0, x);
								valorAgregar = valorAgregar.substring(x+1);
								System.out.println("campoAdicional # "+contador);	
								if(valor!=null && !valor.trim().equals("")){
								if(contador<key.length){								
									if(contador>=1){
										if(!tagInfoAdicional.contains("<infoAdicional>")){
											tagCampoAdicional=tagCampoAdicional+"<infoAdicional>";
											tagInfoAdicional="<infoAdicional>";
										}
										tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+key[contador]+"\">"+valor.trim()+"</campoAdicional>";
									}else{
										tagInfoAdicional = "<infoAdicional><campoAdicional nombre=\""+key[contador]+"\">"+valor.trim()+"</campoAdicional>";
									}						
								}else{								
									if(contador>=1){
										if(!tagInfoAdicional.contains("<infoAdicional>")){
											tagCampoAdicional=tagCampoAdicional+"<infoAdicional>";
											tagInfoAdicional="<infoAdicional>";
										}
										tagCampoAdicional = tagCampoAdicional + "<campoAdicional nombre=\""+"VALOR"+(contador+1)+"\">"+valor.trim()+"</campoAdicional>";
									}else{
										tagInfoAdicional = "<infoAdicional><campoAdicional nombre=\""+"VALOR"+(contador+1)+"\">"+valor.trim()+"</campoAdicional>";
									}	
								}
								}
								contador ++;
							
								x = 0;
							}
						}
						tagRestoXml = xml.substring(posTagCierra);
						if(!tagCampoAdicional.contains("<infoAdicional>"))
							xml = xml.substring(0,xml.indexOf(tagCierra))+tagInfoAdicional+tagCampoAdicional+"</infoAdicional>"+tagRestoXml;	
						else
							xml = xml.substring(0,xml.indexOf(tagCierra))+tagCampoAdicional+"</infoAdicional>"+tagRestoXml;
					}
				}catch(Exception e){
					e.printStackTrace();
					return "ERROR";				
				}			
			}
//			System.err.println("------------------------------------------------");
//			System.err.println(xml);
//			System.err.println("------------------------------------------------");
			return xml;
		}
		
		@TransactionAttribute(TransactionAttributeType.SUPPORTS)
		public void registrarXmlAutorizados(String xml) throws Throwable{
			String xmlResouestaAutorizacion =null;		
			ServiciosTransformacion st = new ServiciosTransformacion();			
			String claveAcceso = "", establecimiento = "", puntoEmision = "", secuencial = "", fechaEmision = "", tipoDoc = "", identificacionCliente = "", nombreCliente = "", ambiente = "";
			//System.err.println(" XML a procesar: "+xml);			
			xmlResouestaAutorizacion = st.obtenerTagXml(xml, "soap:Body");			
			String xmlComprobante = null;
			Autorizacion respuestaAutorizado = null;
			try {				
				JAXBContext contexto = JAXBContext.newInstance(RespuestaComprobante.class.getPackage().getName());
				Unmarshaller msh = contexto.createUnmarshaller();
				byte[] str = xmlResouestaAutorizacion.getBytes();
				AutorizacionComprobanteResponse respComprobante = (AutorizacionComprobanteResponse) JAXBIntrospector.getValue(msh.unmarshal(new ByteArrayInputStream(str)));
				RespuestaComprobante respuesta = respComprobante.getRespuestaAutorizacionComprobante();

				System.err.println("se obtiene la respuesta comprobante"+ respuesta.getClaveAccesoConsultada());
				
				List<Autorizacion> autorizaciones = respuesta.getAutorizaciones().getAutorizacion();

				respuestaAutorizado = null;
				
				for (Autorizacion aut : autorizaciones) {
					if (aut.getEstado().equals(
							INombresParametros.RESPUESTA_RECIBIDA_AUTORIZACION_SRI)) {
						respuestaAutorizado = aut;
					}
				}

				xmlComprobante = respuestaAutorizado.getComprobante();

			} catch (Exception e) {				
				e.printStackTrace();
			}

			ComprobanteProcesadoIndividual comp = new ComprobanteProcesadoIndividual();

			xmlComprobante = xmlComprobante.replaceAll(INombresParametros.NOMBRE_TAG_AMBIENTE_PRODUCCION, INombresParametros.VALOR_TAG_AMBIENTE_PRODUCCION);
			xmlComprobante = xmlComprobante.replaceAll(INombresParametros.NOMBRE_TAG_AMBIENTE_PRUEBAS, INombresParametros.VALOR_TAG_AMBIENTE_PRUEBAS);

			claveAcceso = st.obtenerTagXml(xmlComprobante, "claveAcceso");
			
			ComprobanteProcesadoIndividual comprobanteIngresado = loggin.obtenerComprobanteAProcesar(claveAcceso);
			if (comprobanteIngresado==null){
					
					comp.setClaveAcceso(claveAcceso);
			
					establecimiento = st.obtenerTagXml(xmlComprobante, "estab");					
					puntoEmision = st.obtenerTagXml(xmlComprobante, "ptoEmi");					
					secuencial = st.obtenerTagXml(xmlComprobante, "secuencial");					
					comp.setNumDocumento(establecimiento + puntoEmision
							+ StringUtils.leftPad(secuencial, 9, "0"));
					String ruc = st.obtenerTagXml(xmlComprobante, "ruc");
					System.out.println("EL RUC DEL EMISOR: "+ruc);
					GeDatosGeneralesCompania datosCia = servicioParametros.obtenerCompaniaPorRUC(ruc);
					if(datosCia==null){
						System.err.println("EL RUC DEL EMISOR: "+ruc+" NO ESTA ASOCIADO A NINGUNA COMPANIA");
						return;
					}
					System.out.println("SE CONSULTO LA COMPAï¿½IA EL ID QUE SE GUARDA ES EL : "+StringUtils.leftPad(datosCia.getId().toString(),2,"0"));
					comp.setCompania(StringUtils.leftPad(datosCia.getId().toString(),2,"0"));
					comp.setEstado("A");
					comp.setFechaActualizacion(new Date());
					comp.setFechaRegistro(new Date());
					comp.setNumeroIntento(1);
					comp.setObservacion("REGISTRO INICIAL DEL PROCESO DE COMPROBANTE");
					comp.setFechaAutorizacion(respuestaAutorizado.getFechaAutorizacion().toGregorianCalendar().getTime());
					comp.setAutorizacion(respuestaAutorizado.getNumeroAutorizacion());
					fechaEmision = st.obtenerTagXml(xmlComprobante, "fechaEmision");
					
					SimpleDateFormat formato = new SimpleDateFormat(
							"dd/MM/yyyy");
					String strFecha = fechaEmision;
					Date fechaDateEmision = null;
					try {
						fechaDateEmision = formato.parse(strFecha);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					System.out.println("FEC EMISION"
							+ fechaDateEmision.toString());
					comp.setFechaEmision(fechaDateEmision);
			
					tipoDoc = st.obtenerTagXml(xmlComprobante, "codDoc");
					comp.setTipoDocumento(tipoDoc);
					System.out.println("TIPO DOC     " + tipoDoc);
			
					if (INombresParametros.TIPO_DOC_FACTURA
							.equals(tipoDoc)
							|| INombresParametros.TIPO_DOC_NC
									.equals(tipoDoc)
							|| INombresParametros.TIPO_DOC_ND
									.equals(tipoDoc)) {
						System.out.println("es factura");
						identificacionCliente = st.obtenerTagXml(xmlComprobante,
								"identificacionComprador");
						comp.setIdentificacionCliente(identificacionCliente);
						nombreCliente = st.obtenerTagXml(xmlComprobante,
								"razonSocialComprador");
						comp.setNombreCliente(nombreCliente);
					} else if (INombresParametros.TIPO_DOC_GUIA
							.equals(tipoDoc)) {
						identificacionCliente = st.obtenerTagXml(xmlComprobante,
								"identificacionDestinatario");
						comp.setIdentificacionCliente(identificacionCliente);
						nombreCliente = st.obtenerTagXml(xmlComprobante,
								"razonSocialDestinatario");
						comp.setNombreCliente(nombreCliente);
					} else if (INombresParametros.TIPO_DOC_RETENCION
							.equals(tipoDoc)) {
						identificacionCliente = st.obtenerTagXml(xmlComprobante,
								"identificacionSujetoRetenido");
						comp.setIdentificacionCliente(identificacionCliente);
						nombreCliente = st.obtenerTagXml(xmlComprobante,
								"razonSocialSujetoRetenido");
						comp.setNombreCliente(nombreCliente);
					}
					System.out.println("RUC" + identificacionCliente);
					System.out.println("RAZON SOCIAL" + nombreCliente);
			
			
					ambiente = st.obtenerTagXml(xmlComprobante, "ambiente");
					comp.setAmbiente(ambiente);
					
					comp.setFechaEnvioMail(new Date());
					comp.setFechaFinEnvioMail(new Date());
					
					xmlResouestaAutorizacion = xmlResouestaAutorizacion.replaceAll("&lt;", "<");
					xmlResouestaAutorizacion = xmlResouestaAutorizacion.replaceAll("&gt;", ">");
					xmlComprobante = xmlComprobante.replaceAll("&lt;", "<");
					xmlComprobante = xmlComprobante.replaceAll("&gt;", ">");
					Double imp12=0.0;
				    Double imp0=0.0;
				    Double totalDoc =0.0;
				    Double subTotal =0.0;
				    Double descuento =0.0;
				    String xmlTmp = xml;
				    if(xmlTmp.contains("<comprobante>")){
				    	xmlTmp = xmlTmp.substring(xmlTmp.indexOf("<comprobante>") + 13);
				    	xmlTmp = xmlTmp.substring(0, xmlTmp.indexOf("</comprobante>"));
				    }
				    xmlTmp=xmlTmp.replaceAll("&lt;", "<");
				    xmlTmp=xmlTmp.replaceAll("&gt;", ">");
				    //cortar la firma				    
				   
					switch (tipoDoc) {
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA:					
						try{
							Factura fac = convierteDocumentAClaseFactura(crearXMLComprobanteRetencion(xmlTmp));
							
						    for(Factura.InfoFactura.TotalConImpuestos.TotalImpuesto imp :fac.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()){
						    	if(imp.getCodigoPorcentaje().equals("2")){
						    		imp12=imp.getValor().doubleValue();
						    	}else{
						    		imp0=imp.getValor().doubleValue();
						    	}
						    }
						    totalDoc =  fac.getInfoFactura().getImporteTotal().doubleValue();
						    subTotal = fac.getInfoFactura().getTotalSinImpuestos().doubleValue();
						    descuento = fac.getInfoFactura().getTotalDescuento().doubleValue();
						}catch(Exception e){
							throw new Throwable("Error al procesar el documento con clave de acceso " + claveAcceso);
						}
						break;
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION:
						try{
							ComprobanteRetencion ret = convierteDocumentARetencion(crearXMLComprobanteRetencion(xmlTmp));
							for (com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.Impuesto det : ret.getImpuestos().getImpuesto()) {								
								totalDoc = totalDoc + det.getValorRetenido().doubleValue();
							}	
						}catch(Exception e){}
					break;
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION:
						//GuiaRemision guiaRemi = convierteDocumentAClaseGuiaRemision(crearXMLComprobanteRetencion(xml));
						break;
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO:
						NotaCredito notaCredito = convierteDocumentAClaseNotaCredito(crearXMLComprobanteRetencion(xmlTmp));
						 for(com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.TotalConImpuestos.TotalImpuesto  imp: notaCredito.getInfoNotaCredito().getTotalConImpuestos().getTotalImpuesto())
					    	{
					    	if(imp.getCodigoPorcentaje().equals("2")){
					    		imp12=imp.getValor().doubleValue();
					    	}else{
					    		imp0=imp.getValor().doubleValue();
					    	}
					    }
						 totalDoc=notaCredito.getInfoNotaCredito().getValorModificacion().doubleValue();
						 comp.setDocumentoModificaddo(notaCredito.getInfoNotaCredito().getNumDocModificado());
					break;
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO:
						NotaDebito notaDebito = convierteDocumentAClaseNotaDebito(crearXMLComprobanteRetencion(xmlTmp));
						totalDoc=notaDebito.getInfoNotaDebito().getValorTotal().doubleValue();
						break;
					default:
						throw new Throwable("El tipo de documento es invalido");
					}	
					
					comp.setIdSuscriptor(datosCia.getIdSuscriptor());
					comp.setImpuesto0(imp0);
					comp.setImpuesto12(imp12);
					comp.setTotalDocumento(totalDoc);
					comp.setSubTotal(subTotal);	
					comp.setDescuento(descuento);
					try {
					    comp.setXmlRespuestaAutorizacion(ModuladorComprobanteComprimido.comprimirXML(xmlResouestaAutorizacion,claveAcceso));
					} catch (IOException e1) {					
					}
					try {
						comp.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(xmlComprobante,claveAcceso));
					} catch (IOException e1) {
					}
				
					try{
						loggin.registrarInicioProcesoComprobanteIndividual(comp);
					}catch(Exception e){
						System.err.println("Se detecto un errro en al insertar el comprobante con la clave de acceso : "+claveAcceso);
					}
				
			}else{
				System.err.println("LA CLAVE DE ACCESO ["+claveAcceso+"] YA SE ENCUENTRA INGRESADA");
			}
		
		}
		
		@TransactionAttribute(TransactionAttributeType.SUPPORTS)
		public void registrarXmlAutorizadoSinSoap(String xml) throws Throwable{
			String    identificacionCliente = "", nombreCliente = "", ambiente = "";
			ServiciosTransformacion st = new ServiciosTransformacion();			
			String xmlComprobante="";
			String claveAcceso="";			
			if(xml.contains("<comprobante>")){
				xmlComprobante = st.obtenerTagXml(xml, "comprobante");
			}
			if(xmlComprobante.contains("<![CDATA[")){
				xmlComprobante=xmlComprobante.replace("<![CDATA[", "");
				xmlComprobante=xmlComprobante.replace("]]>", "");
			}			
			xmlComprobante = xmlComprobante.replaceAll("&lt;", "<");
			xmlComprobante = xmlComprobante.replaceAll("&gt;", ">");			
			claveAcceso= obtenerTagXml(xml, "claveAcceso");
			String fechaEmision = claveAcceso.substring(0,2)+"/"+claveAcceso.substring(2,4)+"/"+claveAcceso.substring(4,8);
			String tipoDoc =  claveAcceso.substring(8, 10);			
			String numeroRuc = claveAcceso.substring(10, 23);
			String tipoAmbiente = claveAcceso.substring(23, 24);		
			String establecimiento = claveAcceso.substring(24, 27);
			String puntoEmision = claveAcceso.substring(27, 30);
			String secuencial = claveAcceso.substring(30, 39);
			String codNumerico =claveAcceso.substring(39, 47);
			String tipoEmision =claveAcceso.substring(47, 48);
			String digitoverificador =claveAcceso.substring(48, 49);	
			String autorizacion =obtenerTagXml(xml, "numeroAutorizacion");
			String fechaAutorizacion=obtenerTagXml(xml, "fechaAutorizacion");
			

			ComprobanteProcesadoIndividual comprobanteIngresado = loggin.obtenerComprobanteAProcesar(claveAcceso);
			if (comprobanteIngresado==null){				
				GeDatosGeneralesCompania datosCia = servicioParametros.obtenerCompaniaPorRUC(numeroRuc);
				if(datosCia==null){
					System.err.println("EL RUC DEL EMISOR: "+numeroRuc+" NO ESTA ASOCIADO A NINGUNA COMPANIA");
					return;
				}
				ComprobanteProcesadoIndividual comp = new ComprobanteProcesadoIndividual();
				System.out.println("SE CONSULTO LA COMPANIA EL ID QUE SE GUARDA ES EL : "+StringUtils.leftPad(datosCia.getId().toString(),2,"0"));
				Date fechaDateEmision = null;
				SimpleDateFormat formato = new SimpleDateFormat(
						"dd/MM/yyyy");
				try {
					fechaDateEmision = formato.parse(fechaEmision);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				if (INombresParametros.TIPO_DOC_FACTURA
						.equals(tipoDoc)
						|| INombresParametros.TIPO_DOC_NC
								.equals(tipoDoc)
						|| INombresParametros.TIPO_DOC_ND
								.equals(tipoDoc)) {					
					identificacionCliente = st.obtenerTagXml(xmlComprobante,
							"identificacionComprador");
					comp.setIdentificacionCliente(identificacionCliente);
					nombreCliente = st.obtenerTagXml(xmlComprobante,
							"razonSocialComprador");					
					comp.setNombreCliente(nombreCliente);
				} else if (INombresParametros.TIPO_DOC_GUIA
						.equals(tipoDoc)) {
					identificacionCliente = st.obtenerTagXml(xmlComprobante,
							"identificacionDestinatario");
					comp.setIdentificacionCliente(identificacionCliente);
					nombreCliente = st.obtenerTagXml(xmlComprobante,
							"razonSocialDestinatario");
					comp.setNombreCliente(nombreCliente);
				} else if (INombresParametros.TIPO_DOC_RETENCION
						.equals(tipoDoc)) {
					identificacionCliente = st.obtenerTagXml(xmlComprobante,
							"identificacionSujetoRetenido");
					comp.setIdentificacionCliente(identificacionCliente);
					nombreCliente = st.obtenerTagXml(xmlComprobante,
							"razonSocialSujetoRetenido");
					comp.setNombreCliente(nombreCliente);
				}		
		
				ambiente = st.obtenerTagXml(xmlComprobante, "ambiente");
				Double imp12=0.0;
			    Double imp0=0.0;
			    Double totalDoc =0.0;
			    Double subTotal =0.0;
			    Double descuento =0.0;
			    String xmlTmp =xmlComprobante;
			    switch (tipoDoc) {
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA:					
					try{
						Factura fac = convierteDocumentAClaseFactura(crearXMLComprobanteRetencion(xmlTmp));
						
					    for(Factura.InfoFactura.TotalConImpuestos.TotalImpuesto imp :fac.getInfoFactura().getTotalConImpuestos().getTotalImpuesto()){
					    	if(imp.getCodigoPorcentaje().equals("2")){
					    		imp12=imp.getValor().doubleValue();
					    	}else{
					    		imp0=imp.getValor().doubleValue();
					    	}
					    }
					    totalDoc =  fac.getInfoFactura().getImporteTotal().doubleValue();
					    subTotal = fac.getInfoFactura().getTotalSinImpuestos().doubleValue();
					    descuento = fac.getInfoFactura().getTotalDescuento().doubleValue();
					}catch(Exception e){
						throw new Throwable("Error al procesar el documento con clave de acceso " + claveAcceso);
					}
					break;
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION:
					try{
						ComprobanteRetencion ret = convierteDocumentARetencion(crearXMLComprobanteRetencion(xmlTmp));
						for (com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.Impuesto det : ret.getImpuestos().getImpuesto()) {								
							totalDoc = totalDoc + det.getValorRetenido().doubleValue();
						}	
					}catch(Exception e){
						e.printStackTrace();
					}
				break;
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION:
					//GuiaRemision guiaRemi = convierteDocumentAClaseGuiaRemision(crearXMLComprobanteRetencion(xml));
					break;
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO:
					NotaCredito notaCredito = convierteDocumentAClaseNotaCredito(crearXMLComprobanteRetencion(xmlTmp));
					 for(com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.TotalConImpuestos.TotalImpuesto  imp: notaCredito.getInfoNotaCredito().getTotalConImpuestos().getTotalImpuesto())
				    	{
				    	if(imp.getCodigoPorcentaje().equals("2")){
				    		imp12=imp.getValor().doubleValue();
				    	}else{
				    		imp0=imp.getValor().doubleValue();
				    	}
				    }
					 totalDoc=notaCredito.getInfoNotaCredito().getValorModificacion().doubleValue();
					 comp.setDocumentoModificaddo(notaCredito.getInfoNotaCredito().getNumDocModificado());
				break;
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO:
					NotaDebito notaDebito = convierteDocumentAClaseNotaDebito(crearXMLComprobanteRetencion(xmlTmp));
					totalDoc=notaDebito.getInfoNotaDebito().getValorTotal().doubleValue();
					break;
				default:
					throw new Throwable("El tipo de documento es invalido");
				}	
			    
			    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
				Date date = null;
				
				try {
					date = format.parse(fechaAutorizacion);					
				} catch (ParseException e) {
					
				}
			    comp.setFechaAutorizacion(date);
			    comp.setFechaEmision(fechaDateEmision);
			    comp.setFechaEnvioMail(new Date());
				comp.setFechaFinEnvioMail(new Date());
			    comp.setAmbiente(tipoAmbiente);
			    comp.setNumDocumento(establecimiento + puntoEmision
						+ StringUtils.leftPad(secuencial, 9, "0"));
			    comp.setTipoDocumento(tipoDoc);
				comp.setClaveAcceso(claveAcceso);
			    comp.setIdSuscriptor(datosCia.getIdSuscriptor());
				comp.setImpuesto0(imp0);
				comp.setImpuesto12(imp12);
				comp.setTotalDocumento(totalDoc);
				comp.setSubTotal(subTotal);	
				comp.setDescuento(descuento);
				comp.setCompania(StringUtils.leftPad(datosCia.getId().toString(),2,"0"));
				comp.setEstado("A");
				comp.setFechaActualizacion(new Date());
				comp.setFechaRegistro(new Date());
				comp.setNumeroIntento(1);
				comp.setObservacion("REGISTRO INICIAL DEL PROCESO DE COMPROBANTE");				
				comp.setAutorizacion(autorizacion);
				try {
				    comp.setXmlRespuestaAutorizacion(ModuladorComprobanteComprimido.comprimirXML(xml,claveAcceso));
				} catch (IOException e1) {					
				}
				try {
					comp.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(xmlComprobante,claveAcceso));
				} catch (IOException e1) {
				}
			
				try{
					loggin.registrarInicioProcesoComprobanteIndividual(comp);
				}catch(Exception e){
					System.err.println("Se detecto un errro en al insertar el comprobante con la clave de acceso : "+claveAcceso);
				}
			}else{
				System.err.println("LA CLAVE DE ACCESO ["+claveAcceso+"] YA SE ENCUENTRA INGRESADA");
			}
		}
		
		public  String obtenerTagXml(String dato, String tag) {
			String iniTag = "<"+tag+">";
			String finTag = "</"+tag+">";
			if (dato.contains(iniTag)) {
				int idxof = dato.indexOf(iniTag);
				int idxofFin = dato.indexOf(finTag);
				return dato.substring(idxof + iniTag.length(),  idxofFin);
			}
			
			String iniTagR = "&lt;"+tag+"&gt;";
			String finTagR = "&lt;/"+tag+"&gt";
			if (dato.contains(iniTagR)) {
				int idxof = dato.indexOf(iniTagR);
				int idxofFin = dato.indexOf(finTagR);
				return dato.substring(idxof + iniTagR.length(),  idxofFin);
			}
			return "";
		}
		
		public String consultaGeneral(String tienda, String fechaEmision){
			
			return "";
		}
		
		@Asynchronous
		public void enviarCorreoArchivoError(String nombreArchivo, String remitente, Long suscriptor, String correoDestinatario, String mensajeError, String tipoDocumento){	
			if(remitente!=null&&!remitente.equals("")){
			String asunto =servicioParametros.getValorParametro("ASUNTO_ENVIO_FILE_ERROR",suscriptor);
			String cuerpoCorreo=servicioParametros.getValorParametro("CUERPO_ENVIO_FILE_ERROR",suscriptor);		
			cuerpoCorreo = cuerpoCorreo.replace("%%ARCHIVO%%", nombreArchivo);		
			cuerpoCorreo = cuerpoCorreo.replace("%%MENSAJE%%", mensajeError);
			cuerpoCorreo = cuerpoCorreo.replace("%%TIPODOCUMENTO%%", tipoDocumento);
			String numDocumento = nombreArchivo;
			numDocumento =numDocumento.substring(numDocumento.indexOf("_")+1);
			numDocumento=numDocumento.substring(0, 16);
			cuerpoCorreo = cuerpoCorreo.replace("%%NUMDOCUMENTO%%", numDocumento);
			GeSuscriptor geSuscriptor =  servicioParametros.consultaSuscriptor(suscriptor);
				try {
					servicioNotificacion.notificacionBienvenida(remitente, correoDestinatario,
							"", asunto, cuerpoCorreo,geSuscriptor.getTrackingNavegacion());
				} catch (Throwable e) {
					System.out.println("ocurrio un error durante el envio del correo");
					e.printStackTrace();
				}	
			}else{
				System.out.println("no se encuentra configurado el correo del usuario");
			}
		}	
		
		//CMA CAMBIOS POR CONSUMO DE WS ERP KFC
		@TransactionAttribute(TransactionAttributeType.SUPPORTS)
		public void actualizarInfoErpWs(Integer cantidadProcesar, String status, Long idSuscriptor , String estadoTomar, String idEmpresaCliente, String compania){
			ServiciosTransformacion st = new ServiciosTransformacion();
			
			System.out.println("CONFIRMACION: Inicia actualizarInfoErpWs");
			SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			List<ComprobanteProcesadoIndividual> listaProcesar=  loggin.listaComprobantesActualizarErp(cantidadProcesar, idSuscriptor, estadoTomar, idEmpresaCliente, compania);
			System.out.println("CONFIRMACION: Se procesaran "+listaProcesar.size());
			
			ClenteCosumoWsErp clienteConsumo = new ClenteCosumoWsErp();
			for (ComprobanteProcesadoIndividual comprobante : listaProcesar) {
				Integer doc = Integer.parseInt(comprobante.getTipoDocumento());
				String msg="NA";
				String msajeComprimido = null;
				if(!estadoTomar.equals("A")){
					try {
						String tipoError = loggin.descripcionEstado(estadoTomar);
						if(tipoError.contains("ERROR EN AUTORIZACION"))
							msajeComprimido = ModuladorComprobanteComprimido.descomprimirXML(comprobante.getXmlRespuestaAutorizacion());
						else
							msajeComprimido = ModuladorComprobanteComprimido.descomprimirXML(comprobante.getXmlRespuestaComprobacion());						
						String mensaje= st.obtenerTagXml(msajeComprimido, "mensaje");
						if(mensaje.contains("<mensaje>")){
							mensaje = mensaje.substring(mensaje.indexOf("<mensaje>")+9);
						}							   
						String infoAdicional= st.obtenerTagXml(msajeComprimido, "informacionAdicional");
						msg = mensaje + "--" + infoAdicional;
					} catch (IOException e) {
						msg="NA";
					}					
				}
				if(msg.length()>= 500){
					msg = msg.substring(0, 499);
				}
				
				msg=replaceCaracter(msg);
				System.out.println("el mensaje a devolver es +"+msg);
				//obtener ruta inicial en base a la compania
				String rutaInicial=servicioParametros.obtenerRutaInicialFtp(comprobante.getCompania());
				if(!rutaInicial.equals("")){
					System.out.println("se va a mover el archivo"+comprobante.getTipoDocumento() + " "+ comprobante.getEstado() + " " +rutaInicial+ " " +comprobante.getNombreArchivoCliente());
					moveFileEstadoFinal(comprobante.getTipoDocumento(),comprobante.getEstado(),rutaInicial,comprobante.getNombreArchivoCliente());
				}
				int respuesta = clienteConsumo.updateAutorizacion(idEmpresaCliente, doc.toString(), comprobante.getNumDocumento().substring(0,6), 
						comprobante.getNumDocumento().substring(6), status, comprobante.getAutorizacion()==null?"":comprobante.getAutorizacion(), msg, 
						formatoFecha.format(comprobante.getFechaAutorizacion()==null?comprobante.getFechaActualizacion():comprobante.getFechaAutorizacion()));	
				Date fechaEnvioConfirmacion = null;
				String observacion =""; 
				if(respuesta==1||respuesta==2){
					fechaEnvioConfirmacion = new Date();
					observacion="Envio confirmacion erp exitoso";
					//comprobante.setFechaEnvioConfirmacionCliente(new Date());
				}else
					observacion="Error en consumo de webservices ERP, codigo devuelto : "+respuesta;
				
				//loggin.actualizarComprobante(comprobante);
				loggin.actualizarErp(observacion,  fechaEnvioConfirmacion,comprobante.getClaveAcceso());
			
			}
		}
		
		public String replaceCaracter(String cadena){
			return cadena.replaceAll("'", "|");
		}

		@TransactionAttribute(TransactionAttributeType.SUPPORTS)
		public List<EstadosComprobantesEmpresas> listaEstadosEmpresaCliente (){
			System.out.println("CONFIRMACION: Se esta en el metodo de ProcesamientoComprobanteIdividual.java");
			return loggin.listaEstadosEmpresaCliente();
		}
				
		public void moveFileEstadoFinal(String tipoDocumento, String estado, String rutaInicial, String archivo){						
			String rutaDestino ="";			
			String rutaError="";	
			String rutaNA="";
			String rutaPrincipal=rutaInicial;
			rutaInicial =rutaPrincipal +"ORIGINALES"+File.separator+"FACTURAS"+File.separator+"PROCESADOS";
			rutaError   =rutaPrincipal +"ORIGINALES"+File.separator+"FACTURAS"+File.separator+"ERROR";
			if(estado.equals("A")){
				if(tipoDocumento.equals("01")){					
					rutaDestino =rutaPrincipal +"AUTORIZADAS"+File.separator+"FACTURAS"+File.separator;
					//rutaError   =rutaPrincipal +"ORIGINALES"+File.separator+"FACTURAS"+File.separator+"ERROR";
					rutaNA=rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"FACTURAS"+File.separator;
				}
				if(tipoDocumento.equals("07")){
				//	rutaInicial =rutaPrincipal +"ORIGINALES"+File.separator+"RETENCIONES"+File.separator+"PROCESADOS";
					rutaDestino =rutaPrincipal +"AUTORIZADAS"+File.separator+"RETENCIONES"+File.separator;
					//rutaError   =rutaPrincipal +"ORIGINALES"+File.separator+"RETENCIONES"+File.separator+"ERROR";
					rutaNA=rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"RETENCIONES"+File.separator;
				}
				if(tipoDocumento.equals("04")){
					//rutaInicial =rutaPrincipal +"ORIGINALES"+File.separator+"NOTA_CREDITO"+File.separator+"PROCESADOS";
					rutaDestino =rutaPrincipal +"AUTORIZADAS"+File.separator+"NOTA_CREDITO"+File.separator;
					//rutaError   =rutaPrincipal +"ORIGINALES"+File.separator+"NOTA_CREDITO"+File.separator+"ERROR";
					rutaNA=rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"NOTA_CREDITO"+File.separator;
				}
				if(tipoDocumento.equals("06")){
					//rutaInicial =rutaPrincipal +"ORIGINALES"+File.separator+"GUIAS"+File.separator+"PROCESADOS";
					rutaDestino =rutaPrincipal +"AUTORIZADAS"+File.separator+"GUIAS"+File.separator;
					//rutaError   =rutaPrincipal +"ORIGINALES"+File.separator+"GUIAS"+File.separator+"ERROR";
					rutaNA=rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"GUIAS"+File.separator;
				}
				if(tipoDocumento.equals("05")){
					//rutaInicial =rutaPrincipal +"ORIGINALES"+File.separator+"NOTA_DEBITO"+File.separator+"PROCESADOS";
					rutaDestino =rutaPrincipal +"AUTORIZADAS"+File.separator+"NOTA_DEBITO"+File.separator;
					//rutaError   =rutaPrincipal +"ORIGINALES"+File.separator+"NOTA_DEBITO"+File.separator+"ERROR";
					rutaNA=rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"NOTA_DEBITO"+File.separator;
				}
				
				File srcFile = new File(rutaInicial+File.separator+archivo);
				if(srcFile.exists()){
					System.out.println("mueve el archivo"+ srcFile.getAbsolutePath());
					OperacionesFile.mueveArchivoProcesado(srcFile,rutaDestino);
					System.out.println("la ruta destino es "+rutaDestino);
				
				}
				// en caso de existir errores previos estan en la carpeta de error 
				File fileDelete = new File(rutaError+File.separator+archivo);
				if(fileDelete.exists()){
					System.out.println("va a borrar el archivo "+ fileDelete.getAbsolutePath());
					fileDelete.delete();
				}
				
				// en caso de estar en la carpeta no autorizado se borra
				File fileDeleteNA = new File(rutaNA+File.separator+archivo);
				if(fileDelete.exists()){
					System.out.println("va a borrar el archivo "+ fileDelete.getAbsolutePath());
					fileDeleteNA.delete();
				}
			}else{
				if(tipoDocumento.equals("01")){
					rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"FACTURAS"+File.separator;
				}
				if(tipoDocumento.equals("07")){					
					rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"RETENCIONES"+File.separator;
				}
				if(tipoDocumento.equals("04")){					
					rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"NOTA_CREDITO"+File.separator;
				}
				if(tipoDocumento.equals("06")){					
					rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"GUIAS"+File.separator;
				}
				if(tipoDocumento.equals("05")){					
					rutaDestino =rutaPrincipal +"NO_AUTORIZADAS"+File.separator+"NOTA_DEBITO"+File.separator;
				}
				File srcFile = new File(rutaInicial+File.separator+archivo);
				if(srcFile.exists()){
					System.out.println("mueve el archivo"+ srcFile.getAbsolutePath());
					OperacionesFile.mueveArchivoProcesado(srcFile,rutaDestino);
				}
			}
		}
		
		//ARO - CAMBIO ENVIO MAIL DE COMP RECHAZADOS
		public String obtenerErrorSri(byte[] xmlRespuesta){
			ServiciosTransformacion st = new ServiciosTransformacion();
			String msajeComprimido = "";
			try {
				msajeComprimido = ModuladorComprobanteComprimido.descomprimirXML(xmlRespuesta);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}                                                        
	        String mensaje= st.obtenerTagXml(msajeComprimido, "mensaje");
	        if(mensaje.contains("<mensaje>")){
	            mensaje = mensaje.substring(mensaje.indexOf("<mensaje>")+9);
	        }                                                                    
	        String infoAdicional = st.obtenerTagXml(msajeComprimido, "informacionAdicional");
	        if(!"".equals(mensaje) || !"".equals(infoAdicional))
	        	return "Mensaje: "+mensaje + ", Informacion Adicional: " + infoAdicional;        
	        return "";
		}
		
		@Asynchronous
		public void enviarCorreoXmlError(String remitente, Long suscriptor, String correoDestinatario, String razonSocial, String identificacionCliente, 
				String tipoDocumento, String errorXml, String numDocumento){ 
		   if(remitente!=null&&!remitente.equals("")){
			   String asunto =servicioParametros.getValorParametro("ASUNTO_ENVIO_XML_ERROR",suscriptor);
			   String cuerpoCorreo=servicioParametros.getValorParametro("CUERPO_ENVIO_XML_ERROR",suscriptor);  
			   cuerpoCorreo = cuerpoCorreo.replace("%%RAZONSOCIAL%%", razonSocial);
			   cuerpoCorreo = cuerpoCorreo.replace("%%IDENTIFICACION%%", identificacionCliente);
			   cuerpoCorreo = cuerpoCorreo.replace("%%TIPODOCUMENTO%%", getTipoDocumento(tipoDocumento));
			   cuerpoCorreo = cuerpoCorreo.replace("%%ERRORXML%%", errorXml);
			   cuerpoCorreo = cuerpoCorreo.replace("%%NUMDOCUMENTO%%", numDocumento);			   
			   GeSuscriptor geSuscriptor =  servicioParametros.consultaSuscriptor(suscriptor);
			   try {
				   System.out.println("listo va a enviar el mail de comprobante rechazado");
				   System.out.println("cuerpoCorreo = "+cuerpoCorreo);
			       servicioNotificacion.notificacionBienvenida(remitente, correoDestinatario, "", asunto, cuerpoCorreo,geSuscriptor.getTrackingNavegacion());
			   } catch (Throwable e) {
			     System.out.println("ocurrio un error durante el envio del correo XML error");
			     e.printStackTrace();
			   } 
		   }else{
		    System.out.println("no se encuentra configurado el correo del usuario XML error");
		   }
		}
		
	
		public void armarEnvioCorreoErrorXml(ComprobanteProcesadoIndividual comprobante, byte[] xmlError) {
			String suscritorPermitido = servicioParametros.getValorParametro("SUSCRIPTOR_PERMITIDO_ENVIA_CORREO");
			System.out.println("ingreso a metodo armarEnvioCorreoErrorXml y suscritorPermitido recibio es "+suscritorPermitido);
			if("".equals(suscritorPermitido) || suscritorPermitido == null){
				System.out.println("PARAMETRO SUSCRIPTOR_PERMITIDO_ENVIA_CORREO NO EXISTE ");
			}else{
				if(suscritorPermitido.equals(comprobante.getIdSuscriptor().toString())){
					System.out.println("suscritorPermitido "+suscritorPermitido);
					GeDatosGeneralesCompania geDatoComp = servicioParametros.datosCompania(comprobante.getCompania());
					String remitente = geDatoComp.getCorreo();
					String destinatarios = geDatoComp.getCorreoEnviarTramaError();
					String errorXml = obtenerErrorSri(xmlError);
					System.out.println("remitente es "+remitente);
					System.out.println("destinatarios son "+destinatarios);
					enviarCorreoXmlError(remitente, comprobante.getIdSuscriptor(), destinatarios, comprobante.getNombreCliente(), comprobante.getIdentificacionCliente(), 
							comprobante.getTipoDocumento(), errorXml, comprobante.getNumDocumento());
				}
			}			
		}
		
		///MBAS INI
		
		@TransactionAttribute(TransactionAttributeType.SUPPORTS)
		public String registrarXmlAutorizadoMigracion(String xml) throws Throwable  {
			
			String  lMensaje=null;
			String identificacionCliente = "", nombreCliente = "", ambiente = "";
			ServiciosTransformacion st = new ServiciosTransformacion();
			String xmlComprobante = "";
			String claveAcceso = "";
			if (xml.contains("<comprobante>")) {
				xmlComprobante = st.obtenerTagXml(xml, "comprobante");
			}
			if (xmlComprobante.contains("<![CDATA[")) {
				xmlComprobante = xmlComprobante.replace("<![CDATA[", "");
				xmlComprobante = xmlComprobante.replace("]]>", "");
			}
			xmlComprobante = xmlComprobante.replaceAll("&lt;", "<");
			xmlComprobante = xmlComprobante.replaceAll("&gt;", ">");
			claveAcceso = obtenerTagXml(xml, "claveAcceso");
			String fechaEmision = claveAcceso.substring(0, 2) + "/" + claveAcceso.substring(2, 4) + "/"
					+ claveAcceso.substring(4, 8);
			String tipoDoc = claveAcceso.substring(8, 10);
			String numeroRuc = claveAcceso.substring(10, 23);
			String tipoAmbiente = claveAcceso.substring(23, 24);
			String establecimiento = claveAcceso.substring(24, 27);
			String puntoEmision = claveAcceso.substring(27, 30);
			String secuencial = claveAcceso.substring(30, 39);
			String codNumerico = claveAcceso.substring(39, 47);
			String tipoEmision = claveAcceso.substring(47, 48);
			String digitoverificador = claveAcceso.substring(48, 49);
			String autorizacion = obtenerTagXml(xml, "numeroAutorizacion");
			
			xml=xml.replace(" class="+COMILLAS+"fechaAutorizacion"+COMILLAS, "");	//FORMATO DE REMAR	  
			String fechaAutorizacion = obtenerTagXml(xml, "fechaAutorizacion");

			ComprobanteProcesadoIndividual comprobanteIngresado = loggin.obtenerComprobanteAProcesar(claveAcceso);
			if (comprobanteIngresado == null) {
				GeDatosGeneralesCompania datosCia = servicioParametros.obtenerCompaniaPorRUC(numeroRuc);
				if (datosCia == null) {
					System.err.println("EL RUC DEL EMISOR: " + numeroRuc + " NO ESTA ASOCIADO A NINGUNA COMPANIA");
					return "EL RUC DEL EMISOR: " + numeroRuc + " NO ESTA ASOCIADO A NINGUNA COMPANIA";
				}
				ComprobanteProcesadoIndividual comp = new ComprobanteProcesadoIndividual();
				System.out.println("SE CONSULTO LA COMPAÑIA EL ID QUE SE GUARDA ES EL : "
						+ StringUtils.leftPad(datosCia.getId().toString(), 2, "0"));
				Date fechaDateEmision = null;
				SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
				try {
					fechaDateEmision = formato.parse(fechaEmision);
				} catch (ParseException lError) {
					lError.printStackTrace();
					lMensaje="ERROR A CONVERTIR LA FECHA DE EMISION";
					return lMensaje;
				}
				if (INombresParametros.TIPO_DOC_FACTURA.equals(tipoDoc) || INombresParametros.TIPO_DOC_NC.equals(tipoDoc)
						|| INombresParametros.TIPO_DOC_ND.equals(tipoDoc)) {
					identificacionCliente = st.obtenerTagXml(xmlComprobante, "identificacionComprador");
					comp.setIdentificacionCliente(identificacionCliente);
					nombreCliente = st.obtenerTagXml(xmlComprobante, "razonSocialComprador");
					comp.setNombreCliente(nombreCliente);
				} else if (INombresParametros.TIPO_DOC_GUIA.equals(tipoDoc)) {
					identificacionCliente = st.obtenerTagXml(xmlComprobante, "identificacionDestinatario");
					comp.setIdentificacionCliente(identificacionCliente);
					nombreCliente = st.obtenerTagXml(xmlComprobante, "razonSocialDestinatario");
					comp.setNombreCliente(nombreCliente);
				} else if (INombresParametros.TIPO_DOC_RETENCION.equals(tipoDoc)) {
					identificacionCliente = st.obtenerTagXml(xmlComprobante, "identificacionSujetoRetenido");
					comp.setIdentificacionCliente(identificacionCliente);
					nombreCliente = st.obtenerTagXml(xmlComprobante, "razonSocialSujetoRetenido");
					comp.setNombreCliente(nombreCliente);
				}

				ambiente = st.obtenerTagXml(xmlComprobante, "ambiente");
				Double imp12 = 0.0;
				Double imp0 = 0.0;
				Double imp14=0.0;
				Double totalDoc = 0.0;
				Double subTotal = 0.0;
				Double descuento = 0.0;
				String xmlTmp = xmlComprobante;
				switch (tipoDoc) {
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA:
					try {
						Factura fac = convierteDocumentAClaseFactura(crearXMLComprobanteRetencion(xmlTmp));

						for (Factura.InfoFactura.TotalConImpuestos.TotalImpuesto imp : fac.getInfoFactura()
								.getTotalConImpuestos().getTotalImpuesto()) {
							 if ((INombresParametros.TIPOIMPUESTO_IVA.equals(imp.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA12.equals(imp.getCodigoPorcentaje())))
						      {		       
									 imp12 = imp.getValor().doubleValue();
						      }
						      if ((INombresParametros.TIPOIMPUESTO_IVA.equals(imp.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA14.equals(imp.getCodigoPorcentaje())))
						      {
							    	  imp14 = imp.getValor().doubleValue();
						      }
						      if ((INombresParametros.TIPOIMPUESTO_IVA.equals(imp.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(imp.getCodigoPorcentaje())))
						      {
						    	  imp0 = imp.getValor().doubleValue();
						       }
						      //MBASTIDAS
							
						}
						totalDoc = fac.getInfoFactura().getImporteTotal().doubleValue();
						subTotal = fac.getInfoFactura().getTotalSinImpuestos().doubleValue();
						descuento = fac.getInfoFactura().getTotalDescuento().doubleValue();
					} catch (Exception lError) {
						
						lError.printStackTrace();
						lMensaje="ERROR AL PROCESAR FACTURA "+lError.getMessage();
						return lMensaje;

					}
					break;
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION:
					try {
						ComprobanteRetencion ret = convierteDocumentARetencion(crearXMLComprobanteRetencion(xmlTmp));
						for (com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.Impuesto det : ret
								.getImpuestos().getImpuesto()) {
							totalDoc = totalDoc + det.getValorRetenido().doubleValue();
							///INI MBASTIDAS
							subTotal= subTotal+det.getBaseImponible().doubleValue();
							///FIN MBASTIDAS
						}
				
					} catch (Exception lError) {
						
						lError.printStackTrace();
						lMensaje="ERROR AL PROCESAR RETENCION "+lError.getMessage();
						return lMensaje;
					}
					break;
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION:
					// GuiaRemision guiaRemi =
					// convierteDocumentAClaseGuiaRemision(crearXMLComprobanteRetencion(xml));
					break;
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO:
					NotaCredito notaCredito = convierteDocumentAClaseNotaCredito(crearXMLComprobanteRetencion(xmlTmp));
					for (com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.TotalConImpuestos.TotalImpuesto imp : notaCredito
							.getInfoNotaCredito().getTotalConImpuestos().getTotalImpuesto()) {
	                 ///MBASTIDAS INI
						
						 if ((INombresParametros.TIPOIMPUESTO_IVA.equals(imp.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA12.equals(imp.getCodigoPorcentaje())))
					      {	 imp12 = imp.getValor().doubleValue();
					      }
					      if ((INombresParametros.TIPOIMPUESTO_IVA.equals(imp.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA14.equals(imp.getCodigoPorcentaje())))
					      {	  imp14 = imp.getValor().doubleValue();
					      	 }
					      if ((INombresParametros.TIPOIMPUESTO_IVA.equals(imp.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(imp.getCodigoPorcentaje())))
					      {		       
					    	  imp0 = imp.getValor().doubleValue();
					       }
					     			
					 ///MBASTIDAS FIN

					}
					totalDoc = notaCredito.getInfoNotaCredito().getValorModificacion().doubleValue();
					 ///MBASTIDAS FIN
					subTotal= notaCredito.getInfoNotaCredito().getTotalSinImpuestos().doubleValue();
					
					 ///MBASTIDAS FIN
					// comp.setDocumentoModificaddo(notaCredito.getInfoNotaCredito().getNumDocModificado());
					
					break;
				case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO:
					NotaDebito notaDebito = convierteDocumentAClaseNotaDebito(crearXMLComprobanteRetencion(xmlTmp));
							
					for (com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.Impuesto ti : notaDebito.getInfoNotaDebito().getImpuestos()
							.getImpuesto()) {

						 if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA12.equals(ti.getCodigoPorcentaje())))
					      {	imp12 = ti.getValor().doubleValue();
					      }
					      		       
					      if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA14.equals(ti.getCodigoPorcentaje())))
					      {  imp14 = ti.getValor().doubleValue();
					       }
					      				      
					      if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && (INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(ti.getCodigoPorcentaje())))
					      {   	  imp0 = ti.getValor().doubleValue();
					       }
											
						}
					///FIN MBASTIDAS
					totalDoc = notaDebito.getInfoNotaDebito().getValorTotal().doubleValue();
					 ///MBASTIDAS FIN
					subTotal= notaDebito.getInfoNotaDebito().getTotalSinImpuestos().doubleValue();
					 ///MBASTIDAS FIN
				
					break;
				default:
					
					lMensaje="ERROR EL TIPO DE DOCUMENTO ES INAVLIDO ";
					return lMensaje;

				}
				
				
				 Date fechaAutorizacionDate=validadorFecha(fechaAutorizacion);
				  if(fechaAutorizacionDate==null){
				      System.out.print("ERROR AL DAR FORMATO A LA FECHA DE AUTORIZACION "+fechaAutorizacion);
				  	  lMensaje="ERROR AL DAR FORMATO A LA FECHA DE AUTORIZACION "+fechaAutorizacion;
				  	return lMensaje;
				    }
				  
				System.out.println(fechaAutorizacionDate);
			
				comp.setFechaAutorizacion(fechaAutorizacionDate);
				comp.setFechaEmision(fechaDateEmision);
				comp.setFechaEnvioMail(new Date());
				comp.setFechaFinEnvioMail(new Date());
				comp.setAmbiente(tipoAmbiente);
				comp.setNumDocumento(establecimiento + puntoEmision + StringUtils.leftPad(secuencial, 9, "0"));
				comp.setTipoDocumento(tipoDoc);
				comp.setClaveAcceso(claveAcceso);
				comp.setIdSuscriptor(datosCia.getIdSuscriptor());
				comp.setImpuesto0(imp0);
				comp.setImpuesto12(imp12);
				comp.setTotalDocumento(totalDoc);
				comp.setSubTotal(subTotal);
				comp.setDescuento(descuento);
				comp.setCompania(StringUtils.leftPad(datosCia.getId().toString(), 2, "0"));
				comp.setEstado("A");
				comp.setFechaActualizacion(new Date());
				comp.setFechaRegistro(new Date());
				comp.setNumeroIntento(1);
				comp.setObservacion("REGISTRO INICIAL DEL PROCESO DE COMPROBANTE");
				comp.setAutorizacion(autorizacion);
				
				/*INI MBASTIDAS01112016 */
				//comp.setImpuesto14(imp14);
				if(imp14>0 ){
					comp.setImpuesto12(imp14);
				}
			
				HashMap<String, Object> datosCadenaLocal =servicioParametros.obtenerCadenaLocal(datosCia.getId(),
	                    establecimiento,
	                    puntoEmision);
				comp.setEstablecimiento(establecimiento);
	            comp.setPuntoEmision(puntoEmision);
	            comp.setLocal(datosCadenaLocal.get("local")==null?null:((BigDecimal)datosCadenaLocal.get("local")).intValue());
	            comp.setCadena(datosCadenaLocal.get("cadena")==null?null:((BigDecimal)datosCadenaLocal.get("cadena")).intValue());
	            comp.setSecuencial(StringUtils.leftPad(secuencial, 9, "0"));
	            /*FIN MBASTIDAS01112016 */
				
				String infoAdicional = obtenerTagXml(xmlComprobante, "infoAdicional");
				String emailCliente = obtenerTagInfoAdicional(infoAdicional, "Email");
				if (emailCliente == null || emailCliente.isEmpty())
					emailCliente = obtenerTagInfoAdicional(infoAdicional, "MailCliente");
				comp.setEmailCliente(emailCliente);
				try {
					comp.setXmlRespuestaAutorizacion(ModuladorComprobanteComprimido.comprimirXML(xml, claveAcceso));
				} catch (IOException lError) {
					lError.printStackTrace();
					lMensaje="ERROR AL COMPRIMIR EL ARCHIVO "+lError.getMessage() ;
					return lMensaje;

				}
				try {
					comp.setXmlComprobante(ModuladorComprobanteComprimido.comprimirXML(xmlComprobante, claveAcceso));
				} catch (IOException lError) {
					
					lError.printStackTrace();
					lMensaje="ERROR AL COMPRIMIR EL ARCHIVO 2 "+lError.getMessage() ;
					return lMensaje;
				
				}
				try {
					loggin.registrarInicioProcesoComprobanteIndividual(comp);
				} catch (Exception lError) {
					
					System.err.println(
							"SE DETECTO UN ERRRO EN AL INSERTAR EL COMPROBANTE CON LA CLAVE DE ACCESO : " + claveAcceso);
					lError.printStackTrace();
					lMensaje="SE DETECTO UN ERRRO EN AL INSERTAR EL COMPROBANTE CON LA CLAVE DE ACCESO" + claveAcceso ;
					return lMensaje;
				
				}
			} else {
				lMensaje="LA CLAVE DE ACCESO [" + claveAcceso + "] YA SE ENCUENTRA INGRESADA";
				System.err.println("LA CLAVE DE ACCESO [" + claveAcceso + "] YA SE ENCUENTRA INGRESADA");
				return lMensaje;
				
			}
			
			return lMensaje;
		}
		
		public String obtenerTagInfoAdicional(String dato, String tag) {
            String delimitador = ABRE_CAMPO_ADICIONAL;

            String iniTag = "<campoAdicional nombre=\"" + tag + "\">";
            String finTag = "</campoAdicional>";

            String[] array = dato.split(delimitador);
            for (int i = 0; i < array.length; i++) {
                   String s = new String(ABRE_CAMPO_ADICIONAL + array[i]);
                   if (s.contains(iniTag)) {
                          int idxof = s.indexOf(iniTag);
                          int idxofFin = s.indexOf(finTag);
                          return s.substring(idxof + iniTag.length(), idxofFin);
                   }
            }

            return "";
      }
		
		 public static Date formateoFecha(String lFecha, String lFormato) throws Throwable {
			  SimpleDateFormat lFormateador = new SimpleDateFormat(lFormato);
			  return lFormateador.parse(lFecha);

			 }
		 
		 public static Date validadorFecha(String pFechaEvaluar) {
			  String[] lFormato = new String[] { "yyyy-MM-dd'T'HH:mm:ssX", "dd/MM/yyyy HH:mm:ss",
			    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "yyyy-MM-dd HH:mm:ss" };
			  Date lFechaResultante = null;
			  for (String lFormatoValidar : lFormato) {
			   try {
			    lFechaResultante = formateoFecha(pFechaEvaluar, lFormatoValidar);
			    System.out.println("Formato aplicado : " + lFormatoValidar);
			    break;
			   } catch (Throwable e) {
			    System.err.println("No es posible aplicar el formato :" + lFormatoValidar);
			   }
			  }
			  return lFechaResultante;

			 }
		
		///MBAS FIN
	
	
	
}
