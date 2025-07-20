package com.ProyectoGPS.Backend.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Servicio para calcular descuentos basados en beneficios de pacientes
 */
@Service
public class BenefitService {

    /**
     * Calcula el descuento aplicable según el tipo de beneficio
     */
    public BigDecimal calculateDiscount(String benefitType, BigDecimal baseAmount) {
        if (benefitType == null || baseAmount == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountPercentage = getDiscountPercentageByBenefitType(benefitType);
        return baseAmount.multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Obtiene el porcentaje de descuento según el tipo de beneficio
     */
    public BigDecimal getDiscountPercentageByBenefitType(String benefitType) {
        switch (benefitType.toUpperCase()) {
            case "ADULTO_MAYOR":
                return new BigDecimal("15.00");
            case "ESTUDIANTE":
                return new BigDecimal("10.00");
            case "FUNCIONARIO_PUBLICO":
                return new BigDecimal("12.00");
            case "DISCAPACIDAD":
                return new BigDecimal("20.00");
            case "TRABAJADOR_SALUD":
                return new BigDecimal("18.00");
            case "FAMILIA_NUMEROSA":
                return new BigDecimal("8.00");
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * Procesa información de beneficios desde el microservicio externo
     */
    public BenefitInfo processBenefitInfo(Map<String, Object> benefitData) {
        if (benefitData == null) {
            return new BenefitInfo(false, "NINGUNO", BigDecimal.ZERO);
        }

        boolean isBeneficiary = (Boolean) benefitData.getOrDefault("esBeneficiario", false);
        String benefitType = (String) benefitData.getOrDefault("tipoBeneficio", "NINGUNO");
        
        // El porcentaje puede venir del servicio externo o ser calculado internamente
        Object discountObj = benefitData.get("descuentoPorcentaje");
        BigDecimal discountPercentage;
        
        if (discountObj instanceof Number) {
            discountPercentage = new BigDecimal(discountObj.toString());
        } else {
            // Si no viene del servicio externo, lo calculamos internamente
            discountPercentage = getDiscountPercentageByBenefitType(benefitType);
        }

        return new BenefitInfo(isBeneficiary, benefitType, discountPercentage);
    }

    /**
     * Aplica el descuento y calcula el total final
     */
    public SaleCalculation applySaleCalculation(BigDecimal subtotal, BenefitInfo benefitInfo) {
        if (!benefitInfo.isBeneficiary()) {
            return new SaleCalculation(subtotal, BigDecimal.ZERO, benefitInfo.getDiscountPercentage(), subtotal);
        }

        BigDecimal discountAmount = calculateDiscount(benefitInfo.getBenefitType(), subtotal);
        BigDecimal totalAmount = subtotal.subtract(discountAmount);

        return new SaleCalculation(subtotal, discountAmount, benefitInfo.getDiscountPercentage(), totalAmount);
    }

    /**
     * Clase para almacenar información de beneficios procesada
     */
    public static class BenefitInfo {
        private final boolean isBeneficiary;
        private final String benefitType;
        private final BigDecimal discountPercentage;

        public BenefitInfo(boolean isBeneficiary, String benefitType, BigDecimal discountPercentage) {
            this.isBeneficiary = isBeneficiary;
            this.benefitType = benefitType;
            this.discountPercentage = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
        }

        public boolean isBeneficiary() {
            return isBeneficiary;
        }

        public String getBenefitType() {
            return benefitType;
        }

        public BigDecimal getDiscountPercentage() {
            return discountPercentage;
        }
    }

    /**
     * Clase para almacenar el resultado de cálculos de venta
     */
    public static class SaleCalculation {
        private final BigDecimal subtotal;
        private final BigDecimal discountAmount;
        private final BigDecimal discountPercentage;
        private final BigDecimal totalAmount;

        public SaleCalculation(BigDecimal subtotal, BigDecimal discountAmount, 
                              BigDecimal discountPercentage, BigDecimal totalAmount) {
            this.subtotal = subtotal;
            this.discountAmount = discountAmount;
            this.discountPercentage = discountPercentage;
            this.totalAmount = totalAmount;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public BigDecimal getDiscountPercentage() {
            return discountPercentage;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }
}
