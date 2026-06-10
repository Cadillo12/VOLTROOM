import { BrowserRouter, Routes, Route } from "react-router-dom";
import PrivateRoute from "./components/PrivateRoute";
import NavbarMenu from "./components/NavbarMenu";
import "./App.css";

import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import InmueblesPage from "./pages/InmueblesPage";
import AmbientesPage from "./pages/AmbientesPage";
import SensoresPage from "./pages/SensoresPage";
import TarifasPage from "./pages/TarifasPage";
import AlertasPage from "./pages/AlertasPage";
import {IncidenciasPage} from "./pages/IncidenciasPage";
import MantenimientosPage from "./pages/MantenimientosPage";
import MonitoreoPage from "./pages/MonitoreoPage";

function Layout({ children }) {
  return (
      <div className="app-layout">
        <NavbarMenu />
        <main className="main-content">
            <div className="container mt-4 page-container">{children}</div>
        </main>
      </div>
  );
}

export default function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LoginPage />} />

          <Route
              path="/dashboard"
              element={
                <PrivateRoute>
                  <Layout>
                    <DashboardPage />
                  </Layout>
                </PrivateRoute>
              }
          />

          <Route
              path="/inmuebles"
              element={
                <PrivateRoute>
                  <Layout>
                    <InmueblesPage />
                  </Layout>
                </PrivateRoute>
              }
          />

          <Route
              path="/ambientes"
              element={
                <PrivateRoute>
                  <Layout>
                    <AmbientesPage />
                  </Layout>
                </PrivateRoute>
              }
          />

          <Route
              path="/sensores"
              element={
                <PrivateRoute>
                  <Layout>
                    <SensoresPage />
                  </Layout>
                </PrivateRoute>
              }
          />

          <Route
              path="/tarifas"
              element={
                <PrivateRoute>
                  <Layout>
                    <TarifasPage />
                  </Layout>
                </PrivateRoute>
              }
          />

          <Route
              path="/alertas"
              element={
                <PrivateRoute>
                  <Layout>
                    <AlertasPage />
                  </Layout>
                </PrivateRoute>
              }
          />

          <Route
              path="/incidencias"
              element={
                <PrivateRoute>
                  <Layout>
                    <IncidenciasPage />
                  </Layout>
                </PrivateRoute>
              }
          />

          <Route
              path="/mantenimientos"
              element={
                <PrivateRoute>
                  <Layout>
                    <MantenimientosPage />
                  </Layout>
                </PrivateRoute>
              }
          />

          <Route
              path="/monitoreo"
              element={
                <PrivateRoute>
                  <Layout>
                    <MonitoreoPage />
                  </Layout>
                </PrivateRoute>
              }
          />
        </Routes>
      </BrowserRouter>
  );
}