import { useEffect, useMemo, useState } from "react";
import styles from "./TopicPage.module.css";
import {
  createTopic,
  deleteTopic,
  getAllTopics,
  updateTopic,
} from "../../api/topicApi";

export default function TopicPage() {
  const [topics, setTopics] = useState([]);
  const [drafts, setDrafts] = useState({});

  const [newTopicName, setNewTopicName] =
    useState("");
  const [search, setSearch] = useState("");

  const [loading, setLoading] =
    useState(true);
  const [creating, setCreating] =
    useState(false);
  const [savingTopicId, setSavingTopicId] =
    useState(null);
  const [deletingTopicId, setDeletingTopicId] =
    useState(null);

  const [error, setError] = useState("");

  useEffect(() => {
    async function loadTopics() {
      try {
        setLoading(true);
        setError("");

        const data = await getAllTopics();

        setTopics(data);

        setDrafts(
          Object.fromEntries(
            data.map((topic) => [
              topic.id,
              topic.name,
            ]),
          ),
        );
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Failed to load topics",
        );
      } finally {
        setLoading(false);
      }
    }

    loadTopics();
  }, []);

  async function handleCreate(event) {
    event.preventDefault();

    const name = newTopicName.trim();

    if (!name) {
      setError("Topic name is required.");
      return;
    }

    try {
      setCreating(true);
      setError("");

      const createdTopic =
        await createTopic({ name });

      setTopics((previous) => [
        createdTopic,
        ...previous,
      ]);

      setDrafts((previous) => ({
        ...previous,
        [createdTopic.id]:
          createdTopic.name,
      }));

      setNewTopicName("");
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to create topic",
      );
    } finally {
      setCreating(false);
    }
  }

  function handleTopicNameChange(
    topicId,
    value,
  ) {
    setDrafts((previous) => ({
      ...previous,
      [topicId]: value,
    }));

    setError("");
  }

  function isTopicChanged(topic) {
    const draft =
      drafts[topic.id] ?? "";

    return (
      draft.trim() !==
      topic.name.trim()
    );
  }

  async function handleUpdate(topic) {
    const name = (
      drafts[topic.id] ?? ""
    ).trim();

    if (!name) {
      setError("Topic name is required.");
      return;
    }

    try {
      setSavingTopicId(topic.id);
      setError("");

      const updatedTopic =
        await updateTopic(topic.id, {
          name,
        });

      setTopics((previous) =>
        previous.map((item) =>
          item.id === updatedTopic.id
            ? updatedTopic
            : item,
        ),
      );

      setDrafts((previous) => ({
        ...previous,
        [updatedTopic.id]:
          updatedTopic.name,
      }));
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to update topic",
      );
    } finally {
      setSavingTopicId(null);
    }
  }

  async function handleDelete(topicId) {
    const confirmed = window.confirm(
      "Delete this topic?",
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingTopicId(topicId);
      setError("");

      await deleteTopic(topicId);

      setTopics((previous) =>
        previous.filter(
          (topic) =>
            topic.id !== topicId,
        ),
      );

      setDrafts((previous) => {
        const next = {
          ...previous,
        };

        delete next[topicId];

        return next;
      });
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to delete topic",
      );
    } finally {
      setDeletingTopicId(null);
    }
  }

  const filteredTopics = useMemo(() => {
    const query = search
      .trim()
      .toLowerCase();

    if (!query) {
      return topics;
    }

    return topics.filter((topic) =>
      (
        drafts[topic.id] ??
        topic.name
      )
        .toLowerCase()
        .includes(query),
    );
  }, [topics, drafts, search]);

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
            <input
              className={styles.input}
              type="text"
              maxLength={64}
              placeholder="Topic name"
              value={newTopicName}
              onChange={(event) =>
                setNewTopicName(
                  event.target.value,
                )
              }
              disabled={creating}
            />
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
              : "Create topic"}
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
              ALL TOPICS
            </div>
          </div>

          <span className={styles.count}>
            {topics.length}
          </span>
        </div>

        {topics.length > 0 && (
          <div className={styles.searchWrapper}>
            <input
              className={styles.input}
              type="search"
              placeholder="Search topics..."
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
            Loading topics...
          </div>
        )}

        {!loading &&
          !error &&
          topics.length === 0 && (
            <div
              className={
                styles.emptyMessage
              }
            >
              No topics yet.
            </div>
          )}

        {!loading &&
          topics.length > 0 &&
          filteredTopics.length === 0 && (
            <div
              className={
                styles.emptyMessage
              }
            >
              No topics found.
            </div>
          )}

        {!loading &&
          filteredTopics.length > 0 && (
            <div className={styles.topicList}>
              {filteredTopics.map((topic) => {
                const changed =
                  isTopicChanged(topic);

                const saving =
                  savingTopicId ===
                  topic.id;

                const deleting =
                  deletingTopicId ===
                  topic.id;

                return (
                  <article
                    key={topic.id}
                    className={
                      styles.topic
                    }
                  >
                    <div
                      className={
                        styles.topicInfo
                      }
                    >
                      <input
                        className={
                          styles.topicInput
                        }
                        type="text"
                        maxLength={64}
                        value={
                          drafts[
                            topic.id
                          ] ?? ""
                        }
                        onChange={(event) =>
                          handleTopicNameChange(
                            topic.id,
                            event.target
                              .value,
                          )
                        }
                        disabled={
                          saving ||
                          deleting
                        }
                      />
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
                            topic,
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
                            topic.id,
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