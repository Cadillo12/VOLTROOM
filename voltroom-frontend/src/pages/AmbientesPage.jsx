import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageHeader from "../components/PageHeader";
import Loading from "../components/Loading";

export default function AmbientesPage() {
    const [items, setItems] = useState([]);
    const [inmuebles, setInmuebles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const initialForm = {
        inmuebleId: "",
        nombre: "",
        tipo: "",
        piso: 1,
        estado: "ACTIVO",
        descripcion: "",
    };

    const [form, setForm] = useState(initialForm);

    const cargar = async () => {
        setLoading(true);
        try {
            const [ambRes, inmRes] = await Promise.all([
                axiosClient.get("/ambientes"),
                axiosClient.get("/inmuebles"),
            ]);
            setItems(Array.isArray(ambRes.data) ? ambRes.data : []);
            setInmuebles(Array.isArray(inmRes.data) ? inmRes.data : []);
            setError("");
        } catch (err) {
            console.error("Error cargando ambientes:", err);
            setError("No se pudieron cargar los datos. Verifique que el backend esté corriendo.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargar();
    }, []);

    const guardar = async (e) => {
        if (e) e.preventDefault();
        setError("");
        setSuccess("");

        if (!form.inmuebleId) {
            setError("Debe seleccionar un inmueble.");
            return;
        }
        if (!form.nombre.trim()) {
            setError("El nombre es obligatorio.");
            return;
        }

        setSaving(true);
        const payload = {
            inmuebleId: Number(form.inmuebleId),
            nombre: form.nombre.trim(),
            tipo: form.tipo || "",
            piso: form.piso ? Number(form.piso) : 0,
            estado: form.estado || "ACTIVO",
            descripcion: form.descripcion || "",
        };

        try {
            if (editingId) {
                await axiosClient.put(`/ambientes/${editingId}`, payload);
                setSuccess("Ambiente actualizado correctamente.");
            } else {
                await axiosClient.post("/ambientes", payload);
                setSuccess("Ambiente registrado correctamente.");
            }
            setForm(initialForm);
            setEditingId(null);
            await cargar();
        } catch (err) {
            console.error("Error al guardar:", err);
            const msg = err.response?.data?.message || err.response?.data?.error || "Error al conectar con el servidor.";
            setError(msg);
        } finally {
            setSaving(false);
        }
    };

    const editar = (item) => {
        setEditingId(item.id);
        setError("");
        setSuccess("");
        setForm({
            inmuebleId: item.inmueble?.id || "",
            nombre: item.nombre || "",
            tipo: item.tipo || "",
            piso: item.piso || 1,
            estado: item.estado || "ACTIVO",
            descripcion: item.descripcion || "",
        });
        window.scrollTo(0, 0);
    };

    const eliminar = async (id) => {
        if (!confirm("¿Está seguro de eliminar este ambiente?")) return;
        setError("");
        setSuccess("");
        try {
            await axiosClient.delete(`/ambientes/${id}`);
            setSuccess("Ambiente eliminado.");
            await cargar();
        } catch (err) {
            console.error("Error eliminando:", err);
            setError("No se pudo eliminar. Es posible que tenga registros asociados (sensores, etc).");
        }
    };

    if (loading && items.length === 0) return <Loading />;

    return (
        <div className="dashboard-container">
            <PageHeader title="Ambientes / Habitaciones" subtitle="Administre las áreas de cada inmueble para telemetría" />

            {/* Mensajes de feedback */}
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

            {/* Formulario */}
            <div className="card shadow-sm border-0 mb-4">
                <div className="card-header bg-white py-3">
                    <h5 className="mb-0 text-primary">
                        {editingId ? "Editar Ambiente" : "Registrar Nuevo Ambiente"}
                    </h5>
                </div>
                <div className="card-body">
                    {inmuebles.length === 0 ? (
                        <div className="alert alert-warning py-2 mb-0">
                            No hay inmuebles registrados. <strong>Debe crear un inmueble primero</strong> antes de registrar ambientes.
                        </div>
                    ) : (
                        <form onSubmit={guardar}>
                            <div className="row g-3">
                                <div className="col-md-4">
                                    <label className="form-label fw-bold">Inmueble / Sede *</label>
                                    <select
                                        className="form-select shadow-none"
                                        value={form.inmuebleId}
                                        onChange={(e) => setForm({ ...form, inmuebleId: e.target.value })}
                                        required
                                    >
                                        <option value="">-- Seleccione Inmueble --</option>
                                        {inmuebles.map((i) => (
                                            <option key={i.id} value={i.id}>{i.nombre}</option>
                                        ))}
                                    </select>
                                </div>

                                <div className="col-md-4">
                                    <label className="form-label fw-bold">Nombre del Ambiente *</label>
                                    <input
                                        className="form-control shadow-none"
                                        placeholder="Ej: Habitación 101, Oficina A"
                                        value={form.nombre}
                                        onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                                        required
                                    />
                                </div>

                                <div className="col-md-4">
                                    <label className="form-label fw-bold">Tipo / Categoría</label>
                                    <input
                                        className="form-control shadow-none"
                                        placeholder="Ej: Dormitorio, Almacén"
                                        value={form.tipo}
                                        onChange={(e) => setForm({ ...form, tipo: e.target.value })}
                                    />
                                </div>

                                <div className="col-md-2">
                                    <label className="form-label fw-bold">N° Piso</label>
                                    <input
                                        type="number"
                                        className="form-control shadow-none"
                                        value={form.piso}
                                        onChange={(e) => setForm({ ...form, piso: e.target.value })}
                                    />
                                </div>

                                <div className="col-md-3">
                                    <label className="form-label fw-bold">Estado</label>
                                    <select
                                        className="form-select shadow-none"
                                        value={form.estado}
                                        onChange={(e) => setForm({ ...form, estado: e.target.value })}
                                    >
                                        <option value="ACTIVO">ACTIVO</option>
                                        <option value="INACTIVO">INACTIVO</option>
                                        <option value="MANTENIMIENTO">MANTENIMIENTO</option>
                                    </select>
                                </div>

                                <div className="col-md-7">
                                    <label className="form-label fw-bold">Descripción / Notas</label>
                                    <input
                                        className="form-control shadow-none"
                                        value={form.descripcion}
                                        onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
                                    />
                                </div>
                            </div>

                            <div className="mt-4 d-flex gap-2">
                                <button type="submit" className="btn btn-primary px-4" disabled={saving}>
                                    {saving ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2"></span>
                                            Procesando...
                                        </>
                                    ) : (
                                        editingId ? "Actualizar Cambios" : "Registrar Ambiente"
                                    )}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-secondary px-4"
                                    onClick={() => {
                                        setForm(initialForm);
                                        setEditingId(null);
                                        setError("");
                                        setSuccess("");
                                    }}
                                    disabled={saving}
                                >
                                    Cancelar / Limpiar
                                </button>
                            </div>
                        </form>
                    )}
                </div>
            </div>

            {/* Listado */}
            <div className="card shadow-sm border-0">
                <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">Lista de Ambientes</h5>
                    <button className="btn btn-sm btn-light" onClick={cargar}>
                        <i className="bi bi-arrow-clockwise me-1"></i> Actualizar
                    </button>
                </div>
                <div className="card-body p-0">
                    <div className="table-responsive">
                        <table className="table table-hover align-middle mb-0">
                            <thead className="table-light">
                                <tr>
                                    <th className="px-3">ID</th>
                                    <th>Ambiente</th>
                                    <th>Inmueble</th>
                                    <th>Tipo</th>
                                    <th className="text-center">Piso</th>
                                    <th>Estado</th>
                                    <th className="text-end px-3">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {items.length === 0 ? (
                                    <tr>
                                        <td colSpan="7" className="text-center py-5 text-muted">
                                            {loading ? "Cargando..." : "No hay ambientes registrados todavía."}
                                        </td>
                                    </tr>
                                ) : (
                                    items.map((x) => (
                                        <tr key={x.id}>
                                            <td className="px-3 text-muted">#{x.id}</td>
                                            <td className="fw-bold">{x.nombre}</td>
                                            <td>{x.inmueble?.nombre || "N/A"}</td>
                                            <td>{x.tipo || "-"}</td>
                                            <td className="text-center">{x.piso}</td>
                                            <td>
                                                <span className={`badge ${
                                                    x.estado === "ACTIVO" ? "bg-success" : 
                                                    x.estado === "MANTENIMIENTO" ? "bg-warning text-dark" : "bg-secondary"
                                                }`}>
                                                    {x.estado}
                                                </span>
                                            </td>
                                            <td className="text-end px-3">
                                                <div className="btn-group">
                                                    <button 
                                                        className="btn btn-sm btn-outline-warning" 
                                                        onClick={() => editar(x)}
                                                        title="Editar"
                                                    >
                                                        <i className="bi bi-pencil"></i>
                                                    </button>
                                                    <button 
                                                        className="btn btn-sm btn-outline-danger" 
                                                        onClick={() => eliminar(x.id)}
                                                        title="Eliminar"
                                                    >
                                                        <i className="bi bi-trash"></i>
                                                    </button>
                                                </div>
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