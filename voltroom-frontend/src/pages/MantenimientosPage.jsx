import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageTitle from "../components/PageTitle";
import Loading from "../components/Loading";
import { formatDate } from "../utils/format";

const initialForm = {
    incidenciaId: "",
    tecnicoId: 2,
    titulo: "",
    detalle: "",
    fechaProgramada: "",
    costo: 0,
    creadoPor: 1,
};

export default function MantenimientosPage() {
    const [items, setItems] = useState([]);
    const [incidencias, setIncidencias] = useState([]);
    const [form, setForm] = useState(initialForm);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        cargarTodo();
    }, []);

    const cargarTodo = async () => {
        setLoading(true);
        try {
            const [manRes, incRes] = await Promise.all([
                axiosClient.get("/mantenimientos"),
                axiosClient.get("/incidencias/abiertas"),
            ]);
            setItems(manRes.data);
            setIncidencias(incRes.data);
        } catch (error) {
            console.error("Error cargando mantenimientos:", error);
        } finally {
            setLoading(false);
        }
    };

    const guardar = async (e) => {
        e.preventDefault();
        await axiosClient.post("/mantenimientos", {
            ...form,
            incidenciaId: form.incidenciaId ? Number(form.incidenciaId) : null,
            tecnicoId: Number(form.tecnicoId),
            creadoPor: Number(form.creadoPor),
            costo: Number(form.costo || 0),
        });
        setForm(initialForm);
        cargarTodo();
    };

    const iniciar = async (id) => {
        await axiosClient.put(`/mantenimientos/${id}/iniciar`);
        cargarTodo();
    };

    const finalizar = async (id) => {
        await axiosClient.put(`/mantenimientos/${id}/finalizar`, {
            resultado: "Trabajo finalizado correctamente",
        });
        cargarTodo();
    };

    if (loading) return <Loading />;

    return (
        <div className="dashboard-container">
            <PageTitle title="Mantenimientos" subtitle="Programación y seguimiento de mantenimiento" />

            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <form onSubmit={guardar}>
                        <div className="row g-3">
                            <div className="col-md-3">
                                <label className="form-label">Incidencia</label>
                                <select
                                    className="form-select"
                                    value={form.incidenciaId}
                                    onChange={(e) => setForm({ ...form, incidenciaId: e.target.value })}
                                >
                                    <option value="">Seleccione</option>
                                    {incidencias.map((i) => (
                                        <option key={i.id} value={i.id}>
                                            {i.titulo}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="col-md-3">
                                <label className="form-label">Título</label>
                                <input
                                    className="form-control"
                                    value={form.titulo}
                                    onChange={(e) => setForm({ ...form, titulo: e.target.value })}
                                />
                            </div>

                            <div className="col-md-3">
                                <label className="form-label">Fecha programada</label>
                                <input
                                    type="datetime-local"
                                    className="form-control"
                                    value={form.fechaProgramada}
                                    onChange={(e) => setForm({ ...form, fechaProgramada: e.target.value })}
                                />
                            </div>

                            <div className="col-md-3">
                                <label className="form-label">Costo</label>
                                <input
                                    type="number"
                                    className="form-control"
                                    value={form.costo}
                                    onChange={(e) => setForm({ ...form, costo: e.target.value })}
                                />
                            </div>

                            <div className="col-md-12">
                                <label className="form-label">Detalle</label>
                                <textarea
                                    className="form-control"
                                    rows="3"
                                    value={form.detalle}
                                    onChange={(e) => setForm({ ...form, detalle: e.target.value })}
                                />
                            </div>
                        </div>

                        <button className="btn btn-primary mt-3">Crear mantenimiento</button>
                    </form>
                </div>
            </div>

            <div className="card shadow-sm">
                <div className="card-body">
                    <table className="table table-hover align-middle">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Título</th>
                            <th>Estado</th>
                            <th>Fecha programada</th>
                            <th>Técnico</th>
                            <th>Resultado</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        {items.map((x) => (
                            <tr key={x.id}>
                                <td>{x.id}</td>
                                <td>{x.titulo}</td>
                                <td>{x.estado}</td>
                                <td>{formatDate(x.fechaProgramada)}</td>
                                <td>{x.tecnico?.nombres || "-"}</td>
                                <td>{x.resultado || "-"}</td>
                                <td className="d-flex gap-2">
                                    <button className="btn btn-sm btn-warning" onClick={() => iniciar(x.id)}>
                                        Iniciar
                                    </button>
                                    <button className="btn btn-sm btn-success" onClick={() => finalizar(x.id)}>
                                        Finalizar
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
            </div>
    );
}