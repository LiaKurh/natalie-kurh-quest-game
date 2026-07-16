package org.quest.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quest.model.QuestStep;
import org.quest.repository.QuestRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestServiceImplTest {
    @Mock
    private QuestRepository questRepository;
    private Map<String, QuestStep> questSteps;

    @BeforeEach
    void setUp() {
        QuestStep startStep = new QuestStep();
        startStep.setId("start");
        startStep.setText("Start quest");
        Map<String, String> answers = new HashMap<>();
        answers.put("Yes", "step2");
        answers.put("No", "step3");
        startStep.setAnswers(answers);

        QuestStep nextStep = new QuestStep();
        nextStep.setId("step2");
        nextStep.setText("Step 2");
        nextStep.setAnswers(new HashMap<>());

        questSteps = new HashMap<>();
        questSteps.put("start", startStep);
        questSteps.put("step2", nextStep);

    }

    @Test
    @DisplayName("Constructor should throw IllegalStateException with correct message when currentStep is null")
    void should_ThrowIllegalStateExceptionWithCorrectMessage_When_CurrentStepIsNull() {
        when(questRepository.getQuestSteps()).thenReturn(Map.of());
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                new QuestServiceImpl(questRepository));
        assertEquals("Start step of quest with id: start not found!",
                exception.getMessage());
    }

    @Test
    @DisplayName("makeStep() should throw IllegalArgumentException with correct message when parameter is invalid")
    void should_ThrowIllegalArgumentException_When_AnswerIsInvalid() {
        when(questRepository.getQuestSteps()).thenReturn(questSteps);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new QuestServiceImpl(questRepository).makeStep("invalid answer"));
        assertEquals("Invalid answer choice: invalid answer", exception.getMessage());
    }

    @Test
    @DisplayName("makeStep() should throw IllegalStateException with correct message when next step not found")
    void should_ThrowIllegalStateException_When_NextStepIsNull() {
        when(questRepository.getQuestSteps()).thenReturn(questSteps);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                new QuestServiceImpl(questRepository).makeStep("No"));
        assertEquals("Quest step with id: 'step3' was not found in repository!",
                exception.getMessage());
    }


    @Test
    @DisplayName("makeStep() should change current quest step to next")
    void should_ChangeCurrentStepToNextQuestStep_When_AnswerIsValid() {
        when(questRepository.getQuestSteps()).thenReturn(questSteps);
        QuestService questService = new QuestServiceImpl(questRepository);
        QuestStep currentQuestStep = questService.getCurrentStep();
        questService.makeStep("Yes");
        assertNotNull(questService.getCurrentStep());
        assertNotEquals(questService.getCurrentStep(), currentQuestStep);
        assertEquals("step2", questService.getCurrentStep().getId());
        assertEquals("Step 2", questService.getCurrentStep().getText());
    }

    @Test
    @DisplayName("reset() should change current quest step to start step")
    void should_ChangeCurrentQuestStepToStartStep_When_ResetIsCalled() {
        when(questRepository.getQuestSteps()).thenReturn(questSteps);
        QuestService questService = new QuestServiceImpl(questRepository);
        questService.makeStep("Yes");
        QuestStep questStep = questService.getCurrentStep();
        questService.reset();
        QuestStep actualQuestStep = questService.getCurrentStep();
        QuestStep expectedQuestStep = questSteps.get("start");
        assertNotNull(actualQuestStep);
        assertNotEquals(questStep, actualQuestStep);
        assertEquals(expectedQuestStep, actualQuestStep);
    }
}