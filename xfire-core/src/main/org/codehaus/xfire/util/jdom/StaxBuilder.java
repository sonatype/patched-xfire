/*--

 Copyright (C) 2000-2004 Jason Hunter & Brett McLaughlin.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions, and the disclaimer that follows
 these conditions in the documentation and/or other materials
 provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
 derived from this software without prior written permission.  For
 written permission, please contact <request_AT_jdom_DOT_org>.

 4. Products derived from this software may not be called "JDOM", nor
 may "JDOM" appear in their name, without prior written permission
 from the JDOM Project Management <request_AT_jdom_DOT_org>.

 In addition, we request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
 "This product includes software developed by the
 JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many
 individuals on behalf of the JDOM Project and was originally
 created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
 Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
 on the JDOM Project, please see <http://www.jdom.org/>.

 */

package org.codehaus.xfire.util.jdom;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.xfire.util.STAXUtils;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMFactory;
import org.jdom.Namespace;
import org.jdom.UncheckedJDOMFactory;

/**
 * Builds a JDOM {@link org.jdom.Document org.jdom.Document} using a
 * {@link javax.xml.stream.XMLStreamReader}.
 * 
 * @version $Revision$, $Date$
 * @author Tatu Saloranta
 * @author Bradley S. Huffman
 */
public class StaxBuilder
{

    /**
     * Map that contains conversion from textual attribute types StAX uses, to
     * int values JDOM uses.
     */
    final static HashMap attrTypes = new HashMap(32);
    static
    {
        attrTypes.put("CDATA", new Integer(Attribute.CDATA_TYPE));
        attrTypes.put("cdata", new Integer(Attribute.CDATA_TYPE));
        attrTypes.put("ID", new Integer(Attribute.ID_TYPE));
        attrTypes.put("id", new Integer(Attribute.ID_TYPE));
        attrTypes.put("IDREF", new Integer(Attribute.IDREF_TYPE));
        attrTypes.put("idref", new Integer(Attribute.IDREF_TYPE));
        attrTypes.put("IDREFS", new Integer(Attribute.IDREFS_TYPE));
        attrTypes.put("idrefs", new Integer(Attribute.IDREFS_TYPE));
        attrTypes.put("ENTITY", new Integer(Attribute.ENTITY_TYPE));
        attrTypes.put("entity", new Integer(Attribute.ENTITY_TYPE));
        attrTypes.put("ENTITIES", new Integer(Attribute.ENTITIES_TYPE));
        attrTypes.put("entities", new Integer(Attribute.ENTITIES_TYPE));
        attrTypes.put("NMTOKEN", new Integer(Attribute.NMTOKEN_TYPE));
        attrTypes.put("nmtoken", new Integer(Attribute.NMTOKEN_TYPE));
        attrTypes.put("NMTOKENS", new Integer(Attribute.NMTOKENS_TYPE));
        attrTypes.put("nmtokens", new Integer(Attribute.NMTOKENS_TYPE));
        attrTypes.put("NOTATION", new Integer(Attribute.NOTATION_TYPE));
        attrTypes.put("notation", new Integer(Attribute.NOTATION_TYPE));
        attrTypes.put("ENUMERATED", new Integer(Attribute.ENUMERATED_TYPE));
        attrTypes.put("enumerated", new Integer(Attribute.ENUMERATED_TYPE));
    }

    /** The factory for creating new JDOM objects */
    private JDOMFactory factory = null;

    private XMLInputFactory xifactory;
    
    /**
     * Whether ignorable white space should be ignored, ie not added in the
     * resulting JDOM tree. If true, it will be ignored; if false, it will be
     * added in the tree. Default value if false.
     */
    protected boolean cfgIgnoreWS = false;

    private Map additionalNamespaces = null;
    
    public Map getAdditionalNamespaces()
    {
        return additionalNamespaces;
    }


    public void setAdditionalNamespaces(Map additionalNamespaces)
    {
        this.additionalNamespaces = additionalNamespaces;
    }


    
    
    /**
     * Default constructor.
     */
    public StaxBuilder()
    {
        xifactory = STAXUtils.getXMLInputFactory(null);
    }

    public StaxBuilder(Map namespaces)
    {
        xifactory = STAXUtils.getXMLInputFactory(null);
        this.additionalNamespaces = namespaces;
    }
    
