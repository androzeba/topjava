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
<h3>${requestScope.action == 'create' ? 'Добавление еды' : 'Редактирование еды'}</h3>
<c:set var="reference" value="../meals?action=update&id=${requestScope.mealToUpdate.getId()}"/>
<div>
    <form action="${requestScope.action == 'create' ? '../meals?action=create' : reference}"
          method="POST">
        <label for="date">Дата</label>
        <input type="date" name="date" id="date"
               value="${requestScope.action == 'create' ? '' : requestScope.mealToUpdate.getDate()}">
        <label for="time">Время</label>
        <input type="time" name="time" id="time"
               value="${requestScope.action == 'create' ? '' : requestScope.mealToUpdate.getTime()}">
        <br>
        <label for="description">Описание</label>
        <input type="text" name="description" id="description"
               value="${requestScope.action == 'create' ? '' : requestScope.mealToUpdate.getDescription()}">
        <br>
        <label for="calories">Калории</label>
        <input type="number" name="calories" id="calories"
               value="${requestScope.action == 'create' ? '' : requestScope.mealToUpdate.getCalories()}">
        <br>
        <input type="submit"
               value="${requestScope.action == 'create' ? 'Добавить в список' : 'Сохранить изменения'}">
    </form>
</div>
</body>
</html>
