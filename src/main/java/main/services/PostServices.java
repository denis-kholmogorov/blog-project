package main.services;

import main.model.Post;
import main.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostServices {

    @Autowired
    PostRepository postRepository;

    public List<Post> getAllPostAndSort(Integer offset, Integer limit, String sortBy){

        Page<Post> pagedResult = null;
        if(sortBy.equals("recent")){
            Pageable paging = PageRequest.of(offset, limit, Sort.by("time"));
            pagedResult = postRepository.findAll(paging);
        }

        if(pagedResult.hasContent()){
            return pagedResult.getContent();
        }else{
            return new ArrayList<Post>();
        }
    }
}
