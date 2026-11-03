# Toolshop E2E

[![API tests](https://github.com/dral0w/toolshop-e2e/actions/workflows/ci.yml/badge.svg)](https://github.com/dral0w/toolshop-e2e/actions/workflows/ci.yml)
[![Allure report](https://img.shields.io/badge/Allure-report-green)](https://dral0w.github.io/toolshop-e2e/)

Автотесты интернет-магазина инструментов [Practice Software Testing](https://practicesoftwaretesting.com).
UI и API здесь — одна система, поэтому состояние для проверок готовится запросами, а браузер
используется только там, где он действительно нужен.

## Стек

Java 21 · Maven · JUnit 5 · REST Assured · JSON Schema Validator · Selenide · Allure · GitHub Actions

## Что проверяется

| Группа | Проверки |
|---|---|
| **Каталог** (`ProductsApiTest`) | соответствие ответа JSON Schema, пагинация и её метаданные, сортировка по цене, фильтр по бренду, поиск, 404 на несуществующем товаре |
| **Корзина** (`CartApiTest`) | создание пустой корзины, добавление товара с количеством, изменение количества, удаление позиции |
| **Авторизация** (`AuthorizationTest`) | защищённые ресурсы без токена → 401, вход с неизвестными данными → 401, выдача Bearer-токена и чтение профиля по нему |
| **Известные дефекты** (`KnownDefectsTest`) | зафиксированное дефектное поведение приложения |
| **Каталог в браузере** (`CatalogUiTest`) | карточки с названием и ценой, сортировка по цене, выдача поиска |
| **Карточка товара** (`ProductUiTest`) | данные на странице против данных API, товар не в наличии нельзя купить |
| **Форма входа** (`LoginUiTest`) | отказ при неверных данных, переход к регистрации |
| **API + UI** (`CartAcrossApiAndUiTest`) | корзина, собранная запросами, отображается в браузере; товар, добавленный кликами, виден через API |

24 теста: 15 API, 7 UI, 2 смешанных. Полный прогон — около полутора минут.

## Главное: состояние готовится через API

Корзина фронтенда привязана к ключу `cart_id` в `sessionStorage`. Это позволяет собрать
корзину запросами и отдать её браузеру — вместо того чтобы кликать по каталогу в каждом тесте:

```java
Product product = ProductsApi.firstInStock();
String cartId = CartApi.create();
CartApi.addItem(cartId, product.id(), 3);

handCartOverToBrowser(cartId);      // sessionStorage.setItem('cart_id', ...)

new CartPage().openPage();          // браузер открывается с уже готовой корзиной
```

Проверяется и обратное направление: товар кладётся в корзину кликами, а затем корзина
читается запросом по идентификатору из `sessionStorage` — фронтенд и бэкенд должны видеть одно и то же.

UI-тесты каталога используют API как эталон: ожидаемый список сравнивается целиком через
`shouldHave(exactTexts(...))`. Это заодно решает проблему асинхронной перерисовки — Selenide
дожидается совпадения, вместо того чтобы читать устаревшую выдачу сразу после клика.

## Известные дефекты приложения

Тесты в `KnownDefectsTest` **зелёные намеренно**. Они фиксируют текущее — неправильное —
поведение, чтобы поймать момент, когда его починят, и служат готовым воспроизведением для
баг-репорта.

**`POST /products` без токена отвечает `500` вместо `401`.** Запрос с синтаксически корректным
телом и без авторизации завершается внутренней ошибкой сервера. Это значит, что тело
обрабатывается раньше, чем проверяется токен. Ожидаемое поведение — `401 Unauthorized`.

## Запуск

Нужны JDK 21+, Maven и браузер. По умолчанию Selenide поднимает Chrome в headless-режиме.

```bash
mvn test
```

Только API-тесты — быстро и без браузера:

```bash
mvn test -Dgroups=api
```

Только UI и смешанные сценарии:

```bash
mvn test -Dgroups=ui,e2e
```

Если Chrome не установлен, подойдёт любой другой браузер:

```bash
mvn test -Dselenide.browser=edge
```

Посмотреть, что делает браузер, вместо headless:

```bash
mvn test -Dselenide.headless=false
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
расписанию и по кнопке.

API- и UI-тесты разведены по разным job и идут параллельно: API-набор не ждёт браузер и даёт
обратную связь за полминуты. Результаты обоих job собираются в один отчёт, который с `main`
публикуется на GitHub Pages — с переносом истории, так что в отчёте виден тренд.

## Структура

```
src/test/java/shop/
├─ config/
│  ├─ Config.java              адреса и учётные данные: -D или переменные окружения
│  ├─ Specs.java               request/response спеки, Allure-фильтр, логирование ошибок
│  ├─ Browser.java             настройки Selenide и слушатель отчёта
│  └─ AllureEnvironment.java   окружение прогона для главной страницы отчёта
├─ model/                      DTO-записи: Product, Cart, CartItem, TokenResponse, …
├─ api/                        клиенты: ProductsApi, CartApi, AuthApi, BrandsApi
├─ ui/
│  ├─ pages/                   CatalogPage, ProductPage, CartPage, LoginPage
│  └─ components/Header.java   шапка с корзиной
└─ tests/
   ├─ BaseApiTest.java         тег api
   ├─ BaseUiTest.java          тег ui, запуск и закрытие браузера
   ├─ api/                     тесты API
   ├─ ui/                      тесты интерфейса
   └─ e2e/                     смешанные сценарии

src/test/resources/
├─ schemas/product-page.json   JSON Schema ответа каталога
├─ allure.properties           результаты из IDE идут туда же, куда из Maven
└─ simplelogger.properties     уровень логирования Selenide и Selenium
```

Тесты не ходят в HTTP напрямую и не дёргают селекторы из тела теста: между ними и системой
лежат клиенты в `api/` и страницы в `ui/`, размеченные `@Step`. Поэтому в отчёте шаги читаются
как сценарий, а смена контракта или вёрстки правится в одном месте.

Селекторы — только по `data-test`: приложение предоставляет их на всех значимых элементах,
так что цепляться за классы вёрстки не нужно.

## Дальше

- `docker-compose.yml` для локального подъёма приложения и прогона в CI без внешнего стенда
- сценарий оформления заказа целиком, с адресом и оплатой
- проверка разграничения ролей: что видит и чего не может покупатель против администратора
