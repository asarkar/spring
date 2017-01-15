package name.abhijitsarkar.javaee.salon.user.web;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import name.abhijitsarkar.javaee.salon.user.repository.AuthorityRepository;

@RepositoryRestController
@RequestMapping(value = "/users/search", method = GET)
public class UserController {
	@Autowired
	private AuthorityRepository authorityRepository;

	@RequestMapping(value = "/findUserDetailsByUsername", method = GET)
	ResponseEntity<Resource<UserDetails>> findUserDetailsByUsername(@RequestParam("username") String username) {
		UserDetails userDetails = authorityRepository.loadUserByUsername(username);

		Resource<UserDetails> userDetailsResource = new Resource<>(userDetails);

		return new ResponseEntity<>(userDetailsResource, OK);
	}
}
