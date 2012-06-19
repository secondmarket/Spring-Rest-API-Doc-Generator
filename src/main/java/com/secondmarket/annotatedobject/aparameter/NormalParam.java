package com.secondmarket.annotatedobject.aparameter;

import com.secondmarket.annotatedobject.aparameter.AnnotatedParam;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Created with IntelliJ IDEA.
 * User: rdavid
 * Date: 6/7/12
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class NormalParam implements AnnotatedParam {
    //Fields
    private String paramName;
    private Class param;

    public String getParam(){
        return this.paramName;
    }


    public NormalParam(Class param){
        this.param = param;
        this.paramName = param.getName();
    }


    public String getValue() {
        return this.param.getCanonicalName();
    }

    public String getType() {
        return this.param.getName();
    }

    public String getName() {
        return this.paramName;
    }

    public String printParam() {
        String result = "";
        result = "Parameter type: " + this.param.getName();
        return result;
    }

    public Element toXML() {
        Element param = DocumentHelper.createElement("parameter");
        Element name = param.addElement("name");
        name.addText(this.getName());
        Element mapped = param.addElement("mapped");
        mapped.addText("False");
        return param;
    }
}
