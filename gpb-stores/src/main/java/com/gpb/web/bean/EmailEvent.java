package com.gpb.web.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailEvent {

    private String recipient;

    private String subject;

    private Map<String, Object> variables;

    private Locale locale;

    private String templateName;
}
