//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.07.30 a las 11:15:29 AM COT 
//

package com.libreria.ofe.modelo.xsd.v200.factura;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contiene la informacion del destinatario
 * 
 * <p>
 * Clase Java para destino complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="destino"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="motivoTraslado" type="{}motivoTraslado"/&gt;
 *         &lt;element name="docAduaneroUnico" type="{}docAduaneroUnico" minOccurs="0"/&gt;
 *         &lt;element name="codEstabDestino" type="{}establecimiento" minOccurs="0"/&gt;
 *         &lt;element name="ruta" type="{}ruta" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "destino", namespace = "", propOrder = { "motivoTraslado", "docAduaneroUnico", "codEstabDestino",
        "ruta" })
public class Destino implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected String motivoTraslado;
    protected String docAduaneroUnico;
    protected String codEstabDestino;
    protected String ruta;

    /**
     * Obtiene el valor de la propiedad motivoTraslado.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getMotivoTraslado() {
        return motivoTraslado;
    }

    /**
     * Define el valor de la propiedad motivoTraslado.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setMotivoTraslado(String value) {
        this.motivoTraslado = value;
    }

    /**
     * Obtiene el valor de la propiedad docAduaneroUnico.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDocAduaneroUnico() {
        return docAduaneroUnico;
    }

    /**
     * Define el valor de la propiedad docAduaneroUnico.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDocAduaneroUnico(String value) {
        this.docAduaneroUnico = value;
    }

    /**
     * Obtiene el valor de la propiedad codEstabDestino.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getCodEstabDestino() {
        return codEstabDestino;
    }

    /**
     * Define el valor de la propiedad codEstabDestino.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setCodEstabDestino(String value) {
        this.codEstabDestino = value;
    }

    /**
     * Obtiene el valor de la propiedad ruta.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRuta() {
        return ruta;
    }

    /**
     * Define el valor de la propiedad ruta.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setRuta(String value) {
        this.ruta = value;
    }

}
