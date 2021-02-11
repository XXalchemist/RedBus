package in.redbus.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.poi.util.SystemOutLogger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import in.redbus.tests.BaseTest;

// have methods needed by docker
public class DockerClass extends BaseTest{
	
	// variables needed by all methods
	public static RemoteWebDriver remoteDriver;
	public static WebDriver webDriver;
	public static DesiredCapabilities caps;
	private static URL url = null;
	private static String browserName = prop.getProperty("browser").toLowerCase() ;
	
	public DockerClass() throws Exception {
		
		// check for browsername
		if(browserName.equals("chrome")||browserName.equals("firefox")||browserName.equals("ie")) {
			
			logger.info("Browser supported");
		}
		else {
			
			System.out.println(browserName);
			logger.error("Browser not supported");
			throw new Exception("Browser not supported");
		}
	}
	
	public static RemoteWebDriver toRunBrowserOnDocker(boolean headlessMode, boolean runOnDocker) throws MalformedURLException {
		
		 url = new URL("http://localhost:4444/wd/hub");
		
		if(headlessMode){
			
			remoteDriver = startBrowserInHeadlessInDocker();
			logger.info(browserName+" in headless mode with docker initiated");
		}
		else{
			
			remoteDriver = startBrowserInNonHeadlessInDocker();
			logger.info(browserName+" in non headless mode and with docker initiated");
		}
		
		return remoteDriver;
	}
	
	public static WebDriver toRunBrowserOnLocal(boolean headlessMode) {
		
		
		if(headlessMode) {
			
			webDriver = startBrowserInHeadlessInLocal();
			logger.info(browserName+" in headless mode without docker initiated");
		}
		
		else {
			
			webDriver = startBrowserInNonHeadlessInLocal();
			logger.info(browserName+" in non headless mode and without docker initiated");
		}
		
		return webDriver;
	}

	//	public static RemoteWebDriver toRunFirefox() throws MalformedURLException {
	//
	//		DesiredCapabilities caps = DesiredCapabilities.firefox();
	//
	//		URL url = new URL("http://localho:4444/wd/hub");
	//		RemoteWebDriver remoteDriver = new RemoteWebDriver(url, caps);
	//		return remoteDriver;
	//	}

	// To start different browsers
	public static RemoteWebDriver startBrowserInHeadlessInDocker() {
		
		ChromeOptions chromeOptions = null;
		FirefoxOptions firefoxOptions = null;
		InternetExplorerOptions ieOptions = null;

		if(browserName.equals("chrome")) {
			
			caps = DesiredCapabilities.chrome();
			chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("headless");
			chromeOptions.addArguments("window-size=1920,1080");
			chromeOptions.merge(caps);
			remoteDriver = new RemoteWebDriver(url, chromeOptions);
		}
		
		else if(browserName.equals("firefox")) {
			
			caps = DesiredCapabilities.firefox();
			firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments("headless");
			firefoxOptions.addArguments("window-size=1920,1080");
			firefoxOptions.merge(caps);
			remoteDriver = new RemoteWebDriver(url, firefoxOptions);
		}
		
		else if(browserName.equals("ie")) {
			
			caps = DesiredCapabilities.internetExplorer();
//			ieOptions = new InternetExplorerOptions();
//			ieOptions.addArguments("headless");
//			ieOptions.addArguments("window-size=1920,1080");
			// ieOptions.merge(caps);
			remoteDriver = new RemoteWebDriver(url, chromeOptions);
		}
		
		
		return remoteDriver;
	}
	
	// To start different browsers in nonheadless
	public static RemoteWebDriver startBrowserInNonHeadlessInDocker() {
		
		if(browserName.equals("chrome")) {
			
			caps = DesiredCapabilities.chrome();
		}
		
		else if(browserName.equals("firefox")) {
			
			caps = DesiredCapabilities.firefox();
		}
		
		else if(browserName.equals("ie")) {
			
			caps = DesiredCapabilities.internetExplorer();
		}
		
		remoteDriver = new RemoteWebDriver(url, caps);
		
		return remoteDriver;
	}
	
public static WebDriver startBrowserInHeadlessInLocal() {
		
		ChromeOptions chromeOptions = null;
		FirefoxOptions firefoxOptions = null;
		InternetExplorerOptions ieOptions = null;

		if(browserName.equals("chrome")) {
			
			System.setProperty("webdriver.chrome.driver","./Resources/drivers/chromedriver.exe");
			chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("headless");
			chromeOptions.addArguments("window-size=1920,1080");
			chromeOptions.addArguments("user-agent=Chrome/88.0.4324.150");
			webDriver = new ChromeDriver(chromeOptions);
		}
		
		else if(browserName.equals("firefox")) {
			
			System.setProperty("webdriver.gecko.driver","./Resources/drivers/geckodriver.exe");
			firefoxOptions = new FirefoxOptions();
			firefoxOptions.addArguments("headless");
			firefoxOptions.addArguments("window-size=1920,1080");
			webDriver = new FirefoxDriver(firefoxOptions);
		}
		
		else if(browserName.equals("ie")) {
			
			System.setProperty("webdriver.ie.driver","./Resources/drivers/IEDriverServer.exe");
			ieOptions = new InternetExplorerOptions();
			
			logger.info("IE doesn't support headless mode .... starting normal");
//			ieOptions.addArguments("window-size=1920,1080");
			// ieOptions.merge(caps);
			webDriver = new InternetExplorerDriver(ieOptions);
		}
		
		
		return webDriver;
	}
	
	// To start different browsers in nonheadless
	public static WebDriver startBrowserInNonHeadlessInLocal() {
		
		if(browserName.equals("chrome")) {
			
			System.setProperty("webdriver.chrome.driver","./Resources/drivers/chromedriver.exe");
			webDriver = new ChromeDriver();
			logger.info("Chrome driver initiated in head mode");
		}
		
		else if(browserName.equals("firefox")) {
			
			System.setProperty("webdriver.gecko.driver","./Resources/drivers/geckodriver.exe");
			webDriver = new FirefoxDriver();
			logger.info("Firefox driver initiated in head mode");
			
		}
		
		else if(browserName.equals("ie")) {
			
			System.setProperty("webdriver.ie.driver","./Resources/drivers/IEDriverServer.exe");
			webDriver = new InternetExplorerDriver();
			logger.info("IE driver initiated in head mode");
		}
		
		
		return webDriver;
	}
}
