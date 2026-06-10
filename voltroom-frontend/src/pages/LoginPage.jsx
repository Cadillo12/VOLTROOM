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
    const [showPassword, setShowPassword] = useState(false);

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
        <div className="login-container">
            <div className="login-left">
                <div className="circle circle-1"></div>
                <div className="circle circle-2"></div>
                <div className="circle circle-3"></div>
                <div className="left-content">
                    <h1>BIENVENIDO</h1>
                    <h2>VOLTROOM </h2>
                    <p>Telemetría avanzada para entornos industriales. Optimiza, monitorea y toma el control absoluto de tu consumo energético en tiempo real.</p>
                </div>
            </div>

            <div className="login-right">
                <div className="corner-circle"></div>
                <div className="right-content">
                    {isLogin ? (
                        <div className="form-wrapper">
                            <h2>Sign in</h2>
                            <p className="subtitle">Ingresa tus credenciales para acceder a tu panel de control.</p>

                            <form onSubmit={handleLoginSubmit}>
                                <div className="input-group-modern">
                                    <span className="icon">
                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                                    </span>
                                    <input
                                        type="email"
                                        name="email"
                                        placeholder="User Name"
                                        value={loginForm.email}
                                        onChange={handleLoginChange}
                                        required
                                    />
                                </div>

                                <div className="input-group-modern">
                                    <span className="icon">
                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                                    </span>
                                    <input
                                        type={showPassword ? "text" : "password"}
                                        name="password"
                                        placeholder="Password"
                                        value={loginForm.password}
                                        onChange={handleLoginChange}
                                        required
                                    />
                                    <span className="show-pwd" onClick={() => setShowPassword(!showPassword)}>
                                        {showPassword ? "HIDE" : "SHOW"}
                                    </span>
                                </div>

                                <div className="form-actions">
                                    <label className="remember">
                                        <input type="checkbox" /> Remember me
                                    </label>
                                    <a href="#" className="forgot" onClick={(e) => e.preventDefault()}>Forgot Password?</a>
                                </div>

                                <button type="submit" className="btn-sign-in" disabled={isLoading}>
                                    {isLoading ? (
                                        <span className="spinner-container">
                                            <svg className="spinner" viewBox="0 0 50 50">
                                                <circle className="path" cx="25" cy="25" r="20" fill="none" strokeWidth="5"></circle>
                                            </svg>
                                            Procesando...
                                        </span>
                                    ) : "Sign in"}
                                </button>

                                <div className="divider">
                                    <span>Or</span>
                                </div>

                                <button type="button" className="btn-sign-other">
                                    Sign in with other
                                </button>

                                <p className="signup-link">Don't have an account? <a href="#" onClick={(e) => { e.preventDefault(); setIsLogin(false); }}>Sign Up</a></p>
                            </form>
                        </div>
                    ) : (
                        <div className="form-wrapper">
                            <h2>Sign Up</h2>
                            <p className="subtitle">Crea una cuenta para empezar a monitorear.</p>

                            <form onSubmit={handleRegisterSubmit}>
                                <div style={{ display: 'flex', gap: '10px' }}>
                                    <div className="input-group-modern" style={{ flex: 1 }}>
                                        <input
                                            type="text"
                                            name="nombres"
                                            placeholder="Nombres"
                                            value={registerForm.nombres}
                                            onChange={handleRegisterChange}
                                            required
                                        />
                                    </div>
                                    <div className="input-group-modern" style={{ flex: 1 }}>
                                        <input
                                            type="text"
                                            name="apellidos"
                                            placeholder="Apellidos"
                                            value={registerForm.apellidos}
                                            onChange={handleRegisterChange}
                                            required
                                        />
                                    </div>
                                </div>

                                <div className="input-group-modern">
                                    <span className="icon">
                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
                                    </span>
                                    <input
                                        type="email"
                                        name="email"
                                        placeholder="Correo Electrónico"
                                        value={registerForm.email}
                                        onChange={handleRegisterChange}
                                        required
                                    />
                                </div>

                                <div className="input-group-modern">
                                    <span className="icon">
                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
                                    </span>
                                    <input
                                        type={showPassword ? "text" : "password"}
                                        name="password"
                                        placeholder="Contraseña"
                                        value={registerForm.password}
                                        onChange={handleRegisterChange}
                                        required
                                    />
                                    <span className="show-pwd" onClick={() => setShowPassword(!showPassword)}>
                                        {showPassword ? "HIDE" : "SHOW"}
                                    </span>
                                </div>

                                <div className="input-group-modern">
                                    <span className="icon">
                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
                                    </span>
                                    <select name="rol" value={registerForm.rol} onChange={handleRegisterChange} required style={{ width: '100%', background: 'transparent', border: 'none', padding: '15px 0', outline: 'none' }}>
                                        <option value="ROLE_OPERADOR">Operador de Planta</option>
                                        <option value="ROLE_PROPIETARIO">Supervisor Energético</option>
                                        <option value="ROLE_ADMIN">Administrador del Sistema</option>
                                    </select>
                                </div>

                                <button type="submit" className="btn-sign-in" disabled={isLoading}>
                                    {isLoading ? (
                                        <span className="spinner-container">
                                            <svg className="spinner" viewBox="0 0 50 50">
                                                <circle className="path" cx="25" cy="25" r="20" fill="none" strokeWidth="5"></circle>
                                            </svg>
                                            Procesando...
                                        </span>
                                    ) : "Sign Up"}
                                </button>

                                <p className="signup-link">Already have an account? <a href="#" onClick={(e) => { e.preventDefault(); setIsLogin(true); }}>Sign in</a></p>
                            </form>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}