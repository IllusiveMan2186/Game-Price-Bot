package com.gpb.common.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationEvent {

    private long basicUserId;

    private Map<String, Object> variables;
}
