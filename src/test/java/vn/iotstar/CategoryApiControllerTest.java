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
class CategoryApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body").isArray());
    }

    @Test
    void testGetCategoryPaginated() throws Exception {
        mockMvc.perform(get("/api/category/page")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body.content").isArray())
                .andExpect(jsonPath("$.body.size").value(3));
    }

    @Test
    void testAddAndUpdateAndDeleteCategory() throws Exception {
        // 1. Add Category
        MockMultipartFile icon = new MockMultipartFile(
                "icon", "test-icon.png", "image/png", "sample-icon-bytes".getBytes()
        );

        String responseContent = mockMvc.perform(multipart("/api/category/addCategory")
                .file(icon)
                .param("categoryName", "Test Danh Mục Mới " + System.currentTimeMillis()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body.categoryId").exists())
                .andReturn().getResponse().getContentAsString();

        // Extract ID from JSON response
        org.json.JSONObject jsonObj = new org.json.JSONObject(responseContent);
        long newCategoryId = jsonObj.getJSONObject("body").getLong("categoryId");

        // 2. Get by ID
        mockMvc.perform(get("/api/category/" + newCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body.categoryId").value(newCategoryId));

        // 3. Update Category (using POST /updateCategory with multipart)
        mockMvc.perform(multipart("/api/category/updateCategory")
                .param("categoryId", String.valueOf(newCategoryId))
                .param("categoryName", "Danh Mục Đã Đổi Tên"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.body.categoryName").value("Danh Mục Đã Đổi Tên"));

        // 4. Delete Category
        mockMvc.perform(delete("/api/category/deleteCategory")
                .param("categoryId", String.valueOf(newCategoryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }
}
