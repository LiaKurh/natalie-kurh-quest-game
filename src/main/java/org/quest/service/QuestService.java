package org.quest.service;

import org.quest.model.QuestStep;


public interface QuestService {

    QuestStep getCurrentStep();

    void makeStep(String answer);

    void reset();
}
