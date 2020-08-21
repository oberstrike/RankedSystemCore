package server.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.quarkus.jackson.ObjectMapperCustomizer
import io.vertx.core.http.HttpServerRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.core.Context
import javax.ws.rs.core.UriInfo


//@Provider
class LoggingFilter : ContainerRequestFilter, ContainerResponseFilter {

    private val logger: Logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    @Context
    lateinit var info: UriInfo

    @Context
    lateinit var request: HttpServerRequest

    override fun filter(ctx: ContainerRequestContext) {
        val stringBuilder = StringBuilder()

        val request = "Request: [ Method: ${ctx.method} Path: ${info.path} ]"
        stringBuilder.append(request)

        val securityContext = ctx.securityContext
        if (securityContext.userPrincipal != null) {
            stringBuilder.append(", Security: [${securityContext.userPrincipal.name}]")
        }

        logger.info(stringBuilder.toString())

    }


    override fun filter(r: ContainerRequestContext, ctx: ContainerResponseContext) {
        val stringBuilder = StringBuilder()
        val response = "Response: [ Method: ${r.method} Path: ${info.path}: Status: ${ctx.status} ]"
        stringBuilder.append(response)

        val securityContext = r.securityContext
        if(securityContext.userPrincipal != null){
            stringBuilder.append(", Security: [${securityContext.userPrincipal.name}]")
        }

        logger.info(stringBuilder.toString())
    }

}

@Singleton
class RegisterCustomModuleCustomizer : ObjectMapperCustomizer {
    override fun customize(mapper: ObjectMapper) {
        mapper.registerModule(KotlinModule())
    }
}