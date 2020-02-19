package main.services.ApiGeneralSevice;

import main.DTOEntity.*;
import main.model.GlobalSettings;
import main.model.ModerationStatus;
import main.repositories.GlobalSettingsRepository;
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
import java.util.stream.Collectors;

@Service
public class ApiGeneralServiceImpl implements ApiGeneralService
{

    TagRepository tagRepository;

    PostRepository postRepository;

    GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public ApiGeneralServiceImpl(TagRepository tagRepository, PostRepository postRepository, GlobalSettingsRepository globalSettingsRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }

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
    public ListTagsDto getTagBySearch(String query)
    {
        query.toLowerCase();
        List<TagDto> list = tagRepository.findAllTagWithWeight((byte) 1, ModerationStatus.ACCEPTED);
        double maxWeight = list.get(0).getWeight();
        list.forEach(tagDto -> tagDto.setWeight(tagDto.getWeight()/maxWeight));
        if(!query.isEmpty())
        {
            list = list.stream().filter(t-> t.getName().toLowerCase().contains(query)).collect(Collectors.toList());
        }
        return new ListTagsDto(list);
    }

    @Override
    public CalendarDto getAllPostByCalendar(Integer year)
    {
        List<String> listDateAndCount;
        Map<String, Integer> map = new HashMap<>();
        List<Integer> allYearsWithPosts = postRepository.findAllYearWithPosts();
        CalendarDto calendarDto = new CalendarDto();

        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        listDateAndCount = postRepository.findCountPostForCalendar(year);
        listDateAndCount.forEach(s->{
            int a = s.indexOf(',');
            map.put(s.substring(0,a),(Integer.valueOf(s.substring(a+1))));
        });
        calendarDto.setPosts(map);
        calendarDto.setYears(allYearsWithPosts);

        return calendarDto;
    }

    @Override
    public AllStatisticsBlogDto getAllStatistics()
    {
        return new AllStatisticsBlogDto(postRepository.findAllStatistics());
    }

    @Override
    public List<GlobalSettings> getGlobalSettings() {
        return globalSettingsRepository.findAllSettings();
    }
}
