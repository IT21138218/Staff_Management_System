import axiosClient from './axiosClient';

export const employeeApi = {
  list: (params) => axiosClient.get('/employees', { params }).then((res) => res.data),
  get: (id) => axiosClient.get(`/employees/${id}`).then((res) => res.data),
  supervisors: () => axiosClient.get('/employees/supervisors').then((res) => res.data),
  create: (form) => axiosClient.post('/employees', form).then((res) => res.data),
  update: (id, form) => axiosClient.put(`/employees/${id}`, form).then((res) => res.data),
  deactivate: (id) => axiosClient.post(`/employees/${id}/deactivate`).then((res) => res.data),
  activate: (id) => axiosClient.post(`/employees/${id}/activate`).then((res) => res.data),
  uploadPhoto: (id, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return axiosClient.post(`/employees/${id}/photo`, formData).then((res) => res.data);
  },
};
