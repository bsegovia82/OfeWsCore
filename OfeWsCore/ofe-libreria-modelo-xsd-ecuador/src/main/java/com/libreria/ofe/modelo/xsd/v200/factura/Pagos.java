//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.07.30 a las 11:15:29 AM COT 
//

package com.libreria.ofe.modelo.xsd.v200.factura;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para pagos complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="pagos"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="pago" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="formaPago" type="{}formaPago"/&gt;
 *                   &lt;element name="total" type="{}total"/&gt;
 *                   &lt;element name="plazo" type="{}plazo" minOccurs="0"/&gt;
 *                   &lt;element name="unidadTiempo" type="{}unidadTiempo" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagos", namespace = "", propOrder = { "pago" })
public class Pagos implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected List<Pagos.Pago> pago;

    /**
     * Gets the value of the pago property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the pago property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getPago().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pagos.Pago }
     * 
     * 
     */
    public List<Pagos.Pago> getPago() {
        if (pago == null) {
            pago = new ArrayList<Pagos.Pago>();
        }
        return this.pago;
    }

    /**
     * <p>
     * Clase Java para anonymous complex type.
     * 
     * <p>
     * El siguiente fragmento de esquema especifica el contenido que se espera
     * que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="formaPago" type="{}formaPago"/&gt;
     *         &lt;element name="total" type="{}total"/&gt;
     *         &lt;element name="plazo" type="{}plazo" minOccurs="0"/&gt;
     *         &lt;element name="unidadTiempo" type="{}unidadTiempo" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "formaPago", "total", "plazo", "unidadTiempo" })
    public static class Pago implements Serializable {

        private static final long serialVersionUID = 1L;

        @XmlElement(required = true)
        protected String formaPago;
        @XmlElement(required = true)
        protected BigDecimal total;
        protected BigDecimal plazo;
        protected String unidadTiempo;

        /**
         * Obtiene el valor de la propiedad formaPago.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getFormaPago() {
            return formaPago;
        }

        /**
         * Define el valor de la propiedad formaPago.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setFormaPago(String value) {
            this.formaPago = value;
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

        /**
         * Obtiene el valor de la propiedad plazo.
         * 
         * @return possible object is {@link BigDecimal }
         * 
         */
        public BigDecimal getPlazo() {
            return plazo;
        }

        /**
         * Define el valor de la propiedad plazo.
         * 
         * @param value
         *            allowed object is {@link BigDecimal }
         * 
         */
        public void setPlazo(BigDecimal value) {
            this.plazo = value;
        }

        /**
         * Obtiene el valor de la propiedad unidadTiempo.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getUnidadTiempo() {
            return unidadTiempo;
        }

        /**
         * Define el valor de la propiedad unidadTiempo.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setUnidadTiempo(String value) {
            this.unidadTiempo = value;
        }

    }

}
