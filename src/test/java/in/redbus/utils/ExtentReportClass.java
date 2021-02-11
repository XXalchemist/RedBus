package in.redbus.utils;

// This class contains code for extent report listener

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.google.common.io.Files;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExtentReportClass implements IReporter {

	
	private ExtentReports extent;
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,String outputDirectory) {

		extent = new ExtentReports("./output/Report/ExtentReportGenerated.html", true);

		for (ISuite suite : suites) {
			Map<String, ISuiteResult> result = suite.getResults();
			for (ISuiteResult r : result.values()) {
				ITestContext context = r.getTestContext();
				buildTestNodes(context.getPassedTests(), LogStatus.PASS);
				buildTestNodes(context.getFailedTests(), LogStatus.FAIL);
				buildTestNodes(context.getSkippedTests(), LogStatus.SKIP);
			}
		}
		extent.flush();
		extent.close();
	}

	private void buildTestNodes(IResultMap tests, LogStatus status) {

		ExtentTest test;
		if (tests.size() > 0) {
			for (ITestResult result : tests.getAllResults()) {
				test = extent.startTest(result.getMethod().getMethodName());
				for (String group : result.getMethod().getGroups())
					test.assignCategory(group);
				if (result.getThrowable() != null) {
					test.log(status, result.getThrowable());
				} else {
					test.log(status, "Test " + status.toString().toLowerCase());
				}
				extent.endTest(test);
			}
		}
		
		

	}
	
	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException {
		
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new java.util.Date());
		TakesScreenshot ts = (TakesScreenshot)driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String destination = System.getProperty("user.dir")+"/FailedTestsScreenshots/"+screenshotName+dateName+".png";
		
		File finalDestination = new File(destination);
		
		Files.copy(source, finalDestination);
		
		return destination;
		//logger.info("Screenshot captured ..."+dateName);
		
	}
}