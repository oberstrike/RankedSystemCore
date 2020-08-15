package server.domain.queue

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.keycloak.Token
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Form
import javax.ws.rs.core.MediaType


@Path("/queue")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
class GameQueueResource {



    @GET
    @Operation(
        summary = "Add a new Match",
        operationId = "addMatch"
    )
    @APIResponses(
        value = [
            APIResponse(
                description = "Test",
                name = "Test"
            )
        ]
    )
    fun login(): String {
        return ""
    }

}


