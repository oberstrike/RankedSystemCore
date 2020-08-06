package server.domain.ranked

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

@Path("/player")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tags(
    Tag(name = "Player", description = "The path to manage the players")
)
class RankedPlayerResource {

    @Inject
    @field: Default
    lateinit var rankedPlayerService: RankedPlayerService

    @POST
    @Operation(
        summary = "Method for creating a new player with a starting rating of 1000",
        operationId = "addPlayer"
    )
    @APIResponses(
        value = [
            APIResponse(responseCode = "201", description = "A new player was created"),
            APIResponse(responseCode = "400", description = "The request was incorrect or the name is already used")
        ]
    )
    fun addPlayer(playerDTO: RankedPlayerDTO): RankedPlayerDTO {
        val existing = rankedPlayerService.getPlayerByName(playerDTO.name)
        if (existing != null)
            throw BadRequestException()

        return rankedPlayerService.create(playerDTO) ?: throw BadRequestException()
    }

    @GET
    @Operation(
        summary = "Get all players",
        operationId = "getAllPlayers"
    )
    @Path("/all")
    fun getAllPlayers(@QueryParam("page") page: Long?): Array<RankedPlayerDTO> {
        return rankedPlayerService.getAll(page ?: 1)
    }

    @Operation(
        summary = "Get player by name",
        operationId = "getPlayerByName"
    )
    @GET
    @APIResponses(
        APIResponse(
            responseCode = "200",
            description = "A suitable player was found",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = RankedPlayerDTO::class)
            )]
        ),
        APIResponse(responseCode = "404", description = "No player with the given name was found")
    )
    @Path("/name/{name}")
    fun getPlayerByName(@PathParam("name") name: String): RankedPlayerDTO? {
        return rankedPlayerService.getPlayerByName(name) ?: throw NotFoundException()
    }

    @GET
    @Operation(
        summary = "Get player by id",
        operationId = "getPlayerById"
    )
    @Path("/id/{id}")
    @APIResponses(
        APIResponse(
            responseCode = "200",
            description = "A suitable player was found",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = RankedPlayerDTO::class)
            )]
        ),
        APIResponse(responseCode = "404", description = "No player with the given ID was found")
    )
    fun getPlayerById(@PathParam("id") id: Long): RankedPlayerDTO? {
        return rankedPlayerService.getPlayerById(id) ?: throw NotFoundException()
    }

    @GET
    @Path("/{name}/matches")
    @Operation(
        summary = "Get All Matches by the name of the user as array of their ids",
        operationId = "getMatchesByPlayerName"
    )
    fun getMatchesFromPlayerName(@PathParam(value = "name") name: String): Array<Long> {
        val player = rankedPlayerService.getPlayerByName(name) ?: throw NotFoundException()
        return rankedPlayerService.getMatchesByPlayer(player)
    }
}