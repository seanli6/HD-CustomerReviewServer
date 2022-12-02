package ca.homedepot.customerreview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * @author Weichen Zhou
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException
{
	
	@Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
	
	
	public UserNotFoundException(Long userId)
	{
		super("User " + userId + " not found!");
	}
}
