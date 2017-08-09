package org.abhijitsarkar.demo

import com.jcraft.jsch.Session
import org.eclipse.jgit.api.Git.cloneRepository
import org.eclipse.jgit.errors.NoRemoteRepositoryException
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshSessionFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.ipc.netty.http.server.HttpServer
import java.io.File
import java.lang.System.getenv
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files.createTempDirectory
import java.nio.file.Files.isDirectory
import java.nio.file.Files.isReadable
import java.nio.file.Files.readAllBytes
import java.nio.file.Paths
import java.util.Objects

class DemoApplication {
    companion object {
        init {
            SshSessionFactory.setInstance(object : JschConfigSessionFactory() {
                override fun configure(hc: OpenSshConfig.Host, session: Session) {
                    session.setConfig("StrictHostKeyChecking", "no")
                }
            })
        }
    }
}

inline fun <reified T : Any> Throwable.findCauseOfType(): T? =
        generateSequence(this) { it.cause }.filterIsInstance<T>().firstOrNull()

fun main(args: Array<String>) {

    fun clone() = HandlerFunction { request: ServerRequest ->
        try {
            val name = request.pathVariables().getOrDefault("projectName",
                    getenv("PROJECT_NAME") ?: "unknown")
            val projectDir = createTempDirectory(null).toFile()
                    .apply { deleteOnExit() }
                    .let { File(it, name) }

            val git = cloneRepository()
                    .setURI("git@github.com:asarkar/$name.git")
                    .setDirectory(projectDir)
                    .setCloneAllBranches(false)
                    .setBranch("master")
                    .call()
            git?.close()

            ok().body(fromObject(projectDir.absolutePath))
        } catch (e: Exception) {
            e.printStackTrace()

            val cause = e.findCauseOfType<NoRemoteRepositoryException>()

            when {
                Objects.isNull(cause) -> status(INTERNAL_SERVER_ERROR).body(fromObject(e.message))
                else -> status(BAD_REQUEST).body(fromObject(cause!!.message))
            }
        }
    }

    fun read() = HandlerFunction { request: ServerRequest ->
        val fileName = request.pathVariable("fileName")
        val file = Paths.get("/root", ".ssh", fileName)

        when {
            isReadable(file) && !isDirectory(file) -> ok().body(fromObject(String(readAllBytes(file), UTF_8)))
            else -> status(BAD_REQUEST).body(fromObject("${file.toFile().absolutePath} is not readable or is a directory."))
        }
    }

    fun routerFunction(): RouterFunction<ServerResponse> =
            route(POST("/"), clone())
                    .and(route(POST("/{projectName}"), clone()))
                    .and(route(GET("/ssh/{fileName}"), read()))

    toHttpHandler(routerFunction())
            .let { ReactorHttpHandlerAdapter(it) }
            .let {
                HttpServer.create(getenv("POD_IP") ?: "localhost", 8080)
                        .startAndAwait(it, null)
            }
}
