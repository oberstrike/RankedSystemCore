package server.util

import server.domain.auth.EmailNotFoundException
import server.domain.auth.RegistrationException
import java.nio.file.attribute.UserPrincipalNotFoundException
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class NotFoundExceptionHandler : ExceptionMapper<NotFoundException> {

    override fun toResponse(exception: NotFoundException): Response {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.message).build()
    }
}

@Provider
class BadRequestExceptionHandler : ExceptionMapper<BadRequestException> {

    override fun toResponse(exception: BadRequestException): Response {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.message).build()
    }

}

@Provider
class UserPrincipalNotFoundExceptionHandler : ExceptionMapper<UserPrincipalNotFoundException> {

    override fun toResponse(exception: UserPrincipalNotFoundException): Response {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.message).build()
    }

}

@Provider
class RegistrationExceptionHandler : ExceptionMapper<RegistrationException> {

    override fun toResponse(registrationException: RegistrationException): Response {
        return Response.status(Response.Status.BAD_REQUEST).entity(registrationException.message).build()
    }
}

@Provider
class EmailNotFoundExceptionHandler : ExceptionMapper<EmailNotFoundException> {

    override fun toResponse(emailNotFoundException: EmailNotFoundException): Response {
        return Response.status(Response.Status.BAD_REQUEST).entity(emailNotFoundException.message).build()
    }
}


