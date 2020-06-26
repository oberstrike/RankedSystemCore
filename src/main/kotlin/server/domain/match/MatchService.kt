package server.domain.match

import io.quarkus.hibernate.orm.panache.PanacheRepository
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class MatchRepository : PanacheRepository<Match> {

}

interface IMatchService {
    fun createFromDTO(matchDTO: MatchDTO): Match?

    fun createNew(): Match

    fun findById(id: Long): Match?

    fun convertToDTO(match: Match): MatchDTO

    fun findAll(): List<MatchDTO>
}

@ApplicationScoped
class MatchServiceImpl : IMatchService {


    @Inject
    @field: Default
    lateinit var matchRepository: MatchRepository

    @Transactional
    override fun createNew(): Match {
        val match = Match.Builder().id(0).build()
        match.persistAndFlush()
        return match
    }

    @Transactional
    override fun findAll(): List<MatchDTO> {
        return matchRepository.findAll().list<Match>().map { convertToDTO(it) }
    }

    @Transactional
    override fun createFromDTO(matchDTO: MatchDTO): Match? {
        var match = findById(matchDTO.id)
        if (match != null)
            return null
        match = Match()

        matchRepository.persist(match)
        return match
    }

    @Transactional
    override fun findById(id: Long): Match? {
        return matchRepository.findByIdOptional(id).orElse(null)
    }


    override fun convertToDTO(match: Match): MatchDTO {
        val matchDTO = MatchDTO()
        matchDTO.id = match.id
        matchDTO.finished = match.finished
        matchDTO.teamA = match.teamA.map { it.id }.toTypedArray()
        matchDTO.teamB = match.teamB.map { it.id }.toTypedArray()
        matchDTO.version = match.version


        return matchDTO
    }

}
