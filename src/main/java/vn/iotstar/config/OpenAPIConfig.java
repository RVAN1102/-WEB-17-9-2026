package vn.iotstar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BaVan Shop - RESTful API Category & Product Management")
                        .version("1.0.0")
                        .description("Tài liệu hướng dẫn và kiểm thử RESTful API BaVan Shop cho bảng Category và Product với tính năng CRUD và tìm kiếm phân trang.")
                        .contact(new Contact()
                                .name("BaVan Shop")
                                .email("contact@bavanshop.vn"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
