import { useEffect, useState } from "react";
import styles from "./LeaderboardPage.module.css";
import {
  getDayLeaderboard,
  getWeekLeaderboard,
  getAllTimeLeaderboard,
} from "../../api/leaderboardApi";

const PERIODS = {
  DAY: {
    label: "Day",
    title: "Today",
    description: "See who earned the most points today.",
    load: getDayLeaderboard,
  },
  WEEK: {
    label: "Week",
    title: "This week",
    description: "See who earned the most points this week.",
    load: getWeekLeaderboard,
  },
  ALL_TIME: {
    label: "All time",
    title: "All time",
    description: "See the highest scores across all answers.",
    load: getAllTimeLeaderboard,
  },
};

export default function LeaderboardPage() {
  const [period, setPeriod] = useState("DAY");
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadLeaderboard() {
      try {
        setLoading(true);
        setError("");

        const data = await PERIODS[period].load();

        setUsers(data);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Failed to load leaderboard"
        );
      } finally {
        setLoading(false);
      }
    }

    loadLeaderboard();
  }, [period]);

  function getRankClass(index) {
    if (index === 0) {
      return styles.rankFirst;
    }

    if (index === 1) {
      return styles.rankSecond;
    }

    if (index === 2) {
      return styles.rankThird;
    }

    return styles.rank;
  }

  function formatScore(score) {
    return score.toLocaleString();
  }

  const selectedPeriod = PERIODS[period];

  return (
    <main className={styles.page}>
      <section className={styles.header}>
        <div className={styles.eyebrow}>
          LEADERBOARD
        </div>

        <h1>{selectedPeriod.title}</h1>

        <p>{selectedPeriod.description}</p>
      </section>

      <section className={styles.leaderboardSection}>
        <div className={styles.tabs}>
          {Object.entries(PERIODS).map(
            ([key, value]) => (
              <button
                type="button"
                key={key}
                className={`${styles.tab} ${
                  period === key
                    ? styles.tabActive
                    : ""
                }`}
                onClick={() => setPeriod(key)}
              >
                {value.label}
              </button>
            )
          )}
        </div>

        <div className={styles.sectionHeader}>
          <h2>
            {selectedPeriod.title.toUpperCase()}
          </h2>

          {!loading && !error && (
            <div className={styles.count}>
              {users.length}
              {users.length === 1
                ? " user"
                : " users"}
            </div>
          )}
        </div>

        {loading && (
          <div className={styles.message}>
            Loading leaderboard...
          </div>
        )}

        {error && (
          <div className={styles.error}>
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          users.length === 0 && (
            <div className={styles.message}>
              No answers yet.
            </div>
          )}

        {!loading &&
          !error &&
          users.length > 0 && (
            <div className={styles.userList}>
              {users.map((user, index) => (
                <article
                  className={styles.user}
                  key={user.userId}
                >
                  <div className={styles.userRank}>
                    <span
                      className={getRankClass(index)}
                    >
                      {index + 1}
                    </span>
                  </div>

                  <div className={styles.userInfo}>
                    <div className={styles.username}>
                      {user.username}
                    </div>

                    <div className={styles.userMeta}>
                      {user.answerCount}{" "}
                      {user.answerCount === 1
                        ? "answer"
                        : "answers"}
                    </div>
                  </div>

                  <div className={styles.score}>
                    <span className={styles.scoreValue}>
                      {formatScore(
                        user.totalScore
                      )}
                    </span>

                    <span className={styles.scoreLabel}>
                      pts
                    </span>
                  </div>
                </article>
              ))}
            </div>
          )}
      </section>
    </main>
  );
}