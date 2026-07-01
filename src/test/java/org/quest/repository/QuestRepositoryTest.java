package org.quest.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quest.exception.QuestDataException;
import org.quest.model.QuestStep;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuestRepositoryTest {

    @Test
    @DisplayName("Constructor should successfully load and map data from valid json file")
    void should_LoadAndMapData_When_JsonFileIsValid() {
        QuestRepository repository = new QuestRepository("valid-quest.json");
        Map<String, QuestStep> questSteps = repository.getQuestSteps();
        assertNotNull(questSteps);
        assertEquals(1, questSteps.size());
        assertTrue(questSteps.containsKey("start"));

        QuestStep expectedQuestStep = new QuestStep();
        expectedQuestStep.setId("start");
        expectedQuestStep.setText("Ви втрачаєте пам'ять. Прийняти виклик НЛО?");
        expectedQuestStep.setAnswers(Map.of("Прийняти виклик", "2",
                "Відхилити виклик", "3"));

        assertEquals(expectedQuestStep, questSteps.get("start"));
    }

    @Test
    @DisplayName("Constructor should throw QuestDataException when file does not exist")
    void should_ThrowQuestDataException_When_FileDoesNotExist() {
        QuestDataException exception = assertThrows(QuestDataException.class, () ->
                new QuestRepository("non-existent-file.json"));
        assertEquals("Can't find file: non-existent-file.json", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor should throw QuestDataException when json data is corrupted")
    void should_ThrowQuestDataException_When_JsonDataIsCorrupted() {
        QuestDataException exception = assertThrows(QuestDataException.class, () ->
                new QuestRepository("invalid-quest.json"));
        assertEquals("Can't load data from file: invalid-quest.json", exception.getMessage());
    }

    @Test
    @DisplayName("Default constructor should successfully load default quest.json file")
    void should_LoadDefaultFile_When_DefaultConstructorIsCalled() {
        QuestRepository repository = new QuestRepository();
        assertNotNull(repository.getQuestSteps());
        assertFalse(repository.getQuestSteps().isEmpty());
    }
}
