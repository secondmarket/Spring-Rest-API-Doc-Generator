package com.secondmarket.xmlgen;

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
    private Document d;
    private Element root;
    private File mainFolder;
    private DocumentFactory df;

    public SummaryGenerator(String location) {
        df = DocumentFactory.getInstance();
        d = df.createDocument();
        root = df.createElement("folders");
        d.setRootElement(root);

        mainFolder = new File(location);
        traverseFolder(mainFolder, root, "");
    }

    private void traverseFolder(File folder, Element parent, String path) {
        Element folderElement = df.createElement("folder");
        folderElement.addAttribute("name", folder.getName());
        File[] children = folder.listFiles();
        String totalPath = path + "/" + folder.getName();
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

    private void traverseFile(File file, Element parent, String path) {
        if (file.getName().endsWith("xml")) {
            //File element
            Element fileElement = df.createElement("file");
            fileElement.addText(file.getName());
            String totalPath = path + "/" + file.getName();
            fileElement.addAttribute("path", totalPath);
            parent.add(fileElement);
        }
    }

    public void writeDocument(String location) throws IOException {
        File summary = new File(mainFolder + File.separator + location);
        if (summary.exists()) {
            summary.delete();
            summary.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(summary);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(fos, format);
        Element root = d.getRootElement();
        writer.write(d);
        writer.flush();
        fos.close();
    }

}
