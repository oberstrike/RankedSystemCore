package server.domain.ranked

import io.quarkus.hibernate.orm.panache.PanacheRepository
import io.quarkus.panache.common.Page
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class RankedPlayerRepository : PanacheRepository<RankedPlayer> {

    fun findByName(name: String): RankedPlayer? {
        return find("name", name).firstResultOptional<RankedPlayer>().orElse(null)
    }


}

interface RankedPlayerService {

    fun getAll(page: Long): Array<RankedPlayerDTO>

    fun getPlayerByDTO(playerDTO: RankedPlayerDTO): RankedPlayerDTO?

    fun getPlayerById(id: Long): RankedPlayerDTO?

    fun getPlayerByName(name: String): RankedPlayerDTO?

    fun getMatchesByPlayer(playerDTO: RankedPlayerDTO): Array<Long>

    fun convertToDTO(player: RankedPlayer?): RankedPlayerDTO?

    fun create(playerDTO: RankedPlayerDTO): RankedPlayerDTO?


}

@ApplicationScoped
class RankedPlayerServiceImpl : RankedPlayerService {

    @Inject
    @field: Default
    lateinit var rankedPlayerRepository: RankedPlayerRepository

    override fun getAll(page: Long): Array<RankedPlayerDTO> {
        val query = rankedPlayerRepository
            .findAll()
        query.page<RankedPlayer>(Page.ofSize(10))
        return query.list<RankedPlayer>().map { convertToDTO(it)!! }.toTypedArray()
    }


    override fun getPlayerByDTO(playerDTO: RankedPlayerDTO) =
        convertToDTO(rankedPlayerRepository.findByName(playerDTO.name))


    @Transactional
    override fun getPlayerById(id: Long) = convertToDTO(rankedPlayerRepository.findByIdOptional(id).orElse(null))


    @Transactional
    override fun getPlayerByName(name: String) =
        convertToDTO(rankedPlayerRepository.findByName(name))

    override fun getMatchesByPlayer(playerDTO: RankedPlayerDTO): Array<Long> {
        val player = getPlayerById(playerDTO.id) ?: return emptyArray()
        return player.matches
    }


    @Transactional
    override fun convertToDTO(player: RankedPlayer?): RankedPlayerDTO? {
        return if (player != null) {
            RankedPlayerDTO(
                id = player.id,
                rating = player.rating,
                matches = player.matches.map { it.id }.toTypedArray(),
                name = player.name
            )
        } else {
            null
        }
    }


    @Transactional
    override fun create(playerDTO: RankedPlayerDTO): RankedPlayerDTO? {
        val rankedPlayer = RankedPlayer.Builder()
            .id(null)
            .name(playerDTO.name)
            .build()

        rankedPlayerRepository.persist(rankedPlayer)
        if (rankedPlayer.isPersistent)
            return convertToDTO(rankedPlayer)
        return null
    }


}