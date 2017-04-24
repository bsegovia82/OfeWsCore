/**
 * WsFELocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.producto.comprobanteselectronicos.cliente;

public class WsFELocator extends org.apache.axis.client.Service implements WsFE {

    public WsFELocator() {
    }


    public WsFELocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WsFELocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WsFESoap
    //private java.lang.String WsFESoap_address = "http://190.95.159.84/FactElectro/WsFE.asmx";// anterior 
    private java.lang.String WsFESoap_address = "http://azdynatec.cloudapp.net:55180/WSFactElectro/WsFE.asmx";

    public java.lang.String getWsFESoapAddress() {
        return WsFESoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WsFESoapWSDDServiceName = "WsFESoap";

    public java.lang.String getWsFESoapWSDDServiceName() {
        return WsFESoapWSDDServiceName;
    }

    public void setWsFESoapWSDDServiceName(java.lang.String name) {
        WsFESoapWSDDServiceName = name;
    }

    public com.producto.comprobanteselectronicos.cliente.WsFESoap getWsFESoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WsFESoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWsFESoap(endpoint);
    }

    public com.producto.comprobanteselectronicos.cliente.WsFESoap getWsFESoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
        	com.producto.comprobanteselectronicos.cliente.WsFESoapStub _stub = new com.producto.comprobanteselectronicos.cliente.WsFESoapStub(portAddress, this);
            _stub.setPortName(getWsFESoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWsFESoapEndpointAddress(java.lang.String address) {
        WsFESoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.producto.comprobanteselectronicos.cliente.WsFESoap.class.isAssignableFrom(serviceEndpointInterface)) {
            	com.producto.comprobanteselectronicos.cliente.WsFESoapStub _stub = new com.producto.comprobanteselectronicos.cliente.WsFESoapStub(new java.net.URL(WsFESoap_address), this);
                _stub.setPortName(getWsFESoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("WsFESoap".equals(inputPortName)) {
            return getWsFESoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://kfc.com.ec/WsFactElectro/", "WsFE");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://kfc.com.ec/WsFactElectro/", "WsFESoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WsFESoap".equals(portName)) {
            setWsFESoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
