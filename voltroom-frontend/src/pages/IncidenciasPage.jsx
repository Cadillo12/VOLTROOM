import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageTitle from "../components/PageTitle";
import Loading from "../components/Loading";
import { formatDate } from "../utils/format";

const initialForm = {
    ambienteId: "",
    sensorId: "",
    reportadoPor: 1,
    titulo: "",
    descripcion: "",
    prioridad: "MEDIA",
};

export function IncidenciasPage() {
    const [items, setItems] = useState([]);
    const [ambientes, setAmbientes] = useState([]);
    const [sensores, setSensores] = useState([]);
    const [form, setForm] = useState(initialForm);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        cargarTodo();
    }, []);

    const cargarTodo = async () => {
        setLoading(true);
        try {
            const [incRes, ambRes, senRes] = await Promise.all([
                axiosClient.get("/incidencias"),
                axiosClient.get("/ambientes"),
                axiosClient.get("/sensores"),
            ]);
            setItems(incRes.data);
            setAmbientes(ambRes.data);
            setSensores(senRes.data);
        } catch (error) {
            console.error("Error cargando incidencias:", error);
        } finally {
            setLoading(false);
        }
    };

    const guardar = async (e) => {
        e.preventDefault();
        await axiosClient.post("/incidencias", {
            ...form,
            ambienteId: form.ambienteId ? Number(form.ambienteId) : null,
            sensorId: form.sensorId ? Number(form.sensorId) : null,
            reportadoPor: Number(form.reportadoPor),
        });
        setForm(initialForm);
        cargarTodo();
    };

    const cambiarEstado = async (id, estado) => {
        await axiosClient.put(`/incidencias/${id}/estado`, {estado});
        cargarTodo();
    };

    const cerrar = async (id) => {
        await axiosClient.put(`/incidencias/${id}/cerrar`, {
            observacionCierre: "Incidencia resuelta desde frontend",
        });
        cargarTodo();
    };

    if (loading) return <Loading/>;

    return (
        <div className="dashboard-container">
            <PageTitle title="Incidencias" subtitle="Registro y seguimiento de incidencias"/>

            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <form onSubmit={guardar}>
                        <div className="row g-3">
                            <div className="col-md-3">
                                <label className="form-label">Ambiente</label>
                                <select
                                    className="form-select"
                                    value={form.ambienteId}
                                    onChange={(e) => setForm({...form, ambienteId: e.target.value})}
                                >
                                    <option value="">Seleccione</option>
                                    {ambientes.map((a) => (
                                        <option key={a.id} value={a.id}>{a.nombre}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="col-md-3">
                                <label className="form-label">Sensor</label>
                                <select
                                    className="form-select"
                                    value={form.sensorId}
                                    onChange={(e) => setForm({...form, sensorId: e.target.value})}
                                >
                                    <option value="">Seleccione</option>
                                    {sensores.map((s) => (
                                        <option key={s.id} value={s.id}>{s.codigo}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Prioridad</label>
                                <select
                                    className="form-select"
                                    value={form.prioridad}
                                    onChange={(e) => setForm({...form, prioridad: e.target.value})}
                                >
                                    <option value="BAJA">BAJA</option>
                                    <option value="MEDIA">MEDIA</option>
                                    <option value="ALTA">ALTA</option>
                                    <option value="CRITICA">CRITICA</option>
                                </select>
                            </div>

                            <div className="col-md-4">
                                <label className="form-label">Título</label>
                                <input
                                    className="form-control"
                                    value={form.titulo}
                                    onChange={(e) => setForm({...form, titulo: e.target.value})}
                                />
                            </div>

                            <div className="col-md-12">
                                <label className="form-label">Descripción</label>
                                <textarea
                                    className="form-control"
                                    rows="3"
                                    value={form.descripcion}
                                    onChange={(e) => setForm({...form, descripcion: e.target.value})}
                                />
                            </div>
                        </div>

                        <button className="btn btn-primary mt-3">Registrar incidencia</button>
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
                            <th>Prioridad</th>
                            <th>Estado</th>
                            <th>Ambiente</th>
                            <th>Sensor</th>
                            <th>Fecha</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        {items.map((x) => (
                            <tr key={x.id}>
                                <td>{x.id}</td>
                                <td>{x.titulo}</td>
                                <td>{x.prioridad}</td>
                                <td>{x.estado}</td>
                                <td>{x.ambiente?.nombre || "-"}</td>
                                <td>{x.sensor?.codigo || "-"}</td>
                                <td>{formatDate(x.fechaReporte)}</td>
                                <td className="d-flex gap-2">
                                    <button className="btn btn-sm btn-warning"
                                            onClick={() => cambiarEstado(x.id, "EN_PROCESO")}>
                                        En proceso
                                    </button>
                                    <button className="btn btn-sm btn-success" onClick={() => cerrar(x.id)}>
                                        Cerrar
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