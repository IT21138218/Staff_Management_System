import axiosClient from './axiosClient';

export const payrollApi = {
  my: () => axiosClient.get('/payroll/my').then((res) => res.data),
  get: (id) => axiosClient.get(`/payroll/${id}`).then((res) => res.data),
  // The PDF endpoint requires the Bearer token, so a plain <a href> won't work -
  // fetch it as a blob and hand the caller an object URL to open/download instead.
  downloadPdf: (id) =>
    axiosClient.get(`/payroll/${id}/pdf`, { responseType: 'blob' }).then((res) => {
      const url = window.URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }));
      const disposition = res.headers['content-disposition'] || '';
      const match = disposition.match(/filename="(.+)"/);
      return { url, filename: match ? match[1] : `payslip-${id}.pdf` };
    }),
  eligibleEmployees: () => axiosClient.get('/payroll/eligible-employees').then((res) => res.data),
  generate: (employeeId, month, year) =>
    axiosClient
      .post('/payroll/generate', null, { params: { employeeId, month, year } })
      .then((res) => res.data),
  generateForAll: (month, year) =>
    axiosClient.post('/payroll/run/all', null, { params: { month, year } }).then((res) => res.data),
  all: (params) => axiosClient.get('/payroll/all', { params }).then((res) => res.data),
};
