package com.yapp.sharefood.local;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
public class MockData {
    private final TagMock tagMock;
    private final CategoryMock categoryMock;

    @PostConstruct
    void setup() {
        tagMock.initTag();

        categoryMock.initCategory();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    private static class TagMock {
        private final TagRepository tagRepository;

        public void initTag() {
            Tag tag1 = Tag.of("카푸치노");
            Tag tag2 = Tag.of("시럽");
            Tag tag3 = Tag.of("샷추가");
            Tag tag4 = Tag.of("크림");
            tagRepository.save(tag1);
            tagRepository.save(tag2);
            tagRepository.save(tag3);
            tagRepository.save(tag4);
        }
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    private static class CategoryMock {
        private final CategoryRepository categoryRepository;


        public void initCategory() {
            Category categoryFood = Category.of("음식");
            Category categoryBeverage = Category.of("음료");

            Category categorySandwich = Category.of("샌드위치");
            Category categoryStarbuks = Category.of("스타벅스");
            categorySandwich.assignParent(categoryFood);
            categoryStarbuks.assignParent(categoryBeverage);

            categoryRepository.save(categoryFood);
            categoryRepository.save(categoryBeverage);

            categoryRepository.save(categorySandwich);
            categoryRepository.save(categoryStarbuks);
        }
    }
}
