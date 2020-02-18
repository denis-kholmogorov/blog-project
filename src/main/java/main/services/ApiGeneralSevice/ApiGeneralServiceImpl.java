package main.services.ApiGeneralSevice;

import main.DTOEntity.CalendarDto;
import main.DTOEntity.InitDto;
import main.DTOEntity.TagDto;
import main.model.ModerationStatus;
import main.repositories.PostRepository;
import main.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiGeneralServiceImpl implements ApiGeneralService
{
    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostRepository postRepository;

    @Override
    public InitDto init()
    {
        List<String> list = null;
        try
        {
            list = Files.readAllLines(Paths.get("copyright.txt"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        InitDto initDto = new InitDto(list.get(0), list.get(1), list.get(2),
                list.get(3), list.get(4), list.get(5));
        return initDto;
    }

    @Override
    public List<TagDto> tagBySearch(String query)
    {
        List<TagDto> listTags;
        if(!query.isEmpty())
        {
            listTags = tagRepository.findAllTagWithWeightByQuery((byte) 1, ModerationStatus.ACCEPTED,query);
        }
        else {
            listTags = tagRepository.findAllTagWithWeight((byte) 1, ModerationStatus.ACCEPTED);
        }
        return listTags;
    }

    public CalendarDto allPostByCalendar(Integer year)
    {
        List<String> listDateAndCount;
        Map<String, Integer> map = new HashMap<>();
        List<Integer> allYearsWithPosts = postRepository.findAllYearWithPosts();
        CalendarDto calendarDto = new CalendarDto();

        if(year != null)
        {
            listDateAndCount = postRepository.findCountPostForCalendar(year);
        }
        else{
            year = Calendar.getInstance().get(Calendar.YEAR);
            listDateAndCount = postRepository.findCountPostForCalendar(year);
        }
        listDateAndCount.forEach(s->{
            int a = s.indexOf(',');
            map.put(s.substring(0,a),(Integer.valueOf(s.substring(a+1))));
        });
        calendarDto.setPosts(map);
        calendarDto.setYears(allYearsWithPosts);

        return calendarDto;
    }
}
