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
        <tr class="${meal.excess ? 'red' : 'green'}">
            <td>${meal.dateTime.format(requestScope.formatter)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="meals?action=update&id=${meal.id}">update</a>
            </td>
            <td><a href="meals?action=delete&id=${meal.id}">delete</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<br>
<a href="meals?action=create">Создать новую запись</a>
</body>
</html>
