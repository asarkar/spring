package name.abhijitsarkar.javaee.hello.web;

import name.abhijitsarkar.javaee.hello.domain.City;
import name.abhijitsarkar.javaee.hello.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Abhijit Sarkar
 */
@RestController
@RequestMapping(path = "cities", produces = APPLICATION_JSON_VALUE, method = GET)
public class CityController {
    @Autowired
    private CityRepository cityRepo;

    @RequestMapping
    public Page<City> findAll(@RequestParam(defaultValue = "1") int pageNum,
                              @RequestParam(defaultValue = "10") int pageSize) {
        return cityRepo.findAll(new PageRequest(pageNum - 1, pageSize));
    }
}
