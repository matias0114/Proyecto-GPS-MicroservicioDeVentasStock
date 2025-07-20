package com.ProyectoGPS.Backend.service;

import com.ProyectoGPS.Backend.client.InventoryServiceClient;
import com.ProyectoGPS.Backend.client.PatientServiceClient;
import com.ProyectoGPS.Backend.dto.*;
import com.ProyectoGPS.Backend.client.dto.InventoryResponse;
import com.ProyectoGPS.Backend.client.dto.PacienteResponse;
import com.ProyectoGPS.Backend.model.Sale;
import com.ProyectoGPS.Backend.model.SaleItem;
import com.ProyectoGPS.Backend.repository.SaleRepository;
import com.ProyectoGPS.Backend.repository.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio principal para la gestión de ventas
 */
@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private PatientServiceClient patientServiceClient;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    private BenefitService benefitService;

    @Autowired
    private PricingService pricingService;

    /**
     * Crear una nueva venta
     */
    @Transactional
    public SaleDTO createSale(SaleCreateDTO saleCreateDTO) {
        // 1. Validar datos de entrada
        validateSaleCreateDTO(saleCreateDTO);

        // 2. Obtener información del paciente
        PacienteResponse patient = patientServiceClient.getPatientByRut(saleCreateDTO.getPatientRut());
        Map<String, Object> benefitData = patientServiceClient.getPatientBenefits(saleCreateDTO.getPatientRut());

        // 3. Procesar beneficios
        BenefitService.BenefitInfo benefitInfo = benefitService.processBenefitInfo(benefitData);

        // 4. Crear la venta
        Sale sale = new Sale();
        sale.setPatientRut(patient.getRut());
        sale.setPatientName(patient.getNombre() + " " + patient.getApellido());
        sale.setIsBeneficiary(benefitInfo.isBeneficiary());
        sale.setBenefitType(benefitInfo.getBenefitType());
        sale.setStatus(Sale.SaleStatus.PENDING);

        // 5. Procesar items de la venta
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (SaleCreateDTO.SaleItemCreateDTO itemDto : saleCreateDTO.getSaleItems()) {
            // Validar disponibilidad de stock
            InventoryResponse inventory = validateAndGetInventory(itemDto);
            
            // Calcular precio
            SaleItem.PricingMethod pricingMethod = pricingService.determinePricingMethod(inventory);
            BigDecimal unitPrice = pricingService.calculateUnitPrice(inventory, pricingMethod, itemDto.getQuantity());
            BigDecimal itemTotalPrice = pricingService.calculateItemTotalPrice(unitPrice, itemDto.getQuantity());

            // Crear el item de venta
            SaleItem saleItem = createSaleItem(inventory, itemDto, unitPrice, pricingMethod);
            saleItem.setSale(sale);
            saleItems.add(saleItem);

            subtotal = subtotal.add(itemTotalPrice);
        }

        // 6. Aplicar descuentos y calcular totales
        BenefitService.SaleCalculation calculation = benefitService.applySaleCalculation(subtotal, benefitInfo);
        
        sale.setSubtotal(calculation.getSubtotal());
        sale.setDiscountAmount(calculation.getDiscountAmount());
        sale.setDiscountPercentage(calculation.getDiscountPercentage());
        sale.setTotalAmount(calculation.getTotalAmount());
        sale.setSaleItems(saleItems);

        // 7. Guardar la venta
        Sale savedSale = saleRepository.save(sale);

        // 8. Actualizar inventarios
        updateInventoryStock(saleItems);

        // 9. Marcar venta como completada
        savedSale.setStatus(Sale.SaleStatus.COMPLETED);
        saleRepository.save(savedSale);

        return convertToSaleDTO(savedSale);
    }

    /**
     * Obtener todas las ventas
     */
    public List<SaleDTO> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream()
                .map(this::convertToSaleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener venta por ID
     */
    public SaleDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta con ID " + id + " no encontrada"));
        return convertToSaleDTO(sale);
    }

    /**
     * Obtener ventas por RUT de paciente
     */
    public List<SaleDTO> getSalesByPatientRut(String patientRut) {
        List<Sale> sales = saleRepository.findByPatientRut(patientRut);
        return sales.stream()
                .map(this::convertToSaleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener ventas del día actual
     */
    public List<SaleDTO> getTodaysSales() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        
        List<Sale> sales = saleRepository.findTodaysSales(startOfDay, endOfDay);
        return sales.stream()
                .map(this::convertToSaleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cancelar una venta
     */
    @Transactional
    public SaleDTO cancelSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta con ID " + saleId + " no encontrada"));

        if (sale.getStatus() == Sale.SaleStatus.CANCELLED) {
            throw new RuntimeException("La venta ya está cancelada");
        }

        // Revertir cambios en inventario
        revertInventoryChanges(sale.getSaleItems());

        // Cambiar estado de la venta
        sale.setStatus(Sale.SaleStatus.CANCELLED);
        Sale savedSale = saleRepository.save(sale);

        return convertToSaleDTO(savedSale);
    }

    // Métodos privados auxiliares

    private void validateSaleCreateDTO(SaleCreateDTO saleCreateDTO) {
        if (saleCreateDTO == null) {
            throw new RuntimeException("Los datos de la venta son requeridos");
        }
        if (saleCreateDTO.getPatientRut() == null || saleCreateDTO.getPatientRut().trim().isEmpty()) {
            throw new RuntimeException("El RUT del paciente es requerido");
        }
        if (saleCreateDTO.getSaleItems() == null || saleCreateDTO.getSaleItems().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un item");
        }

        for (SaleCreateDTO.SaleItemCreateDTO item : saleCreateDTO.getSaleItems()) {
            if (item.getProductId() == null || item.getBatchId() == null || 
                item.getWarehouseId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("Todos los campos del item son requeridos y la cantidad debe ser mayor a 0");
            }
        }
    }

    private InventoryResponse validateAndGetInventory(SaleCreateDTO.SaleItemCreateDTO itemDto) {
        InventoryResponse inventory = inventoryServiceClient.getInventoryByBatchAndWarehouse(
                itemDto.getBatchId(), itemDto.getWarehouseId());

        if (inventory == null) {
            throw new RuntimeException("Inventario no encontrado para el producto especificado");
        }

        if (!inventoryServiceClient.checkStockAvailability(inventory.getId(), itemDto.getQuantity())) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + inventory.getCurrentStock() + 
                    ", Requerido: " + itemDto.getQuantity());
        }

        return inventory;
    }

    private SaleItem createSaleItem(InventoryResponse inventory, SaleCreateDTO.SaleItemCreateDTO itemDto, 
                                   BigDecimal unitPrice, SaleItem.PricingMethod pricingMethod) {
        SaleItem saleItem = new SaleItem();
        saleItem.setProductId(inventory.getBatch().getProduct().getId());
        
        // Usar código real del producto con fallback por seguridad
        String productCode = inventory.getBatch().getProduct().getCode();
        if (productCode == null || productCode.trim().isEmpty()) {
            productCode = "PROD-" + inventory.getBatch().getProduct().getId(); // Fallback para compatibilidad
        }
        saleItem.setProductCode(productCode);
        
        saleItem.setProductName(inventory.getBatch().getProduct().getName());
        saleItem.setBatchId(inventory.getBatchId());
        saleItem.setBatchNumber(inventory.getBatch().getBatchNumber());
        saleItem.setWarehouseId(inventory.getWarehouseId());
        saleItem.setWarehouseName(inventory.getWarehouse().getName());
        saleItem.setQuantity(itemDto.getQuantity());
        saleItem.setUnitPrice(unitPrice);
        saleItem.setPricingMethod(pricingMethod);
        
        // Calcular y establecer el precio total
        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(itemDto.getQuantity()));
        saleItem.setTotalPrice(totalPrice);

        return saleItem;
    }

    private void updateInventoryStock(List<SaleItem> saleItems) {
        for (SaleItem item : saleItems) {
            try {
                // Obtener el inventario específico
                InventoryResponse inventory = inventoryServiceClient.getInventoryByBatchAndWarehouse(
                        item.getBatchId(), item.getWarehouseId());
                
                // Actualizar stock
                boolean updated = inventoryServiceClient.updateStock(inventory.getId(), item.getQuantity());
                
                if (!updated) {
                    throw new RuntimeException("No se pudo actualizar el stock para el producto: " + item.getProductName());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar inventario: " + e.getMessage());
            }
        }
    }

    private void revertInventoryChanges(List<SaleItem> saleItems) {
        for (SaleItem item : saleItems) {
            try {
                // Obtener el inventario específico
                InventoryResponse inventory = inventoryServiceClient.getInventoryByBatchAndWarehouse(
                        item.getBatchId(), item.getWarehouseId());
                
                // Incrementar el stock (revertir la venta)
                boolean reverted = inventoryServiceClient.incrementStock(inventory.getId(), item.getQuantity());
                
                if (!reverted) {
                    // Log error pero no detener el proceso de cancelación
                    System.err.println("No se pudo revertir el stock para el producto: " + item.getProductName());
                }
            } catch (Exception e) {
                // Log error pero no detener el proceso de cancelación
                System.err.println("Error al revertir inventario para " + item.getProductName() + ": " + e.getMessage());
            }
        }
    }

    private SaleDTO convertToSaleDTO(Sale sale) {
        SaleDTO dto = new SaleDTO();
        dto.setId(sale.getId());
        dto.setPatientRut(sale.getPatientRut());
        dto.setPatientName(sale.getPatientName());
        dto.setSaleDate(sale.getSaleDate());
        dto.setSubtotal(sale.getSubtotal());
        dto.setDiscountAmount(sale.getDiscountAmount());
        dto.setDiscountPercentage(sale.getDiscountPercentage());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setBenefitType(sale.getBenefitType());
        dto.setIsBeneficiary(sale.getIsBeneficiary());
        dto.setStatus(sale.getStatus().name());

        if (sale.getSaleItems() != null) {
            List<SaleDTO.SaleItemDTO> itemDTOs = sale.getSaleItems().stream()
                    .map(this::convertToSaleItemDTO)
                    .collect(Collectors.toList());
            dto.setSaleItems(itemDTOs);
        }

        return dto;
    }

    private SaleDTO.SaleItemDTO convertToSaleItemDTO(SaleItem item) {
        SaleDTO.SaleItemDTO dto = new SaleDTO.SaleItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductCode(item.getProductCode());
        dto.setProductName(item.getProductName());
        dto.setBatchId(item.getBatchId());
        dto.setBatchNumber(item.getBatchNumber());
        dto.setWarehouseId(item.getWarehouseId());
        dto.setWarehouseName(item.getWarehouseName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setPricingMethod(item.getPricingMethod() != null ? item.getPricingMethod().name() : null);
        return dto;
    }
}
