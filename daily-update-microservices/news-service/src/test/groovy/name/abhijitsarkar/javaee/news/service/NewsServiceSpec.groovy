package name.abhijitsarkar.javaee.news.service

import name.abhijitsarkar.javaee.common.domain.Story
import name.abhijitsarkar.javaee.news.repository.NYTClient
import name.abhijitsarkar.javaee.news.repository.NYTClientStub
import spock.lang.Specification

/**
 * @author Abhijit Sarkar
 */
class NewsServiceSpec extends Specification {
    NewsService newsService = new NewsService()
    NYTClientStub nytClientStub = new NYTClientStub()

    NYTClient nytClient = Mock()

    def "gets top stories in the world section"() {
        setup:
        newsService.nytClient = nytClient
        nytClient.getTopStories(section1) >> nytClientStub.getTopStories(section1)

        when:
        Collection<Story> topStories = newsService.getTopStories([section2].asImmutable());

        then:
        topStories.isEmpty() == empty

        if (!empty) {
            topStories.collect { it.section }.every { it == section1 }
        }

        where:
        section1 | section2 | empty
        'world'  | 'world'  | false
        'world'  | 'WORLD'  | false
        'junk'   | 'junk'   | true
    }

}