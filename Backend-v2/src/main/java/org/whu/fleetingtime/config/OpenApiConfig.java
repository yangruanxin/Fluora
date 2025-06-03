package org.whu.fleetingtime.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        servers = {
                // 确保这里的URL包含了正确的端口号
                @Server(url = "https://121.43.136.251:8080", description = "Production Server URL with Port")
        }
        // info = @io.swagger.v3.oas.annotations.info.Info(title = "Fleeting Time API", version = "v2", description = "...")
)
public class OpenApiConfig {
    // 这个类可以是空的
}