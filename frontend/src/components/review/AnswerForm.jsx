import styles from "./AnswerForm.module.css";

export default function AnswerForm({
  answer,
  onAnswerChange,
  onSubmit,
  onReveal,
  isProcessing,
  submitting,
  revealing,
  error,
}) {
  return (
    <form className={styles.form} onSubmit={onSubmit}>
      <label className={styles.label} htmlFor="answer">
        Your answer
      </label>

      <textarea
        id="answer"
        className={styles.textarea}
        value={answer}
        onChange={(event) => onAnswerChange(event.target.value)}
        placeholder="Your answer"
        rows={8}
        required
        disabled={isProcessing}
      />

      {error && <div className={styles.error}>{error}</div>}

      <div className={styles.actions}>
        <button
          className={styles.submitButton}
          type="submit"
          disabled={isProcessing || !answer.trim()}
        >
          {submitting ? "Submitting..." : "Submit"}
        </button>

        <button
          className={styles.revealButton}
          type="button"
          onClick={onReveal}
          disabled={isProcessing}
        >
          {revealing ? "Revealing..." : "Reveal answer"}
        </button>
      </div>
    </form>
  );
}