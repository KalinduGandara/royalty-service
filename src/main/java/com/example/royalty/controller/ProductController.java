package com.example.royalty.controller;

import com.example.royalty.dao.BulkUploadDAO;
import com.example.royalty.dao.GenerateCodeDAO;
import com.example.royalty.modal.Code;
import com.example.royalty.modal.Product;
import com.example.royalty.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.royalty.util.ReadFile.readCSV;
import static com.example.royalty.util.ReadFile.readExcel;

@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public String all(Model model, @ModelAttribute("message") String message, @ModelAttribute("error") String error) {
        List<Product> products = productService.getAll();
        model.addAttribute("products", products);
        model.addAttribute("upload", new BulkUploadDAO());
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "products";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("product", new Product());
        return "addProduct";
    }

    @PostMapping("/upload")
    public String uploadFile(@ModelAttribute("upload") BulkUploadDAO upload, RedirectAttributes redirectAttributes) {
        String fileName = upload.getFile().getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file.");
            return "redirect:/product";
        }
        List<String[]> rows = null;
        String[] headers = {"Name", "Code", "Capacity", "Description", "Points"};
        try {
            if (fileName.endsWith(".csv")) {
                rows = readCSV(upload.getFile().getInputStream(), headers);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                rows = readExcel(upload.getFile().getInputStream(), headers);
            } else {
                redirectAttributes.addFlashAttribute("error", "Please select a valid file.");
                return "redirect:/product";
            }
        } catch (IOException e) {
            if (e.getMessage().equalsIgnoreCase("Invalid file headers")) {
                redirectAttributes.addFlashAttribute("error", "Invalid file headers.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Something went wrong.");
            }
            return "redirect:/product";
        }

        int incompleteRows = productService.createBulk(rows);
        if (incompleteRows > 0) {
            redirectAttributes.addFlashAttribute("error", "There are " + incompleteRows + " incomplete rows in the file.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Products added successfully.");
        }
        return "redirect:/product";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("product") Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "addProduct";
        }
        if (productService.create(product)) {
            return "redirect:/product";
        }
        result.rejectValue("code", "unique", "Code is already exist.");
        return "addProduct";
    }


    @GetMapping("/{id}")
    public String getOne(@PathVariable long id, Model model, @ModelAttribute("message") String message, @ModelAttribute("error") String error) {
        Product product = productService.getById(id);
        if (product == null) {
            return "redirect:/product";
        }
        LocalDateTime startDateTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDateTime = LocalDateTime.now();
        model.addAttribute("code", new GenerateCodeDAO(id, 0));
        model.addAttribute("product", product);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        model.addAttribute("startDate", startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("endDate", endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return "product";
    }

    @GetMapping("/{id}/codes")
    public String viewCodes(@PathVariable long id, Model model,
                            @RequestParam(required = false, defaultValue = "") String startDate,
                            @RequestParam(required = false, defaultValue = "") String endDate) {
        List<Code> codes = productService.getCodesByProductIdAndCreatedDate(
                id,
                parseDate(startDate + " 00:00:00", LocalDateTime.now().minusMonths(1)),
                parseDate(endDate + " 23:59:59", LocalDateTime.now())
        );
        model.addAttribute("codes", codes);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "codes";
    }

    /* @PostMapping("/generate")
     public String generateCode(@Valid GenerateCodeDAO code, BindingResult result, RedirectAttributes redirectAttributes) {
         if (result.hasErrors()){
             redirectAttributes.addFlashAttribute("error", "Count should be greater than 0.");
             return "redirect:/product/"+code.getProductId();
         }
         Product product = productService.getById(code.getProductId());
         if (product == null) {
             redirectAttributes.addFlashAttribute("error", "Product not found.");
             return "redirect:/product";
         }
 //        if(productService.generateCode(product, code.getCount())){
 //            redirectAttributes.addFlashAttribute("message", "Code Generated Successfully.");
 //        }else {
 //            redirectAttributes.addFlashAttribute("error", "Something went wrong.");
 //        }
         List<Code> codes = productService.generateCode(product, code.getCount());
         return "redirect:/product/"+code.getProductId();
     }*/
    @PostMapping("/generate")
    public void downloadCsv(@ModelAttribute("count") String count, @ModelAttribute("productId") String productId, HttpServletResponse response) throws IOException {
        int numberOfRows = Integer.parseInt(count);
        long id = Long.parseLong(productId);
        Product product = productService.getById(id);
        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product Not Found"); // 404.
            return;
        }

        StringBuilder csvContent = new StringBuilder();
        List<Code> codes = productService.generateCode(product, numberOfRows);
        csvContent.append("Code\n");
        for (Code code : codes) {
            csvContent.append(code.getCode()).append("\n");
        }

        String fileName = product.getName().replaceAll("\\s+", "_") + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".csv";

        // Set response headers
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // Write CSV content to response
        try (PrintWriter writer = response.getWriter()) {
            writer.write(csvContent.toString());
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute("product") Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "product";
        }
        if (productService.update(id, product)) {
            return "redirect:/product";
        }
        return "product";
    }

    private static LocalDateTime parseDate(String dateString, LocalDateTime defaultValue) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
