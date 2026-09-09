import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import styles from "./HomePage.module.css";
import {
  getAllDesks,
  getDeskStatistics,
} from "../../api/deskApi";

export default function HomePage() {
  const navigate = useNavigate();

  const [desks, setDesks] = useState([]);
  const [statistics, setStatistics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        setError("");

        const [desksData, statisticsData] =
          await Promise.all([
            getAllDesks(),
            getDeskStatistics(),
          ]);

        setDesks(desksData);
        setStatistics(statisticsData);
      } catch (err) {
        setError(
          err instanceof Error
            ? err.message
            : "Failed to load desks"
        );
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, []);

  function getDeskStatisticsForDesk(deskId) {
    if (Array.isArray(statistics)) {
      const result = statistics.find(
        (item) => item.deskId === deskId
      );

      if (result) {
        return result;
      }
    }

    if (
      statistics &&
      typeof statistics === "object" &&
      !Array.isArray(statistics)
    ) {
      const result = statistics[deskId];

      if (result) {
        return result;
      }
    }

    return {
      blue: 0,
      red: 0,
      yellow: 0,
      green: 0,
    };
  }

  return (
    <main className={styles.page}>
      <section className={styles.review}>
        <div className={styles.reviewContent}>
          <div className={styles.eyebrow}>
            READY TO REVIEW
          </div>

          <h1>Start your review</h1>
        </div>

        <button
          className={styles.startButton}
          type="button"
          onClick={() => navigate("/review")}
        >
          START
        </button>
      </section>

      <section className={styles.desksSection}>
        <h2>DESKS</h2>

        {loading && (
          <div className={styles.message}>
            Loading desks...
          </div>
        )}

        {error && (
          <div className={styles.error}>
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          desks.length === 0 && (
            <div className={styles.message}>
              No desks yet.
            </div>
          )}

        {!loading &&
          !error &&
          desks.length > 0 && (
            <div className={styles.deskList}>
              {desks.map((desk) => {
                const stats =
                  getDeskStatisticsForDesk(desk.id);

                const total =
                  stats.blue +
                  stats.red +
                  stats.yellow +
                  stats.green;

                return (
                  <div
                    className={styles.desk}
                    key={desk.id}
                  >
                    <div className={styles.deskName}>
                      {desk.name}
                    </div>

                    <div className={styles.stats}>
                      <span className={styles.blue}>
                        {stats.blue}
                      </span>

                      <span className={styles.red}>
                        {stats.red}
                      </span>

                      <span className={styles.yellow}>
                        {stats.yellow}
                      </span>

                      <span className={styles.green}>
                        {stats.green}
                      </span>

                      <span className={styles.total}>
                        ({total})
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
      </section>
    </main>
  );
}