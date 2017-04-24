//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.07.30 a las 11:05:06 AM COT 
//

package com.libreria.ofe.modelo.xsd.v110.notacredito;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Clase Java para anonymous complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="infoTributaria" type="{}infoTributaria"/&gt;
 *         &lt;element name="infoNotaCredito"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="fechaEmision" type="{}fechaEmision"/&gt;
 *                   &lt;element name="dirEstablecimiento" type="{}dirEstablecimiento" minOccurs="0"/&gt;
 *                   &lt;element name="tipoIdentificacionComprador" type="{}tipoIdentificacionComprador"/&gt;
 *                   &lt;element name="razonSocialComprador" type="{}razonSocialComprador"/&gt;
 *                   &lt;element name="identificacionComprador" type="{}identificacionComprador"/&gt;
 *                   &lt;element name="contribuyenteEspecial" type="{}contribuyenteEspecial" minOccurs="0"/&gt;
 *                   &lt;element name="obligadoContabilidad" type="{}obligadoContabilidad" minOccurs="0"/&gt;
 *                   &lt;element name="rise" type="{}rise" minOccurs="0"/&gt;
 *                   &lt;element name="codDocModificado" type="{}codDocModificado"/&gt;
 *                   &lt;element name="numDocModificado" type="{}numDocModificado"/&gt;
 *                   &lt;element name="fechaEmisionDocSustento" type="{}fechaEmisionDocSustento"/&gt;
 *                   &lt;element name="totalSinImpuestos" type="{}totalSinImpuestos"/&gt;
 *                   &lt;element ref="{}compensaciones" minOccurs="0"/&gt;
 *                   &lt;element name="valorModificacion" type="{}valorModificacion"/&gt;
 *                   &lt;element name="moneda" type="{}moneda" minOccurs="0"/&gt;
 *                   &lt;element ref="{}totalConImpuestos"/&gt;
 *                   &lt;element name="motivo" type="{}motivo"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="detalles"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="detalle" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="codigoInterno" type="{}codigoInterno" minOccurs="0"/&gt;
 *                             &lt;element name="codigoAdicional" type="{}codigoAdicional" minOccurs="0"/&gt;
 *                             &lt;element name="descripcion" type="{}descripcion"/&gt;
 *                             &lt;element name="cantidad" type="{}cantidad"/&gt;
 *                             &lt;element name="precioUnitario" type="{}precioUnitario"/&gt;
 *                             &lt;element name="descuento" type="{}descuento" minOccurs="0"/&gt;
 *                             &lt;element name="precioTotalSinImpuesto" type="{}precioTotalSinImpuesto"/&gt;
 *                             &lt;element name="detallesAdicionales" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="detAdicional" maxOccurs="3"&gt;
 *                                         &lt;complexType&gt;
 *                                           &lt;complexContent&gt;
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                               &lt;attribute name="nombre" use="required"&gt;
 *                                                 &lt;simpleType&gt;
 *                                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                                                     &lt;minLength value="1"/&gt;
 *                                                     &lt;maxLength value="300"/&gt;
 *                                                   &lt;/restriction&gt;
 *                                                 &lt;/simpleType&gt;
 *                                               &lt;/attribute&gt;
 *                                               &lt;attribute name="valor" use="required"&gt;
 *                                                 &lt;simpleType&gt;
 *                                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                                                     &lt;pattern value="[^\n]*"/&gt;
 *                                                     &lt;minLength value="1"/&gt;
 *                                                     &lt;maxLength value="300"/&gt;
 *                                                   &lt;/restriction&gt;
 *                                                 &lt;/simpleType&gt;
 *                                               &lt;/attribute&gt;
 *                                             &lt;/restriction&gt;
 *                                           &lt;/complexContent&gt;
 *                                         &lt;/complexType&gt;
 *                                       &lt;/element&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                             &lt;element name="impuestos"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="impuesto" type="{}impuesto" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                                     &lt;/sequence&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="infoAdicional" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="campoAdicional" maxOccurs="15"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;simpleContent&gt;
 *                         &lt;extension base="&lt;&gt;campoAdicional"&gt;
 *                           &lt;attribute name="nombre" use="required" type="{}nombre" /&gt;
 *                         &lt;/extension&gt;
 *                       &lt;/simpleContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="comprobante"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "infoTributaria", "infoNotaCredito", "detalles", "infoAdicional", "signature" })
