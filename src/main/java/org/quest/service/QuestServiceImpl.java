package org.quest.service;

import lombok.Getter;
import org.quest.model.QuestStep;
import org.quest.repository.QuestRepository;

import java.util.Map;
import java.util.Objects;

@Getter
public class QuestServiceImpl implements QuestService {
    private static final String QUEST_START_ID = "start";
    private final QuestRepository repository;
    private QuestStep currentStep;

    public QuestServiceImpl(QuestRepository repository) {
        this.repository = repository;
        this.currentStep = repository.getQuestSteps().get(QUEST_START_ID);
        if (this.currentStep == null) {
            throw new IllegalStateException("Start step of quest with id: " + QUEST_START_ID + " not found!");
        }
    }

    @Override
    public void makeStep(String answer) {
        String nextStepId = getNextStepId(answer);
        currentStep = getNextStep(nextStepId);
    }

    @Override
    public void reset() {
        this.currentStep = repository.getQuestSteps().get(QUEST_START_ID);
    }

    private String getNextStepId(String answer) {
        Map<String, String> answers = Objects.requireNonNullElse(
                currentStep.getAnswers(), Map.of());
        if (!answers.containsKey(answer)) {
            throw new IllegalArgumentException("Invalid answer choice: " + answer);
        }
        return answers.get(answer);
    }

    private QuestStep getNextStep(String nextStepId) {
        QuestStep nextStep = repository.getQuestSteps().get(nextStepId);
        if (nextStep == null) {
            throw new IllegalStateException("Quest step with id: '" + nextStepId + "' was not found in repository!");
        }
        return nextStep;
    }
}
