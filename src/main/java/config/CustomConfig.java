package config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom")
@Setter
@Getter
public class CustomConfig {
    String host = "http://localhost:8080/";
    Integer applyOrderThread = 3;
}
