export default function StatCard({ value, label }) {
  return (
    <div className="card card-stat">
      <div className="card-body">
        <div className="stat-value">{value}</div>
        <div className="text-muted">{label}</div>
      </div>
    </div>
  );
}
