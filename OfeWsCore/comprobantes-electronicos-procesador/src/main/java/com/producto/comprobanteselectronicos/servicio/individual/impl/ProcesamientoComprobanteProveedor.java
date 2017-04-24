package com.producto.comprobanteselectronicos.servicio.individual.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.Autorizacion;
import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.AutorizacionComprobanteResponse;
import com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.RespuestaComprobante;
import com.producto.comprobanteselectronicos.autorizador.servicios.implementacion.AutorizacionComprobante;
import com.producto.comprobanteselectronicos.documentos.modelo.entidades.FeComprobanteProveedor;
import com.producto.comprobanteselectronicos.documentos.modelo.entidades.FeDetalleComprobanteProveedor;
import com.producto.comprobanteselectronicos.eao.util.ServicioPropertie;
import com.producto.comprobanteselectronicos.generales.ConstanteInfoTributaria;
import com.producto.comprobanteselectronicos.logs.modelo.util.ParametrosEstaticosBase;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.ComprobanteRetencion;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.comprobanteretencion.Impuesto;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.NotaDebito;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.NotaDebito.InfoNotaDebito.Pagos;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.guiaremision.Destinatario;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.guiaremision.GuiaRemision;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.NotaCredito;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.TotalConImpuestos;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v210.factura.Factura;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v210.factura.Factura.InfoAdicional.CampoAdicional;
import com.producto.comprobanteselectronicos.modelo.normal.xsd.v210.factura.Pagos.Pago;
import com.producto.comprobanteselectronicos.parametros.modelo.entidades.GeDatosGeneralesCompania;
import com.producto.comprobanteselectronicos.servicio.etapas.ServicioAutorizacion;
import com.producto.comprobanteselectronicos.servicio.individual.IProcesamientoComprobanteProveedor;
import com.producto.comprobanteselectronicos.servicio.logicanegocio.ServicioComprobacionWS;
import com.producto.comprobanteselectronicos.servicio.parametros.INombresParametros;
import com.producto.comprobanteselectronicos.servicio.parametros.IServicioParametros;
import com.producto.comprobanteselectronicos.servicios.IServicioFacturaLoteMasivo;
import com.producto.comprobanteselectronicos.utilerias.generales.ModuladorComprobanteComprimido;
import com.producto.comprobanteselectronicos.utilerias.generales.ServiciosTransformacion;

