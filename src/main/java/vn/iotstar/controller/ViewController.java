package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories")
    public String categoriesPage() {
        return "categories";
    }

    @GetMapping("/admin/products")
    public String productsPage() {
        return "products";
    }
}
