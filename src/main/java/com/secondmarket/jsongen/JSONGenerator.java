package com.secondmarket.jsongen;

import java.util.HashSet;
import java.util.Set;

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
