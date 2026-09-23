import { useEffect, useState } from "react";
import styles from "./SettingPage.module.css";

import {
  getSettings,
  updateSettings,
} from "../../api/settingApi";

const DEFAULT_SETTINGS = {
  answerEvaluationPrompt: "",
  newCardRecencyMultiplier: 1.5,
  newCardDifficultyMultiplier: 1.5,
  newCardColor: "#2563eb",

  difficultyMultipliers: [],
  meetChanceMultipliers: [],
  recencyMultipliers: [],
  scoreColor: [],
};

function createDifficultyMultiplier() {
  return {
    lastAnswerScoreBorder: 0,
    multiplier: 1,
  };
}

function createMeetChanceMultiplier() {
  return {
    meetChanceBorder: 0,
    multiplier: 1,
  };
}

function createRecencyMultiplier() {
  return {
    secondsBorder: 0,
    multiplier: 1,
  };
}

function createScoreColor() {
  return {
    score: 0,
    colorHex: "#000000",
  };
}

function normalizeSettings(settings) {
  return {
    answerEvaluationPrompt:
      settings.answerEvaluationPrompt ?? "",

    newCardRecencyMultiplier:
      settings.newCardRecencyMultiplier ?? 1.5,

    newCardDifficultyMultiplier:
      settings.newCardDifficultyMultiplier ?? 1.5,

    newCardColor:
      settings.newCardColor ?? "#2563eb",

    difficultyMultipliers:
      settings.difficultyMultipliers ?? [],

    meetChanceMultipliers:
      settings.meetChanceMultipliers ?? [],

    recencyMultipliers:
      settings.recencyMultipliers ?? [],

    scoreColor:
      settings.scoreColor ?? [],
  };
}

function toRequest(settings) {
  return {
    answerEvaluationPrompt: settings.answerEvaluationPrompt,

    newCardRecencyMultiplier:
      Number(settings.newCardRecencyMultiplier),

    newCardDifficultyMultiplier:
      Number(settings.newCardDifficultyMultiplier),

    newCardColor: settings.newCardColor,

    difficultyMultipliers:
      settings.difficultyMultipliers.map((item) => ({
        lastAnswerScoreBorder: Number(item.lastAnswerScoreBorder),
        multiplier: Number(item.multiplier),
      })),

    meetChanceMultipliers:
      settings.meetChanceMultipliers.map((item) => ({
        meetChanceBorder: Number(item.meetChanceBorder),
        multiplier: Number(item.multiplier),
      })),

    recencyMultipliers:
      settings.recencyMultipliers.map((item) => ({
        secondsBorder: Number(item.secondsBorder),
        multiplier: Number(item.multiplier),
      })),

    scoreColor:
      settings.scoreColor.map((item) => ({
        score: Number(item.score),
        colorHex: item.colorHex,
      })),
  };
}

