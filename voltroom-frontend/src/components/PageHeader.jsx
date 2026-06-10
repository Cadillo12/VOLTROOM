export default function PageHeader({ title, subtitle }) {
    return (
        <div className="pa-header">
            <div className="pa-header-left">
                <div className="pa-breadcrumb"><span className="home-icon">🏠</span> Home / <strong>{title}</strong></div>
                <h1 className="pa-title">{title}</h1>
                <p className="pa-subtitle">{subtitle}</p>
            </div>
            <div className="pa-header-right">
                <div className="pa-header-stat">
                    <span className="stat-label">SYSTEM STATUS</span>
                    <div className="pa-live-indicator">
                        <span className="live-dot"></span>
                        <span className="live-text">ONLINE</span>
                    </div>
                </div>
            </div>
        </div>
    );
}