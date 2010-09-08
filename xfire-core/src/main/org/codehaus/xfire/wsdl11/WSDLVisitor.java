/**
 *
 * Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codehaus.xfire.wsdl11;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;

public class WSDLVisitor
{
    protected final Definition definition;

    public WSDLVisitor(Definition definition)
    {
        this.definition = definition;
    }

    public Definition getDefinition()
    {
        return definition;
    }

    public void walkTree() throws Exception
    {
        Exception e;
        
        begin();

        visit(definition);
        Collection imports = definition.getImports().values();
        for (Iterator iterator = imports.iterator(); iterator.hasNext();)
        {
            Import wsdlImport = (Import) iterator.next();
            visit(wsdlImport);
        }
        visit(definition.getTypes());
        
        Collection messages = definition.getMessages().values();
        for (Iterator iterator = messages.iterator(); iterator.hasNext();)
        {
            Message message = (Message) iterator.next();
            visit(message);
            Collection parts = message.getParts().values();
            for (Iterator iterator2 = parts.iterator(); iterator2.hasNext();)
            {
                Part part = (Part) iterator2.next();
                visit(part);
            }
        }
        
        Collection services = definition.getServices().values();
        for (Iterator iterator = services.iterator(); iterator.hasNext();)
        {
            Service service = (Service) iterator.next();
            begin(service);
            
            Collection ports = service.getPorts().values();
            for (Iterator iterator1 = ports.iterator(); iterator1.hasNext();)
            {
                Port port = (Port) iterator1.next();
                visit(port);
                
                Binding binding = port.getBinding();
                PortType portType = binding.getPortType();
                
                visit(binding);
                
                List bindingOperations = binding.getBindingOperations();
                for (int i = 0; i < bindingOperations.size(); i++)
                {
                    BindingOperation bindingOperation = 
                        (BindingOperation) bindingOperations.get(i);
                    
                    visit(bindingOperation);
                    visit(bindingOperation.getBindingInput(), bindingOperation.getOperation().getInput());
                    visit(bindingOperation.getBindingOutput(), bindingOperation.getOperation().getOutput());
                    
                    Collection bindingFaults = bindingOperation.getBindingFaults().values();
                    for (Iterator iterator2 = bindingFaults.iterator(); iterator2.hasNext();)
                    {
                        BindingFault bindingFault = (BindingFault) iterator2.next();
                        Fault fault = bindingOperation.getOperation().getFault(bindingFault.getName());
                        
                        visit(bindingFault, fault);
                    }

                }
                
                visit(portType);
                
                List operations = portType.getOperations();
                for (int i = 0; i < operations.size(); i++)
                {
                    Operation operation = (Operation) operations.get(i);
                    visit(operation);
                    {
                        Input input = operation.getInput();
                        visit(input);
                    }
                    {
                        Output output = operation.getOutput();
                        visit(output);
                    }
                    
                    Collection faults = operation.getFaults().values();
                    for (Iterator iterator2 = faults.iterator(); iterator2.hasNext();)
                    {
                        Fault fault = (Fault) iterator2.next();
                        visit(fault);
                    }
                }
            }
            end(service);
        }

        end();
    }

    protected void begin()
    {
    }

    protected void end()
    {
    }

    protected void visit(Fault fault)
    {
    }

    protected void visit(Definition definition)
    {
    }

    protected void visit(Import wsdlImport)
    {
    }

    protected void visit(Types types)
    {
    }

    protected void visit(BindingFault bindingFault, Fault fault)
    {
    }

    protected void visit(BindingOutput bindingOutput, Output output)
    {
    }

    protected void visit(BindingInput bindingInput, Input input)
    {
    }

    protected void visit(Output output)
    {
    }

    protected void visit(Part part)
    {
    }

    protected void visit(Message message)
    {
    }

    protected void visit(Input input)
    {
    }

    protected void visit(Operation operation)
    {
    }

    protected void visit(PortType portType)
    {
    }

    protected void visit(BindingOperation bindingOperation)
    {
    }

    protected void visit(Binding binding)
    {
    }

    protected void visit(Port port)
    {
    }

    protected void begin(javax.wsdl.Service wservice)
    {
    }

    protected void end(javax.wsdl.Service wservice)
    {
    }
    
}