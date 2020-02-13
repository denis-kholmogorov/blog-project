package main.controllers;

import lombok.Data;

@Data
public class InitClass
{
    public InitClass(String title, String subtitle, String phone, String email, String copyright, String copyrightFrom) {
        this.title = title;
        this.subtitle = subtitle;
        this.phone = phone;
        this.email = email;
        this.copyright = copyright;
        this.copyrightFrom = copyrightFrom;
    }

    private String title;

    private String subtitle;

    private String phone;

    private String email;

    private String copyright;

    private String copyrightFrom;
}
