package util

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer

class KPostgreSQLContainer() : PostgreSQLContainer<KPostgreSQLContainer>("postgres")

interface IContainerCreator<T : GenericContainer<T>?> {

    fun getContainer(): GenericContainer<T>

    fun getConfig(): MutableMap<String, String>
}
