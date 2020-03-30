package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.DTOEntity.request.RequestCommentsDto;
import main.DTOEntity.request.RequestProfileDto;
import main.DTOEntity.request.RequestProfileWithPhotoDto;
import main.model.GlobalSettings;
import main.security.ProviderToken;
import main.services.apiGeneralSevice.ApiGeneralServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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
    public ResponseEntity<ListTagsDto> tagBySearch(@RequestParam(value = "query",
            required = false, defaultValue = "") String query)
    {
        return ResponseEntity.ok(apiGeneralService.findTagsByQuery(query));
    }

    @GetMapping(value = "/calendar", params = {"year"})
    public ResponseEntity<CalendarDto> postsByCalendar(@RequestParam("year") Integer year){
        CalendarDto calendarDto = apiGeneralService.getAllPostByCalendar(year);
        return ResponseEntity.ok(calendarDto);
    }

    @GetMapping(value = "/statistics/all")
    public ResponseEntity<?> getAllStatistics(HttpSession httpSession){

        Optional<GlobalSettings> settings = apiGeneralService.getSettingIsPublic();
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
    public ResponseEntity<?> getMyStatistics(HttpSession httpSession){         /*/*/
        StatisticsBlogDto gs = apiGeneralService.getMyStatistics(httpSession.getId());
        if(gs!=null){
            return ResponseEntity.ok(gs);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping(value = "/image")
    public ResponseEntity<?> uploadImage(@RequestBody byte[] image)
    {
        String answer = apiGeneralService.loadFile(image);
        if(answer != null){
            return ResponseEntity.ok(answer);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(value = "/settings")
    public ResponseEntity<?> getGlobalSettings(HttpSession httpSession)
    {
        log.info("Зашли в настройки get_mapping");
        Map<String, Boolean> settings = apiGeneralService.getSettings(httpSession.getId());
        return ResponseEntity.ok(settings);
    }

    @PutMapping(value = "/settings")
    public ResponseEntity<?> setGlobalSettings(@RequestBody Map<String, Boolean> settings, HttpSession httpSession)
    {

        log.info("Получение put mapping глобальных настроек - " + settings.size()+ " ");

        for(Object a: settings.keySet()){
            System.out.println(a + " " + settings.get(a));
        }
        apiGeneralService.setSettings(settings, httpSession.getId());
        return ResponseEntity.ok().body(null);
    }

    @PostMapping(value = "/moderation")
    public ResponseEntity<?> setModerationAction(@RequestBody ModerationDecisionDto decision, HttpSession session)
    {
        log.info(decision.getDecision() + "  " + decision.getPost_id());
        apiGeneralService.setModerationDecision(decision, session.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }


    @PostMapping(value = "/comment")
    public ResponseEntity<?> setComments(@RequestBody RequestCommentsDto comment, HttpSession session){

        return apiGeneralService.setComment(comment, session);
    }

    @PostMapping(value = "profile/my", consumes = {"multipart/form-data"})
    public ResponseEntity<?> setMyProfileWithPhoto(@ModelAttribute RequestProfileWithPhotoDto profileDto, HttpSession session)
    {
        ResponseEntity answer = apiGeneralService.setMyProfile(profileDto, session);
        if(answer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        return answer;
    }

    @PostMapping(value = "profile/my", produces = {"application/json"})
    public ResponseEntity<?> setMyProfile(@RequestBody RequestProfileDto profileDto, HttpSession session)
    {
        ResponseEntity answer = apiGeneralService.setMyProfile(profileDto, session);
        if(answer == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        return answer;
    }

}

