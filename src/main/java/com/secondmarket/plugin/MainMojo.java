package com.secondmarket.plugin;

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello World, from the Plugin!");
        getLog().info("Scanning for Controllers");
        getLog().info("Package: " + packageName.toString());
        getLog().info("Resolving Parent Class Loader");
        try {
            getClasses();
        } catch (Exception e) {
            getLog().error(e);
        }
        Reflections reflections = new Reflections(packageName.toString());
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        for (Class c : controllers) {
            getLog().info(c.getName());
        }
        XMLGenerator.generateXML(controllers, rootURL.toString(), docDestination.toString());
        SummaryGenerator sg = new SummaryGenerator(docDestination.toString());
        try {
            sg.writeDocument("summary.xml");
        } catch (IOException e) {
            getLog().error(e);
        }
    }

    /**
     * Magic
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
            getLog().info(e);
            File elementFile = new File(e);
            URL url = new URL("file:///" + elementFile.getPath() + (elementFile.isDirectory() ? "/" : ""));
            realm.addConstituent(url);
        }
        Thread.currentThread().setContextClassLoader(realm.getClassLoader());
    }
}
