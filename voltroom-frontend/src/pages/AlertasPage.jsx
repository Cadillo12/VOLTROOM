import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageTitle from "../components/PageTitle";
import Loading from "../components/Loading";
import { formatDate } from "../utils/format";

export default function AlertasPage() {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    useEffect(() => {
        cargarTodo();
    }, []);

    const cargarTodo = async () => {
        setLoading(true);
        try {
            const { data } = await axiosClient.get("/alertas");
            setItems(Array.isArray(data) ? data : []);
            setError("");
        } catch (error) {
            console.error("Error cargando alertas:", error);
            setError("Error al cargar el historial de alertas.");
        } finally {
            setLoading(false);
        }
    };

    const atender = async (id) => {
        setError("");
        setSuccess("");
        try {
            await axiosClient.put(`/alertas/${id}/atender`, { atendidaPor: 1 });
            setSuccess("Alerta atendida y eliminada correctamente.");
            await cargarTodo();
        } catch (err) {
            console.error("Error al atender alerta:", err);
            setError("Error al procesar la alerta.");
        }
    };

    if (loading && items.length === 0) return <Loading />;

    return (
        <div className="dashboard-container">
            <PageTitle title="Historial de Alertas" subtitle="Monitoreo de alertas automáticas del sistema" />

            {error && (
                <div className="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                    <i className="bi bi-exclamation-triangle-fill me-2"></i>
                    {error}
                    <button type="button" className="btn-close" onClick={() => setError("")}></button>
                </div>
            )}
            {success && (
                <div className="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                    <i className="bi bi-check-circle-fill me-2"></i>
                    {success}
                    <button type="button" className="btn-close" onClick={() => setSuccess("")}></button>
                </div>
            )}

            <div className="alert alert-info shadow-sm mb-4">
                <i className="bi bi-info-circle-fill me-2"></i>
                Las alertas se generan <strong>automáticamente</strong> por el sistema de telemetría cuando los sensores detectan anomalías (ej. variaciones críticas de voltaje o cortes de suministro).
            </div>

            <div className="card shadow-sm border-0">
                <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">Alertas Detectadas</h5>
                    <button className="btn btn-sm btn-light" onClick={cargarTodo}>
                        <i className="bi bi-arrow-clockwise me-1"></i> Actualizar
                    </button>
                </div>
                <div className="card-body p-0">
                    <div className="table-responsive">
                        <table className="table table-hover align-middle mb-0">
                            <thead className="table-light">
                            <tr>
                                <th className="px-3">ID</th>
                                <th>Fecha y Hora</th>
                                <th>Título</th>
                                <th>Tipo</th>
                                <th>Gravedad</th>
                                <th>Ambiente</th>
                                <th>Sensor</th>
                                <th>Estado</th>
                                <th className="text-end px-3">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            {items.length === 0 ? (
                                <tr>
                                    <td colSpan="9" className="text-center py-5 text-muted">
                                        No hay alertas registradas en el sistema. Todo está funcionando correctamente.
                                    </td>
                                </tr>
                            ) : (
                                items.map((x) => (
                                    <tr key={x.id}>
                                        <td className="px-3 text-muted">#{x.id}</td>
                                        <td>{formatDate(x.fechaHora)}</td>
                                        <td className="fw-bold">{x.titulo}</td>
                                        <td>{x.tipoAlerta}</td>
                                        <td>
                                            <span className={`badge ${
                                                x.nivel === 'CRITICO' ? 'bg-danger' :
                                                x.nivel === 'ALTO' ? 'bg-warning text-dark' :
                                                x.nivel === 'MEDIO' ? 'bg-info text-dark' : 'bg-success'
                                            }`}>
                                                {x.nivel}
                                            </span>
                                        </td>
                                        <td>{x.ambiente?.nombre || "-"}</td>
                                        <td>{x.sensor?.codigo || "-"}</td>
                                        <td>
                                            <span className={`badge ${x.atendida ? 'bg-secondary' : 'bg-danger'}`}>
                                                {x.atendida ? "Atendida" : "Pendiente"}
                                            </span>
                                        </td>
                                        <td className="text-end px-3">
                                            {!x.atendida && (
                                                <button className="btn btn-sm btn-success" onClick={() => atender(x.id)}>
                                                    Atender y Eliminar
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            </div>
    );
}