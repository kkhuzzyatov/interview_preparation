import { useEffect, useState } from "react";
import styles from "./SettingPage.module.css";

import {
  createSettings,
  deleteSettings,
  getSettings,
  updateSettings,
} from "../../api/settingApi";

const DEFAULT_SETTINGS = {
  answerEvaluationPrompt: "",

  evaluationRedAverageScore: 0,
  evaluationGreenMinAnswers: 0,
  evaluationGreenAverageScore: 0,

  reviewMultiplierRecencyDefaultMultiplier: 1,
  reviewMultiplierMeetChanceMin: 0,
  reviewMultiplierMeetChanceMax: 1,
  reviewMultiplierMinScore: 0,
  reviewMultiplierMaxScore: 100,
  reviewMultiplierBaseDifficultyMultiplier: 1,
  reviewMultiplierDefaultDifficultyMultiplier: 1,
};

const NUMBER_FIELDS = [
  "evaluationRedAverageScore",
  "evaluationGreenMinAnswers",
  "evaluationGreenAverageScore",
  "reviewMultiplierRecencyDefaultMultiplier",
  "reviewMultiplierMeetChanceMin",
  "reviewMultiplierMeetChanceMax",
  "reviewMultiplierMinScore",
  "reviewMultiplierMaxScore",
  "reviewMultiplierBaseDifficultyMultiplier",
  "reviewMultiplierDefaultDifficultyMultiplier",
];

function normalizeSettings(settings) {
  return {
    answerEvaluationPrompt:
      settings.answerEvaluationPrompt ?? "",

    evaluationRedAverageScore:
      settings.evaluationRedAverageScore ?? 0,

    evaluationGreenMinAnswers:
      settings.evaluationGreenMinAnswers ?? 0,

    evaluationGreenAverageScore:
      settings.evaluationGreenAverageScore ?? 0,

    reviewMultiplierRecencyDefaultMultiplier:
      settings.reviewMultiplierRecencyDefaultMultiplier ?? 1,

    reviewMultiplierMeetChanceMin:
      settings.reviewMultiplierMeetChanceMin ?? 0,

    reviewMultiplierMeetChanceMax:
      settings.reviewMultiplierMeetChanceMax ?? 1,

    reviewMultiplierMinScore:
      settings.reviewMultiplierMinScore ?? 0,

    reviewMultiplierMaxScore:
      settings.reviewMultiplierMaxScore ?? 100,

    reviewMultiplierBaseDifficultyMultiplier:
      settings.reviewMultiplierBaseDifficultyMultiplier ?? 1,

    reviewMultiplierDefaultDifficultyMultiplier:
      settings.reviewMultiplierDefaultDifficultyMultiplier ?? 1,
  };
}

function toRequest(settings) {
  return {
    answerEvaluationPrompt:
      settings.answerEvaluationPrompt,

    evaluationRedAverageScore:
      Number(settings.evaluationRedAverageScore),

    evaluationGreenMinAnswers:
      Number(settings.evaluationGreenMinAnswers),

    evaluationGreenAverageScore:
      Number(settings.evaluationGreenAverageScore),

    reviewMultiplierRecencyDefaultMultiplier:
      Number(
        settings.reviewMultiplierRecencyDefaultMultiplier
      ),

    reviewMultiplierMeetChanceMin:
      Number(settings.reviewMultiplierMeetChanceMin),

    reviewMultiplierMeetChanceMax:
      Number(settings.reviewMultiplierMeetChanceMax),

    reviewMultiplierMinScore:
      Number(settings.reviewMultiplierMinScore),

    reviewMultiplierMaxScore:
      Number(settings.reviewMultiplierMaxScore),

    reviewMultiplierBaseDifficultyMultiplier:
      Number(
        settings.reviewMultiplierBaseDifficultyMultiplier
      ),

    reviewMultiplierDefaultDifficultyMultiplier:
      Number(
        settings.reviewMultiplierDefaultDifficultyMultiplier
      ),
  };
}

