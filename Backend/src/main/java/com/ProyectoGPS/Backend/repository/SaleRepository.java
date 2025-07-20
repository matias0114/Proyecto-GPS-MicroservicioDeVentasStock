package com.ProyectoGPS.Backend.repository;

import com.ProyectoGPS.Backend.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    /**
     * Buscar ventas por RUT del paciente
     */
    List<Sale> findByPatientRut(String patientRut);
    
    /**
     * Buscar ventas por estado
     */
    List<Sale> findByStatus(Sale.SaleStatus status);
    
    /**
     * Buscar ventas entre fechas
     */
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Buscar ventas de beneficiarios
     */
    List<Sale> findByIsBeneficiaryTrue();
    
    /**
     * Buscar ventas por tipo de beneficio
     */
    List<Sale> findByBenefitType(String benefitType);
    
    /**
     * Obtener ventas del día actual
     */
    @Query("SELECT s FROM Sale s WHERE s.saleDate >= :startOfDay AND s.saleDate < :endOfDay")
    List<Sale> findTodaysSales(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    /**
     * Obtener total de ventas por fecha
     */
    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.saleDate >= :startOfDay AND s.saleDate < :endOfDay")
    java.math.BigDecimal getTotalSalesByDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    /**
     * Contar ventas por paciente
     */
    long countByPatientRut(String patientRut);
    
    /**
     * Buscar ventas por estado ordenadas por fecha
     */
    List<Sale> findByStatusOrderBySaleDateDesc(Sale.SaleStatus status);
}
