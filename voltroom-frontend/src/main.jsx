import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { AuthProvider } from "./context/AuthContext";
import { TelemetriaProvider } from "./context/TelemetriaContext";
import "bootstrap/dist/css/bootstrap.min.css";
import "./index.css";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <AuthProvider>
            <TelemetriaProvider>
                <App />
                <ToastContainer position="bottom-right" theme="colored" />
            </TelemetriaProvider>
        </AuthProvider>
    </React.StrictMode>
);