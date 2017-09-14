package com.finstone.app.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class FileUtils {
	public static FileUtils getInstance(){
		FileUtils fu = new FileUtils();
		return fu;
	}
	
	public FileUtils() {
		initRootPath();
		initProperties();
	}

	private Properties setings;
	private String rootPath;

	public void initProperties() {
		InputStream in = FileUtils.readFileAsStream(new File(rootPath
				+ "/classes/config/application.properties"));
		try {
			setings = new Properties();
			setings.load(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void initRootPath() {
		String path = FileUtils.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		if (path.startsWith("file:")) {
			path = path.substring(5);
		}
		if (path.startsWith("/") && path.indexOf(":") != -1) {
			path = path.substring(1);
		}

		rootPath = path;
		int index1 = path.indexOf(".jar");
		if (index1 != -1) {
			rootPath = "";
			path = path.split(".jar")[0];
			String[] paths = path.split("/");
			for (int i = 0; i < paths.length - 1; i++) {
				rootPath = rootPath + paths[i] + File.separator;
			}
		} else {
			rootPath = path.split("WEB-INF")[0];
			if (rootPath.endsWith("/")) {
				rootPath = rootPath + "WEB-INF";
			} else {
				rootPath = rootPath + "/WEB-INF";
			}
		}
		if (rootPath.endsWith("/")) {
			rootPath = rootPath.substring(0, rootPath.length() - 1);
		}
		if (rootPath.endsWith("\\")) {
			rootPath = rootPath.substring(0, rootPath.length() - 1);
		}
		rootPath = (new File(rootPath)).getAbsolutePath();

	}

	public String getProperValue(String key) {
		return setings.getProperty(key);
	}

	public String getPubicPay() {
		return setings.getProperty("publicPay");
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
}
