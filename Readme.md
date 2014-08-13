zlog
====
A lightweighted logger for android app
--------------------------------------
Example:

zlog.properties in assets folder

	root=debug  
	logger.com.example.app.MainActivity=info,Activity
	logger.com.example.app=warn

App.java

	package com.example.app;
	
	import me.allenz.zlog.LoggerFactory;
	import android.app.Application;
	
	public class App extends Application {
	
		@Override
		public void onCreate() {
			LoggerFactory.init(this);
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

Log output:

>08-13 14:25:55.489: I/Activity(1245): info  
>08-13 14:25:55.489: W/Activity(1245): warn  
>08-13 14:25:55.489: W/A(1245): warn  
>08-13 14:25:55.489: D/B(1245): debug  
>08-13 14:25:55.489: I/B(1245): info  
>08-13 14:25:55.489: W/B(1245): warn
