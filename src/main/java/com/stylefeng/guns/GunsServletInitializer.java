package com.stylefeng.guns;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Guns Web程序启动类
 *
 * https://www.baeldung.com/spring-boot-servlet-initializer This is an extension
 * of WebApplicationInitializer which runs a SpringApplication from a
 * traditional WAR archive deployed on a web container. This class binds
 * Servlet, Filter and ServletContextInitializer beans from the application
 * context to the server. Extending the SpringBootServletInitializer class also
 * allows us to configure our application when it’s run by the servlet
 * container, by overriding the configure() method.
 */
public class GunsServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(GunsApplication.class);
	}

}
