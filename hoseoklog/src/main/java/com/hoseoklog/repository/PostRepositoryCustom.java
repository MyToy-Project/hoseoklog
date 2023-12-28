package com.hoseoklog.repository;

import com.hoseoklog.domain.Post;
import com.hoseoklog.request.PostSearchRequest;
import java.util.List;

public interface PostRepositoryCustom {

    List<Post> findPosts(PostSearchRequest page);
}
