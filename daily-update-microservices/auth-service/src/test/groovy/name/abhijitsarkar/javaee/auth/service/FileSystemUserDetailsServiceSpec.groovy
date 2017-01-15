package name.abhijitsarkar.javaee.auth.service

import name.abhijitsarkar.javaee.common.ObjectMapperFactory
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Abhijit Sarkar
 */
class FileSystemUserDetailsServiceSpec extends Specification {
    @Shared
    FileSystemUserDetailsService service = new FileSystemUserDetailsService()

    def setupSpec() {
        service.objectMapper = ObjectMapperFactory.newInstance()
        service.init()
    }

    @Unroll
    def "loads user #username"() {
        when:
        def userDetails = service.loadUserByUsername(username)

        then:
        userDetails.password == 'secret'
        userDetails.enabled == enabled
        userDetails.authorities.join(',') == authorities

        where:
        username          | enabled | authorities
        'abhijitsarkar'   | true    | 'ADMIN'
        'johndoe'         | true    | 'NEWS'
        'janedoe'         | true    | 'MOVIES'
        'johnnyappleseed' | true    | 'WEATHER'
        'nopayer'         | false   | ''
    }

    def "throws exception when username not found"() {
        when:
        service.loadUserByUsername('noone')

        then:
        thrown(UsernameNotFoundException)
    }
}
