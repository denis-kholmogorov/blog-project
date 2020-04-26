package main.controllers;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import main.DTOEntity.*;
import main.DTOEntity.request.RequestCommentsDto;
import main.DTOEntity.request.RequestProfileDto;
import main.DTOEntity.request.RequestProfileWithPhotoDto;
import main.DTOEntity.response.ResponseCalendarDto;
import main.model.GlobalSettings;
import main.security.ProviderToken;
import main.security.UserAuthenticationException;
import main.services.apiGeneralSevice.ApiGeneralServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Optional;

@Log4j2
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
        log.info("Copyright отображен");
        return ResponseEntity.ok(apiGeneralService.init());
    }

    @GetMapping(value = "/tag")
    public ResponseEntity<ListTagsDto> tagBySearch(@RequestParam(value = "query",required = false,
                                                                 defaultValue = "") String query){
        log.info("Отображение тегов");
        return ResponseEntity.ok(apiGeneralService.findTagsByQuery(query));
    }

    @GetMapping(value = "/calendar", params = {"year"})
    public ResponseEntity<ResponseCalendarDto> postsByCalendar(@RequestParam("year") Integer year){
        log.info("Отображение постов для календаря для {} года", year);
        ResponseCalendarDto responseCalendarDto = apiGeneralService.getAllPostByCalendar(year);
        return ResponseEntity.ok(responseCalendarDto);
    }

    @GetMapping(value = "/statistics/all")
    public ResponseEntity<?> getAllStatistics(HttpSession httpSession){
        Optional<GlobalSettings> settings = apiGeneralService.getSettingIsPublic();
        if(!settings.get().isValue() && !providerToken.validateToken(httpSession.getId())) {
            throw new UserAuthenticationException("Запрещен доступ к статистике");
        }
        log.info("Отображена полная статистика сайта");
        StatisticsBlogDto allStat = apiGeneralService.getAllStatistics();
        return ResponseEntity.ok(allStat);
    }

    @GetMapping(value = "/statistics/my")
    public ResponseEntity<?> getMyStatistics(HttpSession httpSession) {
        StatisticsBlogDto dto = apiGeneralService.getMyStatistics(httpSession.getId());
        log.info("Отображена пользовательская статистика сайта по сессии {}", httpSession.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/image")
    public ResponseEntity<?> uploadImage(@RequestBody MultipartFile image) throws IOException {
        String answer = apiGeneralService.loadFile(image.getBytes());
        if(answer != null){
            log.info("Фотография загружена");
            return ResponseEntity.ok(answer);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(value = "/settings")
    public ResponseEntity<?> getGlobalSettings(HttpSession httpSession)
    {
        Map<String, Boolean> settings = apiGeneralService.getSettings(httpSession.getId());
        log.info("Отображена статистика сайта");
        return ResponseEntity.ok(settings);
    }

    @PutMapping(value = "/settings")
    public ResponseEntity<?> setGlobalSettings(@RequestBody Map<String, Boolean> settings, HttpSession httpSession){
        apiGeneralService.setSettings(settings, httpSession.getId());
        return ResponseEntity.ok().body(null);
    }

    @PostMapping(value = "/moderation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setModerationAction(@RequestBody ModerationDecisionDto decision, HttpSession session){
        log.info("Пост " + decision.getPost_id() + " изменен статус на " + decision.getDecision() );
        apiGeneralService.setModerationDecision(decision, session.getId());
    }

    @PostMapping(value = "/comment")
    public ResponseEntity<?> setComments(@RequestBody RequestCommentsDto comment, HttpSession session){
        log.info("Добавлен комментарий для поста " + comment.getPostId());
        return apiGeneralService.setComment(comment, session);
    }

    @PostMapping(value = "profile/my", consumes = {"multipart/form-data"})
    public ResponseEntity<?> setMyProfileWithPhoto(@ModelAttribute RequestProfileWithPhotoDto profileDto,
                                                   HttpSession session){
        log.info("Запрос по изменению фотографии в профиле");
        ResponseEntity answer = apiGeneralService.setMyProfile(profileDto, session);
        return answer;
    }

    @PostMapping(value = "profile/my", produces = {"application/json"})
    public ResponseEntity<?> setMyProfile(@RequestBody RequestProfileDto profileDto, HttpSession session)
    {
        log.info("Запрос по изменению данных в профиле");
        ResponseEntity answer = apiGeneralService.setMyProfile(profileDto, session);
        return answer;
    }

}

