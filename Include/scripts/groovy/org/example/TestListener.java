package org.example;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.reporters.ExitCodeListener;

public class TestListener extends ExitCodeListener {

	public TestListener(TestNG runner) {
		super(runner);
	}

	@Override
	public void onStart(ITestContext context) {
		System.out.println("Start test " + context.getName());
	}

	@Override
	public void onFinish(ITestContext context) {
		System.out.println("End test " + context.getName());
	}
	
	@Override
	public void onConfigurationFailure(ITestResult arg0) {
		System.out.println("Error 123");
	}
}
