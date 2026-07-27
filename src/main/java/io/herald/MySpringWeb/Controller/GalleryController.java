package io.herald.MySpringWeb.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    @GetMapping("/gallery")
    public String gallerGet(HttpServletRequest req, Model m){
        HttpSession session = req.getSession();
        if(session.getAttribute("username")==null){
            m.addAttribute("message", "you are not logged in");
            return "login";
        }
        return "galleryPage.html";
    }

    @PostMapping("/gallery")
    public String galleryPost(@RequestParam("image")MultipartFile image)
    {
        try{
        byte[] imgBytes = image.getBytes();
        //we will use Base64 Encoder
        //we will encode the byte information of file into string
        // to decode, we will again use the Base64 Decoder

            String imgString = Base64.getEncoder().encodeToString(imgBytes);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return "galleryPage";

    }
}
