package org.abhijitsarkar.client

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

/**
 * @author Abhijit Sarkar
 */
typealias GroupName = String

interface GitLabClient {
    fun projects(pair: Pair<GroupName, GitLabProperties.GroupProperties>): Flux<Pair<GroupName, Group.Project>>
}

internal class GitLabClientImpl constructor(
        val webClient: WebClient = org.abhijitsarkar.webClient(),
        val gitLabProperties: GitLabProperties
) : GitLabClient {
    override fun projects(pair: Pair<GroupName, GitLabProperties.GroupProperties>): Flux<Pair<GroupName, Group.Project>> =
            webClient
                    .get()
                    .uri("{baseUrl}/api/v4/groups/{groupName}", gitLabProperties.baseUrl, pair.first)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("PRIVATE-TOKEN", pair.second.privateToken)
                    .retrieve()
                    .bodyToMono(Group::class.java)
                    .retry(2)
                    .flatMapIterable(Group::projects)
                    .map { pair.first.to(it) }
                    .onErrorResume { _ -> Flux.empty() }
}