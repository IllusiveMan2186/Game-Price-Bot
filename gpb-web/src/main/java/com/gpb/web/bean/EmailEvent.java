package com.gpb.web.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thymeleaf.context.Context;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailEvent {

    private String recipient;

    private String subject;

    private Context context;

    private String templateName;
}
