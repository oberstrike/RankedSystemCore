package server.util

import io.quarkus.jsonb.JsonbConfigCustomizer
import server.domain.Match
import javax.json.bind.JsonbConfig
import javax.json.bind.annotation.JsonbTypeSerializer
import javax.json.bind.serializer.JsonbSerializer
import javax.json.bind.serializer.SerializationContext
import javax.json.stream.JsonGenerator
import kotlin.reflect.KClass

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