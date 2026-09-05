import { useEffect, useState } from "react";
import styles from "./ReviewPage.module.css";
import {
  getNextReviewCard,
  submitAnswer,
} from "../../api/reviewApi";

export default function ReviewPage() {
  const [card, setCard] = useState(null);
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState(null);

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [loadingNext, setLoadingNext] = useState(false);
  const [error, setError] = useState("");

  async function loadCard() {
    try {
      setLoading(true);
      setError("");
      setResult(null);
      setAnswer("");

      const data = await getNextReviewCard();
      setCard(data);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to load review card"
      );
      setCard(null);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadCard();
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();

    if (!card || !answer.trim()) {
      return;
    }

    try {
      setSubmitting(true);
      setError("");

      const data = await submitAnswer(
        card.cardId,
        answer
      );

      setResult(data);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to submit answer"
      );
    } finally {
      setSubmitting(false);
    }
  }

  async function handleNextQuestion() {
    try {
      setLoadingNext(true);
      setError("");
      setResult(null);
      setAnswer("");

      const data = await getNextReviewCard();
      setCard(data);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to load next question"
      );
    } finally {
      setLoadingNext(false);
    }
  }

  if (loading) {
    return (
      <main className={styles.page}>
        <div className={styles.message}>
          Loading...
        </div>
      </main>
    );
  }

  if (!card) {
    return (
      <main className={styles.page}>
        <div className={styles.error}>
          {error || "No cards available for review."}
        </div>
      </main>
    );
  }

  return (
    <main className={styles.page}>
      <section className={styles.card}>
        <div className={styles.deskName}>
          {card.deskName}
        </div>

        <div className={styles.question}>
          {card.question}
        </div>

        {!result && (
          <form
            className={styles.form}
            onSubmit={handleSubmit}
          >
            <label
              className={styles.label}
              htmlFor="answer"
            >
              Your answer
            </label>

            <textarea
              id="answer"
              className={styles.textarea}
              value={answer}
              onChange={(event) =>
                setAnswer(event.target.value)
              }
              placeholder="Your answer"
              rows={8}
              required
            />

            {error && (
              <div className={styles.error}>
                {error}
              </div>
            )}

            <button
              className={styles.submitButton}
              type="submit"
              disabled={
                submitting || !answer.trim()
              }
            >
              {submitting ? "Submitting..." : "Submit"}
            </button>
          </form>
        )}

        {result && (
          <div className={styles.result}>
            <div className={styles.score}>
              Score: {result.score}
            </div>

            <div className={styles.userAnswer}>
              <strong>Your answer:</strong>
              <div>{answer}</div>
            </div>

            {result.feedback && (
              <div className={styles.feedback}>
                <strong>Feedback:</strong>
                <div>{result.feedback}</div>
              </div>
            )}

            {result.correctAnswer && (
              <div className={styles.correctAnswer}>
                <strong>Correct answer:</strong>
                <div>{result.correctAnswer}</div>
              </div>
            )}

            <button
              className={styles.nextButton}
              type="button"
              onClick={handleNextQuestion}
              disabled={loadingNext}
            >
              {loadingNext ? "Loading..." : "Next question"}
            </button>
          </div>
        )}

        {error && result && (
          <div className={styles.error}>
            {error}
          </div>
        )}
      </section>
    </main>
  );
}