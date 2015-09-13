package com.adelegue.reactivestreams.utils;

public class Utils {

	public static void print(String step, Object i){
		System.out.println(getThreadName() + " --- "+step+" : "+i);
	}
	
	public static String getThreadName(){
		Thread thread = Thread.currentThread();
		return "Thread name : "+thread.getId()+" - "+ thread.getName();
	}
}
