A lightweighted logger for android app
======================================
####Usage:
Put zlog.properties into 'assets' or 'res/raw' under your app's root directory, format:

	debug=[Show zlog debug log:True|False]
	file=[Output to file:True|False],[Internal|External|Custom file path]
	root=[Log level],[Log tag],[Show thread name in tag:True|False]
	logger.[Package or class fullname]=[Log level],[Log tag],[Show thread name in tag:True|False]

Value of 'Log level' can be one of following:

	VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT, OFF

Initialize zlog in your Application class:

	LoggerFactory.init(getApplicationContext());

Now use loggers just like using slf4j!
	
	private static final Logger LOGGER = LoggerFactory.getLogger();
	
	LOGGER.debug("Message");
	LOGGER.info("Message with arguments: %s %s", "arg1", "arg2");
	LOGGER.warn(new Throwable());
	LOGGER.error(new Throwable(), "Throwable with a message");

####Example:


zlog.properties

	debug=false  
	file=true,internal  
	root=debug  
	logger.com.example.app.MainActivity=info,Activity,true
	logger.com.example.app=warn

App.java

	package com.example.app;
	
	import me.allenz.zlog.LoggerFactory;
	import android.app.Application;
	
	public class App extends Application {
	
		@Override
		public void onCreate() {
			LoggerFactory.init(this); // must be initialize in your custom Application
			super.onCreate();
		}
	
	}

A.java

	package com.example.app;
	
	import me.allenz.zlog.Logger;
	import me.allenz.zlog.LoggerFactory;
	
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
	
	import me.allenz.zlog.Logger;
	import me.allenz.zlog.LoggerFactory;
	
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
	
	import me.allenz.zlog.Logger;
	import me.allenz.zlog.LoggerFactory;
	import android.app.Activity;
	import android.os.Bundle;
	
	import com.example.B;
	
	public class MainActivity extends Activity {
	
		private static final Logger logger = LoggerFactory.getLogger();
	
		@Override
		protected void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			logger.verbose("verbose");
			logger.debug("debug");
			logger.info("info");
			logger.warn("warn");
			new A();
			new B();
		}
	
	}

Log output in '/data/data/com.example.app/files/zlog.log':

>2014-08-26 12:05:50.269	INFO	[main]Activity	info  
>2014-08-26 12:05:50.269	WARN	[main]Activity	warn  
>2014-08-26 12:05:50.269	WARN	A	warn  
>2014-08-26 12:05:50.269	DEBUG	B	debug  
>2014-08-26 12:05:50.269	INFO	B	info  
>2014-08-26 12:05:50.269	WARN	B	warn
