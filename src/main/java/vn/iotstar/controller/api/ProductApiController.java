package vn.iotstar.controller.api;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/api/product")
@Tag(name = "Product Controller", description = "RESTful API CRUD và tìm kiếm phân trang cho Product")
public class ProductApiController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @Operation(summary = "Lấy tất cả sản phẩm (Product)")
    @GetMapping
    public ResponseEntity<?> getAllProduct() {
        return new ResponseEntity<>(new Response(true, "Thành công", productService.findAll()), HttpStatus.OK);
    }

    @Operation(summary = "Lấy chi tiết sản phẩm theo ID (PathVariable)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        Optional<Product> optProduct = productService.findById(id);
        if (optProduct.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", optProduct.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Không tìm thấy Product với ID: " + id, null), HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Lấy chi tiết sản phẩm theo ID (RequestParam)")
    @PostMapping(path = "/getProduct")
    public ResponseEntity<?> getProduct(@Validated @RequestParam("id") Long id) {
        Optional<Product> optProduct = productService.findById(id);
        if (optProduct.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", optProduct.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Không tìm thấy Product với ID: " + id, null), HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Tìm kiếm sản phẩm có phân trang và lọc theo Category")
    @GetMapping(path = "/page")
    public ResponseEntity<?> getProductPaginated(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sort", defaultValue = "productId,asc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Product> productPage;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = categoryId != null && categoryId > 0;

        if (hasKeyword && hasCategory) {
            productPage = productService.findByProductNameContainingAndCategoryCategoryId(keyword.trim(), categoryId, pageable);
        } else if (hasCategory) {
            productPage = productService.findByCategoryCategoryId(categoryId, pageable);
        } else if (hasKeyword) {
            productPage = productService.findByProductNameContaining(keyword.trim(), pageable);
        } else {
            productPage = productService.findAll(pageable);
        }

        return new ResponseEntity<>(new Response(true, "Thành công", productPage), HttpStatus.OK);
    }

    @Operation(summary = "Thêm mới Product có upload nhiều hình ảnh và chọn Category")
    @PostMapping(path = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageFiles", required = false) java.util.List<MultipartFile> imageFiles,
            @RequestParam(value = "images", required = false) String imagesUrlString,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @RequestParam(value = "discount", defaultValue = "0") Double discount,
            @RequestParam(value = "description", defaultValue = "") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "status", defaultValue = "1") Short status) {

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy danh mục (Category) với ID: " + categoryId, null),
                    HttpStatus.BAD_REQUEST);
        }

        Product product = new Product();
        product.setProductName(productName);
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);
        product.setCategory(optCategory.get());
        product.setCreateDate(new Date());

        java.util.List<String> imageList = new java.util.ArrayList<>();

        // Handle single imageFile
        if (imageFile != null && !imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String filename = storageService.getSorageFilename(imageFile, uuid.toString());
            storageService.store(imageFile, filename);
            imageList.add(filename);
        }

        // Handle multiple imageFiles
        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (file != null && !file.isEmpty()) {
                    UUID uuid = UUID.randomUUID();
                    String filename = storageService.getSorageFilename(file, uuid.toString());
                    storageService.store(file, filename);
                    imageList.add(filename);
                }
            }
        }

        // Handle image URLs passed directly
        if (imagesUrlString != null && !imagesUrlString.trim().isEmpty()) {
            String[] urls = imagesUrlString.split("[,;\\n]+");
            for (String u : urls) {
                if (!u.trim().isEmpty()) {
                    imageList.add(u.trim());
                }
            }
        }

        if (!imageList.isEmpty()) {
            product.setImages(String.join(",", imageList));
        }

        productService.save(product);
        return new ResponseEntity<>(new Response(true, "Thêm sản phẩm thành công", product), HttpStatus.CREATED);
    }

    @Operation(summary = "Cập nhật Product có upload hình ảnh mới hoặc giữ hình cũ")
    @PutMapping(path = "/updateProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @Validated @RequestParam("productId") Long productId,
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageFiles", required = false) java.util.List<MultipartFile> imageFiles,
            @RequestParam(value = "images", required = false) String imagesUrlString,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @RequestParam(value = "discount", defaultValue = "0") Double discount,
            @RequestParam(value = "description", defaultValue = "") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "status", defaultValue = "1") Short status) {

        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Product với ID: " + productId, null),
                    HttpStatus.BAD_REQUEST);
        }

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy danh mục với ID: " + categoryId, null),
                    HttpStatus.BAD_REQUEST);
        }

        Product product = optProduct.get();
        product.setProductName(productName);
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);
        product.setCategory(optCategory.get());

        java.util.List<String> newImageList = new java.util.ArrayList<>();

        if (imageFile != null && !imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String filename = storageService.getSorageFilename(imageFile, uuid.toString());
            storageService.store(imageFile, filename);
            newImageList.add(filename);
        }

        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (file != null && !file.isEmpty()) {
                    UUID uuid = UUID.randomUUID();
                    String filename = storageService.getSorageFilename(file, uuid.toString());
                    storageService.store(file, filename);
                    newImageList.add(filename);
                }
            }
        }

        if (imagesUrlString != null && !imagesUrlString.trim().isEmpty()) {
            String[] urls = imagesUrlString.split("[,;\\n]+");
            for (String u : urls) {
                if (!u.trim().isEmpty()) {
                    newImageList.add(u.trim());
                }
            }
        }

        if (!newImageList.isEmpty()) {
            product.setImages(String.join(",", newImageList));
        }

        productService.save(product);
        return new ResponseEntity<>(new Response(true, "Cập nhật sản phẩm thành công", product), HttpStatus.OK);
    }

    // Support POST updateProduct for browsers / jQuery multipart PUT compatibility
    @PostMapping(path = "/updateProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProductPost(
            @Validated @RequestParam("productId") Long productId,
            @Validated @RequestParam("productName") String productName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageFiles", required = false) java.util.List<MultipartFile> imageFiles,
            @RequestParam(value = "images", required = false) String imagesUrlString,
            @Validated @RequestParam("unitPrice") Double unitPrice,
            @RequestParam(value = "discount", defaultValue = "0") Double discount,
            @RequestParam(value = "description", defaultValue = "") String description,
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "status", defaultValue = "1") Short status) {
        return updateProduct(productId, productName, imageFile, imageFiles, imagesUrlString, unitPrice, discount, description, categoryId, quantity, status);
    }

    @Operation(summary = "Xóa Product theo ID (RequestParam)")
    @DeleteMapping(path = "/deleteProduct")
    public ResponseEntity<?> deleteProduct(@Validated @RequestParam("productId") Long productId) {
        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Product với ID: " + productId, null),
                    HttpStatus.BAD_REQUEST);
        }

        productService.delete(optProduct.get());
        return new ResponseEntity<>(new Response(true, "Xóa sản phẩm thành công", optProduct.get()), HttpStatus.OK);
    }

    @Operation(summary = "Xóa Product theo ID (PathVariable)")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable("id") Long id) {
        return deleteProduct(id);
    }
}
