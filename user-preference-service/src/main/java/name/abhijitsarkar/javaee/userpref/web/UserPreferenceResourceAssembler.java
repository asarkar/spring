package name.abhijitsarkar.javaee.userpref.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import name.abhijitsarkar.javaee.userpref.domain.UserPreference;
import name.abhijitsarkar.javaee.userpref.repository.UserPreferenceRepository;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class UserPreferenceResourceAssembler
	implements ResourceAssembler<UserPreference, Resource<UserPreference>> {
    @Autowired
    private final UserPreferenceRepository userPreferenceRepository;

    private static final MappingDiscoverer DISCOVERER = new AnnotationMappingDiscoverer(
	    RequestMapping.class);

    private static final String CREATE_METHOD = "createUserPreference";

    private static final Class<UserPreferenceController> CONTROLLER_CLASS = UserPreferenceController.class;

    private static final String CREATE_LINK = "create";
    private static final String UPDATE_LINK = "update";
    private static final String DELETE_LINK = "delete";

    private UserPreference newUserPreference(String name, int serviceId,
	    String value) {
	return new UserPreference(name, value, serviceId);
    }

    @Override
    public Resource<UserPreference> toResource(UserPreference userPreference) {
	Resource<UserPreference> resource = new Resource<>(userPreference);

	Link selfLink = linkTo(methodOn(CONTROLLER_CLASS).findUserPreference(
		userPreference.getName(), userPreference.getServiceId()))
			.withSelfRel();
	// TODO: figure out how to add templated create link
	// Method createUserPreference = ReflectionUtils.findMethod(
	// UserPreferenceController.class, CREATE_METHOD, String.class,
	// int.class, String.class);
	//
	// Assert.state(createUserPreference != null,
	// String.format("Unable to find create method: %s in %s.",
	// CREATE_METHOD, CONTROLLER_CLASS.getName()));
	//
	// String mapping = DISCOVERER.getMapping(createUserPreference);
	//
	// UriTemplate uriTemplate = new UriTemplate(mapping);
	//
	// Link createLink = new Link(uriTemplate, CREATE_LINK);

	Link updateLink = linkTo(
		methodOn(CONTROLLER_CLASS).updateUserPreference(
			userPreference.getName(), userPreference.getServiceId(),
			userPreference.getValue())).withRel(UPDATE_LINK);
	Link deleteLink = linkTo(methodOn(CONTROLLER_CLASS)
		.deleteUserPreference(userPreference.getName(),
			userPreference.getServiceId())).withRel(DELETE_LINK);

	resource.add(Arrays.asList(selfLink, updateLink, deleteLink));

	return resource;
    }

    public Resource<UserPreference> findUserPreference(String name,
	    int serviceId) {
	Optional<UserPreference> userPreference = userPreferenceRepository
		.findOne(newUserPreference(name, serviceId, null));

	return userPreference.isPresent() ? toResource(userPreference.get())
		: null;
    }

    public Resource<UserPreference> saveUserPreference(String name,
	    int serviceId, String value) {
	Optional<UserPreference> userPreference = userPreferenceRepository
		.save(newUserPreference(name, serviceId, value));

	return userPreference.isPresent() ? toResource(userPreference.get())
		: null;
    }
}
