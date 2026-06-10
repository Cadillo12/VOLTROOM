import { useTelemetria } from "../context/TelemetriaContext";
import PageTitle from "../components/PageTitle";
import Loading from "../components/Loading";
import "./Monitoreo.css";

export default function MonitoreoPage() {
    const { sensores, ultimaActualizacion, loading, recargar } = useTelemetria();

    const getVoltajeColor = (voltaje) => {
        const v = parseFloat(voltaje);
        if (v < 180 || v > 240) return "bar-danger"; // Rojo
        if (v < 200 || v > 230) return "bar-warning"; // Amarillo
        return ""; // Normal (Cyber blue)
    };

    const getPowerPercent = (watts) => {
        const w = parseFloat(watts);
        // Suponiendo un máximo de 3000W para la barra
        const pct = (w / 3000) * 100;
        return Math.min(pct, 100);
    };

    const getVoltajePercent = (v) => {
        const val = parseFloat(v);
        // Rango 0-300V
        return (val / 300) * 100;
    };

    if (loading) return <Loading />;
    // Agrupar sensores por Inmueble
    const sensoresPorInmueble = sensores.reduce((acc, sensor) => {
        const key = sensor.inmuebleNombre || "Otros";
        if (!acc[key]) acc[key] = [];
        acc[key].push(sensor);
        return acc;
    }, {});

    return (
        <div className="dashboard-container">
            <div className="monitoreo-container">
                <div className="monitoreo-header">
                    <div className="d-flex justify-content-between align-items-center">
                        <div>
                            <h2>Monitoreo en Vivo</h2>
                            <p className="text-secondary mb-0">Visualización de flujo eléctrico por nodo de telemetría</p>
                        </div>
                        <div className="telemetria-status-bar">
                            <div className="live-indicator">
                                <div className="pulse-dot"></div>
                                <div>
                                    <span className="status-label">Telemetria Activa</span>
                                    <small>Sync: {ultimaActualizacion?.toLocaleTimeString("es-PE") || "-"}</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Secciones por Inmueble */}
                {Object.keys(sensoresPorInmueble).map(inmueble => (
                    <div key={inmueble} className="inmueble-section mb-5">
                        <div className="inmueble-header">
                            <span className="inmueble-icon">🏢</span>
                            <h4 className="inmueble-title">{inmueble}</h4>
                            <span className="inmueble-count">{sensoresPorInmueble[inmueble].length} Nodos</span>
                        </div>

                        <div className="monitoreo-grid">
                            {sensoresPorInmueble[inmueble].map((s) => (
                                <div key={s.sensorId} className="sensor-monitor-card">
                                    <div className="card-header-main">
                                        <div className="sensor-info-header">
                                            <span className="sensor-id-label">{s.codigoSensor}</span>
                                            <span className="sensor-loc-label">{s.ambienteNombre}</span>
                                        </div>
                                        <span className={`status-badge-live status-${s.estado.toLowerCase()}`}>
                                            {s.estado}
                                        </span>
                                    </div>

                                    <div className="telemetria-visual">
                                        <div className="radar-container">
                                            <div className="radar-rings"></div>
                                            <div className="radar-sweep"></div>
                                            <div className="radar-dot"></div>
                                        </div>
                                        <div className="visual-power-display">
                                            <span className="power-value">
                                                {parseFloat(s.potenciaW || 0).toFixed(1)}
                                                <small className="power-unit">W</small>
                                            </span>
                                        </div>
                                    </div>

                                    <div className="telemetria-display">
                                        <div className="data-row">
                                            <span className="data-label">Voltaje</span>
                                            <span className="data-value" style={{ color: getVoltajeColor(s.voltaje) ? '#f43f5e' : '#38bdf8' }}>
                                                {parseFloat(s.voltaje || 0).toFixed(1)}V
                                            </span>
                                        </div>
                                        <div className="data-row">
                                            <span className="data-label">Corriente</span>
                                            <span className="data-value">
                                                {parseFloat(s.amperaje || 0).toFixed(2)}A
                                            </span>
                                        </div>
                                        <div className="level-bar mt-2">
                                            <div
                                                className={`level-bar-fill ${parseFloat(s.potenciaW) > 2500 ? 'bg-danger' : ''}`}
                                                style={{ width: `${getPowerPercent(s.potenciaW)}%` }}
                                            ></div>
                                        </div>
                                    </div>

                                    <div className="card-footer-mini">
                                        <div className="footer-item">
                                            <span>CONSUMO MES</span>
                                            <strong>{parseFloat(s.kwhConsumoMes || 0).toFixed(3)} kWh</strong>
                                        </div>
                                        <div className="footer-item text-end">
                                            <span>COSTO EST.</span>
                                            <strong className="text-info">S/ {parseFloat(s.costoEstimadoMes || 0).toFixed(2)}</strong>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}

                {/* Detailed Table View at bottom */}
                <div className="telemetria-detail-card">
                    <div className="p-3 border-bottom d-flex justify-content-between align-items-center">
                        <h5 className="mb-0 fw-bold">Registro de Simulación en Tiempo Real</h5>
                        <button className="btn btn-sm btn-outline-info" onClick={recargar}>
                            Reiniciar desde DB
                        </button>
                    </div>
                    <div className="table-responsive">
                        <table className="table table-telemetria mb-0">
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Ubicación</th>
                                    <th className="text-center">Potencia</th>
                                    <th className="text-center">KWh Mes</th>
                                    <th className="text-center">Costo Est.</th>
                                    <th>Estado</th>
                                    <th>Sincronización</th>
                                </tr>
                            </thead>
                            <tbody>
                                {sensores.map((s) => (
                                    <tr key={s.sensorId}>
                                        <td className="fw-bold">{s.codigoSensor}</td>
                                        <td>{s.ambienteNombre}</td>
                                        <td className="text-center fw-bold text-info">{parseFloat(s.potenciaW || 0).toFixed(1)} W</td>
                                        <td className="text-center">{parseFloat(s.kwhConsumoMes || 0).toFixed(4)}</td>
                                        <td className="text-center fw-bold text-primary">S/ {parseFloat(s.costoEstimadoMes || 0).toFixed(2)}</td>
                                        <td>
                                            <span className={`badge status-${s.estado}`}>
                                                {s.estado}
                                            </span>
                                        </td>
                                        <td className="text-muted small">
                                            {s.ultimaLectura ? new Date(s.ultimaLectura).toLocaleTimeString("es-PE") : "—"}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
}