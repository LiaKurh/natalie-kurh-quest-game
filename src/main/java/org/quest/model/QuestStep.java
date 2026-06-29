package org.quest.model;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestStep {
    private String id;
    private String text;
    private Map<String, String> answers;
}
