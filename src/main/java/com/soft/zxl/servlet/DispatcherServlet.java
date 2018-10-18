/**
 * 
 */
package com.soft.zxl.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Administrator
 *
 */
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private List<String> classNames = new ArrayList<String>();
	
	/**
	 * <load-on-startup>0</load-on-startup>
	 */
	public void init(ServletConfig config) {
		doScan("com.soft.zxl");
	}

	/**
	 * 扫描工程里面的类
	 * @param string
	 */
	private void doScan(String basePackage) {
		URL url = this.getClass().getClassLoader().getResource("/"+basePackage.replaceAll("\\.", "/"));
		String filePath = url.getFile();
		File file = new File(filePath);
		String[] fileList = file.list();
		for (String path : fileList) {
			File files = new File(filePath+path);
			if(files.isDirectory()) {
				doScan(basePackage + "." + path);
			} else {
				classNames.add(basePackage+"."+files.getName());
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

}