export default function SettingPage() {
  const [settings, setSettings] = useState(DEFAULT_SETTINGS);

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
      } catch (err) {
        if (
          err instanceof Error &&
          err.message === "Настройки не найдены"
        ) {
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

  function handlePromptChange(event) {
    setSettings((previous) => ({
      ...previous,
      answerEvaluationPrompt: event.target.value,
    }));

    setSuccess("");
    setError("");
  }

  function handleNewCardRecencyMultiplierChange(event) {
    setSettings((previous) => ({
      ...previous,
      newCardRecencyMultiplier: event.target.value,
    }));

    setSuccess("");
    setError("");
  }

  function handleNewCardDifficultyMultiplierChange(event) {
    setSettings((previous) => ({
      ...previous,
      newCardDifficultyMultiplier: event.target.value,
    }));

    setSuccess("");
    setError("");
  }

  function handleNewCardColorChange(event) {
    setSettings((previous) => ({
      ...previous,
      newCardColor: event.target.value,
    }));

    setSuccess("");
    setError("");
  }

  function updateListItem(listName, index, field, value) {
    setSettings((previous) => {
      const list = [...previous[listName]];

      list[index] = {
        ...list[index],
        [field]: value,
      };

      return {
        ...previous,
        [listName]: list,
      };
    });

    setSuccess("");
    setError("");
  }

  function addListItem(listName, factory) {
    setSettings((previous) => ({
      ...previous,
      [listName]: [
        ...previous[listName],
        factory(),
      ],
    }));

    setSuccess("");
    setError("");
  }

  function removeListItem(listName, index) {
    setSettings((previous) => ({
      ...previous,
      [listName]: previous[listName].filter(
        (_, itemIndex) => itemIndex !== index
      ),
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
      const data = await updateSettings(request);

      setSettings(normalizeSettings(data));
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
              value={settings.answerEvaluationPrompt}
              onChange={handlePromptChange}
              rows={8}
              disabled={saving}
            />
          </div>
        </section>

        <section className={styles.section}>
          <h2>New cards</h2>

          <p>
            Settings used when selecting new cards for review.
          </p>

          <div className={styles.row}>
            <div className={styles.field}>
              <label htmlFor="newCardRecencyMultiplier">
                RECENCY MULTIPLIER
              </label>

              <input
                id="newCardRecencyMultiplier"
                type="number"
                step="any"
                value={settings.newCardRecencyMultiplier}
                onChange={handleNewCardRecencyMultiplierChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="newCardDifficultyMultiplier">
                DIFFICULTY MULTIPLIER
              </label>

              <input
                id="newCardDifficultyMultiplier"
                type="number"
                step="any"
                value={settings.newCardDifficultyMultiplier}
                onChange={handleNewCardDifficultyMultiplierChange}
                disabled={saving}
              />
            </div>

            <div className={styles.field}>
              <label htmlFor="newCardColor">
                COLOR
              </label>

              <div className={styles.colorInput}>
                <input
                  id="newCardColor"
                  type="color"
                  value={settings.newCardColor}
                  onChange={handleNewCardColorChange}
                  disabled={saving}
                />

                <input
                  type="text"
                  value={settings.newCardColor}
                  onChange={handleNewCardColorChange}
                  disabled={saving}
                />
              </div>
            </div>
          </div>
        </section>

        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <div>
              <h2>Difficulty multipliers</h2>
              <p>
                Multiplier based on the last answer score.
              </p>
            </div>

            <button
              type="button"
              className={styles.addButton}
              onClick={() =>
                addListItem(
                  "difficultyMultipliers",
                  createDifficultyMultiplier
                )
              }
              disabled={saving}
            >
              ADD
            </button>
          </div>

          <div className={styles.list}>
            {settings.difficultyMultipliers.map(
              (item, index) => (
                <div
                  className={styles.row}
                  key={
                    item.difficultyMultiplierId ??
                    `difficulty-${index}`
                  }
                >
                  <div className={styles.field}>
                    <label>LAST ANSWER SCORE</label>

                    <input
                      type="number"
                      value={item.lastAnswerScoreBorder}
                      onChange={(event) =>
                        updateListItem(
                          "difficultyMultipliers",
                          index,
                          "lastAnswerScoreBorder",
                          event.target.value
                        )
                      }
                      disabled={saving}
                    />
                  </div>

                  <div className={styles.field}>
                    <label>MULTIPLIER</label>

                    <input
                      type="number"
                      step="any"
                      value={item.multiplier}
                      onChange={(event) =>
                        updateListItem(
                          "difficultyMultipliers",
                          index,
                          "multiplier",
                          event.target.value
                        )
                      }
                      disabled={saving}
                    />
                  </div>

                  <button
                    type="button"
                    className={styles.removeButton}
                    onClick={() =>
                      removeListItem(
                        "difficultyMultipliers",
                        index
                      )
                    }
                    disabled={saving}
                  >
                    REMOVE
                  </button>
                </div>
              )
            )}

            {settings.difficultyMultipliers.length === 0 && (
              <div className={styles.emptyList}>
                No difficulty multipliers configured.
              </div>
            )}
          </div>
        </section>

        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <div>
              <h2>Meet chance multipliers</h2>
              <p>
                Multiplier based on meet chance.
              </p>
            </div>

            <button
              type="button"
              className={styles.addButton}
              onClick={() =>
                addListItem(
                  "meetChanceMultipliers",
                  createMeetChanceMultiplier
                )
              }
              disabled={saving}
            >
              ADD
            </button>
          </div>

          <div className={styles.list}>
            {settings.meetChanceMultipliers.map(
              (item, index) => (
                <div
                  className={styles.row}
                  key={
                    item.meetChanceMultiplierId ??
                    `meet-chance-${index}`
                  }
                >
                  <div className={styles.field}>
                    <label>MEET CHANCE BORDER</label>

                    <input
                      type="number"
                      step="any"
                      value={item.meetChanceBorder}
                      onChange={(event) =>
                        updateListItem(
                          "meetChanceMultipliers",
                          index,
                          "meetChanceBorder",
                          event.target.value
                        )
                      }
                      disabled={saving}
                    />
                  </div>

                  <div className={styles.field}>
                    <label>MULTIPLIER</label>

                    <input
                      type="number"
                      step="any"
                      value={item.multiplier}
                      onChange={(event) =>
                        updateListItem(
                          "meetChanceMultipliers",
                          index,
                          "multiplier",
                          event.target.value
                        )
                      }
                      disabled={saving}
                    />
                  </div>

                  <button
                    type="button"
                    className={styles.removeButton}
                    onClick={() =>
                      removeListItem(
                        "meetChanceMultipliers",
                        index
                      )
                    }
                    disabled={saving}
                  >
                    REMOVE
                  </button>
                </div>
              )
            )}

            {settings.meetChanceMultipliers.length === 0 && (
              <div className={styles.emptyList}>
                No meet chance multipliers configured.
              </div>
            )}
          </div>
        </section>

        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <div>
              <h2>Recency multipliers</h2>
              <p>
                Multiplier based on time since the last answer.
              </p>
            </div>

            <button
              type="button"
              className={styles.addButton}
              onClick={() =>
                addListItem(
                  "recencyMultipliers",
                  createRecencyMultiplier
                )
              }
              disabled={saving}
            >
              ADD
            </button>
          </div>

          <div className={styles.list}>
            {settings.recencyMultipliers.map(
              (item, index) => (
                <div
                  className={styles.row}
                  key={
                    item.recencyMultiplierId ??
                    `recency-${index}`
                  }
                >
                  <div className={styles.field}>
                    <label>SECONDS BORDER</label>

                    <input
                      type="number"
                      value={item.secondsBorder}
                      onChange={(event) =>
                        updateListItem(
                          "recencyMultipliers",
                          index,
                          "secondsBorder",
                          event.target.value
                        )
                      }
                      disabled={saving}
                    />
                  </div>

                  <div className={styles.field}>
                    <label>MULTIPLIER</label>

                    <input
                      type="number"
                      step="any"
                      value={item.multiplier}
                      onChange={(event) =>
                        updateListItem(
                          "recencyMultipliers",
                          index,
                          "multiplier",
                          event.target.value
                        )
                      }
                      disabled={saving}
                    />
                  </div>

                  <button
                    type="button"
                    className={styles.removeButton}
                    onClick={() =>
                      removeListItem(
                        "recencyMultipliers",
                        index
                      )
                    }
                    disabled={saving}
                  >
                    REMOVE
                  </button>
                </div>
              )
            )}

            {settings.recencyMultipliers.length === 0 && (
              <div className={styles.emptyList}>
                No recency multipliers configured.
              </div>
            )}
          </div>
        </section>

        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <div>
              <h2>Score colors</h2>
              <p>
                Color assigned to each score threshold.
              </p>
            </div>

            <button
              type="button"
              className={styles.addButton}
              onClick={() =>
                addListItem(
                  "scoreColor",
                  createScoreColor
                )
              }
              disabled={saving}
            >
              ADD
            </button>
          </div>

          <div className={styles.list}>
            {settings.scoreColor.map(
              (item, index) => (
                <div
                  className={styles.row}
                  key={
                    item.scoreColorsId ??
                    `score-color-${index}`
                  }
                >
                  <div className={styles.field}>
                    <label>SCORE</label>

                    <input
                      type="number"
                      value={item.score}
                      onChange={(event) =>
                        updateListItem(
                          "scoreColor",
                          index,
                          "score",
                          event.target.value
                        )
                      }
                      disabled={saving}
                    />
                  </div>

                  <div className={styles.field}>
                    <label>COLOR</label>

                    <div className={styles.colorInput}>
                      <input
                        type="color"
                        value={item.colorHex}
                        onChange={(event) =>
                          updateListItem(
                            "scoreColor",
                            index,
                            "colorHex",
                            event.target.value
                          )
                        }
                        disabled={saving}
                      />

                      <input
                        type="text"
                        value={item.colorHex}
                        onChange={(event) =>
                          updateListItem(
                            "scoreColor",
                            index,
                            "colorHex",
                            event.target.value
                          )
                        }
                        disabled={saving}
                      />
                    </div>
                  </div>

                  <button
                    type="button"
                    className={styles.removeButton}
                    onClick={() =>
                      removeListItem(
                        "scoreColor",
                        index
                      )
                    }
                    disabled={saving}
                  >
                    REMOVE
                  </button>
                </div>
              )
            )}

            {settings.scoreColor.length === 0 && (
              <div className={styles.emptyList}>
                No score colors configured.
              </div>
            )}
          </div>
        </section>

        <div className={styles.actions}>
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