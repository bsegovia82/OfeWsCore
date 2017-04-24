/**
 * WsFESoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.producto.comprobanteselectronicos.cliente;

public interface WsFESoap extends java.rmi.Remote {

    /**
     * Enviar Datos de Autorizacion
     */
    public int updateAutorizacion(java.lang.String dataareaid, java.lang.String tipocomprobante, java.lang.String establecimientoEmision, java.lang.String secuencial, java.lang.String status, java.lang.String autorizacion, java.lang.String motivo, java.lang.String fecharecepcion) throws java.rmi.RemoteException;
}
