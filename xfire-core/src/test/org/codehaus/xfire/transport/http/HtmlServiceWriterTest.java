package org.codehaus.xfire.transport.http;

/**
 * @author Arjen Poutsma
 */

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceInfo;
import org.custommonkey.xmlunit.XMLTestCase;

public class HtmlServiceWriterTest
        extends XMLTestCase
{
    private HtmlServiceWriter htmlServiceWriter;
    private Service service;

    protected void setUp()
            throws Exception
    {
        htmlServiceWriter = new HtmlServiceWriter();
        ServiceInfo serviceInfo = new ServiceInfo(new QName("serviceport"), getClass());
        service = new Service(serviceInfo);
        service.setName(new QName("service"));
    }

    public void testdescribeServices()
            throws Exception
    {
        ArrayList services = new ArrayList();
        services.add(service);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        htmlServiceWriter.write(os, services);
        os.close();

        String expected = "<?xml version='1.0' encoding='UTF-8'?><!DOCTYPE html "
            + " PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
            + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html>"
            + "<head><title>XFire Services</title></head><body><p />"
            + "<p>Services:</p><ul><li>service <a href=\"/service?wsdl\">[wsdl]"
            + "</a></li></ul></body></html>";
        
        String output = new String(os.toByteArray(),"UTF-8");
        assertXMLEqual(expected, output);
    }

    public void testDescribeService()
            throws Exception
    {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        htmlServiceWriter.write(os, service);
        os.close();

        String expected = "<?xml version='1.0' encoding='UTF-8'?><!DOCTYPE html "
            + "PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
            + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html>"
            + "<head><title>service Web Service</title></head><body>"
            + "<h1>service Web Service</h1></body></html>";                
        
        String output = new String(os.toByteArray(),"UTF-8");
        assertXMLEqual(expected, output);

    }
}