import { useEffect, useState } from 'react';
import { payrollApi } from '../../api/payrollApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function MyPayslips() {
  const [payslips, setPayslips] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    payrollApi.my().then(setPayslips).catch(setError);
  }, []);

  const handleDownload = async (id) => {
    try {
      const { url, filename } = await payrollApi.downloadPdf(id);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      a.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div>
      <h3 className="mb-4"><i className="fa-solid fa-file-invoice-dollar"></i> My Payslips</h3>

      <ErrorAlert error={error} />

      <div className="card card-stat"><div className="card-body">
        <table className="table table-hover">
          <thead>
            <tr><th>Period</th><th>Basic</th><th>Overtime</th><th>Allowances</th><th>Deductions</th><th>Tax</th><th>Net Salary</th><th></th></tr>
          </thead>
          <tbody>
            {payslips.map((p) => (
              <tr key={p.id}>
                <td>{p.payMonth}/{p.payYear}</td>
                <td>{p.basicSalary}</td>
                <td>{p.overtimePay}</td>
                <td>{p.totalAllowances}</td>
                <td>{p.totalDeductions}</td>
                <td>{p.tax}</td>
                <td><strong>{p.netSalary}</strong></td>
                <td>
                  <button className="btn btn-sm btn-outline-secondary" onClick={() => handleDownload(p.id)}>PDF</button>
                </td>
              </tr>
            ))}
            {payslips.length === 0 && <tr><td colSpan={8} className="text-muted text-center py-4">No payslips yet.</td></tr>}
          </tbody>
        </table>
      </div></div>
    </div>
  );
}
