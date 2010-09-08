package org.codehaus.xfire.service.documentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.xfire.service.OperationInfo;

/**
 * Provider of documentation for service object
 * 
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class DocumentationProvider

{
    private String serviceDocumentation;

    private Map operationsDocumentation = new HashMap();

    
    public String getOperationDoc(OperationInfo operation)
    {
        MethodInfo info = findMethodInfo(operation);

        return (info == null ? null : info.getDocumentation() );
    }

    private MethodInfo findMethodInfo(OperationInfo operation){
        
        List operations = (List) operationsDocumentation.get(operation.getName());
        int paramsNr = operation.getMethod().getParameterTypes().length;
        if (operations != null)
        {
            for (int i = 0; i < operations.size(); i++)
            {
                MethodInfo info = (MethodInfo) operations.get(i);
                if (info.getParamsDocumentation().size() == paramsNr)
                {
                    return info;
                }
            }
        }
        
        return null;
        
        
    }
    
    public String getParamters(OperationInfo operation, int index)
    {
        MethodInfo info = findMethodInfo(operation);
        
        
        return (String) (info == null ? null : info.getParamsDocumentation().get(index));
    }
    
    public String getResultDocumentation(OperationInfo operation){
        MethodInfo info = findMethodInfo(operation);
        return (String) (info == null ? null : info.getReturnDocumentation());
    }

    public String getExceptionDocumentation(OperationInfo operation,String className){
        MethodInfo info = findMethodInfo(operation);
        return (String) (info == null ? null : info.getExceptions().get(className));
    }
    public String getServiceDoc()
    {
        return serviceDocumentation;
    }

    public String getServiceDocumentation()
    {
        return serviceDocumentation;
    }

    public void setServiceDocumentation(String serviceDocumentation)
    {
        this.serviceDocumentation = serviceDocumentation;
    }

    void addOperation(String methodName, String documentation, List parameters, String resultDoc, Map exceptions)
    {
        List operations = (List) operationsDocumentation.get(methodName);
        MethodInfo info = new MethodInfo(methodName, parameters);
        info.setDocumentation(documentation);
        info.setReturnDocumentation(resultDoc);
        info.setExceptions(exceptions);
        
        if (operations == null)
        {
            operations = new ArrayList();
            operationsDocumentation.put(methodName, operations);
        }

        operations.add(info);
    }

    /**
     * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
     * 
     */
    private class MethodInfo
    {

        private List paramsDocumentation;

        private String documentation;
        
        private String returnDocumentation;

        private Map exceptions;
        
        
        
        public Map getExceptions()
        {
            return exceptions;
        }

        public void setExceptions(Map exceptions)
        {
            this.exceptions = exceptions;
        }

        public MethodInfo(String operationDoc, List paramsDoc)
        {
            this.documentation = operationDoc;
            this.paramsDocumentation = paramsDoc;
        }

        public List getParamsDocumentation()
        {
            return paramsDocumentation;
        }

        public void setDocumentation(String operationDocumentation)
        {
            this.documentation = operationDocumentation;
        }

        public String getDocumentation()
        {
            return documentation;
        }

        public String getReturnDocumentation()
        {
            return returnDocumentation;
        }

        public void setReturnDocumentation(String returnDocumentation)
        {
            this.returnDocumentation = returnDocumentation;
        }

    }

}
