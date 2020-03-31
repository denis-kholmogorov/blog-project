package main.services.apiGeneralSevice;

import main.DTOEntity.StatisticsBlogDto;
import main.DTOEntity.response.ResponseCalendarDto;
import main.DTOEntity.InitDto;
import main.DTOEntity.ListTagsDto;

public interface ApiGeneralService
{
    InitDto init();

    ListTagsDto findTagsByQuery(String query);

    ResponseCalendarDto getAllPostByCalendar(Integer year);

    StatisticsBlogDto getAllStatistics();

    String loadFile(byte[] file);
}
