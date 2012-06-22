package com.secondmarket.jsongen;

import com.secondmarket.annotatedobject.amethod.AnnotatedMethod;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * main.User: rdavid
 * Date: 6/19/12
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassConverter {

    //Fields
    private Field[] listOfFields;
    private static ArrayList<Class> listOfClasses = new ArrayList<Class>();
    private String location;

    public ClassConverter(Class clazz, String location){
        //System.out.println("Constructor gets: " + clazz.getName());
        System.out.println();
        this.listOfFields = getAllFields(clazz);
        this.location = location;
        searchForSMClasses(clazz,this.listOfFields);
    }

    public void searchForSMClasses(Class clazzLocation, Field[] lof){
        //System.out.println("NOW LOOKING AT:"  + clazz.getName());
        JSONObject jObj = new JSONObject();
        for(Field f: lof){

            //Get generic type.
            Class genericType = getFieldGenericType(f);

            //If SM object as a generic type.
            if(!genericType.getCanonicalName().endsWith("Boolean") && !genericType.getName().startsWith("java")){
                listOfClasses.add(genericType);
                Field[] SMInGenericClass = getAllFields(genericType);
                JSONObject jsonGeneric = new JSONObject();
                jsonGeneric.put("name", f.getGenericType().toString());
                jsonGeneric.put("replace", genericType.getName());
                jObj.put(f.getName(),jsonGeneric);
                generateJSON(genericType);
            }

            //If regular SM object.
            else if(!f.getType().isPrimitive()
                    && !(f.getType().getName().startsWith("java."))
                    && !listOfClasses.contains(f.getType())){

                listOfClasses.add(f.getType());
                Class clazz = f.getType();
                Field[] SMClasses = getAllFields(f.getType());
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("name", f.getGenericType().toString());
                jsonObj.put("replace", f.getGenericType().toString());
                jObj.put(f.getName(), jsonObj);
                generateJSON(f.getType());
            }
            //Else
            else{
                jObj.put(f.getName(), f.getGenericType().toString());
            }
        }

        String JSONClass = AnnotatedMethod.pathMash(this.location,clazzLocation.getName() + ".json");
        File f = new File(JSONClass);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
        } else {
            f.delete();
        }

        //write the JSONObject to a file.
        try {
            FileWriter file = new FileWriter(JSONClass);
            file.write(jObj.toJSONString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Field[] getAllFields(Class klass) {
        List<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(klass.getDeclaredFields()));

        if (klass.getSuperclass() != null) {
            fields.addAll(Arrays.asList(getAllFields(klass.getSuperclass())));
        }
        return fields.toArray(new Field[] {});
    }

    //Returns generic type of any field
    public Class getFieldGenericType(Field field) {
        try {
            if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                return ((Class) (genericType.getActualTypeArguments()[0]));
            } else {
                //Returns dummy Boolean Class to compare with ValueObject & FormBean
                return new Boolean(false).getClass();
            }
        } catch (ClassCastException e) {
            System.err.println("Error with class casting " + field.getName());
            return new Boolean(false).getClass();
        }
    }

    public void generateJSON(Class clazz){
        Field[] SMClasses = getAllFields(clazz);
        searchForSMClasses(clazz,SMClasses);
    }
}