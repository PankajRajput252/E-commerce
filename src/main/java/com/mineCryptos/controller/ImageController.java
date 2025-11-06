package com.mineCryptos.controller;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.service.Service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload")
    public FinalResponse uploadImage(@RequestParam("file") MultipartFile file,
                                     @RequestParam(value = "userNodeId", required = false) String userNodeId) {
        FinalResponse finalResponse = new FinalResponse();
        try {

            String imageId = imageUploadService.uploadFile(file);
            // You can save imageUrl to your user/entity table here
            userRepository.updateProfileImageUrlBasedOnNodeId(imageId, userNodeId);
            Util.setSuccessMessage(finalResponse);
            finalResponse.setMessage("Image uploaded successfully: " + imageId);
        } catch (Exception e) {
            finalResponse.setMessage("Upload failed: " + e.getMessage());
        }
        return finalResponse;
    }
}
