package vn.iotstar.controller.api;

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
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IStorageService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/api/category")
@Tag(name = "Category Controller", description = "RESTful API CRUD và tìm kiếm phân trang cho Category")
public class CategoryAPIController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @Operation(summary = "Lấy tất cả danh mục (Category)")
    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        return new ResponseEntity<>(new Response(true, "Thành công", categoryService.findAll()), HttpStatus.OK);
    }

    @Operation(summary = "Lấy danh mục theo ID (PathVariable)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") Long id) {
        Optional<Category> optCategory = categoryService.findById(id);
        if (optCategory.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", optCategory.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response(false, "Không tìm thấy Category với ID: " + id, null), HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Lấy danh mục theo ID (RequestParam)")
    @PostMapping(path = "/getCategory")
    public ResponseEntity<?> getCategory(@Validated @RequestParam("id") Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Tìm kiếm danh mục có phân trang")
    @GetMapping(path = "/page")
    public ResponseEntity<?> getCategoryPaginated(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sort", defaultValue = "categoryId,asc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Category> categoryPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            categoryPage = categoryService.findByCategoryNameContaining(keyword.trim(), pageable);
        } else {
            categoryPage = categoryService.findAll(pageable);
        }

        return new ResponseEntity<>(new Response(true, "Thành công", categoryPage), HttpStatus.OK);
    }

    @Operation(summary = "Thêm mới Category có upload icon")
    @PostMapping(path = "/addCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addCategory(
            @Validated @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {

        Optional<Category> optCategory = categoryService.findByCategoryName(categoryName);
        if (optCategory.isPresent()) {
            return new ResponseEntity<>(new Response(false, "Loại sản phẩm này đã tồn tại trong hệ thống", optCategory.get()),
                    HttpStatus.BAD_REQUEST);
        }

        Category category = new Category();
        if (icon != null && !icon.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String filename = storageService.getSorageFilename(icon, uuid.toString());
            storageService.store(icon, filename);
            category.setIcon(filename);
        }

        category.setCategoryName(categoryName);
        categoryService.save(category);

        return new ResponseEntity<>(new Response(true, "Thêm thành công", category), HttpStatus.CREATED);
    }

    @Operation(summary = "Cập nhật Category có upload icon mới hoặc giữ icon cũ")
    @PutMapping(path = "/updateCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategory(
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }

        Category category = optCategory.get();
        if (icon != null && !icon.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String filename = storageService.getSorageFilename(icon, uuid.toString());
            storageService.store(icon, filename);
            category.setIcon(filename);
        }

        category.setCategoryName(categoryName);
        categoryService.save(category);

        return new ResponseEntity<>(new Response(true, "Cập nhật thành công", category), HttpStatus.OK);
    }

    // Support POST updateCategory for clients/browsers without native multipart PUT support
    @PostMapping(path = "/updateCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCategoryPost(
            @Validated @RequestParam("categoryId") Long categoryId,
            @Validated @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        return updateCategory(categoryId, categoryName, icon);
    }

    @Operation(summary = "Xóa Category theo ID (RequestParam)")
    @DeleteMapping(path = "/deleteCategory")
    public ResponseEntity<?> deleteCategory(@Validated @RequestParam("categoryId") Long categoryId) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        }

        categoryService.delete(optCategory.get());
        return new ResponseEntity<>(new Response(true, "Xóa thành công", optCategory.get()), HttpStatus.OK);
    }

    @Operation(summary = "Xóa Category theo ID (PathVariable)")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable("id") Long id) {
        return deleteCategory(id);
    }
}
