package com.ProyectoGPS.Backend.Metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ProyectoGPS.Backend.repository.UsuarioRepository;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class UsuarioMetrics {

    private final UsuarioRepository usuarioRepository;
    private final AtomicLong usuariosTotales;
    
    public UsuarioMetrics(UsuarioRepository usuarioRepository, MeterRegistry meterRegistry) {
        this.usuarioRepository = usuarioRepository;
        this.usuariosTotales = new AtomicLong(0);
        // Registro la métrica una sola vez vinculada al AtomicLong
        Gauge.builder("miapp_usuarios_totales", usuariosTotales, AtomicLong::get)
                .description("Cantidad total de usuarios en la base de datos")
                .register(meterRegistry);
    }

    @Scheduled(fixedRate = 10000) // Cada 10 segundos
    public void actualizarUsuariosTotales() {
        long total = usuarioRepository.count();
        usuariosTotales.set(total);  // Actualizo el valor del AtomicLong
    }
}
