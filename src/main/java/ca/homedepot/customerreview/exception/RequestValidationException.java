package ca.homedepot.customerreview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * @author Sean Li
 */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class RequestValidationException extends RuntimeException
{
	
	@Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
	
	
	public RequestValidationException(String message)
	{
		super(message);
	}
}
