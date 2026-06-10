import axios from "axios";

const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
});

axiosClient.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    const isAuthEndpoint = config.url.includes("/auth/login") || config.url.includes("/auth/register");
    if (token && !isAuthEndpoint) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default axiosClient;