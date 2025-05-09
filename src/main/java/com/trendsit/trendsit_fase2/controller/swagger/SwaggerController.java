package com.trendsit.trendsit_fase2.controller.swagger;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

// URL SWAGGER http://localhost:8080/swagger-ui/swagger-ui/index.html
@RestController
public class SwaggerController {
    @GetMapping("/")
    public void redirectToSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }
}
