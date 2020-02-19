package main.services.ApiGeneralSevice;

import main.DTOEntity.AllStatisticsBlogDto;
import main.DTOEntity.CalendarDto;
import main.DTOEntity.InitDto;
import main.DTOEntity.ListTagsDto;
import main.model.GlobalSettings;

import java.util.List;

public interface ApiGeneralService
{
    InitDto init();

    ListTagsDto getTagBySearch(String query);

    CalendarDto getAllPostByCalendar(Integer year);

    AllStatisticsBlogDto getAllStatistics();

    List<GlobalSettings> getGlobalSettings();
}
