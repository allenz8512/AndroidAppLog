Android App Log  
======================================
A lightweight android logger support auto tag, file logging and show logs on screen  
![image](https://github.com/allenz8512/androidapplog/blob/master/screenshots/screenshot2.png)  
![image](https://github.com/allenz8512/androidapplog/blob/master/screenshots/screenshot1.png)  
![image](https://github.com/allenz8512/androidapplog/blob/master/screenshots/screenshot3.png)

####New in 1.4.0:
Use LoggerFactoryConfig.setPropertiesEncoding(encoding) to set the encoding for your aal.properties, 'ISO-8859-1' by default.
####Build：  
Binary download:  
[![Download](https://api.bintray.com/packages/allenz8512/maven/android-app-log/images/download.svg) ](https://github.com/allenz8512/AndroidAppLog/releases/download/1.4.0/android-app-log-1.4.0.aar)  
Gradle build (jcenter):  

    dependencies{
    	compile 'me.allenz:android-app-log:1.4.0'
    }	

Will be synchronize to maven central soon!
####How to use:

Put aal.properties into 'assets' or 'res/raw' under your app's root directory, format:

	debug=[Show debug log:True|False]
	root=[Log level],[Log tag],[Show thread name in tag:True|False]
	logcat=[Output to logcat:True|False]
	file=[Output to file:True|False],[parent folder of log files],[rolling file size],[enable gzip compress:true|false]
	textview=[Output to textview:True|False]
	handleex=[Log uncaught exception message:True|False]
	logger.[Package or class fullname]=[Log level],[Log tag],[Show thread name in tag:True|False]

Value of 'Log level' can be one of following:

	VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, OFF

Now use loggers just like log4j!
	
	private static final Logger LOGGER = LoggerFactory.getLogger();
	
	LOGGER.debug("Message");
	LOGGER.info("Message with arguments: %s %d", "str", 123);
	LOGGER.warn(new Throwable());
	LOGGER.error(new Throwable(), "Throwable with a message");

####Example:


aal.properties

	debug=false  
	root=debug  
	#use ${internal} or ${external} to be prefix of folder path, means using internal or external storage of app  
	file=true,${internal}/logs  
	textview=true  
	logger.com.example.app.MainActivity=info,Activity,true
	logger.com.example.app=warn

A.java

	package com.example.app;
	
	import me.allenz.androidapplog.Logger;
	import me.allenz.androidapplog.LoggerFactory;
	
	public class A {
	
		private static final Logger logger = LoggerFactory.getLogger();
	
		public A() {
			logger.verbose("verbose");
			logger.debug("debug");
			logger.info("info");
			logger.warn("warn");
		}
	}
	
B.java

	package com.example;
	
	import me.allenz.androidapplog.Logger;
	import me.allenz.androidapplog.LoggerFactory;
	
	public class B {
	
		private static final Logger logger = LoggerFactory.getLogger();
	
		public B() {
			logger.verbose("verbose");
			logger.debug("debug");
			logger.info("info");
			logger.warn("warn");
		}
	}
	
MainActivity.java

	package com.example.app;
	
	import me.allenz.androidapplog.Logger;
	import me.allenz.androidapplog.LoggerFactory;
	import android.app.Activity;
	import android.os.Bundle;
	
	import com.example.B;
	
	public class MainActivity extends Activity {
	
		private static final Logger logger = LoggerFactory.getLogger();
		
		private TextView logView;
	
		@Override
		protected void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			//create an intouchable and transparent textview to show logs on screen
			logView = LoggerFactory.createLogTextView(this);
			logger.verbose("verbose");
			logger.debug("debug");
			logger.info("info");
			logger.warn("warn");
			new A();
			new B();
		}
		
		@Override
    	protected void onResume() {
        	super.onResume();
        	LoggerFactory.bindTextView(logView);
    	}

    	@Override
    	protected void onPause() {
        	super.onPause();
        	LoggerFactory.unbindTextView();
    	}
	
	}

Log output in '/data/data/com.example.app/files/logs/com.example.app.log':

>2014-08-26 12:05:50.269	INFO	[main]Activity	info  
>2014-08-26 12:05:50.269	WARN	[main]Activity	warn  
>2014-08-26 12:05:50.269	WARN	A	warn  
>2014-08-26 12:05:50.269	DEBUG	B	debug  
>2014-08-26 12:05:50.269	INFO	B	info  
>2014-08-26 12:05:50.269	WARN	B	warn