    public StaxBuilder(XMLInputFactory xifactory)
    {
        this.xifactory = xifactory;
    }
    
    /*
     * This sets a custom JDOMFactory for the builder. Use this to build the
     * tree with your own subclasses of the JDOM classes.
     * 
     * @param factory <code>JDOMFactory</code> to use
     */
    public void setFactory(JDOMFactory f)
    {
        factory = f;
    }

    public void setIgnoreWhitespace(boolean state)
    {
        cfgIgnoreWS = state;
    }

    /**
     * Returns the current {@link org.jdom.JDOMFactory} in use, if one has been
     * previously set with {@link #setFactory}, otherwise null.
     * 
     * @return the factory builder will use
     */
    public JDOMFactory getFactory()
    {
        return factory;
    }

    /**
     * This will build a JDOM tree given a StAX stream reader.
     * 
     * @param r
     *            Stream reader from which input is read.
     * @return <code>Document</code> - JDOM document object.
     * @throws XMLStreamException
     *             If the reader threw such exception (to indicate a parsing or
     *             I/O problem)
     */
    public Document build(XMLStreamReader r)
        throws XMLStreamException
    {
        /*
         * Should we do sanity checking to see that r is positioned at
         * beginning? Not doing so will allow creating documents from sub-trees,
         * though?
         */
        JDOMFactory f = factory;
        if (f == null)
        {
            f = new UncheckedJDOMFactory();
        }
        Document doc = f.document(null);
        buildTree(f, r, doc);
        return doc;
    }

    public Document build(InputStream is) throws XMLStreamException
    {
        return build(xifactory.createXMLStreamReader(is));
    }
    
    public Document build(Reader reader) throws XMLStreamException
    {
        return build(xifactory.createXMLStreamReader(reader));
    }
    
