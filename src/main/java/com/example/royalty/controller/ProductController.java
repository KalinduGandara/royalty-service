package com.example.royalty.controller;

import com.example.royalty.dao.BulkUploadDAO;
import com.example.royalty.dao.GenerateCodeDAO;
import com.example.royalty.modal.Code;
import com.example.royalty.modal.Product;
import com.example.royalty.service.ProductService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
    public String all(Model model, @ModelAttribute("message") String message,@ModelAttribute("error") String error) {
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
    public String uploadFile(@ModelAttribute("upload") BulkUploadDAO upload, RedirectAttributes redirectAttributes) throws IOException {
        String fileName = upload.getFile().getOriginalFilename();
        if (fileName == null || fileName.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Please select a file.");
            return "redirect:/product";
        }
        List<String[]> rows = null;
        if (fileName.endsWith(".csv")) {
            rows = readCSV(upload.getFile().getInputStream());
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            rows = readExcel(upload.getFile().getInputStream());
        } else {
            redirectAttributes.addFlashAttribute("error", "Please select a valid file.");
            return "redirect:/product";
        }

        int incompleteRows = productService.createBulk(rows);
        if(incompleteRows>0){
            redirectAttributes.addFlashAttribute("error", "There are "+incompleteRows+" incomplete rows in the file.");
        }
        return "redirect:/product";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("product")Product product, BindingResult result) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            return "addProduct";
        }
        if (productService.create(product)){
            return "redirect:/product";
        }
        result.rejectValue("code", "unique", "Code is already exist.");
        return "addProduct";
    }


    @GetMapping("/{id}")
    public String getOne(@PathVariable long id, Model model,@ModelAttribute("message") String message,@ModelAttribute("error") String error) {
        Product product = productService.getById(id);
        if (product == null) {
            return "redirect:/product";
        }
        model.addAttribute("code", new GenerateCodeDAO(id, 0));
        model.addAttribute("product", product);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "product";
    }
    @GetMapping("/{id}/codes")
    public String viewCodes(@PathVariable long id, Model model) {
        List<Code> codes = productService.getCodes(id);
        model.addAttribute("codes", codes);
        return "codes";
    }

    @PostMapping("/generate")
    public String generateCode(@Valid GenerateCodeDAO code, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "Count should be greater than 0.");
            return "redirect:/product/"+code.getProductId();
        }
        Product product = productService.getById(code.getProductId());
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found.");
            return "redirect:/product";
        }
        if(productService.generateCode(product, code.getCount())){
            redirectAttributes.addFlashAttribute("message", "Code Generated Successfully.");
        }else {
            redirectAttributes.addFlashAttribute("error", "Something went wrong.");
        }
        return "redirect:/product/"+code.getProductId();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute("product") Product product, BindingResult result) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            return "product";
        }
        if (productService.update(id,product)) {
            return "redirect:/product";
        }
        return "product";
    }
}
