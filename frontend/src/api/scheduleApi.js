import axiosClient from './axiosClient';

export const scheduleApi = {
  my: () => axiosClient.get('/schedule/my').then((res) => res.data),
  colleagues: () => axiosClient.get('/schedule/colleagues').then((res) => res.data),
  requestSwap: (rosterAssignmentId, swapWithEmployeeId) =>
    axiosClient
      .post('/schedule/swap-request', null, { params: { rosterAssignmentId, swapWithEmployeeId } })
      .then((res) => res.data),
  manage: (params) => axiosClient.get('/schedule/manage', { params }).then((res) => res.data),
  shifts: () => axiosClient.get('/schedule/manage/shifts').then((res) => res.data),
  manageableEmployees: () => axiosClient.get('/schedule/manage/employees').then((res) => res.data),
  assign: (form) => axiosClient.post('/schedule/manage/assign', form).then((res) => res.data),
  unassign: (id) => axiosClient.delete(`/schedule/manage/assign/${id}`).then((res) => res.data),
  newShift: (form) => axiosClient.post('/schedule/manage/shifts/new', form).then((res) => res.data),
  updateShift: (id, form) => axiosClient.put(`/schedule/manage/shifts/${id}`, form).then((res) => res.data),
  deleteShift: (id) => axiosClient.delete(`/schedule/manage/shifts/${id}`).then((res) => res.data),
  pendingSwaps: () => axiosClient.get('/schedule/manage/swaps').then((res) => res.data),
  decideSwap: (id, approve) =>
    axiosClient.post(`/schedule/manage/swaps/${id}/decide`, null, { params: { approve } }).then((res) => res.data),
};
