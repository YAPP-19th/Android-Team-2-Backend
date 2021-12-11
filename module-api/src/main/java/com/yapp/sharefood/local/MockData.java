package com.yapp.sharefood.local;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@Profile({"local"})
@RequiredArgsConstructor
public class MockData {
    private final FlavorMock flavorMock;
    private final TagMock tagMock;
    private final CategoryMock categoryMock;

    @PostConstruct
    void setup() {
        tagMock.initTag();

        categoryMock.initCategory();

        flavorMock.initFlavor();
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
    private static class FlavorMock {
        private final FlavorRepository flavorRepository;

        public void initFlavor() {
            for (FlavorType flavorType : FlavorType.values()) {
                flavorRepository.save(Flavor.of(flavorType));
            }
        }
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    private static class CategoryMock {
        private final CategoryRepository categoryRepository;


        public void initCategory() {
            Category categoryFood = Category.of("음식");
            Category categorySandwich = Category.of("샌드위치");
            Category categoryMara = Category.of("마라탕");
            Category categoryTebbokkie = Category.of("떡볶이");
            Category categorySalad = Category.of("샐러드");
            Category categoryYoguart = Category.of("요거트");
            Category categoryRamen = Category.of("라면");

            categorySandwich.assignParent(categoryFood);
            categoryMara.assignParent(categoryFood);
            categoryTebbokkie.assignParent(categoryFood);
            categorySalad.assignParent(categoryFood);
            categoryYoguart.assignParent(categoryFood);
            categoryRamen.assignParent(categoryFood);

            categoryRepository.save(categoryMara);
            categoryRepository.save(categoryTebbokkie);
            categoryRepository.save(categorySalad);
            categoryRepository.save(categoryYoguart);
            categoryRepository.save(categoryRamen);
            categoryRepository.save(categorySandwich);

            categoryRepository.save(categoryFood);


            Category categoryBeverage = Category.of("음료");
            Category categoryStarbuks = Category.of("스타벅스");
            Category categoryAmasbean = Category.of("아마스빈");
            Category categoryGongCha = Category.of("공차");
            Category categoryEdia = Category.of("이디야");
            Category categoryCaktail = Category.of("칵테일");
            Category categoryLeft = Category.of("기타");
            categoryStarbuks.assignParent(categoryBeverage);
            categoryAmasbean.assignParent(categoryBeverage);
            categoryGongCha.assignParent(categoryBeverage);
            categoryEdia.assignParent(categoryBeverage);
            categoryCaktail.assignParent(categoryBeverage);
            categoryLeft.assignParent(categoryBeverage);

            categoryRepository.save(categoryStarbuks);
            categoryRepository.save(categoryAmasbean);
            categoryRepository.save(categoryGongCha);
            categoryRepository.save(categoryEdia);
            categoryRepository.save(categoryCaktail);
            categoryRepository.save(categoryLeft);
            categoryRepository.save(categoryBeverage);
        }
    }
}
