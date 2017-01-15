package name.abhijitsarkar.javaee.salon.web;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CatchAllExceptionHandler {
	@ExceptionHandler(Exception.class)
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<String> catchAll(HttpServletRequest req, Exception e) throws Exception {
		return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
	}
}
