package org.quest.controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.quest.exception.QuestDataException;
import org.quest.repository.QuestRepository;
import org.quest.service.QuestService;
import org.quest.service.QuestServiceImpl;

import java.io.IOException;

@WebServlet("/quest")
public class QuestServlet extends HttpServlet {
    private static final String IS_STARTED = "isStarted";
    private QuestRepository repository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.repository = new QuestRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            HttpSession session = req.getSession();
            Boolean isStarted = (Boolean) session.getAttribute(IS_STARTED);
            if (isStarted == null || !isStarted) {
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                return;
            }
            req.setAttribute("currentStep", getQuestServiceForSession(req).getCurrentStep());
            req.getRequestDispatcher("/quest.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            log("Error inside doGet", e);
            sendErrorQuietly(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Внутрішня помилка сервера");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");
            HttpSession session = req.getSession();
            QuestService questService = getQuestServiceForSession(req);
            String action = req.getParameter("action");
            String answer = req.getParameter("answer");
            if ("start_game".equalsIgnoreCase(action)) {
                session.setAttribute(IS_STARTED, true);
                questService.reset();
            } else if ("reset".equalsIgnoreCase(action)) {
                session.setAttribute(IS_STARTED, false);
                questService.reset();
            } else if (answer != null && !answer.isBlank()) {
                questService.makeStep(answer);
            }
            resp.sendRedirect("quest");
        } catch (IllegalArgumentException e) {
            sendErrorQuietly(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (QuestDataException e) {
            sendErrorQuietly(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Помилка даних квесту: " + e.getMessage());
        } catch (Exception e) {
            log("Unexpected error in doPost", e);
            sendErrorQuietly(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Непередбачувана помилка");
        }
    }

    private QuestService getQuestServiceForSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        QuestService questService = (QuestService) session.getAttribute("questService");
        if (questService == null) {
            questService = new QuestServiceImpl(repository);
            session.setAttribute("questService", questService);
        }
        return questService;
    }

    private void sendErrorQuietly(HttpServletResponse resp, int statusCode, String message) {
        try {
            resp.sendError(statusCode, message);
        } catch (IOException e) {
            log("Failed to send error response", e);
        }
    }
}
