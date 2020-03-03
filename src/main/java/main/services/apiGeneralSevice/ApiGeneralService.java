package main.services.apiGeneralSevice;

import main.DTOEntity.StatisticsBlogDto;
import main.DTOEntity.CalendarDto;
import main.DTOEntity.InitDto;
import main.DTOEntity.ListTagsDto;
import main.model.GlobalSettings;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApiGeneralService
{
    InitDto init();

    ListTagsDto findTagsByQuery(String query);

    CalendarDto getAllPostByCalendar(Integer year);

    StatisticsBlogDto getAllStatistics();

    String loadFile(MultipartFile file);
}
