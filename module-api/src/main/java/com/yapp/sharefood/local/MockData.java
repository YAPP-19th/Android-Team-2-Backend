package com.yapp.sharefood.local;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.repository.CategoryRepository;
import com.yapp.sharefood.flavor.domain.Flavor;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.repository.FlavorRepository;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.domain.TagWrapper;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.like.domain.Like;
import com.yapp.sharefood.like.repository.LikeRepository;
import com.yapp.sharefood.tag.domain.Tag;
import com.yapp.sharefood.tag.repository.TagRepository;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Profile({"local"})
@RequiredArgsConstructor
public class MockData {
    private final UserMock userMock;
    private final FlavorMock flavorMock;
    private final TagMock tagMock;
    private final CategoryMock categoryMock;
    private final FoodMock foodMock;

    @PostConstruct
    void setup() {
        userMock.initUser();

        tagMock.initTag();

        categoryMock.initCategory();

        flavorMock.initFlavor();

        foodMock.initFood();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    private static class UserMock {
        private final UserRepository userRepository;

        public void initUser() {
            for (int i = 1; i <= 15; i++) {
                User user = User.builder()
                        .nickname("nickname" + i)
                        .name("name" + i)
                        .id((long) i)
                        .oauthId("1234" + i)
                        .oAuthType(OAuthType.KAKAO)
                        .build();
                userRepository.save(user);
            }
        }
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    private static class TagMock {
        private final TagRepository tagRepository;

        public void initTag() {
            for (int i = 0; i < 20; i++) {
                tagRepository.save(Tag.of("??????" + i));
            }
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
            Category categoryFood = Category.of("??????");
            Category categorySandwich = Category.of("????????????");
            Category categoryMara = Category.of("?????????");
            Category categoryTebbokkie = Category.of("?????????");
            Category categorySalad = Category.of("?????????");
            Category categoryYoguart = Category.of("?????????");
            Category categoryRamen = Category.of("??????");

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


            Category categoryBeverage = Category.of("??????");
            Category categoryStarbuks = Category.of("????????????");
            Category categoryAmasbean = Category.of("????????????");
            Category categoryGongCha = Category.of("??????");
            Category categoryEdia = Category.of("?????????");
            Category categoryCaktail = Category.of("?????????");
            Category categoryLeft = Category.of("??????");
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

    @Component
    @Transactional
    @RequiredArgsConstructor
    private static class FoodMock {
        private final FoodRepository foodRepository;
        private final CategoryRepository categoryRepository;
        private final FlavorRepository flavorRepository;
        private final TagRepository tagRepository;
        private final UserRepository userRepository;
        private final LikeRepository likeRepository;

        public void initFood() {
            List<User> users = userRepository.findAll();
            List<List<Flavor>> flavorEpoch = findFlavorEpoch();
            List<List<TagWrapper>> tagEpoch = findTagEpoch();

            List<Category> categories = categoryRepository.findAll().stream()
                    .filter(category -> !category.getName().equals("??????") && !category.getName().equals("??????"))
                    .collect(Collectors.toList());

            for (Category category : categories) {
                List<Food> foods = new ArrayList<>();
                for (int i = 0; i < 15; i++) {
                    Food food = Food.builder()
                            .writer(users.get(i % users.size()))
                            .foodTitle("title " + category.getName() + i)
                            .foodStatus(FoodStatus.SHARED)
                            .reviewMsg("review msg" + category.getName() + i)
                            .price(i * 1000)
                            .category(category)
                            .build();
                    foods.add(food);
                    food.assignFlavors(flavorEpoch.get(i % flavorEpoch.size()));
                    food.assignWrapperTags(tagEpoch.get(i % tagEpoch.size()));
                }
                List<Food> savedFoods = foodRepository.saveAll(foods);

                assignLike(users, savedFoods);


                List<Food> mineFoods = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    Food food = Food.builder()
                            .writer(users.get(i % users.size()))
                            .foodTitle("title " + category.getName() + (15 + i))
                            .foodStatus(FoodStatus.MINE)
                            .reviewMsg("review msg" + category.getName() + (15 + i))
                            .price((15 + i) * 1000)
                            .category(category)
                            .build();
                    mineFoods.add(food);
                    food.assignFlavors(flavorEpoch.get(i % flavorEpoch.size()));
                    food.assignWrapperTags(tagEpoch.get(i % tagEpoch.size()));
                }

                foodRepository.saveAll(mineFoods);
            }
        }

        private void assignLike(List<User> users, List<Food> foods) {
            if (foods.size() != users.size()) {
                throw new IllegalArgumentException("food user size??? ??????");
            }
            for (int foodId = 0; foodId < foods.size(); foodId++) {
                for (int userId = 0; userId < foodId; userId++) {
                    foods.get(foodId).assignLike(Like.of(users.get(userId)));
                }
            }
            likeRepository.flush();
        }

        private List<List<Flavor>> findFlavorEpoch() {
            List<Flavor> flavors = flavorRepository.findAll();
            int epoch = flavors.size() / 4;
            List<Flavor> flavorPartOne = flavors.subList(0, epoch);
            List<Flavor> flavorPartTwo = flavors.subList(epoch + 1, epoch * 2);
            List<Flavor> flavorPartThree = flavors.subList(epoch * 2, epoch * 3);
            List<Flavor> flavorPartFour = flavors.subList(epoch * 3, flavors.size());

            return List.of(flavorPartOne, flavorPartTwo, flavorPartThree, flavorPartFour);
        }

        private List<List<TagWrapper>> findTagEpoch() {
            List<Tag> tags = tagRepository.findAll();
            int epoch = tags.size() / 4;
            List<Tag> tagPartOne = tags.subList(0, epoch);
            List<TagWrapper> wrapperTagPartOne = IntStream
                    .range(0, tagPartOne.size() - 1).mapToObj(index -> {
                        FoodIngredientType type = FoodIngredientType.EXTRACT;
                        if (index == 0) {
                            type = FoodIngredientType.MAIN;
                        } else if (index % 2 == 0) {
                            type = FoodIngredientType.ADD;
                        }
                        return new TagWrapper(tagPartOne.get(index), type);
                    }).collect(Collectors.toList());

            List<Tag> tagPartTwo = tags.subList(epoch + 1, epoch * 2);
            List<TagWrapper> wrapperTagPartTwo = IntStream
                    .range(0, tagPartTwo.size() - 1).mapToObj(index -> {
                        FoodIngredientType type = FoodIngredientType.EXTRACT;
                        if (index == 0) {
                            type = FoodIngredientType.MAIN;
                        } else if (index % 2 == 0) {
                            type = FoodIngredientType.ADD;
                        }
                        return new TagWrapper(tagPartTwo.get(index), type);
                    }).collect(Collectors.toList());

            List<Tag> tagPartThree = tags.subList(epoch * 2, epoch * 3);
            List<TagWrapper> wrapperTagPartThree = IntStream
                    .range(0, tagPartThree.size() - 1).mapToObj(index -> {
                        FoodIngredientType type = FoodIngredientType.EXTRACT;
                        if (index == 0) {
                            type = FoodIngredientType.MAIN;
                        } else if (index % 2 == 0) {
                            type = FoodIngredientType.ADD;
                        }
                        return new TagWrapper(tagPartThree.get(index), type);
                    }).collect(Collectors.toList());

            List<Tag> tagPartFour = tags.subList(epoch * 3, tags.size());
            List<TagWrapper> wrapperTagPartFour = IntStream
                    .range(0, tagPartFour.size() - 1).mapToObj(index -> {
                        FoodIngredientType type = FoodIngredientType.EXTRACT;
                        if (index == 0) {
                            type = FoodIngredientType.MAIN;
                        } else if (index % 2 == 0) {
                            type = FoodIngredientType.ADD;
                        }
                        return new TagWrapper(tagPartFour.get(index), type);
                    }).collect(Collectors.toList());

            return List.of(wrapperTagPartOne, wrapperTagPartTwo, wrapperTagPartThree, wrapperTagPartFour);
        }
    }
}
