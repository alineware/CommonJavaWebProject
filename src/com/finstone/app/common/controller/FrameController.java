package com.finstone.app.common.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.finstone.UrlTest;
import com.finstone.app.common.util.FileUtils;

@Controller
public class FrameController {

	@RequestMapping(value = "/outfssvr", method = RequestMethod.POST)
	public @ResponseBody String outfssvr(HttpServletRequest request){
		System.out.println("------->>>-------业务系统请求外部系统开始------------------");
		UrlTest t = new UrlTest();
		try {
			String params = t.getRequestContent(request, "UTF-8");
			System.out.println("请求参数:"+params);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = t.getJson(t.getRootPath()+"/config/response.json");
		System.out.println("返回:"+json);
		return json;
	}
	
	@RequestMapping(value = "/gateway", method = RequestMethod.GET)	
	public @ResponseBody String gateway(HttpServletRequest request){
		System.out.println("------->>>-------外部系统请求业务系统开始------------------");
		UrlTest t = new UrlTest();
		String json = t.getJson(t.getRootPath()+"/config/request.json");
		System.out.println("请求参数："+json);
		String url = FileUtils.getInstance().getProperValue("yszxESBURL");
		System.out.println("请求URL："+url);
		if(url.equals("http://ip:port/webapp/action.do")){//说明是模板
			return "GET请求可以正常访问！请配置正确请求地址！";
		}
		String entity = t.doPost(url, json);
		System.out.println("业务系统返回："+entity);
		return entity;
	}
	
}