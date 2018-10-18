package com.soft.zxl.service.impl;

import com.soft.zxl.service.DemoService;

public class DemoServiceImpl implements DemoService {

	public String query(String name, String age) {
		return "name = " +name + "; age = " +age +";";
	}

}
