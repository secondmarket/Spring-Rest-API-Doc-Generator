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
 * Created with IntelliJ IDEA.
 * User: rdavid
 * Date: 6/7/12
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotatedClass {

    //Fields
    private ArrayList<AnnotatedMethod> listOfMethods = new ArrayList<AnnotatedMethod>();
    private String className;
    private Class clazz;
    private Annotation[] classAnnotations;
    private String classMapping="";
    private String path="";
    private String methodName="";
    private String action="";


    public AnnotatedClass(Class clazz){
        this.clazz = clazz;
        this.className = clazz.getName();
        this.classAnnotations =  clazz.getAnnotations();
        this.printAndExecuteClassInfo();
        Method[] methods = clazz.getMethods();
        for(Method method: methods){
            if(method.isAnnotationPresent(RequestMapping.class)){
                this.listOfMethods.add(new AnnotatedMethod(method, this.path));
            }
        }

        printAndExecuteClassInfo();
    }

    public String getPath(){
        return this.path;
    }

    public String getMethodName(){
        return this.methodName;
    }

    public String getClassMapping(){
        return this.classMapping;
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

    public void printAndExecuteClassInfo(){
        //System.out.println("Class Name: " + this.className);

        for(Annotation ca: classAnnotations){
            if(ca.annotationType().getCanonicalName().contains("RequestMapping")){
                classMapping+= "Class annotation: " + ca.annotationType();

                //System.out.println(ca.annotationType());
                RequestMapping rm = (RequestMapping) ca;


                RequestMethod[] reqMethod = rm.method();
                int countClassMethods = 1;
                classMapping+=", RequestMapping Methods: ";

                //System.out.println("RequestMapping Methods: ");
                for(RequestMethod rMet: reqMethod){
                    this.methodName = rMet.name();
                    classMapping+= ", Method #"+countClassMethods +": "+  rMet.name();
                    //System.out.println("Method #"+countClassMethods +": "+  rMet.name());
                    countClassMethods++;
                }

                String[] classHeaders = rm.headers();
                String header="";
                for(String h: classHeaders){
                    header+=h;
                }

                classMapping+=", RequestMapping Header: " + header;
                //System.out.println("RequestMapping Header: " + header);

                String[] classValue = rm.value();
                String value="";
                for(String v: classValue){
                    value+=v;
                }
                if (classValue.length > 0) {
                    this.path = classValue[0];
                }
                classMapping+= "RequestMapping Class Path: " + value;
                ////System.out.println("RequestMapping Class Path: " + value);

            }
        }

        //System.out.println("class Methods: ");
        for(AnnotatedMethod am: this.listOfMethods){

            //System.out.println(am.toString());
            //System.out.println();
        }
    }

    public Element toXML(Document doc){

        Element root = doc.addElement("annotatedclass");
        Element clazz = root.addElement("name");
        clazz.addText(this.getClassName());

        if(!this.getPath().isEmpty()){
            Element mapping = root.addElement("mapping");
            mapping.addText(this.getPath());
        }

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

    public void saveToXML(String location) throws IOException {
        String path = this.getPath();
        //Delete existing
        File dir = new File(location);
        if (dir.exists()) {
            dir.delete();
        }
        //Make it a local directory
        path = location + path;
        for (AnnotatedMethod am : listOfMethods) {
            //Right now, not using cleaned path
            //TODO Evaluate this strategy
            String methodPath = am.getPath();
            String requestMethod = am.getRequestMethod();
            Element methodXML = am.toXML();
            Document methodDoc = DocumentFactory.getInstance().createDocument();
            methodDoc.setRootElement(methodXML);
            //save
            String totalPath = path + "/" + methodPath + "/" + requestMethod + ".xml";
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

}
