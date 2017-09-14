package com.fins.html.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;

/**
 * 
 * @author Administrator
 *
 */
public class FileUtils {
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readResourceAsString(String filePath) {
		InputStream in = null;
		try {
			in = FileUtils.class.getClassLoader().getResourceAsStream(filePath);
			return IOUtils.toString(in, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static InputStream readResourceAsStream(String filePath) {
		try {
			return FileUtils.class.getClassLoader().getResourceAsStream(filePath);
			 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static InputStream readFileAsStream(String filePath) {
		InputStream in = null;
		try {
			String path = FileUtils.class.getClassLoader().getResource(filePath).getPath();
			path = path.replaceAll("%20", " ");
			in = new FileInputStream( new File(path));
			return in;
		} catch (Exception e) {
			IOUtils.closeQuietly(in);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFileAsString(String filePath) {
		InputStream in = null;
		try {
			in = readFileAsStream(filePath);
			return IOUtils.toString(in, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	public static Document getDocument(String filePath) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(readFileAsStream(filePath));
			return doc;
		} catch (Exception es) {
			System.err.println(es.toString());
		}
		return null;
	}
	public static Document getDocumentFromString(String xml) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			return doc;
		} catch (Exception es) {
			System.err.println(es.toString());
		}
		return null;
	}	
	public static Document getDocument(InputStream filein) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(filein);
			return doc;
		} catch (Exception es) {
			System.err.println(es.toString());
		}
		return null;
	}
	
	public static Document getDocument(File file) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(readFileAsStream(file));
			return doc;
		} catch (Exception es) {
			//System.err.println(es.toString());
		}
		return null;
	}
	
	
	
	public static InputStream readFileAsStream(File file) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			return in;
		} catch (Exception e) {
			IOUtils.closeQuietly(in);
			throw new RuntimeException(e);
		}
	}
	
	public static File readFile(String filePath) {

		String path = FileUtils.class.getClassLoader().getResource(filePath).getPath();
		path = path.replaceAll("%20", " ");
		return new File(path);

	}
}
