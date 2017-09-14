package com.fins.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fins.modules.core.spring.ServiceLocator;


@SuppressWarnings("rawtypes")
public class Application {
	
	private static String des = "";

	private static Map property = new HashMap();
	
	private static Properties applicationSettings;  

	public static Map getLicenseProperty() {
		return property;
	}

	public static String getDes() {
		return des;
	}

	public static void setDes(String des) {
		Application.des = des;
	}

	public static void setProperty(Map property) {
		Application.property = property;
	}
	
	public static Object getLicenseValue(String name){
		return property.get(name);
	}
	
	public static  Properties getApplicationSettings(){
		if(applicationSettings == null){
			applicationSettings =  (Properties) ServiceLocator.getInstance().getBean("applicationProperties");
		}
		return applicationSettings;
	}

}
