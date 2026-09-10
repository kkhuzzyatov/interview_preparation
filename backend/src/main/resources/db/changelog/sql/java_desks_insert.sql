INSERT INTO desks (desk_id, name, topic_id)
SELECT
    gen_random_uuid(),
    desk.name,
    topic.topic_id
FROM (
         VALUES
             ('ООП'),
             ('SOLID'),
             ('Java Core'),
             ('Коллекции'),
             ('Исключения'),
             ('Дженерики'),
             ('Функциональные интерфейсы'),
             ('Stream API'),
             ('Многопоточность'),
             ('Базы данных'),
             ('SQL'),
             ('NoSQL'),
             ('Миграции'),
             ('ORM'),
             ('Spring'),
             ('Паттерны проектирования'),
             ('WEB'),
             ('Микросервисы'),
             ('Брокеры сообщений'),
             ('Тестирование'),
             ('Деплой'),
             ('Другое')
     ) AS desk(name)
         CROSS JOIN topics topic
WHERE topic.name = 'Java backend';