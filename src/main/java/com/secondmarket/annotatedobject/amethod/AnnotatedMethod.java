package com.secondmarket.annotatedobject.amethod;

import com.secondmarket.annotatedobject.aparameter.*;
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

    //Fields
    private Method method;
    private Class returnType;
    private String requestMethod="";
    private String path="";
    private String parentURL;
    private ArrayList<AnnotatedParam> listOfParams = new ArrayList<AnnotatedParam>();

    //Pattern to represent /{.....}/
    private static String paramPattern = "/\\{[^/]*\\}/";

    public AnnotatedMethod(Method method, String parentURL){
        this.method = method;
        this.parentURL = parentURL;
        this.returnType = method.getReturnType();
        init();
    }

    public void init(){

        //Mapping for the method parameters.
        Annotation[] mapping =  method.getDeclaredAnnotations();

        for (Annotation ann: mapping){
            if(ann instanceof RequestMapping){
                RequestMapping rm =  (RequestMapping) ann;
                String[] listPath=  rm.value();

                for(String reqPath: listPath){
                    this.path+= reqPath;
                }

                RequestMethod[] listRequestMethod = ((RequestMapping) ann).method();

                for(RequestMethod rMet: listRequestMethod){
                    this.requestMethod+= rMet;

                }
            }
        }

        Class[] regularParams = method.getParameterTypes();
        int countParams = 0;

        Annotation[][] paramClass = method.getParameterAnnotations();

        //Send the Method's parameters to the appropriate param class.
        for(int i = 0; i < paramClass.length; i++){
            Annotation[] annotations = paramClass[i];

            if (annotations.length == 0) {
                NormalParam np = new NormalParam(regularParams[i]);
                listOfParams.add(np);

            } else {
                for(Annotation ann : annotations){
                    Annotation myAnnotation =  (Annotation) ann;

                    if(ann instanceof PathVariable){
                        listOfParams.add(addParamPathVariable(myAnnotation, regularParams[countParams]));
                    }
                    else if(ann instanceof RequestHeader){
                        listOfParams.add(addParamRequestHeader(myAnnotation,regularParams[countParams]));
                    }
                    else if(ann instanceof RequestBody){
                        listOfParams.add(addParamRequestBody(myAnnotation, regularParams[countParams]));
                    }
                    else if(ann instanceof RequestParam){
                        listOfParams.add(addParamRequestParam(myAnnotation, regularParams[countParams]));
                    }
                }
            }
            countParams++;
        }
    }

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

    public String toString(){
        String result ="";
        result = "Method name: " + this.method.getName() + ", Return value: " + this.returnType
                + ", Annotation Path: " + this.path + ", Annotation method: " + this.requestMethod +  ", Parameter: ";

        for(AnnotatedParam ap: this.listOfParams){
            result+= ap.printParam() + " ";

        }

        return result;
    }

    public ArrayList<AnnotatedParam> getAllParams(){
        return this.listOfParams;
    }

    public String getPath(){
        return this.path;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    /**
     * Clean all of the URL parameters out of the path
     * @return
     */
    public String getCleanPath() {
        return this.path.replaceAll(paramPattern, "");
    }

    public Element toXML(){
        Element method = DocumentHelper.createElement("method");
        Element name = method.addElement("name");
        name.addText(this.method.getName());
        if(!this.getPath().isEmpty()){
            Element mapping = method.addElement("mapping");
            mapping.addText(this.parentURL + "/" + this.getPath());
        }
        if(!this.getPath().isEmpty()){
            Element action = method.addElement("action");
            action.addText(this.requestMethod);
        }

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

}