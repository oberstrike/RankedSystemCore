package server.domain

import elo.IMatch

import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.transaction.Transactional


interface IMatchService {
    fun create(matchDTO: MatchDTO): IMatch?
    fun convert(matchDTO: MatchDTO): IMatch?
}

@ApplicationScoped
class MatchServiceImpl() : IMatchService {

    @Inject
    @field: Default
    lateinit var entityManager: EntityManager

    @Transactional
    override fun create(matchDTO: MatchDTO): Match? {
        val match = convert(matchDTO) ?: return null
        entityManager.persist(match)
        return match
    }

    override fun convert(matchDTO: MatchDTO): Match? {
        val match = Match.Builder()
            .id(matchDTO.id)
            .build()
        return if (match == null) null else match as Match
    }


}
