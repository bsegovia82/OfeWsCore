//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantaci�n de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perder�n si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.07.30 a las 10:57:05 AM COT 
//

package com.libreria.ofe.modelo.xsd.v210.factura;

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
 * Clase Java para detalleImpuestos complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="detalleImpuestos"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="detalleImpuesto" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="codigo" type="{}codigoReembolso"/&gt;
 *                   &lt;element name="codigoPorcentaje" type="{}codigoPorcentajeReembolso"/&gt;
 *                   &lt;element name="tarifa" type="{}tarifaReembolso"/&gt;
 *                   &lt;element name="baseImponibleReembolso" type="{}baseImponibleReembolso"/&gt;
 *                   &lt;element name="impuestoReembolso" type="{}impuestoReembolso"/&gt;
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
@XmlType(name = "detalleImpuestos", namespace = "", propOrder = { "detalleImpuesto" })
public class DetalleImpuestos implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected List<DetalleImpuestos.DetalleImpuesto> detalleImpuesto;

    /**
     * Gets the value of the detalleImpuesto property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the detalleImpuesto property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getDetalleImpuesto().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DetalleImpuestos.DetalleImpuesto }
     * 
     * 
     */
    public List<DetalleImpuestos.DetalleImpuesto> getDetalleImpuesto() {
        if (detalleImpuesto == null) {
            detalleImpuesto = new ArrayList<DetalleImpuestos.DetalleImpuesto>();
        }
        return this.detalleImpuesto;
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
     *         &lt;element name="codigo" type="{}codigoReembolso"/&gt;
     *         &lt;element name="codigoPorcentaje" type="{}codigoPorcentajeReembolso"/&gt;
     *         &lt;element name="tarifa" type="{}tarifaReembolso"/&gt;
     *         &lt;element name="baseImponibleReembolso" type="{}baseImponibleReembolso"/&gt;
     *         &lt;element name="impuestoReembolso" type="{}impuestoReembolso"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "codigo", "codigoPorcentaje", "tarifa", "baseImponibleReembolso",
            "impuestoReembolso" })
    public static class DetalleImpuesto implements Serializable {
        private static final long serialVersionUID = 1L;

        @XmlElement(required = true)
        protected String codigo;
        @XmlElement(required = true)
        protected String codigoPorcentaje;
        @XmlElement(required = true)
        protected String tarifa;
        @XmlElement(required = true)
        protected BigDecimal baseImponibleReembolso;
        @XmlElement(required = true)
        protected BigDecimal impuestoReembolso;

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
         * Obtiene el valor de la propiedad codigoPorcentaje.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getCodigoPorcentaje() {
            return codigoPorcentaje;
        }

        /**
         * Define el valor de la propiedad codigoPorcentaje.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setCodigoPorcentaje(String value) {
            this.codigoPorcentaje = value;
        }

        /**
         * Obtiene el valor de la propiedad tarifa.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTarifa() {
            return tarifa;
        }

        /**
         * Define el valor de la propiedad tarifa.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTarifa(String value) {
            this.tarifa = value;
        }

        /**
         * Obtiene el valor de la propiedad baseImponibleReembolso.
         * 
         * @return possible object is {@link BigDecimal }
         * 
         */
        public BigDecimal getBaseImponibleReembolso() {
            return baseImponibleReembolso;
        }

        /**
         * Define el valor de la propiedad baseImponibleReembolso.
         * 
         * @param value
         *            allowed object is {@link BigDecimal }
         * 
         */
        public void setBaseImponibleReembolso(BigDecimal value) {
            this.baseImponibleReembolso = value;
        }

        /**
         * Obtiene el valor de la propiedad impuestoReembolso.
         * 
         * @return possible object is {@link BigDecimal }
         * 
         */
        public BigDecimal getImpuestoReembolso() {
            return impuestoReembolso;
        }

        /**
         * Define el valor de la propiedad impuestoReembolso.
         * 
         * @param value
         *            allowed object is {@link BigDecimal }
         * 
         */
        public void setImpuestoReembolso(BigDecimal value) {
            this.impuestoReembolso = value;
        }

    }

}
