package com.secondmarket.annotatedobject.aclass;

import com.secondmarket.annotatedobject.amethod.AnnotatedMethod;
import com.sun.servicetag.SystemEnvironment;
import org.dom4j.DocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.dom4j.Element;
import org.dom4j.Document;

/**
 * Represents a class to be reflected and described
 */
public class AnnotatedClass {

    private ArrayList<AnnotatedMethod> listOfMethods = new ArrayList<AnnotatedMethod>();
    private String className;
    private Class clazz;
    private Annotation[] classAnnotations;
    private String path="";
    private String methodName="";
    private String action="";


    public AnnotatedClass(Class clazz){
        this.clazz = clazz;
        this.className = clazz.getName();
        this.classAnnotations =  clazz.getAnnotations();
        this.initialize();
        Method[] methods = clazz.getMethods();
        for(Method method: methods){
            if(method.isAnnotationPresent(RequestMapping.class)){
                this.listOfMethods.add(new AnnotatedMethod(method, this.path));
            }
        }
    }



    public void initialize(){
        for(Annotation ca: classAnnotations){
            if(ca.annotationType().getCanonicalName().contains("RequestMapping")){
                RequestMapping rm = (RequestMapping) ca;
                RequestMethod[] reqMethod = rm.method();
                for(RequestMethod rMet: reqMethod){
                    this.methodName = rMet.name();
                }
                String[] classValue = rm.value();
                if (classValue.length > 0) {
                    this.path = classValue[0];
                }
            }
        }
    }

    public Element toXML(Document doc){
        Element root = doc.addElement("annotatedclass");
        Element clazz = root.addElement("name");
        clazz.addText(this.getClassName());
        Element mapping = root.addElement("mapping");
        mapping.addText(this.getPath());
        if(!this.getAction().isEmpty()){
            Element action = root.addElement("action");
            action.addText(this.getAction());
        }
        if(!this.listOfMethods.isEmpty()){
            Element methods = root.addElement("methods");

            for(AnnotatedMethod am: listOfMethods){

                methods.add(am.toXML());
            }
        }
        return root;
    }

    public void saveToXML(String rootURL, String location) throws IOException {
        String path = this.getPath();
        //Delete existing
        File dir = new File(location);
        if (dir.exists()) {
            dir.delete();
        }
        //Make it a local directory
        String pathWithRoot = AnnotatedMethod.pathMash(rootURL, path);
        path = AnnotatedMethod.pathMash(location, pathWithRoot);
        for (AnnotatedMethod am : listOfMethods) {
            //Right now, not using cleaned path
            String methodPath = am.getPath();
            String requestMethod = am.getRequestMethod();
            //add the root to the method before making XML
            Element methodXML = am.toXML(rootURL);
            Document methodDoc = DocumentFactory.getInstance().createDocument();
            methodDoc.setRootElement(methodXML);
            //save
            String folderPath = AnnotatedMethod.pathMash(path, methodPath);
            String totalPath = AnnotatedMethod.pathMash(folderPath, requestMethod + ".xml");
            //Make directory structure
            File f = new File(totalPath);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } else {
                f.delete();
            }
            FileOutputStream fos = new FileOutputStream(totalPath);
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(fos, format);
            writer.write(methodDoc);
            writer.flush();
            fos.close();
        }
    }


    //==========================================================================
    //===========================GETTERS========================================
    //==========================================================================

    public String getPath(){
        return this.path;
    }

    public String getMethodName(){
        return this.methodName;
    }

    public ArrayList<AnnotatedMethod> getAnnotatedMethods(){
        return this.listOfMethods;
    }

    public String getClassName(){
        return this.className;
    }

    public String getAction(){
        return this.action;
    }

}
