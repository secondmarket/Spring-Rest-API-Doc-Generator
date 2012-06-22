package com.secondmarket.jsongen;

import com.secondmarket.annotatedobject.amethod.AnnotatedMethod;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * main.User: rdavid
 * Date: 6/19/12
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassConverter {

    //Fields
    private Field[] fields;
    private static Set<Class> classes = new HashSet<Class>();
    private String location;

    public ClassConverter(Class clazz, String location){
        this.fields = getAllFields(clazz);
        this.location = location;
        searchForClasses(clazz, this.fields);
    }

    public void searchForClasses(Class clazzLocation, Field[] lof){
        JSONObject jObj = new JSONObject();
        for(Field f: lof){
            Class parameterizedType = getParameterizedType(f);
            String fieldName = f.getName();
            Type fieldGenericType = f.getGenericType();
            Class<?> fieldType = f.getType();

            if((parameterizedType != null) && !parameterizedType.getName().startsWith("java")){
                classes.add(parameterizedType);
                Field[] SMInGenericClass = getAllFields(parameterizedType);
                addGenericField(jObj, fieldName, fieldGenericType.toString(), parameterizedType.getName().toString());
                generateJSON(parameterizedType);
            } else if(!fieldType.isPrimitive() && !fieldType.getName().startsWith("java")){
                classes.add(fieldType);
                Field[] SMClasses = getAllFields(fieldType);
                addGenericField(jObj, fieldName, fieldType.getName().toString(), fieldType.getName().toString());
                //generateJSON(fieldType);
            } else{
                addField(jObj, fieldName, fieldGenericType.toString());
            }
        }
        String jsonFileLocation = AnnotatedMethod.pathMash(this.location,clazzLocation.getName() + ".json");
        writeToFile(jObj, jsonFileLocation);
    }

    public void addField(JSONObject jObj, String fieldName, String type) {
        jObj.put(fieldName, type);
    }

    public void addGenericField(JSONObject jObj, String fieldName, String name, String replace) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("replace", replace);
        jObj.put(fieldName, jsonObject);
    }

    public void writeToFile(JSONObject jsonObject, String location) {
        File f = new File(location);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
        } else {
            f.delete();
        }
        try {
            FileWriter file = new FileWriter(location);
            file.write(jsonObject.toJSONString());
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

    public static Class getParameterizedType(Field field) {
        try {
            if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                return ((Class) (genericType.getActualTypeArguments()[0]));
            } else {
                return (null);
            }
        } catch (ClassCastException e) {
            System.err.println("Error with class casting " + field.getName());
            return (null);
        }
    }

    public void generateJSON(Class clazz) {
        Field[] SMClasses = getAllFields(clazz);
        searchForClasses(clazz, SMClasses);
    }

    public static void main(String[] args) {
        class Nonsense {
            ArrayList<String> stringArrayList = new ArrayList<String>();
        }
        Field[] fields0 = (Nonsense.class).getDeclaredFields();
        Field f0 = fields0[0];
        String typeString0 = f0.getType().toString();
        String genTypeString0 = f0.getGenericType().toString();
        String fieldGenericTypeString0 = ClassConverter.getParameterizedType(f0).toString();
        System.out.println(typeString0);
        System.out.println(genTypeString0);
        System.out.println(fieldGenericTypeString0);
        System.out.println();
        class Nonsense1 {
            HttpHeaders httpHeaders = new HttpHeaders();
        }
        Field[] fields = (Nonsense1.class).getDeclaredFields();
        Field f1 = fields[0];
        String typeString1 = f1.getType().getName().toString();
        String genTypeString1 = f1.getGenericType().toString();
        //String fieldGenericTypeString1 = ClassConverter.getParameterizedType(f1).toString();
        System.out.println(typeString1);
        System.out.println(genTypeString1);
        System.out.println(ClassConverter.getParameterizedType(f0).getName().toString());
        //System.out.println(fieldGenericTypeString1);
    }
}