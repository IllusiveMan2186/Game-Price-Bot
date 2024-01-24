package com.gpb.email.bean;

import lombok.Data;
import org.thymeleaf.context.Context;

@Data
public class EmailEvent {

    private String recipient;

    private String subject;

    private Context context;

    private String templateName;
}
