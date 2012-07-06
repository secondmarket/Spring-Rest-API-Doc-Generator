package com.secondmarket.jsongen;

import java.util.HashSet;
import java.util.Set;

public class JSONGenerator {

    public static Set<Class> encounteredClasses = new HashSet<Class>();

    /**
     * Generate all JSON files for the project.
     * @param location the location to save the files
     */
    public static void generateJSON(String location){
        for (Class c : encounteredClasses) {
            ClassConverter cc = new ClassConverter(c, location);
            cc.start();
        }
    }

    /**
     * Add a class to the list of "seen" types
     * @param c
     */
    public static void addClass(Class c) {
        encounteredClasses.add(c);
    }
}
