package io.herald.MySpringWeb.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(
                ObjectUtils.asMap(
                "cloud_name", "mepvadgq",
                "api_key", "896799638554571",
                "api_secret", "9DSXBFp3L4AsYB_dqD7yVNedyuA",
                "secure",true
        ));
    }
}