@Stateless
public class ProcesamientoComprobanteProveedor implements
		IProcesamientoComprobanteProveedor {

	private String URLWSSRI;
	private static String  COD_NOTA_CREDITO="04";
	private static String  COD_FACTURA="01";

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_EXTRACION_LOTE)
	private IServicioFacturaLoteMasivo servicioFacturaLoteMasivo;
	
	@EJB
	private ServicioAutorizacion servicioAutorizacion;

	@EJB(lookup = INombresParametros.NOMBRE_JNDI_COMPONENTE_PARAMETROS)
	private IServicioParametros servicioParametros;
	
	@EJB
	private ServicioComprobacionWS comprobadorWS;

	@PostConstruct
	private void init() {
		URLWSSRI = ParametrosEstaticosBase.getURLWSSRIAutorizacion();// servicioParametros.getValorParametro(INombresParametros.PROCESO_AUTORIZACION_INDIVIDUAL_URL_OFFLINE);
	}

	public void validaYGuardaXMLMail(String xmlProveedor, Object objCompania)
			throws Throwable 
			{
		Boolean avanza  = true;
		RespuestaComprobante respuestaComprobante = null;
		GeDatosGeneralesCompania compania = (GeDatosGeneralesCompania)objCompania;
		String tipoDocumento = null; 
		
		
		if (xmlProveedor != null) {
			FeComprobanteProveedor comProv = new FeComprobanteProveedor();
			String xmlStringProveedor = xmlProveedor;
			String claveAcceso = obtenerClaveAcceso(xmlStringProveedor);
			
			//INI CMA 
			if (claveAcceso!= null){
					String xmlProv = servicioFacturaLoteMasivo.obtenerXmlProveedor(claveAcceso);
					if(xmlProv!=null){
						avanza = false;
						System.out.println("LA CLAVE DE ACCESO "+claveAcceso+" YA SE ENCUENTRA INGRESADA");
					}
				
			}else{
				avanza = false;
				System.out.println("LA CLAVE DE ACCESO ESTA VACIA EN EL XML DEL PROVEEDOR");
			}
			//FIN CMA
			if (avanza){
				
			
			
			String numdocumento = obtenerNumDocumento(xmlStringProveedor);
//			System.err.println("953");
			
			/*System.err.println("------------------------------------");
			System.err.println("clave de acceso " + claveAcceso);
			System.err.println("url del sri " + URLWSSRI);
			System.err.println("------------------------------------");
			*/
			
			
			if (comprobadorWS.servicioAutorizacionIndividual(URLWSSRI)) {
				//System.err.println("956");
				//System.err.println("El web service esta activo ");
				respuestaComprobante = AutorizacionComprobante
						.consultaAutorizacionProveedor(claveAcceso, "",
								RespuestaComprobante.class,URLWSSRI);
				
			} else {
				System.err.println("NO HAY WEB SERVICE");
				return;
			}
			
			//System.err.println("Numero XML validos " + respuestaComprobante.getNumeroComprobantes());

			//		System.err.println("965");
			if (new Integer(respuestaComprobante.getNumeroComprobantes()) > 0) {
				//System.err.println("956");
				tipoDocumento = claveAcceso.substring(8, 10);
				Object respuestaWebService[] = determinaComprobanteAutorizado(respuestaComprobante);
				String xmlAutorizado = (String) respuestaWebService[0];
				String numeroAutorizacion = (String) respuestaWebService[1];
				//System.err.println("972");
				String rucDestinatario = "";
				if (xmlAutorizado != null) {
					switch (tipoDocumento) {
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA:
						Factura fac = convierteDocumentAClaseFactura(crearXMLComprobanteRetencion(xmlAutorizado));
						comProv.setNumComprobante(fac.getInfoTributaria()
								.getEstab()
								+ "-"
								+ fac.getInfoTributaria().getPtoEmi()
								+ "-"
								+ fac.getInfoTributaria().getSecuencial());
						rucDestinatario = fac.getInfoFactura()
								.getIdentificacionComprador();
						comProv.setRucEmisor(fac.getInfoTributaria().getRuc());
						comProv.setRazonSocial(fac.getInfoTributaria().getRazonSocial());

						break;
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO:
						NotaCredito notaCredito = convierteDocumentAClaseNotaCredito(crearXMLComprobanteRetencion(xmlAutorizado));
						comProv.setNumComprobante(notaCredito
								.getInfoTributaria().getEstab()
								+ "-"
								+ notaCredito.getInfoTributaria().getPtoEmi()
								+ "-"
								+ notaCredito.getInfoTributaria()
										.getSecuencial());
						rucDestinatario = notaCredito.getInfoNotaCredito()
								.getIdentificacionComprador();
						comProv.setRucEmisor(notaCredito.getInfoTributaria()
								.getRuc());
						comProv.setRazonSocial(notaCredito.getInfoTributaria().getRazonSocial());
						break;
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO:
						NotaDebito notaDebito = convierteDocumentAClaseNotaDebito(crearXMLComprobanteRetencion(xmlAutorizado));
						comProv.setNumComprobante(notaDebito
								.getInfoTributaria().getEstab()
								+ "-"
								+ notaDebito.getInfoTributaria().getPtoEmi()
								+ "-"
								+ notaDebito.getInfoTributaria()
										.getSecuencial());
						rucDestinatario = notaDebito.getInfoNotaDebito()
								.getIdentificacionComprador();
						comProv.setRucEmisor(notaDebito.getInfoTributaria()
								.getRuc());
						comProv.setRazonSocial(notaDebito.getInfoTributaria().getRazonSocial());
						break;
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION:
						GuiaRemision guiaRemi = convierteDocumentAClaseGuiaRemision(crearXMLComprobanteRetencion(xmlAutorizado));
						comProv.setNumComprobante(guiaRemi.getInfoTributaria()
								.getEstab()
								+ "-"
								+ guiaRemi.getInfoTributaria().getPtoEmi()
								+ "-"
								+ guiaRemi.getInfoTributaria().getSecuencial());
						rucDestinatario = guiaRemi.getDestinatarios()
								.getDestinatario().get(0)
								.getIdentificacionDestinatario();
						comProv.setRucEmisor(guiaRemi.getInfoTributaria()
								.getRuc());
						comProv.setRazonSocial(guiaRemi.getInfoTributaria().getRazonSocial());
						break;
					case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION:
						ComprobanteRetencion rentencion = convierteDocumentARetencion(crearXMLComprobanteRetencion(xmlAutorizado));
						comProv.setNumComprobante(rentencion
								.getInfoTributaria().getEstab()
								+ "-"
								+ rentencion.getInfoTributaria().getPtoEmi()
								+ "-"
								+ rentencion.getInfoTributaria()
										.getSecuencial());
						rucDestinatario = rentencion.getInfoCompRetencion()
								.getIdentificacionSujetoRetenido();
						comProv.setRucEmisor(rentencion.getInfoTributaria()
								.getRuc());
						comProv.setRazonSocial(rentencion.getInfoTributaria().getRazonSocial());
						break;
					default:
						break;
					}
					//System.err.println("1036");
					if (compania.getIdentificacion().equals(rucDestinatario)) {
						comProv.setEstado("V");
						comProv.setObservacion("COMPROBANTE VALIDADO EXITOSAMENTE");
						comProv.setCompania(StringUtils.leftPad(String.valueOf(compania.getId()), 2, "0"));
					} else {
						comProv.setEstado("E");
						comProv.setObservacion("RUC DESTINATARIO NO COINCIDE CON LA EMPRESA");
					}
					//System.err.println("1044");
					comProv.setFechaAutorizacion((Date) respuestaWebService[2]);
					comProv.setNumAutorizacion(numeroAutorizacion);
					comProv.setXmlSRI(xmlAutorizado);
					comProv.setRucDestinatario(rucDestinatario);
				}else {
					comProv.setEstado("E");
					comProv.setObservacion("EL COMPROBANTE NO SE ENCUENTRA AUTORIZADO EN EL SRI");
				}
			} else {
				//System.err.println("1049");
				if (claveAcceso != null && claveAcceso.length() > 10) {
					tipoDocumento = claveAcceso.substring(8, 10);
				}
				comProv.setNumComprobante(numdocumento);
				comProv.setTipo(tipoDocumento);
				comProv.setRucEmisor(obtenerRuc(xmlProveedor));
				comProv.setRazonSocial(obtenerRazonSocial(xmlProveedor));
				comProv.setEstado("E");
				comProv.setObservacion("COMPROBANTE NO EXISTE EN EL SRI");
			}
			comProv.setClaveAcceso(claveAcceso);
			comProv.setXmlProveedor(xmlStringProveedor);
			comProv.setFechaRegistro(new Date());
			comProv.setTipo(tipoDocumento);
			comProv.setCompania(String.valueOf(compania.getId()));
			//System.err.println("1056:guardar:" + comProv.getClaveAcceso());
			 servicioFacturaLoteMasivo.guardaComprobanteProveedor(comProv);
			//System.err.println("1058");
		
			 //
			}
			 //
		} else {
			//System.err.println("1060");
			System.err.println("No guardo nada por que el Input Stream es nulo ");
		}
	}
	
	private String obtenerNumDocumento(String dato) {
		String numdocumento = "";
		if (dato.contains("<estab>") && dato.contains("</estab>")) {
			int idxof = dato.indexOf("<estab>");
			numdocumento = dato.substring(idxof + 7,idxof +  10) + "-";
		}
		if (dato.contains("&lt;estab&gt;") && dato.contains("&lt;/estab&gt;")) {
			int idxof = dato.indexOf("&lt;estab&gt;");
			numdocumento = dato.substring( idxof + 13, idxof + 16) + "-";
		}
		if (dato.contains("<ptoEmi>") && dato.contains("</ptoEmi>")) {
			int idxof = dato.indexOf("<ptoEmi>");
			numdocumento += dato.substring(idxof + 8,idxof +  11) + "-";
		}
		if (dato.contains("&lt;ptoEmi&gt;") && dato.contains("&lt;/ptoEmi&gt;")) {
			int idxof = dato.indexOf("&lt;ptoEmi&gt;");
			numdocumento += dato.substring( idxof + 14, idxof + 17) + "-";
		}
		if (dato.contains("<secuencial>") && dato.contains("</secuencial>")) {
			int idxof = dato.indexOf("<secuencial>");
			numdocumento += dato.substring(idxof + 12,idxof +  21);
		}
		if (dato.contains("&lt;secuencial&gt;") && dato.contains("&lt;/secuencial&gt;")) {
			int idxof = dato.indexOf("&lt;secuencial&gt;");
			numdocumento += dato.substring( idxof + 18, idxof + 27);
		}
		
		return numdocumento;
	}

	private ComprobanteRetencion convierteDocumentARetencion(Document xml)
			throws Throwable {
		// Document doc = crearXMLComprobanteRetencion(xmlretencion);
		JAXBContext jaxbcontext = JAXBContext
				.newInstance(ComprobanteRetencion.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		ComprobanteRetencion comp = (ComprobanteRetencion) unmarshaller
				.unmarshal(xml);
		return comp;
	}

	private Factura convierteDocumentAClaseFactura(Document xml)
			throws Throwable {
		// Document doc = crearXMLComprobanteRetencion(xmlretencion);
		JAXBContext jaxbcontext = JAXBContext.newInstance(Factura.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		Factura comp = (Factura) unmarshaller.unmarshal(xml);
		return comp;
	}

	private NotaCredito convierteDocumentAClaseNotaCredito(Document xml)
			throws Throwable {
		// Document doc = crearXMLComprobanteRetencion(xmlretencion);
		JAXBContext jaxbcontext = JAXBContext.newInstance(NotaCredito.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		NotaCredito comp = (NotaCredito) unmarshaller.unmarshal(xml);
		return comp;
	}

	private NotaDebito convierteDocumentAClaseNotaDebito(Document xml)
			throws Throwable {
		// Document doc = crearXMLComprobanteRetencion(xmlretencion);
		JAXBContext jaxbcontext = JAXBContext.newInstance(NotaDebito.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		NotaDebito comp = (NotaDebito) unmarshaller.unmarshal(xml);
		return comp;
	}

	private GuiaRemision convierteDocumentAClaseGuiaRemision(Document xml)
			throws Throwable {
		// Document doc = crearXMLComprobanteRetencion(xmlretencion);
		JAXBContext jaxbcontext = JAXBContext.newInstance(GuiaRemision.class);
		Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
		GuiaRemision comp = (GuiaRemision) unmarshaller.unmarshal(xml);
		return comp;
	}

	private Document crearXMLComprobanteRetencion(String comprobante)
			throws ParserConfigurationException, JAXBException,
			TransformerException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(new InputSource(new StringReader(
				comprobante)));
		return document;
	}

	private Object[] determinaComprobanteAutorizado(
			RespuestaComprobante respuestaComprobante) {
		List<Autorizacion> autorizacions = respuestaComprobante
				.getAutorizaciones().getAutorizacion();
		Object[] respuesta = new Object[3];

		for (Autorizacion autorizacion : autorizacions) {
			if (autorizacion.getEstado().equals("AUTORIZADO")) {
				respuesta[0] = autorizacion.getComprobante();
				respuesta[1] = autorizacion.getNumeroAutorizacion();
				respuesta[2] = autorizacion.getFechaAutorizacion().toGregorianCalendar().getTime();
				break;
			}
		}

		return respuesta;
	}

//	public static void main(String args[])
//	{
//		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><Autorizacion>  <estado>AUTORIZADO</estado>  <numeroAutorizacion>2609201409570909901790850010961051527</numeroAutorizacion>  <fechaAutorizacion>Friday, September 26, 2014</fechaAutorizacion>  <ambiente>PRODUCCIÓN</ambiente>  <comprobante>&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&lt;comprobanteRetencion id=&quot;comprobante&quot; version=&quot;1.0.0&quot;&gt;&lt;infoTributaria&gt;&lt;ambiente&gt;2&lt;/ambiente&gt;&lt;tipoEmision&gt;1&lt;/tipoEmision&gt;&lt;razonSocial&gt;PAPELESA CIA. LTDA&lt;/razonSocial&gt;&lt;nombreComercial&gt;Papelesa Cia. Ltda&lt;/nombreComercial&gt;&lt;ruc&gt;0990179085001&lt;/ruc&gt;&lt;claveAcceso&gt;2609201407099017908500120010010000010710000107114&lt;/claveAcceso&gt;&lt;codDoc&gt;07&lt;/codDoc&gt;&lt;estab&gt;001&lt;/estab&gt;&lt;ptoEmi&gt;001&lt;/ptoEmi&gt;"
//				+ "&lt;secuencial&gt;000001071&lt;/secuencial&gt;&lt;dirMatriz&gt;SN KILOMETRO 11.5 VIA DAULE  &lt;/dirMatriz&gt;&lt;/infoTributaria&gt;&lt;infoCompRetencion&gt;&lt;fechaEmision&gt;26/09/2014&lt;/fechaEmision&gt;&lt;contribuyenteEspecial&gt;06925&lt;/contribuyenteEspecial&gt;&lt;obligadoContabilidad&gt;SI&lt;/obligadoContabilidad&gt;&lt;tipoIdentificacionSujetoRetenido&gt;04&lt;/tipoIdentificacionSujetoRetenido&gt;&lt;razonSocialSujetoRetenido&gt;TRANSOCEANICA COMPANIA LIMITADA&lt;/razonSocialSujetoRetenido&gt;&lt;identificacionSujetoRetenido&gt;0990082820001&lt;/identificacionSujetoRetenido&gt;&lt;periodoFiscal&gt;09/2014&lt;/periodoFiscal&gt;&lt;/infoCompRetencion&gt;&lt;impuestos&gt;&lt;impuesto&gt;&lt;codigo&gt;1&lt;/codigo&gt;&lt;codigoRetencion&gt;341&lt;/codigoRetencion&gt;&lt;baseImponible&gt;15.00&lt;/baseImponible&gt;&lt;porcentajeRetener&gt;2&lt;/porcentajeRetener&gt;&lt;valorRetenido&gt;0.30&lt;/valorRetenido&gt;&lt;codDocSustento&gt;01&lt;/codDocSustento&gt;&lt;numDocSustento&gt;001010000001060&lt;/numDocSustento&gt;&lt;fechaEmisionDocSustento&gt;26/09/2014&lt;/fechaEmisionDocSustento&gt;&lt;/impuesto&gt;&lt;/impuestos&gt;&lt;infoAdicional&gt;&lt;campoAdicional nombre=&quot;Direccion Cliente&quot;&gt;MALECON # 1401 E ILLINGWORTH (ESQ.)&lt;/campoAdicional&gt;&lt;campoAdicional nombre=&quot;Total Retenido&quot;&gt;0.30&lt;/campoAdicional&gt;&lt;/infoAdicional&gt;&lt;ds:Signature xmlns:ds=&quot;http://www.w3.org/2000/09/xmldsig#&quot; xmlns:etsi=&quot;http://uri.etsi.org/01903/v1.3.2#&quot; Id=&quot;Signature424041&quot;&gt;&lt;ds:SignedInfo Id=&quot;Signature-SignedInfo306627&quot;&gt;&lt;ds:CanonicalizationMethod Algorithm=&quot;http://www.w3.org/TR/2001/REC-xml-c14n-20010315&quot;&gt;&lt;/ds:CanonicalizationMethod&gt;&lt;ds:SignatureMethod Algorithm=&quot;http://www.w3.org/2000/09/xmldsig#rsa-sha1&quot;&gt;&lt;/ds:SignatureMethod&gt;&lt;ds:Reference Id=&quot;SignedPropertiesID57143&quot; Type=&quot;http://uri.etsi.org/01903#SignedProperties&quot; URI=&quot;#Signature424041-SignedProperties532898&quot;&gt;&lt;ds:DigestMethod Algorithm=&quot;http://www.w3.org/2000/09/xmldsig#sha1&quot;&gt;&lt;/ds:DigestMethod&gt;&lt;ds:DigestValue&gt;t0r8E+QN0TI/h2xRVxHVwXi5XMI=&lt;/ds:DigestValue&gt;&lt;/ds:Reference&gt;&lt;ds:Reference URI=&quot;#Certificate1149275&quot;&gt;&lt;ds:DigestMethod Algorithm=&quot;http://www.w3.org/2000/09/xmldsig#sha1&quot;&gt;&lt;/ds:DigestMethod&gt;&lt;ds:DigestValue&gt;hZpUKE+DbsZXKSBi5LSxNeuMKNw=&lt;/ds:DigestValue&gt;&lt;/ds:Reference&gt;&lt;ds:Reference Id=&quot;Reference-ID-334494&quot; URI=&quot;#comprobante&quot;&gt;&lt;ds:Transforms&gt;&lt;ds:Transform Algorithm=&quot;http://www.w3.org/2000/09/xmldsig#enveloped-signature&quot;&gt;&lt;/ds:Transform&gt;&lt;/ds:Transforms&gt;&lt;ds:DigestMethod Algorithm=&quot;http://www.w3.org/2000/09/xmldsig#sha1&quot;&gt;&lt;/ds:DigestMethod&gt;&lt;ds:DigestValue&gt;/bJJR1X7RlHpx1RzXPYM8xzo9OQ=&lt;/ds:DigestValue&gt;&lt;/ds:Reference&gt;&lt;/ds:SignedInfo&gt;&lt;ds:SignatureValue Id=&quot;SignatureValue110779&quot;&gt;dEAICoy/BWKKqs6M9pMzZavpEuLkCVB5l4r8czANdJeFGtXVioXD7awtLA3Bk9IntVSOdmO3lGsL1wm2/rwR7Glxz3hCUbJdjmUHX0yyiWt/ggiaGa8xMAv3ckXH20ri8otAF0CR+fuf7qMhzbjfakR8+vFhe6D6uBknQDMkeX7oTM4B20RP/1R3aQXZX67j+m4RaA1mbtkzwwDuxRI1HXVh+Xfcc3fBf8aKMvfAPmL1oxmPXrnNEv8m8jisQiB/q90cdx5YuDSNO5Q2aQhHJp7bnwbRgdpJEjHrMrOWcKZ1lTpR4PJdXU1dnR+XZ/souzhmJV9cWkBupXxHsOopiQ==&lt;/ds:SignatureValue&gt;&lt;ds:KeyInfo Id=&quot;Certificate1149275&quot;&gt;&lt;ds:X509Data&gt;&lt;ds:X509Certificate&gt;MIIJ4TCCB8mgAwIBAgIETkLcJDANBgkqhkiG9w0BAQsFADCBoTELMAkGA1UEBhMCRUMxIjAgBgNVBAoTGUJBTkNPIENFTlRSQUwgREVMIEVDVUFET1IxNzA1BgNVBAsTLkVOVElEQUQgREUgQ0VSVElGSUNBQ0lPTiBERSBJTkZPUk1BQ0lPTi1FQ0lCQ0UxDjAMBgNVBAcTBVFVSVRPMSUwIwYDVQQDExxBQyBCQU5DTyBDRU5UUkFMIERFTCBFQ1VBRE9SMB4XDTE0MDYxOTE2MjIwN1oXDTE1MDYxOTE2NTIwN1owgbgxCzAJBgNVBAYTAkVDMSIwIAYDVQQKExlCQU5DTyBDRU5UUkFMIERFTCBFQ1VBRE9SMTcwNQYDVQQLEy5FTlRJREFEIERFIENFUlRJRklDQUNJT04gREUgSU5GT1JNQUNJT04tRUNJQkNFMQ4wDAYDVQQHEwVRVUlUTzE8MBEGA1UEBRMKMDAwMDA3MzA0OTAnBgNVBAMTIEhFQ1RPUiBHRVJBUkRPIEFMQkFSQURPIFBBUlJBTEVTMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3M+QUO/yZotkCzmkU1yVEtJJEAaidsa+G/EHX23dmBnjfyO1egOPy0QPqceVIS6NZ+WjIEem0L0wYfYan+jMYNnQPVOo7R0e6glOD+R+KmU1UGohi4z9fdwnHx+izap5jF5bQB0MHSGJPogRUN+3tBGIfm82yoZyqe+xgvNv3vlrCVXvtslEEnEiYH2LgKeZ2KuuFAGo7fTS/D8nmunPFucvgkFb1z2nRDDxqwSaVjwuO4FBu+kjwDaezcUq5WKE1o8/xrKXC2T5SA+zn6Ch9+2TLGyQLAHXNBNTOIKG0L/8RBwJmjx+zMEMM5zmBCBnHZ050gh12offmp2ELijGNwIDAQABo4IFBjCCBQIwCwYDVR0PBAQDAgeAMGcGA1UdIARgMF4wXAYLKwYBBAGCqDsCAgEwTTBLBggrBgEFBQcCARY/aHR0cDovL3d3dy5lY2kuYmNlLmVjL3BvbGl0aWNhLWNlcnRpZmljYWRvL3BlcnNvbmEtanVyaWRpY2EucGRmMIGRBggrBgEFBQcBAQSBhDCBgTA+BggrBgEFBQcwAYYyaHR0cDovL29jc3AuZWNpLmJjZS5lYy9lamJjYS9wdWJsaWN3ZWIvc3RhdHVzL29jc3AwPwYIKwYBBQUHMAGGM2h0dHA6Ly9vY3NwMS5lY2kuYmNlLmVjL2VqYmNhL3B1YmxpY3dlYi9zdGF0dXMvb2NzcDAhBgorBgEEAYKoOwMKBBMTEVBBUEVMRVNBIENJQSBMVERBMB0GCisGAQQBgqg7AwsEDxMNMDk5MDE3OTA4NTAwMTAaBgorBgEEAYKoOwMBBAwTCjA5MjA2NjkwMDkwHgYKKwYBBAGCqDsDAgQQEw5IRUNUT1IgR0VSQVJETzAYBgorBgEEAYKoOwMDBAoTCEFMQkFSQURPMBgGCisGAQQBgqg7AwQEChMIUEFSUkFMRVMwGAYKKwYBBAGCqDsDBQQKEwhDT05UQURPUjAgBgorBgEEAYKoOwMHBBITEEtNIDExNSBWSUEgREFVTEUwGQYKKwYBBAGCqDsDCAQLEwkwNDIxMDA3NzcwGQYKKwYBBAGCqDsDCQQLEwlHdWF5YXF1aWwwFwYKKwYBBAGCqDsDDAQJEwdFQ1VBRE9SMCAGCisGAQQBgqg7AzMEEhMQU09GVFdBUkUtQVJDSElWTzAhBgNVHREEGjAYgRZoYWx2YXJhZG9AcGFwZWxlc2EuY29tMIIB3wYDVR0fBIIB1jCCAdIwggHOoIIByqCCAcaGgdVsZGFwOi8vYmNlcWxkYXBzdWJwMS5iY2UuZWMvY249Q1JMMTM2LGNuPUFDJTIwQkFOQ08lMjBDRU5UUkFMJTIwREVMJTIwRUNVQURPUixsPVFVSVRPLG91PUVOVElEQUQlMjBERSUyMENFUlRJRklDQUNJT04lMjBERSUyMElORk9STUFDSU9OLUVDSUJDRSxvPUJBTkNPJTIwQ0VOVFJBTCUyMERFTCUyMEVDVUFET1IsYz1FQz9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2WGNGh0dHA6Ly93d3cuZWNpLmJjZS5lYy9DUkwvZWNpX2JjZV9lY19jcmxmaWxlY29tYi5jcmykgbUwgbIxCzAJBgNVBAYTAkVDMSIwIAYDVQQKExlCQU5DTyBDRU5UUkFMIERFTCBFQ1VBRE9SMTcwNQYDVQQLEy5FTlRJREFEIERFIENFUlRJRklDQUNJT04gREUgSU5GT1JNQUNJT04tRUNJQkNFMQ4wDAYDVQQHEwVRVUlUTzElMCMGA1UEAxMcQUMgQkFOQ08gQ0VOVFJBTCBERUwgRUNVQURPUjEPMA0GA1UEAxMGQ1JMMTM2MCsGA1UdEAQkMCKADzIwMTQwNjE5MTYyMjA3WoEPMjAxNTA2MTkxNjUyMDdaMB8GA1UdIwQYMBaAFBj58PvmMhyZZjkqyouyaX1JJ7/OMB0GA1UdDgQWBBQpx6tWJQDw/SucySJl+GcAtpvjjTAJBgNVHRMEAjAAMBkGCSqGSIb2fQdBAAQMMAobBFY4LjEDAgSwMA0GCSqGSIb3DQEBCwUAA4ICAQBfrrBSFV4lKTwW5uPk2Zz6rNUMqxtugCSoOMZRTH2eljKLw8BNXq6hJoiAtG5U3gNWlRTW9lY/KIn0MtYSh8mJU51ghnyd7FTQ35eZchqUXz7chPIvDR4T4Areq3vtiocA8aTjQ1j/wrcxSOhxEg72k7nQaCla1R76ftkdjcrnHAD7T/bLpPyBPHFAF8+Trig30r7WhsndOG4XuIu6Izt/QpWgfPjbJoZddz3bAwvsJDsC3eP85QBDR27+TvFRSIesSZRkX/zYZgDCiOZSJkjseXdVnGplsSDcNifCwOby77BjaVcuBCmMaF0G1q7P6MGz/96NZvcsCGIwf/qp1+JYGQg65QYtap/gGMMkPhsocw9QbZKx0CghL3Wflqh63zZmpbBwVRFeMTkucCNTRlob40wf02p3PWGbZ5TkUWopKlJZTyo7QUU+hVNHY+/5MmV76kaO3tqhlTfXDyWu5nzJQBicXoNbwm5asp9TMY9Hyt9UJMdG6lmnozyc7b7Ot2TFXhJ034Rc8o3EyYIH6ZVyhgJ5ikMN6cThKKMz7qegEuHlP8XLOgWnSk5m5+aHOVk7O9CSPOT9d1hbg8gS5tyKIA2/Prb7OBpoalN6r5IzUayct1UrX9FJaDamNsbkbLPB/UhHcO8SzES35QgxgDwHHllrAsdIxXaN+ttSok29sA==&lt;/ds:X509Certificate&gt;&lt;/ds:X509Data&gt;&lt;ds:KeyValue&gt;&lt;ds:RSAKeyValue&gt;&lt;ds:Modulus&gt;3M+QUO/yZotkCzmkU1yVEtJJEAaidsa+G/EHX23dmBnjfyO1egOPy0QPqceVIS6NZ+WjIEem0L0wYfYan+jMYNnQPVOo7R0e6glOD+R+KmU1UGohi4z9fdwnHx+izap5jF5bQB0MHSGJPogRUN+3tBGIfm82yoZyqe+xgvNv3vlrCVXvtslEEnEiYH2LgKeZ2KuuFAGo7fTS/D8nmunPFucvgkFb1z2nRDDxqwSaVjwuO4FBu+kjwDaezcUq5WKE1o8/xrKXC2T5SA+zn6Ch9+2TLGyQLAHXNBNTOIKG0L/8RBwJmjx+zMEMM5zmBCBnHZ050gh12offmp2ELijGNw==&lt;/ds:Modulus&gt;&lt;ds:Exponent&gt;AQAB&lt;/ds:Exponent&gt;&lt;/ds:RSAKeyValue&gt;&lt;/ds:KeyValue&gt;&lt;/ds:KeyInfo&gt;&lt;ds:Object Id=&quot;Signature424041-Object650183&quot;&gt;&lt;etsi:QualifyingProperties Target=&quot;#Signature424041&quot;&gt;&lt;etsi:SignedProperties Id=&quot;Signature424041-SignedProperties532898&quot;&gt;&lt;etsi:SignedSignatureProperties&gt;&lt;etsi:SigningTime&gt;2014-09-26T09:55:54-05:00&lt;/etsi:SigningTime&gt;&lt;etsi:SigningCertificate&gt;&lt;etsi:Cert&gt;&lt;etsi:CertDigest&gt;&lt;ds:DigestMethod Algorithm=&quot;http://www.w3.org/2000/09/xmldsig#sha1&quot;&gt;&lt;/ds:DigestMethod&gt;&lt;ds:DigestValue&gt;YIUJOtMFRO+mYEREvIkbZ/Hcg3o=&lt;/ds:DigestValue&gt;&lt;/etsi:CertDigest&gt;&lt;etsi:IssuerSerial&gt;&lt;ds:X509IssuerName&gt;CN=AC BANCO CENTRAL DEL ECUADOR,L=QUITO,OU=ENTIDAD DE CERTIFICACION DE INFORMACION-ECIBCE,O=BANCO CENTRAL DEL ECUADOR,C=EC&lt;/ds:X509IssuerName&gt;&lt;ds:X509SerialNumber&gt;1313004580&lt;/ds:X509SerialNumber&gt;&lt;/etsi:IssuerSerial&gt;&lt;/etsi:Cert&gt;&lt;/etsi:SigningCertificate&gt;&lt;/etsi:SignedSignatureProperties&gt;&lt;etsi:SignedDataObjectProperties&gt;&lt;etsi:DataObjectFormat ObjectReference=&quot;#Reference-ID-334494&quot;&gt;&lt;etsi:Description&gt;contenido comprobante&lt;/etsi:Description&gt;&lt;etsi:MimeType&gt;text/xml&lt;/etsi:MimeType&gt;&lt;/etsi:DataObjectFormat&gt;&lt;/etsi:SignedDataObjectProperties&gt;&lt;/etsi:SignedProperties&gt;&lt;/etsi:QualifyingProperties&gt;&lt;/ds:Object&gt;&lt;/ds:Signature&gt;&lt;/comprobanteRetencion&gt;</comprobante></Autorizacion>";
//		System.out.println("<"+obtenerClaveAcceso(xml)+">");
//	}
	
	public static String obtenerClaveAcceso(String dato) {
		if (dato.contains("<claveAcceso>")) {
			int idxof = dato.indexOf("<claveAcceso>");
			return dato.substring(idxof + 13,idxof +  62);
		}
		if (dato.contains("&lt;claveAcceso&gt;")) {
			int idxof = dato.indexOf("&lt;claveAcceso&gt;");
			return dato.substring( idxof + 19, idxof + 68);
		}
		return "";
	}
	public static String obtenerRazonSocial(String dato){
		try {
			if (dato.contains("<razonSocial>")) {
				///return dato.substring(dato.indexOf("<razonSocial>")+13,dato.indexOf("</razonSocial>")-1);
				return dato.substring(dato.indexOf("<razonSocial>")+13,dato.indexOf("</razonSocial>"));//MBAS se cortaba el ultimo caracter
			}
			if (dato.contains("&lt;razonSocial&gt;")) {
				//return dato.substring(dato.indexOf("&lt;razonSocial&gt;")+19,dato.indexOf("&lt;/razonSocial&gt;")-1);
				return dato.substring(dato.indexOf("&lt;razonSocial&gt;")+19,dato.indexOf("&lt;/razonSocial&gt;"));//MBAS se cortaba el ultimo caracter
			}
		} catch (Exception e) {
		}
		return "";
	}
	public static String obtenerRuc(String dato){
		try {
			if (dato.contains("<ruc>")) {
				return dato.substring(dato.indexOf("<ruc>")+5,dato.indexOf("</ruc>")-1);
			}
			if (dato.contains("&lt;ruc&gt;")) {
				return dato.substring(dato.indexOf("&lt;ruc&gt;")+12,dato.indexOf("&lt;/ruc&gt;")-1);
			}
		} catch (Exception e) {
		}
		return "";
	}
	
	public static String obtenerEtiqueta(String dato,String etiqueta) {
		if (dato.contains("<"+etiqueta+">")) {		
			int idxof = dato.indexOf("<"+etiqueta+">");
			System.out.println("idxof " + idxof);
			System.out.println("etiqueta " + etiqueta);
			return dato.substring(idxof + etiqueta.length()+2,dato.indexOf("</"+etiqueta+">"));
		}
		if (dato.contains("&lt;"+etiqueta+"&gt;")) {
			int idxof = dato.indexOf("&lt;"+etiqueta+"&gt;");
			return dato.substring( idxof + etiqueta.length()+8,dato.indexOf("&lt;/"+etiqueta+"&gt;"));
		}
		return "";
	}
	
	@SuppressWarnings("unused")
	@Override
	public void guardaXmlMail(String xmlProveedor, Object compania) {
		
		if (xmlProveedor != null) {
			String xmlStringProveedor = xmlProveedor;
			String claveAcceso = obtenerClaveAcceso(xmlStringProveedor);
			if(servicioFacturaLoteMasivo.comprobanteExiste(claveAcceso)){		
			String tipoDocumento="";
			GeDatosGeneralesCompania cia = (GeDatosGeneralesCompania)compania;
			FeComprobanteProveedor comProv = new FeComprobanteProveedor();			
			String numdocumento = obtenerNumDocumento(xmlStringProveedor);
			String rucEmisor =obtenerEtiqueta(xmlStringProveedor,"ruc");
			String razonSocial=obtenerEtiqueta(xmlStringProveedor,"razonSocial");
			String valor="";
			String rucDestinatario="";
			String docAsociado="";
			if(claveAcceso.substring(8, 10).equals("01")){
				valor=obtenerEtiqueta(xmlStringProveedor,"importeTotal");
				rucDestinatario=obtenerEtiqueta(xmlStringProveedor,"identificacionComprador");
			}
			if(claveAcceso.substring(8, 10).equals("04")){
				valor=obtenerEtiqueta(xmlStringProveedor,"valorModificacion");
				docAsociado=obtenerEtiqueta(xmlStringProveedor,"numDocModificado");
				rucDestinatario=obtenerEtiqueta(xmlStringProveedor,"identificacionComprador");
			}
			if(claveAcceso.substring(8, 10).equals("05")){
				valor=obtenerEtiqueta(xmlStringProveedor,"valorTotal");
				docAsociado=obtenerEtiqueta(xmlStringProveedor,"numDocModificado");
				rucDestinatario=obtenerEtiqueta(xmlStringProveedor,"identificacionComprador");
			}
			if(claveAcceso.substring(8, 10).equals("06")||claveAcceso.substring(8, 10).equals("07")){
				valor="0";
				if(claveAcceso.substring(8, 10).equals("07")){
					docAsociado=obtenerEtiqueta(xmlStringProveedor,"numDocSustento");
					rucDestinatario=obtenerEtiqueta(xmlStringProveedor,"identificacionSujetoRetenido");
				}
				if(claveAcceso.substring(8, 10).equals("06")){					
					rucDestinatario=obtenerEtiqueta(xmlStringProveedor,"rucTransportista");
				}
			}
						
			Boolean valida = true;
			if (claveAcceso!= null ){
				if("".equals(claveAcceso)){
					valida = false;
					System.out.println("AL CLAVE DE ACCESO ESTA VACIA, EL FORMATO DE TAG NO PUDO SER LEIDO");
				}else{
					String val = servicioFacturaLoteMasivo.obtenerXmlProveedor(claveAcceso);
					if (val!= null){
						valida = false;
						System.out.println("LA CLAVE DE ACCESO "+claveAcceso+" YA SE ENCUENTRA REGISTRADA ");
					}	
				}				
				
			}else{
				
				valida = false;
				System.out.println("AL CLAVE DE ACCESO ESTA NULL, EL FORMATO DE TAG NO PUDO SER LEIDO");
			}
			if (valida){
				
				docAsociado=docAsociado.replaceAll("-", "");
				tipoDocumento = claveAcceso.substring(8, 10);
				comProv.setEstado("P");
				comProv.setObservacion("COMPROBANTE RECIBIDO, PENDIENTE DE VALIDACION");
				comProv.setCompania(StringUtils.leftPad(String.valueOf(cia.getId()), 2, "0"));
				
				//System.err.println("1044");
				comProv.setNumComprobante(numdocumento);
				comProv.setClaveAcceso(claveAcceso);
				comProv.setNumAutorizacion(claveAcceso);
				comProv.setRazonSocial(razonSocial);
				comProv.setXmlProveedor(xmlStringProveedor);
				comProv.setFechaRegistro(new Date());
				comProv.setTipo(tipoDocumento);
				comProv.setCompania(String.valueOf(cia.getId()));
				comProv.setRucDestinatario(rucDestinatario);
				comProv.setRucEmisor(rucEmisor);
				comProv.setIdSuscriptor(cia.getIdSuscriptor());
				//comProv.setValor(valor);
				
				valor=valor==null?"0.0":valor;
				valor=valor.equals("")?"0.0":valor;
				comProv.setTotalDocumento(Double.parseDouble(valor));
				comProv.setDocAsociado(docAsociado);
				comProv.setOrigen("MAIL");
				String fechaE =claveAcceso.substring(0,2)+"/"+claveAcceso.substring(2,4)+"/"+claveAcceso.substring(4,8);
				DateFormat formatE = new SimpleDateFormat("dd/MM/yyyy");
				Date dateE = null;				
				try {
					dateE = formatE.parse(fechaE);
					
				} catch (ParseException e) {
					e.printStackTrace();
				}	
				comProv.setFechaEmision(dateE);
				//System.err.println("1056:guardar:" + comProv.getClaveAcceso());
			    servicioFacturaLoteMasivo.guardaComprobanteProveedor(comProv);		
			}
		}else{
			servicioFacturaLoteMasivo.actualizarOrigen(claveAcceso);
		}
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void validaXmlMail(Object compania) {
		GeDatosGeneralesCompania cia = (GeDatosGeneralesCompania) compania;	
		List<FeComprobanteProveedor> listaPendientes = servicioFacturaLoteMasivo
				.consultaComprobantesProveedorPorEstado("P", cia.getId());
		RespuestaComprobante respuestaComprobante = null;
		String numerComprobanteProv = "";
		String rucEmisorProv = "";
		String razonSocialProv = "";
		String newEstadoProv = "";
		String observacion = "";
		String xmlAutorizado = "";
		String numeroAutorizacion ="";
		String rucDestinatario = "";
		Date fechaAutorizacion = null;
		Integer horasSumar = new Integer(servicioParametros.getValorParametro(INombresParametros.HORAS_SUMAR_VALIDACION_COMPROBANTE_RECEPCION));
		System.out.println("Existen "+listaPendientes.size() + " pendientes de validar para la companida "+cia.getId());
		for (FeComprobanteProveedor feComprobanteProveedor : listaPendientes) {
			System.out.println("Se va ha procesar el documento : "+feComprobanteProveedor.getNumComprobante());
			String claveAcceso = feComprobanteProveedor.getClaveAcceso();
			String numdocumento =feComprobanteProveedor.getNumComprobante();
			String tipoDocumento = feComprobanteProveedor.getTipo();			
			Date fechaActual= new Date();
			Date fechaActualizarRegistro=sumarRestarHorasFecha(feComprobanteProveedor.getFechaRegistro(),horasSumar);			
			boolean actualizar=false;
			try {
				System.out.println("Se validara disponibilidad del webservices del sri ["+URLWSSRI+"]");
				if (ServicioComprobacionWS
						.servicioAutorizacionIndividual(URLWSSRI)) {
					// System.err.println("956");
					// System.err.println("El web service esta activo ");

					respuestaComprobante = AutorizacionComprobante
							.consultaAutorizacionProveedor(claveAcceso, "",
									RespuestaComprobante.class, URLWSSRI);

					if (new Integer(
							respuestaComprobante.getNumeroComprobantes()) > 0) {
						// System.err.println("956");

						Object respuestaWebService[] = determinaComprobanteAutorizado(respuestaComprobante);
						xmlAutorizado = (String) respuestaWebService[0];
						numeroAutorizacion = (String) respuestaWebService[1];
						// System.err.println("972");
						
						if (xmlAutorizado != null) {
							switch (tipoDocumento) {
							case ConstanteInfoTributaria.TIPO_COMPROBANTE_FACTURA:
								Factura fac = convierteDocumentAClaseFactura(crearXMLComprobanteRetencion(xmlAutorizado));
								numerComprobanteProv = fac.getInfoTributaria()
										.getEstab()
										+ "-"
										+ fac.getInfoTributaria().getPtoEmi()
										+ "-"
										+ fac.getInfoTributaria()
												.getSecuencial();
								rucDestinatario = fac.getInfoFactura()
										.getIdentificacionComprador();
								rucEmisorProv = fac.getInfoTributaria()
										.getRuc();
								razonSocialProv = fac.getInfoTributaria()
										.getRazonSocial();

								break;
							case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_CREDITO:
								NotaCredito notaCredito = convierteDocumentAClaseNotaCredito(crearXMLComprobanteRetencion(xmlAutorizado));
								numerComprobanteProv = notaCredito
										.getInfoTributaria().getEstab()
										+ "-"
										+ notaCredito.getInfoTributaria()
												.getPtoEmi()
										+ "-"
										+ notaCredito.getInfoTributaria()
												.getSecuencial();
								rucDestinatario = notaCredito
										.getInfoNotaCredito()
										.getIdentificacionComprador();
								rucEmisorProv = notaCredito.getInfoTributaria()
										.getRuc();
								razonSocialProv = notaCredito
										.getInfoTributaria().getRazonSocial();
								break;
							case ConstanteInfoTributaria.TIPO_COMPROBANTE_NOTA_DEBITO:
								NotaDebito notaDebito = convierteDocumentAClaseNotaDebito(crearXMLComprobanteRetencion(xmlAutorizado));
								numerComprobanteProv = notaDebito
										.getInfoTributaria().getEstab()
										+ "-"
										+ notaDebito.getInfoTributaria()
												.getPtoEmi()
										+ "-"
										+ notaDebito.getInfoTributaria()
												.getSecuencial();
								rucDestinatario = notaDebito
										.getInfoNotaDebito()
										.getIdentificacionComprador();
								rucEmisorProv = notaDebito.getInfoTributaria()
										.getRuc();
								razonSocialProv = notaDebito
										.getInfoTributaria().getRazonSocial();
								break;
							case ConstanteInfoTributaria.TIPO_COMPROBANTE_GUIA_REMISION:
								GuiaRemision guiaRemi = convierteDocumentAClaseGuiaRemision(crearXMLComprobanteRetencion(xmlAutorizado));
								numerComprobanteProv = guiaRemi
										.getInfoTributaria().getEstab()
										+ "-"
										+ guiaRemi.getInfoTributaria()
												.getPtoEmi()
										+ "-"
										+ guiaRemi.getInfoTributaria()
												.getSecuencial();
								rucDestinatario = guiaRemi.getDestinatarios()
										.getDestinatario().get(0)
										.getIdentificacionDestinatario();
								rucEmisorProv = guiaRemi.getInfoTributaria()
										.getRuc();
								razonSocialProv = guiaRemi.getInfoTributaria()
										.getRazonSocial();
								break;
							case ConstanteInfoTributaria.TIPO_COMPROBANTE_RETENCION:
								ComprobanteRetencion rentencion = convierteDocumentARetencion(crearXMLComprobanteRetencion(xmlAutorizado));
								numerComprobanteProv = rentencion
										.getInfoTributaria().getEstab()
										+ "-"
										+ rentencion.getInfoTributaria()
												.getPtoEmi()
										+ "-"
										+ rentencion.getInfoTributaria()
												.getSecuencial();
								rucDestinatario = rentencion
										.getInfoCompRetencion()
										.getIdentificacionSujetoRetenido();
								rucEmisorProv = rentencion.getInfoTributaria()
										.getRuc();
								razonSocialProv = rentencion
										.getInfoTributaria().getRazonSocial();
								break;
							default:
								break;
							}							
							if (cia.getIdentificacion().equals(rucDestinatario)) {
								newEstadoProv = "V";
								observacion = "COMPROBANTE VALIDADO EXITOSAMENTE";
								actualizar=true;

							} else {
								newEstadoProv = "E";
								observacion = "RUC DESTINATARIO NO COINCIDE CON LA EMPRESA";
								actualizar=true;
							}
							// System.err.println("1044");
							fechaAutorizacion = (Date) respuestaWebService[2];

						} else {
							newEstadoProv = "E";
							observacion = "EL COMPROBANTE NO SE ENCUENTRA AUTORIZADO EN EL SRI";
						}
					} else {
						// System.err.println("1049");
						if (claveAcceso != null && claveAcceso.length() > 10) {
							tipoDocumento = claveAcceso.substring(8, 10);
						}
						newEstadoProv = "E";
						observacion = "COMPROBANTE NO EXISTE EN EL SRI";
					}
					System.out.println("Se busca comprobante por clave de acceso :"+claveAcceso);
					FeComprobanteProveedor cm = servicioFacturaLoteMasivo.obtenerComprobanteProveedor(claveAcceso);
					if (cm != null){
						System.out.println("OBJETO NO ES NULL");
					}else{
						System.out.println("OBJETO ES UN NULL");
					}
					
					if(fechaActual.compareTo(fechaActualizarRegistro)>0||fechaActual.compareTo(fechaActualizarRegistro)==0){
						actualizar=true;
					}
						
					if(actualizar){
						cm.setObservacion(observacion);
						cm.setEstado(newEstadoProv);
						cm.setXmlSRI(xmlAutorizado);
						cm.setFechaAutorizacion(fechaAutorizacion);
						cm.setFechaActualizacion(new Date());
								
						servicioFacturaLoteMasivo.actualizaComprobanteProveedor(cm);
					}
				} else {
					System.err.println("NO HAY WEB SERVICE");
					return;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR AL PROCESAR XML DEL COMPROBANTE : "
						+ claveAcceso);
				e.printStackTrace();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR AL PROCESAR XML DEL COMPROBANTE : "
						+ claveAcceso);
				e.printStackTrace();
			}
		}
	}
	
	public Date sumarRestarHorasFecha(Date fecha, int horas){
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTime(fecha); // Configuramos la fecha que se recibe
	      calendar.add(Calendar.HOUR, horas);  // numero de horas a añadir, o restar en caso de horas<0
	      return calendar.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas
	 }
	
	public void procesarArchivoDirectorio(){
		String rutaMoverError="";
		File archivoError = null;
		try{			
			String directorio = servicioParametros.getValorParametro("RUTA_PROCESA_ARCHIVOS_AUT_ROBOT");			
			int cantidadTomar= 100;
			try{
				cantidadTomar=Integer.parseInt(servicioParametros.getValorParametro("CANTIDAD_ROBOT"));				
			}catch(Exception e){
				cantidadTomar = 100;
				System.out.println("valor a sumar");
			}
			File directorioXml = new File(directorio);
			File[] arrayFile = directorioXml.listFiles();	
			rutaMoverError=directorioXml.getAbsolutePath()+"/ERROR";
			int contador= 0;
			for (File zip : arrayFile) {
						if(zip.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_ZIP)){
							archivoError = zip;
							unzip(zip.getAbsolutePath(), directorio);
							zip.delete();
						}
						//procesar los archivos xml los listos 
						File[] arrayFileXml = directorioXml.listFiles();					
						for (File xml : arrayFileXml) {
							if(xml.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_XML)){
								 archivoError=xml;
								 Path wiki_path = Paths.get(directorioXml.getAbsolutePath(), xml.getName());						
								  byte[] wikiArray = Files.readAllBytes(wiki_path);
							      String wikiString = new String(wikiArray, "UTF-8");
							      String claveAcceso = obtenerClaveAcceso(wikiString);	
							      String newFile=xml.getName().toLowerCase();
							      newFile = newFile.replace(INombresParametros.EXTENCION_ARCHIVOS_XML, "");
							      newFile = newFile+"_"+claveAcceso+INombresParametros.EXTENCION_ARCHIVOS_XML;		
							      File nuevoArchivo =new File(directorioXml.getAbsolutePath()+"/"+newFile);
							      boolean siRenombro = false;
							      String  rutaMover=directorioXml.getAbsolutePath()+"/PROCESADO/";
							      if(xml.renameTo(nuevoArchivo)){
							    	  siRenombro= true;	
							    	  archivoError=nuevoArchivo;
								  }else{
										siRenombro=false;
										archivoError=xml;
								  }
							      try{
							    	 int resultado= guardaXmlMail(wikiString );							    	
							         if(resultado == 1){
							    	  if(siRenombro){
							    		  mueveArchivoProcesado(nuevoArchivo,rutaMover);
							    	  }else{
							    		  mueveArchivoProcesado(xml,rutaMover);
							    	  }
							         }else{
							        	 if(siRenombro){
								    		  mueveArchivoProcesado(nuevoArchivo,directorioXml.getAbsolutePath()+"/EXISTE/");
								    	  }else{
								    		  mueveArchivoProcesado(xml,directorioXml.getAbsolutePath()+"/EXISTE/");
								    	  }
							         }
							      }catch(Exception e ){
							    	  System.out.println("ruta mover error "+ rutaMoverError);
							    	  if(siRenombro){
							    		  mueveArchivoProcesado(nuevoArchivo,rutaMoverError);
							    		  System.out.println("ruta mover error "+ nuevoArchivo.getAbsolutePath());
							    	  }else{
							    		  System.out.println("ruta mover error "+ nuevoArchivo.getAbsolutePath());
							    		  mueveArchivoProcesado(xml,rutaMoverError);
							    	  }
							    	  System.out.println("ocurrio un error " );
							    	  e.printStackTrace();
							      }
							}
					}
					contador=contador+1;
	
				}
				
		}catch(Exception e ){			
			System.out.println("se mueve el archivo a una ruta de errror");
			  mueveArchivoProcesado(archivoError,rutaMoverError);
			System.out.println("ocurrio un error durante la carga de recepcion");
			e.printStackTrace();
		}
			
		
	}
	
	
	//JCA ROBOT		
	public List<String> leerAchivosDirectorio(){
		System.err.println("entro al metodo para leer los archivos descargados por robot");
		List<String> is = new ArrayList<String>();
		try{
			String directorio = servicioParametros.getValorParametro("RUTA_PROCESA_ARCHIVOS_AUT_ROBOT");
			File directorioXml = new File(directorio);
			File[] arrayFile = directorioXml.listFiles();
			System.err.println("cantidas de archivos zip es "+arrayFile.length);
			//descomprimir aarchivos pueden venir varios companias en uno
			for (File xml : arrayFile) {
				if(xml.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_ZIP)){
					try {
						unzip(xml.getAbsolutePath(), directorio);
						xml.delete();
					} catch (IOException e) {		
							e.printStackTrace();
					}
				}
			}
			//todos van hacer xml obtengo e l ruc del xml y veo a que carpeta mover
			for (File xml : arrayFile) {
				if(xml.getName().toLowerCase().endsWith(INombresParametros.EXTENCION_ARCHIVOS_XML)){
					try {
						  Path wiki_path = Paths.get(directorioXml.getAbsolutePath(), xml.getName());						
						  byte[] wikiArray = Files.readAllBytes(wiki_path);
					      String wikiString = new String(wikiArray, "UTF-8");
					      is.add(wikiString);
					      //renombrar archivo
					  	  String claveAcceso = obtenerClaveAcceso(wikiString);					  	  
					  	  String ruc = "" ;
						  	if(claveAcceso.substring(8, 10).equals("01")){								
						  		ruc=obtenerTagXml(wikiString,"identificacionComprador");
							}
							if(claveAcceso.substring(8, 10).equals("04")){									
								ruc=obtenerTagXml(wikiString,"identificacionComprador");
							}
							if(claveAcceso.substring(8, 10).equals("05")){									
								ruc=obtenerTagXml(wikiString,"identificacionComprador");
							}
							if(claveAcceso.substring(8, 10).equals("06")||claveAcceso.substring(8, 10).equals("07")){								
								if(claveAcceso.substring(8, 10).equals("07")){						
									ruc=obtenerTagXml(wikiString,"identificacionSujetoRetenido");
								}
								if(claveAcceso.substring(8, 10).equals("06")){					
									ruc=obtenerTagXml(wikiString,"rucTransportista");
								}
							}					  	
					  	   
					  	  
					  	  GeDatosGeneralesCompania compania = servicioParametros.obtenerCompaniaPorRUC(ruc);
					  	  String rutaMover = "";					  	  
					  	  if(compania!=null && compania.getRutaRaizArchivoFirmado()!=null &&  !(compania.getRutaRaizArchivoFirmado().equals("")))
					  		  rutaMover= compania.getRutaRaizArchivoFirmado();

						  if("".equals(rutaMover))
							  rutaMover=directorioXml.getAbsolutePath()+"/PROCESADO/";
						  System.out.println("el archivo a moVER RUTA "+ xml.getName() + " "+ rutaMover);
					  		mueveArchivoProcesado(xml,rutaMover);
					  	 // }
					} catch (Exception e) {
						  mueveArchivoProcesado(xml,directorioXml.getAbsolutePath()+"/ERROR");
						  e.printStackTrace();
					}
				   }
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}
		
	public boolean renameFile(File oldFile,File newFile){	
		boolean status = oldFile.renameTo(newFile);   
        return status;
	}
	
	public void mueveArchivoProcesado(File archivoTransferir, String rutaMueve) {
		File directorioXml = new File(rutaMueve);
		if(!directorioXml.exists())
			directorioXml.mkdirs();
		fileCopy(archivoTransferir.getAbsoluteFile().toString(), rutaMueve
				+ "/" + archivoTransferir.getName());
		archivoTransferir.delete();

	}
	
	public void fileCopy(String sourceFile, String destinationFile) {
		try {
			Path FROM = Paths.get(sourceFile);
			Path TO = Paths.get(destinationFile);
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES };
			Files.copy(FROM, TO, options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public int guardaXmlMail(String xmlProveedor) throws Exception{
			int devolver = 0;
			if (xmlProveedor != null) {
				String claveAcceso = obtenerClaveAcceso(xmlProveedor);			
				if(servicioFacturaLoteMasivo.comprobanteExiste(claveAcceso)){
				String tipoDocumento="";		
				FeComprobanteProveedor comProv = new FeComprobanteProveedor();
				String xmlStringProveedor = xmlProveedor;
				String rucDestinatario="";
				String numdocumento = obtenerNumDocumento(xmlStringProveedor);
				String ruc =obtenerTagXml(xmlProveedor,"ruc");
				String fecha=obtenerTagXml(xmlProveedor,"fechaAutorizacion");
				String numeroAutorizacion=obtenerTagXml(xmlProveedor,"numeroAutorizacion");			
				
				Boolean valida = true;
				if (claveAcceso!= null ){
					if("".equals(claveAcceso)){
						valida = false;
						System.err.println("LA CLAVE DE ACCESO ESTA VACIA, EL FORMATO DE TAG NO PUDO SER LEIDO");
					}else{
						String val = servicioFacturaLoteMasivo.obtenerXmlProveedor(claveAcceso);
						if (val!= null){
							valida = false;
							System.err.println("LA CLAVE DE ACCESO "+claveAcceso+" YA SE ENCUENTRA REGISTRADA ");
						}	
					}				
					
				}else{
					valida = false;
					System.err.println("AL CLAVE DE ACCESO ESTA NULL, EL FORMATO DE TAG NO PUDO SER LEIDO");
				}
				
				String valor="";
				if(claveAcceso.substring(8, 10).equals("01")){
					valor=obtenerTagXml(xmlProveedor,"importeTotal");
					rucDestinatario=obtenerTagXml(xmlStringProveedor,"identificacionComprador");
				}
				if(claveAcceso.substring(8, 10).equals("04")){
					valor=obtenerTagXml(xmlProveedor,"valorModificacion");	
					rucDestinatario=obtenerTagXml(xmlStringProveedor,"identificacionComprador");
				}
				if(claveAcceso.substring(8, 10).equals("05")){
					valor=obtenerTagXml(xmlProveedor,"valorTotal");	
					rucDestinatario=obtenerTagXml(xmlStringProveedor,"identificacionComprador");
				}
				if(claveAcceso.substring(8, 10).equals("06")||claveAcceso.substring(8, 10).equals("07")){
					valor="0";	
					if(claveAcceso.substring(8, 10).equals("07")){						
						rucDestinatario=obtenerTagXml(xmlStringProveedor,"identificacionSujetoRetenido");
					}
					if(claveAcceso.substring(8, 10).equals("06")){					
						rucDestinatario=obtenerTagXml(xmlStringProveedor,"rucTransportista");
					}
				}
				GeDatosGeneralesCompania empresa = servicioParametros.obtenerCompaniaPorRUC(rucDestinatario);
				System.out.println("robot - la empresa es"+ empresa.getRazonSocial());
				if(empresa ==null ||empresa.getId()==null){
					valida=false;
					System.out.println("el ruc destinatario no forma parte de las empresas configuradas "+ ruc);
				}
				if (valida){
					String xmlBase = xmlProveedor;
					System.out.println();
					if (xmlBase.contains("<![CDATA[")) {
						xmlBase = xmlBase.substring(xmlBase.indexOf("<![CDATA[") + 9);
						xmlBase = xmlBase.substring(0, xmlBase.indexOf("]]>"));
					}
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
					Date date = null;				
					try {
						date = format.parse(fecha);
						System.out.println(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}	
					
					tipoDocumento = claveAcceso.substring(8, 10);
					comProv.setEstado("V");
					comProv.setObservacion("COMPROBANTE RECIBIDO");
					comProv.setNumComprobante(numdocumento);
					comProv.setClaveAcceso(claveAcceso);
					comProv.setXmlSRI(xmlBase);
					comProv.setXmlProveedor(xmlStringProveedor);
					comProv.setFechaRegistro(new Date());
					comProv.setTipo(tipoDocumento);
					comProv.setNumAutorizacion(numeroAutorizacion);
					comProv.setRucEmisor(obtenerTagXml(xmlProveedor,"ruc"));
					comProv.setRazonSocial(obtenerRazonSocial(xmlProveedor));

					valor=valor==null?"0.0":valor;
					valor=valor.equals("")?"0.0":valor;
					comProv.setTotalDocumento(Double.parseDouble(valor));
					comProv.setRucDestinatario(empresa.getIdentificacion());
					
					try{
						comProv.setFechaAutorizacion(date);
					}catch(Exception e){}
					String fechaE =claveAcceso.substring(0,2)+"/"+claveAcceso.substring(2,4)+"/"+claveAcceso.substring(4,8);
					DateFormat formatE = new SimpleDateFormat("dd/MM/yyyy");
					Date dateE = null;				
					try {
						dateE = formatE.parse(fechaE);
					} catch (ParseException e) {
						e.printStackTrace();
					}	
					comProv.setFechaEmision(dateE);
					comProv.setCompania(String.valueOf(empresa.getId()));
					comProv.setIdSuscriptor(empresa.getIdSuscriptor());
					comProv.setOrigen("SRI");				
					servicioFacturaLoteMasivo.guardaComprobanteProveedor(comProv);
					devolver =1;
				}
			}
		 }
			return devolver;
			
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
		public void unzip(String zipFilePath, String destDirectory) throws IOException {
	        File destDir = new File(destDirectory);
	        if (!destDir.exists()) {
	            destDir.mkdir();
	        }
	        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
	        ZipEntry entry = zipIn.getNextEntry();
	        // iterates over entries in the zip file
	        while (entry != null) {
	            String filePath = destDirectory + File.separator + entry.getName();
	            if (!entry.isDirectory()) {
	                // if the entry is a file, extracts it
	                extractFile(zipIn, filePath);
	            } else {
	                // if the entry is a directory, make the directory
	                File dir = new File(filePath);
	                dir.mkdir();
	            }
	            zipIn.closeEntry();
	            entry = zipIn.getNextEntry();
	        }
	        zipIn.close();
	    }
	    /**
	     * Extracts a zip entry (file entry)
	     * @param zipIn
	     * @param filePath
	     * @throws IOException
	     */
	 private static final int BUFFER_SIZE = 4096;
	    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
	        byte[] bytesIn = new byte[BUFFER_SIZE];
	        int read = 0;
	        while ((read = zipIn.read(bytesIn)) != -1) {
	            bos.write(bytesIn, 0, read);
	        }
	        bos.close();
	    }
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)    
	public void procesarArchivo(){
		String directorio = servicioParametros.getValorParametro("RUTA_PROCESA_ARCHIVOS_AUT_ROBOT");	
		File directorioXml = new File(directorio);
		File[] arrayFile = directorioXml.listFiles();	
		for (File xml : arrayFile) {
			if(xml.getName().toLowerCase().endsWith(".txt")){			
			//	insertarDatosAProcesar(mapInsertar(xml,directorioXml.getAbsolutePath().toString()));
				//mapInsertar(xml,directorioXml.getAbsolutePath().toString());
				mapInsertarV2(xml,directorioXml.getAbsolutePath().toString());
			}
		}	
	}
	
	
	//bsegovia
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean mapInsertarV2(File archivo, String directorioXml){
		HashMap<String, String[]> listProcesar = new HashMap<>();
		List<HashMap<String,String[]>> listProcesarT = new ArrayList();
		String trama="";
		String delimitador = "\t";
		BufferedReader bf = null;
		int contador=0;
		Integer lSuscriptor=0;
		String  lTipoDocumento="0";
		try{
			 bf = new BufferedReader(new FileReader(archivo.getAbsolutePath()));				
			while ((trama = bf.readLine())!=null) {
				String[] temp;
				
				temp = trama.split(delimitador);
				String claveAcceso ="";
				String numeroComprobante="";
				// mbas ini 13012017
				String rucDestinatario="";
				String [] Datos=new String [10];
				String rucEmisor="";
				String razonSocialEmisor="";
				String tipoComprobante="";
				String fechaEmision="";
				String fechaAutorizacion="";
				String numeroAutorizacion="";
				
				for(int i=0; i<temp.length; i++){						
					if(i==1){						
						numeroComprobante=temp[i].replace(" ", "");	
					}
					
				
					if(i==2){						
						rucEmisor=temp[i].replace(" ", "");	
					}
					
					if(i==3){						
						razonSocialEmisor=temp[i].replace(" ", "");	
					}
					
					
					if(i==4){						
						fechaEmision=temp[i].replace(" ", "");	
				       }
					
					if(i==5){						
						fechaAutorizacion=temp[i].replace(" ", "");	
				       }
					
					if(i==8){						
					    rucDestinatario=temp[i].replace(" ", "");	
				       }
					
					
					if(i==9 && temp[i].replace(" ", "").length()==49){		
						
						claveAcceso=temp[i].replace(" ", "");	
     					tipoComprobante=obtenerDatosClaveAcceso(claveAcceso,8,10);
					}	
					
					if(i==10){						
						numeroAutorizacion=temp[i].replace(" ", "");	
				       }						
				}	
				/// MBAS INI
				
				if(contador<=5000){
					if(!claveAcceso.equals("")&& !numeroComprobante.equals("")){
						/// INI MBAS 13012017
					Datos[0]=numeroComprobante;
					Datos[1]=rucDestinatario;
					Datos[2]=rucEmisor;
					Datos[3]=razonSocialEmisor.replace("'", "");
					Datos[4]=tipoComprobante;
					Datos[5]=fechaEmision;
					Datos[6]=fechaAutorizacion;
					Datos[7]=numeroAutorizacion;
					/// FIN MBAS 13012017
					listProcesar.put(claveAcceso, Datos);	
					
					}
					contador++;
				}else{
					contador=0;
					listProcesarT.add(listProcesar);
					listProcesar = new HashMap<>();
				}
			}	
			
			if(contador!=0){
			listProcesarT.add(listProcesar);
			}
		
			
			bf.close();
			mueveArchivoProcesado(archivo,directorioXml+"/PROCESADO/");
			/// MBASTIDAS INI
			try{
				
				System.out.print("Cantidad de Lotes a Procesar "+listProcesarT.size()+" Cada uno de 5000 registros.");
				 int lVueltas=0;

				 StringBuffer lSentenciasTodas= new StringBuffer();
				 String lSentenciaMolde =  ServicioPropertie.getPropiedad("insertBatchProveedores");
				 
				 StringBuffer lSentenciasTodasTmp= new StringBuffer();
				 String lSentenciaMoldeTMP = ServicioPropertie.getPropiedad("insertBatchProveedoresTMP");
				 String[] datosComprobantes= new String[10];
				 String lSenteciaLotetmp= new String();
				 
				 System.out.println("Sentencia molde aplicar "+lSentenciaMolde);
				for( HashMap<String, String[]> loteMap: listProcesarT ){
					System.out.println(" *********Lote a procesar de 5000 registros que se esta procesando "+lVueltas+" *************");
			
				 Iterator<Entry<String, String[]>> it = loteMap.entrySet().iterator();
				 String lSenteciaLote= new String();
			     while (it.hasNext()) {
				     Map.Entry e = (Map.Entry)it.next();
				     datosComprobantes=(String[]) e.getValue();
				     
				     if(lSuscriptor== 0){
				    	    lTipoDocumento=datosComprobantes[4];
		          			lSuscriptor=servicioFacturaLoteMasivo.extraerSuscriptor(datosComprobantes[1])[0];
		          		    System.out.println("***PROCESANDO ANULADO: SUSCRIPTOR "+lSuscriptor+" ");
		          		    System.out.println("***PROCESANDO ANULADO: TIPO DOCUMENTO "+lTipoDocumento+" ");
		          		 ///  Eliminar los datos temporales
				          	  try{   
				          		  System.out.println(" *********PROCESANDO ANULADOS ANTES DE ELIMINAR LOS TEMPORALES *************");
				          		  servicioFacturaLoteMasivo.eliminarTMP(lSuscriptor);
				          		  
				          		  System.out.println(" *********PROCESANDO ANULADOS DESPUES DE ELIMINAR LOS TEMPORALES *************");
				          	   }catch(Exception lErrorTemporales){
				          		   System.out.println(" *********ERROR ELIMINAR TEMPORALES *************");
				          		   lErrorTemporales.printStackTrace();
				          	   }
		          		}
				     
				     ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
				        if(servicioFacturaLoteMasivo.comprobanteExiste(e.getKey().toString())){
				           	try{
				       
				          		
				          		lSenteciaLote=lSentenciaMolde;
				          		lSenteciaLote=lSenteciaLote.replace("@CLAVE_ACCESO@", e.getKey().toString());
				          	
				          		/// INI MBAS 13012017
				          		lSenteciaLote=lSenteciaLote.replace("@NUMERO_COMPROBANTE@", datosComprobantes[0]);
				          		lSenteciaLote=lSenteciaLote.replace("@RUC_DESTINATARIO@",datosComprobantes[1]);
				          		lSenteciaLote=lSenteciaLote.replace("@RUC_EMISOR@", datosComprobantes[2]);
				          		lSenteciaLote=lSenteciaLote.replace("@RAZON_SOCIAL@", datosComprobantes[3]);
				          		lSenteciaLote=lSenteciaLote.replace("@TIPO_COMPROBANTE@", datosComprobantes[4]);
				          		lSenteciaLote=lSenteciaLote.replace("@FECHA_EMISION@",  datosComprobantes[5]);
				          		lSenteciaLote=lSenteciaLote.replace("@FECHA_AUTORIZACION@",  formatoFechaAutorizacion(datosComprobantes[6]));
				          		lSenteciaLote=lSenteciaLote.replace("@ID_SUSCRIPTOR@", servicioFacturaLoteMasivo.extraerSuscriptor(datosComprobantes[1])[0].toString());
				          		lSenteciaLote=lSenteciaLote.replace("@COMPANIA@", servicioFacturaLoteMasivo.extraerSuscriptor(datosComprobantes[1])[1].toString());
				          		lSenteciaLote=lSenteciaLote.replace("@NUM_AUTORIZACION@",  datosComprobantes[7]);
				          		lSentenciasTodas.append(lSenteciaLote);
					
				        	}catch(Exception lError){
					    		System.out.println(" Error al insertar FE_COMPROBANTES_PROVEEDOR la clave de acceso  :"+ e.getKey().toString());
					    		lError.printStackTrace();
					    	}
							
						}else{
							System.out.println("la clave de acceso ya se encuentra registrada FE_COMPROBANTES_PROVEEDOR"+ e.getKey().toString());
						}
				     
				        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
				        
				       	if(servicioFacturaLoteMasivo.comprobanteExisteTMP(e.getKey().toString())){
				       		//Si no existe en la TMP entonces la crea
				           	try{
				      			        		          		
				          		lSenteciaLotetmp=lSentenciaMoldeTMP;
					       		lSenteciaLotetmp=lSenteciaLotetmp.replace("@CLAVE_ACCESO@", e.getKey().toString());
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@NUMERO_COMPROBANTE@", datosComprobantes[0]);
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@RUC_DESTINATARIO@",datosComprobantes[1]);
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@RUC_EMISOR@", datosComprobantes[2]);
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@RAZON_SOCIAL@", datosComprobantes[3]);
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@TIPO_COMPROBANTE@", datosComprobantes[4]);
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@FECHA_EMISION@",  datosComprobantes[5]);
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@FECHA_AUTORIZACION@",  formatoFechaAutorizacion(datosComprobantes[6]));
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@ID_SUSCRIPTOR@", servicioFacturaLoteMasivo.extraerSuscriptor(datosComprobantes[1])[0].toString());
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@COMPANIA@", servicioFacturaLoteMasivo.extraerSuscriptor(datosComprobantes[1])[1].toString());
				          		lSenteciaLotetmp=lSenteciaLotetmp.replace("@NUM_AUTORIZACION@",  datosComprobantes[7]);
				          		lSentenciasTodasTmp.append(lSenteciaLotetmp);
				          			       
				        	}catch(Exception lError){
				      
					    		System.out.println(" Error al insertar FE_COMPROBANTES_PROVEEDOR_TMP la clave de acceso en  :"+ e.getKey().toString());
					    		lError.printStackTrace();
					    	}
			           	
						
						}else{
							System.out.println("la clave de acceso ya se encuentra registrada FE_COMPROBANTES_PROVEEDOR_TMP"+ e.getKey().toString());
						}
				        
				        
				        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				    }
			        
			        
			        System.out.println(lSentenciasTodas.toString());
			        
			        registroComprobantesBatch(lSentenciasTodas, lSentenciasTodasTmp);
					
				    System.out.println(" *********Lote a Aplicado "+lVueltas+" *************");

				    lSentenciasTodas= new StringBuffer();
				    lSentenciasTodasTmp=new StringBuffer();
				    lVueltas=lVueltas+1;
				   }
				
				System.out.println(" *********Total de Lotes aplicados "+lVueltas+1+" *************");
				
			
				
			/// MBASTIDAS FIN
					
			}catch(Exception lError){
				
				lError.printStackTrace();
			}
			
			
			
			
		}catch(Exception e){
			if(bf!=null)
				try {
					bf.close();
				} catch (IOException e1) {				
					e1.printStackTrace();
				}
			mueveArchivoProcesado(archivo,directorioXml+"/ERROR/");
			e.printStackTrace();
		}
		//MBASTIDAS
		  try{
        	  System.out.println(" *********INICIO PROCESANDO ANULADOS*************");
        	  Date[] lFechasParametro=    servicioFacturaLoteMasivo.extraeFechasMaximaMinima(lSuscriptor);
        	  System.out.println(" *********PROCESANDO ANULADOS SUSCRIPTOR *************"+lSuscriptor);
        	  System.out.println(" *********PROCESANDO ANULADOS FECHA MINIMA *************"+lFechasParametro[0]);
        	  System.out.println(" *********PROCESANDO ANULADOS FECHA MAXIMA *************"+lFechasParametro[1]);
        	  List<String> lRucDestinatarios=  servicioFacturaLoteMasivo.extraeDestinatariosXSuscriptor(lSuscriptor,lFechasParametro[0],lFechasParametro[1]);
        	  System.out.println(" *********PROCESANDO ANULADOS CANTIDAD DE RUC *************"+lRucDestinatarios.size());
        	  for(String rucDestinatario: lRucDestinatarios){
        		  try{        			  
        			  System.out.println(" *********PROCESANDO ANULADOS MARCA EL RUC *************"+rucDestinatario);
        			  Integer lTolerancia = new Integer(servicioParametros.getValorParametro(INombresParametros.TOLERANCIA_PORCENTAJE_ANULADOS));
        			  System.out.println(" *********PROCESANDO ANULADOS TOLERANCIA CONFIGURADA *************"+lTolerancia); 
        			  servicioFacturaLoteMasivo.marcaAnuladosComparacionRegistros(lSuscriptor, rucDestinatario, lFechasParametro[0],
        					  lFechasParametro[1], lTolerancia,lTipoDocumento);
        			  System.out.println(" *********PROCESANDO ANULADOS DESPUES DE MARCAR COMPARACION DE ARCHIVOS *************"+rucDestinatario);
        			  }
        		  catch(Exception lErrorDestinatario){
        			  lErrorDestinatario.printStackTrace();
        		  }
        		 }
        	  ///anulados con mas de 3 dias sin tener respuesta del SRI
        	  try{  
        		  System.out.println(" *********PROCESANDO ANULADOS ANTES DE MARCAR LOS REZAGADOS *************");
        		  servicioFacturaLoteMasivo.anuladosRezagados();
        		  System.out.println(" *********PROCESANDO ANULADOS DESPUES DE MARCAR LOS REZAGADOS *************");
        	  }catch(Exception lErrorResagados){
        		  System.out.println(" *********ERROR REZAGADOS *************");
        		  lErrorResagados.printStackTrace();
        	   }
        	 
        	  System.out.println(" *********FIN DE PROCESANDO ANULADOS*************");
        	  
             }catch(Exception lError){
        	 lError.printStackTrace();
          }

		
		return true;
		//return listProcesar;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void registroComprobantesBatch(StringBuffer lSentenciasTodas, StringBuffer lSentenciasTodasTmp) {
		
		
		if(lSentenciasTodas.length() >50){
		System.out.println(" *********INSERTANDO COMPROBANTES  lSentenciasTodas*************");
		servicioFacturaLoteMasivo.guardaComprobanteProveedorBatch(lSentenciasTodas.toString());
		System.out.println(" *********DESPUES DE INSERTAR COMPROBANTES  lSentenciasTodas*************");
		}
		
		if(lSentenciasTodasTmp.length()>50){
		System.out.println(" *********INSERTANDO COMPROBANTES TMP lSentenciasTodasTmp*************");
		servicioFacturaLoteMasivo.guardaComprobanteProveedorBatch(lSentenciasTodasTmp.toString());
		System.out.println(" *********DESPUES DE INSERTAR COMPROBANTES  lSentenciasTodas*************");
		}
	}
	
	public static String  obtenerDatosClaveAcceso(String pClaveAcceso, int pInicio, int pFin){
		String lDatoBuscar=new String();
		if(pClaveAcceso!=null){
			lDatoBuscar=	pClaveAcceso.substring(pInicio,pFin);
		}
		return lDatoBuscar;
	}
	
	
	public static String formatoFechaAutorizacion(String pFechaAutorizacion){
		String lFechaAutorizacion=pFechaAutorizacion;
			
		if(pFechaAutorizacion!=null){
			if(pFechaAutorizacion.length()==18){
				lFechaAutorizacion=pFechaAutorizacion.substring(0, 10)+" "+pFechaAutorizacion.substring(10, 18);
		       }
			}
			return lFechaAutorizacion;
		}	
	



	///////////////////////// fin 16012017
	
	
	    
 
    public void actualizacionComprobantes(){
    	Integer cantidadTomar = 20;
    	try{
    		cantidadTomar= Integer.parseInt(servicioParametros.getValorParametro("CANTIDAD_TOMAR_ROBOT_ACTUALIZACION"));
    	}catch(Exception e){
    		cantidadTomar = 20;
    		System.out.println("SE DEBE CONFIGUARAR EL PARAETRO CANTIDAD_TOMAR_ROBOT_ACTUALIZACION");
    	}
    	List<String> listaClaves= servicioFacturaLoteMasivo.obtenerClavesProcesar(cantidadTomar);
    	for(String clave : listaClaves){
    		try{
    			consultarAutorizacion(clave);
    		}catch(Throwable e){
    			e.printStackTrace();
    		}
    	}
    }
    
    public void extraerDatos(String respuestaWebService, String claveAcceso) throws ParserConfigurationException, JAXBException, TransformerException, SAXException, IOException, Throwable{    	
    	String xmlAutorizado = respuestaWebService;		
    	List <FeDetalleComprobanteProveedor> listFeDetalleComprobanteProveedor = new ArrayList<>();
    	
    	String lNumeroDocSustento= new String();
		if (xmlAutorizado != null) {				
			FeComprobanteProveedor comProv = new FeComprobanteProveedor();
			String xmlStringProveedor = xmlAutorizado;
			String rucDestinatario="";
			String numdocumento = obtenerNumDocumento(xmlStringProveedor);
			String ruc =obtenerTagXml(xmlAutorizado,"ruc");
			String fecha=obtenerTagXml(xmlAutorizado,"fechaAutorizacion");
			String numeroAutorizacion=obtenerTagXml(xmlAutorizado,"numeroAutorizacion");
			String valor="";
			String documentoAsociado ="";
			Double datosFactura[]= new Double[6];
			Double datosNotaCredito[]= new Double[6];
			/// INI MBAS
			
			String lSentExtraerSumaNotasCreditoXFactura= servicioParametros.getValorParametro(INombresParametros.SENTENCIA_SUMA_NOTAS_CREDITO_X_FACTURA);
			String lSentExtraeDatosFactura=servicioParametros.getValorParametro(INombresParametros.SENTENCIA_EXTRAE_DATOS_FACTURA);
			String lSentActualizacionSumaNotasCredito= servicioParametros.getValorParametro(INombresParametros.SENTENCIA_ACT_SUMA_NC_X_FACTURA);
			String lSentActualizacionNotasCreditoxFactura= servicioParametros.getValorParametro(INombresParametros.SENTENCIA_ACT_NC_X_FACTURA);
			String lSentDocumentosAsociadosNotasCredito= servicioParametros.getValorParametro(INombresParametros.SENTENCIA_DOCUMENTOS_ASOCIADOS_NOTA_CREDITO); 
			
			String  lNumdocumentoTMP= new String();
			Double lDocAsociadoValorDocumento=0.0;// VALOR_DOCUMENTO
			Double lDocAsociadoSubtotal14=0.0;//SUBTOTAL_12
			Double lDocAsociadoSubtotal0=0.0;//SUBTOTAL_0
			Double lDocAsociadoIVA=0.0;//IVA
			Double lDocAsociadoISD=0.0;//ISD
			//FIN MBAS
			
			if(claveAcceso.substring(8, 10).equals("01")){
				valor=obtenerTagXml(xmlAutorizado,"importeTotal");
				rucDestinatario=obtenerTagXml(xmlStringProveedor,"identificacionComprador");				
			}
			if(claveAcceso.substring(8, 10).equals("04")){
				valor=obtenerTagXml(xmlAutorizado,"valorModificacion");	
				rucDestinatario=obtenerTagXml(xmlStringProveedor,"identificacionComprador");
				documentoAsociado=obtenerTagXml(xmlStringProveedor,"numDocModificado");
			}
			if(claveAcceso.substring(8, 10).equals("05")){
				valor=obtenerTagXml(xmlAutorizado,"valorTotal");	
				rucDestinatario=obtenerTagXml(xmlStringProveedor,"identificacionComprador");
				documentoAsociado=obtenerTagXml(xmlStringProveedor,"numDocModificado");
			}
			if(claveAcceso.substring(8, 10).equals("06")||claveAcceso.substring(8, 10).equals("07")){
				valor="0";	
				if(claveAcceso.substring(8, 10).equals("07")){						
					rucDestinatario=obtenerTagXml(xmlStringProveedor,"identificacionSujetoRetenido");
				}
				if(claveAcceso.substring(8, 10).equals("06")){					
					rucDestinatario=obtenerTagXml(xmlStringProveedor,"rucTransportista");
				}
			}
			GeDatosGeneralesCompania empresa = servicioParametros.obtenerCompaniaPorRUC(rucDestinatario);
			if(empresa !=null &&empresa.getId()!=null){
				String xmlBase = xmlAutorizado;				
				if (xmlBase.contains("<![CDATA[")) {
					xmlBase = xmlBase.substring(xmlBase.indexOf("<![CDATA[") + 9);
					xmlBase = xmlBase.substring(0, xmlBase.lastIndexOf("]]>"));
				}
				if(xmlBase.contains("<comprobante>")){
					xmlBase=obtenerTagXml(xmlBase,"comprobante");
				}
				xmlBase = xmlBase.replaceAll("&lt;", "<");
				xmlBase = xmlBase.replaceAll("&gt;", ">");
				BigDecimal totalValorRetenido =  new BigDecimal(0.0D);
				if(claveAcceso.substring(8, 10).equals("07")){		
					ComprobanteRetencion retencion = null;
					try {
						retencion = convierteDocumentARetencion(crearXMLComprobanteRetencion(xmlBase));
						for (Impuesto det : retencion.getImpuestos().getImpuesto()) {
							String docu= det.getNumDocSustento();
							if("".equals(documentoAsociado)){
								documentoAsociado=docu;
							}								
							try{
								totalValorRetenido = totalValorRetenido.add(det.getValorRetenido());
							}catch(Exception e){
								
							}							
						}
						valor = totalValorRetenido.toString();
					} catch (Throwable e1) {							
						e1.printStackTrace();
					}								
				}
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
				Date date = null;				
				try {
					date = format.parse(fecha);					
				} catch (ParseException e) {					
					format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
					try {
						date = format.parse(fecha);					
					} catch (ParseException e1) {
						
					}
				}	
				
				comProv.setEstado("V");
				comProv.setObservacion("COMPROBANTE RECIBIDO, PROCESO FINALIZADO ROBOT");
				comProv.setNumComprobante(numdocumento);
				comProv.setClaveAcceso(claveAcceso);
				comProv.setXmlSRI(xmlBase);
				comProv.setXmlProveedor(xmlAutorizado);
				comProv.setFechaRegistro(new Date());
				comProv.setTipo(claveAcceso.substring(8, 10));
				comProv.setNumAutorizacion(numeroAutorizacion);
				comProv.setRucEmisor(obtenerTagXml(xmlAutorizado,"ruc"));
				comProv.setRazonSocial(obtenerRazonSocial(xmlAutorizado));
				valor=valor==null?"0.0":valor;
				valor=valor.equals("")?"0.0":valor;
				comProv.setTotalDocumento(Double.parseDouble(valor));
				comProv.setRucDestinatario(empresa.getIdentificacion());	
				
				
				try{
					comProv.setFechaAutorizacion(date);
				}catch(Exception e){
					
				}
				String fechaE =claveAcceso.substring(0,2)+"/"+claveAcceso.substring(2,4)+"/"+claveAcceso.substring(4,8);
				DateFormat formatE = new SimpleDateFormat("dd/MM/yyyy");
				Date dateE = null;				
				try {
					dateE = formatE.parse(fechaE);
				} catch (ParseException e) {
					
				}	
				comProv.setFechaEmision(dateE);
				comProv.setCompania(String.valueOf(empresa.getId()));
				comProv.setIdSuscriptor(empresa.getIdSuscriptor());
				comProv.setOrigen("SRI");	
				
				
				comProv.setDocAsociado(documentoAsociado);
				
				//MBAS INI
				
				try {
					if(!claveAcceso.substring(8, 10).equals("01")){
						System.out.println("no es factura, ingresa por validacion y buscara documento asociado "+documentoAsociado);
						if(documentoAsociado==null || "".equals(documentoAsociado.trim()) ){
							comProv.setObservacion("ATENCION: Este comprobante no tiene un documento relacionado.");	
						}						
					}					
				} catch (Exception e) {
					e.printStackTrace();
					
				}	
				
				//MBAS FIN
				
			
				
				String datosAdicionales="";
				Double subtotal =0.0;
				/// INI MBAS
				Double lISD =0.0;
				/// FIN MBAS
				Double subtotal12 =0.0;
				Double subtotal0 =0.0;
				Double iva =0.0;
				String formaPago="";
				String contribuyenteEspecial ="";
				StringBuffer descripcionesProductos = new StringBuffer();
				final String separador = "|";
				String tipoEmision = "";
				
				if(claveAcceso.substring(8, 10).equals("01")){					
					Document doc = crearRideFacturaXML(xmlBase);
					JAXBContext contextFactura = JAXBContext.newInstance(Factura.class);
					Unmarshaller unmarshalFactura = contextFactura.createUnmarshaller();
					Factura lFactura = (Factura)unmarshalFactura.unmarshal(doc);
					String cEspecial="";
					String oContabilidad=""; 
					try{
						cEspecial=lFactura.getInfoFactura().getContribuyenteEspecial();
					}catch(Exception e){
						
					}
					try{
						oContabilidad=lFactura.getInfoFactura().getObligadoContabilidad().value();
					}catch(Exception e){
						
					}
					if(cEspecial!=null && !cEspecial.equals("") && oContabilidad!=null && oContabilidad.equals("SI"))
						contribuyenteEspecial="C_ESP";
					if((cEspecial==null||cEspecial.equals("")) &&  oContabilidad!=null && oContabilidad.equals("SI"))
						contribuyenteEspecial="C_NOR_OBLI";
					if((cEspecial==null||cEspecial.equals("")) && (oContabilidad==null || oContabilidad.equals("")))
						contribuyenteEspecial="C_NOR_NO_OBLIG";
						
					if (lFactura.getInfoAdicional()!=null)
					{
						for(CampoAdicional campoA : lFactura.getInfoAdicional().getCampoAdicional()){							
							datosAdicionales+=campoA.getNombre().toUpperCase()+ ":"+campoA.getValue()+",";
							
						}
					}
					try{
						subtotal=lFactura.getInfoFactura().getTotalSinImpuestos().doubleValue();
					}catch(Exception e){
						
					}
					for (Factura.InfoFactura.TotalConImpuestos.TotalImpuesto ti : lFactura.getInfoFactura().getTotalConImpuestos().getTotalImpuesto())
				    {
						if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && !(INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(ti.getCodigoPorcentaje())||
					    		  INombresParametros.TIPOIMPUESTO_IVA_NOSUJETO.equals(ti.getCodigoPorcentaje())))
					      {
							subtotal12=ti.getBaseImponible().doubleValue();
							iva=ti.getValor().doubleValue();
					      }
						
						if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(ti.getCodigoPorcentaje()))
					      {
							subtotal0=ti.getBaseImponible().doubleValue();
					      }
					      
						   ////// INI MBAS ISD IMPUESTO DE SALIDA DE DIVISAS
						if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && INombresParametros.TIPOIMPUESTO_IVA_ISD.equals(ti.getCodigoPorcentaje()))
						  {
						   lISD=ti.getBaseImponible().doubleValue();
						   }
											
											comProv.setISD(lISD);
						   /// FIN MBAS
												
				    }
					
					if(!(lFactura.getInfoFactura().getPagos()==null)){				
						for(Pago pag : lFactura.getInfoFactura().getPagos().getPago()){								
							formaPago+=pag.getFormaPago()+",";			
						}		
					}
					
					tipoEmision = lFactura.getInfoTributaria().getTipoEmision();
					
					if (lFactura.getDetalles() != null){
						for (Factura.Detalles.Detalle detalleFactura : lFactura.getDetalles().getDetalle()){
							descripcionesProductos.append(detalleFactura.getDescripcion());
							descripcionesProductos.append(separador);
	
						}
					}
					
					/// INI MBAS 25012017 CAMBIOS PARA GUARDAR SUMA DE LAS NOTAS DE CREDITO CUANDO LLEGUE UNA FACTURA
					
				   try{
					  
					   					
					 lNumdocumentoTMP=numdocumento.replace("-", "");
					 lDocAsociadoValorDocumento=0.0;// VALOR_DOCUMENTO
					 lDocAsociadoSubtotal14=0.0;//SUBTOTAL_12
					 lDocAsociadoSubtotal0=0.0;//SUBTOTAL_0
					 lDocAsociadoIVA=0.0;//IVA
					 lDocAsociadoISD=0.0;//ISD
					
					System.out.println("**FACTURAS************ ANTES DE SUMAR LAS NOTAS DE CREDITO **************NUMERO COMPROBANTE "+lNumdocumentoTMP+"**** RUC DESTINATARIO "+empresa.getIdentificacion());
					datosFactura=servicioFacturaLoteMasivo.obtenerDatosSentenciasCuadreRobot(lSentExtraerSumaNotasCreditoXFactura, 
							     lNumdocumentoTMP, empresa.getIdentificacion(), COD_NOTA_CREDITO,"T");
					
					lDocAsociadoValorDocumento=datosFactura[0];
					lDocAsociadoSubtotal14=datosFactura[1];
					lDocAsociadoSubtotal0=datosFactura[2];
					lDocAsociadoIVA=datosFactura[3];
					lDocAsociadoISD=datosFactura[4];
					
					System.out.println("lDocAsociadoValorDocumento:"+lDocAsociadoValorDocumento); 
					System.out.println("lDocAsociadoSubtotal14:"+lDocAsociadoSubtotal14);
					System.out.println("lDocAsociadoSubtotal0:"+lDocAsociadoSubtotal0);
					System.out.println("lDocAsociadoIVA:"+lDocAsociadoIVA);
					System.out.println("lDocAsociadoISD:"+lDocAsociadoISD);
					System.out.println("********FACTURAS*********DESPUES DE SUMAR NOTAS CREDITO***********"); 
										
					comProv.setDocAsociadoValorDocumento(lDocAsociadoValorDocumento);
					comProv.setDocAsociadoSubtotal0(lDocAsociadoSubtotal0);
			    	comProv.setDocAsociadoSubtotal14(lDocAsociadoSubtotal14);
					comProv.setDocAsociadoIVA(lDocAsociadoIVA);
					comProv.setDocAsociadoISD(lDocAsociadoISD);
					
												
					} catch(Exception lErrorCuadre){
						System.out.println("************** ERROR EN CUADRE FACTURAS CON NOTAS DE CREDITO ***********************");
						lErrorCuadre.printStackTrace();
					}
					
		           /// FIN MBAS 25012017
						
				}
				
				if(claveAcceso.substring(8, 10).equals("04")){//nc
					Document doc = crearRideFacturaXML(xmlBase);
					JAXBContext jaxbcontext = JAXBContext
							.newInstance(com.producto.comprobanteselectronicos.modelo.normal.xsd.v110.notacredito.NotaCredito.class);
					Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
					NotaCredito lNotaCredito = (NotaCredito) unmarshaller.unmarshal(doc);
					if (lNotaCredito.getInfoAdicional() != null) {
						for (NotaCredito.InfoAdicional.CampoAdicional campoA : lNotaCredito
								.getInfoAdicional().getCampoAdicional()) {
							datosAdicionales+=campoA.getNombre().toUpperCase()+ ":"+campoA.getValue()+",";
						}
					}
					try{
						subtotal=lNotaCredito.getInfoNotaCredito().getTotalSinImpuestos().doubleValue();
					}catch(Exception e){
						
					}
					for (TotalConImpuestos.TotalImpuesto ti : lNotaCredito
							.getInfoNotaCredito().getTotalConImpuestos().getTotalImpuesto()){
						if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && !(INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(ti.getCodigoPorcentaje())||
					    		  INombresParametros.TIPOIMPUESTO_IVA_NOSUJETO.equals(ti.getCodigoPorcentaje())))
					      {
							subtotal12=ti.getBaseImponible().doubleValue();
							iva=ti.getValor().doubleValue();
					      }

						if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(ti.getCodigoPorcentaje()))
					      {
							subtotal0=ti.getBaseImponible().doubleValue();
					      }
						
						////// INI MBAS ISD IMPUESTO DE SALIDA DE DIVISAS
						if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && INombresParametros.TIPOIMPUESTO_IVA_ISD.equals(ti.getCodigoPorcentaje()))
					      {
							   lISD=ti.getBaseImponible().doubleValue();
					      }
						
						comProv.setISD(lISD);
					    /// FIN MBAS
												
					}
					
					String cEspecial="";
					String oContabilidad="";
					try{
						cEspecial=lNotaCredito.getInfoNotaCredito().getContribuyenteEspecial();
					}catch(Exception e){
						
					}
					try{
						oContabilidad=lNotaCredito.getInfoNotaCredito().getObligadoContabilidad().value();
					}catch(Exception e){
						
					}
					if(cEspecial!=null && !cEspecial.equals("") && oContabilidad!=null && oContabilidad.equals("SI"))
						contribuyenteEspecial="C_ESP";
					if((cEspecial==null||cEspecial.equals("")) &&  oContabilidad!=null && oContabilidad.equals("SI"))
						contribuyenteEspecial="C_NOR_OBLI";
					if((cEspecial==null||cEspecial.equals("")) && (oContabilidad==null || oContabilidad.equals("")))
						contribuyenteEspecial="C_NOR_NO_OBLIG";
					
					if (lNotaCredito.getDetalles() != null){
						for (NotaCredito.Detalles.Detalle detalleNotaCredito : lNotaCredito.getDetalles().getDetalle()){
							descripcionesProductos.append(detalleNotaCredito.getDescripcion());
							descripcionesProductos.append(separador);
	
						}
					}
					
					tipoEmision = lNotaCredito.getInfoTributaria().getTipoEmision();
					
					
					// INI MBAS 06012017
				   	lNumeroDocSustento=lNotaCredito.getInfoNotaCredito().getNumDocModificado();
					comProv.setDocumentoAsociadoRet(lNumeroDocSustento);
					// FIN MBAS 06012017
					
					
					
					
					/// INI MBAS 25012017 CAMBIOS PARA GUARDAR SUMA DE LAS NOTAS DE CREDITO CUANDO LLEGUE UNA NOTA DE CREDITO
					
					   try{
						   
						 lDocAsociadoValorDocumento=0.0;// VALOR_DOCUMENTO
						 lDocAsociadoSubtotal14=0.0;//SUBTOTAL_12
						 lDocAsociadoSubtotal0=0.0;//SUBTOTAL_0
						 lDocAsociadoIVA=0.0;//IVA
						 lDocAsociadoISD=0.0;//ISD
						 datosFactura=servicioFacturaLoteMasivo.obtenerDatosSentenciasCuadreRobot(lSentExtraeDatosFactura, 
								 lNumeroDocSustento, empresa.getIdentificacion(), COD_FACTURA,null);
						 
						 lDocAsociadoValorDocumento=datosFactura[0];
						 lDocAsociadoSubtotal14=datosFactura[1];
						 lDocAsociadoSubtotal0=datosFactura[2];
						 lDocAsociadoIVA=datosFactura[3];
						 lDocAsociadoISD=datosFactura[4];
						
						 comProv.setDocAsociadoValorDocumento(lDocAsociadoValorDocumento);
						 comProv.setDocAsociadoSubtotal0(lDocAsociadoSubtotal0);
					     comProv.setDocAsociadoSubtotal14(lDocAsociadoSubtotal14);
						 comProv.setDocAsociadoIVA(lDocAsociadoIVA);
						 comProv.setDocAsociadoISD(lDocAsociadoISD);
						 
						
						 /////////////////////TERMINA DA ACTUALIZAR LA NOTA DE CREDITO CON EL VALOR DE LA FACTURA
									   
							
						 lNumdocumentoTMP=lNumeroDocSustento.replace("-", "");
						 lNumdocumentoTMP=lNumdocumentoTMP.replace(";", "");
						 lDocAsociadoValorDocumento=0.0;// VALOR_DOCUMENTO
						 lDocAsociadoSubtotal14=0.0;//SUBTOTAL_12
						 lDocAsociadoSubtotal0=0.0;//SUBTOTAL_0
						 lDocAsociadoIVA=0.0;//IVA
						 lDocAsociadoISD=0.0;//ISD
						
						System.out.println("****NOTAS CREDITO ********** ANTES DE SUMAR LAS NOTAS DE CREDITO **************NUMERO COMPROBANTE "+lNumdocumentoTMP+"**** RUC DESTINATARIO "+empresa.getIdentificacion());
						System.out.println("**NOTAS CREDITO ***Parametro : "+lNumdocumentoTMP); 
						System.out.println("**NOTAS CREDITO ***Parametro Ruc Emisor: "+empresa.getIdentificacion());
						System.out.println("**NOTAS CREDITO ***Parametrol tipo documento: "+COD_NOTA_CREDITO);
						
						datosNotaCredito=servicioFacturaLoteMasivo.obtenerDatosSentenciasCuadreRobot(lSentExtraerSumaNotasCreditoXFactura, 
								     lNumdocumentoTMP, empresa.getIdentificacion(), COD_NOTA_CREDITO,claveAcceso);
						
						lDocAsociadoValorDocumento=datosNotaCredito[0];
						lDocAsociadoSubtotal14=datosNotaCredito[1];
						lDocAsociadoSubtotal0=datosNotaCredito[2];
						lDocAsociadoIVA=datosNotaCredito[3];
						lDocAsociadoISD=datosNotaCredito[4];
						
						System.out.println("**NOTAS CREDITO ***lDocAsociadoValorDocumento: "+lDocAsociadoValorDocumento); 
						System.out.println("**NOTAS CREDITO ***lDocAsociadoSubtotal14: "+lDocAsociadoSubtotal14);
						System.out.println("**NOTAS CREDITO ***lDocAsociadoSubtotal0: "+lDocAsociadoSubtotal0);
						System.out.println("**NOTAS CREDITO ***lDocAsociadoIVA: "+lDocAsociadoIVA);
						System.out.println("**NOTAS CREDITO ***lDocAsociadoISD: "+lDocAsociadoISD);
						System.out.println("****NOTAS CREDITO***** DESPUES DE SUMAR NOTAS CREDITO***********"); 
						System.out.println("**NOTAS CREDITO ***lNumeroDocSustento: "+lDocAsociadoValorDocumento);
						System.out.println("**NOTAS CREDITO ***ruc emisor: "+empresa.getIdentificacion());
						System.out.println("**NOTAS CREDITO ***tipo Documento: "+COD_FACTURA);
						
						System.out.println("**NOTAS CREDITO **SUMA DE DOCUMENTO CON ACTUAL***********"); 
						lDocAsociadoValorDocumento=lDocAsociadoValorDocumento+validaNulosDouble(comProv.getTotalDocumento()); 
						System.out.println("**NOTAS CREDITO ***lDocAsociadoValorDocumento SUMADO: "+lDocAsociadoValorDocumento); 
						
						lDocAsociadoSubtotal14=lDocAsociadoSubtotal14+validaNulosDouble(subtotal12);
						System.out.println("**NOTAS CREDITO ***lDocAsociadoSubtotal14 SUMADO: "+lDocAsociadoSubtotal14);
						
						lDocAsociadoSubtotal0=lDocAsociadoSubtotal0+validaNulosDouble(subtotal0);
						System.out.println("**NOTAS CREDITO ***lDocAsociadoSubtotal0: "+lDocAsociadoSubtotal0);
						
						lDocAsociadoIVA=lDocAsociadoIVA+validaNulosDouble(iva);
						System.out.println("**NOTAS CREDITO ***lDocAsociadoIVA SUMADO: "+lDocAsociadoIVA);
						
						lDocAsociadoISD=lDocAsociadoISD+validaNulosDouble(lISD);
						System.out.println("**NOTAS CREDITO ***lDocAsociadoISD SUMADO: "+lDocAsociadoISD);
						
						System.out.println("****NOTAS CREDITO***** DESPUES DE SUMAR NOTAS CREDITO***********"); 
						System.out.println("**NOTAS CREDITO ***lNumeroDocSustento: "+lDocAsociadoValorDocumento);
						System.out.println("**NOTAS CREDITO ***ruc emisor: "+empresa.getIdentificacion());
						System.out.println("**NOTAS CREDITO ***tipo Documento: "+COD_FACTURA);
						
						
						System.out.println("******NOTAS CREDITO********** OBTENER TODAS LAS NOTAS DE CREDTIO ASOCIADAS DE LA FACTURA ***********");
						
						List<String> listaDocumentosAsociados= new ArrayList<>();
						listaDocumentosAsociados=   servicioFacturaLoteMasivo.extraeDocumentosAsociados(lSentDocumentosAsociadosNotasCredito,  lNumdocumentoTMP, 
								  empresa.getIdentificacion(), COD_NOTA_CREDITO,  claveAcceso);
						 
						String lDocumentosAsociados=new String();
						for(String NCAsociadaAFactura :listaDocumentosAsociados){
						lDocumentosAsociados=lDocumentosAsociados+";"+NCAsociadaAFactura;
						}
						System.out.println("*********NOTAS CREDITO*******DESPUES OBTENER TODAS LASNOTAS DE CREDTIO ASOCIADAS DE LA FACTURA ***********"+lDocumentosAsociados);
						lDocumentosAsociados=lDocumentosAsociados+";"+comProv.getNumComprobante();;
					    servicioFacturaLoteMasivo.actualizacionSentencia(lSentActualizacionSumaNotasCredito,
								lNumeroDocSustento,
								empresa.getIdentificacion(),
								COD_FACTURA, lDocAsociadoValorDocumento,lDocAsociadoSubtotal14, lDocAsociadoSubtotal0,lDocAsociadoIVA,lDocAsociadoISD, lDocumentosAsociados);
						System.out.println("****NOTAS CREDITO***** DESPUES DE ACTUALIZAR NOTAS CREDITO***********"); 
						
						} catch(Exception lErrorCuadre){
							System.out.println("************** ERROR EN CUADRE FACTURAS CON NOTAS DE CREDITO ***********************");
							lErrorCuadre.printStackTrace();
						}
						
			           /// FIN MBAS 25012017
					
				}
				if(claveAcceso.substring(8, 10).equals("05")){ //nd
					Document doc = crearRideFacturaXML(xmlBase);
					JAXBContext jaxbcontext = JAXBContext.newInstance(NotaDebito.class);
					Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
					NotaDebito lNotaDebito = (NotaDebito) unmarshaller.unmarshal(doc);
					if (lNotaDebito.getInfoAdicional() != null) {
						for (NotaDebito.InfoAdicional.CampoAdicional campoA : lNotaDebito
								.getInfoAdicional().getCampoAdicional()) {
							datosAdicionales+=campoA.getNombre().toUpperCase()+ ":"+campoA.getValue()+",";
						}
					}
					try{
						subtotal=lNotaDebito.getInfoNotaDebito().getTotalSinImpuestos().doubleValue();
					}catch(Exception e){
						
					}
					for (com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.Impuesto ti : lNotaDebito.getInfoNotaDebito().getImpuestos()
							.getImpuesto()) {
						if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && !(INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(ti.getCodigoPorcentaje())||
					    		  INombresParametros.TIPOIMPUESTO_IVA_NOSUJETO.equals(ti.getCodigoPorcentaje())))
					      {
							subtotal12=ti.getBaseImponible().doubleValue();
							iva=ti.getValor().doubleValue();
					      }

						if ((INombresParametros.TIPOIMPUESTO_IVA.equals(ti.getCodigo())) && INombresParametros.TIPOIMPUESTO_IVA_VENTA0.equals(ti.getCodigoPorcentaje()))
					      {
							subtotal0=ti.getBaseImponible().doubleValue();
					      }
					}
					if(lNotaDebito.getInfoNotaDebito().getPagos()!=null){					
						for( com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.NotaDebito.InfoNotaDebito.Pagos pag : lNotaDebito.getInfoNotaDebito().getPagos()){
							for(com.producto.comprobanteselectronicos.modelo.normal.xsd.notadebito.Pago p : pag.getPago()){
								formaPago+=p.getFormaPago();
							}
							
						}
					}
					
					String cEspecial="";
					String oContabilidad="";
					try{
						cEspecial=lNotaDebito.getInfoNotaDebito().getContribuyenteEspecial();
					}catch(Exception e){
						
					}
					try{
						oContabilidad=lNotaDebito.getInfoNotaDebito().getObligadoContabilidad().value();
					}catch(Exception e){
						
					}
					if(cEspecial!=null && !cEspecial.equals("") && oContabilidad!=null && oContabilidad.equals("SI"))
						contribuyenteEspecial="C_ESP";
					if((cEspecial==null||cEspecial.equals("")) &&  oContabilidad!=null && oContabilidad.equals("SI"))
						contribuyenteEspecial="C_NOR_OBLI";
					if((cEspecial==null||cEspecial.equals("")) && (oContabilidad==null || oContabilidad.equals("")))
						contribuyenteEspecial="C_NOR_NO_OBLIG";
					
					tipoEmision = lNotaDebito.getInfoTributaria().getTipoEmision();
					
					// INI MBAS 06012017
				   	lNumeroDocSustento=lNotaDebito.getInfoNotaDebito().getNumDocModificado();
					comProv.setDocumentoAsociadoRet(lNumeroDocSustento);
					// FIN MBAS 06012017
					
					
				}
				if(claveAcceso.substring(8, 10).equals("06")||claveAcceso.substring(8, 10).equals("07")){
					valor="0";	
					if(claveAcceso.substring(8, 10).equals("07")){			//retencion			
						Document documentoRetencion = crearRideFacturaXML(xmlBase);
						JAXBContext jaxbcontext = JAXBContext
								.newInstance(ComprobanteRetencion.class);
						Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
						ComprobanteRetencion lComprobanteRetencion = (ComprobanteRetencion) unmarshaller
								.unmarshal(documentoRetencion);
						if (lComprobanteRetencion.getInfoAdicional() != null) {
							for (ComprobanteRetencion.InfoAdicional.CampoAdicional campoA : lComprobanteRetencion
									.getInfoAdicional().getCampoAdicional()) {
								datosAdicionales+=campoA.getNombre().toUpperCase()+ ":"+campoA.getValue()+",";
							}
						}
						
						String cEspecial="";
						String oContabilidad="";
						try{
							cEspecial=lComprobanteRetencion.getInfoCompRetencion().getContribuyenteEspecial();
						}catch(Exception e){
							
						}
						try{
							oContabilidad=lComprobanteRetencion.getInfoCompRetencion().getObligadoContabilidad().value();
						}catch(Exception e){
							
						}
						if(cEspecial!=null && !cEspecial.equals("") && oContabilidad!=null && oContabilidad.equals("SI"))
							contribuyenteEspecial="C_ESP";
						if((cEspecial==null||cEspecial.equals("")) &&  oContabilidad!=null && oContabilidad.equals("SI"))
							contribuyenteEspecial="C_NOR_OBLI";
						if((cEspecial==null||cEspecial.equals("")) && (oContabilidad==null || oContabilidad.equals("")))
							contribuyenteEspecial="C_NOR_NO_OBLIG";
						
						
						for (Impuesto imp : lComprobanteRetencion.getImpuestos().getImpuesto()) {
							FeDetalleComprobanteProveedor detalleComprobanteProveedor = new FeDetalleComprobanteProveedor();
                          
							detalleComprobanteProveedor.setBaseImponible(imp.getBaseImponible().doubleValue());
							detalleComprobanteProveedor.setClaveAcceso(lComprobanteRetencion.getInfoTributaria().getClaveAcceso());
							detalleComprobanteProveedor.setCodigoRetencion(imp.getCodigoRetencion());
							detalleComprobanteProveedor.setFecharegistro(new Date());
							detalleComprobanteProveedor.setPorcentajeRetencion(imp.getPorcentajeRetener().doubleValue());
							detalleComprobanteProveedor.setValorRetenido(imp.getValorRetenido().doubleValue());
							detalleComprobanteProveedor.setDocumentoSustento(imp.getNumDocSustento());
							detalleComprobanteProveedor.setTipoRetencion(imp.getCodigo());
							
							listFeDetalleComprobanteProveedor.add(detalleComprobanteProveedor);
							
							
                     }

						tipoEmision = lComprobanteRetencion.getInfoTributaria().getTipoEmision();
						
						lNumeroDocSustento="";
						for (Impuesto impuestoRetencion : lComprobanteRetencion.getImpuestos().getImpuesto()){
																
						// INI MBAS 06012017
					   	 try{
							lNumeroDocSustento=lNumeroDocSustento+";"+impuestoRetencion.getNumDocSustento();
							}catch(Exception lErrorDestinatarios){
								lErrorDestinatarios.printStackTrace();
							}
					 // FIN MBAS 06012017
						} 
						// INI MBAS 06012017
						comProv.setDocumentoAsociadoRet(lNumeroDocSustento);
						// FIN MBAS 06012017
						
					}
					if(claveAcceso.substring(8, 10).equals("06")){ //guia					
						Document doc = crearRideFacturaXML(xmlBase);
						JAXBContext jaxbcontext = JAXBContext.newInstance(GuiaRemision.class);
						Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
						GuiaRemision guia = (GuiaRemision) unmarshaller.unmarshal(doc);
						if (guia.getInfoAdicional() != null) {
							for (GuiaRemision.InfoAdicional.CampoAdicional campoA : guia
									.getInfoAdicional().getCampoAdicional()) {
								datosAdicionales+=campoA.getNombre().toUpperCase()+ ":"+campoA.getValue()+",";
							}
						}
						
						String cEspecial="";
						String oContabilidad="";
						try{
							cEspecial=guia.getInfoGuiaRemision().getContribuyenteEspecial();
						}catch(Exception e){
							
						}
						try{
							oContabilidad=guia.getInfoGuiaRemision().getObligadoContabilidad().value();
						}catch(Exception e){
							
						}
						if(cEspecial!=null && !cEspecial.equals("") && oContabilidad!=null && oContabilidad.equals("SI"))
							contribuyenteEspecial="C_ESP";
						if((cEspecial==null||cEspecial.equals("")) &&  oContabilidad!=null && oContabilidad.equals("SI"))
							contribuyenteEspecial="C_NOR_OBLI";
						if((cEspecial==null||cEspecial.equals("")) && (oContabilidad==null || oContabilidad.equals("")))
							contribuyenteEspecial="C_NOR_NO_OBLIG";
						
						tipoEmision = guia.getInfoTributaria().getTipoEmision();
						
					  /// MBAS INI MBAS 06012017
						lNumeroDocSustento="";
						for (Destinatario destinatario:guia.getDestinatarios().getDestinatario() ){
							try{
							
							lNumeroDocSustento=	lNumeroDocSustento+";"+destinatario.getNumDocSustento();
							}catch(Exception lErrorDestinatarios){
								lErrorDestinatarios.printStackTrace();
							}
							
						}						
						comProv.setDocumentoAsociadoRet(lNumeroDocSustento);
					   /// MBAS FIN MBAS 06012017
					}
				}
				
				comProv.setSerie(obtenerTagXml(xmlAutorizado,"ptoEmi"));
				comProv.setEstablecimiento(obtenerTagXml(xmlAutorizado,"estab"));
				comProv.setSecuencial(obtenerTagXml(xmlAutorizado,"secuencial"));
				comProv.setSubtotal0(subtotal0);
				comProv.setSubtotal(subtotal);//falta retencion
				comProv.setSubtotal12(subtotal12);
				if(datosAdicionales!=null &&datosAdicionales.endsWith(","))
					datosAdicionales=datosAdicionales.substring(0, datosAdicionales.length()-1);
				comProv.setDatosAdicionales(datosAdicionales);
				comProv.setIva(iva);
				if(formaPago!=null &&formaPago.endsWith(","))
					formaPago=formaPago.substring(0, formaPago.length()-1);
				comProv.setFormaPago(formaPago);
				comProv.setNombreCliente(empresa.getRazonSocial());				
				comProv.setContribuyenteEspecial(contribuyenteEspecial);
				comProv.setTipoEmision(tipoEmision);
				comProv.setDetalleComprobante(descripcionesProductos.toString());
				
				System.out.println("ANTES DE ACTUALIZAR RECEPCION: "+ comProv.getClaveAcceso() + " TIPO EMISION: "+ tipoEmision + " DETALLE_COMPROBANTE: "+comProv.getDetalleComprobante());
				
				System.out.println("DOCUMENTO ASOCIADO: "+comProv.getDocAsociado());
				
			
				System.out.println("DOCUMENTO ASOCIADO RET: "+comProv.getDocumentoAsociadoRet());
				if(comProv.getDocumentoAsociadoRet()!=null){
					System.out.println("DOCUMENTO ASOCIADO RET LONGITUD: "+comProv.getDocumentoAsociadoRet().length());
				}
				
				 if(comProv.getDocAsociado()!=null){
					 System.out.println("DOCUMENTO ASOCIADO LONGITUD : "+comProv.getDocAsociado().length());
					 try{
						 if(comProv.getDocAsociado().length()>20){
					     System.out.println("PROBLEMA DOCUMENTO ASOCIADO: "+comProv.getDocumentoAsociadoRet()+" DE LA CLAVE "+comProv.getClaveAcceso());	 
					     comProv.setDocAsociado(comProv.getDocAsociado().substring(0, 20));	
						 }
									 
						 
					 }catch(Exception lError){
						 lError.printStackTrace();
					 }
					 
				 }
				 
				 
				 
				 if(claveAcceso.substring(8, 10).equals("01")){	
					 
					   System.out.println("********FACTURAS********* OBTENER TODAS LAS NOTAS DE CREDTIO ASOCIADAS DE LA FACTURA ***********");
					   System.out.println("********FACTURAS********* lNumdocumentoTMP "+lNumdocumentoTMP);
					   System.out.println("********FACTURAS*********  empresa.getIdentificacion() "+empresa.getIdentificacion());
					 
					   List<String> listaDocumentosAsociados= new ArrayList<>();
					   
					   listaDocumentosAsociados=   servicioFacturaLoteMasivo.extraeDocumentosAsociados(lSentDocumentosAsociadosNotasCredito,  lNumdocumentoTMP, 
							  empresa.getIdentificacion(), COD_NOTA_CREDITO,  "T");
					 
					   System.out.println("********FACTURAS********* CANTIDAD OBTENIDA DOCUMENTO ASOCIADO "+listaDocumentosAsociados.size());
					   String lDocumentosAsociados=new String();
					   for(String NCAsociadaAFactura :listaDocumentosAsociados){
					   lDocumentosAsociados=lDocumentosAsociados+";"+NCAsociadaAFactura;
					   }
					   
					   System.out.println("********FACTURAS*********DESPUES OBTENER TODAS LASNOTAS DE CREDTIO ASOCIADAS DE LA FACTURA ***********"+lDocumentosAsociados);
								   
					   comProv.setDocumentoAsociadoRet(lDocumentosAsociados);
					 
					   System.out.println("********FACTURAS*********ANTES DE ACTUALIZAR NOTAS DE CREDITO CON DATOS DE FACTURA ***********"); 
						
						 lDocAsociadoValorDocumento=0.0;// VALOR_DOCUMENTO
						 lDocAsociadoSubtotal14=0.0;//SUBTOTAL_12
						 lDocAsociadoSubtotal0=0.0;//SUBTOTAL_0
						 lDocAsociadoIVA=0.0;//IVA
						 lDocAsociadoISD=0.0;//ISD
						
						 lDocAsociadoValorDocumento=comProv.getTotalDocumento();// VALOR_DOCUMENTO
						 lDocAsociadoSubtotal14=comProv.getSubtotal12();//SUBTOTAL_12
						 lDocAsociadoSubtotal0=comProv.getSubtotal0();//SUBTOTAL_0
						 lDocAsociadoIVA=comProv.getIva();//IVA
						 lDocAsociadoISD=comProv.getISD();//ISD
						 
						 System.out.println("**FACTURAS ***lNumdocumentoTMP: "+lNumdocumentoTMP);
						 System.out.println("**FACTURAS ***empresa.getIdentificacion(): "+empresa.getIdentificacion());
						 System.out.println("**FACTURAS ***lDocAsociadoValorDocumento: "+lDocAsociadoValorDocumento); 
						 System.out.println("**FACTURAS***lDocAsociadoSubtotal14: "+lDocAsociadoSubtotal14);
						 System.out.println("**FACTURAS ***lDocAsociadoSubtotal0: "+lDocAsociadoSubtotal0);
						 System.out.println("**FACTURAS ***lDocAsociadoIVA: "+lDocAsociadoIVA);
						 System.out.println("**FACTURAS ***lDocAsociadoISD: "+lDocAsociadoISD);
						 
						servicioFacturaLoteMasivo.actualizacionSentencia(lSentActualizacionNotasCreditoxFactura,
								lNumdocumentoTMP,
								empresa.getIdentificacion(),
								COD_NOTA_CREDITO, lDocAsociadoValorDocumento,lDocAsociadoSubtotal14, lDocAsociadoSubtotal0,lDocAsociadoIVA,lDocAsociadoISD,null);
						System.out.println("********FACTURAS*********DESPUES DE ACTUALIZAR NOTAS DE CREDITO ***********"); 
					  
					 
					 
				 }
				 
				 
				try{
				servicioFacturaLoteMasivo.actualizaComprobanteProveedor(comProv);
				}catch(Exception lError){
					System.out.println("************** ERROR EN ACTUALIZACION DE COMPROBANTES ************** "+ comProv.getClaveAcceso());
					lError.printStackTrace();
					System.out.println("************** ERROR EN ACTUALIZACION DE COMPROBANTES ************** "+ comProv.getClaveAcceso());
				}
				System.out.println("DESPUES DE ACTUALIZAR RECEPCION: "+ comProv.getClaveAcceso() + " TIPO EMISION: "+ tipoEmision + " DETALLE_COMPROBANTE: "+comProv.getDetalleComprobante());
				
				if (claveAcceso.substring(8, 10).equals("07") && !listFeDetalleComprobanteProveedor.isEmpty()){ //inserto detalles de retenciones
					servicioFacturaLoteMasivo.eliminarDetallesComprobanteProveedor(claveAcceso);
					servicioFacturaLoteMasivo.insertaDetallesComprobantesProveedor(listFeDetalleComprobanteProveedor);
				}
				
			}
		}
		
		
    }
    
    
    //----------------------------------
  //CAMBIO OFFLINE 
  	private String tranformarAutorizacionRecepcion(com.producto.comprobanteselectronicos.autorizador.desarrollo.offline.Autorizacion autorizacion, String clase) {
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
  	
  	 public void consultarAutorizacion(String claveAcceso) throws Throwable{    	
     	RespuestaComprobante respuestaComprobante = null;
     	if (ServicioComprobacionWS.servicioAutorizacionIndividual(URLWSSRI)) {
     		respuestaComprobante = AutorizacionComprobante.consultaAutorizacionProveedor(claveAcceso, "",RespuestaComprobante.class, URLWSSRI);
     		 			
     		List<Autorizacion> autorizaciones = respuestaComprobante.getAutorizaciones().getAutorizacion();
     		Autorizacion respuestaAutorizado = null;
     		
     		for (Autorizacion aut : autorizaciones) {
				
     			if (aut.getEstado().equals(INombresParametros.RESPUESTA_RECIBIDA_AUTORIZACION_SRI)) {
					respuestaAutorizado = aut;
					
					break;
				}
			}
     		String xmlAut;
     		if (respuestaAutorizado!=null)
     		{
     			xmlAut = tranformarAutorizacionRecepcion(respuestaAutorizado,INombresParametros.CLASE_AUTORIZACION_JAXB_DESA_OFFLINE);	
     			if(xmlAut!=null && xmlAut.contains("<ns2:RespuestaAutorizacion xsi:type=\"ns2:autorizacion\""))
     				xmlAut=xmlAut.replace("<ns2:RespuestaAutorizacion xsi:type=\"ns2:autorizacion\" xmlns:ns2=\"http://ec.gob.sri.ws.autorizacion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
     						"<autorizacion>");
     				xmlAut = xmlAut.replaceAll("&lt;", "<");
     				xmlAut = xmlAut.replaceAll("&gt;", ">");
     			    xmlAut=xmlAut.replace("<comprobante>", "<comprobante><![CDATA[");
     			    xmlAut=xmlAut.replace("</comprobante>", "]]></comprobante>");
     			    xmlAut=xmlAut.replace("</ns2:RespuestaAutorizacion>", "</autorizacion>");
     			extraerDatos(xmlAut,claveAcceso);
     			
     		}else
     		{
     			System.out.println("No se encontro comprobante"+ claveAcceso);
     		}
     	}else {
 			System.err.println("NO HAY WEB SERVICE");
 		
 		}
     	
     }
  	//MBAS INI
  	/* public FeComprobanteProveedor consultarAutorizacionV2(String claveAcceso) throws Throwable{    	
      	RespuestaComprobante respuestaComprobante = null;
      	FeComprobanteProveedor lFeComprobanteProveedor=null;
      	if (ServicioComprobacionWS.servicioAutorizacionIndividual(URLWSSRI)) {
      		respuestaComprobante = AutorizacionComprobante.consultaAutorizacionProveedor(claveAcceso, "",RespuestaComprobante.class, URLWSSRI);
      		 			
      		List<Autorizacion> autorizaciones = respuestaComprobante.getAutorizaciones().getAutorizacion();
      		Autorizacion respuestaAutorizado = null;
       		for (Autorizacion aut : autorizaciones) {
 				
      			if (aut.getEstado().equals(INombresParametros.RESPUESTA_RECIBIDA_AUTORIZACION_SRI)) {
 					respuestaAutorizado = aut;
 					
 					break;
 				}
 			}
      		String xmlAut;
      		if (respuestaAutorizado!=null)
      		{
      			xmlAut = tranformarAutorizacionRecepcion(respuestaAutorizado,INombresParametros.CLASE_AUTORIZACION_JAXB_DESA_OFFLINE);	
      			if(xmlAut!=null && xmlAut.contains("<ns2:RespuestaAutorizacion xsi:type=\"ns2:autorizacion\""))
      				xmlAut=xmlAut.replace("<ns2:RespuestaAutorizacion xsi:type=\"ns2:autorizacion\" xmlns:ns2=\"http://ec.gob.sri.ws.autorizacion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">",
      						"<autorizacion>");
      				xmlAut = xmlAut.replaceAll("&lt;", "<");
      				xmlAut = xmlAut.replaceAll("&gt;", ">");
      			    xmlAut=xmlAut.replace("<comprobante>", "<comprobante><![CDATA[");
      			    xmlAut=xmlAut.replace("</comprobante>", "]]></comprobante>");
      			    xmlAut=xmlAut.replace("</ns2:RespuestaAutorizacion>", "</autorizacion>");
      			   lFeComprobanteProveedor=	extraerDatosV2(xmlAut,claveAcceso);
      			
      		}else
      		{
      			System.out.println("No se encontraron registros para la clave de acceso "+ claveAcceso+ " : "+ URLWSSRI);
      		}
      	}else {
  			System.err.println("No se encuentra disponible el Web : "+ URLWSSRI);
  		
  		}
      	
      	return lFeComprobanteProveedor;
      	
      }
  	 */
  	 //MBAS FIN
  	 public void consultarAutorizacion2(String claveAcceso) throws Throwable{   	
     
      	if (ServicioComprobacionWS.servicioAutorizacionIndividual(URLWSSRI)) {
      		RespuestaComprobante respuestaComprobante = AutorizacionComprobante.obtieneAutorizacionComprobante(claveAcceso, "2", RespuestaComprobante.class, URLWSSRI);			
			List<Autorizacion> autorizaciones = respuestaComprobante.getAutorizaciones().getAutorizacion();
			Autorizacion respuestaAutorizado = null;
			Autorizacion respuestaXA = null;
			for (Autorizacion aut : autorizaciones) {
				respuestaXA = autorizaciones.get(0);
				if (aut.getEstado().equals(INombresParametros.RESPUESTA_RECIBIDA_AUTORIZACION_SRI)) {
					respuestaAutorizado = aut;
				}				
			}
			if (respuestaAutorizado != null) {			
				
				String xmlAut = tranformarAutorizacionRecepcion(respuestaAutorizado,INombresParametros.CLASE_AUTORIZACION_JAXB_DESA_OFFLINE);
				extraerDatos( xmlAut,  claveAcceso);
			}
				
      	}else {
  			System.err.println("NO HAY WEB SERVICE");
  		
  		}
      	
      }
     	
  	private static Document crearRideFacturaXML(String factura)
			throws ParserConfigurationException, JAXBException,
			TransformerException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(new InputSource(new StringReader(
				factura)));
		
	}
  	
  	public static Double validaNulosDouble(Double pValor){
  		
  		if(pValor!=null){
  			return pValor;
  		}else{
  			pValor=0.0;  			
  		}
  		return pValor;
  		
  	}

  	 
}
