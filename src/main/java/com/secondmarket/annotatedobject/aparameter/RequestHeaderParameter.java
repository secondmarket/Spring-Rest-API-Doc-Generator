package com.secondmarket.annotatedobject.aparameter;

import com.secondmarket.annotatedobject.aparameter.AnnotatedParam;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Created with IntelliJ IDEA.
 * User: rdavid
 * Date: 6/7/12
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestHeaderParameter implements AnnotatedParam {

    //Fields
    private String paramName;
    private RequestHeader param;
    private Class paramType;

    public RequestHeaderParameter( RequestHeader rh ,Class param){
        this.param = rh;
        this.paramName = "RequestHeader";
        this.paramType = param;
    }

    public String getValue() {
        return this.param.value();
    }


    public String getType() {
        return this.paramType.getName();
    }

    public RequestHeader getRequestHeader(){
        return this.param;
    }

    public String getAnnotationName(){
        return this.paramName;
    }

    public String printParam() {
        String result="";
        result = "Annotation type: " + this.paramName +  ", parameter value: " + this.param.value() + ", parameter type: " + this.paramType.getName();
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
        mappingType.addText("Parameter should be bound to a web request header " + "[" + this.getAnnotationName() + "]");
        if(!this.getRequestHeader().value().isEmpty()){
            Element value = mappingInfo.addElement("value");
            value.addText(this.getRequestHeader().value());
        }
        Element required = mappingInfo.addElement("required");

        if(this.getRequestHeader().required()){
            required.addText("True");
        }
        else{
            required.addText("False");
        }
        if(!this.getRequestHeader().defaultValue().isEmpty()){
            Element defaultValue = mappingInfo.addElement("defaultvalue");
            defaultValue.addText(this.getRequestHeader().defaultValue());
        }

        return param;
    }
}
