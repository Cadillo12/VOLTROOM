export default function StatCard({ title, value, bg }) {
    return (
        <div className="stat-card" style={{ background: bg }}>
            <div className="mb-2">{title}</div>
            <div className="stat-number">{value}</div>
        </div>
    );
}