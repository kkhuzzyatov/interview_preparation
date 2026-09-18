import { useEffect, useState } from "react";
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  Outlet,
} from "react-router-dom";

import Header from "./components/header/Header";
import LoginPage from "./pages/login/LoginPage";
import RegisterPage from "./pages/register/RegisterPage";
import NotFound from "./pages/not-found/NotFound";
import HomePage from "./pages/home/HomePage";
import ReviewPage from "./pages/review/ReviewPage";
import AnswerPage from "./pages/answer/AnswerPage";
import LeaderboardPage from "./pages/leaderboard/LeaderboardPage";
import CardPage from "./pages/card/CardPage";
import SettingPage from "./pages/setting/SettingPage";

import { getCurrentUser } from "./api/userApi";

function ProtectedRoute() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function checkAuthentication() {
      const token = localStorage.getItem("token");

      if (!token) {
        setUser(null);
        setLoading(false);
        return;
      }

      try {
        const currentUser = await getCurrentUser(token);
        setUser(currentUser);
      } catch {
        localStorage.removeItem("token");
        setUser(null);
      } finally {
        setLoading(false);
      }
    }

    checkAuthentication();
  }, []);

  if (loading) {
    return null;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return (
    <>
      <Header isAdmin={user.role === "ADMIN"} />

      <Outlet context={{ user }} />
    </>
  );
}

function AdminRoute() {
  const { user } = require("react-router-dom").useOutletContext();

  if (user?.role !== "ADMIN") {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={<LoginPage />}
        />

        <Route
          path="/register"
          element={<RegisterPage />}
        />

        <Route element={<ProtectedRoute />}>
          <Route
            path="/"
            element={<HomePage />}
          />

          <Route
            path="/review"
            element={<ReviewPage />}
          />

          <Route
            path="/answers"
            element={<AnswerPage />}
          />

          <Route
            path="/leaderboard"
            element={<LeaderboardPage />}
          />

          <Route element={<AdminRoute />}>
            <Route
              path="/cards"
              element={<CardPage />}
            />

            <Route
              path="/settings"
              element={<SettingPage />}
            />
          </Route>
        </Route>

        <Route
          path="*"
          element={<NotFound />}
        />
      </Routes>
    </BrowserRouter>
  );
}