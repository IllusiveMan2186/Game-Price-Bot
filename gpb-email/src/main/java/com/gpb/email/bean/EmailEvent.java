package com.gpb.email.bean;

import lombok.Data;

import java.util.Locale;
import java.util.Map;

@Data
public class EmailEvent {

    private String recipient;

    private String subject;

    private Map<String, Object> variables;

    private Locale locale;

    private String templateName;
}
