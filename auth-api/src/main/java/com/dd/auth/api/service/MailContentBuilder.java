package com.dd.auth.api.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Data
@AllArgsConstructor
@Service
public class MailContentBuilder {

    private final TemplateEngine templateEngine;

    String build(String message) {
        Context context = new Context();
        context.setVariable("message", message);

        return templateEngine.process("th_mailTemplate", context);
    }
}
