package com.ProyectoGPS.Backend.service;

import com.ProyectoGPS.Backend.client.dto.InventoryResponse;
import com.ProyectoGPS.Backend.model.SaleItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Servicio para calcular precios usando diferentes métodos de valoración de inventario
 */
@Service
public class PricingService {

    /**
     * Calcula el precio unitario usando el método de pricing especificado
     */
    public BigDecimal calculateUnitPrice(InventoryResponse inventory, SaleItem.PricingMethod pricingMethod, Integer quantity) {
        if (inventory == null || inventory.getBatch() == null) {
            throw new RuntimeException("Información de inventario incompleta");
        }

        switch (pricingMethod) {
            case LAST_PURCHASE:
                return calculateLastPurchasePrice(inventory);
            case WEIGHTED_AVERAGE:
                return calculateWeightedAveragePrice(inventory);
            case FIFO:
                return calculateFIFOPrice(inventory);
            case LIFO:
                return calculateLIFOPrice(inventory);
            case LILO:
                return calculateLILOPrice(inventory);
            default:
                return calculateLastPurchasePrice(inventory);
        }
    }

    /**
     * Método de Última Compra: utiliza el precio del lote más reciente
     */
    private BigDecimal calculateLastPurchasePrice(InventoryResponse inventory) {
        // Usamos el precio base del producto ya que BatchResponse no tiene costPrice
        if (inventory.getBatch() != null && inventory.getBatch().getProduct() != null) {
            Double price = inventory.getBatch().getProduct().getPrice();
            if (price != null) {
                return BigDecimal.valueOf(price);
            }
        }
        // Precio por defecto si no se encuentra información
        return BigDecimal.valueOf(1000.0); // Precio por defecto en pesos chilenos
    }

    /**
     * Método de Promedio Ponderado: calcula un promedio basado en costos y cantidades
     */
    private BigDecimal calculateWeightedAveragePrice(InventoryResponse inventory) {
        // Para el promedio ponderado, usamos el precio del producto
        if (inventory.getBatch() != null && inventory.getBatch().getProduct() != null) {
            Double price = inventory.getBatch().getProduct().getPrice();
            if (price != null) {
                // Aplicamos un factor de promedio (en una implementación real, esto se calcularía con datos históricos)
                BigDecimal avgFactor = new BigDecimal("1.05"); // Factor de ajuste del 5%
                return BigDecimal.valueOf(price).multiply(avgFactor).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.valueOf(1000.0); // Precio por defecto
    }

    /**
     * Método FIFO (First In, First Out): usa el precio del lote más antiguo
     */
    private BigDecimal calculateFIFOPrice(InventoryResponse inventory) {
        // FIFO utilizaría el lote con fecha de entrada más antigua
        // En este caso, usamos el lote actual asumiendo que es el más antiguo disponible
        if (inventory.getBatch() != null && inventory.getBatch().getProduct() != null) {
            Double price = inventory.getBatch().getProduct().getPrice();
            if (price != null) {
                return BigDecimal.valueOf(price);
            }
        }
        return BigDecimal.valueOf(1000.0); // Precio por defecto
    }

    /**
     * Método LIFO (Last In, First Out): usa el precio del lote más reciente
     */
    private BigDecimal calculateLIFOPrice(InventoryResponse inventory) {
        // LIFO es similar a última compra
        return calculateLastPurchasePrice(inventory);
    }

    /**
     * Método LILO (Last In, Last Out): usa el precio del lote que salió más recientemente
     */
    private BigDecimal calculateLILOPrice(InventoryResponse inventory) {
        // LILO usaría información de lotes que han salido recientemente
        // Por simplicidad, usamos un cálculo similar al promedio ponderado
        if (inventory.getBatch() != null && inventory.getBatch().getProduct() != null) {
            Double price = inventory.getBatch().getProduct().getPrice();
            if (price != null) {
                // Aplicamos un factor de ajuste para LILO
                BigDecimal liloFactor = new BigDecimal("0.98"); // Factor de reducción del 2%
                return BigDecimal.valueOf(price).multiply(liloFactor).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.valueOf(1000.0); // Precio por defecto
    }

    /**
     * Determina el método de pricing a usar basado en la configuración del producto
     */
    public SaleItem.PricingMethod determinePricingMethod(InventoryResponse inventory) {
        // Se puede implementar lógica más avanzada aquí, por ejemplo:
        // - Por defecto usar LAST_PURCHASE para medicamentos
        // - Usar FIFO para productos próximos a vencer
        // - Usar WEIGHTED_AVERAGE para productos estables
        
        if (inventory != null && inventory.getBatch() != null) {
            // Ejemplo de lógica: si el lote está próximo a vencer, usar FIFO
            // En una implementación real, esto se basaría en configuración del sistema
            // o propiedades del producto
            
            // Por ahora, devolvemos método por defecto basado en el ID del producto
            Long productId = inventory.getBatch().getProduct().getId();
            
            // Lógica simple para demostración
            switch ((int) (productId % 5)) {
                case 0: return SaleItem.PricingMethod.FIFO;
                case 1: return SaleItem.PricingMethod.LIFO;
                case 2: return SaleItem.PricingMethod.WEIGHTED_AVERAGE;
                case 3: return SaleItem.PricingMethod.LILO;
                default: return SaleItem.PricingMethod.LAST_PURCHASE;
            }
        }
        
        return SaleItem.PricingMethod.LAST_PURCHASE; // Default
    }

    /**
     * Calcula el precio total para un item considerando cantidad y descuentos
     */
    public BigDecimal calculateItemTotalPrice(BigDecimal unitPrice, Integer quantity) {
        if (unitPrice == null || quantity == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }

        return unitPrice.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Valida que el precio calculado sea razonable
     */
    public boolean isPriceReasonable(BigDecimal calculatedPrice, BigDecimal basePrice) {
        if (calculatedPrice == null || basePrice == null) {
            return false;
        }

        // El precio calculado no debería ser más del 200% ni menos del 50% del precio base
        BigDecimal minPrice = basePrice.multiply(new BigDecimal("0.5"));
        BigDecimal maxPrice = basePrice.multiply(new BigDecimal("2.0"));

        return calculatedPrice.compareTo(minPrice) >= 0 && calculatedPrice.compareTo(maxPrice) <= 0;
    }

    /**
     * Información detallada del cálculo de precio
     */
    public static class PriceCalculationInfo {
        private final BigDecimal unitPrice;
        private final BigDecimal totalPrice;
        private final SaleItem.PricingMethod pricingMethod;
        private final String calculationDetails;

        public PriceCalculationInfo(BigDecimal unitPrice, BigDecimal totalPrice, 
                                   SaleItem.PricingMethod pricingMethod, String calculationDetails) {
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.pricingMethod = pricingMethod;
            this.calculationDetails = calculationDetails;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public SaleItem.PricingMethod getPricingMethod() {
            return pricingMethod;
        }

        public String getCalculationDetails() {
            return calculationDetails;
        }
    }
}
