//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.07.30 a las 11:05:06 AM COT 
//

package com.libreria.ofe.modelo.xsd.v110.notacredito;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contiene la informacion de las compensaciones
 * 
 * <p>
 * Clase Java para compensacion complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="compensacion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigo" type="{}codigoPorcentajeCompensacion"/&gt;
 *         &lt;element name="tarifa" type="{}tarifa"/&gt;
 *         &lt;element name="valor" type="{}valor"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "compensacion", namespace = "", propOrder = { "codigo", "tarifa", "valor" })
public class Compensacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected String codigo;
    @XmlElement(required = true)
    protected BigDecimal tarifa;
    @XmlElement(required = true)
    protected BigDecimal valor;

    /**
     * Obtiene el valor de la propiedad codigo.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Define el valor de la propiedad codigo.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setCodigo(String value) {
        this.codigo = value;
    }

    /**
     * Obtiene el valor de la propiedad tarifa.
     * 
     * @return possible object is {@link BigDecimal }
     * 
     */
    public BigDecimal getTarifa() {
        return tarifa;
    }

    /**
     * Define el valor de la propiedad tarifa.
     * 
     * @param value
     *            allowed object is {@link BigDecimal }
     * 
     */
    public void setTarifa(BigDecimal value) {
        this.tarifa = value;
    }

    /**
     * Obtiene el valor de la propiedad valor.
     * 
     * @return possible object is {@link BigDecimal }
     * 
     */
    public BigDecimal getValor() {
        return valor;
    }

    /**
     * Define el valor de la propiedad valor.
     * 
     * @param value
     *            allowed object is {@link BigDecimal }
     * 
     */
    public void setValor(BigDecimal value) {
        this.valor = value;
    }

}
