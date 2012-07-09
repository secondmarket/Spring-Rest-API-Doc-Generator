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
    private String path="";
    private String methodName="";
    private String action="";

    public AnnotatedClass(Class clazz){
        this.clazz = clazz;
        this.className = clazz.getName();
        this.initialize();
        try {
            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(method.isAnnotationPresent(RequestMapping.class)){
                    this.listOfMethods.add(new AnnotatedMethod(method, this.path));
                }
            }
        } catch (NoClassDefFoundError error) {
            System.err.println("No Class Def Found Error: " + className);
        }
    }

    /**
     * Check for an @RequestMapping annotation on the class and, if present, store
     * the RequestMethod and the URL mapping
     */
    public void initialize() {
        Annotation ann = clazz.getAnnotation(RequestMapping.class);
        if (ann != null && (ann instanceof RequestMapping)) {
            RequestMapping rm = (RequestMapping) ann;
            RequestMethod[] reqMethod = rm.method();
            if (reqMethod.length > 0) {
                this.methodName = reqMethod[0].name();
            }
            String[] classValue = rm.value();
            if (classValue.length > 0) {
                this.path = classValue[0];
            }
        }
    }

    /**
     * Create an XML representation of the Annotated Class, passing a Document to which the
     * XML should be added.
     * @param doc generally an emtpy Document
     * @return
     */
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

    /**
     * Iterate through all AnnotatedMethods belonging to this class and store each one in a
     * properly organized XML file mirroring the REST api in the directory passed to the application.
     * @param rootURL the relative root URL for set of Controllers to which this class belongs
     *                ex: /home/users NOT http://foo.com/bar/
     * @param location the location in which to save the XML files belonging to the methods of this class
     * @throws IOException
     */
    public void saveToXML(String rootURL, String location) throws IOException {
        String path = this.getPath();
        File dir = new File(location);
        if (dir.exists()) {
            dir.delete();
        }
        //Make it a local directory
        String pathWithRoot = AnnotatedMethod.pathMash(rootURL, path);
        path = AnnotatedMethod.pathMash(location, pathWithRoot);
        for (AnnotatedMethod am : listOfMethods) {
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
            writeFile(totalPath, methodDoc);
        }
    }

    /**
     * Write an XML document to a path, creating the appropriate folder structure
     * if necessary
     * @param path the local path to the file
     * @param document the Document object to be written to disk
     * @throws IOException
     */
    private void writeFile(String path, Document document) throws IOException {
        File f = new File(path);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } else {
            f.delete();
        }
        FileOutputStream fos = new FileOutputStream(path);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(fos, format);
        writer.write(document);
        writer.flush();
        fos.close();
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
