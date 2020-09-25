package util

import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import org.testcontainers.containers.GenericContainer
import javax.enterprise.context.ApplicationScoped

class PostgresContainerCreator : IContainerCreator<KPostgreSQLContainer> {

    companion object {
        const val USERNAME = "postgres"
        const val PASSWORD = "postgres"
        const val PORT = 5434
        const val DB_NAME = "ranked"
        const val DB_KIND = "postgresql"
    }


    override fun getConfig(): MutableMap<String, String> {
        return mutableMapOf(
            "quarkus.datasource.jdbc.url" to "jdbc:postgresql://localhost:$PORT/$DB_NAME",
            "quarkus.datasource.password" to PASSWORD,
            "quarkus.datasource.username" to USERNAME,
            "quarkus.datasource.db-kind" to DB_KIND
        )
    }

    override fun getContainer(): GenericContainer<KPostgreSQLContainer> {
        return KPostgreSQLContainer().apply {
            withDatabaseName(DB_NAME)
            withUsername(USERNAME)
            withPassword(PASSWORD)
            withExposedPorts(PORT)
            withCreateContainerCmdModifier { cmd ->
                cmd.withHostConfig(
                    HostConfig.newHostConfig().withPortBindings(
                        PortBinding.parse("$PORT:${5432}")
                    )
                )
            }
        }
    }
}
