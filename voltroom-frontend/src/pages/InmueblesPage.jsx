import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageHeader from "../components/PageHeader";
import { toast } from "react-toastify";

export default function InmueblesPage() {
    const [items, setItems] = useState([]);
    const [editingId, setEditingId] = useState(null);

    const initialForm = {
        nombre: "",
        direccion: "",
        descripcion: "",
        activo: true,
    };

    const [form, setForm] = useState(initialForm);

    const cargar = async () => {
        const { data } = await axiosClient.get("/inmuebles");
        setItems(data);
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        cargar().then((data) => {
            // Maneja el resultado aquí
            console.log(data);
        });
    }, []);

    const guardar = async (e) => {
        e.preventDefault();

        if (editingId) {
            await axiosClient.put(`/inmuebles/${editingId}`, form);
        } else {
            await axiosClient.post("/inmuebles", form);
        }

        setForm(initialForm);
        setEditingId(null);
        cargar();
    };

    const editar = (item) => {
        setEditingId(item.id);
        setForm({
            nombre: item.nombre || "",
            direccion: item.direccion || "",
            descripcion: item.descripcion || "",
            activo: item.activo ?? true,
        });
    };

    const eliminar = async (id) => {
        if (!confirm("¿Eliminar inmueble?")) return;
        try {
            await axiosClient.delete(`/inmuebles/${id}`);
            cargar();
            toast.success("Inmueble eliminado correctamente");
        } catch (err) {
            toast.error(err?.response?.data?.message || "No se pudo eliminar el inmueble porque tiene ambientes asociados.");
        }
    };

    return (
        <div className="dashboard-container">
            <PageHeader title="Inmuebles" subtitle="Gestión de inmuebles del sistema" />

            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <form onSubmit={guardar}>
                        <div className="row g-3">
                            <div className="col-md-4">
                                <label className="form-label">Nombre</label>
                                <input
                                    className="form-control"
                                    value={form.nombre}
                                    onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                                />
                            </div>

                            <div className="col-md-4">
                                <label className="form-label">Dirección</label>
                                <input
                                    className="form-control"
                                    value={form.direccion}
                                    onChange={(e) => setForm({ ...form, direccion: e.target.value })}
                                />
                            </div>

                            <div className="col-md-4">
                                <label className="form-label">Descripción</label>
                                <input
                                    className="form-control"
                                    value={form.descripcion}
                                    onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
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
                            <th>Nombre</th>
                            <th>Dirección</th>
                            <th>Descripción</th>
                            <th>Activo</th>
                            <th style={{ width: 160 }}>Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        {items.map((x) => (
                            <tr key={x.id}>
                                <td>{x.id}</td>
                                <td>{x.nombre}</td>
                                <td>{x.direccion}</td>
                                <td>{x.descripcion}</td>
                                <td>{x.activo ? "Sí" : "No"}</td>
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
                                <td colSpan="6" className="text-center">Sin registros</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
            </div>
    );
}