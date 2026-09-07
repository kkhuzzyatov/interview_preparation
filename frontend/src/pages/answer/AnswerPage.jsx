import { useEffect, useState } from "react";
import styles from "./AnswerPage.module.css";
import { getAllAnswers } from "../../api/answerApi";

export default function AnswerPage() {
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadAnswers() {
      try {
        setLoading(true);
        setError("");

        const data = await getAllAnswers();

        setAnswers(data);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Failed to load answers"
        );
      } finally {
        setLoading(false);
      }
    }

    loadAnswers();
  }, []);

  function formatDate(dateString) {
    return new Date(dateString).toLocaleString();
  }

  return (
    <main className={styles.page}>
      <section className={styles.header}>
        <div className={styles.eyebrow}>
          ANSWER HISTORY
        </div>

        <h1>Your answers</h1>

        <p>
          Review your previous answers and evaluation
          scores.
        </p>
      </section>

      <section className={styles.answersSection}>
        <h2>ANSWERS</h2>

        {loading && (
          <div className={styles.message}>
            Loading answers...
          </div>
        )}

        {error && (
          <div className={styles.error}>
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          answers.length === 0 && (
            <div className={styles.message}>
              No answers yet.
            </div>
          )}

        {!loading &&
          !error &&
          answers.length > 0 && (
            <div className={styles.answerList}>
              {answers.map((answer) => (
                <article
                  className={styles.answer}
                  key={answer.id}
                >
                  <div className={styles.answerHeader}>
                    <div className={styles.question}>
                      {answer.question}
                    </div>

                    <div
                      className={`${styles.score} ${
                        answer.score >= 8
                          ? styles.scoreGood
                          : answer.score >= 5
                            ? styles.scoreMedium
                            : styles.scoreBad
                      }`}
                    >
                      {answer.score}/10
                    </div>
                  </div>

                  <div className={styles.correctAnswer}>
                    <div className={styles.label}>
                      CORRECT ANSWER
                    </div>

                    <div className={styles.answerText}>
                      {answer.correctAnswer}
                    </div>
                  </div>

                  <div className={styles.footer}>
                    <span>
                      {formatDate(answer.createdAt)}
                    </span>
                  </div>
                </article>
              ))}
            </div>
          )}
      </section>
    </main>
  );
}