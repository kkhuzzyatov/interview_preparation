import { useEffect, useState } from "react";
import styles from "./ReviewPage.module.css";
import {
  getNextReviewCard,
  submitAnswer,
  revealAnswer,
} from "../../api/reviewApi";
import { getAllDesks } from "../../api/deskApi";
import AnswerForm from "../../components/review/AnswerForm";

export default function ReviewPage() {
  const [desks, setDesks] = useState([]);
  const [selectedDeskIds, setSelectedDeskIds] = useState([]);

  const [card, setCard] = useState(null);
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState(null);
  const [wasRevealed, setWasRevealed] = useState(false);

  const [loading, setLoading] = useState(true);
  const [loadingDesks, setLoadingDesks] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [revealing, setRevealing] = useState(false);
  const [loadingNext, setLoadingNext] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDesks() {
      try {
        setLoadingDesks(true);

        const data = await getAllDesks();

        setDesks(data);

        // Select all desks by default.
        setSelectedDeskIds(data.map((desk) => desk.id));
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "Failed to load desks"
        );
      } finally {
        setLoadingDesks(false);
      }
    }

    loadDesks();
  }, []);

  async function fetchCard({ initial = false } = {}) {
    if (selectedDeskIds.length === 0) {
      setCard(null);
      setError("");
      setLoading(false);
      setLoadingNext(false);
      return;
    }

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

      const data = await getNextReviewCard(selectedDeskIds);

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
    if (!loadingDesks && selectedDeskIds.length > 0) {
      fetchCard({ initial: true });
    }

    if (!loadingDesks && selectedDeskIds.length === 0) {
      setCard(null);
      setLoading(false);
      setResult(null);
      setAnswer("");
      setWasRevealed(false);
      setError("");
    }
  }, [loadingDesks, selectedDeskIds]);

  function handleDeskToggle(deskId) {
    setSelectedDeskIds((current) => {
      if (current.includes(deskId)) {
        return current.filter((id) => id !== deskId);
      }

      return [...current, deskId];
    });
  }

  function handleSelectAll() {
    setSelectedDeskIds(desks.map((desk) => desk.id));
  }

  function handleClearAll() {
    setSelectedDeskIds([]);
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!card || !answer.trim()) {
      return;
    }

    try {
      setSubmitting(true);
      setError("");
      setWasRevealed(false);

      const data = await submitAnswer(card.cardId, answer);

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
  const allDesksSelected =
    desks.length > 0 && selectedDeskIds.length === desks.length;
  const noDesksSelected = selectedDeskIds.length === 0;

  if (loadingDesks) {
    return (
      <main className={styles.page}>
        <div className={styles.message}>Loading desks...</div>
      </main>
    );
  }

  return (
    <main className={styles.page}>
      <section className={styles.card}>
        <div className={styles.filter}>
          <div className={styles.filterHeader}>
            <div className={styles.filterLabel}>Review desks</div>

            <div className={styles.filterActions}>
              <button
                type="button"
                onClick={handleSelectAll}
                disabled={
                  isProcessing ||
                  loadingNext ||
                  allDesksSelected
                }
              >
                Select all
              </button>

              <button
                type="button"
                onClick={handleClearAll}
                disabled={
                  isProcessing ||
                  loadingNext ||
                  noDesksSelected
                }
              >
                Clear all
              </button>
            </div>
          </div>

          <div className={styles.deskList}>
            {desks.map((desk) => {
              const selected = selectedDeskIds.includes(desk.id);

              return (
                <label
                  key={desk.id}
                  className={`${styles.deskOption} ${
                    selected
                      ? styles.deskOptionSelected
                      : ""
                  }`}
                >
                  <input
                    type="checkbox"
                    checked={selected}
                    onChange={() => handleDeskToggle(desk.id)}
                    disabled={isProcessing || loadingNext}
                  />

                  <span
                    className={styles.checkmark}
                    aria-hidden="true"
                  >
                    {selected ? "✓" : ""}
                  </span>

                  <span className={styles.deskOptionName}>
                    {desk.name}
                  </span>
                </label>
              );
            })}
          </div>
        </div>

        {noDesksSelected ? (
          <div className={styles.deskName}>
            Select at least one desk to get a question.
          </div>
        ) : (
          <>
            {loading && (
              <div className={styles.message}>
                Loading...
              </div>
            )}

            {!loading && !card && (
              <div className={styles.error}>
                {error || "No cards available for review."}
              </div>
            )}

            {!loading && card && (
              <>
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

                        <div
                          className={styles.correctAnswerText}
                        >
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
              </>
            )}
          </>
        )}
      </section>
    </main>
  );
}