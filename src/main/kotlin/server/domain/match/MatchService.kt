package server.domain.match

import elo.MatchResultType
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

    fun getById(id: Long): Match?

    fun getByVersion(version: String): MatchDTO?

    fun convertToDTO(match: Match): MatchDTO

    fun findAll(): List<MatchDTO>

    fun finishGame(result: MatchResultType, match: Match): MatchDTO?
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
    override fun finishGame(
        result: MatchResultType,
        match: Match
    ): MatchDTO? {
        if (!match.isPersistent)
            return null
        match.gameIsOver(result)
        return convertToDTO(match)
    }

    @Transactional
    override fun createFromDTO(matchDTO: MatchDTO): Match? {
        var match = getById(matchDTO.id)
        if (match != null)
            return null
        match = Match()
        match.version = matchDTO.version

        matchRepository.persist(match)
        return match
    }

    @Transactional
    override fun getById(id: Long): Match? {
        return matchRepository.findByIdOptional(id).orElse(null)
    }

    override fun getByVersion(version: String): MatchDTO? {
        return convertToDTO(matchRepository.find("version", version).firstResultOptional<Match>().orElse(null))
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