export default function SettingPage() {
  const [settings, setSettings] = useState(
    DEFAULT_SETTINGS
  );

  const [exists, setExists] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    async function loadSettings() {
      try {
        setLoading(true);
        setError("");

        const data = await getSettings();

        setSettings(normalizeSettings(data));
        setExists(true);
      } catch (err) {
        if (
          err instanceof Error &&
          err.message === "Настройки не найдены"
        ) {
          setExists(false);
          setSettings(DEFAULT_SETTINGS);
        } else {
          setError(
            err instanceof Error
              ? err.message
              : "Failed to load settings"
          );
        }
      } finally {
        setLoading(false);
      }
    }

    loadSettings();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;

    setSettings((previous) => ({
      ...previous,
      [name]: NUMBER_FIELDS.includes(name)
        ? value
        : value,
    }));

    setSuccess("");
    setError("");
  }

  async function handleSave(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      const request = toRequest(settings);

      const data = exists
        ? await updateSettings(request)
        : await createSettings(request);

      setSettings(normalizeSettings(data));
      setExists(true);
      setSuccess("Settings saved successfully.");
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to save settings"
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    const confirmed = window.confirm(
      "Are you sure you want to delete application settings?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      await deleteSettings();

      setSettings(DEFAULT_SETTINGS);
      setExists(false);
      setSuccess("Settings deleted successfully.");
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to delete settings"
      );
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <main className={styles.page}>
        <div className={styles.emptyMessage}>
          Loading settings...
        </div>
      </main>
    );
  }

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <div className={styles.eyebrow}>
            SETTINGS
          </div>

          <h1>Application settings</h1>
        </div>
      </header>

      {error && (
        <div className={styles.error}>
          {error}
        </div>
      )}

      {success && (
        <div className={styles.success}>
          {success}
        </div>
      )}

      <form
        className={styles.form}
        onSubmit={handleSave}
      >
        <section className={styles.section}>
          <h2>Answer evaluation</h2>

          <div className={styles.field}>
            <label htmlFor="answerEvaluationPrompt">
              ANSWER EVALUATION PROMPT
            </label>

            <textarea
              id="answerEvaluationPrompt"
              name="answerEvaluationPrompt"
              value={settings.answerEvaluationPrompt}
              onChange={handleChange}
              rows={8}
              disabled={saving}
            />
          </div>

          <div className={styles.grid}>
            <div className={styles.field}>
              <label htmlFor="evaluationRedAverageScore">
                RED AVERAGE SCORE
              </label>

              <input
                id="evaluationRedAverageScore"
                name="evaluationRedAverageScore"
                type="number"
                step="any"
                value={
                  settings.evaluationRedAverageScore
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="evaluationGreenMinAnswers">
                GREEN MIN ANSWERS
              </label>

              <input
                id="evaluationGreenMinAnswers"
                name="evaluationGreenMinAnswers"
                type="number"
                value={
                  settings.evaluationGreenMinAnswers
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="evaluationGreenAverageScore">
                GREEN AVERAGE SCORE
              </label>

              <input
                id="evaluationGreenAverageScore"
                name="evaluationGreenAverageScore"
                type="number"
                step="any"
                value={
                  settings.evaluationGreenAverageScore
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>
          </div>
        </section>

        <section className={styles.section}>
          <h2>Review multiplier</h2>

          <div className={styles.grid}>
            <div className={styles.field}>
              <label htmlFor="reviewMultiplierRecencyDefaultMultiplier">
                RECENCY DEFAULT MULTIPLIER
              </label>

              <input
                id="reviewMultiplierRecencyDefaultMultiplier"
                name="reviewMultiplierRecencyDefaultMultiplier"
                type="number"
                step="any"
                value={
                  settings.reviewMultiplierRecencyDefaultMultiplier
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="reviewMultiplierMeetChanceMin">
                MEET CHANCE MIN
              </label>

              <input
                id="reviewMultiplierMeetChanceMin"
                name="reviewMultiplierMeetChanceMin"
                type="number"
                step="any"
                value={
                  settings.reviewMultiplierMeetChanceMin
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="reviewMultiplierMeetChanceMax">
                MEET CHANCE MAX
              </label>

              <input
                id="reviewMultiplierMeetChanceMax"
                name="reviewMultiplierMeetChanceMax"
                type="number"
                step="any"
                value={
                  settings.reviewMultiplierMeetChanceMax
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="reviewMultiplierMinScore">
                MIN SCORE
              </label>

              <input
                id="reviewMultiplierMinScore"
                name="reviewMultiplierMinScore"
                type="number"
                value={
                  settings.reviewMultiplierMinScore
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="reviewMultiplierMaxScore">
                MAX SCORE
              </label>

              <input
                id="reviewMultiplierMaxScore"
                name="reviewMultiplierMaxScore"
                type="number"
                value={
                  settings.reviewMultiplierMaxScore
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="reviewMultiplierBaseDifficultyMultiplier">
                BASE DIFFICULTY MULTIPLIER
              </label>

              <input
                id="reviewMultiplierBaseDifficultyMultiplier"
                name="reviewMultiplierBaseDifficultyMultiplier"
                type="number"
                step="any"
                value={
                  settings.reviewMultiplierBaseDifficultyMultiplier
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="reviewMultiplierDefaultDifficultyMultiplier">
                DEFAULT DIFFICULTY MULTIPLIER
              </label>

              <input
                id="reviewMultiplierDefaultDifficultyMultiplier"
                name="reviewMultiplierDefaultDifficultyMultiplier"
                type="number"
                step="any"
                value={
                  settings.reviewMultiplierDefaultDifficultyMultiplier
                }
                onChange={handleChange}
                disabled={saving}
              />
            </div>
          </div>
        </section>

        <div className={styles.actions}>
          {exists && (
            <button
              type="button"
              className={styles.deleteButton}
              onClick={handleDelete}
              disabled={saving}
            >
              DELETE
            </button>
          )}

          <button
            type="submit"
            className={styles.saveButton}
            disabled={saving}
          >
            {saving ? "SAVING..." : "SAVE"}
          </button>
        </div>
      </form>
    </main>
  );
}