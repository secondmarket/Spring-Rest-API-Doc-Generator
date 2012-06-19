package jsongen;

import com.google.gson.Gson;
import com.secondmarket.annotatedobject.aclass.AnnotatedClass;

/**
 * Created with IntelliJ IDEA.
 * User: sstern
 * Date: 6/19/12
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSONClassDescriptor {

    public static void makeJSON(Class c) {
        Gson gson = new Gson();
        String json = gson.toJson(c);
        System.out.println(json);
    }

    public static void main(String[] args) {
        makeJSON(AnnotatedClass.class);
    }

}
