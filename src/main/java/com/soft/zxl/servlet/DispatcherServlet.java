package com.soft.zxl.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soft.zxl.annotation.Autowired;
import com.soft.zxl.annotation.Controller;
import com.soft.zxl.annotation.RequestMapping;
import com.soft.zxl.annotation.Service;

/**
 * @author zhaoxl
 * @date 2018年10月22日 16:22:50
 * @version 1.0
 */
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private List<String> classNames = new ArrayList<String>();

	private Map<String, Object> beansMap = new HashMap<String, Object>();

	private Map<String, Method> handlerMap = new HashMap<String, Method>();

	/**
	 * <load-on-startup>0</load-on-startup>
	 */
	public void init(ServletConfig config) {
		doScan("com.soft.zxl");
		doInstance();
		doAutoWired();
		urlMapping();
	}

	/**
	 * 路径映射关系生成
	 */
	private void urlMapping() {
		for (Map.Entry<String, Object> entry : beansMap.entrySet()) {
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();
			if (clazz.isAnnotationPresent(Controller.class)) {
				RequestMapping reqClassMapping = clazz.getAnnotation(RequestMapping.class);
				String classPath = reqClassMapping.value();
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(RequestMapping.class)) {
						RequestMapping reqMethodMapping = method.getAnnotation(RequestMapping.class);
						String methodPath = reqMethodMapping.value();
						handlerMap.put(classPath + methodPath, method);
					} else {
						continue;
					}
				}
			} else {
				continue;
			}
		}
	}

	/**
	 * 注入依赖
	 */
	private void doAutoWired() {
		for (Map.Entry<String, Object> entry : beansMap.entrySet()) {
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();

			if (clazz.isAnnotationPresent(Controller.class)) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(Autowired.class)) {
						Autowired autowired = field.getAnnotation(Autowired.class);
						String key = autowired.value();
						Object value = beansMap.get(key);
						try {
							// 针对私有属性强制反射
							field.setAccessible(true);
							field.set(instance, value);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					} else {
						continue;
					}
				}

			}
		}
	}

	/**
	 * 实例化类对象
	 */
	private void doInstance() {
		for (String className : classNames) {
			className.replace(".class", "");
			try {
				Class<?> clazz = Class.forName(className);
				if (clazz.isAnnotationPresent(Controller.class)) {
					Object instance = clazz.newInstance();
					RequestMapping reqMap = clazz.getAnnotation(RequestMapping.class);
					String key = reqMap.value();
					beansMap.put(key, instance);
				} else if (clazz.isAnnotationPresent(Service.class)) {
					Object instance = clazz.newInstance();
					Service reqMap = clazz.getAnnotation(Service.class);
					String key = reqMap.value();
					beansMap.put(key, instance);
				} else {
					continue;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 扫描工程里面的类
	 * 
	 * @param string
	 */
	private void doScan(String basePackage) {
		URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
		String filePath = url.getFile();
		File file = new File(filePath);
		String[] fileList = file.list();
		for (String path : fileList) {
			File files = new File(filePath + path);
			if (files.isDirectory()) {
				doScan(basePackage + "." + path);
			} else {
				classNames.add(basePackage + "." + files.getName());
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();  //完整请求路径
		String context = req.getContextPath();	//项目路径
		String path = uri.replace(context, "");
		String instanceKey = "/"+path.split("/")[0];
		String methodKey = "/"+path.split("/")[1];
		Object instance = beansMap.get(instanceKey); 
		//TODO 待完善 requestParam注入,以及方法调用
	}

}
