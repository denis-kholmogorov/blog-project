package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.model.GlobalSettings;
import main.security.ProviderToken;
import main.services.apiGeneralSevice.ApiGeneralServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.swing.text.html.parser.Entity;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequestMapping("/api")
@RestController
public class ApiGeneralController
{
    @Autowired
    ApiGeneralServiceImpl apiGeneralService;

    @Autowired
    ProviderToken providerToken;
    

    @GetMapping("/init")
    public ResponseEntity<InitDto> init() {

        return ResponseEntity.ok(apiGeneralService.init());
    }

    @GetMapping(value = "/tag")
    public ResponseEntity<ListTagsDto> tagBySearch()//@RequestParam("query") String query
    {
        String query = "";
        return ResponseEntity.ok(apiGeneralService.findTagsByQuery(query));
    }

    @GetMapping(value = "/calendar", params = {"year"})
    public ResponseEntity<CalendarDto> postsByCalendar(@RequestParam("year") Integer year, HttpSession session){
        log.info("id session " + session.getId());
        CalendarDto calendarDto = apiGeneralService.getAllPostByCalendar(year);
        return ResponseEntity.ok(calendarDto);
    }

    @GetMapping(value = "/statistics/all")
    public ResponseEntity getAllStatistics(HttpSession httpSession){

        Optional<GlobalSettings> settings = apiGeneralService.getSettingIsPublic();
        log.info(settings.get().getName() + " значени настройки показа статистики");
        if(!settings.get().isValue() && !providerToken.validateToken(httpSession.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        else {
            StatisticsBlogDto allStat = apiGeneralService.getAllStatistics();
            log.info("Показываем общую статистику");
            return ResponseEntity.ok(allStat);
        }
    }

    @GetMapping(value = "/statistics/my")
    public ResponseEntity getMyStatistics(HttpSession httpSession){         /*/*/
        StatisticsBlogDto gs = apiGeneralService.getMyStatistics(httpSession.getId());
        if(gs!=null){
            return ResponseEntity.ok(gs);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping(value = "/image")
    public ResponseEntity uploadImage(@RequestParam("image") MultipartFile image)
    {
        String answer = apiGeneralService.loadFile(image);
        if(answer != null){
            return ResponseEntity.ok(answer);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(value = "/settings")
    public ResponseEntity getGlobalSettings(HttpSession httpSession)
    {
        log.info("Зашли в настройки get_mapping");
        Map<String, Boolean> settings = apiGeneralService.getSettings(httpSession.getId());
        return ResponseEntity.ok(settings);
    }

    @PutMapping(value = "/settings")
    public ResponseEntity setGlobalSettings(@RequestBody Map<String, Boolean> settings, HttpSession httpSession)
    {

        log.info("Получение putmapping глобальных настроек - " + settings.size()+ " ");

        for(Object a: settings.keySet()){
            System.out.println(a + " " + settings.get(a));
        }
        apiGeneralService.setSettings(settings, httpSession.getId());
        return ResponseEntity.ok().body(null);
    }

    @PostMapping(value = "/moderation")
    public ResponseEntity setModerationAction(@RequestBody ModerationDecisionDto decision, HttpSession session)
    {
        log.info(decision.getDecision() + "  " + decision.getPost_id());
        apiGeneralService.setModerationDecision(decision, session.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @PostMapping(value = "profile/my")  // не работает
    public ResponseEntity setMyProfile(@RequestBody MultipartFile file,
                                                    Integer removeImage,
                                                    String name,
                                                    String email,
                                                    String password)
    {
        System.out.println(file + " " + name + " " + email);
        return null;
    }
}

