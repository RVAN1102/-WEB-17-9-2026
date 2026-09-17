package vn.iotstar;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import org.springframework.jdbc.core.JdbcTemplate;
import vn.iotstar.config.StorageProperties;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.service.IStorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Api1Application {

    public static void main(String[] args) {
        SpringApplication.run(Api1Application.class, args);
    }

    @Bean
    CommandLineRunner init(IStorageService storageService,
                          CategoryRepository categoryRepository,
                          ProductRepository productRepository,
                          JdbcTemplate jdbcTemplate) {
        return args -> {
            storageService.init();

            // Check if we need to initialize or update products with local high-quality images starting from ID 1
            boolean needsReseed = false;
            if (categoryRepository.count() == 0 || productRepository.count() == 0) {
                needsReseed = true;
            } else {
                Product firstProduct = productRepository.findAll().stream().findFirst().orElse(null);
                if (firstProduct == null || firstProduct.getProductId() == null || firstProduct.getProductId() != 1L) {
                    needsReseed = true;
                } else {
                    for (Product p : productRepository.findAll()) {
                        if (p.getImages() == null || p.getImages().contains("cellphones.com.vn") || !p.getImages().startsWith("p1_")) {
                            needsReseed = true;
                            break;
                        }
                    }
                }
            }

            if (needsReseed) {
                productRepository.deleteAll();
                categoryRepository.deleteAll();
                try {
                    jdbcTemplate.execute("ALTER TABLE products ALTER COLUMN product_id RESTART WITH 1");
                    jdbcTemplate.execute("ALTER TABLE categories ALTER COLUMN category_id RESTART WITH 1");
                } catch (Exception ignored) {
                }

                Category c1 = categoryRepository.save(new Category(null, "Điện thoại di động", "cate-phone.svg", null));
                Category c2 = categoryRepository.save(new Category(null, "Máy tính xách tay - Laptop", "cate-laptop.svg", null));
                Category c3 = categoryRepository.save(new Category(null, "Đồng hồ thông minh", "cate-watch.svg", null));
                Category c4 = categoryRepository.save(new Category(null, "Tai nghe & Phụ kiện", "cate-audio.svg", null));
                Category c5 = categoryRepository.save(new Category(null, "Máy tính bảng - Tablet", "cate-tablet.svg", null));
                Category c6 = categoryRepository.save(new Category(null, "Thiết bị mạng & Router", "cate-network.svg", null));

                // 1. iPhone 15 Pro Max (4 authentic product photos)
                String ip15Images = "p1_1.jpg,p1_2.jpg,p1_3.jpg,p1_4.jpg";
                productRepository.save(new Product(null, "iPhone 15 Pro Max 256GB", 25, 29990000.0, ip15Images,
                        "Điện thoại flagship cao cấp nhất của Apple với khung viền Titan chuẩn hàng không vũ trụ và chip A17 Pro mạnh mẽ bậc nhất.", 5.0, new Date(), (short) 1, c1));

                // 2. Samsung Galaxy S24 Ultra (4 authentic product photos)
                String s24Images = "p2_1.jpg,p2_2.jpg,p2_3.jpg,p2_4.jpg";
                productRepository.save(new Product(null, "Samsung Galaxy S24 Ultra 512GB", 18, 28500000.0, s24Images,
                        "Điện thoại quyền năng tích hợp Galaxy AI đột phá, khung viền Titan và cụm 4 camera 200MP zoom siêu phân giải.", 8.0, new Date(), (short) 1, c1));

                // 3. Xiaomi 14 Ultra (4 authentic product photos)
                String mi14Images = "p3_1.png,p3_2.png,p3_3.png,p3_4.png";
                productRepository.save(new Product(null, "Xiaomi 14 Ultra Leica Camera", 15, 23990000.0, mi14Images,
                        "Huyền thoại nhiếp ảnh di động cùng Leica Summilux với cảm biến 1 inch thế hệ mới nhất LYT-900.", 10.0, new Date(), (short) 1, c1));

                // 4. MacBook Pro 16 M3 Max (4 authentic product photos)
                String macImages = "p4_1.jpg,p4_2.jpg,p4_3.jpg,p4_4.jpg";
                productRepository.save(new Product(null, "MacBook Pro 16 inch M3 Max", 10, 89990000.0, macImages,
                        "Máy trạm di động vô song dành cho lập trình viên và chuyên gia xử lý đồ họa 3D với màn hình Liquid Retina XDR cực đỉnh.", 3.0, new Date(), (short) 1, c2));

                // 5. Dell XPS 15 OLED (4 authentic product photos)
                String dellImages = "p5_1.jpg,p5_2.jpg,p5_3.jpg,p5_4.jpg";
                productRepository.save(new Product(null, "Dell XPS 15 OLED InfinityEdge", 12, 45000000.0, dellImages,
                        "Kiệt tác thiết kế laptop doanh nhân vỏ nhôm nguyên khối, chiếu nghỉ tay sợi carbon và màn hình 3.5K OLED.", 7.0, new Date(), (short) 1, c2));

                // 6. Asus ROG Zephyrus G16 (4 authentic product photos)
                String rogImages = "p6_1.jpg,p6_2.jpg,p6_3.jpg,p6_4.jpg";
                productRepository.save(new Product(null, "Asus ROG Zephyrus G16 OLED", 14, 52000000.0, rogImages,
                        "Laptop gaming mỏng nhẹ cao cấp hàng đầu với màn hình ROG Nebula OLED 240Hz và đồ họa NVIDIA RTX 4080.", 4.0, new Date(), (short) 1, c2));

                // 7. Apple Watch Ultra 2 (4 authentic product photos)
                String awImages = "p7_1.jpg,p7_2.jpg,p7_3.jpg,p7_4.jpg";
                productRepository.save(new Product(null, "Apple Watch Ultra 2 Titanium GPS + Cellular", 20, 19990000.0, awImages,
                        "Đồng hồ thông minh thám hiểm và thể thao bền bỉ nhất thế giới với độ sáng 3000 nits và GPS tần số kép L1+L5.", 5.0, new Date(), (short) 1, c3));

                // 8. Samsung Galaxy Watch 6 Classic (4 authentic product photos)
                String gwImages = "p8_1.jpg,p8_2.jpg,p8_3.jpg,p8_4.jpg";
                productRepository.save(new Product(null, "Samsung Galaxy Watch 6 Classic Bezel", 22, 7990000.0, gwImages,
                        "Vòng xoay bezel vật lý sang trọng kết hợp phân tích thành phần cơ thể BIA và huấn luyện giấc ngủ chuyên sâu.", 12.0, new Date(), (short) 1, c3));

                // 9. AirPods Pro 2 (4 authentic product photos)
                String apImages = "p9_1.jpg,p9_2.jpg,p9_3.jpg,p9_4.jpg";
                productRepository.save(new Product(null, "AirPods Pro 2 USB-C MagSafe Case", 30, 5490000.0, apImages,
                        "Khử tiếng ồn chủ động gấp 2 lần, âm thanh thích ứng Adaptive Audio và cổng sạc USB-C hiện đại.", 6.0, new Date(), (short) 1, c4));

                // 10. Sony WH-1000XM5 (4 authentic product photos)
                String sonyImages = "p10_1.jpg,p10_2.png,p10_3.png,p10_4.png";
                productRepository.save(new Product(null, "Sony WH-1000XM5 Hi-Res Noise Canceling", 15, 7690000.0, sonyImages,
                        "Tai nghe chụp tai chống ồn số 1 thế giới với bộ đôi chip xử lý V1 và QN1 cùng 8 micro lọc âm siêu sạch.", 10.0, new Date(), (short) 1, c4));
            }
        };
    }
}
