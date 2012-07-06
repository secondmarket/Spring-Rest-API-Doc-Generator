package com.secondmarket.xmlgen;

import com.secondmarket.annotatedobject.amethod.AnnotatedMethod;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: sstern
 * Date: 6/19/12
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class SummaryGenerator {
    private Document document;
    private Element root;
    private File mainFolder;
    private DocumentFactory documentFactory;

    public SummaryGenerator(String location) {
        documentFactory = DocumentFactory.getInstance();
        document = documentFactory.createDocument();
        root = documentFactory.createElement("folders");
        document.setRootElement(root);
        root.addAttribute("rootPath", "http://secondmarket.com/");

        mainFolder = new File(location);
        traverseFolder(mainFolder, root, "");
    }

    /**
     * Recursively traverse a folder in the filesystem for XML>
     * @param folder
     * @param parent
     * @param path
     */
    private void traverseFolder(File folder, Element parent, String path) {
        Element folderElement = documentFactory.createElement("folder");
        folderElement.addAttribute("name", folder.getName());
        File[] children = folder.listFiles();
        String totalPath = AnnotatedMethod.pathMash(path,folder.getName());
        if (children != null) {
            for (File f : children) {
                if (f.isDirectory()) {
                    traverseFolder(f, folderElement, totalPath);
                } else {
                    traverseFile(f, folderElement, totalPath);
                }
            }
        }
        parent.add(folderElement);
    }

    /**
     * Add a file to the XML summary
     * @param file
     * @param parent
     * @param path
     */
    private void traverseFile(File file, Element parent, String path) {
        if (file.getName().endsWith("xml")) {
            Element fileElement = documentFactory.createElement("file");
            fileElement.addText(file.getName());
            String totalPath = AnnotatedMethod.pathMash(path, file.getName());
            fileElement.addAttribute("path", totalPath);
            parent.add(fileElement);
        }
    }

    /**
     * Write the summary document to disk
     * @param location
     * @throws IOException
     */
    public void writeDocument(String location) throws IOException {
        File summary = new File(mainFolder + File.separator + location);
        if (summary.exists()) {
            summary.delete();
            summary.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(summary);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(fos, format);
        writer.write(document);
        writer.flush();
        fos.close();
    }

}
