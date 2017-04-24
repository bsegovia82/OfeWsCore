//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.07.30 a las 11:15:29 AM COT 
//

package com.libreria.ofe.modelo.xsd.v200.factura;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contiene la informacion del rubro
 * 
 * <p>
 * Clase Java para rubro complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="rubro"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="concepto" type="{}concepto"/&gt;
 *         &lt;element name="total" type="{}total"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rubro", namespace = "", propOrder = { "concepto", "total" })
public class Rubro implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected String concepto;
    @XmlElement(required = true)
    protected BigDecimal total;

    /**
     * Obtiene el valor de la propiedad concepto.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * Define el valor de la propiedad concepto.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setConcepto(String value) {
        this.concepto = value;
    }

    /**
     * Obtiene el valor de la propiedad total.
     * 
     * @return possible object is {@link BigDecimal }
     * 
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Define el valor de la propiedad total.
     * 
     * @param value
     *            allowed object is {@link BigDecimal }
     * 
     */
    public void setTotal(BigDecimal value) {
        this.total = value;
    }

}
