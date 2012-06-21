package com.secondmarket.annotatedobject.aparameter;

import org.dom4j.Element;

/**
 * Created with IntelliJ IDEA.
 * User: rdavid
 * Date: 6/7/12
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AnnotatedParam {
    public String getValue();
    public String getType();
    public Element toXML();
}
