package rest

import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.junit.jupiter.api.Test


open class AbstractRestTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    protected var port: Int = 0

    protected fun sendGet(path: String, auth: Pair<String, String>? = null, params: Map<String, *>? = null): Response {
        return Given {
            if (auth != null)
                auth().preemptive().basic(auth.first, auth.second)
            if (params != null)
                params(params)
            port(port)
            log().all()
        }.When {
            get(path)
        }
    }

    protected fun sendPost(path: String, body: String, auth: Pair<String, String>? = null): Response {
        return Given {
            if (auth != null)
                auth().preemptive().basic(auth.first, auth.second)
            port(port)
            log().all()
            body(body)
            contentType(ContentType.JSON)
        }.When {
            post(path)
        }
    }

    protected fun sendPatch(
        path: String,
        body: String,
        auth: Pair<String, String>? = null,
        params: Map<String, *>? = null
    ): Response {
        return Given {
            if (auth != null)
                auth().preemptive().basic(auth.first, auth.second)
            port(port)
            if (params != null)
                params(params)
            log().all()
            body(body)
            contentType(ContentType.JSON)
        }.When {
            patch(path)
        }

    }

}