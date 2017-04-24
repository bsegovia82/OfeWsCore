package com.producto.comprobanteselectronicos.servicio.logicanegocio;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.producto.comprobanteselectronicos.utilerias.seguridad.UtilCryptography;

import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.EnumFormatoFirma;
import es.mityc.javasign.pkstore.IPKStoreManager;
import es.mityc.javasign.pkstore.IPassStoreKS;
import es.mityc.javasign.pkstore.keystore.KSStore;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;

public class ServicioFirma {

	public static String firmarDocumentoXML(Document document,
			String claveAccesoDocumento, String rutaArchivoCertificado,
			String password, String rutaArchivoResultante,
			String descripcionNodo, String observacion, Integer maxSize, boolean isNew) throws Throwable {
		if(isNew){
			return firmaXMLNuevo(document, claveAccesoDocumento, rutaArchivoCertificado,
				password, rutaArchivoResultante, descripcionNodo, observacion, maxSize);
		}else{
			return firmaXMLReproceso(document, claveAccesoDocumento, rutaArchivoCertificado,
					password, rutaArchivoResultante, descripcionNodo, observacion, maxSize);
		}
	}

	private static String firmaXMLNuevo(Document document, String claveAcceso,
			String rutaArchivo, String password, String archivoResultante,
			String descripcionNodo, String observacion, Integer MaxSize) throws Exception {
		
		String claveDesencriptada = UtilCryptography.desencriptar(password);
		final char[] PKCS12_PASSWORD = claveDesencriptada.toCharArray();		
		KeyStore ks = KeyStore.getInstance("PKCS12");
		String rutaCertificado = rutaArchivo;
		ks.load(new FileInputStream(rutaCertificado), PKCS12_PASSWORD);
		IPKStoreManager storeManager = new KSStore(ks, new IPassStoreKS() {
			@Override
			public char[] getPassword(X509Certificate certificate, String alias) {
				return PKCS12_PASSWORD;
			}
		});

		X509Certificate certificate = storeManager.getSignCertificates().get(0);
		PrivateKey privateKey = storeManager.getPrivateKey(certificate);
		Provider provider = storeManager.getProvider(certificate);

		DataToSign dataToSign = createDataToSign(document,
				descripcionNodo/* lote */, observacion/* loteMasivo */);// loteMasivoPrueba
		FirmaXML firma = new FirmaXML();
		Object[] res = firma.signFile(certificate, dataToSign, privateKey,provider);
		Document documentSigned = (Document) res[0];

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(documentSigned);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(baos);
		transformer.transform(source, result);
		
		byte[] b = baos.toByteArray();
		
		int tamBytes = b.length/1024;
		
		if(tamBytes >= MaxSize) return null;

		File file = new File(archivoResultante);

		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
//		FileOutputStream fo = new FileOutputStream(file);
//		fo.write(b);
//		fo.flush();
//		fo.close();
		
		CharsetEncoder encoder = StandardCharsets.ISO_8859_1.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.IGNORE);
		encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos,encoder);
		BufferedWriter out = new BufferedWriter(osw);
		
		String archivoFirmado = new String(b,StandardCharsets.ISO_8859_1);
		out.write(archivoFirmado);
		out.flush();
		out.close();
		fos.flush();
		fos.close();
		
		return new String(b);
	}
	
	private static String firmaXMLReproceso(Document document, String claveAcceso,
			String rutaArchivo, String password, String archivoResultante,
			String descripcionNodo, String observacion, Integer MaxSize) throws Exception {
		
		String claveDesencriptada = UtilCryptography.desencriptar(password);
		final char[] PKCS12_PASSWORD = claveDesencriptada.toCharArray();		
		KeyStore ks = KeyStore.getInstance("PKCS12");
		String rutaCertificado = rutaArchivo;
		ks.load(new FileInputStream(rutaCertificado), PKCS12_PASSWORD);
		IPKStoreManager storeManager = new KSStore(ks, new IPassStoreKS() {
			@Override
			public char[] getPassword(X509Certificate certificate, String alias) {
				return PKCS12_PASSWORD;
			}
		});

		X509Certificate certificate = storeManager.getSignCertificates().get(0);
		PrivateKey privateKey = storeManager.getPrivateKey(certificate);
		Provider provider = storeManager.getProvider(certificate);
		DataToSign dataToSign = createDataToSign(document,
				descripcionNodo/* lote */, observacion/* loteMasivo */);// loteMasivoPrueba
		FirmaXML firma = new FirmaXML();
		Object[] res = firma.signFile(certificate, dataToSign, privateKey, provider);
		Document documentSigned = (Document) res[0];
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(documentSigned);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(baos);
		transformer.transform(source, result);
		
		byte[] b = baos.toByteArray();
		
		int tamBytes = b.length/1024;
		
		if(tamBytes >= MaxSize) return null;

		File file = new File(archivoResultante);

		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileOutputStream fo = new FileOutputStream(file);
		fo.write(b);
		fo.flush();
		fo.close();
		
		return new String(b);
	}

	private static DataToSign createDataToSign(Document document, String nodoFirmar, String descripcionNodoFirmar)
			throws ParserConfigurationException, JAXBException {
		DataToSign dataToSign = new DataToSign();
		dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
		dataToSign.setEsquema(XAdESSchemas.XAdES_132);
		dataToSign.setXMLEncoding("UTF-8");
		dataToSign.setEnveloped(true);
		dataToSign.addObject(new ObjectToSign(
				new InternObjectToSign(nodoFirmar), descripcionNodoFirmar,
				null, "text/xml", null));
		dataToSign.setDocument(document);
		return dataToSign;
	}

}
