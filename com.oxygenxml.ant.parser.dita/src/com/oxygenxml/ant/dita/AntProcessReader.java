package com.oxygenxml.ant.dita;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.dita.dost.util.CatalogUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class AntProcessReader implements XMLReader {

	/**
	 * Entity resolver
	 */
	private EntityResolver resolver;
	/**
	 * Content Handler
	 */
	private ContentHandler handler;
	/**
	 * Error Handler.
	 */
	private ErrorHandler errorHandler;

	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return false;
	}

	@Override
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
	}

	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return null;
	}

	@Override
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
	}

	@Override
	public void setEntityResolver(EntityResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public EntityResolver getEntityResolver() {
		return resolver;
	}

	@Override
	public void setDTDHandler(DTDHandler handler) {
		//
	}

	@Override
	public DTDHandler getDTDHandler() {
		return null;
	}

	@Override
	public void setContentHandler(ContentHandler handler) {
		this.handler = handler;
	}

	@Override
	public ContentHandler getContentHandler() {
		return handler;
	}

	@Override
	public void setErrorHandler(ErrorHandler handler) {
		errorHandler = handler;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	@Override
	public void parse(InputSource input) throws IOException, SAXException {
		try {
			InputStream is = input.getByteStream();
			URL url = new URL(input.getSystemId());
			if(is == null) {
				URL urlForConnect = url;
				if("file".equals(url.getProtocol())) {
					String urlStr = urlForConnect.toString();
					if(urlStr.contains("?")) {
						//Remove query part
						urlForConnect = new URL(urlStr.substring(0, urlStr.indexOf("?")));
					}
				}
				is = urlForConnect.openStream();
			}

			File originalFile = new File(url.toURI());
			if(originalFile != null) {

				//Expected output file
				File outputFile = File.createTempFile("test", ".out");

				Source resolvedBuildXML = CatalogUtils.getCatalogResolver().resolve("plugin:com.oxygenxml.ant.dita:" + "/resources/build.xml", new File(".").toURI().toURL().toString());
				if(resolvedBuildXML != null) {
					//We need to run the build file.
					boolean success = executeAntBuildFile(
							new File(new URL(resolvedBuildXML.getSystemId()).toURI()).getAbsolutePath(),
							originalFile.getAbsolutePath(), outputFile.getAbsolutePath());

					if(success) {
						StringBuilder contentBuilder = new StringBuilder();
						FileInputStream fis = new FileInputStream(outputFile);
						InputStreamReader reader = null;
						try {
							char[] chars = new char[1024];
							reader = new InputStreamReader(fis, "UTF-8");
							int len = -1;
							while((len = reader.read(chars)) != -1) {
								contentBuilder.append(chars, 0, len);
							}
						} finally {
							if(reader != null) {
								//Close the input stream
								reader.close();
							}
						} 

						TransformerFactory factory = TransformerFactory.newInstance();
						factory.setURIResolver(CatalogUtils.getCatalogResolver());
						SAXResult result = new SAXResult(handler);
						Transformer newTransformer = factory.newTransformer();
						newTransformer.transform(new StreamSource(new StringReader(contentBuilder.toString()), url.toString()),
								result);
					}
				}
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Exec an ANT build file.
	 * @param buildXmlFileFullPath Path to the build file
	 * @return <code>true</code> if success
	 */
    private static boolean executeAntBuildFile(String buildXmlFileFullPath, String inputFilePath, String outputFilePath) {
        boolean success = false;
        DefaultLogger consoleLogger = getConsoleLogger();
 
        // Prepare Ant project
        Project project = new Project();
        File buildFile = new File(buildXmlFileFullPath);
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        project.setUserProperty("input.file", inputFilePath);
        project.setUserProperty("output.file", outputFilePath);
        project.addBuildListener(consoleLogger);
 
        // Capture event for Ant script build start / stop / failure
        try {
            project.fireBuildStarted();
            project.init();
            ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
            project.addReference("ant.projectHelper", projectHelper);
            projectHelper.parse(project, buildFile);
             
            // If no target specified then default target will be executed.
            project.executeTarget("dist");
            project.fireBuildFinished(null);
            success = true;
        } catch (BuildException buildException) {
            project.fireBuildFinished(buildException);
            throw new RuntimeException("Unable to execute build file", buildException);
        }
         
        return success;
    }
     
    /**
     * Logger to log output generated while executing ant script in console
     * 
     * @return
     */
    private static DefaultLogger getConsoleLogger() {
        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
         
        return consoleLogger;
    }
	
	
	@Override
	public void parse(String systemId) throws IOException, SAXException {
		parse(new InputSource(systemId));
	}
	

}
