package com.secondmarket.jsongen;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sstern
 * Date: 6/21/12
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSONGenerator {

    public static Set<Class> returnTypeClasses = new HashSet<Class>();

    public static void generateJSON(String location){
        for (Class c : returnTypeClasses) {
            ClassConverter cc = new ClassConverter(c, location);
        }
    }

    public static void addClass(Class c) {
        returnTypeClasses.add(c);
    }
}
