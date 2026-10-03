import axiosClient from './axiosClient';

export const authApi = {
  login: (username, password) =>
    axiosClient.post('/auth/login', { username, password }).then((res) => res.data),
  register: (form) => axiosClient.post('/auth/register', form).then((res) => res.data),
};
