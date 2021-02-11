package in.redbus.tests;

// This class contains important test features needed by all the tests of redbus pages

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import in.redbus.utils.DockerClass;
import in.redbus.utils.ExcelFileIO;
import in.redbus.utils.UseScreenshots;

public class BaseTest {

	// To initialize, select(Firefox, Chrome, IE), open and quit browser.
	public static WebDriver driver;

	// Remote web driver to run parallel cases on docker
	public static RemoteWebDriver remotedriver;

	public static Properties prop = new Properties();
	public static File file = new File("./Resources/configuration.properties");
	public static FileInputStream fis = null;

	// Excel File Test Data Reader
	public static ExcelFileIO reader = null;

	// Log4j logger
	public final static Logger logger = Logger.getLogger(BaseTest.class);

	// Extent Report
	public static ExtentReports extentReport = null;
	public static ExtentTest extentTest = null;

	// Creating and using properties file
	static { 

		// Exception Handling for FIS
		try {

			fis = new FileInputStream(file);
		}catch(FileNotFoundException e){

			logger.error(e.getMessage());
		}

		// Exception Handling for Prop
		try {

			prop.load(fis);
		}catch(IOException e) {

			logger.error(e.getMessage());
		}

		// Exception Handling for Excel File
		try {

			reader = new ExcelFileIO(prop.getProperty("testDataFileLocation"));
		}
		catch(Exception e) {

			logger.error(e.getMessage());
		}
	}


	@BeforeTest
	public void createReport() {

		// setting up extent report
		String sheetName = prop.getProperty("extentReportSystemInfoSheetName");
		String id = "Krishna";

		HashMap<String, String> systemInfo = reader.getRowTestData(sheetName,id);
		extentReport = new ExtentReports(prop.getProperty("reportOutputLocation")+prop.getProperty("reportName"),true);
		extentReport.addSystemInfo(systemInfo);
	}

	@BeforeMethod
	public void initDriver() throws MalformedURLException {

		//		ChromeOptions options = null;
		//		FirefoxOptions firefoxOptions = null;
		//		EdgeOptions edgeOptions = null;

		boolean headlessMode = Boolean.parseBoolean(prop.getProperty("headlessMode"));
		boolean runOnDocker = Boolean.parseBoolean(prop.getProperty("runOnDocker"));

		// to run on docker
		if(runOnDocker) {

			if(headlessMode) {
				
				 driver = DockerClass.startBrowserInHeadlessInDocker();
			}
			else {
				
				driver = DockerClass.startBrowserInNonHeadlessInDocker();
			}
		}
		else {

			if(headlessMode) {
				System.out.println("---- headless---");
				
				driver = DockerClass.startBrowserInHeadlessInLocal();
			}
			else {
				System.out.println("---- Nonheadless---");
				
				driver = DockerClass.startBrowserInNonHeadlessInLocal();
			}
		}
	}

	// depends on initDriver()
	@BeforeMethod(dependsOnMethods = "initDriver")
	public void setUp() {

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		
		if(!prop.getProperty("browser").equals("firefox")) {
			
			driver.manage().timeouts().implicitlyWait(Integer.parseInt(prop.getProperty("globalWait")), TimeUnit.SECONDS);
			
		}
		// implicit wait will be for all elements 
		
		driver.get(prop.getProperty("testUrl"));
		
		logger.info(prop.getProperty("testUrl")+" page opened...");


		//		extentReport.config().documentTitle("RedBusAutomationReport");
		//		extentReport.config().reportName("RedBusTestReport");
		//		extentReport.assignProject("RedBusProject");

	}


	@AfterMethod
	public void tearDown(ITestResult result) {

		// for fail test cases
		if(result.getStatus() == ITestResult.FAILURE) {

			extentTest.log(LogStatus.FAIL, "TEST FAILED "+result.getName());
			extentTest.log(LogStatus.FAIL, "TEST FAILED THROWABLE EXC "+result.getThrowable());

			// adding screenshot
			String screenshotPath = UseScreenshots.getScreenshot(driver, result.getName());
			extentTest.log(LogStatus.FAIL, extentTest.addScreenCapture(screenshotPath));
		}

		else if(result.getStatus() == ITestResult.SKIP) {

			extentTest.log(LogStatus.SKIP, "TEST SKIPPED "+result.getName());
		}

		else if(result.getStatus() == ITestResult.SUCCESS) {

			extentTest.log(LogStatus.PASS, "TEST PASSED "+result.getName());
		}

		// end the testcase in the extent report
		extentReport.endTest(extentTest);

		// closing the driver
		driver.quit();
		logger.info("Browser closed...");
	}

	@AfterTest
	public void endReport() {

		// tearing down extent report
		extentReport.flush();
		extentReport.close();
	}

}
