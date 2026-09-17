package vn.iotstar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body").isArray());
    }

    @Test
    void testGetProductPaginated() throws Exception {
        mockMvc.perform(get("/api/product/page")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body.content").isArray())
                .andExpect(jsonPath("$.body.size").value(3));
    }

    @Test
    void testAddAndUpdateAndDeleteProduct() throws Exception {
        // 1. Add Product
        MockMultipartFile image = new MockMultipartFile(
                "imageFile", "sample-pro.jpg", "image/jpeg", "image-bytes".getBytes()
        );

        String responseContent = mockMvc.perform(multipart("/api/product/addProduct")
                .file(image)
                .param("productName", "Test Sản Phẩm Mới " + System.currentTimeMillis())
                .param("categoryId", "1")
                .param("unitPrice", "15000000")
                .param("discount", "5")
                .param("quantity", "20")
                .param("description", "Mô tả sản phẩm test")
                .param("status", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body.productId").exists())
                .andReturn().getResponse().getContentAsString();

        org.json.JSONObject jsonObj = new org.json.JSONObject(responseContent);
        long newProductId = jsonObj.getJSONObject("body").getLong("productId");

        // 2. Get by ID
        mockMvc.perform(get("/api/product/" + newProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body.productId").value(newProductId));

        // 3. Update Product
        mockMvc.perform(multipart("/api/product/updateProduct")
                .param("productId", String.valueOf(newProductId))
                .param("productName", "Sản Phẩm Đã Sửa Tên")
                .param("categoryId", "1")
                .param("unitPrice", "18000000")
                .param("discount", "10")
                .param("quantity", "15")
                .param("description", "Mô tả mới")
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body.productName").value("Sản Phẩm Đã Sửa Tên"));

        // 4. Delete Product
        mockMvc.perform(delete("/api/product/deleteProduct")
                .param("productId", String.valueOf(newProductId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }
}