@XmlRootElement(name = "notaCredito", namespace = "")
public class NotaCredito implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected InfoTributaria infoTributaria;
    @XmlElement(required = true)
    protected NotaCredito.InfoNotaCredito infoNotaCredito;
    @XmlElement(required = true)
    protected NotaCredito.Detalles detalles;
    protected NotaCredito.InfoAdicional infoAdicional;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "version", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String version;

    /**
     * Obtiene el valor de la propiedad infoTributaria.
     * 
     * @return possible object is {@link InfoTributaria }
     * 
     */
    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    /**
     * Define el valor de la propiedad infoTributaria.
     * 
     * @param value
     *            allowed object is {@link InfoTributaria }
     * 
     */
    public void setInfoTributaria(InfoTributaria value) {
        this.infoTributaria = value;
    }

    /**
     * Obtiene el valor de la propiedad infoNotaCredito.
     * 
     * @return possible object is {@link NotaCredito.InfoNotaCredito }
     * 
     */
    public NotaCredito.InfoNotaCredito getInfoNotaCredito() {
        return infoNotaCredito;
    }

    /**
     * Define el valor de la propiedad infoNotaCredito.
     * 
     * @param value
     *            allowed object is {@link NotaCredito.InfoNotaCredito }
     * 
     */
    public void setInfoNotaCredito(NotaCredito.InfoNotaCredito value) {
        this.infoNotaCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad detalles.
     * 
     * @return possible object is {@link NotaCredito.Detalles }
     * 
     */
    public NotaCredito.Detalles getDetalles() {
        return detalles;
    }

    /**
     * Define el valor de la propiedad detalles.
     * 
     * @param value
     *            allowed object is {@link NotaCredito.Detalles }
     * 
     */
    public void setDetalles(NotaCredito.Detalles value) {
        this.detalles = value;
    }

    /**
     * Obtiene el valor de la propiedad infoAdicional.
     * 
     * @return possible object is {@link NotaCredito.InfoAdicional }
     * 
     */
    public NotaCredito.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    /**
     * Define el valor de la propiedad infoAdicional.
     * 
     * @param value
     *            allowed object is {@link NotaCredito.InfoAdicional }
     * 
     */
    public void setInfoAdicional(NotaCredito.InfoAdicional value) {
        this.infoAdicional = value;
    }

    /**
     * Conjunto de datos asociados a la factura que garantizarán la autoría y la
     * integridad del mensaje. Se define como opcional para facilitar la
     * verificación y el tránsito del fichero. No obstante, debe cumplimentarse
     * este bloque de firma electrónica para que se considere una factura
     * electrónica válida legalmente frente a terceros.
     * 
     * @return possible object is {@link SignatureType }
     * 
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Define el valor de la propiedad signature.
     * 
     * @param value
     *            allowed object is {@link SignatureType }
     * 
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad version.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getVersion() {
        return version;
    }

    /**
     * Define el valor de la propiedad version.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setVersion(String value) {
        this.version = value;
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
     *         &lt;element name="detalle" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="codigoInterno" type="{}codigoInterno" minOccurs="0"/&gt;
     *                   &lt;element name="codigoAdicional" type="{}codigoAdicional" minOccurs="0"/&gt;
     *                   &lt;element name="descripcion" type="{}descripcion"/&gt;
     *                   &lt;element name="cantidad" type="{}cantidad"/&gt;
     *                   &lt;element name="precioUnitario" type="{}precioUnitario"/&gt;
     *                   &lt;element name="descuento" type="{}descuento" minOccurs="0"/&gt;
     *                   &lt;element name="precioTotalSinImpuesto" type="{}precioTotalSinImpuesto"/&gt;
     *                   &lt;element name="detallesAdicionales" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="detAdicional" maxOccurs="3"&gt;
     *                               &lt;complexType&gt;
     *                                 &lt;complexContent&gt;
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                                     &lt;attribute name="nombre" use="required"&gt;
     *                                       &lt;simpleType&gt;
     *                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *                                           &lt;minLength value="1"/&gt;
     *                                           &lt;maxLength value="300"/&gt;
     *                                         &lt;/restriction&gt;
     *                                       &lt;/simpleType&gt;
     *                                     &lt;/attribute&gt;
     *                                     &lt;attribute name="valor" use="required"&gt;
     *                                       &lt;simpleType&gt;
     *                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *                                           &lt;pattern value="[^\n]*"/&gt;
     *                                           &lt;minLength value="1"/&gt;
     *                                           &lt;maxLength value="300"/&gt;
     *                                         &lt;/restriction&gt;
     *                                       &lt;/simpleType&gt;
     *                                     &lt;/attribute&gt;
     *                                   &lt;/restriction&gt;
     *                                 &lt;/complexContent&gt;
     *                               &lt;/complexType&gt;
     *                             &lt;/element&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                   &lt;element name="impuestos"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="impuesto" type="{}impuesto" maxOccurs="unbounded" minOccurs="0"/&gt;
     *                           &lt;/sequence&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
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
    @XmlType(name = "", propOrder = { "detalle" })
    public static class Detalles implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @XmlElement(required = true)
        protected List<NotaCredito.Detalles.Detalle> detalle;

        /**
         * Gets the value of the detalle property.
         * 
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the detalle property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getDetalle().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NotaCredito.Detalles.Detalle }
         * 
         * 
         */
        public List<NotaCredito.Detalles.Detalle> getDetalle() {
            if (detalle == null) {
                detalle = new ArrayList<NotaCredito.Detalles.Detalle>();
            }
            return this.detalle;
        }

        /**
         * <p>
         * Clase Java para anonymous complex type.
         * 
         * <p>
         * El siguiente fragmento de esquema especifica el contenido que se
         * espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="codigoInterno" type="{}codigoInterno" minOccurs="0"/&gt;
         *         &lt;element name="codigoAdicional" type="{}codigoAdicional" minOccurs="0"/&gt;
         *         &lt;element name="descripcion" type="{}descripcion"/&gt;
         *         &lt;element name="cantidad" type="{}cantidad"/&gt;
         *         &lt;element name="precioUnitario" type="{}precioUnitario"/&gt;
         *         &lt;element name="descuento" type="{}descuento" minOccurs="0"/&gt;
         *         &lt;element name="precioTotalSinImpuesto" type="{}precioTotalSinImpuesto"/&gt;
         *         &lt;element name="detallesAdicionales" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="detAdicional" maxOccurs="3"&gt;
         *                     &lt;complexType&gt;
         *                       &lt;complexContent&gt;
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                           &lt;attribute name="nombre" use="required"&gt;
         *                             &lt;simpleType&gt;
         *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
         *                                 &lt;minLength value="1"/&gt;
         *                                 &lt;maxLength value="300"/&gt;
         *                               &lt;/restriction&gt;
         *                             &lt;/simpleType&gt;
         *                           &lt;/attribute&gt;
         *                           &lt;attribute name="valor" use="required"&gt;
         *                             &lt;simpleType&gt;
         *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
         *                                 &lt;pattern value="[^\n]*"/&gt;
         *                                 &lt;minLength value="1"/&gt;
         *                                 &lt;maxLength value="300"/&gt;
         *                               &lt;/restriction&gt;
         *                             &lt;/simpleType&gt;
         *                           &lt;/attribute&gt;
         *                         &lt;/restriction&gt;
         *                       &lt;/complexContent&gt;
         *                     &lt;/complexType&gt;
         *                   &lt;/element&gt;
         *                 &lt;/sequence&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *         &lt;element name="impuestos"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="impuesto" type="{}impuesto" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        @XmlType(name = "", propOrder = { "codigoInterno", "codigoAdicional", "descripcion", "cantidad",
                "precioUnitario", "descuento", "precioTotalSinImpuesto", "detallesAdicionales", "impuestos" })
        public static class Detalle implements Serializable {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            protected String codigoInterno;
            protected String codigoAdicional;
            @XmlElement(required = true)
            protected String descripcion;
            @XmlElement(required = true)
            protected BigDecimal cantidad;
            @XmlElement(required = true)
            protected BigDecimal precioUnitario;
            protected BigDecimal descuento;
            @XmlElement(required = true)
            protected BigDecimal precioTotalSinImpuesto;
            protected NotaCredito.Detalles.Detalle.DetallesAdicionales detallesAdicionales;
            @XmlElement(required = true)
            protected NotaCredito.Detalles.Detalle.Impuestos impuestos;

            /**
             * Obtiene el valor de la propiedad codigoInterno.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getCodigoInterno() {
                return codigoInterno;
            }

            /**
             * Define el valor de la propiedad codigoInterno.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setCodigoInterno(String value) {
                this.codigoInterno = value;
            }

            /**
             * Obtiene el valor de la propiedad codigoAdicional.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getCodigoAdicional() {
                return codigoAdicional;
            }

            /**
             * Define el valor de la propiedad codigoAdicional.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setCodigoAdicional(String value) {
                this.codigoAdicional = value;
            }

            /**
             * Obtiene el valor de la propiedad descripcion.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getDescripcion() {
                return descripcion;
            }

            /**
             * Define el valor de la propiedad descripcion.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setDescripcion(String value) {
                this.descripcion = value;
            }

            /**
             * Obtiene el valor de la propiedad cantidad.
             * 
             * @return possible object is {@link BigDecimal }
             * 
             */
            public BigDecimal getCantidad() {
                return cantidad;
            }

            /**
             * Define el valor de la propiedad cantidad.
             * 
             * @param value
             *            allowed object is {@link BigDecimal }
             * 
             */
            public void setCantidad(BigDecimal value) {
                this.cantidad = value;
            }

            /**
             * Obtiene el valor de la propiedad precioUnitario.
             * 
             * @return possible object is {@link BigDecimal }
             * 
             */
            public BigDecimal getPrecioUnitario() {
                return precioUnitario;
            }

            /**
             * Define el valor de la propiedad precioUnitario.
             * 
             * @param value
             *            allowed object is {@link BigDecimal }
             * 
             */
            public void setPrecioUnitario(BigDecimal value) {
                this.precioUnitario = value;
            }

            /**
             * Obtiene el valor de la propiedad descuento.
             * 
             * @return possible object is {@link BigDecimal }
             * 
             */
            public BigDecimal getDescuento() {
                return descuento;
            }

            /**
             * Define el valor de la propiedad descuento.
             * 
             * @param value
             *            allowed object is {@link BigDecimal }
             * 
             */
            public void setDescuento(BigDecimal value) {
                this.descuento = value;
            }

            /**
             * Obtiene el valor de la propiedad precioTotalSinImpuesto.
             * 
             * @return possible object is {@link BigDecimal }
             * 
             */
            public BigDecimal getPrecioTotalSinImpuesto() {
                return precioTotalSinImpuesto;
            }

            /**
             * Define el valor de la propiedad precioTotalSinImpuesto.
             * 
             * @param value
             *            allowed object is {@link BigDecimal }
             * 
             */
            public void setPrecioTotalSinImpuesto(BigDecimal value) {
                this.precioTotalSinImpuesto = value;
            }

            /**
             * Obtiene el valor de la propiedad detallesAdicionales.
             * 
             * @return possible object is
             *         {@link NotaCredito.Detalles.Detalle.DetallesAdicionales }
             * 
             */
            public NotaCredito.Detalles.Detalle.DetallesAdicionales getDetallesAdicionales() {
                return detallesAdicionales;
            }

            /**
             * Define el valor de la propiedad detallesAdicionales.
             * 
             * @param value
             *            allowed object is
             *            {@link NotaCredito.Detalles.Detalle.DetallesAdicionales }
             * 
             */
            public void setDetallesAdicionales(NotaCredito.Detalles.Detalle.DetallesAdicionales value) {
                this.detallesAdicionales = value;
            }

            /**
             * Obtiene el valor de la propiedad impuestos.
             * 
             * @return possible object is
             *         {@link NotaCredito.Detalles.Detalle.Impuestos }
             * 
             */
            public NotaCredito.Detalles.Detalle.Impuestos getImpuestos() {
                return impuestos;
            }

            /**
             * Define el valor de la propiedad impuestos.
             * 
             * @param value
             *            allowed object is
             *            {@link NotaCredito.Detalles.Detalle.Impuestos }
             * 
             */
            public void setImpuestos(NotaCredito.Detalles.Detalle.Impuestos value) {
                this.impuestos = value;
            }

            /**
             * <p>
             * Clase Java para anonymous complex type.
             * 
             * <p>
             * El siguiente fragmento de esquema especifica el contenido que se
             * espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType&gt;
             *   &lt;complexContent&gt;
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *       &lt;sequence&gt;
             *         &lt;element name="detAdicional" maxOccurs="3"&gt;
             *           &lt;complexType&gt;
             *             &lt;complexContent&gt;
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                 &lt;attribute name="nombre" use="required"&gt;
             *                   &lt;simpleType&gt;
             *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                       &lt;minLength value="1"/&gt;
             *                       &lt;maxLength value="300"/&gt;
             *                     &lt;/restriction&gt;
             *                   &lt;/simpleType&gt;
             *                 &lt;/attribute&gt;
             *                 &lt;attribute name="valor" use="required"&gt;
             *                   &lt;simpleType&gt;
             *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                       &lt;pattern value="[^\n]*"/&gt;
             *                       &lt;minLength value="1"/&gt;
             *                       &lt;maxLength value="300"/&gt;
             *                     &lt;/restriction&gt;
             *                   &lt;/simpleType&gt;
             *                 &lt;/attribute&gt;
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
            @XmlType(name = "", propOrder = { "detAdicional" })
            public static class DetallesAdicionales implements Serializable {

                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                @XmlElement(required = true)
                protected List<NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional> detAdicional;

                /**
                 * Gets the value of the detAdicional property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object. This is
                 * why there is not a <CODE>set</CODE> method for the
                 * detAdicional property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * 
                 * <pre>
                 * getDetAdicional().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional }
                 * 
                 * 
                 */
                public List<NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional> getDetAdicional() {
                    if (detAdicional == null) {
                        detAdicional = new ArrayList<NotaCredito.Detalles.Detalle.DetallesAdicionales.DetAdicional>();
                    }
                    return this.detAdicional;
                }

                /**
                 * <p>
                 * Clase Java para anonymous complex type.
                 * 
                 * <p>
                 * El siguiente fragmento de esquema especifica el contenido que
                 * se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType&gt;
                 *   &lt;complexContent&gt;
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                 *       &lt;attribute name="nombre" use="required"&gt;
                 *         &lt;simpleType&gt;
                 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *             &lt;minLength value="1"/&gt;
                 *             &lt;maxLength value="300"/&gt;
                 *           &lt;/restriction&gt;
                 *         &lt;/simpleType&gt;
                 *       &lt;/attribute&gt;
                 *       &lt;attribute name="valor" use="required"&gt;
                 *         &lt;simpleType&gt;
                 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *             &lt;pattern value="[^\n]*"/&gt;
                 *             &lt;minLength value="1"/&gt;
                 *             &lt;maxLength value="300"/&gt;
                 *           &lt;/restriction&gt;
                 *         &lt;/simpleType&gt;
                 *       &lt;/attribute&gt;
                 *     &lt;/restriction&gt;
                 *   &lt;/complexContent&gt;
                 * &lt;/complexType&gt;
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class DetAdicional implements Serializable {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = 1L;

                    @XmlAttribute(name = "nombre", required = true)
                    protected String nombre;
                    @XmlAttribute(name = "valor", required = true)
                    protected String valor;

                    /**
                     * Obtiene el valor de la propiedad nombre.
                     * 
                     * @return possible object is {@link String }
                     * 
                     */
                    public String getNombre() {
                        return nombre;
                    }

                    /**
                     * Define el valor de la propiedad nombre.
                     * 
                     * @param value
                     *            allowed object is {@link String }
                     * 
                     */
                    public void setNombre(String value) {
                        this.nombre = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad valor.
                     * 
                     * @return possible object is {@link String }
                     * 
                     */
                    public String getValor() {
                        return valor;
                    }

                    /**
                     * Define el valor de la propiedad valor.
                     * 
                     * @param value
                     *            allowed object is {@link String }
                     * 
                     */
                    public void setValor(String value) {
                        this.valor = value;
                    }

                }

            }

            /**
             * <p>
             * Clase Java para anonymous complex type.
             * 
             * <p>
             * El siguiente fragmento de esquema especifica el contenido que se
             * espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType&gt;
             *   &lt;complexContent&gt;
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *       &lt;sequence&gt;
             *         &lt;element name="impuesto" type="{}impuesto" maxOccurs="unbounded" minOccurs="0"/&gt;
             *       &lt;/sequence&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = { "impuesto" })
            public static class Impuestos implements Serializable {

                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                protected List<Impuesto> impuesto;

                /**
                 * Gets the value of the impuesto property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object. This is
                 * why there is not a <CODE>set</CODE> method for the impuesto
                 * property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * 
                 * <pre>
                 * getImpuesto().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Impuesto }
                 * 
                 * 
                 */
                public List<Impuesto> getImpuesto() {
                    if (impuesto == null) {
                        impuesto = new ArrayList<Impuesto>();
                    }
                    return this.impuesto;
                }

            }

        }

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
     *         &lt;element name="campoAdicional" maxOccurs="15"&gt;
     *           &lt;complexType&gt;
     *             &lt;simpleContent&gt;
     *               &lt;extension base="&lt;&gt;campoAdicional"&gt;
     *                 &lt;attribute name="nombre" use="required" type="{}nombre" /&gt;
     *               &lt;/extension&gt;
     *             &lt;/simpleContent&gt;
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
    @XmlType(name = "", propOrder = { "campoAdicional" })
    public static class InfoAdicional implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @XmlElement(required = true)
        protected List<NotaCredito.InfoAdicional.CampoAdicional> campoAdicional;

        /**
         * Gets the value of the campoAdicional property.
         * 
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the campoAdicional property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getCampoAdicional().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NotaCredito.InfoAdicional.CampoAdicional }
         * 
         * 
         */
        public List<NotaCredito.InfoAdicional.CampoAdicional> getCampoAdicional() {
            if (campoAdicional == null) {
                campoAdicional = new ArrayList<NotaCredito.InfoAdicional.CampoAdicional>();
            }
            return this.campoAdicional;
        }

        /**
         * <p>
         * Clase Java para anonymous complex type.
         * 
         * <p>
         * El siguiente fragmento de esquema especifica el contenido que se
         * espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;simpleContent&gt;
         *     &lt;extension base="&lt;&gt;campoAdicional"&gt;
         *       &lt;attribute name="nombre" use="required" type="{}nombre" /&gt;
         *     &lt;/extension&gt;
         *   &lt;/simpleContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "value" })
        public static class CampoAdicional implements Serializable {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @XmlValue
            protected String value;
            @XmlAttribute(name = "nombre", required = true)
            protected String nombre;

            /**
             * Obtiene el valor de la propiedad value.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getValue() {
                return value;
            }

            /**
             * Define el valor de la propiedad value.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Obtiene el valor de la propiedad nombre.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getNombre() {
                return nombre;
            }

            /**
             * Define el valor de la propiedad nombre.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setNombre(String value) {
                this.nombre = value;
            }

        }

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
     *         &lt;element name="fechaEmision" type="{}fechaEmision"/&gt;
     *         &lt;element name="dirEstablecimiento" type="{}dirEstablecimiento" minOccurs="0"/&gt;
     *         &lt;element name="tipoIdentificacionComprador" type="{}tipoIdentificacionComprador"/&gt;
     *         &lt;element name="razonSocialComprador" type="{}razonSocialComprador"/&gt;
     *         &lt;element name="identificacionComprador" type="{}identificacionComprador"/&gt;
     *         &lt;element name="contribuyenteEspecial" type="{}contribuyenteEspecial" minOccurs="0"/&gt;
     *         &lt;element name="obligadoContabilidad" type="{}obligadoContabilidad" minOccurs="0"/&gt;
     *         &lt;element name="rise" type="{}rise" minOccurs="0"/&gt;
     *         &lt;element name="codDocModificado" type="{}codDocModificado"/&gt;
     *         &lt;element name="numDocModificado" type="{}numDocModificado"/&gt;
     *         &lt;element name="fechaEmisionDocSustento" type="{}fechaEmisionDocSustento"/&gt;
     *         &lt;element name="totalSinImpuestos" type="{}totalSinImpuestos"/&gt;
     *         &lt;element ref="{}compensaciones" minOccurs="0"/&gt;
     *         &lt;element name="valorModificacion" type="{}valorModificacion"/&gt;
     *         &lt;element name="moneda" type="{}moneda" minOccurs="0"/&gt;
     *         &lt;element ref="{}totalConImpuestos"/&gt;
     *         &lt;element name="motivo" type="{}motivo"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador",
            "razonSocialComprador", "identificacionComprador", "contribuyenteEspecial", "obligadoContabilidad", "rise",
            "codDocModificado", "numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos", "compensaciones",
            "valorModificacion", "moneda", "totalConImpuestos", "motivo" })
    public static class InfoNotaCredito implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @XmlElement(required = true)
        protected String fechaEmision;
        protected String dirEstablecimiento;
        @XmlElement(required = true)
        protected String tipoIdentificacionComprador;
        @XmlElement(required = true)
        protected String razonSocialComprador;
        @XmlElement(required = true)
        protected String identificacionComprador;
        protected String contribuyenteEspecial;
        @XmlSchemaType(name = "string")
        protected ObligadoContabilidad obligadoContabilidad;
        protected String rise;
        @XmlElement(required = true)
        protected String codDocModificado;
        @XmlElement(required = true)
        protected String numDocModificado;
        @XmlElement(required = true)
        protected String fechaEmisionDocSustento;
        @XmlElement(required = true)
        protected BigDecimal totalSinImpuestos;
        protected Compensaciones compensaciones;
        @XmlElement(required = true)
        protected BigDecimal valorModificacion;
        protected String moneda;
        @XmlElement(required = true)
        protected TotalConImpuestos totalConImpuestos;
        @XmlElement(required = true)
        protected String motivo;

        /**
         * Obtiene el valor de la propiedad fechaEmision.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getFechaEmision() {
            return fechaEmision;
        }

        /**
         * Define el valor de la propiedad fechaEmision.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setFechaEmision(String value) {
            this.fechaEmision = value;
        }

        /**
         * Obtiene el valor de la propiedad dirEstablecimiento.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getDirEstablecimiento() {
            return dirEstablecimiento;
        }

        /**
         * Define el valor de la propiedad dirEstablecimiento.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setDirEstablecimiento(String value) {
            this.dirEstablecimiento = value;
        }

        /**
         * Obtiene el valor de la propiedad tipoIdentificacionComprador.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getTipoIdentificacionComprador() {
            return tipoIdentificacionComprador;
        }

        /**
         * Define el valor de la propiedad tipoIdentificacionComprador.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setTipoIdentificacionComprador(String value) {
            this.tipoIdentificacionComprador = value;
        }

        /**
         * Obtiene el valor de la propiedad razonSocialComprador.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getRazonSocialComprador() {
            return razonSocialComprador;
        }

        /**
         * Define el valor de la propiedad razonSocialComprador.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setRazonSocialComprador(String value) {
            this.razonSocialComprador = value;
        }

        /**
         * Obtiene el valor de la propiedad identificacionComprador.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getIdentificacionComprador() {
            return identificacionComprador;
        }

        /**
         * Define el valor de la propiedad identificacionComprador.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setIdentificacionComprador(String value) {
            this.identificacionComprador = value;
        }

        /**
         * Obtiene el valor de la propiedad contribuyenteEspecial.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getContribuyenteEspecial() {
            return contribuyenteEspecial;
        }

        /**
         * Define el valor de la propiedad contribuyenteEspecial.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setContribuyenteEspecial(String value) {
            this.contribuyenteEspecial = value;
        }

        /**
         * Obtiene el valor de la propiedad obligadoContabilidad.
         * 
         * @return possible object is {@link ObligadoContabilidad }
         * 
         */
        public ObligadoContabilidad getObligadoContabilidad() {
            return obligadoContabilidad;
        }

        /**
         * Define el valor de la propiedad obligadoContabilidad.
         * 
         * @param value
         *            allowed object is {@link ObligadoContabilidad }
         * 
         */
        public void setObligadoContabilidad(ObligadoContabilidad value) {
            this.obligadoContabilidad = value;
        }

        /**
         * Obtiene el valor de la propiedad rise.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getRise() {
            return rise;
        }

        /**
         * Define el valor de la propiedad rise.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setRise(String value) {
            this.rise = value;
        }

        /**
         * Obtiene el valor de la propiedad codDocModificado.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getCodDocModificado() {
            return codDocModificado;
        }

        /**
         * Define el valor de la propiedad codDocModificado.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setCodDocModificado(String value) {
            this.codDocModificado = value;
        }

        /**
         * Obtiene el valor de la propiedad numDocModificado.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getNumDocModificado() {
            return numDocModificado;
        }

        /**
         * Define el valor de la propiedad numDocModificado.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setNumDocModificado(String value) {
            this.numDocModificado = value;
        }

        /**
         * Obtiene el valor de la propiedad fechaEmisionDocSustento.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getFechaEmisionDocSustento() {
            return fechaEmisionDocSustento;
        }

        /**
         * Define el valor de la propiedad fechaEmisionDocSustento.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setFechaEmisionDocSustento(String value) {
            this.fechaEmisionDocSustento = value;
        }

        /**
         * Obtiene el valor de la propiedad totalSinImpuestos.
         * 
         * @return possible object is {@link BigDecimal }
         * 
         */
        public BigDecimal getTotalSinImpuestos() {
            return totalSinImpuestos;
        }

        /**
         * Define el valor de la propiedad totalSinImpuestos.
         * 
         * @param value
         *            allowed object is {@link BigDecimal }
         * 
         */
        public void setTotalSinImpuestos(BigDecimal value) {
            this.totalSinImpuestos = value;
        }

        /**
         * Obtiene el valor de la propiedad compensaciones.
         * 
         * @return possible object is {@link Compensaciones }
         * 
         */
        public Compensaciones getCompensaciones() {
            return compensaciones;
        }

        /**
         * Define el valor de la propiedad compensaciones.
         * 
         * @param value
         *            allowed object is {@link Compensaciones }
         * 
         */
        public void setCompensaciones(Compensaciones value) {
            this.compensaciones = value;
        }

        /**
         * Obtiene el valor de la propiedad valorModificacion.
         * 
         * @return possible object is {@link BigDecimal }
         * 
         */
        public BigDecimal getValorModificacion() {
            return valorModificacion;
        }

        /**
         * Define el valor de la propiedad valorModificacion.
         * 
         * @param value
         *            allowed object is {@link BigDecimal }
         * 
         */
        public void setValorModificacion(BigDecimal value) {
            this.valorModificacion = value;
        }

        /**
         * Obtiene el valor de la propiedad moneda.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getMoneda() {
            return moneda;
        }

        /**
         * Define el valor de la propiedad moneda.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setMoneda(String value) {
            this.moneda = value;
        }

        /**
         * Obtiene el valor de la propiedad totalConImpuestos.
         * 
         * @return possible object is {@link TotalConImpuestos }
         * 
         */
        public TotalConImpuestos getTotalConImpuestos() {
            return totalConImpuestos;
        }

        /**
         * Define el valor de la propiedad totalConImpuestos.
         * 
         * @param value
         *            allowed object is {@link TotalConImpuestos }
         * 
         */
        public void setTotalConImpuestos(TotalConImpuestos value) {
            this.totalConImpuestos = value;
        }

        /**
         * Obtiene el valor de la propiedad motivo.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getMotivo() {
            return motivo;
        }

        /**
         * Define el valor de la propiedad motivo.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setMotivo(String value) {
            this.motivo = value;
        }

    }

}
