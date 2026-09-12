import { useState } from "react";
import "./App.css";
import { NavLink, Routes, Route, useLocation, useNavigate } from "react-router-dom";
import ProtectedRoute from "./features/auth/components/ProtectedRoute";

import DashboardPage from "./pages/DashboardPage";
import HomePage from "./pages/HomePage";
import DataEntryPage from "./pages/DataEntryPage.jsx";
import Recommendations from "./pages/RecommendationsPage.jsx";
import ImportExportPage from "./pages/ImportExportPage.jsx";
import WeatherPage from "./pages/WeatherPage.jsx";
import AdminPage from "./pages/AdminPage.jsx";
import ProfilePage from "./pages/ProfilePage.jsx";

import LoginPage from "./features/auth/pages/LoginPage";
import RegisterPage from "./features/auth/pages/RegisterPage";

import { clearCurrentUser, getCurrentUser } from "./api";

function LandingPage() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const location = useLocation();
  const navigate = useNavigate();
  const currentUser = getCurrentUser();

  const showHero = location.pathname === "/";

  const closeMenu = () => {
    setMenuOpen(false);
    setUserMenuOpen(false);
  };

  const handleLogout = () => {
    clearCurrentUser();
    closeMenu();
    navigate("/");
  };

  return (
      <div className="app-container">
        <nav className="app-navbar">
          <NavLink to="/" className="navbar-brand" onClick={closeMenu}>
            <img
                src="/agriculture-icon.png"
                alt="AgriSense AI logo"
                className="brand-logo"
            />

            <div>
              <h1>AgriSense AI</h1>
              <p>Intelligent Agriculture System</p>
            </div>
          </NavLink>

          <button
              className={`mobile-menu-toggle ${menuOpen ? "open" : ""}`}
              type="button"
              onClick={() => setMenuOpen((previous) => !previous)}
              aria-label="Toggle navigation menu"
              aria-expanded={menuOpen}
          >
            <span></span>
            <span></span>
            <span></span>
          </button>

          <div className={`navbar-menu ${menuOpen ? "open" : ""}`}>
            <div className="navbar-links">
              <NavLink to="/" end onClick={closeMenu}>
                Home
              </NavLink>

              <NavLink to="/dashboard" onClick={closeMenu}>
                Dashboard
              </NavLink>

              <NavLink to="/data-entry" onClick={closeMenu}>
                Data Entry
              </NavLink>

              <NavLink to="/recommendations" onClick={closeMenu}>
                Recommendations
              </NavLink>

              <NavLink to="/import-export" onClick={closeMenu}>
                Import / Export
              </NavLink>

              <NavLink to="/weather" onClick={closeMenu}>
                Weather API
              </NavLink>
            </div>

            <div className="navbar-auth">
              {currentUser ? (
                  <div className="user-dropdown">
                    <button
                        type="button"
                        className="user-dropdown-toggle"
                        onClick={() => setUserMenuOpen((previous) => !previous)}
                    >
                      <span>{currentUser.fullName}</span>
                      <span className="dropdown-arrow">▾</span>
                    </button>

                    {userMenuOpen && (
                        <div className="user-dropdown-menu">
                          <NavLink to="/profile" onClick={closeMenu}>
                            Profile
                          </NavLink>

                          {currentUser.role === "ADMIN" && (
                              <NavLink to="/admin" onClick={closeMenu}>
                                Admin Panel
                              </NavLink>
                          )}

                          <button type="button" onClick={handleLogout}>
                            Logout
                          </button>
                        </div>
                    )}
                  </div>
              ) : (
                  <>
                    <NavLink to="/auth/login" className="auth-link" onClick={closeMenu}>
                      Login
                    </NavLink>

                    <NavLink
                        to="/auth/register"
                        className="auth-button"
                        onClick={closeMenu}
                    >
                      Register
                    </NavLink>
                  </>
              )}
            </div>
          </div>
        </nav>

        {showHero && (
            <header className="hero-section">
              <div className="hero-content">
                <span className="hero-badge">AI in Agriculture · Team 38</span>

                <h2>Intelligent Agriculture System</h2>

                <p>
                  A web-based platform for managing agricultural data, analyzing soil
                  and crop conditions, importing/exporting records, integrating weather
                  data, and generating AI-supported irrigation recommendations.
                </p>

                <div className="hero-actions">
                  <NavLink to="/dashboard" className="primary-action">
                    View Dashboard
                  </NavLink>

                  <NavLink to="/data-entry" className="secondary-action">
                    Add Agricultural Data
                  </NavLink>
                </div>
              </div>
            </header>
        )}

        <main className="content-section">
          <Routes>
            <Route path="/" element={<HomePage />} />

            <Route
                path="/dashboard"
                element={
                  <ProtectedRoute>
                    <DashboardPage />
                  </ProtectedRoute>
                }
            />

            <Route
                path="/data-entry"
                element={
                  <ProtectedRoute>
                    <DataEntryPage />
                  </ProtectedRoute>
                }
            />

            <Route
                path="/recommendations"
                element={
                  <ProtectedRoute>
                    <Recommendations />
                  </ProtectedRoute>
                }
            />

            <Route
                path="/import-export"
                element={
                  <ProtectedRoute>
                    <ImportExportPage />
                  </ProtectedRoute>
                }
            />

            <Route path="/weather" element={<WeatherPage />} />

            <Route
                path="/profile"
                element={
                  <ProtectedRoute>
                    <ProfilePage />
                  </ProtectedRoute>
                }
            />

            <Route
                path="/admin"
                element={
                  <ProtectedRoute requiredRole="ADMIN">
                    <AdminPage />
                  </ProtectedRoute>
                }
            />
          </Routes>
        </main>

        <footer className="footer">
          <p>© 2026 – Intelligent Agriculture System · Team 38</p>
        </footer>
      </div>
  );
}

function App() {
  return (
      <Routes>
        <Route path="/auth/login" element={<LoginPage />} />
        <Route path="/auth/register" element={<RegisterPage />} />
        <Route path="/*" element={<LandingPage />} />
      </Routes>
  );
}

export default App;