package pl.gdansk.cinema.cinema_booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cinemaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cinema Booking API")
                        .description("System rezerwacji biletów do kina")
                        .version("v0.0.1"));
    }

    @Bean
    public OperationCustomizer addImpersonateHeader() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(new Parameter()
                    .in("header")
                    .name("X-Impersonate-User")
                    .description("Nazwa użytkownika do udawania (tylko dla adminów)")
                    .required(false));
            return operation;
        };
    }
}
