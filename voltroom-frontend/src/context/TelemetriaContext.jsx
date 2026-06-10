import { createContext, useContext, useState, useEffect } from "react";
import axiosClient from "../api/axiosClient";
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const TelemetriaContext = createContext();

export const TelemetriaProvider = ({ children }) => {
    const [sensores, setSensores] = useState([]);
    const [ultimaActualizacion, setUltimaActualizacion] = useState(null);
    const [loading, setLoading] = useState(true);
    const cargarEstadoInicial = async () => {
        try {
            const { data } = await axiosClient.get("/telemetria/estado");
            setSensores(data);
            setUltimaActualizacion(new Date());
        } catch (error) {
            console.error("Error cargando telemetría inicial:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarEstadoInicial();

        let brokerURL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";
        brokerURL = brokerURL.replace('/api', '/ws-telemetria');

        const stompClient = new Client({
            webSocketFactory: () => new SockJS(brokerURL),
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('Conectado a WebSocket de Telemetría');
                stompClient.subscribe('/topic/telemetria', (message) => {
                    if (message.body) {
                        const newSensores = JSON.parse(message.body);
                        setSensores(newSensores);
                        setUltimaActualizacion(new Date());
                    }
                });
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
            },
        });

        stompClient.activate();

        return () => {
            stompClient.deactivate();
        };
    }, []);

    return (
        <TelemetriaContext.Provider value={{ 
            sensores, 
            ultimaActualizacion, 
            loading, 
            recargar: cargarEstadoInicial 
        }}>
            {children}
        </TelemetriaContext.Provider>
    );
};

export const useTelemetria = () => {
    const context = useContext(TelemetriaContext);
    if (!context) {
        throw new Error("useTelemetria debe usarse dentro de un TelemetriaProvider");
    }
    return context;
};