    /**
     * This takes a <code>XMLStreamReader</code> and builds up a JDOM tree.
     * Recursion has been eliminated by using local stack of open elements; this
     * improves performance somewhat (classic
     * recursion-by-iteration-and-explicit stack transformation)
     * 
     * @param node
     *            <code>Code</node> to examine.
     * @param doc JDOM <code>Document</code> being built.
     */
    private void buildTree(JDOMFactory f, XMLStreamReader r, Document doc)
        throws XMLStreamException
    {
        Element current = null; // At top level
        int event = r.getEventType();
        
        // if we're at the start then we need to do a next
        if (event == -1) event = r.next();

        while (true)
        {
            boolean noadd = false;
            Content child = null;

            switch (event)
            {
            case XMLStreamConstants.CDATA:
                child = f.cdata(r.getText());
                break;

            case XMLStreamConstants.SPACE:
                if (cfgIgnoreWS)
                {
                    noadd = true;
                    break;
                }
            // fall through

            case XMLStreamConstants.CHARACTERS:
                /*
                 * Small complication: although (ignorable) white space is
                 * allowed in prolog/epilog, and StAX may report such event,
                 * JDOM barfs if trying to add it. Thus, let's just ignore all
                 * textual stuff outside the tree:
                 */
                if (current == null)
                {
                    noadd = true;
                    break;
                }
                child = f.text(r.getText());
                break;

            case XMLStreamConstants.COMMENT:
                child = f.comment(r.getText());
                break;

            case XMLStreamConstants.END_DOCUMENT:
                return;

            case XMLStreamConstants.END_ELEMENT:
                /**
                 * If current.getParentElement() previously
                 * returned null and we get this event
                 * again we shouldn't bail out with a
                 * NullPointerException
                 */
                if(current != null)
                {
                    current = current.getParentElement();
                }
                noadd = true;
                break;

            case XMLStreamConstants.ENTITY_DECLARATION:
            case XMLStreamConstants.NOTATION_DECLARATION:
                /*
                 * Shouldn't really get these, but maybe some stream readers do
                 * provide the info. If so, better ignore it -- DTD event should
                 * have most/all we need.
                 */
                noadd = true;
                break;

            case XMLStreamConstants.ENTITY_REFERENCE:
                child = f.entityRef(r.getLocalName());
                break;

            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                child = f.processingInstruction(r.getPITarget(), r.getPIData());
                break;

            case XMLStreamConstants.START_ELEMENT:
            // Ok, need to add a new element and simulate recursion
            {
                Element newElem = null;
                String nsURI = r.getNamespaceURI();
                String elemPrefix = r.getPrefix(); // needed for special
                                                    // handling of elem's
                                                    // namespace
                String ln = r.getLocalName();

                if (nsURI == null || nsURI.length() == 0)
                {
                    if (elemPrefix == null || elemPrefix.length() == 0)
                    {
                        newElem = f.element(ln);
                    }
                    else
                    {
                        /*
                         * Happens when a prefix is bound to the default (empty)
                         * namespace...
                         */
                        newElem = f.element(ln, elemPrefix, "");
                    }
                }
                else
                {
                    newElem = f.element(ln, elemPrefix, nsURI);
                }

                /*
                 * Let's add element right away (probably have to do it to bind
                 * attribute namespaces, too)
                 */
                if (current == null)
                { // at root
                    doc.setRootElement(newElem);
                    if(additionalNamespaces != null ){
                     for( Iterator iter = additionalNamespaces .keySet().iterator();iter.hasNext();){
                        String prefix = (String) iter.next();
                        String uri = (String) additionalNamespaces .get(prefix);

                        newElem.addNamespaceDeclaration(Namespace.getNamespace(prefix,uri));
                     }
                    }
                }
                else
                {
                    f.addContent(current, newElem);
                }

                // Any declared namespaces?
                for (int i = 0, len = r.getNamespaceCount(); i < len; ++i)
                {
                    String prefix = r.getNamespacePrefix(i);
                    Namespace ns = Namespace.getNamespace(prefix, r.getNamespaceURI(i));
                    // JDOM has special handling for element's "own" ns:
                    if (prefix != null && prefix.equals(elemPrefix))
                    {
                        ; // already set by when it was constructed...
                    }
                    else
                    {
                        f.addNamespaceDeclaration(newElem, ns);
                    }
                }

                // And then the attributes:
                for (int i = 0, len = r.getAttributeCount(); i < len; ++i)
                {
                    String prefix = r.getAttributePrefix(i);
                    Namespace ns;

                    if (prefix == null || prefix.length() == 0)
                    {
                        // Attribute not in any namespace
                        ns = Namespace.NO_NAMESPACE;
                    }
                    else
                    {
                        ns = newElem.getNamespace(prefix);
                        
                    }
                    Attribute attr = f.attribute(r.getAttributeLocalName(i),
                                                 r.getAttributeValue(i),
                                                 resolveAttrType(r.getAttributeType(i)),
                                                 ns);
                    f.setAttribute(newElem, attr);
                }
                // And then 'push' new element...
                current = newElem;
            }

                // Already added the element, can continue
                noadd = true;
                break;

            case XMLStreamConstants.START_DOCUMENT:
            /*
             * This should only be received at the beginning of document... so,
             * should we indicate the problem or not?
             */
            /*
             * For now, let it pass: maybe some (broken) readers pass that info
             * as first event in beginning of doc?
             */

            case XMLStreamConstants.DTD:
                /*
                 * !!! Note: StAX does not expose enough information about
                 * doctype declaration (specifically, public and system id!);
                 * should (re-)parse information... not yet implemented
                 */
                // TBI
                //continue main_loop;

            // Should never get these, from a stream reader:

            /*
             * (commented out entries are just FYI; default catches them all)
             */

            // case XMLStreamConstants.ATTRIBUTE:
            // case XMLStreamConstants.NAMESPACE:
            default:
               /* throw new XMLStreamException("Unrecognized iterator event type: "
                        + r.getEventType()
                        + "; should not receive such types (broken stream reader?)");*/
                break;
            }

            if (!noadd && child != null)
            {
                if (current == null)
                {
                    f.addContent(doc, child);
                }
                else
                {
                    f.addContent(current, child);
                }
            }
            
            if (r.hasNext()) 
            {
                event = r.next();
            }
            else
            {
                break;
            }
        }
    }

    private static int resolveAttrType(String typeStr)
    {
        if (typeStr != null && typeStr.length() > 0)
        {
            Integer I = (Integer) attrTypes.get(typeStr);
            if (I != null)
            {
                return I.intValue();
            }
        }
        return Attribute.UNDECLARED_TYPE;
    }
}