package com.secondmarket.annotatedobject.aparameter;

import com.secondmarket.annotatedobject.aparameter.AnnotatedParam;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created with IntelliJ IDEA.
 * User: rdavid
 * Date: 6/7/12
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestBodyParameter implements AnnotatedParam {
    //Fields
    private String paramName;
    private RequestBody param;
    private Class paramType;

    public RequestBodyParameter( RequestBody rb, Class param){
        this.param = rb;
        this.paramName = "RequestBody";
        this.paramType = param;

    }

    public String getValue() {
        return null;
    }

    public String getType() {
        return this.paramType.getName();
    }

    public RequestBody getRequestBody(){
        return this.param;
    }

    public String getAnnotationName(){
        return this.paramName;
    }

    public String printParam() {
        String result="";
        result = "Annotation type: " + this.paramName + ", parameter type: " + this.paramType.getName();
        return result;
    }

    public Element toXML() {
        Element param = DocumentHelper.createElement("parameter");
        Element name = param.addElement("name");
        name.addText(this.getType());
        Element mapped = param.addElement("mapped");
        mapped.addText("True");
        Element mappingInfo = param.addElement("mappinginfo");
        Element mappingType = mappingInfo.addElement("mappingtype");
        mappingType.addText("Parameter should be bound to the web request body " + "[" + this.getAnnotationName() + "]");
        return param;
    }

}
