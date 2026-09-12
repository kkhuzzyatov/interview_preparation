import { useEffect, useMemo, useState } from "react";
import styles from "./CardPage.module.css";
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
    new Set()
  );

  const [loadingDesks, setLoadingDesks] = useState(true);
  const [loadingCards, setLoadingCards] = useState(false);

  const [error, setError] = useState("");

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
            : "Failed to load desks"
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
            : []
        );

        setExpandedCards(new Set());
      } catch (err) {
        setCards([]);

        setError(
          err instanceof Error
            ? err.message
            : "Failed to load cards"
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
          ? updatedCard
          : card
      )
    );
  }

  const filteredCards = useMemo(() => {
    const query = search.trim().toLowerCase();

    if (!query) {
      return cards;
    }

    return cards.filter((card) =>
      card.question
        .toLowerCase()
        .includes(query)
    );
  }, [cards, search]);

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <div className={styles.eyebrow}>
            CARDS
          </div>

          <h1>Cards</h1>
        </div>
      </header>

      <section className={styles.controls}>
        <div className={styles.selectWrapper}>
          <label
            className={styles.label}
            htmlFor="desk"
          >
            DESK
          </label>

          <select
            id="desk"
            className={styles.select}
            value={selectedDeskId}
            onChange={(event) => {
              setSelectedDeskId(event.target.value);
              setSearch("");
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
            <label
              className={styles.label}
              htmlFor="card-search"
            >
              SEARCH
            </label>

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
              />
            ))}
          </section>
        )}
    </main>
  );
}