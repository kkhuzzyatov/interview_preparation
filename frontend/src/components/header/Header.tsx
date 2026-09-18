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

        {isAdmin && (
          <>
            <button
              type="button"
              className={styles.navButton}
              onClick={() => navigate("/cards")}
            >
              CARDS
            </button>

            <button
              type="button"
              className={styles.navButton}
              onClick={() => navigate("/settings")}
            >
              SETTINGS
            </button>
          </>
        )}

        <button
          type="button"
          className={styles.navButton}
          onClick={() => navigate("/leaderboard")}
        >
          LEADERBOARD
        </button>
      </div>

      <div className={styles.version}>
        v1.0
      </div>
    </header>
  );
}