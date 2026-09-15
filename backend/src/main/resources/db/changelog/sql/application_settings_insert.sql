INSERT INTO application_settings (
    id,
    answer_evaluation_prompt,

    evaluation_red_average_score,
    evaluation_green_min_answers,
    evaluation_green_average_score,

    review_multiplier_recency_default_multiplier,
    review_multiplier_meet_chance_min,
    review_multiplier_meet_chance_max,
    review_multiplier_min_score,
    review_multiplier_max_score,
    review_multiplier_base_difficulty_multiplier,
    review_multiplier_default_difficulty_multiplier
)
VALUES (
    1,
    'Строго, но справедливо оцени ответ кандидата на технический вопрос.

Оценивай только те знания, которые кандидат явно продемонстрировал в своем ответе.
Не додумывай знания кандидата и не засчитывай то, чего он не сказал.

Учитывай:
- корректность ответа;
- полноту относительно эталона;
- наличие фактических ошибок;
- понимание основных концепций.

Если кандидат пропустил второстепенную деталь, это не должно сильно снижать оценку.
Если кандидат допускает фактическую ошибку или демонстрирует неправильное понимание концепции, снижай оценку соответственно.

Вопрос: %s
Эталон: %s
Ответ кандидата: %s

Верни ТОЛЬКО валидный JSON без markdown:
{"score":0,"feedback":"Максимум 2 коротких предложения на русском."}',
    
    6.0,
    5,
    8.5,

    3.0,
    0.0,
    100.0,
    0,
    10,
    1.0,
    1.5
);