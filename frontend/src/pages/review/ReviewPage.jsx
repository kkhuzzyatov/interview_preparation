import { useEffect, useState } from "react";
import styles from "./ReviewPage.module.css";
import {
  getNextReviewCard,
  submitAnswer,
  revealAnswer,
} from "../../api/reviewApi";
import AnswerForm from "../../components/review/AnswerForm";

export default function ReviewPage() {
  const [card, setCard] = useState(null);
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState(null);
  const [wasRevealed, setWasRevealed] = useState(false);

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [revealing, setRevealing] = useState(false);
  const [loadingNext, setLoadingNext] = useState(false);
  const [error, setError] = useState("");

  async function fetchCard({ initial = false } = {}) {
    try {
      if (initial) {
        setLoading(true);
      } else {
        setLoadingNext(true);
      }

      setError("");
      setResult(null);
      setAnswer("");
      setWasRevealed(false);

      const data = await getNextReviewCard();
      setCard(data);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to load review card"
      );

      if (initial) {
        setCard(null);
      }
    } finally {
      if (initial) {
        setLoading(false);
      } else {
        setLoadingNext(false);
      }
    }
  }

  useEffect(() => {
    fetchCard({ initial: true });
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();

    if (!card || !answer.trim()) {
      return;
    }

    try {
      setSubmitting(true);
      setError("");
      setWasRevealed(false);

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

  async function handleReveal() {
    if (!card) {
      return;
    }

    try {
      setRevealing(true);
      setError("");

      const data = await revealAnswer(card.cardId);

      setWasRevealed(true);

      setResult({
        score: 0,
        feedback: null,
        correctAnswer: data.correctAnswer,
      });
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to reveal correct answer"
      );
    } finally {
      setRevealing(false);
    }
  }

  function handleAnswerChange(value) {
    setAnswer(value);

    if (error) {
      setError("");
    }
  }

  const isProcessing = submitting || revealing;

  if (loading) {
    return (
      <main className={styles.page}>
        <div className={styles.message}>Loading...</div>
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
          <AnswerForm
            answer={answer}
            onAnswerChange={handleAnswerChange}
            onSubmit={handleSubmit}
            onReveal={handleReveal}
            isProcessing={isProcessing}
            submitting={submitting}
            revealing={revealing}
            error={error}
          />
        )}

        {result && (
          <div className={styles.result}>
            {!wasRevealed && (
              <>
                {result.score !== undefined && (
                  <div className={styles.score}>
                    Score: {result.score}
                  </div>
                )}

                {answer && (
                  <div className={styles.userAnswer}>
                    <strong>Your answer:</strong>
                    <div>{answer}</div>
                  </div>
                )}

                {result.feedback && (
                  <div className={styles.feedback}>
                    <strong>Feedback:</strong>
                    <div>{result.feedback}</div>
                  </div>
                )}
              </>
            )}

            {result.correctAnswer && (
              <div className={styles.correctAnswer}>
                <strong>Correct answer:</strong>

                <div className={styles.correctAnswerText}>
                  {result.correctAnswer}
                </div>
              </div>
            )}

            <button
              className={styles.nextButton}
              type="button"
              onClick={() => fetchCard()}
              disabled={loadingNext}
            >
              {loadingNext
                ? "Loading..."
                : "Next question"}
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