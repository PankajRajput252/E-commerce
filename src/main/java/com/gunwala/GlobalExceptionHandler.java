package com.gunwala;

import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.ConflictException;
import com.gunwala.exception.DuplicateTransactionReferenceException;
import com.gunwala.exception.FieldValidationException;
import com.gunwala.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(FinalException.class)
//    public ResponseEntity<?> handleFinalException(FinalException ex) {
//
//        FinalResponse response = new FinalResponse();
//        Util.setMessage(response, "100", ex.getMessage());
//
//        return ResponseEntity.badRequest().body(response);
//    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleException(FinalException exc){
        ExceptionResponse error = new ExceptionResponse();
//        error.setStatus(HttpStatus.BAD_REQUEST.value()); to make tapresponse and advice uniform
        error.setStatusCode("100");
        error.setStatus(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleException(RecruitmentException exc){
        ExceptionResponse error = new ExceptionResponse();
        error.setStatusCode("100");
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        logger.error("Global exception handler caught: ", e);
        return ResponseEntity.status(500).body("Internal server error");
    }


    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Bad value in one field: the UI shows the message under that field. */
    @ExceptionHandler(FieldValidationException.class)
    public ProblemDetail handleFieldValidation(FieldValidationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setProperty("errors", Map.of(ex.getField(), ex.getMessage()));
        return pd;
    }

    /** @PreAuthorize failures (e.g. a non-admin calling an admin endpoint); otherwise the catch-all turns them into 500. */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You don't have permission to do this.");
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Same reference number submitted twice. 409 with a machine-readable code and a field error
     * (same "errors" shape as validation failures) so the UI can show it under the Reference No field.
     */
    @ExceptionHandler(DuplicateTransactionReferenceException.class)
    public ProblemDetail handleDuplicateReference(DuplicateTransactionReferenceException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Duplicate transaction reference");
        pd.setProperty("code", DuplicateTransactionReferenceException.CODE);
        pd.setProperty("errors", Map.of("transactionReference", ex.getMessage()));
        if (ex.getExistingPaymentId() != null) {
            pd.setProperty("existingPaymentId", ex.getExistingPaymentId());
        }
        return pd;
    }

    /** Covers @Valid on @RequestBody and @ModelAttribute (MethodArgumentNotValidException extends BindException). */
    @ExceptionHandler(BindException.class)
    public ProblemDetail handleValidation(BindException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> Objects.requireNonNullElse(fe.getDefaultMessage(), "is invalid"),
                        (first, second) -> first));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler({MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    public ProblemDetail handleMissingPart(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleTooLarge(MaxUploadSizeExceededException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE,
                "File is too large. Maximum allowed size is 5 MB.");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "This record was updated by someone else. Please refresh and try again.");
    }

}
