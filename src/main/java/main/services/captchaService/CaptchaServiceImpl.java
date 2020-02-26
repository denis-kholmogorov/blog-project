package main.services.captchaService;

import main.DTOEntity.CaptchaDto;
import main.model.CaptchaCodes;
import main.repositories.CaptchaCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Random;

@Service
public class CaptchaServiceImpl
{
    @Value("${intervalCountTime}")
    String intervalCountTime;

    @Autowired
    CaptchaCodesRepository codesRepository;

    public ResponseEntity<CaptchaDto> captcha() {

        int countSimbols = 6;
        Random randChars = new Random();
        String imageCode = (Long.toString(Math.abs(randChars.nextLong()), 36)).substring(0, countSimbols);
        String encodeImage = createEncodeImage(countSimbols, imageCode, randChars);
        String secretCode = encodeImage.hashCode() + "";
        CaptchaCodes captcha = new CaptchaCodes();
        captcha.setCode(imageCode);
        captcha.setSecretCode(secretCode);
        codesRepository.save(captcha);
        CaptchaDto captchaDto = new CaptchaDto(secretCode, encodeImage);
        codesRepository.deleteAll(codesRepository.findAllOlderCodes(intervalCountTime));

        return ResponseEntity.ok(captchaDto);
    }


    private String createEncodeImage(Integer countWords, String secretKey, Random random){
        int iTotalChars = countWords;
        int iHeight = 40;
        int iWidth = 150;
        String imageCode = secretKey;
        Random randChars = random;
        Font fntStyle1 = new Font("Arial", Font.BOLD, 30);
        BufferedImage biImage = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2dImage = (Graphics2D) biImage.getGraphics();
        int iCircle = 15;
        for (int i = 0; i < iCircle; i++) {
            g2dImage.setColor(new Color(randChars.nextInt(255), randChars.nextInt(255), randChars.nextInt(255)));
        }
        g2dImage.setFont(fntStyle1);
        for (int i = 0; i < iTotalChars; i++) {
            g2dImage.setColor(new Color(randChars.nextInt(255), randChars.nextInt(255), randChars.nextInt(255)));
            if (i % 2 == 0) {
                g2dImage.drawString(imageCode.substring(i, i + 1), 25 * i, 24);
            } else {
                g2dImage.drawString(imageCode.substring(i, i + 1), 25 * i, 35);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String encodeImage = null;
        try {
            ImageIO.write(biImage, "jpeg", baos);
            baos.flush();
            encodeImage = Base64.getEncoder().encodeToString(baos.toByteArray());
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return encodeImage;
    }
 }
