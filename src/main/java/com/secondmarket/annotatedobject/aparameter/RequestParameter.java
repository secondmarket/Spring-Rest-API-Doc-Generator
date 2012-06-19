package com.secondmarket.annotatedobject.aparameter;

import com.secondmarket.annotatedobject.aparameter.AnnotatedParam;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: rdavid
 * Date: 6/7/12
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestParameter implements AnnotatedParam {
    //Fields
    private RequestParam requestParam;
    private String paramName;
    private Class paramType;

    public RequestParameter (RequestParam rp, Class param){
        this.requestParam = rp;
        this.paramName = "RequestParam";
        this.paramType = param;
    }

    public String getValue() {
        return this.requestParam.value();
    }

    public String getType() {
        return this.paramType.getName();
    }

    public RequestParam getRequestParam(){
        return this.requestParam;
    }
    public String printParam() {
        String result="";
        result = "Annotation type: " +  this.paramName + ", parameter value: " + this.requestParam.value() + ", parameter Default: " + this.requestParam.defaultValue() + ", parameter type: " + this.paramType.getName();
        return result;
    }

    public String getAnnotationName(){
        return this.paramName;
    }


    public Element toXML() {
        Element param = DocumentHelper.createElement("parameter");
        Element name = param.addElement("name");
        name.addText(this.getType());
        Element mapped = param.addElement("mapped");
        mapped.addText("True");
        Element mappingInfo = param.addElement("mappinginfo");
        Element mappingType = mappingInfo.addElement("mappingtype");
        mappingType.addText("Parameter should be bound to a web request parameter "+ "[" + this.getAnnotationName() + "]");
        if(!this.requestParam.value().isEmpty()) {
            Element value = mappingInfo.addElement("value");
            value.addText(this.requestParam.value());
        }
        Element required = mappingInfo.addElement("required");

        if(this.getRequestParam().required()){
            required.addText("True");
        }
        else{
            required.addText("False");
        }
        if(!this.getRequestParam().defaultValue().isEmpty()) {
            Element defaultValue = mappingInfo.addElement("defaultvalue");
            //System.out.println("Default value is: " + this.getRequestParam().defaultValue());
            defaultValue.addText(this.getRequestParam().defaultValue());
        }
        return param;
    }

}
