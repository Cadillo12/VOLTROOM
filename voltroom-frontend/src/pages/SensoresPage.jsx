import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageHeader from "../components/PageHeader";
import { toast } from "react-toastify";

export default function SensoresPage() {
    const [items, setItems] = useState([]);
    const [ambientes, setAmbientes] = useState([]);
    const [editingId, setEditingId] = useState(null);

    const initialForm = {
        ambienteId: "",
        codigo: "",
        tipoSensor: "",
        unidadMedida: "kWh",
        estado: "OPERATIVO",
        fechaInstalacion: "",
    };

    const [form, setForm] = useState(initialForm);

    const cargar = async () => {
        const { data } = await axiosClient.get("/sensores");
        setItems(data);
    };

    const cargarAmbientes = async () => {
        const { data } = await axiosClient.get("/ambientes");
        setAmbientes(data);
    };

    useEffect(() => {
        const init = async () => {
            await cargar();
            await cargarAmbientes();
        };

        init();
    }, []);

    const guardar = async (e) => {
        e.preventDefault();

        const payload = {
            ...form,
            ambienteId: Number(form.ambienteId),
        };

        if (editingId) {
            await axiosClient.put(`/sensores/${editingId}`, payload);
        } else {
            await axiosClient.post("/sensores", payload);
        }

        setForm(initialForm);
        setEditingId(null);
        cargar();
    };

    const editar = (item) => {
        setEditingId(item.id);
        setForm({
            ambienteId: item.ambiente?.id || "",
            codigo: item.codigo || "",
            tipoSensor: item.tipoSensor || "",
            unidadMedida: item.unidadMedida || "kWh",
            estado: item.estado || "OPERATIVO",
            fechaInstalacion: item.fechaInstalacion || "",
        });
    };

    const eliminar = async (id) => {
        if (!confirm("¿Eliminar sensor?")) return;
        try {
            await axiosClient.delete(`/sensores/${id}`);
            cargar();
            toast.success("Sensor eliminado correctamente");
        } catch (err) {
            toast.error(err?.response?.data?.message || "No se pudo eliminar el sensor porque tiene lecturas o alertas asociadas.");
        }
    };

    return (
        <div className="dashboard-container">
            <PageHeader title="Sensores" subtitle="Gestión de sensores por ambiente" />

            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <form onSubmit={guardar}>
                        <div className="row g-3">
                            <div className="col-md-3">
                                <label className="form-label">Ambiente</label>
                                <select
                                    className="form-select"
                                    value={form.ambienteId}
                                    onChange={(e) => setForm({ ...form, ambienteId: e.target.value })}
                                >
                                    <option value="">Seleccione</option>
                                    {ambientes.map((a) => (
                                        <option key={a.id} value={a.id}>{a.nombre}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Código</label>
                                <input
                                    className="form-control"
                                    value={form.codigo}
                                    onChange={(e) => setForm({ ...form, codigo: e.target.value })}
                                />
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Tipo</label>
                                <input
                                    className="form-control"
                                    value={form.tipoSensor}
                                    onChange={(e) => setForm({ ...form, tipoSensor: e.target.value })}
                                />
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Unidad</label>
                                <input
                                    className="form-control"
                                    value={form.unidadMedida}
                                    onChange={(e) => setForm({ ...form, unidadMedida: e.target.value })}
                                />
                            </div>

                            <div className="col-md-1">
                                <label className="form-label">Estado</label>
                                <select
                                    className="form-select"
                                    value={form.estado}
                                    onChange={(e) => setForm({ ...form, estado: e.target.value })}
                                >
                                    <option value="OPERATIVO">OPERATIVO</option>
                                    <option value="INACTIVO">INACTIVO</option>
                                    <option value="FALLA">FALLA</option>
                                    <option value="MANTENIMIENTO">MANTENIMIENTO</option>
                                </select>
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Fecha instalación</label>
                                <input
                                    type="date"
                                    className="form-control"
                                    value={form.fechaInstalacion}
                                    onChange={(e) =>
                                        setForm({ ...form, fechaInstalacion: e.target.value })
                                    }
                                />
                            </div>
                        </div>

                        <div className="mt-3 d-flex gap-2">
                            <button className="btn btn-primary">
                                {editingId ? "Actualizar" : "Guardar"}
                            </button>
                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={() => {
                                    setForm(initialForm);
                                    setEditingId(null);
                                }}
                            >
                                Limpiar
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <div className="card shadow-sm">
                <div className="card-body">
                    <table className="table table-bordered table-hover">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Ambiente</th>
                            <th>Código</th>
                            <th>Tipo</th>
                            <th>Unidad</th>
                            <th>Estado</th>
                            <th>Fecha</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        {items.map((x) => (
                            <tr key={x.id}>
                                <td>{x.id}</td>
                                <td>{x.ambiente?.nombre}</td>
                                <td>{x.codigo}</td>
                                <td>{x.tipoSensor}</td>
                                <td>{x.unidadMedida}</td>
                                <td>{x.estado}</td>
                                <td>{x.fechaInstalacion}</td>
                                <td>
                                    <div className="d-flex gap-2">
                                        <button className="btn btn-warning btn-sm" onClick={() => editar(x)}>
                                            Editar
                                        </button>
                                        <button className="btn btn-danger btn-sm" onClick={() => eliminar(x.id)}>
                                            Eliminar
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        {items.length === 0 && (
                            <tr>
                                <td colSpan="8" className="text-center">Sin registros</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
            </div>
    );
}