package com.parshva.docket.DBDocketController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parshva.docket.DBDocket.Docket;
import com.parshva.docket.DBDocketRepo.DocketRepository;
import com.parshva.docket.packageOrder.PurchaseOrder;
import com.parshva.docket.packageOrder.PurchaseOrderService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.*;
import java.util.*;

@Controller
public class DocketController {

    @Autowired
    private DocketRepository docketRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("docket", new Docket());
        return "index";
    }

    @PostMapping("/submit")
    public String processForm(@ModelAttribute Docket docket, Model model) {
        // Save the submitted docket to the database
        System.out.println(docket.toString());
        docketRepository.save(docket);
        model.addAttribute("successmsg", "Docket created Successfully");

        model.addAttribute("docket", new Docket());
        return "index"; // Redirect to the form again
    }

    @GetMapping("/dockets")
    public String getDockets(Model model) {
        List<Docket> items = docketRepository.findAll();
        model.addAttribute("items", items);
        return "dockets"; // Return the name of your HTML template
    }

    @GetMapping("/read-excel")
    public ResponseEntity<String> readExcel() {
        System.out.println("Inside read-excel endpoint....");
        String filePath = "/Users/abhishekkumar/Downloads/export29913.xlsx";
//        List<PurchaseOrder> purchaseOrders = purchaseOrderService.readPurchaseOrdersFromCsv();
        Map<String, String> purchaseOrders = purchaseOrderService.readPurchaseOrdersFromCsv();
        Map<String, List<Map<String, String>>> supplierDataMap = new LinkedHashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming you want the first sheet

            Iterator<Row> rowIterator = sheet.iterator();

            // Skip the header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            String currentSupplier = null;
            List<Map<String, String>> currentSupplierData = null;


            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String poNumber = row.getCell(1).toString(); // Column B for Supplier
                String supplier = row.getCell(11).toString(); // Column L for PO Number
                String description = row.getCell(15).toString(); // Column P for Description

            if (!supplier.isEmpty()) {
//                    // If the Supplier column is not empty, set it as the current supplier
                currentSupplier = purchaseOrders.get(poNumber) + "_" + poNumber;
                currentSupplierData = new ArrayList<>();

                supplierDataMap.put(currentSupplier, currentSupplierData);
            }
                // Build the JSON data
                String poNumberDesc = poNumber + "_" + description;
                currentSupplierData.add(Map.of("poNumber_Desc", poNumberDesc));
            }



            // Convert the map to a JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(supplierDataMap);
            System.out.println("JSON data = " + jsonData);

            return ResponseEntity.ok(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading the Excel file.");
        }
    }
}

