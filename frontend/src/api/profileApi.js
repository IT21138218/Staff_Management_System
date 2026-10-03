import axiosClient from './axiosClient';

export const profileApi = {
  get: () => axiosClient.get('/profile').then((res) => res.data),
  changePassword: (form) => axiosClient.post('/profile/change-password', form).then((res) => res.data),
  uploadPhoto: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return axiosClient.post('/profile/photo', formData).then((res) => res.data);
  },
};
