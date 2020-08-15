package server.domain.queue

import io.quarkus.hibernate.orm.panache.PanacheRepository
import server.domain.match.Match
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class GameQueueRepository : PanacheRepository<GameQueue> {

    fun findByIdOrNull(id: Long): GameQueue? {
        return find("id", id).firstResultOptional<GameQueue>().orElse(null)
    }

}

interface GameQueueService {

}

class GameQueueServiceImpl {
}