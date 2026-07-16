<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Ласкаво просимо до квесту</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #121212;
            color: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .welcome-container {
            background-color: #1e1e1e;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.7);
            max-width: 550px;
            width: 100%;
            text-align: center;
            border: 1px solid #333;
        }

        h1 {
            color: #007bff;
            margin-bottom: 20px;
            font-size: 28px;
        }

        p {
            font-size: 16px;
            line-height: 1.6;
            color: #ccc;
            margin-bottom: 30px;
            text-align: justify;
        }

        button {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 15px 40px;
            font-size: 18px;
            font-weight: bold;
            border-radius: 6px;
            cursor: pointer;
            transition: background 0.2s, transform 0.1s;
        }

        button:hover {
            background-color: #218838;
            transform: scale(1.02);
        }
    </style>
</head>
<body>

<div class="welcome-container">
    <h1>Ласкаво просимо до текстового квесту!</h1>
    <p>
        Ви прокидаєтесь у холодній металевій капсулі. Голова неймовірно болить, а в думках — суцільна темрява.
        Ви абсолютно не пам'ятаєте, хто ви, як тут опинилися і що це за дивні прилади миготять навколо.
        Раптом тишу розриває гучний звуковий сигнал — на головному екрані з'являється невідоме сповіщення.
        Ваша історія починається прямо зараз...
    </p>

    <form action="quest" method="POST">
        <input type="hidden" name="action" value="start_game">
        <button type="submit">Розпочати гру</button>
    </form>
</div>

</body>
</html>
