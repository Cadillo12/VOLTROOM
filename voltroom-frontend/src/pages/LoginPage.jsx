import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import axiosClient from "../api/axiosClient";
import { toast } from "react-toastify";
import "./Login.css";

export default function LoginPage() {
    const navigate = useNavigate();
    const { login: authLogin } = useContext(AuthContext);
    
    // UI State
    const [isLogin, setIsLogin] = useState(true);
    const [isLoading, setIsLoading] = useState(false);

    // Form States
    const [loginForm, setLoginForm] = useState({
        email: "admin@voltroom.com",
        password: "123456"
    });

    const [registerForm, setRegisterForm] = useState({
        nombres: "",
        apellidos: "",
        email: "",
        password: "",
        rol: "ROLE_OPERADOR"
    });

    // Error Parser
    const parseError = (err, defaultMsg) => {
        if (err?.response?.data) {
            const data = err.response.data;
            if (data.message) return data.message;
            if (data.error) return data.error;
            if (typeof data === 'object') {
                const vals = Object.values(data);
                if (vals.length > 0 && typeof vals[0] === 'string') {
                    return vals.join(" | ");
                }
            }
        }
        return defaultMsg;
    };

    // Handlers
    const handleLoginChange = (e) => setLoginForm({ ...loginForm, [e.target.name]: e.target.value });
    const handleRegisterChange = (e) => setRegisterForm({ ...registerForm, [e.target.name]: e.target.value });

    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            const payload = { 
                email: loginForm.email.toLowerCase().trim(), 
                password: loginForm.password 
            };
            const { data } = await axiosClient.post("/auth/login", payload);
            toast.success("¡Bienvenido a VoltRoom!");
            authLogin(data);
            navigate("/dashboard");
        } catch (err) {
            toast.error(parseError(err, "Credenciales incorrectas"));
        } finally {
            setIsLoading(false);
        }
    };

    const handleRegisterSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            const payload = { 
                ...registerForm, 
                email: registerForm.email.toLowerCase().trim() 
            };
            
            // 1. Registrar
            await axiosClient.post("/auth/register", payload);
            toast.success("¡Registro exitoso! Iniciando sesión...");
            
            // 2. Auto-login
            const loginPayload = { email: payload.email, password: payload.password };
            const loginRes = await axiosClient.post("/auth/login", loginPayload);
            
            // 3. Redirigir
            authLogin(loginRes.data);
            navigate("/dashboard");
            
        } catch (err) {
            toast.error(parseError(err, "Verifique los datos ingresados"));
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="login-layout">
            {/* Immersive Background */}
            <div className="login-background">
                <div className="bg-overlay"></div>
                <div className="bg-grid"></div>
                <div className="glow-orb orb-1"></div>
                <div className="glow-orb orb-2"></div>
            </div>

            <div className="login-content-wrapper">
                <div className="login-brand slide-in-left">
                    <img src="/logo.png" alt="VoltRoom Logo" className="brand-logo" onError={(e) => { e.target.style.display = 'none'; e.target.nextSibling.style.display = 'flex'; }} />
                    <div className="logo-placeholder" style={{ display: 'none' }}>
                        <div className="logo-icon-bg">
                            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{color: '#0dbaba'}}>
                                <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"></path>
                            </svg>
                        </div>
                        <span>VoltRoom</span>
                    </div>
                    <h1>Inteligencia Energética</h1>
                    <p>Telemetría avanzada para entornos industriales. Optimiza, monitorea y toma el control absoluto de tu consumo energético en tiempo real.</p>
                    
                    <div className="brand-stats">
                        <div className="stat-item">
                            <span className="stat-value">99.9%</span>
                            <span className="stat-label">Precisión</span>
                        </div>
                        <div className="stat-divider"></div>
                        <div className="stat-item">
                            <span className="stat-value">24/7</span>
                            <span className="stat-label">Monitoreo</span>
                        </div>
                    </div>
                </div>

                <div className="login-glass-card slide-in-right">
                    <div className="form-toggle-modern">
                        <button 
                            className={`toggle-modern-btn ${isLogin ? 'active' : ''}`} 
                            onClick={() => setIsLogin(true)}
                            type="button"
                        >
                            Iniciar Sesión
                        </button>
                        <button 
                            className={`toggle-modern-btn ${!isLogin ? 'active' : ''}`} 
                            onClick={() => setIsLogin(false)}
                            type="button"
                        >
                            Crear Cuenta
                        </button>
                        <div className="toggle-slider" style={{ transform: isLogin ? 'translateX(0)' : 'translateX(100%)' }}></div>
                    </div>

                    <div className="form-scroll-container">
                        {isLogin ? (
                            <div className="form-view fade-enter">
                                <div className="form-header-modern">
                                    <h2>Bienvenido de nuevo</h2>
                                    <p>Ingresa tus credenciales para acceder a tu panel de control.</p>
                                </div>
                                
                                <form onSubmit={handleLoginSubmit} className="modern-form">
                                    <div className="input-group">
                                        <div className="input-icon">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                                        </div>
                                        <input 
                                            type="email" 
                                            name="email"
                                            placeholder="Correo Electrónico (ej. admin@voltroom.com)"
                                            value={loginForm.email} 
                                            onChange={handleLoginChange} 
                                            required 
                                        />
                                    </div>
                                    
                                    <div className="input-group">
                                        <div className="input-icon">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                                        </div>
                                        <input 
                                            type="password" 
                                            name="password"
                                            placeholder="Contraseña"
                                            value={loginForm.password} 
                                            onChange={handleLoginChange} 
                                            required 
                                        />
                                    </div>

                                    <button type="submit" className="btn-glow" disabled={isLoading}>
                                        {isLoading ? (
                                            <span className="spinner-container">
                                                <svg className="spinner" viewBox="0 0 50 50">
                                                    <circle className="path" cx="25" cy="25" r="20" fill="none" strokeWidth="5"></circle>
                                                </svg>
                                                Procesando...
                                            </span>
                                        ) : "Acceder al Sistema"}
                                    </button>
                                </form>
                            </div>
                        ) : (
                            <div className="form-view fade-enter">
                                <div className="form-header-modern">
                                    <h2>Únete a VoltRoom</h2>
                                    <p>Configura tu cuenta y empieza a monitorear hoy mismo.</p>
                                </div>

                                <form onSubmit={handleRegisterSubmit} className="modern-form">
                                    <div className="input-row-modern">
                                        <div className="input-group">
                                            <div className="input-icon">
                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                                            </div>
                                            <input 
                                                type="text" 
                                                name="nombres"
                                                placeholder="Nombres"
                                                value={registerForm.nombres} 
                                                onChange={handleRegisterChange} 
                                                required 
                                            />
                                        </div>
                                        <div className="input-group">
                                            <input 
                                                type="text" 
                                                name="apellidos"
                                                placeholder="Apellidos"
                                                value={registerForm.apellidos} 
                                                onChange={handleRegisterChange} 
                                                required 
                                                style={{ paddingLeft: '1.2rem' }}
                                            />
                                        </div>
                                    </div>

                                    <div className="input-group">
                                        <div className="input-icon">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                                        </div>
                                        <input 
                                            type="email" 
                                            name="email"
                                            placeholder="Correo Electrónico"
                                            value={registerForm.email} 
                                            onChange={handleRegisterChange} 
                                            required 
                                        />
                                    </div>

                                    <div className="input-group">
                                        <div className="input-icon">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                                        </div>
                                        <input 
                                            type="password" 
                                            name="password"
                                            placeholder="Contraseña (Mín. 6 caracteres)"
                                            value={registerForm.password} 
                                            onChange={handleRegisterChange} 
                                            required 
                                        />
                                    </div>

                                    <div className="input-group">
                                        <div className="input-icon">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
                                        </div>
                                        <select name="rol" value={registerForm.rol} onChange={handleRegisterChange} required className="modern-select">
                                            <option value="ROLE_OPERADOR">Operador de Planta</option>
                                            <option value="ROLE_PROPIETARIO">Supervisor Energético</option>
                                            <option value="ROLE_ADMIN">Administrador del Sistema</option>
                                        </select>
                                    </div>

                                    <button type="submit" className="btn-glow" disabled={isLoading}>
                                        {isLoading ? (
                                            <span className="spinner-container">
                                                <svg className="spinner" viewBox="0 0 50 50">
                                                    <circle className="path" cx="25" cy="25" r="20" fill="none" strokeWidth="5"></circle>
                                                </svg>
                                                Registrando...
                                            </span>
                                        ) : "Crear Cuenta e Iniciar"}
                                    </button>
                                </form>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}