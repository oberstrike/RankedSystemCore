package server.domain.match

import elo.MatchResultType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
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
    fun getAll(@QueryParam("page") page: Int?): Array<MatchDTO> {
        return matchService.findAll(page ?: 0)
    }

    @GET
    @Path("/id/{id}")
    @Operation(
        summary = "Get the game depending on the ID",
        operationId = "getMatchById"
    )
    @APIResponses(
        value = [
            APIResponse(
                description = "",
                responseCode = "200",
                content = arrayOf(
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = MatchDTO::class)
                    )
                )
            ),
            APIResponse(
                description = "Not Found",
                responseCode = "404",
                content = arrayOf(
                    Content(
                        mediaType = MediaType.TEXT_PLAIN,
                        example = "Not Found"
                    )
                )
            )
        ]
    )
    fun getById(@PathParam("id") id: Long): MatchDTO? {
        return matchService.getById(id) ?: throw NotFoundException()
    }

    @GET
    @Operation(
        summary = "Ends the game and evaluates the result.",
        operationId = "finishMatchById"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "201",
                description = "The game was successfully finished",
                content = arrayOf(
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = MatchDTO::class)
                    )
                )
            ),
            APIResponse(
                responseCode = "404",
                description = "No Game with this ID was found",
                content = arrayOf(
                    Content(
                        mediaType = MediaType.TEXT_PLAIN,
                        example = "HTTP 404 Not Found"
                    )
                )
            ),
            APIResponse(
                responseCode = "400",
                description = "Game is already over",
                content = arrayOf(
                    Content(
                        mediaType = MediaType.TEXT_PLAIN,
                        example = "HTTP 400 Bad Request"
                    )
                )
            )
        ]
    )
    @Path("/{id}/finish/{result}")
    fun finishById(
        @PathParam("id") id: Long,
        @PathParam("result") result: MatchResultType
    ): MatchDTO {
        val matchDTO = matchService.getById(id) ?: throw NotFoundException()
        return matchService.finishGame(result, matchDTO) ?: throw BadRequestException()
    }


    @GET
    @Operation(
        summary = "Returns all games with a specific version",
        operationId = "getMatchByVersion"
    )
    @Path("/version/{version}")
    fun getByVersion(
        @PathParam(value = "version") version: String,
        @QueryParam(value = "page") page: Long?
    ): Array<MatchDTO> {
        return matchService.getByVersion(version, page ?: 1)
    }
}