package io.herald.MySpringWeb.Controller;

import io.herald.MySpringWeb.Model.ImageTable;
import io.herald.MySpringWeb.Repository.ImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
public class GalleryController {

    @Autowired
    private ImageRepository imageRepo;

    @GetMapping("/gallery")
    public String galleryGet(HttpServletRequest request, Model m) {
        HttpSession session = request.getSession();
        if(session.getAttribute("username") == null){
            m.addAttribute("message", "You are not logged in");
            return "login.html";
        }

        return "galleryPage";
    }

    @PostMapping("/gallery")
    public String galleryPost(@RequestParam("image") MultipartFile image, HttpSession session) {
        try{
            byte[] imgBytes = image.getBytes();
            //we will use base64 encoder
            //we will encode the byte information of file into string
            //to deport, we will again use the Base64 Decoder
            String imgString = Base64.getEncoder().encodeToString(imgBytes);

            ImageTable img = new ImageTable();
            img.setImage(imgString);

            imageRepo.save(img);
        }catch(IOException e){
            e.printStackTrace();
        }
        session.setAttribute("totalImages", imageRepo.findAll());
        return "galleryPage";


    }
}