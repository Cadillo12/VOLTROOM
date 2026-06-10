import { useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";
import PageTitle from "../components/PageTitle";
import Loading from "../components/Loading";
import { formatMoney } from "../utils/format";

export default function ConsumosPage() {
    const [consumos, setConsumos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [recalculando, setRecalculando] = useState(false);

    useEffect(() => {
        cargarConsumos();
    }, []);

    const cargarConsumos = async () => {
        setLoading(true);
        try {
            const { data } = await axiosClient.get("/consumos/mes-actual");
            setConsumos(data);
        } catch (error) {
            console.error("Error cargando consumos:", error);
        } finally {
            setLoading(false);
        }
    };

    const recalcular = async () => {
        setRecalculando(true);
        try {
            await axiosClient.post("/consumos/recalcular");
            await cargarConsumos();
        } catch (error) {
            console.error("Error recalculando:", error);
        } finally {
            setRecalculando(false);
        }
    };

    const getNivelConsumo = (kwh) => {
        const valor = parseFloat(kwh);
        if (valor >= 100) return { clase: "bg-danger", texto: "Alto" };
        if (valor >= 50) return { clase: "bg-warning text-dark", texto: "Medio" };
        return { clase: "bg-success", texto: "Normal" };
    };

    const totalKwh = consumos.reduce((sum, c) => sum + parseFloat(c.totalKwh || 0), 0);
    const totalCosto = consumos.reduce((sum, c) => sum + parseFloat(c.costoEstimado || 0), 0);

    if (loading) return <Loading />;

    return (
        <>
            <PageTitle title="Consumos" subtitle="Resumen automático de consumo eléctrico por habitación" />

            <div className="row g-3 mb-4">
                <div className="col-md-4">
                    <div className="card shadow-sm border-0" style={{ background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)" }}>
                        <div className="card-body text-white text-center">
                            <h6 className="mb-1">Total Consumo Mes</h6>
                            <h3 className="fw-bold">{totalKwh.toFixed(3)} kWh</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-4">
                    <div className="card shadow-sm border-0" style={{ background: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)" }}>
                        <div className="card-body text-white text-center">
                            <h6 className="mb-1">Costo Estimado Total</h6>
                            <h3 className="fw-bold">S/ {totalCosto.toFixed(2)}</h3>
                        </div>
                    </div>
                </div>
                <div className="col-md-4">
                    <div className="card shadow-sm border-0" style={{ background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)" }}>
                        <div className="card-body text-white text-center">
                            <h6 className="mb-1">Ambientes Monitoreados</h6>
                            <h3 className="fw-bold">{consumos.length}</h3>
                        </div>
                    </div>
                </div>
            </div>

            <div className="card shadow-sm">
                <div className="card-body">
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h5 className="card-title mb-0">Consumo por Habitación — Mes Actual</h5>
                        <button
                            className="btn btn-outline-primary btn-sm"
                            onClick={recalcular}
                            disabled={recalculando}
                        >
                            {recalculando ? (
                                <>
                                    <span className="spinner-border spinner-border-sm me-1"></span>
                                    Recalculando...
                                </>
                            ) : (
                                "⟳ Recalcular"
                            )}
                        </button>
                    </div>

                    <table className="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Ambiente</th>
                                <th>Inmueble</th>
                                <th>Consumo (kWh)</th>
                                <th>Tarifa (S//kWh)</th>
                                <th>Costo Estimado</th>
                                <th>Nivel</th>
                                <th>Periodo</th>
                            </tr>
                        </thead>
                        <tbody>
                            {consumos.length === 0 ? (
                                <tr>
                                    <td colSpan="7" className="text-center text-muted py-4">
                                        No hay datos de consumo aún. Los sensores generan lecturas cada 5 minutos
                                        y el cálculo se actualiza cada 30 minutos.
                                        <br />
                                        <button className="btn btn-sm btn-primary mt-2" onClick={recalcular}>
                                            Forzar cálculo ahora
                                        </button>
                                    </td>
                                </tr>
                            ) : (
                                consumos.map((c) => {
                                    const nivel = getNivelConsumo(c.totalKwh);
                                    return (
                                        <tr key={c.id}>
                                            <td className="fw-semibold">{c.ambiente?.nombre || "-"}</td>
                                            <td>{c.ambiente?.inmueble?.nombre || "-"}</td>
                                            <td>{parseFloat(c.totalKwh).toFixed(3)}</td>
                                            <td>{c.tarifa ? formatMoney(c.tarifa.precioPorKwh) : "S/ 0.85"}</td>
                                            <td className="fw-bold text-primary">
                                                S/ {parseFloat(c.costoEstimado).toFixed(2)}
                                            </td>
                                            <td>
                                                <span className={`badge ${nivel.clase}`}>{nivel.texto}</span>
                                            </td>
                                            <td>{`${c.periodoMes}/${c.periodoAnio}`}</td>
                                        </tr>
                                    );
                                })
                            )}
                        </tbody>
                    </table>

                    {consumos.length > 0 && (
                        <div className="border-top pt-3 mt-2">
                            <div className="row">
                                <div className="col-md-6">
                                    <strong>Total kWh:</strong> {totalKwh.toFixed(3)} kWh
                                </div>
                                <div className="col-md-6 text-end">
                                    <strong>Total a cobrar:</strong>{" "}
                                    <span className="text-primary fw-bold fs-5">
                                        S/ {totalCosto.toFixed(2)}
                                    </span>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}
