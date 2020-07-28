# curl-запросы для тестирования MealRestController

### Запрос всей еды:
curl http://localhost:8080/topjava/rest/meals

### Запрос еды по id=100003:
curl http://localhost:8080/topjava/rest/meals/100003

### Запрос еды с фильтрацией:
curl "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&startTime=00:00&endDate=2020-01-30&endTime=23:59"

### Удаление еды с id=100005:
curl -X DELETE http://localhost:8080/topjava/rest/meals/100005

### Обновление еды с id=100004:
curl -X PUT -H "Content-Type: application/json" -d '{"description":"UPDATED","calories":2000,"id":100004,"dateTime":"2020-01-30T22:00:00"}' http://localhost:8080/topjava/rest/meals/100004

### Создание новой еды:
curl -X POST -H "Content-Type: application/json" -d '{"description":"NEW MEAL","calories":1500,"dateTime":"2020-02-02T12:00:00"}' http://localhost:8080/topjava/rest/meals

