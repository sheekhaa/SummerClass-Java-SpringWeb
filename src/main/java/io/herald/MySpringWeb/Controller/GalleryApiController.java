package io.herald.MySpringWeb.Controller;

import io.herald.MySpringWeb.Model.ImageTable;
import io.herald.MySpringWeb.Repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API Controller for gallery/image management.
 * Provides endpoints for retrieving and managing gallery items.
 */
@RestController
@RequestMapping("/api/gallery")
@CrossOrigin(origins = "*")
public class GalleryApiController {

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Get all gallery images.
     */
    @GetMapping
    public ResponseEntity<?> getAllGalleryItems() {
        try {
            List<ImageTable> images = imageRepository.findAll();
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching gallery items");
        }
    }

    /**
     * Get a specific gallery item by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGalleryItem(@PathVariable int id) {
        try {
            Optional<ImageTable> item = imageRepository.findById(id);
            if (item.isPresent()) {
                return ResponseEntity.ok(item.get());
            }
            return ResponseEntity.status(404).body("Gallery item not found");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching gallery item");
        }
    }

    /**
     * Delete a gallery item.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGalleryItem(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (imageRepository.existsById(id)) {
                imageRepository.deleteById(id);
                response.put("message", "Gallery item deleted successfully");
                return ResponseEntity.ok(response);
            }
            response.put("message", "Gallery item not found");
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("message", "Error deleting gallery item: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
