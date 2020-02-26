package main.services.apiGeneralSevice;

import main.DTOEntity.*;
import main.model.GlobalSettings;
import main.model.ModerationStatus;
import main.repositories.GlobalSettingsRepository;
import main.repositories.PostRepository;
import main.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
    public ListTagsDto findTagsByQuery(String query)
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

    public String loadFile(MultipartFile image){

        if(!image.isEmpty()){
            int leftLimit = 48;
            int rightLimit = 122;
            int targetStringLength = 12;
            Random random = new Random();

            new StringBuilder();
            StringBuilder generatedString =random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);

            for (int i = 0; i < 10; i = i + 3){
                generatedString.insert(i,"/");
            }

            generatedString.insert(0,"upload");
            String dirs = generatedString.substring(0, generatedString.lastIndexOf("/"));
            String imageName = generatedString.substring(generatedString.lastIndexOf("/"));
            File file = new File(dirs);
            file.mkdirs();
            String pathImage = file + imageName + ".jpeg";

            try {
                byte[] bytes = image.getBytes();
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                BufferedImage bi = ImageIO.read(bais);
                ImageIO.write(bi, "jpeg",new File(pathImage));
                return pathImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
