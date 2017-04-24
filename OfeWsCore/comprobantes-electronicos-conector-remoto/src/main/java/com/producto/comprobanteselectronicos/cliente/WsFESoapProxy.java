package com.producto.comprobanteselectronicos.cliente;

public class WsFESoapProxy implements com.producto.comprobanteselectronicos.cliente.WsFESoap {
  private String _endpoint = null;
  private com.producto.comprobanteselectronicos.cliente.WsFESoap wsFESoap = null;
  
  public WsFESoapProxy() {
    _initWsFESoapProxy();
  }
  
  public WsFESoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initWsFESoapProxy();
  }
  
  private void _initWsFESoapProxy() {
    try {
      wsFESoap = (new com.producto.comprobanteselectronicos.cliente.WsFELocator()).getWsFESoap();
      if (wsFESoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wsFESoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wsFESoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wsFESoap != null)
      ((javax.xml.rpc.Stub)wsFESoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.producto.comprobanteselectronicos.cliente.WsFESoap getWsFESoap() {
    if (wsFESoap == null)
      _initWsFESoapProxy();
    return wsFESoap;
  }
  
  public int updateAutorizacion(java.lang.String dataareaid, java.lang.String tipocomprobante, java.lang.String establecimientoEmision, java.lang.String secuencial, java.lang.String status, java.lang.String autorizacion, java.lang.String motivo, java.lang.String fecharecepcion) throws java.rmi.RemoteException{
    if (wsFESoap == null)
      _initWsFESoapProxy();
    return wsFESoap.updateAutorizacion(dataareaid, tipocomprobante, establecimientoEmision, secuencial, status, autorizacion, motivo, fecharecepcion);
  }
  
  
}