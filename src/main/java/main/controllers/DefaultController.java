package main.controllers;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Log4j2(topic = "Controllers")
@Controller
public class DefaultController
{

    @RequestMapping("/")
    public String index(Model model)
    {
        log.info("Load method index of DefaultController");
        return "index";
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{path:[^\\.]*}")
    public String redirectToIndex() {
        log.info("Load method redirectToIndex of DefaultController");
        return "forward:/";
    }
}
