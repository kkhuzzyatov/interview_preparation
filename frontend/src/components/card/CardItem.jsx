import { useState } from "react";
import { updateCard } from "../../api/cardApi";
import styles from "./CardItem.module.css";

export default function CardItem({
  card,
  isExpanded,
  onToggle,
  onUpdated,
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [question, setQuestion] = useState(card.question);
  const [answer, setAnswer] = useState(card.answer);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  function handleEdit() {
    setQuestion(card.question);
    setAnswer(card.answer);
    setError("");
    setIsEditing(true);

    if (!isExpanded) {
      onToggle();
    }
  }

  function handleCancel() {
    setQuestion(card.question);
    setAnswer(card.answer);
    setError("");
    setIsEditing(false);
  }

  async function handleSave() {
    try {
      setSaving(true);
      setError("");

      const updatedCard = await updateCard(card.id, {
        question,
        answer,
      });

      onUpdated(updatedCard);
      setIsEditing(false);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to update card"
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <article className={styles.card}>
      <div className={styles.cardHeader}>
        <div className={styles.question}>
          {card.question}
        </div>

        <div className={styles.actions}>
          <button
            type="button"
            className={styles.updateButton}
            onClick={handleEdit}
            disabled={saving}
          >
            UPDATE
          </button>

          <button
            type="button"
            className={`${styles.toggleButton} ${
              isExpanded ? styles.expanded : ""
            }`}
            onClick={onToggle}
            aria-expanded={isExpanded}
            aria-label={
              isExpanded ? "Hide answer" : "Show answer"
            }
          >
            <span>+</span>
          </button>
        </div>
      </div>

      {isExpanded && (
        <div className={styles.answer}>
          {isEditing ? (
            <div className={styles.editForm}>
              <label className={styles.editLabel}>
                QUESTION
              </label>

              <textarea
                className={styles.editInput}
                value={question}
                onChange={(event) =>
                  setQuestion(event.target.value)
                }
                rows={3}
                disabled={saving}
              />

              <label className={styles.editLabel}>
                ANSWER
              </label>

              <textarea
                className={styles.editInput}
                value={answer}
                onChange={(event) =>
                  setAnswer(event.target.value)
                }
                rows={6}
                disabled={saving}
              />

              {error && (
                <div className={styles.editError}>
                  {error}
                </div>
              )}

              <div className={styles.editActions}>
                <button
                  type="button"
                  className={styles.cancelButton}
                  onClick={handleCancel}
                  disabled={saving}
                >
                  CANCEL
                </button>

                <button
                  type="button"
                  className={styles.saveButton}
                  onClick={handleSave}
                  disabled={saving}
                >
                  {saving ? "SAVING..." : "SAVE"}
                </button>
              </div>
            </div>
          ) : (
            <>
              <div className={styles.answerText}>
                {card.answer}
              </div>

              <div className={styles.cardId}>
                <strong>Card ID:</strong>{" "}
                {card.id}
              </div>
            </>
          )}
        </div>
      )}
    </article>
  );
}