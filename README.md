# Toolshop E2E

[![API tests](https://github.com/dral0w/toolshop-e2e/actions/workflows/ci.yml/badge.svg)](https://github.com/dral0w/toolshop-e2e/actions/workflows/ci.yml)
[![Allure report](https://img.shields.io/badge/Allure-report-green)](https://dral0w.github.io/toolshop-e2e/)

Автотесты интернет-магазина инструментов [Practice Software Testing](https://practicesoftwaretesting.com).
UI и API здесь — одна система, поэтому состояние для проверок готовится запросами, а браузер
используется только там, где он действительно нужен.

**Сейчас в репозитории — API-слой.** UI-тесты на Selenide и смешанные сценарии — следующий этап,
см. «Дальше» в конце.

## Стек

Java 21 · Maven · JUnit 5 · REST Assured · JSON Schema Validator · Allure · GitHub Actions

## Что проверяется

| Группа | Проверки |
|---|---|
| **Каталог** (`ProductsApiTest`) | соответствие ответа JSON Schema, пагинация и её метаданные, сортировка по цене, фильтр по бренду, поиск, 404 на несуществующем товаре |
| **Корзина** (`CartApiTest`) | создание пустой корзины, добавление товара с количеством, изменение количества, удаление позиции |
| **Авторизация** (`AuthorizationTest`) | защищённые ресурсы без токена → 401, вход с неизвестными данными → 401, выдача Bearer-токена и чтение профиля по нему |
| **Известные дефекты** (`KnownDefectsTest`) | зафиксированное дефектное поведение приложения |

15 тестов. Прогон — около полуминуты.

## Известные дефекты приложения

Тесты в `KnownDefectsTest` **зелёные намеренно**. Они фиксируют текущее — неправильное —
поведение, чтобы поймать момент, когда его починят, и служат готовым воспроизведением для
баг-репорта.

**`POST /products` без токена отвечает `500` вместо `401`.** Запрос с синтаксически корректным
телом и без авторизации завершается внутренней ошибкой сервера. Это значит, что тело
обрабатывается раньше, чем проверяется токен. Ожидаемое поведение — `401 Unauthorized`.

## Запуск

Нужны JDK 21+ и Maven.

```bash
mvn test
```

Собрать Allure-отчёт:

```bash
mvn clean test allure:report
```

Посмотреть его (статический файл по `file://` не откроется — это SPA):

```bash
mvn allure:serve
```

Прогнать против другого окружения, например локально поднятого приложения:

```bash
mvn test -Dtoolshop.baseUri=http://localhost:8091
```

## Учётные данные

В репозитории их нет и быть не должно. Тесты, которым нужен вход, читают их из системных
свойств или переменных окружения, а без них **осознанно пропускаются**, а не падают:

```bash
mvn test -Dtoolshop.email=you@example.com -Dtoolshop.password=...
```

В CI те же значения приходят из GitHub Secrets — `TOOLSHOP_EMAIL` и `TOOLSHOP_PASSWORD`.
Аккаунт заводится на [practicesoftwaretesting.com](https://practicesoftwaretesting.com).

## CI

`.github/workflows/ci.yml` гоняет тесты на каждый push в `main`, на pull request, ночью по
расписанию и по кнопке. Результаты прогона сохраняются артефактом, а с `main` отчёт
публикуется на GitHub Pages с сохранением истории последних 20 прогонов.

Чтобы отчёт поехал на Pages, в настройках репозитория нужно включить Pages с источником
`gh-pages`.

## Структура

```
src/test/java/shop/
├─ config/
│  ├─ Config.java          baseUri и учётные данные: -D или переменные окружения
│  └─ Specs.java           request/response спеки, Allure-фильтр, логирование ошибок
├─ model/                  DTO-записи: Product, Cart, CartItem, TokenResponse, …
├─ api/                    клиенты: ProductsApi, CartApi, AuthApi, BrandsApi
└─ tests/
   ├─ BaseApiTest.java     окружение прогона для отчёта
   └─ api/                 сами тесты

src/test/resources/
└─ schemas/product-page.json   JSON Schema ответа каталога
```

Тесты не ходят в HTTP напрямую: между ними и сетью лежат клиенты в `api/`, размеченные
`@Step`. Поэтому в отчёте шаги читаются как сценарий, а смена контракта правится в одном месте.

## Дальше

- UI-тесты на Selenide: каталог, карточка товара, корзина, чекаут
- Смешанные сценарии: авторизация и наполнение корзины через API, проверка через браузер
- `docker-compose.yml` для локального подъёма приложения и прогона в CI без внешнего стенда
