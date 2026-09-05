import { useNavigate } from "react-router-dom";
import styles from "./Header.module.css";

export default function Header() {
  const navigate = useNavigate();

  return (
    <header className={styles.header}>
      <button
        type="button"
        className={styles.homeButton}
        onClick={() => navigate("/home")}
      >
        HOME
      </button>
    </header>
  );
}