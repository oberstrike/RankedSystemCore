package server.domain.auth

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.*
import javax.ws.rs.core.Form
import javax.ws.rs.core.MediaType


@RegisterRestClient
interface UserAuthClient {

    @Path("/token")
    @POST
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun login(@RequestBody login: Form): Map<String, Any>


    @Path("/logout")
    @POST
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun logout(@HeaderParam("Authorization") authHeaderValue: String, @RequestBody logout: Form): Map<String, Any>


    @Path("/password")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun changePassword()

}
