package server.domain.match

import elo.MatchResultType
import io.quarkus.hibernate.orm.panache.PanacheRepository
import io.quarkus.panache.common.Page
import server.domain.ranked.RankedPlayerRepository
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class MatchRepository : PanacheRepository<Match> {

    fun findByIdOrNull(id: Long): Match? {
        return find("id", id).firstResultOptional<Match>().orElse(null)
    }

}

interface IMatchService {
    fun createFromDTO(matchDTO: MatchDTO): Match?

    fun getById(id: Long): MatchDTO?

    fun getByVersion(version: String, page: Long = 1): Array<MatchDTO>

    fun convertToDTO(match: Match): MatchDTO

    fun findAll(page: Int = 0): Array<MatchDTO>

    fun finishGame(result: MatchResultType, matchDTO: MatchDTO): MatchDTO?

}

@ApplicationScoped
class MatchServiceImpl : IMatchService {


    @Inject
    @field: Default
    lateinit var matchRepository: MatchRepository

    @Inject
    @field:Default
    lateinit var playerRepository: RankedPlayerRepository


    @Transactional
    override fun findAll(page: Int): Array<MatchDTO> {
        val query = matchRepository.findAll()
        return query.page<Match>(Page.of(page, 10)).list<Match>().map { convertToDTO(it) }.toTypedArray()
    }


    @Transactional
    override fun finishGame(
        result: MatchResultType,
        matchDTO: MatchDTO
    ): MatchDTO? {
        val match = matchRepository.findByIdOptional(matchDTO.id).orElse(null)

        if (!match.isPersistent)
            return null
        if (match.finished)
            throw Match.GameIsAlreadyOverException()

        match.gameIsOver(result)
        return convertToDTO(match)
    }

    @Transactional
    override fun createFromDTO(matchDTO: MatchDTO): Match? {
        var match = matchRepository.findById(matchDTO.id)
        if (match != null)
            return null
        match = Match()
        match.version = matchDTO.version ?: "1.0"
        val teamA = matchDTO.teamA.map { playerRepository.findById(it) }.toMutableSet()
        val teamB = matchDTO.teamB.map { playerRepository.findById(it) }.toMutableSet()
        match.teamA = teamA
        match.teamB = teamB

        matchRepository.persist(match)
        return match
    }

    @Transactional
    override fun getById(id: Long): MatchDTO? {
        val match = matchRepository.findByIdOrNull(id) ?: return null
        return convertToDTO(match)
    }

    override fun getByVersion(version: String, page: Long): Array<MatchDTO> {
        val query = matchRepository.find("version", version)
        query.page<Match>(Page.ofSize(10))

        return query.list<Match>().map { convertToDTO(it) }.toTypedArray()
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
