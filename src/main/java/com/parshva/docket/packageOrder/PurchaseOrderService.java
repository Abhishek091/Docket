package com.parshva.docket.packageOrder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class PurchaseOrderService {

    public Map<String, String> readPurchaseOrdersFromCsv() throws IOException {
        Resource resource = new ClassPathResource("export29913.xlsx");
//        String filePath = "export29913.xlsx";
//        String filePath = resource.getFile().getAbsolutePath();
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        List<Map<String, String>> lists = new ArrayList<>();
        Map<String, String> maps = null;

        try (InputStream inputStream = resource.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming you want the first sheet

            Iterator<Row> rowIterator = sheet.iterator();

            // Skip the header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String poNumber = row.getCell(1).toString(); // Column B for PO Number
                String supplier = row.getCell(11).toString(); // Column L for Supplier
                String description = row.getCell(15).toString(); // Column P for Description

                maps = new LinkedHashMap<>();
                maps.put(poNumber, supplier);
                lists.add(maps);

                PurchaseOrder purchaseOrder = new PurchaseOrder(poNumber, supplier);
                purchaseOrders.add(purchaseOrder);
            }
            List<PurchaseOrder> updatedPurchaseOrders = fillEmptySuppliers(purchaseOrders);
            System.out.println("Lists : "+lists);
            Map<String, String> retMap = supplierMapping(lists);
//            return updatedPurchaseOrders;
            return retMap;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String,String> supplierMapping(List<Map<String, String>> mappingLists){
        Map<String, String> poSupplierMapping = new HashMap<>();
        for (Map<String, String> entry : mappingLists) {
            for (Map.Entry<String, String> pair : entry.entrySet()) {
                String poNumber = pair.getKey();
                String supplier = pair.getValue();

                if (supplier.isEmpty()) {
                    // If the supplier is empty, check if it has a matching key in the mapping
                    if (poSupplierMapping.containsKey(poNumber)) {
                        supplier = poSupplierMapping.get(poNumber);
                    }
                }

                poSupplierMapping.put(poNumber, supplier);
            }
        }
        return poSupplierMapping;
    }

    public Map<String, String> toPurchaseOrderMap(List<PurchaseOrder> purchaseOrders) {
        // Create a new map to store the purchase order data.
        Map<String, String> purchaseOrderMap = new HashMap<>();

        // Iterate over the list of purchase orders.
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            // Add the PO number to the map as the key and the supplier value to the map as the value.
            purchaseOrderMap.put(purchaseOrder.getPoNumber(), purchaseOrder.getSupplier());
        }
        // Return the map.
        return purchaseOrderMap;
    }


    public static List<PurchaseOrder> fillEmptySuppliers(List<PurchaseOrder> purchaseOrders) {
        // Create a new list to store the updated supplier values.
        List<PurchaseOrder> updatedPurchaseOrders = new ArrayList<>();

        // Create a map to store the PO Number and supplier value for each purchase order.
        Map<String, String> poNumberToSupplierMap = new HashMap<>();

        // Iterate over the original list of purchase orders.
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            // Get the supplier value for the current purchase order.
            String supplier = purchaseOrder.getSupplier();

            // If the supplier value is empty, check if the PO Number has already appeared in the list.
            if (supplier == null || supplier.isEmpty()) {
                // If the PO Number has already appeared in the list, add the same supplier value to the current purchase order as the supplier value for the previous purchase order with the same PO Number.
                if (poNumberToSupplierMap.containsKey(purchaseOrder.getPoNumber())) {
                    purchaseOrder.setSupplier(poNumberToSupplierMap.get(purchaseOrder.getPoNumber()));
                } else {
                    // Add the current PO Number to the list with a `null` supplier value.
                    poNumberToSupplierMap.put(purchaseOrder.getPoNumber(), null);
                }
            } else {
                // Add the current purchase order to the new list.
                updatedPurchaseOrders.add(purchaseOrder);
            }
        }

        // Return the new list of purchase orders.
        return updatedPurchaseOrders;
    }

}