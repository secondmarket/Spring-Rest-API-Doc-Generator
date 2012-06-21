package com.secondmarket.annotatedobject.aparameter;

import com.secondmarket.annotatedobject.aparameter.AnnotatedParam;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created with IntelliJ IDEA.
 * User: rdavid
 * Date: 6/7/12
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PathVariableParameter implements AnnotatedParam {
    //Fields
    private String paramName;
    private PathVariable param;
    private Class paramType;

    public PathVariableParameter(PathVariable pathVariable, Class param) {
        this.param = pathVariable;
        this.paramName = "PathVariable";
        this.paramType = param;

    }

    public Element toXML() {
        Element param = DocumentHelper.createElement("parameter");
        Element name = param.addElement("name");
        name.addText(this.getType());
        Element mapped = param.addElement("mapped");
        mapped.addText("True");
        Element mappingInfo = param.addElement("mappinginfo");
        Element mappingType = mappingInfo.addElement("mappingtype");
        mappingType.addText("Parameter should be bound to a URI template " + "[" + this.getAnnotationName() + "]");
        if(!this.getPathVariable().value().isEmpty()){
            Element value = mappingInfo.addElement("value");
            value.addText(this.getPathVariable().value());
        }
        return param;
    }

    //==========================================================================
    //===========================GETTERS========================================
    //==========================================================================

    public PathVariable getPathVariable() {
        return param;
    }

    public String getValue() {
        return this.param.value();
    }

    public String getType() {
        return this.paramType.getName();
    }

    public String getAnnotationName(){
        return this.paramName;
    }

}
