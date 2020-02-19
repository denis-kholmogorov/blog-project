package main.controllers;

import main.DTOEntity.AllStatisticsBlogDto;
import main.DTOEntity.CalendarDto;
import main.DTOEntity.InitDto;
import main.DTOEntity.ListTagsDto;
import main.model.GlobalSettings;
import main.services.ApiGeneralSevice.ApiGeneralServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiGeneralController
{
    @Autowired
    ApiGeneralServiceImpl apiGeneralService;

    @GetMapping("/api/init")
    public ResponseEntity<InitDto> init() {

        return ResponseEntity.ok(apiGeneralService.init());
    }

    @GetMapping(value = "/api/tag", params = {"query"})
    public ResponseEntity<ListTagsDto> tagBySearch(@RequestParam("query") String query)
    {
        return ResponseEntity.ok(apiGeneralService.findTagsByQuery(query));
    }

    @GetMapping(value = "/api/calendar", params = {"year"})
    public ResponseEntity<CalendarDto> postsByCalendar(@RequestParam("year") Integer year){
        CalendarDto calendarDto = apiGeneralService.getAllPostByCalendar(year);
        return ResponseEntity.ok(calendarDto);
    }

    @GetMapping(value = "/api/statistics/all")
    public ResponseEntity allStatisticsBlog(){

        List<GlobalSettings> settings = apiGeneralService.getGlobalSettings();

        if(settings.get(2).getValue().equals("1")) {
            AllStatisticsBlogDto allStat = apiGeneralService.getAllStatistics();
            return ResponseEntity.ok(allStat);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
