package com.asarkar.spring.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import kotlin.random.Random

data class Person(
    val id: Int?,
    val firstName: String,
    val lastName: String
)

@RestController
@RequestMapping("/persons")
class PersonController(val redisTemplate: ReactiveStringRedisTemplate) {
    private val objectMapper = Jackson2ObjectMapperBuilder.json().build<ObjectMapper>()

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun savePerson(@RequestBody person: Person): Mono<Int> {
        val p = if (person.id == null) person.copy(id = Random.nextInt(1, 10000))
        else person
        return redisTemplate.opsForValue()
            .set("person:${p.id}", objectMapper.writeValueAsString(p))
            .filter { it }
            .map { p.id }
    }

    @GetMapping(path = ["{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPerson(@PathVariable("id") id: Int): Mono<Person> {
        return redisTemplate.opsForValue()
            .get("person:$id")
            .map { objectMapper.readValue(it, Person::class.java) }
    }
}