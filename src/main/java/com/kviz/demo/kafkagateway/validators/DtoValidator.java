package com.kviz.demo.kafkagateway.validators;

import com.kviz.demo.kafkagateway.dto.Request;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

/**
 * Валидатор CntsEnvelopeDto по обязательным полям сообщения передаваемого в Кафку для внутрисистемной интеграции между сервисами ЦЕНТРОС.
 */
@Component
public class DtoValidator {

    public void validCntsEnvelopeDto(Request request){
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
