package com.Banking.udfcbankapplication.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Swagger Demo")
                        .version("1.0")
                        .description("Swagger Demo"));
    }
}
































//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//
//@Configuration
//public class OpenApiConfig {
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("Swagger Demo")
//                        .version("1.0")
//                        .description("Swagger Demo"));
//    }
//}


//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
//@Configuration
//public class SwaggerConfig {
//        @Bean
//        public Docket api() {
//            return new Docket(DocumentationType.SWAGGER_2)
//                    .select()
//                    .apis(RequestHandlerSelectors.basePackage("com.springcrud"))
//                    .paths(PathSelectors.any())
//                    .build();
//        }
//    }
//









//===============below this was chatgpt
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
////@Configuration
////public class SwaggerConfig {
////
////    @Bean
////    public OpenAPI openAPI() {
////        return new OpenAPI()
////                .info(new Info()
////                        .title("UDFC Bank API")
////                        .description("API documentation for UDFC Bank application")
////                        .version("1.0"))
////                .addSecurityItem(new SecurityRequirement().addList("JWT"))
////                .components(new io.swagger.v3.oas.models.Components()
////                        .addSecuritySchemes("JWT", apiKey()));
////    }
////
////    private SecurityScheme apiKey() {
////        return new SecurityScheme()
////                .name("Authorization")
////                .description("Enter JWT token in the format: Bearer <your-token>")
////                .in(SecurityScheme.In.HEADER)
////                .type(SecurityScheme.Type.HTTP)
////                .scheme("bearer")
////                .bearerFormat("JWT");
////    }
////}
