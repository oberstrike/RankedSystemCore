package server.domain.match

import org.eclipse.microprofile.openapi.annotations.Operation
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
    Tag(name = "Matches", description = "The path to manage the games")
)
class MatchResource {

    @Inject
    @field: Default
    lateinit var matchService: IMatchService


    @GET
    @Path("/all")
    @Operation(
        summary = "Get all matches"
    )
    fun getAll(): Array<MatchDTO> {
        return matchService.findAll().toTypedArray()
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Get the game depending on the ID"
    )
    fun getById(@PathParam("id") id: Long): MatchDTO? {
        return matchService.findById(id)?.let { matchService.convertToDTO(it) }
    }

    @GET
    @Path("/create")
    @Operation(
        summary = "Creates a game and returns it"
    )
    fun createMatch(): MatchDTO {
        return matchService.convertToDTO(matchService.createNew())
    }


    @PUT
    @Operation(
        summary = "Add a new Match"
    )
    fun addMatch(matchDTO: MatchDTO) {
        matchService.createFromDTO(matchDTO)
    }


}