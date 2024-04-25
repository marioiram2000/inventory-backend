package com.company.ejercicio1;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MiAppSpring {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = ClassPathXmlApplicationContext("applicationContext.xml");
	}

}
