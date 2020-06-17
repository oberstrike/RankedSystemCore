package server

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import javax.ws.rs.core.Application

@OpenAPIDefinition(
    tags = [Tag(name = "matches", description = "The path over which the games are managed.")],
    info = Info(title = "Open API Swagger Demo", version = "1.0"),
    security = [SecurityRequirement(name = "basicAuth")]
)
class RankedApplication : Application() {

}