# ДЗ по курсу Highload Architect

### Запуск приложения:
#### Docker:
Для запуска приложения используя docker необходимо выполнить:
```shell
docker-compose up
```

#### Локально:
Для сборки jar локально и запуска приложения необходимы следующие зависимости:

- JDK 11
- Apache Maven 3.1.1+

```shell
mvn clean install

java -jar target/social-network-0.0.1-SNAPSHOT.jar
```

### Тестирование API:
Для ручного тестирования API необходимо импортировать social-network-morozov.postman_collection.json в Postman 
