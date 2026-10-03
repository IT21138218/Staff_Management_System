import axiosClient from './axiosClient';

function downloadBlob(promise, fallbackFilename) {
  return promise.then((res) => {
    const url = window.URL.createObjectURL(new Blob([res.data]));
    const disposition = res.headers['content-disposition'] || '';
    const match = disposition.match(/filename="(.+)"/);
    return { url, filename: match ? match[1] : fallbackFilename };
  });
}

export const reportApi = {
  exportEmployees: () =>
    downloadBlob(axiosClient.get('/reports/employees/export', { responseType: 'blob' }), 'employees-report.xlsx'),
  exportAttendance: (start, end) =>
    downloadBlob(
      axiosClient.get('/reports/attendance/export', { params: { start, end }, responseType: 'blob' }),
      'attendance-report.xlsx'
    ),
};
