package com.secondmarket.annotatedobject.amethod;

import com.secondmarket.annotatedobject.aparameter.*;
import com.secondmarket.jsongen.JSONGenerator;
import com.secondmarket.xmlgen.XMLGenerator;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: rdavid
 * Date: 6/7/12
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotatedMethod {

    private Method method;
    private Class returnType;
    private String requestMethod="";
    private String path="";
    private String parentURL;
    private ArrayList<AnnotatedParam> listOfParams = new ArrayList<AnnotatedParam>();

    public AnnotatedMethod(Method method, String parentURL){
        this.method = method;
        this.parentURL = parentURL;
        this.returnType = method.getReturnType();
        JSONGenerator.addClass(this.returnType);
        init();
    }

    public void init() {
        initPathandMethod();
        initParameters();
    }

    /**
     * Check for @RequestMapping annotation and, if present, extract path and method
     * information
     */
    private void initPathandMethod() {
        Annotation[] mapping =  method.getDeclaredAnnotations();
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            String[] listPath = requestMapping.value();
            if (listPath.length > 0) {
                this.path = listPath[0];
            }
            RequestMethod[] listRequestMethod = requestMapping.method();
            if(listRequestMethod.length > 0) {
                this.requestMethod = listRequestMethod[0].toString();
            }
        }
    }

    /**
     * Analyze and cast all parameters of the method, inlcuding parameter
     * annotations
     */
    private void initParameters() {
        Class[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for(int i = 0; i < parameterAnnotations.length; i++){
            Annotation[] annotations = parameterAnnotations[i];
            if (annotations.length == 0) {
                NormalParam np = new NormalParam(parameterTypes[i]);
                listOfParams.add(np);
            } else {
                for(Annotation ann : annotations){
                    Annotation myAnnotation =  (Annotation) ann;
                    if(ann instanceof PathVariable){
                        listOfParams.add(addParamPathVariable(myAnnotation, parameterTypes[i]));
                    }
                    else if(ann instanceof RequestHeader){
                        listOfParams.add(addParamRequestHeader(myAnnotation, parameterTypes[i]));
                    }
                    else if(ann instanceof RequestBody){
                        listOfParams.add(addParamRequestBody(myAnnotation, parameterTypes[i]));
                    }
                    else if(ann instanceof RequestParam){
                        listOfParams.add(addParamRequestParam(myAnnotation, parameterTypes[i]));
                    }
                }
            }
        }
    }

    /**
     * Convert Method to an XML Element
     * @return
     */
    public Element toXML(){
        Element method = DocumentHelper.createElement("method");
        Element name = method.addElement("name");
        name.addText(this.method.getName());
        Element mapping = method.addElement("mapping");
        mapping.addText(pathMash(this.parentURL, this.getPath()));
        Element action = method.addElement("action");
        action.addText(this.requestMethod);
        if(!this.listOfParams.isEmpty()) {
            Element parameter = method.addElement("parameters");
            for(AnnotatedParam ap: listOfParams){
                parameter.add(ap.toXML());
            }
        }
        Element returnType = method.addElement("returntype");
        returnType.addText(this.method.getReturnType().getName());
        return method;
    }

    /**
     * Convert to XML Element with an additional prefix on the URL path
     * @param root
     * @return
     */
    public Element toXML(String root) {
        this.parentURL = pathMash(root, parentURL);
        return toXML();
    }

    //==========================================================================
    //===========================PARAM CASTING==================================
    //==========================================================================

    public PathVariableParameter addParamPathVariable(Annotation ann, Class param){
        PathVariable pv =  (PathVariable) ann;
        PathVariableParameter pathVarParam = new PathVariableParameter(pv, param);
        return pathVarParam;
    }

    public RequestHeaderParameter addParamRequestHeader(Annotation ann, Class param){
        RequestHeader rh =  (RequestHeader) ann;
        RequestHeaderParameter reqHeadParam = new RequestHeaderParameter(rh, param);
        return reqHeadParam;
    }

    public RequestBodyParameter addParamRequestBody(Annotation ann, Class param){
        RequestBody rb =  (RequestBody) ann;
        RequestBodyParameter reqBodyParam = new RequestBodyParameter(rb, param);
        return reqBodyParam;
    }

    public RequestParameter addParamRequestParam(Annotation ann, Class param){
        RequestParam rp =  (RequestParam) ann;
        RequestParameter reqParam = new RequestParameter(rp, param);
        return reqParam;
    }

    //==========================================================================
    //===========================GETTERS========================================
    //==========================================================================

    public ArrayList<AnnotatedParam> getAllParams(){
        return this.listOfParams;
    }

    public String getPath(){
        return this.path;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    //==========================================================================          s
    //============================STATIC========================================
    //==========================================================================

    /**
     * Static helper to combine paths
     * @param first
     * @param second
     * @return
     */
    public static String pathMash(String first, String second) {
        if (first.endsWith("/") && second.startsWith("/")) {
            return first + second.substring(1);
        } else if (first.endsWith("/") || second.startsWith("/")) {
            return first + second;
        } else {
            return first + "/" + second;
        }
    }
}