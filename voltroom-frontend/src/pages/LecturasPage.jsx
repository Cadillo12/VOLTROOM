import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageTitle from "../components/PageTitle";
import Loading from "../components/Loading";
import { formatDate } from "../utils/format";

const initialForm = {
    sensorId: "",
    fechaHora: "",
    valorKwh: "",
    origenLectura: "MANUAL",
    observacion: "",
};

export default function LecturasPage() {
    const [items, setItems] = useState([]);
    const [sensores, setSensores] = useState([]);
    const [form, setForm] = useState(initialForm);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const inicializar = async () => {
            try {
                // eslint-disable-next-line react-hooks/immutability
                await cargarTodo();
// Hacer algo con el resultado
            } catch (error) {
                console.error("Error al cargar:", error);
            }
        };

        inicializar();
    }, []);

    const cargarTodo = async () => {
        setLoading(true);
        const [lecRes, senRes] = await Promise.all([
            axiosClient.get("/lecturas"),
            axiosClient.get("/sensores"),
        ]);
        setItems(lecRes.data);
        setSensores(senRes.data);
        setLoading(false);
    };

    const guardar = async (e) => {
        e.preventDefault();
        await axiosClient.post("/lecturas", {
            ...form,
            sensorId: Number(form.sensorId),
            valorKwh: Number(form.valorKwh),
        });
        setForm(initialForm);
        cargarTodo();
    };

    /**
     * Consume el endpoint binario generado con Apache POI para descargar el Excel
     * de lecturas de un sensor en particular (Manejo de Blobs).
     */
    const descargarExcel = async () => {
        if (!form.sensorId) {
            alert("Seleccione un sensor en el formulario para descargar su reporte");
            return;
        }
        try {
            const response = await axiosClient.get(`/reportes/lecturas/excel/${form.sensorId}`, {
                responseType: 'blob' // Crítico para manejar la descarga binaria (Excel POI)
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `lecturas_sensor_${form.sensorId}.xlsx`);
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
        } catch (error) {
            console.error("Error al descargar el Excel", error);
            alert("No se pudo descargar el reporte.");
        }
    };

    if (loading) return <Loading />;

    return (
        <>
            <PageTitle title="Lecturas" subtitle="Registro de consumo eléctrico" />

            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <form onSubmit={guardar}>
                        <div className="row g-3">
                            <div className="col-md-3">
                                <label className="form-label">Sensor</label>
                                <select
                                    className="form-select"
                                    value={form.sensorId}
                                    onChange={(e) => setForm({ ...form, sensorId: e.target.value })}
                                >
                                    <option value="">Seleccione</option>
                                    {sensores.map((s) => (
                                        <option key={s.id} value={s.id}>
                                            {s.codigo}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="col-md-3">
                                <label className="form-label">Fecha y hora</label>
                                <input
                                    type="datetime-local"
                                    className="form-control"
                                    value={form.fechaHora}
                                    onChange={(e) => setForm({ ...form, fechaHora: e.target.value })}
                                />
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">kWh</label>
                                <input
                                    type="number"
                                    step="0.001"
                                    className="form-control"
                                    value={form.valorKwh}
                                    onChange={(e) => setForm({ ...form, valorKwh: e.target.value })}
                                />
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Origen</label>
                                <select
                                    className="form-select"
                                    value={form.origenLectura}
                                    onChange={(e) => setForm({ ...form, origenLectura: e.target.value })}
                                >
                                    <option value="MANUAL">MANUAL</option>
                                    <option value="SIMULADA">SIMULADA</option>
                                    <option value="AUTOMATICA">AUTOMATICA</option>
                                </select>
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Observación</label>
                                <input
                                    className="form-control"
                                    value={form.observacion}
                                    onChange={(e) => setForm({ ...form, observacion: e.target.value })}
                                />
                            </div>
                        </div>

                        <div className="d-flex gap-2 mt-3">
                            <button type="submit" className="btn btn-primary">Registrar lectura</button>
                            <button type="button" className="btn btn-success" onClick={descargarExcel}>
                                <i className="bi bi-file-earmark-excel"></i> Descargar Reporte Excel
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <div className="card shadow-sm">
                <div className="card-body">
                    <table className="table table-hover align-middle">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Sensor</th>
                            <th>Fecha</th>
                            <th>kWh</th>
                            <th>Origen</th>
                            <th>Observación</th>
                        </tr>
                        </thead>
                        <tbody>
                        {items.map((x) => (
                            <tr key={x.id}>
                                <td>{x.id}</td>
                                <td>{x.sensor?.codigo}</td>
                                <td>{formatDate(x.fechaHora)}</td>
                                <td>{x.valorKwh}</td>
                                <td>{x.origenLectura}</td>
                                <td>{x.observacion}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
    );
}