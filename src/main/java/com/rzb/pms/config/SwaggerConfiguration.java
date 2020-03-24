package com.rzb.pms.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.common.collect.Lists;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

	// public static final Contact DEFAULT_CONTACT = new Contact("PILL-H Team",
	// "https://www.pill.in/", "contact@pill.in");
	public static final String JWT_KEY = "JWT";
	public static final String DEFAULT_API_PATTERN = "/.*";
	public static final Contact DEFAULT_CONTACT = new Contact("Apache", "https://www.apache.org/", "");

	@Bean
	public Docket postsApi() {
		// @formatter:off
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.securityContexts(Lists.newArrayList(securityContext())).securitySchemes(Lists.newArrayList(apiKey()))
				.select()
				.apis(RequestHandlerSelectors.any())
				.build();
		// @formatter:on
	}

	private ApiInfo apiInfo() {
		// @formatter:off
		return new ApiInfoBuilder().title("NextGen-Pharma System")
				.description("NextGen-Pharma API's for UI Integration").license("Apache License Version 1.0")
				.licenseUrl("").version("V 1.0").contact(DEFAULT_CONTACT).build();
		// @formatter:on
	}

//	private ApiInfo apiInfo() {
//		// @formatter:off
//		return new ApiInfoBuilder().title("OpenSource-Pharma System")
//				.description("OpenSource-Pharma API's for UI Integration").license("Apache License Version 1.0")
//				.licenseUrl("").version("V 1.0").contact(DEFAULT_CONTACT).build();
//		// @formatter:on
//	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	private SecurityContext securityContext() {
		// @formatter:off
		return SecurityContext.builder().securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(DEFAULT_API_PATTERN)).build();
		// @formatter:on
	}

	private ApiKey apiKey() {
		return new ApiKey(JWT_KEY, "Authorization", "header");
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference(JWT_KEY, authorizationScopes));
	}
}