<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Edit Meal</title>
    <style>
        <%@include file="/WEB-INF/css/style.css" %>
    </style>
</head>
<body>
<c:if test="${requestScope.action == 'update'}">
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
            <td>${requestScope.mealToUpdate.getDateTime().format(requestScope.formatter)}</td>
            <td>${requestScope.mealToUpdate.getDescription()}</td>
            <td>${requestScope.mealToUpdate.getCalories()}</td>
        </tr>
        </tbody>
    </table>
</c:if>
<h3>${requestScope.action == 'create' ? 'Добавление еды' : 'Редактирование еды'}</h3>
<c:set var="reference" value="editMeal?action=update&id=${requestScope.mealToUpdate.getId()}"/>
<div>
    <form action="${requestScope.action == 'create' ? 'editMeal?action=create' : reference}"
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
               value="${requestScope.action == 'create' ? 'Добавить в список' : 'Сохранить изменения'}">
    </form>
</div>
</body>
</html>
