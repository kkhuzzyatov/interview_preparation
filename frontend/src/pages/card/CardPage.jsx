import { useEffect, useMemo, useState } from "react";
import styles from "./CardPage.module.css";
import {
  createCard,
  deleteCard,
} from "../../api/cardApi";
import {
  getAllDesks,
  getDeskById,
} from "../../api/deskApi";
import CardItem from "../../components/card/CardItem";

export default function CardsPage() {
  const [desks, setDesks] = useState([]);
  const [selectedDeskId, setSelectedDeskId] = useState("");
  const [cards, setCards] = useState([]);

  const [search, setSearch] = useState("");
  const [expandedCards, setExpandedCards] = useState(
    new Set(),
  );

  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [creating, setCreating] = useState(false);

  const [loadingDesks, setLoadingDesks] = useState(true);
  const [loadingCards, setLoadingCards] = useState(false);
  const [deletingCardId, setDeletingCardId] = useState(null);

  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");

  useEffect(() => {
    async function loadDesks() {
      try {
        setLoadingDesks(true);
        setError("");

        const data = await getAllDesks();
        setDesks(data);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Failed to load desks",
        );
      } finally {
        setLoadingDesks(false);
      }
    }

    loadDesks();
  }, []);

  useEffect(() => {
    if (!selectedDeskId) {
      setCards([]);
      setExpandedCards(new Set());
      setSearch("");
      return;
    }

    async function loadCards() {
      try {
        setLoadingCards(true);
        setError("");

        const desk = await getDeskById(selectedDeskId);

        setCards(
          Array.isArray(desk.cards)
            ? desk.cards
            : [],
        );

        setExpandedCards(new Set());
      } catch (err) {
        setCards([]);

        setError(
          err instanceof Error
            ? err.message
            : "Failed to load cards",
        );
      } finally {
        setLoadingCards(false);
      }
    }

    loadCards();
  }, [selectedDeskId]);

  function toggleCard(cardId) {
    setExpandedCards((previous) => {
      const next = new Set(previous);

      if (next.has(cardId)) {
        next.delete(cardId);
      } else {
        next.add(cardId);
      }

      return next;
    });
  }

  function handleCardUpdated(updatedCard) {
    setCards((previous) =>
      previous.map((card) =>
        card.id === updatedCard.id
          ? {
              ...card,
              ...updatedCard,
            }
          : card,
      ),
    );
  }

  async function handleCreate(event) {
    event.preventDefault();

    const trimmedQuestion = question.trim();
    const trimmedAnswer = answer.trim();

    if (!selectedDeskId) {
      setFormError("Select a desk first.");
      return;
    }

    if (!trimmedQuestion) {
      setFormError("Question is required.");
      return;
    }

    if (!trimmedAnswer) {
      setFormError("Answer is required.");
      return;
    }

    try {
      setCreating(true);
      setFormError("");
      setError("");

      const createdCard = await createCard({
        question: trimmedQuestion,
        answer: trimmedAnswer,
        deskId: selectedDeskId,
      });

      setCards((previous) => [
        ...previous,
        createdCard,
      ]);

      setQuestion("");
      setAnswer("");
    } catch (err) {
      setFormError(
        err instanceof Error
          ? err.message
          : "Failed to create card",
      );
    } finally {
      setCreating(false);
    }
  }

  async function handleCardDeleted(cardId) {
    const confirmed = window.confirm(
      "Delete this card?",
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingCardId(cardId);
      setError("");

      await deleteCard(cardId);

      setCards((previous) =>
        previous.filter((card) => card.id !== cardId),
      );

      setExpandedCards((previous) => {
        const next = new Set(previous);
        next.delete(cardId);
        return next;
      });
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to delete card",
      );
    } finally {
      setDeletingCardId(null);
    }
  }

  const filteredCards = useMemo(() => {
    const query = search.trim().toLowerCase();

    if (!query) {
      return cards;
    }

    return cards.filter((card) =>
      card.question
        .toLowerCase()
        .includes(query),
    );
  }, [cards, search]);

  return (
    <main className={styles.page}>

      <section className={styles.controls}>
        <div className={styles.selectWrapper}>

          <select
            id="desk"
            className={styles.select}
            value={selectedDeskId}
            onChange={(event) => {
              setSelectedDeskId(event.target.value);
              setSearch("");
              setQuestion("");
              setAnswer("");
              setFormError("");
            }}
            disabled={loadingDesks}
          >
            <option value="">
              Select desk
            </option>

            {desks.map((desk) => (
              <option
                key={desk.id}
                value={desk.id}
              >
                {desk.name}
              </option>
            ))}
          </select>
        </div>

        {selectedDeskId && (
          <div className={styles.searchWrapper}>

            <input
              id="card-search"
              className={styles.search}
              type="search"
              placeholder="Search by question..."
              value={search}
              onChange={(event) =>
                setSearch(event.target.value)
              }
            />
          </div>
        )}
      </section>

      {selectedDeskId && (
        <section className={styles.createSection}>

          <form
            className={styles.createForm}
            onSubmit={handleCreate}
          >
            <textarea
              className={styles.textarea}
              placeholder="Question..."
              value={question}
              onChange={(event) =>
                setQuestion(event.target.value)
              }
              rows={3}
              disabled={creating}
            />

            <textarea
              className={styles.textarea}
              placeholder="Answer..."
              value={answer}
              onChange={(event) =>
                setAnswer(event.target.value)
              }
              rows={5}
              disabled={creating}
            />

            {formError && (
              <div className={styles.formError}>
                {formError}
              </div>
            )}

            <div className={styles.createActions}>
              <button
                type="submit"
                className={styles.createButton}
                disabled={creating}
              >
                {creating
                  ? "Creating..."
                  : "Create card"}
              </button>
            </div>
          </form>
        </section>
      )}

      {error && (
        <div className={styles.error}>
          {error}
        </div>
      )}

      {!selectedDeskId && !loadingDesks && (
        <div className={styles.emptyMessage}>
          Select desk to get cards.
        </div>
      )}

      {selectedDeskId && loadingCards && (
        <div className={styles.emptyMessage}>
          Loading cards...
        </div>
      )}

      {selectedDeskId &&
        !loadingCards &&
        !error &&
        filteredCards.length === 0 && (
          <div className={styles.emptyMessage}>
            {search
              ? "No cards found."
              : "No cards yet."}
          </div>
        )}

      {selectedDeskId &&
        !loadingCards &&
        filteredCards.length > 0 && (
          <section className={styles.cardList}>
            {filteredCards.map((card) => (
              <CardItem
                key={card.id}
                card={card}
                isExpanded={expandedCards.has(card.id)}
                onToggle={() =>
                  toggleCard(card.id)
                }
                onUpdated={handleCardUpdated}
                onDeleted={() =>
                  handleCardDeleted(card.id)
                }
                isDeleting={
                  deletingCardId === card.id
                }
              />
            ))}
          </section>
        )}
    </main>
  );
}