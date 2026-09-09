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

import { getCurrentUser } from "./api/userApi";

function ProtectedRoute() {
  const [authenticated, setAuthenticated] = useState(null);

  useEffect(() => {
    async function checkAuthentication() {
      const token = localStorage.getItem("token");

      if (!token) {
        setAuthenticated(false);
        return;
      }

      try {
        await getCurrentUser(token);
        setAuthenticated(true);
      } catch {
        localStorage.removeItem("token");
        setAuthenticated(false);
      }
    }

    checkAuthentication();
  }, []);

  // Authentication check is still in progress
  if (authenticated === null) {
    return null;
  }

  // User is not authenticated
  if (!authenticated) {
    return <Navigate to="/login" replace />;
  }

  // User is authenticated
  return <Outlet />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Header />

      <Routes>
        <Route
          path="/"
          element={
            <Navigate
              to="/login"
              replace
            />
          }
        />

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
            path="/home"
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
        </Route>

        <Route
          path="*"
          element={<NotFound />}
        />
      </Routes>
    </BrowserRouter>
  );
}