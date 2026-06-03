package dunglt.temporal.error.service;

import io.temporal.failure.ApplicationFailure;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ErrorService {

    public AppException notFound(String code, String message, String details) {
        return new AppException(HttpStatus.NOT_FOUND, code, message, details);
    }

    public AppException badRequest(String code, String message, String details) {
        return new AppException(HttpStatus.BAD_REQUEST, code, message, details);
    }

    public AppException externalHttpError(HttpStatus status, String code, String message, String details) {
        return new AppException(status, code, message, details);
    }

    public void nonRetryableError(String message) {
        throw ApplicationFailure.newNonRetryableFailure(message, "NonRetryableError");
    }

        public void retryableError(String message) {
            throw ApplicationFailure.newFailure(message, "RetryableError");
        }
}
