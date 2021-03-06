# Spring MVC REST API Documentation Generator

### What is this project?
This is a Maven Plugin for Spring MVC projects to automatically generate XML-based documentation for the REST API outlined in the project's Controllers source files.

### How does it work?
The Maven plugin runs during the "package" phase of your build/install process, and scans the compiled source files using Java Reflection.  By default, the plugin will not run during your install/deploy process.  Please consult the "How Do I Run The Plugin?" section below for more information.

### What's the point?
This tool will allow you to generate an automatic fresh version of your API documentation every time you build.  You can build your own interface to interpret the XML and JSON files generated by this plugin into HTML or some other human-readable format.

### How do I configure the plugin?
Install the plugin like any other Maven plugin, and then add the following to your pom.xml in the project you wish to document:
<pre>
	&lt;build&gt;
	    &lt;plugins&gt;
	        &lt;plugin&gt;
	            &lt;groupId&gt;MavenReflectionPlugin&lt;/groupId&gt;
	            &lt;artifactId&gt;MavenReflectionPlugin&lt;/artifactId&gt;
	            &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
	            &lt;executions&gt;
	                &lt;execution&gt;
	                    &lt;id&gt;DocExecution&lt;/id&gt;
	                    &lt;phase&gt;package&lt;/phase&gt;
	                    &lt;goals&gt;
	                        &lt;goal&gt;generatedocs&lt;/goal&gt;
	                    &lt;/goals&gt;
	                    &lt;configuration&gt;
	                        &lt;!-- Package to scan --&gt;
	                        &lt;packageName&gt;your.package.name&lt;/packageName&gt;
	                        &lt;!-- Destination for Documentation --&gt;
	                        &lt;!-- XML will go in docDestination/xml, JSON will go in docDestination/json --&gt;
	                        &lt;docDestination&gt;${basedir}/your/folder&lt;/docDestination&gt;
	                        &lt;!-- The root URL that prefixes your annotations, just the path and NOT the domain --&gt;
	                        &lt;!-- Example: "impl" not "http://www.secondmarket.com/impl" --&gt;
	                        &lt;rootURL&gt;impl&lt;/rootURL&gt;
	                    &lt;/configuration&gt;
	                &lt;/execution&gt;
	            &lt;/executions&gt;
	        &lt;/plugin&gt;
	    &lt;/plugins&gt;
	&lt;/build&gt;
</pre>

Note: To be safe, always prefix docDestination with ${basedir} to avoid complications with project hierarchy.  This only applies if you are trying to direct the output of the plugin to a location on your classpath rather than an absolute URI on your filesystem.

### How Do I Run The Plugin?
By default, the plugin is configured not to run.   If you want to generate documentation, add the flag <code>-Ddocs.generate=true</code> when you install/deploy the Maven application containing the plugin.

### What kind of output will I get?
This plugin generates three basic classes of output.  
<ul>
	<li>The first type is a unique XML file for each method in a Controller class that has a <code>@RequestMapping</code> annotation.   These files are named based on the HTTP verb (<code>GET, PUT, POST, DELETE</code>) assigned to the method and live in a folder denoting their full URL path.  Example: the file describing the call <code>GET http://www.yoursite.com/rootURL/users/1</code> will be saved at <code>docDestination/xml/rootURL/users/1/GET.xml</code>.</li>
	<li>The second type is a JSON file for each non-standard (does not live in package <code>java.*</code>) class that is assigned to a parameter or return type of one of the methods outlined in the XML files.  This file will be located at <code>docDestination/json/${classname}.json</code></li>
	<li>The third and final output is a file location at <code>docDestination/xml/summary.xml</code> which describes the entire hierarchy of the XML documents generated by the plugin as they are represented on your filesystem.  This is the best way to get an overview of your REST API and useful for parsing in order to navigate the folder hierarchy generated.  This does not describe the URL paths of your application but rather the filesystem paths to the XML files that contain the API information.</li>
</ul>

### What are the limitations?
There are a few limitations to the REST Doc Generator
<ul>
	<li>It only works on one package at a time.</li>
	<li>All classes must contain the proper annotations.  Controllers must be labeled <code>@Controller</code>, methods/routes to be documented must have the <code>@RequestMapping</code> annotation, and parameters must be annotated with either <code>@PathVariable</code>, <code>@RequestBody</code>, <code>@RequestHeader</code>, or <code>@RequestParameter</code>.
	<li>Generic support in parameter and return types is fairly basic, users requiring advanced support for nested generic classes should consider an advanced annotation-based JSON-generator like Jackson</li>

</ul>

### How Can I Use The Output?
There are many uses for the output generated by this plugin.  At SecondMarket, we built a single-page webapp that used the summary.xml file to generate a collapsible-list hierarchy of the entire REST API.  Each element in the list was a link to an XSLT-formatted version of the XML file for its REST call.  Inside that XML, the <code>{ "fullname" : "xxx", "generic" : "yyy"}</code> declarations were used to provide links to the JSON files describing the non-obvious return and parameter types of the methods.

### Are there any known bugs?
Yes, there are a few non-critical issues with this plugin:
<ul>
	<li>The folder specified in <code>docDestination</code> must exist before the plugin is run</li>
</ul>

## Powered By: <img src="https://www.secondmarket.com/static/hg/images/sm-logo-big.png"/>
