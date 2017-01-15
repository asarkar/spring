package name.abhijitsarkar.javaee.hello.web;

import name.abhijitsarkar.javaee.hello.domain.Country;
import name.abhijitsarkar.javaee.hello.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@RestController
@RequestMapping(path = "countries", produces = APPLICATION_JSON_VALUE, method = GET)
public class CountryController {
    @Autowired
    private CountryRepository countryRepo;

    @RequestMapping
    public Page<Country> findAll(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        return countryRepo.findAll(new PageRequest(pageNum - 1, pageSize));
    }
}
