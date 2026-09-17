package vn.iotstar.service;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {

    void init();

    void store(MultipartFile file, String storeFilename);

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String storeFilename) throws Exception;

    String getSorageFilename(MultipartFile file, String id);
}
