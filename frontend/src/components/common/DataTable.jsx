export default function DataTable({ columns, rows, rowKey, emptyMessage = 'No records found.' }) {
  return (
    <table className="table table-hover align-middle">
      <thead>
        <tr>
          {columns.map((col) => (
            <th key={col.header}>{col.header}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {rows.length === 0 && (
          <tr>
            <td colSpan={columns.length} className="text-muted text-center py-4">
              {emptyMessage}
            </td>
          </tr>
        )}
        {rows.map((row) => (
          <tr key={rowKey(row)}>
            {columns.map((col) => (
              <td key={col.header}>{col.render ? col.render(row) : row[col.field]}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
