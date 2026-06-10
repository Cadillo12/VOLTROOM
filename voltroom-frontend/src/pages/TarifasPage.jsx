import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageTitle from "../components/PageTitle";
import Loading from "../components/Loading";
import { formatMoney } from "../utils/format";

const initialForm = {
    nombre: "",
    precioPorKwh: "",
    fechaInicio: "",
    fechaFin: "",
    activa: true,
    descripcion: "",
};

function TarifasPage() {
    const [items, setItems] = useState([]);
    const [form, setForm] = useState(initialForm);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            // eslint-disable-next-line react-hooks/immutability
            await cargar();
// manejar r aquí
        };

        fetchData();
    }, []);
    const cargar = async () => {
        setLoading(true);
        const {data} = await axiosClient.get("/tarifas");
        setItems(data);
        setLoading(false);
    };

    const guardar = async (e) => {
        e.preventDefault();
        await axiosClient.post("/tarifas", {
            ...form,
            precioPorKwh: Number(form.precioPorKwh),
        });
        setForm(initialForm);
        cargar();
    };

    if (loading) return <Loading/>;

    return (
        <div className="dashboard-container">
            <PageTitle title="Tarifas" subtitle="Administración de tarifas eléctricas"/>

            <div className="card shadow-sm mb-4">
                <div className="card-body">
                    <form onSubmit={guardar}>
                        <div className="row g-3">
                            <div className="col-md-3">
                                <label className="form-label">Nombre</label>
                                <input
                                    className="form-control"
                                    value={form.nombre}
                                    onChange={(e) => setForm({...form, nombre: e.target.value})}
                                />
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Precio por kWh</label>
                                <input
                                    type="number"
                                    step="0.0001"
                                    className="form-control"
                                    value={form.precioPorKwh}
                                    onChange={(e) => setForm({...form, precioPorKwh: e.target.value})}
                                />
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Fecha inicio</label>
                                <input
                                    type="date"
                                    className="form-control"
                                    value={form.fechaInicio}
                                    onChange={(e) => setForm({...form, fechaInicio: e.target.value})}
                                />
                            </div>

                            <div className="col-md-2">
                                <label className="form-label">Fecha fin</label>
                                <input
                                    type="date"
                                    className="form-control"
                                    value={form.fechaFin}
                                    onChange={(e) => setForm({...form, fechaFin: e.target.value})}
                                />
                            </div>

                            <div className="col-md-3">
                                <label className="form-label">Descripción</label>
                                <input
                                    className="form-control"
                                    value={form.descripcion}
                                    onChange={(e) => setForm({...form, descripcion: e.target.value})}
                                />
                            </div>
                        </div>

                        <div className="form-check mt-3">
                            <input
                                className="form-check-input"
                                type="checkbox"
                                checked={form.activa}
                                onChange={(e) => setForm({...form, activa: e.target.checked})}
                            />
                            <label className="form-check-label">Tarifa activa</label>
                        </div>

                        <button className="btn btn-primary mt-3">Guardar tarifa</button>
                    </form>
                </div>
            </div>

            <div className="card shadow-sm">
                <div className="card-body">
                    <table className="table table-hover">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Precio</th>
                            <th>Inicio</th>
                            <th>Fin</th>
                            <th>Activa</th>
                        </tr>
                        </thead>
                        <tbody>
                        {items.map((x) => (
                            <tr key={x.id}>
                                <td>{x.id}</td>
                                <td>{x.nombre}</td>
                                <td>{formatMoney(x.precioPorKwh)}</td>
                                <td>{x.fechaInicio}</td>
                                <td>{x.fechaFin || "-"}</td>
                                <td>{x.activa ? "Sí" : "No"}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default TarifasPage