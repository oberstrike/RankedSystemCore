package server.paths

import server.domain.IMatchService
import server.domain.MatchDTO
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/match")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MatchResource {

    @Inject
    @field: Default
    lateinit var matchService: IMatchService


    @GET
    @Path("/all")
    fun getAll(): Array<MatchDTO> {
        val matchDTO = MatchDTO(0, true)
        matchService.create(matchDTO)
        return arrayOf(matchDTO)
    }

    /*
    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: String){
        return matchService.findById(id)
    }*/


}