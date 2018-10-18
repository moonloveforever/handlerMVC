package com.soft.zxl.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soft.zxl.annotation.Autowired;
import com.soft.zxl.annotation.Controller;
import com.soft.zxl.annotation.RequestMapping;
import com.soft.zxl.annotation.RequestParam;
import com.soft.zxl.service.impl.DemoServiceImpl;

@Controller
@RequestMapping("/demo")
public class DemoController {

	@Autowired
	private DemoServiceImpl demoServiceImpl;

	@RequestMapping("/test")
	public void test(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name,
			@RequestParam("age") String age) {
		try {
			response.getWriter().write(demoServiceImpl.query(name, age));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
