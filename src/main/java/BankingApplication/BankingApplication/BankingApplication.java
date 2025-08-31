package BankingApplication.BankingApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = " Krish's Project",
        description="Backend restApi for banking application",
        version = "v1.0",
        contact = @Contact(
                name = " Krish Kapoor",
                email = "krishkapoor311@gmail.com",
                url = "https://github.com/krishxkapoor/Banking-Applicaion.git"
        ),
        license = @License(
                name = "Krish kapoor",
                url = "https://github.com/krishxkapoor/Banking-Applicaion.git"
        )
        
    ),
        externalDocs = @ExternalDocumentation(
                description = "Banking application documentation",
                url = "https://github.com/krishxkapoor/Banking-Applicaion.git"
        )
        
)
public class BankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

}
