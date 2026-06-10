import { useContext } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import "./Sidebar.css";

// Definición de privilegios por rol
const ROLE_ACCESS = {
    ROLE_ADMIN: ["/dashboard", "/inmuebles", "/ambientes", "/sensores", "/tarifas", "/alertas", "/incidencias", "/mantenimientos", "/monitoreo"],
    ROLE_PROPIETARIO: ["/dashboard", "/inmuebles", "/ambientes", "/tarifas", "/monitoreo"],
    ROLE_OPERADOR: ["/dashboard", "/sensores", "/alertas", "/incidencias", "/monitoreo"],
    ROLE_TECNICO: ["/dashboard", "/incidencias", "/mantenimientos"],
};

const ALL_NAV_ITEMS = [
    { path: "/dashboard", label: "Panel de Control", icon: "📊" },
    { path: "/inmuebles", label: "Inmuebles", icon: "🏢" },
    { path: "/ambientes", label: "Ambientes", icon: "🏠" },
    { path: "/sensores", label: "Sensores", icon: "📡" },
    { path: "/tarifas", label: "Tarifas", icon: "💲" },
    { path: "/alertas", label: "Alertas", icon: "🔔" },
    { path: "/incidencias", label: "Incidencias", icon: "⚠️" },
    { path: "/mantenimientos", label: "Mantenimientos", icon: "🔧" },
    { path: "/monitoreo", label: "Monitoreo", icon: "⚡", isMonitoreo: true },
];

export default function NavbarMenu() {
    const navigate = useNavigate();
    const location = useLocation();
    const { roles, userEmail, logout: authLogout } = useContext(AuthContext);

    const handleLogout = () => {
        authLogout();
        navigate("/");
    };

    // Determinar los accesos del usuario según su rol principal
    const userRole = roles && roles.length > 0 ? roles[0] : "ROLE_OPERADOR";
    const allowedPaths = ROLE_ACCESS[userRole] || ROLE_ACCESS["ROLE_OPERADOR"];

    const filteredItems = ALL_NAV_ITEMS.filter(item => allowedPaths.includes(item.path));

    // Etiqueta amigable para el rol
    const roleLabels = {
        ROLE_ADMIN: "Administrador",
        ROLE_PROPIETARIO: "Propietario",
        ROLE_OPERADOR: "Operador",
        ROLE_TECNICO: "Técnico",
    };

    return (
        <aside className="sidebar">
            <div className="sidebar-header">
                <Link to="/dashboard" className="sidebar-brand">
                    ⚡ VoltRoom
                </Link>
            </div>

            <div className="sidebar-user-profile">
                <div className="profile-avatar">
                    {userEmail ? userEmail.charAt(0).toUpperCase() : 'U'}
                </div>
                <div className="profile-info">
                    <span className="profile-name">
                        {userEmail ? userEmail.split('@')[0] : 'Usuario'}
                    </span>
                    <span className="profile-role">
                        {roleLabels[userRole] || "Operador"}
                    </span>
                </div>
            </div>

            <ul className="sidebar-nav">
                {filteredItems.map((item) => (
                    <li key={item.path} className="nav-item">
                        <Link
                            to={item.path}
                            className={`nav-link ${location.pathname === item.path ? "active" : ""}`}
                        >
                            <span className="nav-icon">{item.icon}</span>
                            {item.isMonitoreo && (
                                <span className="spinner-grow spinner-grow-sm text-success" style={{ width: '0.5rem', height: '0.5rem', marginRight: '0.4rem' }}></span>
                            )}
                            {item.label}
                        </Link>
                    </li>
                ))}
            </ul>

            <div className="sidebar-footer">
                <button className="btn-sidebar-logout" onClick={handleLogout}>
                    <span style={{marginRight: '8px'}}>🚪</span> Cerrar Sesión
                </button>
            </div>
        </aside>
    );
}