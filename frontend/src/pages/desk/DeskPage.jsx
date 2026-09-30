import { useEffect, useMemo, useState } from "react";
import styles from "./DeskPage.module.css";

import {
  createDesk,
  deleteDesk,
  getAllDesks,
  updateDesk,
} from "../../api/deskApi";

import { getAllTopics } from "../../api/topicApi";

export default function DeskPage() {
  const [desks, setDesks] = useState([]);
  const [topics, setTopics] = useState([]);

  const [newDesk, setNewDesk] = useState({
    name: "",
    topicId: "",
  });

  const [drafts, setDrafts] = useState({});

  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [savingDeskId, setSavingDeskId] =
    useState(null);
  const [deletingDeskId, setDeletingDeskId] =
    useState(null);

  const [error, setError] = useState("");

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        setError("");

        const [desksData, topicsData] =
          await Promise.all([
            getAllDesks(),
            getAllTopics(),
          ]);

        setDesks(desksData);
        setTopics(topicsData);

        setDrafts(
          Object.fromEntries(
            desksData.map((desk) => [
              desk.id,
              {
                name: desk.name,
                topicId: desk.topicId,
              },
            ]),
          ),
        );
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Failed to load desks",
        );
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, []);

  async function handleCreate(event) {
    event.preventDefault();

    const name = newDesk.name.trim();

    if (!name) {
      setError("Desk name is required.");
      return;
    }

    if (!newDesk.topicId) {
      setError("Topic is required.");
      return;
    }

    try {
      setCreating(true);
      setError("");

      const createdDesk = await createDesk({
        name,
        topicId: newDesk.topicId,
      });

      setDesks((previous) => [
        createdDesk,
        ...previous,
      ]);

      setDrafts((previous) => ({
        ...previous,
        [createdDesk.id]: {
          name: createdDesk.name,
          topicId: createdDesk.topicId,
        },
      }));

      setNewDesk({
        name: "",
        topicId: "",
      });
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to create desk",
      );
    } finally {
      setCreating(false);
    }
  }

  function handleDraftChange(
    deskId,
    field,
    value,
  ) {
    setDrafts((previous) => ({
      ...previous,
      [deskId]: {
        ...previous[deskId],
        [field]: value,
      },
    }));

    setError("");
  }

  function isDeskChanged(desk) {
    const draft = drafts[desk.id];

    if (!draft) {
      return false;
    }

    return (
      draft.name.trim() !==
        desk.name.trim() ||
      draft.topicId !== desk.topicId
    );
  }

  async function handleUpdate(desk) {
    const draft = drafts[desk.id];

    if (!draft) {
      return;
    }

    const name = draft.name.trim();

    if (!name) {
      setError("Desk name is required.");
      return;
    }

    if (!draft.topicId) {
      setError("Topic is required.");
      return;
    }

    try {
      setSavingDeskId(desk.id);
      setError("");

      const updatedDesk =
        await updateDesk(desk.id, {
          name,
          topicId: draft.topicId,
        });

      setDesks((previous) =>
        previous.map((item) =>
          item.id === updatedDesk.id
            ? updatedDesk
            : item,
        ),
      );

      setDrafts((previous) => ({
        ...previous,
        [updatedDesk.id]: {
          name: updatedDesk.name,
          topicId: updatedDesk.topicId,
        },
      }));
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to update desk",
      );
    } finally {
      setSavingDeskId(null);
    }
  }

  async function handleDelete(deskId) {
    const confirmed = window.confirm(
      "Delete this desk? All cards belonging to it will also be deleted.",
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingDeskId(deskId);
      setError("");

      await deleteDesk(deskId);

      setDesks((previous) =>
        previous.filter(
          (desk) => desk.id !== deskId,
        ),
      );

      setDrafts((previous) => {
        const next = {
          ...previous,
        };

        delete next[deskId];

        return next;
      });
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to delete desk",
      );
    } finally {
      setDeletingDeskId(null);
    }
  }

  const filteredDesks = useMemo(() => {
    const query = search
      .trim()
      .toLowerCase();

    if (!query) {
      return desks;
    }

    return desks.filter((desk) => {
      const draft = drafts[desk.id];

      return (
        draft?.name
          ?.toLowerCase()
          .includes(query) ||
        desk.name
          .toLowerCase()
          .includes(query)
      );
    });
  }, [desks, drafts, search]);

  return (
    <main className={styles.page}>
      <section className={styles.formSection}>
        <div className={styles.sectionHeader}>
          <div>
            <div
              className={
                styles.sectionEyebrow
              }
            >
              CREATE
            </div>
          </div>
        </div>

        <form
          className={styles.form}
          onSubmit={handleCreate}
        >
          <div className={styles.field}>
            <textarea
              className={styles.textarea}
              placeholder="Desk name"
              maxLength={64}
              rows={1}
              value={newDesk.name}
              onChange={(event) =>
                setNewDesk((previous) => ({
                  ...previous,
                  name: event.target.value,
                }))
              }
              disabled={creating}
            />
          </div>

          <div className={styles.field}>
            <select
              className={styles.input}
              value={newDesk.topicId}
              onChange={(event) =>
                setNewDesk((previous) => ({
                  ...previous,
                  topicId: event.target.value,
                }))
              }
              disabled={creating}
            >
              <option value="">
                Select topic
              </option>

              {topics.map((topic) => (
                <option
                  key={topic.id}
                  value={topic.id}
                >
                  {topic.name}
                </option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            className={
              styles.primaryButton
            }
            disabled={creating}
          >
            {creating
              ? "Creating..."
              : "Create desk"}
          </button>
        </form>
      </section>

      <section className={styles.listSection}>
        <div className={styles.listHeader}>
          <div>
            <div
              className={
                styles.sectionEyebrow
              }
            >
              ALL DESKS
            </div>
          </div>

          <span className={styles.count}>
            {desks.length}
          </span>
        </div>

        {desks.length > 0 && (
          <div className={styles.searchWrapper}>
            <input
              className={styles.input}
              type="search"
              placeholder="Search desks..."
              value={search}
              onChange={(event) =>
                setSearch(
                  event.target.value,
                )
              }
            />
          </div>
        )}

        {error && (
          <div className={styles.error}>
            {error}
          </div>
        )}

        {loading && (
          <div
            className={
              styles.emptyMessage
            }
          >
            Loading desks...
          </div>
        )}

        {!loading &&
          !error &&
          desks.length === 0 && (
            <div
              className={
                styles.emptyMessage
              }
            >
              No desks yet.
            </div>
          )}

        {!loading &&
          desks.length > 0 &&
          filteredDesks.length === 0 && (
            <div
              className={
                styles.emptyMessage
              }
            >
              No desks found.
            </div>
          )}

        {!loading &&
          filteredDesks.length > 0 && (
            <div className={styles.deskList}>
              {filteredDesks.map((desk) => {
                const draft =
                  drafts[desk.id];

                const changed =
                  isDeskChanged(desk);

                const saving =
                  savingDeskId ===
                  desk.id;

                const deleting =
                  deletingDeskId ===
                  desk.id;

                return (
                  <article
                    key={desk.id}
                    className={styles.desk}
                  >
                    <div
                      className={
                        styles.deskInfo
                      }
                    >
                      <textarea
                        className={
                          styles.deskName
                        }
                        rows={1}
                        maxLength={64}
                        value={
                          draft?.name ?? ""
                        }
                        onChange={(event) =>
                          handleDraftChange(
                            desk.id,
                            "name",
                            event.target
                              .value,
                          )
                        }
                        disabled={
                          saving ||
                          deleting
                        }
                      />

                      <select
                        className={
                          styles.deskTopic
                        }
                        value={
                          draft?.topicId ??
                          ""
                        }
                        onChange={(event) =>
                          handleDraftChange(
                            desk.id,
                            "topicId",
                            event.target
                              .value,
                          )
                        }
                        disabled={
                          saving ||
                          deleting
                        }
                      >
                        {topics.map(
                          (topic) => (
                            <option
                              key={
                                topic.id
                              }
                              value={
                                topic.id
                              }
                            >
                              {
                                topic.name
                              }
                            </option>
                          ),
                        )}
                      </select>
                    </div>

                    <div
                      className={
                        styles.actions
                      }
                    >
                      <button
                        type="button"
                        className={
                          changed
                            ? styles.updateButton
                            : styles.secondaryButton
                        }
                        disabled={
                          !changed ||
                          saving ||
                          deleting
                        }
                        onClick={() =>
                          handleUpdate(
                            desk,
                          )
                        }
                      >
                        {saving
                          ? "Saving..."
                          : "Edit"}
                      </button>

                      <button
                        type="button"
                        className={
                          styles.deleteButton
                        }
                        disabled={
                          saving ||
                          deleting
                        }
                        onClick={() =>
                          handleDelete(
                            desk.id,
                          )
                        }
                      >
                        {deleting
                          ? "Deleting..."
                          : "Delete"}
                      </button>
                    </div>
                  </article>
                );
              })}
            </div>
          )}
      </section>
    </main>
  );
}