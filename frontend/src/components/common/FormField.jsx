export default function FormField({ label, error, children }) {
  return (
    <div className="mb-3">
      <label className="form-label">{label}</label>
      {children}
      {error && <div className="text-danger small">{error}</div>}
    </div>
  );
}
