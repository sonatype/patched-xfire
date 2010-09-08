package org.codehaus.xfire.wsdl11.builder;

import javax.wsdl.Definition;

public interface WSDLBuilderExtension
{
    public void extend(Definition definition, WSDLBuilder builder);
}
