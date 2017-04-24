//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2016.07.30 a las 11:14:19 AM COT 
//

package com.libreria.ofe.modelo.xsd.notadebito;

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
 *         &lt;element name="infoNotaDebito"&gt;
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
 *                   &lt;element name="impuestos"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="impuesto" type="{}impuesto" maxOccurs="unbounded"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="compensaciones" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="compensacion" type="{}compensacion" maxOccurs="unbounded"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="valorTotal" type="{}valorTotal"/&gt;
 *                   &lt;element name="pagos" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="pago" type="{}pago" maxOccurs="unbounded"/&gt;
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
 *         &lt;element name="motivos"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence maxOccurs="unbounded"&gt;
 *                   &lt;element name="motivo"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="razon" type="{}razon"/&gt;
 *                             &lt;element name="valor" type="{}valor"/&gt;
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
 *                         &lt;extension base="&lt;&gt;nombre"&gt;
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
 *       &lt;attribute name="id"&gt;
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
@XmlType(name = "", propOrder = { "infoTributaria", "infoNotaDebito", "motivos", "infoAdicional", "signature" })
@XmlRootElement(name = "notaDebito", namespace = "")
public class NotaDebito implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    protected InfoTributaria infoTributaria;
    @XmlElement(required = true)
    protected NotaDebito.InfoNotaDebito infoNotaDebito;
    @XmlElement(required = true)
    protected NotaDebito.Motivos motivos;
    protected NotaDebito.InfoAdicional infoAdicional;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "id")
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
     * Obtiene el valor de la propiedad infoNotaDebito.
     * 
     * @return possible object is {@link NotaDebito.InfoNotaDebito }
     * 
     */
    public NotaDebito.InfoNotaDebito getInfoNotaDebito() {
        return infoNotaDebito;
    }

    /**
     * Define el valor de la propiedad infoNotaDebito.
     * 
     * @param value
     *            allowed object is {@link NotaDebito.InfoNotaDebito }
     * 
     */
    public void setInfoNotaDebito(NotaDebito.InfoNotaDebito value) {
        this.infoNotaDebito = value;
    }

    /**
     * Obtiene el valor de la propiedad motivos.
     * 
     * @return possible object is {@link NotaDebito.Motivos }
     * 
     */
    public NotaDebito.Motivos getMotivos() {
        return motivos;
    }

    /**
     * Define el valor de la propiedad motivos.
     * 
     * @param value
     *            allowed object is {@link NotaDebito.Motivos }
     * 
     */
    public void setMotivos(NotaDebito.Motivos value) {
        this.motivos = value;
    }

    /**
     * Obtiene el valor de la propiedad infoAdicional.
     * 
     * @return possible object is {@link NotaDebito.InfoAdicional }
     * 
     */
    public NotaDebito.InfoAdicional getInfoAdicional() {
        return infoAdicional;
    }

    /**
     * Define el valor de la propiedad infoAdicional.
     * 
     * @param value
     *            allowed object is {@link NotaDebito.InfoAdicional }
     * 
     */
    public void setInfoAdicional(NotaDebito.InfoAdicional value) {
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
     *         &lt;element name="campoAdicional" maxOccurs="15"&gt;
     *           &lt;complexType&gt;
     *             &lt;simpleContent&gt;
     *               &lt;extension base="&lt;&gt;nombre"&gt;
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
        protected List<NotaDebito.InfoAdicional.CampoAdicional> campoAdicional;

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
         * {@link NotaDebito.InfoAdicional.CampoAdicional }
         * 
         * 
         */
        public List<NotaDebito.InfoAdicional.CampoAdicional> getCampoAdicional() {
            if (campoAdicional == null) {
                campoAdicional = new ArrayList<NotaDebito.InfoAdicional.CampoAdicional>();
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
         *     &lt;extension base="&lt;&gt;nombre"&gt;
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
     *         &lt;element name="impuestos"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="impuesto" type="{}impuesto" maxOccurs="unbounded"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="compensaciones" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="compensacion" type="{}compensacion" maxOccurs="unbounded"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="valorTotal" type="{}valorTotal"/&gt;
     *         &lt;element name="pagos" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="pago" type="{}pago" maxOccurs="unbounded"/&gt;
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
    @XmlType(name = "", propOrder = { "fechaEmision", "dirEstablecimiento", "tipoIdentificacionComprador",
            "razonSocialComprador", "identificacionComprador", "contribuyenteEspecial", "obligadoContabilidad", "rise",
            "codDocModificado", "numDocModificado", "fechaEmisionDocSustento", "totalSinImpuestos", "impuestos",
            "compensaciones", "valorTotal", "pagos" })
    public static class InfoNotaDebito implements Serializable {

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
        @XmlElement(required = true)
        protected NotaDebito.InfoNotaDebito.Impuestos impuestos;
        protected NotaDebito.InfoNotaDebito.Compensaciones compensaciones;
        @XmlElement(required = true)
        protected BigDecimal valorTotal;
        protected List<NotaDebito.InfoNotaDebito.Pagos> pagos;

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
         * Obtiene el valor de la propiedad impuestos.
         * 
         * @return possible object is
         *         {@link NotaDebito.InfoNotaDebito.Impuestos }
         * 
         */
        public NotaDebito.InfoNotaDebito.Impuestos getImpuestos() {
            return impuestos;
        }

        /**
         * Define el valor de la propiedad impuestos.
         * 
         * @param value
         *            allowed object is
         *            {@link NotaDebito.InfoNotaDebito.Impuestos }
         * 
         */
        public void setImpuestos(NotaDebito.InfoNotaDebito.Impuestos value) {
            this.impuestos = value;
        }

        /**
         * Obtiene el valor de la propiedad compensaciones.
         * 
         * @return possible object is
         *         {@link NotaDebito.InfoNotaDebito.Compensaciones }
         * 
         */
        public NotaDebito.InfoNotaDebito.Compensaciones getCompensaciones() {
            return compensaciones;
        }

        /**
         * Define el valor de la propiedad compensaciones.
         * 
         * @param value
         *            allowed object is
         *            {@link NotaDebito.InfoNotaDebito.Compensaciones }
         * 
         */
        public void setCompensaciones(NotaDebito.InfoNotaDebito.Compensaciones value) {
            this.compensaciones = value;
        }

        /**
         * Obtiene el valor de la propiedad valorTotal.
         * 
         * @return possible object is {@link BigDecimal }
         * 
         */
        public BigDecimal getValorTotal() {
            return valorTotal;
        }

        /**
         * Define el valor de la propiedad valorTotal.
         * 
         * @param value
         *            allowed object is {@link BigDecimal }
         * 
         */
        public void setValorTotal(BigDecimal value) {
            this.valorTotal = value;
        }

        /**
         * Gets the value of the pagos property.
         * 
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the pagos property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getPagos().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NotaDebito.InfoNotaDebito.Pagos }
         * 
         * 
         */
        public List<NotaDebito.InfoNotaDebito.Pagos> getPagos() {
            if (pagos == null) {
                pagos = new ArrayList<NotaDebito.InfoNotaDebito.Pagos>();
            }
            return this.pagos;
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
         *         &lt;element name="compensacion" type="{}compensacion" maxOccurs="unbounded"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "compensacion" })
        public static class Compensaciones implements Serializable {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @XmlElement(required = true)
            protected List<Compensacion> compensacion;

            /**
             * Gets the value of the compensacion property.
             * 
             * <p>
             * This accessor method returns a reference to the live list, not a
             * snapshot. Therefore any modification you make to the returned
             * list will be present inside the JAXB object. This is why there is
             * not a <CODE>set</CODE> method for the compensacion property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * 
             * <pre>
             * getCompensacion().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Compensacion }
             * 
             * 
             */
            public List<Compensacion> getCompensacion() {
                if (compensacion == null) {
                    compensacion = new ArrayList<Compensacion>();
                }
                return this.compensacion;
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
         *         &lt;element name="impuesto" type="{}impuesto" maxOccurs="unbounded"/&gt;
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

            @XmlElement(required = true)
            protected List<Impuesto> impuesto;

            /**
             * Gets the value of the impuesto property.
             * 
             * <p>
             * This accessor method returns a reference to the live list, not a
             * snapshot. Therefore any modification you make to the returned
             * list will be present inside the JAXB object. This is why there is
             * not a <CODE>set</CODE> method for the impuesto property.
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
         *         &lt;element name="pago" type="{}pago" maxOccurs="unbounded"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "pago" })
        public static class Pagos implements Serializable {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @XmlElement(required = true)
            protected List<Pago> pago;

            /**
             * Gets the value of the pago property.
             * 
             * <p>
             * This accessor method returns a reference to the live list, not a
             * snapshot. Therefore any modification you make to the returned
             * list will be present inside the JAXB object. This is why there is
             * not a <CODE>set</CODE> method for the pago property.
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
             * {@link Pago }
             * 
             * 
             */
            public List<Pago> getPago() {
                if (pago == null) {
                    pago = new ArrayList<Pago>();
                }
                return this.pago;
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
     *       &lt;sequence maxOccurs="unbounded"&gt;
     *         &lt;element name="motivo"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="razon" type="{}razon"/&gt;
     *                   &lt;element name="valor" type="{}valor"/&gt;
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
    @XmlType(name = "", propOrder = { "motivo" })
    public static class Motivos implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @XmlElement(required = true)
        protected List<NotaDebito.Motivos.Motivo> motivo;

        /**
         * Gets the value of the motivo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the motivo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * 
         * <pre>
         * getMotivo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NotaDebito.Motivos.Motivo }
         * 
         * 
         */
        public List<NotaDebito.Motivos.Motivo> getMotivo() {
            if (motivo == null) {
                motivo = new ArrayList<NotaDebito.Motivos.Motivo>();
            }
            return this.motivo;
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
         *         &lt;element name="razon" type="{}razon"/&gt;
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
        @XmlType(name = "", propOrder = { "razon", "valor" })
        public static class Motivo implements Serializable {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @XmlElement(required = true)
            protected String razon;
            @XmlElement(required = true)
            protected BigDecimal valor;

            /**
             * Obtiene el valor de la propiedad razon.
             * 
             * @return possible object is {@link String }
             * 
             */
            public String getRazon() {
                return razon;
            }

            /**
             * Define el valor de la propiedad razon.
             * 
             * @param value
             *            allowed object is {@link String }
             * 
             */
            public void setRazon(String value) {
                this.razon = value;
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

    }

}
