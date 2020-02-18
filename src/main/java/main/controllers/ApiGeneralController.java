package main.controllers;

import main.DTOEntity.CalendarDto;
import main.DTOEntity.InitDto;
import main.DTOEntity.TagDto;
import main.services.ApiGeneralSevice.ApiGeneralServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<TagDto>> tagBySearch(@RequestParam("query") String query)
    {
        return ResponseEntity.ok(apiGeneralService.tagBySearch(query));
    }

    @GetMapping(value = "/api/calendar", params = {"year"})
    public ResponseEntity<CalendarDto> postsByCalendar(@RequestParam("year") Integer year){
        CalendarDto calendarDto = apiGeneralService.allPostByCalendar(year);
        return ResponseEntity.ok(calendarDto);
    }
}
