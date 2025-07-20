package com.ProyectoGPS.Backend.repository;

import com.ProyectoGPS.Backend.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    
    /**
     * Buscar items por venta
     */
    List<SaleItem> findBySaleId(Long saleId);
    
    /**
     * Buscar items por producto
     */
    List<SaleItem> findByProductId(Long productId);
    
    /**
     * Buscar items por lote
     */
    List<SaleItem> findByBatchId(Long batchId);
    
    /**
     * Buscar items por bodega
     */
    List<SaleItem> findByWarehouseId(Long warehouseId);
    
    /**
     * Obtener total de unidades vendidas por producto
     */
    @Query("SELECT SUM(si.quantity) FROM SaleItem si WHERE si.productId = :productId")
    Integer getTotalSoldQuantityByProduct(@Param("productId") Long productId);
    
    /**
     * Obtener total de unidades vendidas por lote
     */
    @Query("SELECT SUM(si.quantity) FROM SaleItem si WHERE si.batchId = :batchId")
    Integer getTotalSoldQuantityByBatch(@Param("batchId") Long batchId);
    
    /**
     * Obtener items vendidos por método de pricing
     */
    List<SaleItem> findByPricingMethod(SaleItem.PricingMethod pricingMethod);
    
    /**
     * Buscar items por código de producto
     */
    List<SaleItem> findByProductCode(String productCode);
}
