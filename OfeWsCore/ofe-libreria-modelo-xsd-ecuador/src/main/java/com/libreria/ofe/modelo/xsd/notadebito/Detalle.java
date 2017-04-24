//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantaci�n de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perder�n si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.07.30 a las 11:14:19 AM COT 
//

package com.libreria.ofe.modelo.xsd.notadebito;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Detalle de una nota de credito.
 * 
 * <p>
 * Clase Java para detalle complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="detalle"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="motivoModificacion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "detalle", namespace = "", propOrder = { "motivoModificacion" })
public class Detalle implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected String motivoModificacion;

    /**
     * Obtiene el valor de la propiedad motivoModificacion.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getMotivoModificacion() {
        return motivoModificacion;
    }

    /**
     * Define el valor de la propiedad motivoModificacion.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setMotivoModificacion(String value) {
        this.motivoModificacion = value;
    }

}
