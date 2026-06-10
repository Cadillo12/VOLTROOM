import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import { useTelemetria } from "../context/TelemetriaContext";
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import "./Dashboard.css";

export default function DashboardPage() {
    const [dashboard, setDashboard] = useState(null);
    const [alertas, setAlertas] = useState([]);
    const [loadingDash, setLoadingDash] = useState(true);
    const { sensores, ultimaActualizacion, loading: loadingTele } = useTelemetria();

    const cargarDashboardData = async () => {
        try {
            const [dashRes, alertRes] = await Promise.all([
                axiosClient.get("/dashboard"),
                axiosClient.get("/alertas"),
            ]);
            setDashboard(dashRes.data);
            setAlertas(alertRes.data);
        } catch (err) {
            console.error("Error cargando dashboard:", err);
        } finally {
            setLoadingDash(false);
        }
    };

    useEffect(() => {
        cargarDashboardData();
        
        let brokerURL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";
        brokerURL = brokerURL.replace('/api', '/ws-telemetria');

        const stompClient = new Client({
            webSocketFactory: () => new SockJS(brokerURL),
            reconnectDelay: 5000,
            onConnect: () => {
                stompClient.subscribe('/topic/alertas', (message) => {
                    if (message.body) {
                        const nuevaAlerta = JSON.parse(message.body);
                        setAlertas(prev => [nuevaAlerta, ...prev].slice(0, 20)); // Mantener últimas 20
                        // Recargar estadísticas del dashboard silenciosamente
                        axiosClient.get("/dashboard").then(res => setDashboard(res.data)).catch(()=>{});
                    }
                });
            }
        });

        stompClient.activate();

        return () => {
            stompClient.deactivate();
        };
    }, []);

    if (loadingDash || loadingTele || !dashboard) {
        return (
            <div className="dash-loading">
                <div className="spinner-border text-primary" role="status"></div>
                <p>Cargando panel de control...</p>
            </div>
        );
    }

    // Computed values from global sensors simulation
    const sensoresOperativos = sensores.filter(s => s.estado === "OPERATIVO").length;
    const sensoresFalla = sensores.filter(s => s.estado === "FALLA" || s.estado === "CRITICO").length;
    const sensoresInactivos = sensores.filter(s => s.estado === "INACTIVO").length;
    const alertasPendientes = alertas.filter(a => a.estado === "PENDIENTE" || a.estado === "ACTIVA").length;
    
    // Usamos los datos simulados en vivo para los totales
    const totalConsumo = sensores.reduce((s, x) => s + parseFloat(x.kwhConsumoMes || 0), 0);
    const totalCosto = sensores.reduce((s, x) => s + parseFloat(x.costoEstimadoMes || 0), 0);

    const getVoltajeColor = (v) => {
        const val = parseFloat(v);
        if (val < 180 || val > 240) return "#ef4444";
        if (val < 200 || val > 230) return "#f59e0b";
        return "#22c55e";
    };

    const getEstadoClass = (estado) => {
        const map = {
            OPERATIVO: "estado-operativo",
            INACTIVO: "estado-inactivo",
            FALLA: "estado-falla",
            CRITICO: "estado-critico",
            MANTENIMIENTO: "estado-mantenimiento",
        };
        return map[estado] || "estado-inactivo";
    };

    const descargarReporteGlobal = async () => {
        try {
            const response = await axiosClient.get("/reportes/general/excel", {
                responseType: 'blob'
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'reporte_general_voltroom.xlsx');
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
        } catch (error) {
            console.error("Error al descargar el Excel", error);
            alert("No se pudo descargar el reporte general.");
        }
    };

    return (
        <div className="dashboard-container">
            {/* Top Bar Header */}
            <div className="pa-header">
                <div className="pa-header-left">
                    <div className="pa-breadcrumb"><span className="home-icon">🏠</span> Home / <strong>Dashboard</strong></div>
                    <h1 className="pa-title">Dashboard</h1>
                    <p className="pa-subtitle">Welcome back {localStorage.getItem("userEmail")?.split('@')[0] || "User"}</p>
                </div>
                <div className="pa-header-right">
                    <div className="pa-header-stat">
                        <span className="stat-label">NEW ALERTS</span>
                        <span className="stat-val">{alertasPendientes}</span>
                    </div>
                    <div className="pa-header-stat">
                        <span className="stat-label">SYSTEM STATUS</span>
                        <div className="pa-live-indicator">
                            <span className="live-dot"></span>
                            <span className="live-text">ONLINE</span>
                        </div>
                    </div>
                    <button className="pa-btn-report" onClick={descargarReporteGlobal}>
                        📊 Report
                    </button>
                </div>
            </div>

            {/* Icon Bar */}
            <div className="pa-icon-bar">
                <div className="icon-item active">
                    <span className="icon">📊</span> Statistic
                </div>
                <div className="icon-item">
                    <span className="icon">🏢</span> Properties <span className="badge">{dashboard.totalInmuebles}</span>
                </div>
                <div className="icon-item">
                    <span className="icon">📡</span> Sensors
                </div>
                <div className="icon-item">
                    <span className="icon">🔔</span> Notification <span className="badge red">{alertasPendientes}</span>
                </div>
                <div className="icon-item">
                    <span className="icon">⚙️</span> Setting
                </div>
            </div>

            {/* 4 Stat Cards Row */}
            <div className="pa-stat-cards">
                <div className="pa-card card-red">
                    <div className="card-content">
                        <h2>{dashboard.totalSensores}</h2>
                        <p>Active Sensors</p>
                        <div className="card-footer">
                            <span><i className="arrow-up">↑</i> {sensoresOperativos} Operational</span>
                            <i className="bg-icon">📡</i>
                        </div>
                    </div>
                </div>

                <div className="pa-card card-teal">
                    <div className="card-content">
                        <h2>{totalConsumo.toFixed(1)} kWh</h2>
                        <p>Total Flow</p>
                        <div className="card-footer">
                            <span><i className="arrow-up">↑</i> Live Sync</span>
                            <i className="bg-icon">⚡</i>
                        </div>
                    </div>
                </div>

                <div className="pa-card card-yellow">
                    <div className="card-content">
                        <h2>{alertas.length}</h2>
                        <p>Total Events</p>
                        <div className="card-footer">
                            <span><i className="arrow-up">↑</i> {alertasPendientes} Pending</span>
                            <i className="bg-icon">🔔</i>
                        </div>
                    </div>
                </div>

                <div className="pa-card card-green">
                    <div className="card-content">
                        <h2>S/ {totalCosto.toFixed(0)}</h2>
                        <p>Est. Balance</p>
                        <div className="card-footer">
                            <span><i className="arrow-up">↑</i> Current Month</span>
                            <i className="bg-icon">💰</i>
                        </div>
                    </div>
                </div>
            </div>

            {/* Main Content Area */}
            <div className="pa-main-content">
                {/* Left Chart */}
                <div className="pa-chart-section">
                    <div className="pa-panel-header">
                        <h3>📊 Energy Traffic</h3>
                        <button className="pa-refresh">↻</button>
                    </div>
                    <div className="pa-chart-container" style={{ height: '350px' }}>
                        <ResponsiveContainer width="100%" height="100%">
                            <AreaChart data={sensores.slice(0, 10)} margin={{ top: 20, right: 30, left: 0, bottom: 0 }}>
                                <defs>
                                    <linearGradient id="colorConsumo" x1="0" y1="0" x2="0" y2="1">
                                        <stop offset="5%" stopColor="#55efc4" stopOpacity={0.4}/>
                                        <stop offset="95%" stopColor="#55efc4" stopOpacity={0}/>
                                    </linearGradient>
                                </defs>
                                <XAxis dataKey="codigoSensor" stroke="#a0b2c0" fontSize={11} tickLine={false} axisLine={false} />
                                <YAxis stroke="#a0b2c0" fontSize={11} tickLine={false} axisLine={false} />
                                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e0e0e0" />
                                <Tooltip 
                                    contentStyle={{ background: '#fff', borderRadius: '4px', border: '1px solid #e0e0e0', boxShadow: '0 4px 10px rgba(0,0,0,0.1)' }}
                                    itemStyle={{ color: '#2d3436', fontWeight: 600 }}
                                />
                                <Area type="monotone" dataKey="kwhConsumoMes" stroke="#55efc4" strokeWidth={3} fillOpacity={1} fill="url(#colorConsumo)" />
                            </AreaChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Right Blocks */}
                <div className="pa-right-blocks">
                    {/* Dark Blue Block */}
                    <div className="pa-block block-darkblue">
                        <h3>Recent Alerts</h3>
                        <div className="pa-alerts-mini">
                            {alertas.slice(0, 4).map((a, i) => (
                                <div key={i} className="mini-alert">
                                    <span className="dot" style={{ background: a.nivel === 'CRITICO' ? '#ff7675' : '#fdcb6e' }}></span>
                                    <div className="alert-text">
                                        <span className="msg">{a.mensaje}</span>
                                        <span className="time">{a.fechaCreacion ? new Date(a.fechaCreacion).toLocaleTimeString("es-PE") : "—"}</span>
                                    </div>
                                </div>
                            ))}
                            {alertas.length === 0 && <p className="text-muted text-center mt-3">No recent alerts</p>}
                        </div>
                    </div>
                    
                    {/* Green Block */}
                    <div className="pa-block block-green">
                        <h3>Sensor Nodes Summary</h3>
                        <div className="pa-node-stats">
                            <div className="node-stat-item">
                                <span className="val">{sensoresOperativos}</span>
                                <span className="lbl">Operational</span>
                            </div>
                            <div className="node-stat-item">
                                <span className="val">{sensoresFalla}</span>
                                <span className="lbl">Critical/Fail</span>
                            </div>
                            <div className="node-stat-item">
                                <span className="val">{sensoresInactivos}</span>
                                <span className="lbl">Inactive</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}