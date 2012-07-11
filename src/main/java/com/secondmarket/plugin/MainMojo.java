package com.secondmarket.plugin;

import com.secondmarket.annotatedobject.amethod.AnnotatedMethod;
import com.secondmarket.jsongen.JSONGenerator;
import com.secondmarket.xmlgen.SummaryGenerator;
import com.secondmarket.xmlgen.XMLGenerator;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sstern
 * Date: 6/18/12
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Main Mojo for the Reflection Docs Plugin
 * @goal generatedocs
 * @requiresProject true
 * @requiresDependencyResolution runtime
 */
public class MainMojo extends AbstractMojo {

    /**
     * @parameter
     * @required
     */
    private Object packageName;

    /**
     * @parameter
     * @required
     */
    private Object docDestination;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter
     * @required
     */
    private Object rootURL;

    /**
     * @parameter expression="${docs.generate}" default-value="false"
     */
    private Object generate;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello World, from the Plugin!");
        getLog().info("Scanning for Controllers");
        getLog().info("Package: " + packageName.toString());
        getLog().info("Resolving Parent Class Loader");
        Boolean shouldRun = Boolean.parseBoolean((String) generate);
        getLog().info("The value of generate is: " + shouldRun.toString());
        //By default, don't run
        if (shouldRun) {
            try {
                getClasses();
            } catch (Exception e) {
                getLog().error(e);
            }
            //Clear old files
            clearDestination();
            //Write new ones
            writeXML();
            writeSummary();
            writeJSON();
        }
    }

    /**
     * Delete docDestination/json and docDestination/xml recursively
     */
    private void clearDestination() {
        String docDest = docDestination.toString();
        String xmlPath = AnnotatedMethod.pathMash(docDest, "/xml/");
        File xmlFile = new File (xmlPath);
        String jsonPath = AnnotatedMethod.pathMash(docDest, "/json/");
        File jsonFile = new File(jsonPath);
        getLog().info("Deleting old XML files");
        deleteDirectory(xmlFile);
        getLog().info("Deleting old JSON files");
        deleteDirectory(jsonFile);
    }

    private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return path.delete();
    }

    /**
     * Generate a hierarchy of XML files representing the REST API
     */
    private void writeXML() {
        Reflections reflections = new Reflections(packageName.toString());
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        String XMLPath = AnnotatedMethod.pathMash(docDestination.toString(), "/xml");
        XMLGenerator.generateXML(controllers, rootURL.toString(), XMLPath);
    }

    /**
     * Generate an XML summary file of all XML files generated,
     */
    private void writeSummary() {
        String XMLPath = AnnotatedMethod.pathMash(docDestination.toString(), "/xml");
        SummaryGenerator sg = new SummaryGenerator(XMLPath);
        try {
            sg.writeDocument("summary.xml");
        } catch (IOException e) {
            getLog().error(e);
        }
    }

    /**
     * Generate a JSON description of all non standard classes in the project.
     */
    private void writeJSON() {
        String JSONPath = AnnotatedMethod.pathMash(docDestination.toString(), "/json");
        JSONGenerator.generateJSON(JSONPath);
    }

    /**
     * Magic method to access the classpath of the containing project.
     * @throws DependencyResolutionRequiredException
     * @throws DuplicateRealmException
     * @throws MalformedURLException
     */
    private void getClasses() throws DependencyResolutionRequiredException, DuplicateRealmException, MalformedURLException {
        List<String> elements = project.getRuntimeClasspathElements();
        ClassWorld world = new ClassWorld();
        ClassRealm realm;
        realm = world.newRealm("maven.plugin." + getClass().getSimpleName(), Thread.currentThread().getContextClassLoader());
        for (String e : elements) {
            File elementFile = new File(e);
            URL url = new URL("file:///" + elementFile.getPath() + (elementFile.isDirectory() ? "/" : ""));
            realm.addConstituent(url);
        }
        Thread.currentThread().setContextClassLoader(realm.getClassLoader());
    }
}
