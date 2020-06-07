<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="javatime" uri="http://sargue.net/jsptags/time" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <style type="text/css">
        TABLE {
            border: 5px double #000;
        }

        TD, TH {
            padding: 5px;
            border: 1px solid #000;
        }

        INPUT, LABEL {
            margin-bottom: 5px;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<ul>
    <li><a href="users">Users</a></li>
</ul>
<c:if test="${action == \"read\"}">
    <table>
        <thead>
        <tr>
            <th>Дата/Время</th>
            <th>Описание</th>
            <th>Калории</th>
            <th colspan="2">Доступные действия</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${mealsToList}" var="meal">
            <c:if test="${meal != null}">
                <tr style="color: ${meal.isExcess() ? 'red' : 'green'};">
                    <td><javatime:format pattern="dd-MM-yyyy HH:mm"
                                         value="${meal.getDateTime()}"/></td>
                    <td>${meal.getDescription()}</td>
                    <td>${meal.getCalories()}</td>
                    <td><a href="meals?action=update&id=${meal.getId()}">update</a>
                    </td>
                    <td><a href="meals?action=delete&id=${meal.getId()}">delete</a></td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
    <br>
    <a href="meals?action=create">Создать новую запись</a>
</c:if>
<c:if test="${action == 'update'}">
    <table>
        <thead>
        <tr>
            <th>Дата/Время</th>
            <th>Описание</th>
            <th>Калории</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>
            <javatime:format pattern="dd-MM-yyyy HH:mm"
                                 value="${mealToUpdate.getDateTime()}"/></td>
            <td>${mealToUpdate.getDescription()}</td>
            <td>${mealToUpdate.getCalories()}</td>
        </tr>
        </tbody>
    </table>
</c:if>
<c:if test="${action == 'create' || action == 'update'}">
    <h3>${action == 'create' ? 'Добавление еды' : 'Редактирование еды'}</h3>
    <c:set var="reference" value="meals?action=update&id=${mealToUpdate.getId()}"/>
    <div>
        <form action="${action == 'create' ? 'meals?action=create' : reference}"
              method="POST">
            <label for="date">Дата</label>
            <input type="date" name="date" id="date">
            <label for="time">Время</label>
            <input type="time" name="time" id="time">
            <br>
            <label for="description">Описание</label>
            <input type="text" name="description" id="description">
            <br>
            <label for="calories">Калории</label>
            <input type="text" name="calories" id="calories">
            <br>
            <input type="submit"
                   value="${action == 'create' ? 'Добавить в список' : 'Сохранить изменения'}">
        </form>
    </div>
</c:if>
</body>
</html>
