import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  getAllDesks,
  getDeskStatistics,
} from "../../api/deskApi";

import styles from "./HomePage.module.css";

function getTotalStatistics(statistics) {
  return statistics.reduce(
    (total, statistic) => total + statistic.count,
    0,
  );
}

export default function HomePage() {
  const navigate = useNavigate();

  const [desks, setDesks] = useState([]);
  const [statistics, setStatistics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function loadData() {
      try {
        setLoading(true);
        setError("");

        const [desksData, statisticsData] =
          await Promise.all([
            getAllDesks(),
            getDeskStatistics(),
          ]);

        if (cancelled) {
          return;
        }

        setDesks(desksData);
        setStatistics(statisticsData);
      } catch (err) {
        if (cancelled) {
          return;
        }

        setError(
          err instanceof Error
            ? err.message
            : "Failed to load desks",
        );
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    loadData();

    return () => {
      cancelled = true;
    };
  }, []);

  const statisticsByDeskId = useMemo(
    () =>
      new Map(
        statistics.map((deskStatistics) => [
          deskStatistics.deskId,
          deskStatistics,
        ]),
      ),
    [statistics],
  );

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

        {!loading && error && (
          <div className={styles.error}>
            {error}
          </div>
        )}

        {!loading && !error && desks.length === 0 && (
          <div className={styles.message}>
            No desks yet.
          </div>
        )}

        {!loading && !error && desks.length > 0 && (
          <div className={styles.deskList}>
            {desks.map((desk) => {
              const deskStatistics =
                statisticsByDeskId.get(desk.id);

              const colorStatistics =
                deskStatistics?.statistics ?? [];

              const total =
                getTotalStatistics(colorStatistics);

              return (
                <div
                  className={styles.desk}
                  key={desk.id}
                >
                  <div className={styles.deskName}>
                    {desk.name}
                  </div>

                  <div className={styles.stats}>
                    {colorStatistics.map(
                      (statistic) => (
                        <span
                          key={statistic.colorHex}
                          style={{
                            color: statistic.colorHex,
                          }}
                        >
                          {statistic.count}
                        </span>
                      ),
                    )}

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