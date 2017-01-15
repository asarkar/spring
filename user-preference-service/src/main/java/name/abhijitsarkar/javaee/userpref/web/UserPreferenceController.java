package name.abhijitsarkar.javaee.userpref.web;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import name.abhijitsarkar.javaee.userpref.domain.UserPreference;
import name.abhijitsarkar.javaee.userpref.repository.UserPreferenceRepository;

@Controller
@RequestMapping("/users/prefs/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class UserPreferenceController {
    private final UserPreferenceResourceAssembler resourceAssembler;
    private final UserPreferenceRepository userPreferenceRepository;

    @RequestMapping("{name}/svc/{serviceId}")
    HttpEntity<Resource<UserPreference>> findUserPreference(
	    @PathVariable("name") String name,
	    @PathVariable("serviceId") int serviceId) {
	Resource<UserPreference> userPreference = resourceAssembler
		.findUserPreference(name, serviceId);

	if (userPreference != null) {
	    return new ResponseEntity<Resource<UserPreference>>(userPreference,
		    OK);
	}

	return new ResponseEntity<Resource<UserPreference>>(NOT_FOUND);
    }

    @RequestMapping(path = "{name}/svc/{serviceId}", method = POST)
    HttpEntity<Resource<UserPreference>> createUserPreference(
	    @PathVariable("name") String name,
	    @PathVariable("serviceId") int serviceId,
	    @RequestParam("value") String value) {
	Resource<UserPreference> userPreference = resourceAssembler
		.saveUserPreference(name, serviceId, value);

	if (userPreference != null) {
	    return new ResponseEntity<Resource<UserPreference>>(userPreference, CREATED);
	}

	return new ResponseEntity<Resource<UserPreference>>(CONFLICT);
    }

    @RequestMapping(path = "{name}/svc/{serviceId}", method = PATCH)
    HttpEntity<Resource<UserPreference>> updateUserPreference(
	    @PathVariable("name") String name,
	    @PathVariable("serviceId") int serviceId,
	    @RequestParam("value") String value) {
	Resource<UserPreference> userPreference = resourceAssembler
		.saveUserPreference(name, serviceId, value);

	if (userPreference != null) {
	    return new ResponseEntity<Resource<UserPreference>>(userPreference, OK);
	}

	return new ResponseEntity<Resource<UserPreference>>(NOT_FOUND);
    }

    @RequestMapping(path = "{name}/svc/{serviceId}", method = DELETE)
    HttpEntity<Resource<UserPreference>> deleteUserPreference(
	    @PathVariable("name") String name,
	    @PathVariable("serviceId") int serviceId) {
	Optional<UserPreference> deletedUserPreference = userPreferenceRepository
		.delete(newUserPreference(name, serviceId, null));

	if (deletedUserPreference.isPresent()) {
	    return new ResponseEntity<Resource<UserPreference>>(NO_CONTENT);
	}

	return new ResponseEntity<Resource<UserPreference>>(NOT_FOUND);
    }

    private UserPreference newUserPreference(String name, int serviceId,
	    String value) {
	return new UserPreference(name, value, serviceId);
    }
}
