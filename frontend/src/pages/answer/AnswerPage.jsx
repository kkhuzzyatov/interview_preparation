import { useEffect, useMemo, useState } from "react";
import styles from "./AnswerPage.module.css";
import { getAllAnswers } from "../../api/answerApi";

const QUESTION_PREVIEW_LENGTH = 120;

export default function AnswerPage() {
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedDesk, setSelectedDesk] = useState("ALL");
  const [expandedAnswers, setExpandedAnswers] = useState(
    new Set()
  );

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

  const desks = useMemo(() => {
    const uniqueDesks = new Map();

    answers.forEach((answer) => {
      if (answer.deskId && answer.deskName) {
        uniqueDesks.set(
          answer.deskId,
          answer.deskName
        );
      }
    });

    return Array.from(uniqueDesks.entries()).map(
      ([id, name]) => ({
        id,
        name,
      })
    );
  }, [answers]);

  const filteredAnswers = useMemo(() => {
    if (selectedDesk === "ALL") {
      return answers;
    }

    return answers.filter(
      (answer) => answer.deskId === selectedDesk
    );
  }, [answers, selectedDesk]);

  function formatDate(dateString) {
    if (!dateString) {
      return "—";
    }

    return new Date(dateString).toLocaleString();
  }

  function formatDuration(durationMs) {
    if (durationMs == null) {
      return "—";
    }

    if (durationMs < 1000) {
      return `${durationMs} ms`;
    }

    return `${(durationMs / 1000).toFixed(2)} s`;
  }

  function getScoreClass(score) {
    if (score >= 8) {
      return styles.scoreGood;
    }

    if (score >= 5) {
      return styles.scoreMedium;
    }

    return styles.scoreBad;
  }

  function getQuestionPreview(question) {
    if (!question) {
      return "—";
    }

    if (question.length <= QUESTION_PREVIEW_LENGTH) {
      return question;
    }

    const preview = question.slice(
      0,
      QUESTION_PREVIEW_LENGTH
    );

    const lastSpace = preview.lastIndexOf(" ");

    if (lastSpace > 0) {
      return `${preview.slice(0, lastSpace)}...`;
    }

    return `${preview}...`;
  }

  function isExpanded(answerId) {
    return expandedAnswers.has(answerId);
  }

  function toggleAnswer(answerId) {
    setExpandedAnswers((current) => {
      const next = new Set(current);

      if (next.has(answerId)) {
        next.delete(answerId);
      } else {
        next.add(answerId);
      }

      return next;
    });
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
        <div className={styles.sectionHeader}>
          <h2>ANSWERS</h2>

          {!loading && !error && answers.length > 0 && (
            <div className={styles.count}>
              {filteredAnswers.length}
              {filteredAnswers.length !== answers.length &&
                ` / ${answers.length}`}
            </div>
          )}
        </div>

        {!loading && !error && answers.length > 0 && (
          <div className={styles.filters}>
            <button
              type="button"
              className={`${styles.filterButton} ${
                selectedDesk === "ALL"
                  ? styles.filterButtonActive
                  : ""
              }`}
              onClick={() => setSelectedDesk("ALL")}
            >
              All
            </button>

            {desks.map((desk) => (
              <button
                type="button"
                key={desk.id}
                className={`${styles.filterButton} ${
                  selectedDesk === desk.id
                    ? styles.filterButtonActive
                    : ""
                }`}
                onClick={() => setSelectedDesk(desk.id)}
              >
                {desk.name}
              </button>
            ))}
          </div>
        )}

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
          answers.length > 0 &&
          filteredAnswers.length === 0 && (
            <div className={styles.message}>
              No answers for this desk.
            </div>
          )}

        {!loading &&
          !error &&
          filteredAnswers.length > 0 && (
            <div className={styles.answerList}>
              {filteredAnswers.map((answer) => {
                const expanded = isExpanded(answer.id);

                return (
                  <article
                    className={`${styles.answer} ${
                      expanded ? styles.answerExpanded : ""
                    }`}
                    key={answer.id}
                  >
                    <button
                      type="button"
                      className={styles.answerToggle}
                      onClick={() =>
                        toggleAnswer(answer.id)
                      }
                      aria-expanded={expanded}
                    >
                      <div className={styles.answerHeader}>
                        <div className={styles.questionBlock}>
                          <div className={styles.question}>
                            {expanded
                              ? answer.question
                              : getQuestionPreview(
                                  answer.question
                                )}
                          </div>

                          <div className={styles.collapsedMeta}>
                            {answer.deskName && (
                              <span
                                className={
                                  styles.deskBadge
                                }
                              >
                                {answer.deskName}
                              </span>
                            )}

                            <span>
                              Submitted:{" "}
                              {formatDate(
                                answer.submissionTime
                              )}
                            </span>
                          </div>
                        </div>

                        <div
                          className={`${styles.score} ${getScoreClass(
                            answer.score
                          )}`}
                        >
                          {answer.score}/10
                        </div>
                      </div>

                      <span
                        className={`${styles.expandIcon} ${
                          expanded
                            ? styles.expandIconExpanded
                            : ""
                        }`}
                        aria-hidden="true"
                      >
                        +
                      </span>
                    </button>

                    {expanded && (
                      <div className={styles.answerContent}>
                        {answer.userAnswer && (
                          <div className={styles.userAnswer}>
                            <div className={styles.label}>
                              YOUR ANSWER
                            </div>

                            <div
                              className={
                                styles.answerText
                              }
                            >
                              {answer.userAnswer}
                            </div>
                          </div>
                        )}

                        {answer.aiFeedback && (
                          <div className={styles.feedback}>
                            <div className={styles.label}>
                              AI FEEDBACK
                            </div>

                            <div
                              className={
                                styles.answerText
                              }
                            >
                              {answer.aiFeedback}
                            </div>
                          </div>
                        )}

                        <div
                          className={
                            styles.correctAnswer
                          }
                        >
                          <div className={styles.label}>
                            CORRECT ANSWER
                          </div>

                          <div
                            className={styles.answerText}
                          >
                            {answer.correctAnswer}
                          </div>
                        </div>

                        <div className={styles.metadata}>
                          <div>
                            <span>Started:</span>{" "}
                            {formatDate(
                              answer.startAnswerTime
                            )}
                          </div>

                          <div>
                            <span>Submitted:</span>{" "}
                            {formatDate(
                              answer.submissionTime
                            )}
                          </div>

                          <div>
                            <span>AI processing:</span>{" "}
                            {formatDuration(
                              answer.aiProcessingDurationMs
                            )}
                          </div>
                        </div>

                        <div className={styles.footer}>
                          <span>
                            {formatDate(answer.createdAt)}
                          </span>
                        </div>
                      </div>
                    )}
                  </article>
                );
              })}
            </div>
          )}
      </section>
    </main>
  );
}