package server.domain.match

import elo.MatchResultType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.openapi.annotations.tags.Tags

import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/match")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tags(
    Tag(name = "Match", description = "The path to manage the games")
)
class MatchResource {

    @Inject
    @field: Default
    lateinit var matchService: IMatchService

    @POST
    @Operation(
        summary = "Add a new Match",
        operationId = "addMatch"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "201",
                description = "A new game was successfully created",
                content = arrayOf(
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = MatchDTO::class)
                    )
                )
            ),
            APIResponse(
                responseCode = "400",
                description = "There was an error when creating a new game"
            )
        ]
    )
    fun addMatch(matchDTO: MatchDTO) {
        matchService.createFromDTO(matchDTO) ?: throw BadRequestException()
    }


    @GET
    @Path("/all")
    @Operation(
        summary = "Get all matches",
        operationId = "getAllMatches"
    )
    fun getAll(): Array<MatchDTO> {
        return matchService.findAll().toTypedArray()
    }

    @GET
    @Path("/id/{id}")
    @Operation(
        summary = "Get the game depending on the ID",
        operationId = "getMatchById"
    )
    fun getById(@PathParam("id") id: Long): MatchDTO? {
        return matchService.getById(id)?.let { matchService.convertToDTO(it) }
    }

    @GET
    @Operation(
        summary = "Ends the game and evaluates the result.",
        operationId = "finishMatchById"
    )
    @Path("/{id}/finish/{result}")
    fun finishById(
        @PathParam("id") id: Long,
        @PathParam("result") result: MatchResultType
    ): MatchDTO {
        val match = matchService.getById(id) ?: throw NotFoundException()
        return matchService.finishGame(result, match) ?: throw BadRequestException()
    }


    @GET
    @Operation(
        summary = "Returns all games with a specific version",
        operationId = "getMatchByVersion"
    )
    @Path("/version/{version}")
    fun getByVersion(@PathParam(value = "version") version: String): MatchDTO? {
        return matchService.getByVersion(version)
    }


}