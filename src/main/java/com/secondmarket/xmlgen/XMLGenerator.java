package com.secondmarket.xmlgen;

import com.secondmarket.annotatedobject.aclass.AnnotatedClass;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sstern
 * Date: 6/19/12
 * Time: 11:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class XMLGenerator {

    public static void generateXML(Set<Class<?>> controllers, String destination) {
        try {
            for (Class c : controllers) {
                Document document = DocumentFactory.getInstance().createDocument();
                AnnotatedClass ac = new AnnotatedClass(c);
                ac.toXML(document);
                ac.saveToXML(destination);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
