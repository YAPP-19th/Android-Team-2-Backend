package com.yapp.sharefood.favorite.query;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.favorite.domain.Favorite;
import com.yapp.sharefood.user.domain.User;

import java.util.List;

public interface FavoriteQueryRepository {
    List<Favorite> findAllByUserAndCategories(User user, List<Category> categories);
}
