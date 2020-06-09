<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <style>
        <%@include file="/WEB-INF/css/style.css" %>
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<ul>
    <li><a href="users">Users</a></li>
</ul>
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
    <c:forEach items="${requestScope.mealsToList}" var="meal">
        <tr class="${meal.isExcess() ? 'red' : 'green'}">
            <td>${meal.getDateTime().format(requestScope.formatter)}</td>
            <td>${meal.getDescription()}</td>
            <td>${meal.getCalories()}</td>
            <td><a href="meals/editMeal?action=update&id=${meal.getId()}">update</a>
            </td>
            <td><a href="meals?action=delete&id=${meal.getId()}">delete</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<br>
<a href="meals/editMeal?action=create">Создать новую запись</a>
</body>
</html>
