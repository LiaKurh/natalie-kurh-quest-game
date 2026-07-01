package org.quest.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.quest.exception.QuestDataException;
import org.quest.model.QuestStep;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class QuestRepository {
    private static final String DEFAULT_FILE = "quest.json";
    private final String fileName;
    private final ObjectMapper mapper;
    private final Map<String, QuestStep> questSteps;

    public QuestRepository() {
         this(DEFAULT_FILE);
    }

    public QuestRepository(String fileName) {
        this.fileName = fileName;
        this.mapper = new ObjectMapper();
        questSteps = getMap();
    }

    private List<QuestStep> load() {
        try (InputStream inputStream = QuestRepository.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new QuestDataException("Can't find file: " + fileName);
            }
            return mapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            throw new QuestDataException("Can't load data from file: " + fileName, e);
        }
    }

    private Map<String, QuestStep> getMap() {
        return load().stream().collect(Collectors.toUnmodifiableMap(QuestStep::getId, step -> step));
    }
}
