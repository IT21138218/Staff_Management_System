import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { payrollApi } from '../../api/payrollApi';
import ErrorAlert from '../../components/common/ErrorAlert';

export default function PayslipView() {
  const { id } = useParams();
  const [payslip, setPayslip] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    payrollApi.get(id).then(setPayslip).catch(setError);
  }, [id]);

  const handleDownload = async () => {
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

  if (error) return <ErrorAlert error={error} />;
  if (!payslip) return <p className="text-muted">Loading...</p>;

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3><i className="fa-solid fa-receipt"></i> Payslip &mdash; {payslip.payMonth}/{payslip.payYear}</h3>
        <button className="btn btn-outline-primary" onClick={handleDownload}>
          <i className="fa-solid fa-file-pdf"></i> Download PDF
        </button>
      </div>

      <div className="card card-stat" style={{ maxWidth: 700 }}>
        <div className="card-body">
          <p><strong>{payslip.employee.firstName} {payslip.employee.lastName}</strong> ({payslip.employee.employeeCode})</p>
          <table className="table">
            <tbody>
              <tr><th>Basic Salary</th><td>{payslip.basicSalary}</td></tr>
              <tr><th>Overtime Pay</th><td>{payslip.overtimePay}</td></tr>
              {payslip.items.map((item) => (
                <tr key={item.id}>
                  <th>{item.type === 'ALLOWANCE' ? '+ ' : '- '}{item.description}</th>
                  <td>{item.amount}</td>
                </tr>
              ))}
              <tr><th>Tax</th><td>{payslip.tax}</td></tr>
              <tr className="table-primary"><th>Net Salary</th><td><strong>{payslip.netSalary}</strong></td></tr>
            </tbody>
          </table>
          <p className="text-muted small">Generated on {new Date(payslip.generatedDate).toLocaleString()}</p>
        </div>
      </div>
    </div>
  );
}
