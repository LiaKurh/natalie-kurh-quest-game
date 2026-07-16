package org.quest.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quest.exception.QuestDataException;
import org.quest.model.QuestStep;
import org.quest.repository.QuestRepository;
import org.quest.service.QuestService;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestServletTest {
    private QuestServlet servlet;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private ServletContext servletContext;
    @Mock
    private QuestRepository questRepository;
    @Mock
    private QuestService questService;
    @Mock
    private HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;


    @BeforeEach
    void setUp() throws Exception {
        servlet = new QuestServlet();
        servlet.init(servletConfig);
        Field repositoryField = QuestServlet.class.getDeclaredField("repository");
        repositoryField.setAccessible(true);
        repositoryField.set(servlet, questRepository);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    @DisplayName("doGet() should forward to index.jsp when game is not started")
    void should_ForwardToIndexJsp_When_GameIsNotStarted() throws Exception {
        when(session.getAttribute("isStarted")).thenReturn(null);
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verifyNoInteractions(questService);
    }

    @Test
    @DisplayName("doGet() should forward to quest.jsp with current step when game is started")
    void should_ForwardToQuestJspWithAttribute_When_GameIsStarted() throws Exception {
        QuestStep expectedStep = new QuestStep();
        expectedStep.setId("start");

        when(session.getAttribute("isStarted")).thenReturn(true);
        when(session.getAttribute("questService")).thenReturn(questService);
        when(questService.getCurrentStep()).thenReturn(expectedStep);
        when(request.getRequestDispatcher("/quest.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);
        verify(request).setAttribute("currentStep", expectedStep);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("doGet() should send 500 Internal Error when ServletException occurs during forward")
    void should_SendInternalServerError_When_ServletExceptionOccursDuringForward() throws Exception {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(session.getAttribute("isStarted")).thenReturn(null);
        when(request.getRequestDispatcher("/index.jsp")).thenReturn(requestDispatcher);
        doThrow(new ServletException("Forward failed simulation"))
                .when(requestDispatcher).forward(request, response);

        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Внутрішня помилка сервера");
    }


    @Test
    @DisplayName("doPost() should start game and reset service when action is start_game")
    void should_StartGameAndResetService_When_ActionIsStartGame() throws Exception {
        when(session.getAttribute("questService")).thenReturn(questService);
        when(request.getParameter("action")).thenReturn("start_game");

        servlet.doPost(request, response);

        verify(request).setCharacterEncoding("UTF-8");
        verify(session).setAttribute("isStarted", true);
        verify(questService).reset();
        verify(response).sendRedirect("quest");
    }

    @Test
    @DisplayName("doPost() should stop game and reset service when action is reset")
    void should_StopGameAndResetService_When_ActionIsReset() throws Exception {
        when(session.getAttribute("questService")).thenReturn(questService);
        when(request.getParameter("action")).thenReturn("reset");

        servlet.doPost(request, response);

        verify(session).setAttribute("isStarted", false);
        verify(questService).reset();
        verify(response).sendRedirect("quest");
    }

    @Test
    @DisplayName("doPost() should make step when valid answer parameter is provided")
    void should_MakeStep_When_AnswerIsValid() throws Exception {
        when(session.getAttribute("questService")).thenReturn(questService);
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("answer")).thenReturn("Yes");

        servlet.doPost(request, response);

        verify(questService).makeStep("Yes");
        verify(response).sendRedirect("quest");
    }

    @Test
    @DisplayName("doPost() should send 400 Bad Request when service throws IllegalArgumentException")
    void should_SendBadRequest_When_ServiceThrowsIllegalArgumentException() throws Exception {
        when(session.getAttribute("questService")).thenReturn(questService);
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("answer")).thenReturn("invalid_answer");
        doThrow(new IllegalArgumentException("Invalid answer")).when(questService).makeStep("invalid_answer");

        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid answer");
    }

    @Test
    @DisplayName("doPost() should send 500 Internal Error when service throws QuestDataException")
    void should_SendInternalError_When_ServiceThrowsQuestDataException() throws Exception {
        when(session.getAttribute("questService")).thenReturn(questService);
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("answer")).thenReturn("Yes");
        doThrow(new QuestDataException("File error")).when(questService).makeStep("Yes");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Помилка даних квесту: File error");
    }

    @Test
    @DisplayName("doPost() should send 500 Internal Error when unexpected Exception occurs")
    void should_SendInternalServerError_When_UnexpectedExceptionOccursInDoPost() throws Exception {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(session.getAttribute("questService")).thenReturn(questService);
        when(request.getParameter("action")).thenThrow(new RuntimeException("Unexpected DB or system failure"));

        servlet.doPost(request, response);
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Непередбачувана помилка");
    }
}
