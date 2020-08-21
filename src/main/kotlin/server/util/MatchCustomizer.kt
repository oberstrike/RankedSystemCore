package server.util

import io.quarkus.jsonb.JsonbConfigCustomizer
import server.domain.auth.LoginForm
import server.domain.match.Match
import java.lang.reflect.Type
import javax.inject.Singleton
import javax.json.bind.JsonbConfig
import javax.json.bind.serializer.DeserializationContext
import javax.json.bind.serializer.JsonbDeserializer
import javax.json.bind.serializer.JsonbSerializer
import javax.json.bind.serializer.SerializationContext
import javax.json.stream.JsonGenerator
import javax.json.stream.JsonParser

@Singleton
class MatchCustomizer : JsonbConfigCustomizer {

    override fun customize(jsonbConfig: JsonbConfig?) {
        jsonbConfig?.withSerializers(MatchSerializer())
    }
}

class MatchSerializer : JsonbSerializer<Match> {

    override fun serialize(match: Match?, generator: JsonGenerator?, context: SerializationContext?) {
        if (match == null)
            return
        if (generator == null)
            return
        if (context == null)
            return
        val id = match.id.toString()
        val finished = match.finished
        val teamA = match.teamA.map { it.name }
        val teamB = match.teamB.map { it.name }


        generator.write("id", id)
        generator.write("finished", finished)
        generator.write("teamA", context.serialize(teamA, generator).toString())
        generator.write("teamB", context.serialize(teamB, generator).toString())
        generator.flush()
    }
}

