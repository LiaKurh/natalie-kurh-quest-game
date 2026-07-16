<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.quest.model.QuestStep" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Космічний Квест (JSP)</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #1a1a1a;
            color: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .quest-container {
            background-color: #2a2a2a;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.5);
            max-width: 500px;
            width: 100%;
            text-align: center;
        }

        h2 {
            margin-bottom: 25px;
            font-size: 22px;
        }

        .btn-container {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 12px;
            font-size: 16px;
            border-radius: 5px;
            cursor: pointer;
            transition: background 0.2s;
        }

        button:hover {
            background-color: #0056b3;
        }

        .reset-btn {
            background-color: #dc3545;
            margin-top: 20px;
            width: 100%;
        }

        .reset-btn:hover {
            background-color: #bd2130;
        }
    </style>
</head>
<body>

<div class="quest-container">
    <h2>${currentStep.text}</h2>

    <div class="btn-container">
        <%
            QuestStep currentStep = (QuestStep) request.getAttribute("currentStep");
            Map<String, String> answers = currentStep.getAnswers();
            if (answers != null && !answers.isEmpty()) {
                for (String answerText : answers.keySet()) {
        %>
        <form action="quest" method="POST">
            <input type="hidden" name="answer" value="<%= answerText %>">
            <button type="submit"><%= answerText %>
            </button>
        </form>
        <%
            }
        } else {
        %>
        <form action="quest" method="POST">
            <input type="hidden" name="action" value="reset">
            <button type="submit" class="reset-btn">Почати заново</button>
        </form>
        <%
            }
        %>
    </div>
</div>

</body>
</html>
