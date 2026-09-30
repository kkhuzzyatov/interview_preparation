import { useNavigate } from "react-router-dom";
import styles from "./Header.module.css";

interface HeaderProps {
  isAdmin?: boolean;
}

export default function Header({
  isAdmin = false,
}: HeaderProps) {
  const navigate = useNavigate();

  return (
    <header className={styles.header}>
      <div className={styles.navigation}>
        <button
          type="button"
          className={styles.navButton}
          onClick={() => navigate("/")}
        >
          HOME
        </button>

        <button
          type="button"
          className={styles.navButton}
          onClick={() => navigate("/answers")}
        >
          ANSWERS
        </button>

        <button
          type="button"
          className={styles.navButton}
          onClick={() => navigate("/leaderboard")}
        >
          LEADERBOARD
        </button>

        {isAdmin && (
          <div className={styles.adminNavigation}>

            <button
              type="button"
              className={`${styles.navButton} ${styles.adminNavButton}`}
              onClick={() => navigate("/topics")}
            >
              TOPICS
            </button>

            <button
              type="button"
              className={`${styles.navButton} ${styles.adminNavButton}`}
              onClick={() => navigate("/desks")}
            >
              DESKS
            </button>

            <button
              type="button"
              className={`${styles.navButton} ${styles.adminNavButton}`}
              onClick={() => navigate("/cards")}
            >
              CARDS
            </button>

            <button
              type="button"
              className={`${styles.navButton} ${styles.adminNavButton}`}
              onClick={() => navigate("/settings")}
            >
              SETTINGS
            </button>
          </div>
        )}
      </div>

      <div className={styles.version}>
        v1.0
      </div>
    </header>
  );
}